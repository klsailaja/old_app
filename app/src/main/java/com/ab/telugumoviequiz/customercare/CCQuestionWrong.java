package com.ab.telugumoviequiz.customercare;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.BaseFragment;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.DialogAction;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.constants.CustomerCareReqType;
import com.ab.telugumoviequiz.main.MainActivity;
import com.ab.telugumoviequiz.main.Navigator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class CCQuestionWrong extends BaseFragment
        implements View.OnClickListener, CallbackResponse, DialogAction {

    private byte[] imgBytes;
    private static final int REQUEST_CODE = 2000;

    ActivityResultLauncher<String> startGallaryForResult =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    returnUri -> {
                        if (returnUri != null) {
                            System.out.println("returnUri.getPath()" + returnUri.getPath());
                            Bitmap bitmapImage = null;
                            try {
                                Activity activity = getActivity();
                                if (activity != null) {
                                    bitmapImage = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), returnUri);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (bitmapImage == null) {
                                return;
                            }
                            View view = getView();
                            if (view != null) {
                                ImageView imageView = view.findViewById(R.id.addBtn);
                                imageView.setImageBitmap(bitmapImage);
                            }

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmapImage.compress(Bitmap.CompressFormat.PNG, 50, stream);
                            imgBytes = stream.toByteArray();
                            try {
                                stream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

    public CCQuestionWrong() {
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
        return inflater.inflate(R.layout.cc_question_wrong, container, false);
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
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.addScreenShot) {
            int permissionCheck = ContextCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                startGallery();
            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE);
            }
        } else if (viewId == R.id.ccCreateBut) {
            HashMap<String, String> ccExtraDetailMap = new HashMap<>();
            String ccExtraDetails = CCUtils.encodeCCExtraValues(ccExtraDetailMap);

            CCUtils.createdCCTicket(CustomerCareReqType.QUESTION_WRONG.getId(),
                    this, ccExtraDetails, this.getActivity());
        } else if (viewId == R.id.removeScreenShot) {
            imgBytes = null;
            View view1 = getView();
            if (view1 != null) {
                ImageView imageView = view1.findViewById(R.id.addBtn);
                imageView.setImageBitmap(null);
            }
        }
    }

    private void startGallery() {
        startGallaryForResult.launch("image/*");
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
    public void handleResponse(int reqId, boolean exceptionThrown,
                               boolean isAPIException, Object response, Object userObject) {
        boolean errorHandled = handleServerError(exceptionThrown, isAPIException, response);
        if (errorHandled) {
            return;
        }
        boolean isApiErrorHandled = handleAPIError(isAPIException, response, 1, null, null);
        if (isApiErrorHandled) {
            return;
        }

        if (reqId == Request.CREATE_CC_ISSUE) {
            Long ccObjectId = (Long) response;
            if (ccObjectId == -1) {
                displayInfo("Error. Please retry", null);
                return;
            }

            String fileName = "id_" + ccObjectId;
            PostPictureTask postPictureTask = Request.getPostPictureTask();
            postPictureTask.setByteArray(imgBytes);
            postPictureTask.setFilename(fileName);
            postPictureTask.setCallbackResponse(this);
            postPictureTask.setActivity(getActivity(), "Processing. May take long time");
            Scheduler.getInstance().submit(postPictureTask);
        } else if (reqId == Request.POST_PIC_ISSUE) {
            Boolean result = (Boolean) response;
            String msg = "Successfully created Ticket";
            if (!result) {
                msg = "Failed to create ticket. Please retry";
            }
            displayInfo(msg, this);
        }
    }

    public void doAction(int calledId, Object userObject) {
        Activity mainActivity = getActivity();
        if (mainActivity instanceof MainActivity) {
            ((MainActivity)mainActivity).launchView(Navigator.CC_REQ_VIEW, null, false);
        }
    }
}
