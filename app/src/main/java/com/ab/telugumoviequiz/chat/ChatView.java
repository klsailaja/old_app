package com.ab.telugumoviequiz.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.BaseFragment;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.Constants;
import com.ab.telugumoviequiz.common.GetTask;
import com.ab.telugumoviequiz.common.PostTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.main.MainActivity;
import com.ab.telugumoviequiz.main.Navigator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.ab.telugumoviequiz.common.Constants.CHAT_MAX_ENTRIES;

public class ChatView extends BaseFragment implements View.OnClickListener, CallbackResponse, Runnable, ChatListener {
    private ViewAdapter chatAdapter;
    private final List<Chat> data = new ArrayList<>();
    private static final String key1 = "FETCHED_ENDTIME";
    private long endTimeFetched = -1;
    private ScheduledFuture<?> chatFetchTask = null;
    private View textView;
    private List<String> mixGameTktRates, mixGameStartTimes, mixGameIds;
    private List<String> celebrityGameTktRates, celebrityGameStartTimes, celebrityGameIds;
    private boolean req1 = false, req2 = false;

    private void fetchRecords() {
        long startTime;
        if (endTimeFetched == -1) {
            endTimeFetched = System.currentTimeMillis();
            startTime = endTimeFetched - Constants.CHAT_MAX_DURATION_IN_MILLIS;
        } else {
            startTime = endTimeFetched;
            endTimeFetched = System.currentTimeMillis();
        }

        //Snackbar snackbar = Snackbar.make(textView, "Fetching Chat Messages", Snackbar.LENGTH_SHORT);
        //snackbar.show();
        GetTask<Chat[]> request = Request.getChatMessages(startTime, endTimeFetched);
        request.setCallbackResponse(this);
        Scheduler.getInstance().submit(request);
    }

    private void handleListeners(View.OnClickListener listener) {
        Button inviteBut = Objects.requireNonNull(getView()).findViewById(R.id.chat_invite_but);
        inviteBut.setOnClickListener(listener);

        Button replyBut = getView().findViewById(R.id.chat_repy_but);
        replyBut.setOnClickListener(listener);

        Button sendBut = getView().findViewById(R.id.chatSendBut);
        sendBut.setOnClickListener(listener);
    }

    @Override
    public void run() {
        try {
            fetchRecords();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void itemsSelected(int messageType, int gameType, String gameRate, String gameTime, String gameId) {
        String ReqTemplate = "Anyone coming for Rs.<RATE> at <TIME> with GameId:<ID> in <TYPE>";
        String ResponseTemplate = "Im joining for Rs.<RATE> at <TIME> with GameId:<ID> in <TYPE>";
        int pos = gameRate.indexOf(":");
        if (pos > -1) {
            gameRate = gameRate.substring(pos + 1).trim();
        }
        pos = gameTime.indexOf(":");
        if (pos > -1) {
            gameTime = gameTime.substring(pos + 1).trim();
        }
        pos = gameId.indexOf(":");
        if (pos > -1) {
            gameId = gameId.substring(pos + 1).trim();
        }
        String gameTypeStr = "Mixed Category";
        if (gameType == ChatMsgDialog.CELEBRITY_GAME_TYPE) {
            gameTypeStr = "Celebrity Category";
        }
        String template = ReqTemplate;
        if (messageType == ChatMsgDialog.REPLY) {
            template = ResponseTemplate;
        }
        String chatMsg = template.replaceAll("<RATE>", gameRate);
        chatMsg = chatMsg.replaceAll("<TIME>", gameTime);
        chatMsg = chatMsg.replaceAll("<ID>", gameId);
        chatMsg = chatMsg.replaceAll("<TYPE>", gameTypeStr);

        TextView chatBox = Objects.requireNonNull(getView()).findViewById(R.id.chatSendMsgTxt);
        chatBox.setText(chatMsg);
        Button sendBut = getView().findViewById(R.id.chatSendBut);
        sendBut.setEnabled(true);
        sendBut.setTag(gameId);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Bundle chatBundle = ((MainActivity) Objects.requireNonNull(getActivity())).getParams(Navigator.CHAT_VIEW);
        if (chatBundle != null) {
            endTimeFetched = bundle.getLong(key1, -1);
            if (endTimeFetched != -1) {
                long currentTime = System.currentTimeMillis();
                if (currentTime > endTimeFetched) {
                    long diff = currentTime - endTimeFetched;
                    if (diff > Constants.CHAT_MAX_DURATION_IN_MILLIS) {
                        endTimeFetched = -1;
                    }
                }
            }
        }

        mixGameTktRates = new ArrayList<>(5);
        mixGameStartTimes = new ArrayList<>(5);
        mixGameIds = new ArrayList<>(5);
        celebrityGameTktRates = new ArrayList<>(5);
        celebrityGameStartTimes = new ArrayList<>(5);
        celebrityGameIds = new ArrayList<>(5);

        mixGameTktRates.add("No Data");
        mixGameStartTimes.add("No Data");
        mixGameIds.add("No Data");
        celebrityGameTktRates.add("No Data");
        celebrityGameStartTimes.add("No Data");
        celebrityGameIds.add("No Data");
    }

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        int[] points = Utils.getScreenWidth(getContext());
        ViewAdapter.screenWidth = points[0];

        View root = inflater.inflate(R.layout.chat_view, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        chatAdapter = new ViewAdapter(data);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        textView = root.findViewById(R.id.chatSendMsgTxt);

        data.clear();
        data.addAll(getChatData());
        fillStrTime();
        chatAdapter.notifyDataSetChanged();

        if (Constants.TEST_MODE == 0) {
            fetchRecords();
            chatFetchTask = Scheduler.getInstance().submitRepeatedTask(this, 0, 10, TimeUnit.SECONDS);
            new GameBasicFetcher().run();
        }
        Button sendBut = root.findViewById(R.id.chatSendBut);
        sendBut.setEnabled(false);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedBundle) {
        super.onActivityCreated(savedBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        handleListeners(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        handleListeners(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        storeEndTime();
        if (chatFetchTask != null) {
            chatFetchTask.cancel(true);
        }
    }

    private void storeEndTime() {
        endTimeFetched++;
        Bundle bundle = ((MainActivity) Objects.requireNonNull(getActivity())).getParams(Navigator.CHAT_VIEW);
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putLong(key1, endTimeFetched);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.chatSendBut) {
            View parentView = getView();
            if (parentView == null) {
                return;
            }
            TextView chatBox = parentView.findViewById(R.id.chatSendMsgTxt);
            String chatMsg = chatBox.getText().toString().trim();
            if (chatMsg.length() == 0) {
                return;
            }
            if (chatMsg.length() > 100) {
                chatMsg = chatMsg.substring(0, 100);
            }

            String selectedGameId = (String) view.getTag();
            long gameStartTime = -1;
            int position;
            if (mixGameIds.contains(selectedGameId)) {
                position = mixGameIds.indexOf(selectedGameId);
                gameStartTime = Long.parseLong(mixGameStartTimes.get(position));
            }
            if (celebrityGameIds.contains(selectedGameId)) {
                position = celebrityGameIds.indexOf(selectedGameId);
                gameStartTime = Long.parseLong(celebrityGameIds.get(position));
            }
            System.out.println("gameStartTime :" + gameStartTime);

            Chat newChatMsg = new Chat();
            newChatMsg.setSenderUserId(UserDetails.getInstance().getUserProfile().getId());
            newChatMsg.setMessage(chatMsg);
            newChatMsg.setSentTimeStamp(System.currentTimeMillis());
            newChatMsg.setSenderName(UserDetails.getInstance().getUserProfile().getName());
            newChatMsg.setGameStartTime(gameStartTime);

            PostTask<Chat,Boolean> postChatMsgTask = Request.postChatMsgTask();
            postChatMsgTask.setCallbackResponse(this);
            postChatMsgTask.setPostObject(newChatMsg);
            Scheduler.getInstance().submit(postChatMsgTask);

            new GameBasicFetcher().run();
            return;
        }
        if ((view.getId() == R.id.chat_invite_but) || (view.getId() == R.id.chat_repy_but)) {
            req1 = false;
            req2 = false;
            enableButtons(false);
            int messageType = ChatMsgDialog.REQUEST;
            if (view.getId() == R.id.chat_repy_but) {
                messageType = ChatMsgDialog.REPLY;
            }

            ChatMsgDialog chatMsgDialog = new ChatMsgDialog(messageType);
            chatMsgDialog.setMixTypeData(mixGameTktRates, mixGameStartTimes, mixGameIds);
            chatMsgDialog.setCelebrityTypeData(celebrityGameTktRates, celebrityGameStartTimes, celebrityGameIds);
            chatMsgDialog.setChatListener(this);
            FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
            chatMsgDialog.show(fragmentManager, "dialog");
        }
    }

    private void fillStrTime() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        String datePattern = "HH:mm";
        for (Chat chatMsg : data) {
            Date date = new Date(chatMsg.getSentTimeStamp());
            simpleDateFormat.applyPattern(datePattern);
            String timeStr = simpleDateFormat.format(date);
            chatMsg.setStrTime(timeStr);
        }
    }

    private List<Chat> getChatData() {
        List<Chat> data = new ArrayList<>();

        // Time as Heading..
        Chat chat0 = new Chat();
        chat0.setSenderUserId(-1);
        chat0.setSenderName("TestUser1");
        chat0.setMessage("Message Testing");
        chat0.setSentTimeStamp(System.currentTimeMillis());
        data.add(chat0);

        Chat chat1 = new Chat();
        chat1.setSenderUserId(UserDetails.getInstance().getUserProfile().getId());
        chat1.setSenderName("Your Name");
        chat1.setMessage("You can send chat messages by clicking on left side buttons.Your messages appear here. Try it out.");
        chat1.setSentTimeStamp(System.currentTimeMillis());
        data.add(chat1);

        Chat chat2 = new Chat();
        chat2.setSenderUserId(20);
        chat2.setSenderName("Username");
        chat2.setMessage("Other users messages appear here. You can respond to these messages and play games.");
        chat2.setSentTimeStamp(System.currentTimeMillis());
        data.add(chat2);

        return data;
    }

    @Override
    public void handleResponse(int reqId, final boolean exceptionThrown, final boolean isAPIException,
                               final Object response, final Object userObject) {

       boolean isHandled = handleServerError(exceptionThrown, isAPIException, response);
       if (isHandled) {
           return;
       }
        if (reqId == Request.CHAT_BULK_FETCH) {
            isHandled = handleAPIError(isAPIException, response, 1, null, null);
            if (isHandled) {
                return;
            }
            endTimeFetched++;

            final Chat[] result = (Chat[]) response;
            List<Chat> newEntries = Arrays.asList(result);

            if (newEntries.size() == 0) {
                return;
            }
            data.addAll(newEntries);
            fillStrTime();
            if (data.size() > CHAT_MAX_ENTRIES) {
                int delStartIndex = data.size() - CHAT_MAX_ENTRIES;
                data.subList(0, delStartIndex).clear();
            }
            Runnable run = () -> chatAdapter.notifyDataSetChanged();
            Activity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(run);
            }

        }
        if ((reqId == Request.CHAT_BASIC_GAME_DETAILS_MIX_SET) || (reqId == Request.CHAT_BASIC_GAME_DETAILS_CELEBRITY_SET)) {
            isHandled = handleAPIError(isAPIException, response, 2, textView, null);
            if (isHandled) {
                return;
            }
            if (reqId == Request.CHAT_BASIC_GAME_DETAILS_MIX_SET) {
                req1 = true;
                final ChatGameDetails[] result = (ChatGameDetails[]) response;
                List<ChatGameDetails> newEntries = Arrays.asList(result);
                fillValues(mixGameTktRates, mixGameStartTimes, mixGameIds, newEntries);
            } else {
                req2 = true;
                final ChatGameDetails[] result = (ChatGameDetails[]) response;
                List<ChatGameDetails> newEntries = Arrays.asList(result);
                fillValues(celebrityGameTktRates, celebrityGameStartTimes, celebrityGameIds, newEntries);
            }
            enableButtons(true);
        } else if (reqId == Request.POST_CHAT_MSG) {
            if (isAPIException) {
                displayErrorAsSnackBar((String) response, textView);
                return;
            }
            Boolean result = (Boolean) response;
            if (result) {
                displayErrorAsSnackBar("Posted Chat message success", textView);
                fetchRecords();
            }
        }
    }

    private void enableButtons(boolean enable) {
       if ((req1) && (req2)) {
           final Button button1 = Objects.requireNonNull(getView()).findViewById(R.id.chat_invite_but);
           final Button button2 = Objects.requireNonNull(getView()).findViewById(R.id.chat_repy_but);
           Runnable run = () -> {
               button1.setEnabled(enable);
               button2.setEnabled(enable);
           };
           Activity activity = getActivity();
           if (activity != null) {
               activity.runOnUiThread(run);
           }
       }
    }

    private void fillValues(List<String> rates, List<String> gameTimes, List<String> gameIds,
                            List<ChatGameDetails> basicGameDetails) {
        rates.clear();
        gameTimes.clear();
        gameIds.clear();

        for (ChatGameDetails gd: basicGameDetails) {
            String value = String.valueOf(gd.getTicketRate());
            value = "Game Rate : " + value;
            if (!rates.contains(value)) {
                rates.add(value);
            }
            value = "Game Time : " + gd.getGameTime();
            if (!gameTimes.contains(value)) {
                gameTimes.add(value);
            }
            value = "Game Id : " + gd.getTempGameId();
            gameIds.add(value);
        }
    }

    private class GameBasicFetcher implements Runnable {
        @Override
        public void run() {
            GetTask<ChatGameDetails[]> request1 = Request.getMixedGameChatBasicGameDetails(1);
            request1.setCallbackResponse(ChatView.this);
            Scheduler.getInstance().submit(request1);

            GetTask<ChatGameDetails[]> request2 = Request.getCelebrityGameChatBasicGameDetails(2);
            request2.setCallbackResponse(ChatView.this);
            Scheduler.getInstance().submit(request2);
        }
    }
}
