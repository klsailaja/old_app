package com.ab.telugumoviequiz.customercare;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.BaseFragment;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.PostTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.UserDetails;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.LinkedMultiValueMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class CCAddedMoneyIssue extends BaseFragment
        implements View.OnClickListener, CallbackResponse {

    public static final int REQUEST_CODE = 2000;

    public CCAddedMoneyIssue() {
        super();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cc_added_money, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        handleListeners(this);
        //handleTextWatchers(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        handleListeners(null);
        //handleTextWatchers(false);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        System.out.println("In onClick");
        if (viewId == R.id.addScreenShot) {
            System.out.println("In add screen shot");
            int permissionCheck = ContextCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                System.out.println("In If");
                startGallery();
            } else {
                System.out.println("In Else");
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE);
            }
        }
    }

    private void startGallery() {
        @SuppressLint("IntentReset")
        Intent cameraIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        cameraIntent.setType("image/*");

        startActivityForResult(cameraIntent, 1000);
        /*if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {

        }*/
    }


    private void handleListeners(View.OnClickListener listener) {
        View view = getView();
        if (view == null) {
            return;
        }
        Button createNewBut = view.findViewById(R.id.ccCreateBut);
        createNewBut.setOnClickListener(listener);

        Button screenShotAdd = view.findViewById(R.id.addScreenShot);
        screenShotAdd.setOnClickListener(listener);

        Button screenShotRemove = view.findViewById(R.id.removeScreenShot);
        screenShotRemove.setOnClickListener(listener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("In onActivityResult in frag" + requestCode);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1000) {
                System.out.println("After all ifs");
                Uri returnUri = data.getData();
                if (returnUri != null) {
                    System.out.println("returnUri.getPath()" + returnUri.getPath());
                }
                Bitmap bitmapImage = null;
                try {
                    bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("image is " + bitmapImage);
                if (bitmapImage == null) {
                    return;
                }
                ImageView imageView = getView().findViewById(R.id.addBtn);
                imageView.setImageBitmap(bitmapImage);

                PostPictureTask createCCTask =
                        Request.getCreateCCTask();
                String imgPath = returnUri.getPath();

                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = this.getActivity().getContentResolver().query(returnUri, projection, null,
                        null, null);
                if (cursor != null) {
                    int column_index =
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String imgPath1 = cursor.getString(column_index);
                    imgPath = imgPath1;
                    System.out.println("imgPath from db" + imgPath1);
                }
                cursor.close();

                //Resource fileSystemResource = new FileSystemResource(new File(imgPath));
                //Resource fileSystemResource = new ClassPathResource("res/drawable/add.png");

                LinkedMultiValueMap formData = new LinkedMultiValueMap<String, Object>();
                //formData.add("file", fileSystemResource);


                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                CustomerTicketHolder ticketHolder = new CustomerTicketHolder();

                CustomerTicket customerTicket = new CustomerTicket();
                customerTicket.setUserId(UserDetails.getInstance().getUserProfile().getId());
                customerTicket.setRequestType(1);
                customerTicket.setOpenedTime(System.currentTimeMillis());
                customerTicket.setStatus(1);
                customerTicket.setExtraDetails("<Test>Testing1</Test>");
                ticketHolder.setCustomerTicket(customerTicket);
                //formData.add("description", "ticket");

                createCCTask.setCallbackResponse(this);
                //createCCTask.setFormData(formData);
                createCCTask.setByteArray(byteArray);

                Scheduler.getInstance().submit(createCCTask);
            }
        }
    }

    @Override
    public void handleResponse(int reqId, boolean exceptionThrown,
                               boolean isAPIException, Object response, Object userObject) {
        System.out.println(reqId + ":" + exceptionThrown + ":" + isAPIException + ":" + response + ":" + userObject);
    }
}
