package com.ab.telugumoviequiz.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.BaseFragment;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.Constants;
import com.ab.telugumoviequiz.common.GetTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.main.MainActivity;
import com.ab.telugumoviequiz.main.Navigator;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.ab.telugumoviequiz.common.Constants.CHAT_MAX_ENTRIES;

public class ChatView extends BaseFragment implements View.OnClickListener, CallbackResponse, Runnable {
    private ViewAdapter chatAdapter;
    private final List<Chat> data = new ArrayList<>();
    private static final String key1 = "FETCHED_ENDTIME";
    private long endTimeFetched = -1;
    private ScheduledFuture<?> fetchTask = null;
    private View textView;

    private void fetchRecords() {
        Bundle bundle = ((MainActivity)getActivity()).getParams(Navigator.CHAT_VIEW);
        if (bundle != null) {
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
        long startTime;
        if (endTimeFetched == -1) {
            endTimeFetched = System.currentTimeMillis();
            startTime = endTimeFetched - Constants.CHAT_MAX_DURATION_IN_MILLIS;
        } else {
            startTime = endTimeFetched;
            endTimeFetched = System.currentTimeMillis();
        }

        Snackbar snackbar = Snackbar.make(textView, "Fetching Chat Messages", Snackbar.LENGTH_SHORT);
        snackbar.show();

        GetTask<Chat[]> request = Request.getChatMessages(startTime, endTimeFetched);
        request.setCallbackResponse(this);
        Scheduler.getInstance().submit(request);
    }

    private void handleListeners(View.OnClickListener listener) {
    }

    @Override
    public void run() {
        fetchRecords();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.chat_view, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        chatAdapter = new ViewAdapter(data);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        textView = root.findViewById(R.id.inviteBut);
        if (Constants.TEST_MODE == 0) {
            fetchTask = Scheduler.getInstance().submitRepeatedTask(this, 0, 10, TimeUnit.SECONDS);
        } else {
            data.clear();
            data.addAll(getChatData());
            fillStrTime();
        }
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
        Bundle bundle = ((MainActivity)getActivity()).getParams(Navigator.CHAT_VIEW);
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putLong(key1, endTimeFetched);
        if (fetchTask != null) {
            fetchTask.cancel(true);
        }
    }

    @Override
    public void onClick(View view) {
    }

    private void fillStrTime() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        String datePattern = "HH:mm";
        for (Chat chatMsg : data) {
            Date date = new Date(chatMsg.getTimeStamp());
            simpleDateFormat.applyPattern(datePattern);
            String timeStr = simpleDateFormat.format(date);
            chatMsg.setStrTime(timeStr);
        }
    }

    private List<Chat> getChatData() {
        List<Chat> data = new ArrayList<>();

        Chat chat0 = new Chat();
        chat0.setSenderUserId(-1);
        chat0.setSenderName("TestUser1");
        chat0.setMessage("Message Testing");
        chat0.setTimeStamp(System.currentTimeMillis());
        data.add(chat0);

        Chat chat1 = new Chat();
        chat1.setSenderUserId(UserDetails.getInstance().getUserProfile().getId());
        chat1.setSenderName("Rajasekhar");
        chat1.setMessage("Anyone coming for Rs.10 at 6:40 PM. GameId 1000");
        chat1.setTimeStamp(System.currentTimeMillis());
        data.add(chat1);

        Chat chat2 = new Chat();
        chat2.setSenderUserId(20);
        chat2.setSenderName("Sailu");
        chat2.setMessage("I am coming");
        chat2.setTimeStamp(System.currentTimeMillis());
        data.add(chat2);

        chat1 = new Chat();
        chat1.setSenderUserId(UserDetails.getInstance().getUserProfile().getId());
        chat1.setSenderName("Rajasekhar");
        chat1.setMessage("Anyone coming for Rs.10 at 6:40 PM. GameId 1000");
        chat1.setTimeStamp(System.currentTimeMillis());
        data.add(chat1);

        chat2 = new Chat();
        chat2.setSenderUserId(20);
        chat2.setSenderName("Sailu");
        chat2.setMessage("I am coming");
        chat2.setTimeStamp(System.currentTimeMillis());
        data.add(chat2);

        return data;
    }

    @Override
    public void handleResponse(int reqId, final boolean exceptionThrown, final boolean isAPIException,
                               final Object response, final Object userObject) {
        Runnable run = () -> {
            if((exceptionThrown) && (!isAPIException)) {
                Snackbar snackbar = Snackbar.make(textView, (String) response, Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        };
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(run);
            return;
        }

        if (reqId == Request.CHAT_BULK_FETCH) {
            if (isAPIException) {
                run = () -> {
                    Snackbar snackbar = Snackbar.make(textView, (String) response, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                };
                activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(run);
                }
                return;
            }

            final Chat[] result = (Chat[]) response;
            List<Chat> newEntries = Arrays.asList(result);
            data.addAll(newEntries);
            fillStrTime();
            if (data.size() > CHAT_MAX_ENTRIES) {
                int delStartIndex = data.size() - CHAT_MAX_ENTRIES;
                data.subList(0, delStartIndex).clear();
            }
            run = () -> chatAdapter.notifyDataSetChanged();
            if (activity != null) {
                activity.runOnUiThread(run);
            }
        }
    }
}
