package com.ab.telugumoviequiz.games;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.BaseFragment;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.Constants;
import com.ab.telugumoviequiz.common.DialogAction;
import com.ab.telugumoviequiz.common.GetTask;
import com.ab.telugumoviequiz.common.Keys;
import com.ab.telugumoviequiz.common.PostTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.UITask;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.constants.UserMoneyAccountType;
import com.ab.telugumoviequiz.main.MainActivity;
import com.ab.telugumoviequiz.main.Navigator;
import com.ab.telugumoviequiz.withdraw.ViewReceipt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.ab.telugumoviequiz.common.Constants.GAME_BEFORE_LOCK_PERIOD_IN_MILLIS;

public class QuestionFragment extends BaseFragment
        implements View.OnClickListener, CallbackResponse, DialogAction {
    private GameDetails gameDetails;
    private ProgressBar progressBar;
    private TextView timerView;
    private TextView questionView;
    private final TextView[] buttonsView = new TextView[4];
    private Button fiftyFifty, changeQues, moreOptions;
    private ArrayList<UserAnswer> userAnswers = new ArrayList<>(10);
    private ViewMyAnswers myAnsersDialog;
    private ViewPrizeDetails viewPrizeDetails;
    private ViewLeaderboard viewLeaderboard;
    private ViewReceipt viewPic;
    private boolean fiftyUsed = false, flipQuestionUsed = false;
    private ScheduledFuture<?> gameStatusPollerHandle;
    private final ArrayList<PrizeDetail> gamePrizeDetails = new ArrayList<>(10);
    private final ArrayList<PlayerSummary> gameLeaderBoardDetails = new ArrayList<>(10);
    private TextView userCountTextLabel;

    private final String FIFTYUSED = "FIFTYUSED";
    private final String FLIPUSED = "FLIPUSED";
    private final String USERANSWERS = "USERANS";
    private final String GAMEDETAILS = "GAMEDETAILS";

    private Bundle saveState() {
        Bundle saveState = new Bundle();
        saveState.putBoolean(FIFTYUSED, fiftyUsed);
        saveState.putBoolean(FLIPUSED, flipQuestionUsed);
        saveState.putParcelableArrayList(USERANSWERS, userAnswers);
        saveState.putSerializable(GAMEDETAILS, gameDetails);
        return saveState;
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate " + savedInstanceState);
        LocalGamesManager.getInstance().stop();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Minimise te game screen case start
        /*System.out.println("Minimise state is " + savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(FIFTYUSED)) {
                fiftyUsed = savedInstanceState.getBoolean(FIFTYUSED);
            }
            if (savedInstanceState.containsKey(FLIPUSED)) {
                flipQuestionUsed = savedInstanceState.getBoolean(FLIPUSED);
            }
            if (savedInstanceState.containsKey(USERANSWERS)) {
                userAnswers = savedInstanceState.getParcelableArrayList(USERANSWERS);
            }
            if (savedInstanceState.containsKey(GAMEDETAILS)) {
                gameDetails = (GameDetails) savedInstanceState.getSerializable(GAMEDETAILS);
            }
        }*/
        // Minimise the game screen case end
        requireActivity().getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return inflater.inflate(R.layout.activity_question, container, false);
    }

    @Override
    public void onStart() {
        restoreState();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("onResume called");
    }

    @Override
    public void onDestroyView() {
        System.out.println("In onDestroyView");
        super.onDestroyView();
        closeAllViews(false);
        LocalGamesManager.getInstance().start();
        requireActivity().getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void restoreState() {
        MainActivity mainActivity = (MainActivity) getActivity();
        Bundle savedState = null;

        if (mainActivity != null) {
            savedState = mainActivity.getParams(Navigator.QUESTION_VIEW);
        }

        GameDetails savedGameDetails = null;
        if (savedState != null) {
            System.out.println("Read from saved state");
            fiftyUsed = savedState.getBoolean(FIFTYUSED);
            flipQuestionUsed = savedState.getBoolean(FLIPUSED);
            userAnswers = savedState.getParcelableArrayList(USERANSWERS);
            savedGameDetails = (GameDetails) savedState.getSerializable(GAMEDETAILS);
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            gameDetails = (GameDetails) bundle.getSerializable("gd");
            if (savedGameDetails != null) {
                if (savedGameDetails.getGameId() != gameDetails.getGameId()) {
                    fiftyUsed = false;
                    flipQuestionUsed = false;
                    userAnswers.clear();
                    gamePrizeDetails.clear();
                    gameLeaderBoardDetails.clear();
                } else {
                    gameDetails = savedGameDetails;
                }
            }
        }
        if (gameDetails != null) {
            if (System.currentTimeMillis() >= (gameDetails.getStartTime() + 10 * 60 * 1000)) {
                Utils.showMessage("Info", "Game Over", getContext(), this, 100, null);
                return;
            }
        }
        View root = getView();
        if (root == null) {
            return;
        }

        timerView = root.findViewById(R.id.timerView);
        progressBar = root.findViewById(R.id.timerProgress);
        questionView = root.findViewById(R.id.questionView);

        buttonsView[0] = root.findViewById(R.id.optionA);
        buttonsView[1] = root.findViewById(R.id.optionB);
        buttonsView[2] = root.findViewById(R.id.optionC);
        buttonsView[3] = root.findViewById(R.id.optionD);

        Resources resources = getResources();
        String successMsg = resources.getString(R.string.game_join_success_msg);

        long cTime = System.currentTimeMillis();
        long timeToStart = gameDetails.getStartTime() - cTime - GAME_BEFORE_LOCK_PERIOD_IN_MILLIS - Constants.SCHEDULER_OFFSET_IN_MILLIS;
        long timeDiff = gameDetails.getStartTime() - cTime;
        if (timeToStart >= 0) {
            displayErrorAsToast(successMsg);
            gameLockedMode(root);
            GetTask<GameStatus> pollStatusTask = Request.getSingleGameStatus(gameDetails.getGameId());
            pollStatusTask.setCallbackResponse(this);
            gameStatusPollerHandle = Scheduler.getInstance().submitRepeatedTask(pollStatusTask, 0, 10, TimeUnit.SECONDS);
        } else if ((timeDiff > 0) && (timeDiff <= GAME_BEFORE_LOCK_PERIOD_IN_MILLIS)) {
            displayErrorAsToast(successMsg);
            scheduleAllQuestions();
            gameStartedMode(root, false);
        } else if (timeDiff < 0) {
            AlertDialog alertDialog = Utils.getProgressDialog(getActivity(), "Rejoining. Please Wait");
            alertDialog.show();
            timeDiff = -1 * timeDiff;
            timeDiff = timeDiff / 1000;
            int mins = (int) (timeDiff / 60);
            int secs = (int) timeDiff - (mins * 60);
            if (secs > 0) {
                mins++;
            }
            int alreadyAnsweredCt = userAnswers.size();
            mins = mins - alreadyAnsweredCt;
            System.out.println("Missed minutes is " + mins);
            // Missed minutes user answers are formed here for rejoin..
            for (int index = 1; index <= mins; index ++) {
                UserAnswer userAnswer = new UserAnswer(alreadyAnsweredCt + index, false, -1L);
                userAnswers.add(userAnswer);
            }
            gameStartedMode(root, true);
            scheduleAllQuestions();
            GetTask<PrizeDetail[]> getPrizeDetailsReq = Request.getPrizeDetails(gameDetails.getGameId());
            getPrizeDetailsReq.setCallbackResponse(this);
            Scheduler.getInstance().submit(getPrizeDetailsReq);

            timerView.setText("0");
            progressBar.setVisibility(View.INVISIBLE);
            quesShowing(false);
            updateLifelines(false);
            alertDialog.dismiss();
        }
    }

    /*
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STARTING_QUESTION_POS, currentQuesPos);
        super.onSaveInstanceState(outState);
    }
    */


    @Override
    public void onPause() {
        super.onPause();
        System.out.println("onPause Called");
        closeAllViews(false);
        if (gameStatusPollerHandle != null) {
            gameStatusPollerHandle.cancel(true);
        }
        boolean gameProgress = isGameInProgress();
        if (!gameProgress) {
            return;
        }
        Scheduler.getInstance().shutDown();
        Bundle saveState = saveState();
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.storeParams(Navigator.QUESTION_VIEW, saveState);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void doAction(int calledId, Object userObject) {
        if (calledId == 10) {
            GameDetails leaveGameDetails = (GameDetails) userObject;
            PostTask<GameOperation, Boolean> joinTask = Request.gameUnjoinTask(leaveGameDetails.getGameId());
            joinTask.setCallbackResponse(this);

            GameOperation gm = new GameOperation();
            gm.setUserProfileId(UserDetails.getInstance().getUserProfile().getId());
            gm.setUserAccountType(UserMoneyAccountType.LOADED_MONEY.getId());

            joinTask.setPostObject(gm);
            joinTask.setHelperObject(leaveGameDetails);
            Scheduler.getInstance().submit(joinTask);
        } else if (calledId == 20) {
            MenuItem item = (MenuItem) userObject;
            Activity parentActivity = getActivity();
            if (parentActivity instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) parentActivity;
                mainActivity.onNavigationItemSelected(item);
            }
        } else if (calledId == 100) {
            Activity parentActivity = getActivity();
            if (parentActivity instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) parentActivity;
                mainActivity.launchView(Navigator.CURRENT_GAMES, null, false);
            }
        }
    }

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    public void onClick(View v){
        int id = v.getId();
        if (id == R.id.game_starts_leave_but) {
            Utils.showConfirmationMessage("Confirm?", "Are you sure to quit?", getContext(), this, 10, gameDetails);
            return;
        }
        switch (id) {
            case R.id.moreOptions: {
                PopupMenu popupMenu = new PopupMenu(getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                boolean enable = true;
                if (userAnswers.size() == 0) {
                    enable = false;
                }
                MenuItem menuItem = popupMenu.getMenu().findItem(R.id.item_my_answers);
                menuItem.setEnabled(enable);
                enable = gameLeaderBoardDetails.size() != 0;
                menuItem = popupMenu.getMenu().findItem(R.id.item_leaderboard);
                menuItem.setEnabled(enable);
                enable = gamePrizeDetails.size() != 0;
                menuItem = popupMenu.getMenu().findItem(R.id.item_prize_money);
                menuItem.setEnabled(enable);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.item_prize_money: {
                            showPrizeDetails();
                            break;
                        }
                        case R.id.item_my_answers: {
                            showUserAnswers(null);
                            break;
                        }
                        case R.id.item_leaderboard: {
                            showLeaderBoardView(false);
                            break;
                        }
                    }
                    return true;
                });
                popupMenu.show();
                break;
            }
            case R.id.flipQuestion: {
                final Integer currentQuesPos = (Integer) v.getTag();
                if (currentQuesPos == null) {
                    break;
                }
                List<Question> questions = gameDetails.getGameQuestions();
                Question oldQuestion = questions.get(currentQuesPos);
                Question newQuestion;
                if (oldQuestion.getQuestionType() == 2) {
                    newQuestion = gameDetails.getFlipPictureQuestion();
                } else {
                    newQuestion = gameDetails.getFlipQuestion();
                }
                newQuestion.setQuestionStartTime(oldQuestion.getQuestionStartTime());
                newQuestion.setQuestionNumber(oldQuestion.getQuestionNumber());
                questions.set(currentQuesPos, newQuestion);
                oldQuestion = questions.get(currentQuesPos);
                oldQuestion.setFlipUsed(true);
                
                setQuestionInUI(newQuestion);
                flipQuestionUsed = true;
                updateLifelines(true);
                displayErrorAsToast("Flip question done");
                break;
            }
            case R.id.fiftyFifty: {
                final Integer currentQuesPos = (Integer) v.getTag();
                if (currentQuesPos == null) {
                    break;
                }
                List<Question> questions = gameDetails.getGameQuestions();
                Question question = questions.get(currentQuesPos);
                int correctOption = question.getCorrectOption();
                int count = 0;
                for (int index = 1; index <= 4; index ++) {
                    if (index != correctOption) {
                        TextView optionButton = getViewCorrespondingToNumber(index);
                        if (optionButton != null) {
                            optionButton.setText("");
                        }
                        count++;
                        if (count == 2) {
                            break;
                        }
                    }
                }
                fiftyUsed = true;
                updateLifelines(true);
                break;
            }
            case R.id.optionA:
            case R.id.optionB:
            case R.id.optionC:
            case R.id.optionD: {
                final Integer currentQuesPos = (Integer) v.getTag();
                if (currentQuesPos == null) {
                    break;
                }
                setQuesView(false);
                updateLifelines(false);
                long butPressedTime = System.currentTimeMillis();
                List<Question> questions = gameDetails.getGameQuestions();
                Question question = questions.get(currentQuesPos);
                long answeredTime = butPressedTime - question.getQuestionStartTime();
                if (answeredTime < 0) {
                    answeredTime = -1 * answeredTime;
                }

                int correctOption = question.getCorrectOption();
                int userAnswerId = 1;
                if (v.getId() == R.id.optionB) {
                    userAnswerId = 2;
                }
                if (v.getId() == R.id.optionC) {
                    userAnswerId = 3;
                }
                if (v.getId() == R.id.optionD) {
                    userAnswerId = 4;
                }
                boolean isCorrect = false;
                if (userAnswerId == correctOption) {
                    isCorrect = true;
                    buttonsView[correctOption - 1].setBackgroundColor(getResources().getColor(R.color.quesCorrect, null));
                } else {
                    buttonsView[correctOption - 1].setBackgroundColor(getResources().getColor(R.color.quesCorrect, null));
                    buttonsView[userAnswerId - 1].setBackgroundColor(getResources().getColor(R.color.quesWrong, null));
                }

                UserAnswer userAnswer = new UserAnswer(question.getQuestionNumber(), isCorrect, answeredTime);
                userAnswers.add(userAnswer);


                PlayerAnswer playerAnswer = new PlayerAnswer();
                playerAnswer.setQuestionNo(question.getQuestionNumber());
                playerAnswer.setUserProfileId(UserDetails.getInstance().getUserProfile().getId());
                playerAnswer.setUserAnswer(userAnswerId);
                playerAnswer.setTimeDiff((int)answeredTime);
                if (question.isFlipUsed()) {
                    playerAnswer.setFlipUsed(true);
                }

                PostTask<PlayerAnswer, String> submitTask = Request.submitAnswerTask(gameDetails.getGameId());
                submitTask.setCallbackResponse(this);
                submitTask.setPostObject(playerAnswer);
                submitTask.setHelperObject(question.getQuestionNumber());
                Scheduler.getInstance().submit(submitTask);
                break;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException, final Object response, final Object helperObject) {
        boolean isHandled = handleServerError(exceptionThrown, isAPIException, response);
        if (isHandled) {
            Scheduler.getInstance().shutDown();
            return;
        }
        switch (reqId) {
            case Request.PRIZE_DETAILS: {
                if (isAPIException) {
                    handleAPIError(true, response, 1, null, null);
                    return;
                }
                List<PrizeDetail> result = Arrays.asList((PrizeDetail[]) response);
                gamePrizeDetails.clear();
                gamePrizeDetails.addAll(result);
                break;
            }
            case Request.SINGLE_GAME_STATUS: {
                handleGameStatus(isAPIException, response);
                break;
            }
            /*case Request.LOCK_TIME_OVER: {
                Runnable run = () -> gameStartedMode(getView());
                Objects.requireNonNull(getActivity()).runOnUiThread(run);
                GetTask<PrizeDetail[]> getPrizeDetailsReq = Request.getPrizeDetails(gameDetails.getGameId());
                getPrizeDetailsReq.setCallbackResponse(this);
                Scheduler.getInstance().submit(getPrizeDetailsReq);
                break;
            }*/
            case Request.SHOW_QUESTION : {
                // Set the question to UI button
                handleSetQuestion((Question) helperObject);
                break;
            }
            case Request.SHOW_USER_ANSWERS: {
                handleShowUserAnswers((Question) helperObject);
                break;
            }
            case Request.SUBMIT_ANSWER_REQ: {
                Integer lastAnsQuestionNo = (Integer) helperObject;
                String submitMsg = "Successfully submitted Question:" + lastAnsQuestionNo + " answer";
                displayErrorAsToast(submitMsg);
                break;
            }
            case Request.LEADER_BOARD: {
                handleShowLeaderBoard(isAPIException, response, helperObject);
                break;
            }
            case Request.SHOW_LEADER_BOARD: {
                Question question = (Question) helperObject;
                int completedQuestionNumber = question.getQuestionNumber();
                GetTask<PlayerSummary[]> leaderBoardReq = Request.getLeaderBoard(gameDetails.getGameId(), completedQuestionNumber);
                leaderBoardReq.setCallbackResponse(this);
                leaderBoardReq.setHelperObject(helperObject);
                Scheduler.getInstance().submit(leaderBoardReq);
                break;
            }
            case Request.SHOW_WINNERS: {
                Runnable runnable = () -> {
                    closeAllViews();
                    clearAll();
                    final AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).create();
                    alertDialog.setTitle("View Winners");
                    alertDialog.setMessage("GAME OVER");
                    ((MainActivity) requireActivity()).startTheWinMoneyStatus(gameDetails.getStartTime());
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "View Winners", (dialogInterface, i) -> {
                        alertDialog.hide();
                        alertDialog.dismiss();
                        alertDialog.cancel();

                        Question question = (Question) helperObject;
                        int completedQuestionNumber = question.getQuestionNumber();
                        GetTask<PlayerSummary[]> leaderBoardReq =
                                Request.getLeaderBoard(gameDetails.getGameId(), completedQuestionNumber);
                        leaderBoardReq.setCallbackResponse(this);
                        leaderBoardReq.setHelperObject(helperObject);
                        Scheduler.getInstance().submit(leaderBoardReq);
                    });
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Close", (dialogInterface, i) -> {
                        alertDialog.hide();
                        alertDialog.dismiss();
                        alertDialog.cancel();
                    });
                    if (!isVisible()) {
                        return;
                    }
                    alertDialog.show();
                };
                requireActivity().runOnUiThread(runnable);
                break;
            }
            case Request.UNJOIN_GAME: {
                String errosMsg;
                if (isAPIException) {
                    errosMsg = (String) response;
                    displayError(errosMsg, null);
                    return;
                } else {
                    Boolean result = (Boolean) response;
                    if (result) {
                        errosMsg = "Leaving game was successful";
                    } else {
                        errosMsg = "Leaving game was unsuccessful";
                    }

                    Activity activity = getActivity();
                    if (activity instanceof MainActivity) {
                        Bundle params = new Bundle();
                        params.putString(Keys.LEAVE_ACTION_RESULT, errosMsg);
                        ((MainActivity)activity).launchView(Navigator.CURRENT_GAMES, params, false);
                        ((MainActivity)activity).fetchUpdateMoney();
                    }
                }
                break;
            }
            /*case Request.MONEY_TASK_STATUS: {
                String errosMsg;
                if (isAPIException) {
                    errosMsg = (String) response;
                    displayError(errosMsg, null);
                    return;
                }
                Integer result = (Integer)response;
                if (result == 1) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    assert mainActivity != null;
                    Scheduler.getInstance().submit(new FetchUserMoneyTask((MainActivity) getActivity()));
                    return;
                }
                Integer reqCount = (Integer) helperObject;
                if (reqCount < 11) {
                    GetTask<Integer> moneyUpdatedTask = Request.getMoneyStatusTask(this.gameDetails.getStartTime());
                    moneyUpdatedTask.setCallbackResponse(this);
                    moneyUpdatedTask.setHelperObject(++reqCount);
                }
                break;
            }*/
            default:
                throw new IllegalStateException("Unexpected value: " + reqId);
        }
    }

    private void updateLifelines(boolean masterFlag) {
        if (fiftyFifty == null) {
            return;
        }
        fiftyFifty.setEnabled(!fiftyUsed);
        changeQues.setEnabled(!flipQuestionUsed);
        if (!masterFlag) {
            fiftyFifty.setEnabled(false);
            changeQues.setEnabled(false);
        }
        //fiftyFifty.setBackgroundResource(fiftyUsed ? R.drawable.fifty50_used : R.drawable.fifty50_active);
        //changeQues.setBackgroundResource(flipQuestionUsed ? R.drawable.change_used : R.drawable.change_active);
    }

    private void gameLockedMode(View root) {
        if (root == null) {
            return;
        }
        TextView label = root.findViewById(R.id.game_starts_label);
        if (label != null) {
            Date date = new Date(gameDetails.getStartTime());

            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            //String datePattern = "MMM dd";
            String timePattern = "hh:mm aaa";

            simpleDateFormat.applyPattern(timePattern);
            String timeStr = simpleDateFormat.format(date);

            label.setText(timeStr);

            userCountTextLabel = root.findViewById(R.id.starts_user_ct_val);

            TextView gameIdValLabel = root.findViewById(R.id.gameid_text_val_id);
            if (gameIdValLabel != null) {
                gameIdValLabel.setText(String.valueOf(gameDetails.getTempGameId()));
            }
        }
        Button leaveButton = root.findViewById(R.id.game_starts_leave_but);
        if (leaveButton != null) {
            leaveButton.setOnClickListener(this);
        }

        timerView.setText("0");
        progressBar.setVisibility(View.INVISIBLE);
        quesShowing(false);
        updateLifelines(false);
    }

    private void gameStartedMode(View root, boolean rejoin) {
        String joinMsg = getString(R.string.game_start_msg);
        if (rejoin) {
            joinMsg = getString(R.string.game_rejoin_msg);
            timerView.setText("0");
            progressBar.setVisibility(View.INVISIBLE);
        }
        //Toast.makeText(getContext(), joinMsg, Toast.LENGTH_SHORT).show();
        TextView gameIdLabel = root.findViewById(R.id.gameIdLabel);
        TextView gameIdValLabel = root.findViewById(R.id.gameid_text_val_id);
        TextView startAtLabel = root.findViewById(R.id.startAtLabel);
        TextView startAtValLabel = root.findViewById(R.id.game_starts_label);
        TextView usersAtLabel = root.findViewById(R.id.usersAtLabel);
        TextView userAtValLabel = root.findViewById(R.id.starts_user_ct_val);
        Button gameStartBut = root.findViewById(R.id.game_starts_leave_but);

        fiftyFifty = root.findViewById(R.id.fiftyFifty);
        changeQues = root.findViewById(R.id.flipQuestion);
        moreOptions = root.findViewById(R.id.moreOptions);

        gameIdLabel.setVisibility(View.GONE);
        gameIdValLabel.setVisibility(View.GONE);
        startAtLabel.setVisibility(View.GONE);
        startAtValLabel.setVisibility(View.GONE);
        usersAtLabel.setVisibility(View.GONE);
        userAtValLabel.setVisibility(View.GONE);
        gameStartBut.setVisibility(View.GONE);

        fiftyFifty.setVisibility(View.VISIBLE);
        changeQues.setVisibility(View.VISIBLE);
        moreOptions.setVisibility(View.VISIBLE);

        fiftyFifty.setOnClickListener(this);
        changeQues.setOnClickListener(this);
        moreOptions.setOnClickListener(this);
        updateLifelines(false);
        for (TextView textView : buttonsView) {
            textView.setOnClickListener(this);
        }
        displayErrorAsSnackBar(joinMsg, moreOptions);
    }
    private void quesShowing(boolean isShowing) {
        if (isShowing) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            //timerView.setText("0");
        }
        setQuesView(isShowing);
    }

    private void setQuesView(boolean enable) {
        questionView.setEnabled(enable);
        for (TextView textView : buttonsView) {
            textView.setEnabled(enable);
        }
    }

    private void resetButtonColors() {
        for (TextView textView : buttonsView) {
            textView.setBackgroundColor(getResources().getColor(R.color.quesBG, null));
        }
    }
    private TextView getViewCorrespondingToNumber(int number) {
        switch (number) {
            case 1: {
                return buttonsView[0];
            }
            case 2: {
                return buttonsView[1];
            }
            case 3: {
                return buttonsView[2];
            }
            case 4: {
                return buttonsView[3];
            }
        }
        return null;
    }
    private void clearAll() {
        resetButtonColors();
        quesShowing(false);
        questionView.setText("");
        buttonsView[0].setText("");
        buttonsView[1].setText("");
        buttonsView[2].setText("");
        buttonsView[3].setText("");
    }

    private void closeAllViews() {
        closeAllViews(true);
    }
    public void closeAllViews(boolean uiThread) {
        Runnable run = () -> {
            if (myAnsersDialog != null) {
                myAnsersDialog.dismiss();
            }
            if (viewPrizeDetails != null) {
                viewPrizeDetails.dismiss();
            }
            if (viewLeaderboard != null) {
                viewLeaderboard.dismiss();
            }
            if (viewPic != null) {
                viewPic.dismiss();
            }
        };
        if (uiThread) {
            requireActivity().runOnUiThread(run);
        } else {
            run.run();
        }
    }

    private void handleShowLeaderBoard(boolean isAPIExceptionThrown, Object response, Object helperObject) {
        if (isAPIExceptionThrown) {
            final String errMsg = (String) response;
            boolean isHandled = handleKnownErrors(errMsg);
            if (isHandled) {
                return;
            }
        }
        if (!(response instanceof PlayerSummary[])) {
            return;
        }
        List<PlayerSummary> result = Arrays.asList((PlayerSummary[]) response);
        gameLeaderBoardDetails.clear();
        gameLeaderBoardDetails.addAll(result);
        final Question question = (Question) helperObject;
        boolean isGameOver = (question.getQuestionNumber() == 10);
        showLeaderBoardView(isGameOver);
    }

    private void showLeaderBoardView(final boolean isGameOver) {
        Runnable run = () -> {
            closeAllViews();
            viewLeaderboard = new ViewLeaderboard(getContext(), isGameOver, gameLeaderBoardDetails, getActivity());
            viewLeaderboard.setTotalWinnersCount(gamePrizeDetails.size());
            viewLeaderboard.setTotalPlayersCount(gameDetails.getCurrentCount());
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            if (!isVisible()) {
                return;
            }
            viewLeaderboard.show(fragmentManager, "dialog");
        };
        requireActivity().runOnUiThread(run);
    }

    private void showPrizeDetails() {
        if (gamePrizeDetails.size() == 0) {
            Utils.showMessage("Error", "Prize Details Not Found", getContext(), null);
            return;
        }
        viewPrizeDetails = new ViewPrizeDetails(getContext(), gameDetails.getCurrentCount());
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        viewPrizeDetails.setValues(gamePrizeDetails);
        if (!isVisible()) {
            return;
        }
        viewPrizeDetails.show(fragmentManager, "dialog");
    }

    private void showUserAnswers(Question question) {
        closeAllViews();
        String viewTitle = getResources().getString(R.string.view_user_answers_title1);
        if (question != null) {
            boolean userAnswered = false;
            for (UserAnswer userAnswer : userAnswers) {
                if (userAnswer.getqNo() == question.getQuestionNumber()) {
                    userAnswered = true;
                    break;
                }
            }
            if (!userAnswered) {
                UserAnswer userAnswer = new UserAnswer(question.getQuestionNumber(), false, 0);
                userAnswers.add(userAnswer);
            }
        } else {
            viewTitle = getResources().getString(R.string.view_user_answers_title2);
        }

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        myAnsersDialog = new ViewMyAnswers(getContext(), userAnswers, viewTitle);
        myAnsersDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        if (!isVisible()) {
            return;
        }
        myAnsersDialog.show(fragmentManager, "dialog");
    }

    private void handleShowUserAnswers (final Question question) {
        Runnable run = () -> showUserAnswers(question);
        requireActivity().runOnUiThread(run);
    }

    private void setTagValueToUIComponents(Integer questionNumber) {
        fiftyFifty.setTag(questionNumber);
        changeQues.setTag(questionNumber);
        moreOptions.setTag(questionNumber);
        for (TextView t : buttonsView) {
            t.setTag(questionNumber);
        }
    }

    private void setQuestionInUI(Question question) {
        int qNo = question.getQuestionNumber();
        String linkText = qNo + ") " + question.getnStatement();
        if (question.getQuestionType() == 2) {
            linkText = + qNo + ") " + question.getnStatement();
            SpannableString ss = new SpannableString(linkText);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    viewPic = new ViewReceipt((getContext()), question.getPictureBytes(), "Picture Based Question");
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    viewPic.show(fragmentManager, "dialog");
                }
            };
            String clickText = "click here to view";
            int startPos = linkText.indexOf(clickText);
            int endPos = startPos + clickText.length();
            ss.setSpan(clickableSpan, startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            questionView.setText(ss);
            questionView.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            questionView.setText(linkText);
        }
        buttonsView[0].setText(question.getnOptionA());
        buttonsView[1].setText(question.getnOptionB());
        buttonsView[2].setText(question.getnOptionC());
        buttonsView[3].setText(question.getnOptionD());
    }

    @SuppressLint("SetTextI18n")
    private void handleSetQuestion(final Question question) {
        final int questionNo = question.getQuestionNumber() - 1;
        @SuppressLint("SetTextI18n") Runnable run = () -> {
            setTagValueToUIComponents(questionNo);
            closeAllViews();
            resetButtonColors();
            quesShowing(true);
            updateLifelines(true);
            setQuestionInUI(question);
            /*int qNo = question.getQuestionNumber();
            String linkText = qNo + ") " + question.getnStatement();
            if (question.getQuestionType() == 2) {
                linkText = "Click Here " + qNo + ") " + question.getnStatement();
                SpannableString ss = new SpannableString(linkText);
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View view) {
                        ViewReceipt viewReceipt = new ViewReceipt((getContext()), question.getPictureBytes(), "Picture Based Question");
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        viewReceipt.show(fragmentManager, "dialog");
                    }
                };
                ss.setSpan(clickableSpan, 0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                questionView.setText(ss);
                questionView.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                questionView.setText(linkText);
            }
            buttonsView[0].setText(question.getnOptionA());
            buttonsView[1].setText(question.getnOptionB());
            buttonsView[2].setText(question.getnOptionC());
            buttonsView[3].setText(question.getnOptionD());*/
        };
        requireActivity().runOnUiThread(run);
        for (int index = 1; index <= Constants.QUESTION_MAX_TIME_IN_SEC; index++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            final int finalIntVal = index;
            run = () -> {
                progressBar.setProgress(finalIntVal);
                timerView.setText(Integer.toString(finalIntVal));
            };
            requireActivity().runOnUiThread(run);
        }
        run = () -> {
            quesShowing(false);
            updateLifelines(false);
        };
        requireActivity().runOnUiThread(run);
    }

    private void scheduleAllQuestions() {
        Scheduler scheduler = Scheduler.getInstance();
        List<Question> gameQuestions = gameDetails.getGameQuestions();
        long questionStartTime;
        long actualStartTime;
        int maxQuestionsCount = gameQuestions.size();
        long currentTime = System.currentTimeMillis();
        for (int index = 0; index <= (maxQuestionsCount - 2); index++) {
            Question question = gameQuestions.get(index);
            questionStartTime = question.getQuestionStartTime();

            if (currentTime < questionStartTime) {
                actualStartTime = questionStartTime - System.currentTimeMillis() - Constants.SCHEDULER_OFFSET_IN_MILLIS;
                UITask setQuestionTask = new UITask(Request.SHOW_QUESTION, this, question);
                scheduler.submit(setQuestionTask, actualStartTime, TimeUnit.MILLISECONDS);
            }

            if (currentTime < (questionStartTime + Constants.USER_ANSWERS_VIEW_START_TIME_IN_MILLIS)) {
                actualStartTime = questionStartTime + Constants.USER_ANSWERS_VIEW_START_TIME_IN_MILLIS - System.currentTimeMillis();
                UITask showUserAnswersTask = new UITask(Request.SHOW_USER_ANSWERS, this, question);
                scheduler.submit(showUserAnswersTask, actualStartTime, TimeUnit.MILLISECONDS);
            }

            if (currentTime < (questionStartTime + Constants.LEADERBOARD_VIEW_START_TIME_IN_MILLIS)) {
                actualStartTime = questionStartTime + Constants.LEADERBOARD_VIEW_START_TIME_IN_MILLIS - System.currentTimeMillis();
                UITask showLeaderBoardTask = new UITask(Request.SHOW_LEADER_BOARD, this, question);
                scheduler.submit(showLeaderBoardTask, actualStartTime, TimeUnit.MILLISECONDS);
            }
        }
        Question question = gameQuestions.get(maxQuestionsCount - 1);
        questionStartTime = question.getQuestionStartTime();

        if (currentTime < questionStartTime) {
            actualStartTime = questionStartTime - System.currentTimeMillis() - Constants.SCHEDULER_OFFSET_IN_MILLIS;
            UITask setQuestionTask = new UITask(Request.SHOW_QUESTION, this, question);
            scheduler.submit(setQuestionTask, actualStartTime, TimeUnit.MILLISECONDS);
        }

        if (currentTime < (questionStartTime + Constants.USER_ANSWERS_VIEW_START_TIME_IN_MILLIS)) {
            actualStartTime = questionStartTime + Constants.USER_ANSWERS_VIEW_START_TIME_IN_MILLIS - System.currentTimeMillis();
            UITask showUserAnswersTask = new UITask(Request.SHOW_USER_ANSWERS, this, question);
            scheduler.submit(showUserAnswersTask, actualStartTime, TimeUnit.MILLISECONDS);
        }

        if (currentTime < (questionStartTime + Constants.LEADERBOARD_VIEW_START_TIME_IN_MILLIS)) {
            actualStartTime = questionStartTime + Constants.LEADERBOARD_VIEW_START_TIME_IN_MILLIS - System.currentTimeMillis();
            UITask showLeaderBoardTask = new UITask(Request.SHOW_WINNERS, this, question);
            scheduler.submit(showLeaderBoardTask, actualStartTime, TimeUnit.MILLISECONDS);
        }

        /*if (currentTime < (questionStartTime + Constants.SCHEDULE_USER_MONEY_FETCH)) {
            actualStartTime = questionStartTime + Constants.SCHEDULE_USER_MONEY_FETCH - System.currentTimeMillis();
            GetTask<Integer> moneyUpdatedTask = Request.getMoneyStatusTask(this.gameDetails.getStartTime());
            moneyUpdatedTask.setCallbackResponse(this);
            moneyUpdatedTask.setHelperObject(1);
            scheduler.submit(moneyUpdatedTask, actualStartTime, TimeUnit.MILLISECONDS);
        }*/
    }

    private boolean handleKnownErrors(final String errMsg) {
        if (errMsg.contains("found")) {
            Runnable run = () -> showErrShowHomeScreen(errMsg);
            requireActivity().runOnUiThread(run);
            return true;
        }
        return false;
    }

    private void handleGameStatus(boolean isAPIExceptionThrown, Object response) {
        boolean isHandled = handleAPIError(isAPIExceptionThrown, response, 1, null, null);
        if (isHandled) {
            if (gameStatusPollerHandle != null) {
                gameStatusPollerHandle.cancel(true);
            }
            return;
        }

        final GameStatus result = (GameStatus) response;
        if ((result.getGameStatus() == 2) || (result.getGameStatus() == -1)) {
            if (gameStatusPollerHandle != null) {
                gameStatusPollerHandle.cancel(true);
            }
        }
        if (result.getGameStatus() == 2) {
            scheduleAllQuestions();
            Runnable run = () -> gameStartedMode(getView(), false);
            requireActivity().runOnUiThread(run);
            GetTask<PrizeDetail[]> getPrizeDetailsReq = Request.getPrizeDetails(gameDetails.getGameId());
            getPrizeDetailsReq.setCallbackResponse(this);
            Scheduler.getInstance().submit(getPrizeDetailsReq);
        }
        gameDetails.setCurrentCount(result.getCurrentCount());
        Runnable run = () -> {
            if (userCountTextLabel != null) {
                userCountTextLabel.setText(String.valueOf(result.getCurrentCount()));
            }
        };
        requireActivity().runOnUiThread(run);
    }

    private boolean isGameInProgress() {
        boolean gameInProgress = false;
        long currentTime = System.currentTimeMillis();
        long gameStartTime = gameDetails.getStartTime() - GAME_BEFORE_LOCK_PERIOD_IN_MILLIS;
        long gameEndTime = gameStartTime + 10 * 60 * 1000 - Constants.TRIGGER_FETCH_MONEY_AFTER_LAST_QUES_IN_MILLIS;
        if ((currentTime >= gameStartTime) && (currentTime <= gameEndTime)) {
            gameInProgress = true;
        }
        return gameInProgress;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        boolean gameInProgress = isGameInProgress();
        if (gameInProgress) {
            Utils.showConfirmationMessage("Confirm", "Game in progress. You will miss questions. Are you sure to proceed?",
                    getContext(), this, 20, item);
            return false;
        }
        return super.onNavigationItemSelected(item);
    }
    /*
    private void handleNetworkSpeed() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
        int downSpeed = nc.getLinkDownstreamBandwidthKbps();
        int upSpeed = nc.getLinkUpstreamBandwidthKbps();
    }*/

    @Override
    public void passData(int reqId, List<String> data) {
        if (reqId != 1000) {
            super.passData(reqId, data);
            return;
        }
        if ((data != null) && (data.size() > 0)) {
            String isGameOverStr = data.get(0);
            if (isGameOverStr.equalsIgnoreCase("1")) {
                displayErrorAsToast("Winning Money is updated for winners");
            }
        }
    }
}
