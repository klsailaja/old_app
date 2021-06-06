package com.ab.telugumoviequiz.help;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.MessageListener;

import java.util.ArrayList;
import java.util.List;

public class HelpFragment extends Fragment implements View.OnClickListener {
    private final String helpContents;
    private final int orientation;
    private final MessageListener listener;
    private final String helpPreferencesKey;

    public HelpFragment(String helpContents, int orientation,
                        MessageListener listener, String helpPreferencesKey) {
        this.helpContents = helpContents;
        this.orientation = orientation;
        this.listener = listener;
        this.helpPreferencesKey = helpPreferencesKey;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int helpWindowLayoutFileName = R.layout.help_window;
        View view = inflater.inflate(helpWindowLayoutFileName, container, false);
        WebView webView = view.findViewById(R.id.webview);
        webView.loadData(helpContents, "text/html", "UTF-8");

        Button closeBut = view.findViewById(R.id.helpCloseBut);
        closeBut.setOnClickListener(this);

        Context context = getContext();
        if (context != null) {
            CheckBox checkBox = view.findViewById(R.id.checkbox1);
            int state = HelpPreferences.getInstance().readPreference(context,
                    helpPreferencesKey);
            checkBox.setChecked(state == 1);
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.helpCloseBut) {
            View root = getView();
            if (root == null) {
                return;
            }
            CheckBox checkBox = root.findViewById(R.id.checkbox1);
            String stateStr = "0";
            if (checkBox.isChecked()) {
                stateStr = "1";
            }
            List<String> values = new ArrayList<>();
            values.add(stateStr);
            listener.passData(-1, values);
        }
    }
}