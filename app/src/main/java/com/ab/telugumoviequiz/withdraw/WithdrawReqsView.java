package com.ab.telugumoviequiz.withdraw;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.BaseFragment;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.DialogAction;
import com.ab.telugumoviequiz.common.GetTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.constants.WithdrawReqState;
import com.ab.telugumoviequiz.constants.WithdrawReqType;
import com.ab.telugumoviequiz.help.HelpPreferences;
import com.ab.telugumoviequiz.help.HelpTopic;
import com.ab.telugumoviequiz.help.ViewHelp;
import com.ab.telugumoviequiz.main.MainActivity;
import com.ab.telugumoviequiz.main.Navigator;
import com.ab.telugumoviequiz.main.UserProfile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class WithdrawReqsView extends BaseFragment implements PopupMenu.OnMenuItemClickListener,
        View.OnClickListener, CallbackResponse, DialogAction {

    private int startPosOffset = 0;
    private ViewAdapter tableAdapter;
    private final List<WithdrawRequest> tableData = new ArrayList<>();
    private int wdStatus = -1;
    public final static int CANCEL_BUTTON_ID = 1;
    public final static int MORE_OPTIONS_BUTTON_ID = 2;
    private final static int FILTER_ALL = 1;
    private final static int FILTER_OPENED = 2;
    private final static int FILTER_CLOSED = 3;
    private final static int FILTER_CANCELLED = 4;
    private final static int VIEW_BENEFICIERY_DETAILS = 10;
    private final static int VIEW_RECEIPT = 11;
    private final static int VIEW_CLOSED_CMTS = 12;

    private void handleListeners(View.OnClickListener listener) {
        View view = getView();
        if (view == null) {
            return;
        }
        Button prevButton = view.findViewById(R.id.myreferals_prev_but);
        Button nextButton = view.findViewById(R.id.myreferals_next_but);

        prevButton.setOnClickListener(listener);
        nextButton.setOnClickListener(listener);

        Button filterAccType = view.findViewById(R.id.filterStatus);
        filterAccType.setOnClickListener(listener);

        FloatingActionButton newWithdrawBut = view.findViewById(R.id.fab);
        newWithdrawBut.setOnClickListener(listener);
    }

    private void fetchRecords() {
        UserProfile userProfile = UserDetails.getInstance().getUserProfile();
        GetTask<WithdrawRequestsHolder> request = Request.getWDReqs(userProfile.getId(), startPosOffset, wdStatus);
        request.setCallbackResponse(this);
        request.setActivity(getActivity(), null);
        Scheduler.getInstance().submit(request);
    }

    private void populateTable(WithdrawRequestsHolder details) {
        View view = getView();
        if (view == null) {
            return;
        }
        Button prevButton = view.findViewById(R.id.myreferals_prev_but);
        Button nextButton = view.findViewById(R.id.myreferals_next_but);

        prevButton.setEnabled(details.isPrevEnabled());
        nextButton.setEnabled(details.isNextEnabled());
        List<WithdrawRequest> list = details.getList();
        tableData.clear();
        tableData.addAll(list);
        tableAdapter.notifyDataSetChanged();
        TextView totalView = view.findViewById(R.id.view_total);
        String totalPrefix = getResources().getString(R.string.total_prefix);
        int start;
        int end;
        if (details.getTotal() == 0) {
            start = 0;
            end = 0;
        } else {
            start = startPosOffset + 1;
            end = startPosOffset + list.size();
        }
        String totalStr = totalPrefix + start + " - " + end + " of " + details.getTotal();
        totalView.setText(totalStr);
    }

    @Override
    public void doAction(int id, Object userObject) {
        if (id == CANCEL_BUTTON_ID) {
            String wdRefId = (String) userObject;
            UserProfile userProfile = UserDetails.getInstance().getUserProfile();
            GetTask<Boolean> cancelTask = Request.getCancelReq(userProfile.getId(), wdRefId);
            cancelTask.setCallbackResponse(this);
            Scheduler.getInstance().submit(cancelTask);
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        int[] points = Utils.getScreenWidth(getContext());
        ViewAdapter.screenWidth = points[0];

        String [] tableHeadings = new String[8];
        Resources resources = getResources();
        tableHeadings[0] = resources.getString(R.string.wd_col1);
        tableHeadings[1] = resources.getString(R.string.wd_col2);
        tableHeadings[2] = resources.getString(R.string.wd_col3);
        tableHeadings[3] = resources.getString(R.string.wd_col4);
        tableHeadings[4] = resources.getString(R.string.wd_col5);
        tableHeadings[5] = resources.getString(R.string.wd_col6);
        tableHeadings[6] = resources.getString(R.string.wd_col7);
        tableHeadings[7] = resources.getString(R.string.wd_col8);

        View root = inflater.inflate(R.layout.withdraw, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        tableAdapter = new ViewAdapter(tableData, tableHeadings);
        tableAdapter.setClickListener(this);
        recyclerView.setAdapter(tableAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        fetchRecords();
        showHelpWindow();
        return root;
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
    public void onClick(View view) {
        int id = view.getId();
        int maxRowCount = 5;
        if (id == R.id.myreferals_prev_but) {
            startPosOffset = startPosOffset - maxRowCount;
            fetchRecords();
        } else if (id == R.id.myreferals_next_but) {
            startPosOffset = startPosOffset + maxRowCount;
            fetchRecords();
        } else if (id == R.id.filterStatus) {
            Resources resources = requireActivity().getResources();
            CharSequence[] accTypes = resources.getTextArray(R.array.wd_options);
            PopupMenu popupMenu = new PopupMenu(getActivity(), view);
            int counter = 1;
            for (CharSequence s : accTypes) {
                popupMenu.getMenu().add(0,counter++, 0, s);
            }
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        } else if (id == CANCEL_BUTTON_ID){
            String wdRefId = (String) view.getTag();
            Utils.showConfirmationMessage("Confirm?", "Please confirm?", getContext(), this, CANCEL_BUTTON_ID, wdRefId);
        } else if (id == MORE_OPTIONS_BUTTON_ID) {
            WithdrawRequest selectedWDRequest = (WithdrawRequest) view.getTag();
            Resources resources = requireActivity().getResources();
            CharSequence[] accTypes = resources.getTextArray(R.array.wd_more_options);
            PopupMenu popupMenu = new PopupMenu(getActivity(), view);
            int counter = 10;
            for (CharSequence s : accTypes) {
                MenuItem item = popupMenu.getMenu().add(0,counter++, 0, s);
                view.setTag(selectedWDRequest);
                item.setActionView(view);
            }
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        } else if (id == R.id.fab) {
            Activity activity = getActivity();
            if (activity instanceof MainActivity) {
                ((MainActivity)activity).launchView(Navigator.NEW_WITHDRAW_REQUEST, null, false);
            }
        }
    }

    @Override
    public boolean onMenuItemClick (MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case FILTER_ALL: {
                startPosOffset = 0;
                wdStatus = -1;
                fetchRecords();
                break;
            }
            case FILTER_OPENED: {
                startPosOffset = 0;
                wdStatus = 1;
                fetchRecords();
                break;
            }
            case FILTER_CLOSED: {
                startPosOffset = 0;
                wdStatus = 2;
                fetchRecords();
                break;
            }
            case FILTER_CANCELLED: {
                startPosOffset = 0;
                wdStatus = 3;
                fetchRecords();
                break;
            }
            case VIEW_BENEFICIERY_DETAILS: {
                WithdrawRequest selectedWDRequest = (WithdrawRequest) item.getActionView().getTag();
                String accDetails = getBenefeciaryAccountDetails(selectedWDRequest);
                Utils.showMessage("Details", accDetails, getContext(), null);
                break;
            }
            case VIEW_RECEIPT: {
                WithdrawRequest selectedWDRequest = (WithdrawRequest) item.getActionView().getTag();
                boolean isReqClosed = (selectedWDRequest.getReqStatus() == WithdrawReqState.CLOSED.getId());
                if (!isReqClosed) {
                    displayInfo("Receipt can be viewed once the withdraw request is closed", null);
                    return false;
                }
                GetTask<byte[]> viewReceiptTask = Request.getReceiptTask(selectedWDRequest.getReceiptId());
                viewReceiptTask.setCallbackResponse(this);
                Scheduler.getInstance().submit(viewReceiptTask);
                break;
            }
            case VIEW_CLOSED_CMTS: {
                WithdrawRequest selectedWDRequest = (WithdrawRequest) item.getActionView().getTag();
                boolean isReqClosed = (selectedWDRequest.getReqStatus() == WithdrawReqState.CLOSED.getId());
                String closedCmts = "Closed comments updated after the withdraw request is closed";
                if (isReqClosed) {
                    closedCmts = selectedWDRequest.getClosedComents();
                }
                Utils.showMessage("Comments", closedCmts, getContext(), null);
                break;
            }
        }
        return true;
    }

    private void showHelpWindow() {
        int isSet = HelpPreferences.getInstance().readPreference(requireContext(), HelpPreferences.WITHDRAW_TIPS);
        if (isSet == 1) {
            return;
        }
        List<String> helpKeys = new ArrayList<>();
        helpKeys.add("topic_name1");
        helpKeys.add("topic_name2");
        helpKeys.add("topic_name3");
        List<HelpTopic> loginHelpLocalTopics = Utils.getHelpTopics(helpKeys, 1);
        List<HelpTopic> loginHelpEnglishTopics = Utils.getHelpTopics(helpKeys, 2);

        ViewHelp viewHelp = new ViewHelp(loginHelpLocalTopics,
                loginHelpEnglishTopics, ViewHelp.HORIZONTAL, HelpPreferences.WITHDRAW_TIPS);
        viewHelp.setLocalMainHeading("Main Heading Telugu");
        viewHelp.setEnglishMainHeading("Terms And Conditions");
        Utils.clearState();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        viewHelp.show(fragmentManager, "dialog");
    }

    private String getBenefeciaryAccountDetails(WithdrawRequest wdRequest) {
        if (wdRequest.getRequestType() == WithdrawReqType.BY_BANK.getId()) {
            WithdrawReqByBank byBank = wdRequest.getByBank();
            return "Pay to Account : " +
                    byBank.getAccountNumber() +
                    "\n" +
                    "Pay to Bank : " +
                    byBank.getBankName() +
                    "\n" +
                    "With IFSC Code : " +
                    byBank.getIfscCode();
        }
        else if (wdRequest.getRequestType() == WithdrawReqType.BY_PHONE.getId()) {
            WithdrawReqByPhone byPhone = wdRequest.getByPhone();
            return "Pay to Phone Number : " +
                    byPhone.getPhNumber() +
                    "\n" +
                    "Using : PhonePe";
        }
        return null;
    }


    @Override
    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException, final Object response, Object helperObject) {
        if((exceptionThrown) && (!isAPIException)) {
            showErrShowHomeScreen((String) response);
            return;
        }
        Activity activity = getActivity();
        if (reqId == Request.USER_WITHDRAW_LIST) {
            if (isAPIException) {
                showErrShowHomeScreen((String) response);
                return;
            }
            final WithdrawRequestsHolder result = (WithdrawRequestsHolder) response;
            Runnable run = () -> populateTable(result);
            if (activity != null) {
                activity.runOnUiThread(run);
            }
        } else if (reqId == Request.WITHDRAW_CANCEL) {
            final Boolean result = (Boolean) response;
            if (result) {
                startPosOffset = 0;
                fetchRecords();
                if (activity instanceof MainActivity) {
                    ((MainActivity)activity).fetchUpdateMoney();
                }
                if (activity != null) {
                    Runnable run = this::run;
                    activity.runOnUiThread(run);
                }
            }
        } else if (reqId == Request.WITHDRAW_RECEIPT) {
            final byte[] contents = (byte[]) response;
            if (contents == null) {
                System.out.println("Bytes is null");
                return;
            }
            System.out.println("This is in receipt view" + contents.length);
            Runnable run = () -> {
                ViewReceipt viewReceipt = new ViewReceipt((getContext()), contents, "Transferred Receipt");
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                viewReceipt.show(fragmentManager, "dialog");
            };
            if (activity != null) {
                activity.runOnUiThread(run);
            }
        }
    }

    private void run() {
        View view = getView();
        if (view == null) {
            return;
        }
        Button filterStatus = getView().findViewById(R.id.filterStatus);
        Snackbar snackbar = Snackbar.make(filterStatus, "Withdraw Request Cancellation success", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
