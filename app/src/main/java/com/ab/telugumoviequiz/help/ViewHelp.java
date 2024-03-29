package com.ab.telugumoviequiz.help;

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
import com.ab.telugumoviequiz.common.MessageListener;
import com.ab.telugumoviequiz.common.MyPagerAdapter;
import com.ab.telugumoviequiz.common.Utils;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class ViewHelp extends DialogFragment implements MessageListener {

    private final List<HelpTopic> localTopics;
    private final List<HelpTopic> englishTopics;
    private String englishMainHeading;
    private String localMainHeading;
    /*public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 2;*/
    private final String helpPreferencesKey;
    private View.OnClickListener onClickListener;


    public ViewHelp(List<HelpTopic> localTopics, List<HelpTopic> englishTopics,
                    String helpPreferencesKey) {
       this.localTopics = localTopics;
       this.englishTopics = englishTopics;
        this.helpPreferencesKey = helpPreferencesKey;
    }
    public void setEnglishMainHeading(String englishMainHeading) {
        this.englishMainHeading = englishMainHeading;
    }
    public void setLocalMainHeading(String localMainHeading) {
        this.localMainHeading = localMainHeading;
    }
    public void setOnClickListener(View.OnClickListener listener) {
        this.onClickListener = listener;
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
        String localLangHelpText = getHelpContents(localMainHeading, localTopics);
        String englishLangHelpText = getHelpContents(englishMainHeading, englishTopics);
        MyPagerAdapter adapterViewPager = new MyPagerAdapter(getChildFragmentManager(),
                localLangHelpText, englishLangHelpText, this, helpPreferencesKey);

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
        int height = (points[1] * 90/ 100);
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(width, height);
            }
        }
    }

    @Override
    public void passData(int reqId, List<String> data) {
        dismiss();
        /*String stateStr = data.get(0);
        int stateInt = Integer.parseInt(stateStr);
        Context context = getContext();
        if (context != null) {
            /*HelpPreferences.getInstance().writePreference(context,
                    helpPreferencesKey, stateInt);
        }*/
        if (onClickListener != null) {
            this.onClickListener.onClick(null);
        }
    }

    @Override
    public void onDismiss (@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    private String getHelpContents(String mainHeading, List<HelpTopic> topics) {

        StringBuilder stringBuilder = new StringBuilder("<HTML>");
        stringBuilder.append("<body>");
        stringBuilder.append("<h2>");
        stringBuilder.append(mainHeading);
        stringBuilder.append("</h2>");

        String colorName;
        for (HelpTopic topic : topics) {
            stringBuilder.append("<font size=\"3\" face=\"arial\" color=\"");
            stringBuilder.append("black");
            stringBuilder.append("\"");
            stringBuilder.append("</font>");
            stringBuilder.append("<h3>");
            stringBuilder.append(topic.getTopicHeading());
            stringBuilder.append("</h3>");
            stringBuilder.append("<ul>");
            for (HelpMessage helpMessage : topic.getTopicMessages()) {
                colorName = "black";
                if (helpMessage.getMsgSeverity() == 2) {
                    colorName = "red";
                }
                stringBuilder.append("<font size=\"3\" face=\"arial\" color=\"");
                stringBuilder.append(colorName);
                stringBuilder.append("\"");
                stringBuilder.append("</font>");
                stringBuilder.append("<li>");
                stringBuilder.append(helpMessage.getMessage());
                if (helpMessage.getSecondLevelMessages().size() > 0) {
                    stringBuilder.append("<ul>");
                    for (HelpMessage secondLevelMsgs : helpMessage.getSecondLevelMessages()) {
                        colorName = "black";
                        if (secondLevelMsgs.getMsgSeverity() == 2) {
                            colorName = "red";
                        }
                        stringBuilder.append("<font size=\"3\" face=\"arial\" color=\"");
                        stringBuilder.append(colorName);
                        stringBuilder.append("\"");
                        stringBuilder.append("</font>");
                        stringBuilder.append("<li>");
                        stringBuilder.append(secondLevelMsgs.getMessage());
                        stringBuilder.append("</li>");
                    }
                    stringBuilder.append("</ul>");
                }
                stringBuilder.append("</li>");
            }
            stringBuilder.append("</ul>");
        }
        return stringBuilder.toString();
    }
}
