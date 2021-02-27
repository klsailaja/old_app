package com.ab.telugumoviequiz.withdraw;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.Utils;

public class ViewReceipt extends DialogFragment implements View.OnClickListener {
    private Context context;
    private byte[] receiptContents;
    private String title;

    public ViewReceipt(Context context, byte[] receiptContents, String title) {
        this.context = context;
        this.receiptContents = receiptContents;
        this.title = title;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDialog().setTitle(title);
        View view = inflater.inflate(R.layout.view_receipt, container, false);
        Button closeButton = view.findViewById(R.id.user_answers_close_but);
        closeButton.setOnClickListener(this);
        ImageView imageView = view.findViewById(R.id.image);

        Bitmap bm = BitmapFactory.decodeByteArray(receiptContents, 0, receiptContents.length);
        //int[] points = Utils.getScreenWidth(context);

        //imageView.setMinimumWidth(points[0]);
        //imageView.setMinimumHeight(points[1]);
        imageView.setImageBitmap(bm);
        return view;
    }

    @Override
    public void onResume() {
        // Sets the height and the width of the DialogFragment
        super.onResume();
        int[] points = Utils.getScreenWidth(getContext());
        int width = (points[0] * 9) /10;
        //int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = (points[1] * 9/ 10);
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
}
