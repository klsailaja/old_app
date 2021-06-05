package com.ab.telugumoviequiz.common;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.ab.telugumoviequiz.R;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class ViewHelp extends DialogFragment implements View.OnClickListener {

    private final List<HelpTopic> topics;
    private String mainHeading = "";

    public ViewHelp(List<HelpTopic> topics) {
       this.topics = topics;
    }
    public void setMainHeading(String mainHeading) {
        this.mainHeading = mainHeading;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.help_tabs_layout, container, false);

        MyPagerAdapter adapterViewPager = new MyPagerAdapter(getChildFragmentManager(),
                "", getHelpContents());
        ViewPager vpPager = root.findViewById(R.id.vpPager);
        vpPager.setAdapter(adapterViewPager);
        TabLayout tabs = root.findViewById(R.id.tabs);
        tabs.setupWithViewPager(vpPager);
        return root;
    }

    @Override
    public void onResume() {
        // Sets the height and the width of the DialogFragment
        super.onResume();
        int[] points = Utils.getScreenWidth(getContext());
        int width = (points[0] * 4) /4;
        //int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = (points[1] * 4/ 4);
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(width, height);
            }
        }
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }

    @Override
    public void onDismiss (@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    private String getHelpContents() {
        StringBuilder stringBuilder = new StringBuilder("<HTML>");
        stringBuilder.append("<body>");
        stringBuilder.append("<h1>");
        stringBuilder.append(mainHeading);
        stringBuilder.append("</h1>");


        for (HelpTopic topic : topics) {
            stringBuilder.append("<h2>");
            stringBuilder.append(topic.getTopicHeading());
            stringBuilder.append("</h2>");
            /*stringBuilder.append("<p>");
            stringBuilder.append("<font size=\"10\" face=\"arial\" color=\"red\"");
            stringBuilder.append("This is color text");
            stringBuilder.append("</font>");
            stringBuilder.append("</p>");*/
            stringBuilder.append("<ul>");
            stringBuilder.append("<font size=\"4\" face=\"arial\" color=\"red\"");
            stringBuilder.append("</font>");
            stringBuilder.append("<li>This is a test meg1</li>");
            stringBuilder.append("<font size=\"4\" face=\"arial\" color=\"black\"");
            stringBuilder.append("</font>");
            stringBuilder.append("<li>This is a test mesg2</li>");
            stringBuilder.append("<li>This is a test mesg3</li>");
            stringBuilder.append("</ul>");
        }
        return stringBuilder.toString();
    }
}
