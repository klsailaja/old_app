package com.ab.telugumoviequiz.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.fragment.app.Fragment;

import com.ab.telugumoviequiz.R;

public class FirstFragment extends Fragment {
    private String helpContents;

    public FirstFragment(String helpContents) {
        this.helpContents = helpContents;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.help_window, container, false);
        WebView webView = view.findViewById(R.id.webview);
        webView.loadData(helpContents, "text/html", "UTF-8");
        return view;
    }
}