package com.ab.telugumoviequiz.customercare;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.BaseFragment;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.DialogAction;
import com.ab.telugumoviequiz.common.NotifyTextChanged;
import com.ab.telugumoviequiz.common.PATextWatcher;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.constants.CustomerCareReqType;
import com.ab.telugumoviequiz.main.MainActivity;
import com.ab.telugumoviequiz.main.Navigator;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

public class CCAddedMoneyIssue extends BaseFragment
        implements View.OnClickListener, CallbackResponse,
        DatePickerDialog.OnDateSetListener, NotifyTextChanged, DialogAction {

    private static final int REQUEST_CODE = 2000;
    private byte[] imgBytes;
    private PATextWatcher moneyWatcher;
    private static final String ISSUE_DATE_KEY = "ISSUE_DATE";
    private static final String ISSUE_AMT_KEY = "ISSUE_AMOUNT";

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
        handleTextWatchers(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        handleListeners(null);
        handleTextWatchers(false);
    }

    @Override
    public void textChanged(int viewId) {
        if (viewId == R.id.ccAddedMoneyAmtET) {
            validateMoneyAmount();
        }
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
            boolean amtValidation = validateMoneyAmount();
            if (!amtValidation) {
                return;
            }
            View currentView = getView();
            if (currentView == null) {
                return;
            }
            TextView issueDate = currentView.findViewById(R.id.ccAddedDateText);
            String issueDateStr = issueDate.getText().toString().trim();
            if (issueDateStr.length() == 0) {
                displayError("Enter money added date", null);
                return;
            }
            StringTokenizer stringTokenizer = new StringTokenizer(issueDateStr, "/");
            int day = Integer.parseInt(stringTokenizer.nextToken().trim());
            int month = Integer.parseInt(stringTokenizer.nextToken().trim());
            int year = Integer.parseInt(stringTokenizer.nextToken().trim());
            boolean issueDateValidation = CCUtils.validateDate(day, month, year, 72);
            if (!issueDateValidation) {
                displayError("Money added date should be less than 72 hrs", null);
                return;
            }

            TextView issueAmt = currentView.findViewById(R.id.ccAddedMoneyAmtET);
            String issueAmtStr = issueAmt.getText().toString().trim();

            HashMap<String,String> ccExtraDetailMap = new HashMap<>();
            ccExtraDetailMap.put(ISSUE_DATE_KEY, issueDateStr);
            ccExtraDetailMap.put(ISSUE_AMT_KEY, issueAmtStr);

            String ccExtraDetails = CCUtils.encodeCCExtraValues(ccExtraDetailMap);

            CCUtils.createdCCTicket(CustomerCareReqType.ADDED_MONEY_NOT_UPDATED.getId(),
                    this, ccExtraDetails, this.getActivity());
        } else if (viewId == R.id.ccAddedMoneySetDate) {
            CCUtils.showDateChooser(getContext(), this);
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

    private boolean validateMoneyAmount() {
        View view = getView();
        if (view == null) {
            return false;
        }
        TextView mailUI = view.findViewById(R.id.ccAddedMoneyAmtET);
        String str = mailUI.getText().toString().trim();
        String result = Utils.fullValidate(str, "Amount", false, -1, -1, true);
        boolean showErr = true;
        if (result != null) {
            showErr = false;
        }
        if (!showErr) {
            mailUI.setError(result);
            mailUI.requestFocus();
            return false;
        }
        int amtInt = Integer.parseInt(str);
        if ((amtInt < 100) || (amtInt > 2000)) {
            mailUI.setError("Valid values are between 100 - 2000");
            mailUI.requestFocus();
            return false;
        }
        return true;
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

        Button setDateButton = view.findViewById(R.id.ccAddedMoneySetDate);
        setDateButton.setOnClickListener(listener);
    }

    private void handleTextWatchers(boolean add) {
        View view = getView();
        if (view == null) {
            return;
        }
        TextView ccMoneyTextView = view.findViewById(R.id.ccAddedMoneyAmtET);
        if (add) {
            moneyWatcher = new PATextWatcher(ccMoneyTextView, this);
            ccMoneyTextView.addTextChangedListener(moneyWatcher);
        } else {
            ccMoneyTextView.removeTextChangedListener(moneyWatcher);
        }
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
            if (imgBytes == null) {
                String msg = "Successfully created Ticket";
                displayInfo(msg, this);
                return;
            }
            String fileName = "id_" + ccObjectId;
            PostPictureTask postPictureTask = Request.getPostPictureTask();
            postPictureTask.setByteArray(imgBytes);
            postPictureTask.setFilename(fileName);
            postPictureTask.setCallbackResponse(this);
            postPictureTask.setActivity(getActivity(), "Processing...");
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

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        View currentView = getView();
        if (currentView == null) {
            return;
        }
        TextInputEditText ccAddedDateText = currentView.findViewById(R.id.ccAddedDateText);
        String dateStr = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        ccAddedDateText.setText(dateStr);
    }

    public void doAction(int calledId, Object userObject) {
        Activity mainActivity = getActivity();
        if (mainActivity instanceof MainActivity) {
            ((MainActivity)mainActivity).launchView(Navigator.CC_REQ_VIEW, null, false);
        }
    }
}
