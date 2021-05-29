package com.ab.telugumoviequiz.money;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.ab.telugumoviequiz.common.BaseFragment;

public class LifeCycle extends BaseFragment {

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate " + savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        System.out.println("onCreateView " + savedInstanceState);
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("onStart ");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("onResume ");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("onPause ");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("onStop ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("onDestroyView ");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        System.out.println("In onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }
}
