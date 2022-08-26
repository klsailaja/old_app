package com.ab.telugumoviequiz.faq;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.BaseFragment;

import java.util.List;

public class MoreGamesView extends BaseFragment {
    public MoreGamesView() {
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.faq_main, container, false);
        TextView introText = root.findViewById(R.id.add_money_intro);
        String moreGamesIntro = getString(R.string.moreGamesViewIntro);
        introText.setText(moreGamesIntro);
        ExpandableListView expandableListView = root.findViewById(R.id.expandableListView);
        FAQModelBuilder faqModelBuilder = new FAQModelBuilder("moregames.txt", getContext());
        List<FAQEntry> faqList = faqModelBuilder.getFileContents();
        ExpandableListAdapter expandableListAdapter = new CustomExpandableListAdapter(getContext(), faqList);
        expandableListView.setAdapter(expandableListAdapter);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
