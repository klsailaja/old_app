package com.ab.telugumoviequiz.customercare;

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
import com.ab.telugumoviequiz.main.MainActivity;
import com.ab.telugumoviequiz.main.Navigator;
import com.ab.telugumoviequiz.main.UserProfile;
import com.ab.telugumoviequiz.withdraw.ViewReceipt;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CCTableView extends BaseFragment implements PopupMenu.OnMenuItemClickListener,
        View.OnClickListener, CallbackResponse, DialogAction {

    private int startPosOffset = 0;
    private ViewAdapter tableAdapter;
    private final List<CustomerTicket> tableData = new ArrayList<>();
    private int ccStatus = -1;
    public final static int CANCEL_BUTTON_ID = 1;
    public final static int MORE_OPTIONS_BUTTON_ID = 2;
    private final static int FILTER_ALL = 1;
    private final static int FILTER_OPENED = 2;
    private final static int FILTER_CLOSED = 3;
    private final static int FILTER_NOT_AN_ISSUE = 4;
    private final static int FILTER_CANCELLED = 5;
    private final static int VIEW_DETAILS = 10;
    private final static int VIEW_ISSUE_PIC = 11;
    private final static int VIEW_RESOLVED_PIC = 12;
    private final static int VIEW_CLOSED_CMTS = 13;

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

        FloatingActionButton newCCBut = view.findViewById(R.id.fab);
        newCCBut.setOnClickListener(listener);
    }

    private void fetchRecords() {
        UserProfile userProfile = UserDetails.getInstance().getUserProfile();
        GetTask<CCTicketsHolder> request = Request.getCCReqs(userProfile.getId(), startPosOffset, ccStatus);
        request.setCallbackResponse(this);
        request.setActivity(getActivity(), null);
        Scheduler.getInstance().submit(request);
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
        tableHeadings[0] = resources.getString(R.string.cc_col1);
        tableHeadings[1] = resources.getString(R.string.cc_col2);
        tableHeadings[2] = resources.getString(R.string.cc_col3);
        tableHeadings[3] = resources.getString(R.string.cc_col4);
        tableHeadings[4] = resources.getString(R.string.cc_col5);
        tableHeadings[5] = resources.getString(R.string.cc_col6);
        tableHeadings[6] = resources.getString(R.string.cc_col7);

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
    public void doAction(int id, Object userObject) {
        if (id == CANCEL_BUTTON_ID) {
            String wdRefId = (String) userObject;
            UserProfile userProfile = UserDetails.getInstance().getUserProfile();
            GetTask<Boolean> cancelTask = Request.getCCReqCancel(userProfile.getId(), wdRefId);
            cancelTask.setCallbackResponse(this);
            Scheduler.getInstance().submit(cancelTask);
        }
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
            CharSequence[] accTypes = resources.getTextArray(R.array.cc_options);
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
            CustomerTicket selectedWDRequest = (CustomerTicket) view.getTag();
            Resources resources = requireActivity().getResources();
            CharSequence[] accTypes = resources.getTextArray(R.array.cc_more_options);
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
                ((MainActivity)activity).launchView(Navigator.NEW_CC_REQUEST, null, false);
            }
        }
    }
    @Override
    public boolean onMenuItemClick (MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case FILTER_ALL: {
                startPosOffset = 0;
                ccStatus = -1;
                fetchRecords();
                break;
            }
            case FILTER_OPENED: {
                startPosOffset = 0;
                ccStatus = 1;
                fetchRecords();
                break;
            }
            case FILTER_CLOSED: {
                startPosOffset = 0;
                ccStatus = 2;
                fetchRecords();
                break;
            }
            case FILTER_CANCELLED: {
                startPosOffset = 0;
                ccStatus = 4;
                fetchRecords();
                break;
            }
            case FILTER_NOT_AN_ISSUE: {
                startPosOffset = 0;
                ccStatus = 3;
                fetchRecords();
                break;
            }
            case VIEW_DETAILS: {
                CustomerTicket selectedCCRequest = (CustomerTicket) item.getActionView().getTag();
                String details = getDetails(selectedCCRequest);
                Utils.showMessage("Details", details, getContext(), null);
                break;
            }
            case VIEW_ISSUE_PIC: {
                CustomerTicket selectedCCRequest = (CustomerTicket) item.getActionView().getTag();
                boolean isPicEmpty = (selectedCCRequest.getProblemPicId() == -1);
                if (isPicEmpty) {
                    displayInfo("Issue Pic not found", null);
                    return false;
                }
                GetTask<byte[]> viewReceiptTask = Request.getReceiptTask(selectedCCRequest.getProblemPicId());
                viewReceiptTask.setCallbackResponse(this);
                Scheduler.getInstance().submit(viewReceiptTask);
                break;
            }
            case VIEW_RESOLVED_PIC: {
                CustomerTicket selectedCCRequest = (CustomerTicket) item.getActionView().getTag();
                boolean isPicEmpty = (selectedCCRequest.getResolvedPicId() == -1);
                if (isPicEmpty) {
                    displayInfo("Resolved Pic not found", null);
                    return false;
                }
                GetTask<byte[]> viewReceiptTask = Request.getReceiptTask(selectedCCRequest.getProblemPicId());
                viewReceiptTask.setCallbackResponse(this);
                Scheduler.getInstance().submit(viewReceiptTask);
                break;
            }
            case VIEW_CLOSED_CMTS: {
                CustomerTicket selectedCCRequest = (CustomerTicket) item.getActionView().getTag();
                boolean isReqClosed = (selectedCCRequest.getStatus() == 2);
                String closedCmts = "Closed comments updated after the request is closed";
                if (isReqClosed) {
                    closedCmts = selectedCCRequest.getClosedCmts();
                }
                Utils.showMessage("Comments", closedCmts, getContext(), null);
                break;
            }
        }
        return true;
    }

    private String getDetails(CustomerTicket customerTicket) {
        String details = customerTicket.getExtraDetails();
        details = details.trim();
        StringTokenizer first = new StringTokenizer(details, ";");
        StringBuilder stringBuilder = new StringBuilder();
        while (first.hasMoreTokens()) {
            StringTokenizer second = new StringTokenizer(first.nextToken(), "=");
            stringBuilder.append(second.nextToken().trim());
            stringBuilder.append(" :");
            stringBuilder.append(second.nextToken().trim());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    private void populateTable(CCTicketsHolder details) {
        View view = getView();
        if (view == null) {
            return;
        }
        Button prevButton = view.findViewById(R.id.myreferals_prev_but);
        Button nextButton = view.findViewById(R.id.myreferals_next_but);

        prevButton.setEnabled(details.isPrevEnabled());
        nextButton.setEnabled(details.isNextEnabled());
        List<CustomerTicket> list = details.getList();
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
    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException, final Object response, Object helperObject) {
        if((exceptionThrown) && (!isAPIException)) {
            showErrShowHomeScreen((String) response);
            return;
        }
        Activity activity = getActivity();
        if (reqId == Request.USER_CC_LIST) {
            if (isAPIException) {
                showErrShowHomeScreen((String) response);
                return;
            }
            final CCTicketsHolder result = (CCTicketsHolder) response;
            Runnable run = () -> populateTable(result);
            if (activity != null) {
                activity.runOnUiThread(run);
            }
        } else if (reqId == Request.CC_CANCEL) {
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
        } else if (reqId == Request.CC_RECEIPT) {
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
        Snackbar snackbar = Snackbar.make(filterStatus, "CustomerCare Request Cancellation success", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
