package com.ab.telugumoviequiz.kyc;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.BaseFragment;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.DialogAction;
import com.ab.telugumoviequiz.common.GetTask;
import com.ab.telugumoviequiz.common.PostTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.customercare.PostPictureTask;
import com.ab.telugumoviequiz.main.MainActivity;
import com.ab.telugumoviequiz.main.Navigator;
import com.ab.telugumoviequiz.withdraw.ViewReceipt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KYCView extends BaseFragment
        implements View.OnClickListener, CallbackResponse,
        DialogAction {

    private byte[] aadharFrontPageBytes;
    private byte[] aadharBackPageBytes;
    private byte[] panPageBytes;
    private static final int REQUEST_CODE = 2000;

    ActivityResultLauncher<String> startaadharFrontPageGallary =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    returnUri -> {
                        if (returnUri != null) {
                            Bitmap bitmapImage = null;
                            try {
                                Activity activity = getActivity();
                                if (activity != null) {
                                    bitmapImage = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), returnUri);
                                }
                            } catch (IOException ignored) {
                            }
                            if (bitmapImage == null) {
                                return;
                            }
                            View view = getView();
                            if (view != null) {
                                ImageView imageView = view.findViewById(R.id.frontPage);
                                imageView.setImageBitmap(bitmapImage);
                            }
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmapImage.compress(Bitmap.CompressFormat.PNG, 50, stream);
                            aadharFrontPageBytes = stream.toByteArray();
                            try {
                                stream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
    ActivityResultLauncher<String> startaadharBackPageGallary =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    returnUri -> {
                        if (returnUri != null) {
                            Bitmap bitmapImage = null;
                            try {
                                Activity activity = getActivity();
                                if (activity != null) {
                                    bitmapImage = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), returnUri);
                                }
                            } catch (IOException ignored) {
                            }
                            if (bitmapImage == null) {
                                return;
                            }
                            View view = getView();
                            if (view != null) {
                                ImageView imageView = view.findViewById(R.id.backPage);
                                imageView.setImageBitmap(bitmapImage);
                            }
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmapImage.compress(Bitmap.CompressFormat.PNG, 50, stream);
                            aadharBackPageBytes = stream.toByteArray();
                            try {
                                stream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
    ActivityResultLauncher<String> panPageGallary =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    returnUri -> {
                        if (returnUri != null) {
                            Bitmap bitmapImage = null;
                            try {
                                Activity activity = getActivity();
                                if (activity != null) {
                                    bitmapImage = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), returnUri);
                                }
                            } catch (IOException ignored) {
                            }
                            if (bitmapImage == null) {
                                return;
                            }
                            View view = getView();
                            if (view != null) {
                                ImageView imageView = view.findViewById(R.id.panPage);
                                imageView.setImageBitmap(bitmapImage);
                            }
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmapImage.compress(Bitmap.CompressFormat.PNG, 50, stream);
                            panPageBytes = stream.toByteArray();
                            try {
                                stream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

    public KYCView() {
        super();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        long uid = UserDetails.getInstance().getUserProfile().getId();
        GetTask<KYCEntry> getKYCTask = Request.getKYV(uid);
        getKYCTask.setCallbackResponse(this);
        getKYCTask.setActivity(this.getActivity(), "Processing...");
        Scheduler.getInstance().submit(getKYCTask);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.kyc, container, false);
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
        long dbPictureId;
        if (viewId == R.id.addFront) {
            int permissionCheck = ContextCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                startGallaryForaadharFront();
            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE);
            }
        } else if (viewId == R.id.addBack) {
            int permissionCheck = ContextCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                startGallaryForaadharBack();
            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE);
            }
        } else if (viewId == R.id.addPan) {
            int permissionCheck = ContextCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                startGallaryForPanPage();
            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE);
            }
        } else if (viewId == R.id.removeFront) {
            aadharFrontPageBytes = null;
            View view1 = getView();
            if (view1 != null) {
                ImageView imageView = view1.findViewById(R.id.frontPage);
                imageView.setImageBitmap(null);
            }
        } else if (viewId == R.id.removeBack) {
            aadharBackPageBytes = null;
            View view1 = getView();
            if (view1 != null) {
                ImageView imageView = view1.findViewById(R.id.backPage);
                imageView.setImageBitmap(null);
            }
        } else if (viewId == R.id.removePan) {
            panPageBytes = null;
            View view1 = getView();
            if (view1 != null) {
                ImageView imageView = view1.findViewById(R.id.panPage);
                imageView.setImageBitmap(null);
            }
        } else if (viewId == R.id.viewFront) {
            dbPictureId = (Long)view.getTag();
            showPictureView(dbPictureId, 1);
        } else if (viewId == R.id.viewBack) {
            dbPictureId = (Long)view.getTag();
            showPictureView(dbPictureId, 2);
        } else if (viewId == R.id.viewPan) {
            dbPictureId = (Long)view.getTag();
            showPictureView(dbPictureId, 3);
        } else if (viewId == R.id.kycCreateBut) {
            if ((aadharFrontPageBytes == null) || (aadharBackPageBytes == null)
                    || (panPageBytes == null)) {
                displayInfo("Please select all 3 images", null);
                return;
            }
            KYCEntry kycEntry = new KYCEntry();
            kycEntry.setAfpId(-1);
            kycEntry.setAbpId(-1);
            kycEntry.setPpId(-1);
            kycEntry.setStatus("Not Submitted");
            kycEntry.setUserId(UserDetails.getInstance().getUserProfile().getId());

            PostTask<KYCEntry, Long> getCreateKYCTask = Request.getCreateKYCTask();
            getCreateKYCTask.setPostObject(kycEntry);
            getCreateKYCTask.setCallbackResponse(this);
            Scheduler.getInstance().submit(getCreateKYCTask);
        }
    }
    private void showPictureView(long id, int type) {
        GetTask<byte[]> viewReceiptTask = Request.getReceiptTask(id);
        viewReceiptTask.setCallbackResponse(this);
        viewReceiptTask.setHelperObject(type);
        Scheduler.getInstance().submit(viewReceiptTask);
    }

    private void startGallaryForaadharFront() {
        startaadharFrontPageGallary.launch("image/*");
    }
    private void startGallaryForaadharBack() {
        startaadharBackPageGallary.launch("image/*");
    }
    private void startGallaryForPanPage() {
        panPageGallary.launch("image/*");
    }

    private void handleListeners(View.OnClickListener listener) {
        View view = getView();
        if (view == null) {
            return;
        }
        Button button = view.findViewById(R.id.addFront);
        button.setOnClickListener(listener);

        button = view.findViewById(R.id.removeFront);
        button.setOnClickListener(listener);

        button = view.findViewById(R.id.addBack);
        button.setOnClickListener(listener);

        button = view.findViewById(R.id.removeBack);
        button.setOnClickListener(listener);

        button = view.findViewById(R.id.addPan);
        button.setOnClickListener(listener);

        button = view.findViewById(R.id.removePan);
        button.setOnClickListener(listener);

        button = view.findViewById(R.id.kycCreateBut);
        button.setOnClickListener(listener);

        button = view.findViewById(R.id.viewFront);
        button.setOnClickListener(listener);

        button = view.findViewById(R.id.viewBack);
        button.setOnClickListener(listener);

        button = view.findViewById(R.id.viewPan);
        button.setOnClickListener(listener);
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
        if (reqId == Request.KYC_GET_OBJECT) {
            Runnable run = () -> {
                KYCEntry kycEntry = (KYCEntry) response;
                setView(kycEntry);
            };
            Activity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(run);
            }
        } else if (reqId == Request.WITHDRAW_RECEIPT) {
            final byte[] contents = (byte[]) response;
            if (contents == null) {
                return;
            }
            int type = (int) userObject;

            Runnable run = () -> {
                ViewReceipt viewReceipt = new ViewReceipt((getContext()), contents, "KYC Saved Image");
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                viewReceipt.show(fragmentManager, "dialog");
                ImageView imageView = getImageView(type);
                if (imageView != null) {
                    imageView.setImageBitmap(BitmapFactory.decodeByteArray(contents, 0, contents.length));
                }
            };
            Activity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(run);
            }
        } else if (reqId == Request.KYC_CREATE) {
            Long kycEntryId = (Long) response;
            if (kycEntryId > -1) {
                String fileName = kycEntryId + "_type_1";
                PostPictureTask postPictureTask = Request.getKYCPostPictureTask();
                postPictureTask.setCallbackResponse(this);
                postPictureTask.setByteArray(aadharFrontPageBytes);
                postPictureTask.setFilename(fileName);
                postPictureTask.setHelperObject(kycEntryId + "_type_2");
                Scheduler.getInstance().submit(postPictureTask);
            }
        } else if (reqId == Request.KYC_POST_PIC) {
            boolean picPostedResult = (Boolean) response;
            if (!picPostedResult) {
                displayInfo("Error. Please retry", null);
                return;
            }
            String fileName = (String) userObject;
            int pos = fileName.lastIndexOf("_");
            String startTypeStr = fileName.substring(0, pos + 1);
            String typeStr = fileName.substring(pos + 1);
            int typeInt = Integer.parseInt(typeStr);
            String nextTypeStr = startTypeStr + (typeInt + 1);

            if (typeInt == 4) {
                displayInfo("KYC Images are submitted successfully", this);
                return;
            }

            byte[] imgBytes = aadharBackPageBytes;
            if (typeInt == 3) {
                imgBytes = panPageBytes;
            }

            PostPictureTask postPictureTask = Request.getKYCPostPictureTask();
            postPictureTask.setCallbackResponse(this);
            postPictureTask.setByteArray(imgBytes);
            postPictureTask.setFilename(fileName);
            postPictureTask.setHelperObject(nextTypeStr);
            Scheduler.getInstance().submit(postPictureTask);
        }
    }

    private ImageView getImageView(int type) {
        View view = getView();
        if (view == null) {
            return null;
        }
        ImageView imageView = null;
        if (type == 1) {
            imageView = view.findViewById(R.id.frontPage);
        } else if (type == 2) {
            imageView = view.findViewById(R.id.backPage);
        } else if (type == 3) {
            imageView = view.findViewById(R.id.panPage);
        }
        return imageView;
    }

    public void doAction(int calledId, Object userObject) {
        Activity mainActivity = getActivity();
        if (mainActivity instanceof MainActivity) {
            ((MainActivity)mainActivity).launchView(Navigator.CURRENT_GAMES, null, false);
        }
    }

    private void setView(KYCEntry kycEntry) {
        int creditColor = Color.parseColor("#FF138C18");
        int withdrawColor = Color.parseColor("#FF0000");

        View view = getView();
        if (view == null) {
            return;
        }
        String statusStr = kycEntry.getStatus();
        if (statusStr == null) {
            statusStr = "not submitted";
        }
        boolean editable = !statusStr.equalsIgnoreCase("approved");

        TextView statusTV = view.findViewById(R.id.statusId);
        statusTV.setText(statusStr);
        if (editable) {
            statusTV.setTextColor(withdrawColor);
        } else {
            statusTV.setTextColor(creditColor);
        }

        Button button = view.findViewById(R.id.addFront);
        button.setEnabled(editable);

        button = view.findViewById(R.id.removeFront);
        button.setEnabled(editable);

        button = view.findViewById(R.id.addBack);
        button.setEnabled(editable);

        button = view.findViewById(R.id.removeBack);
        button.setEnabled(editable);

        button = view.findViewById(R.id.addPan);
        button.setEnabled(editable);

        button = view.findViewById(R.id.removePan);
        button.setEnabled(editable);

        button = view.findViewById(R.id.kycCreateBut);
        button.setEnabled(editable);

        button = view.findViewById(R.id.viewFront);
        button.setEnabled(kycEntry.getAfpId() != -1);
        button.setTag(kycEntry.getAfpId());

        button = view.findViewById(R.id.viewBack);
        button.setEnabled(kycEntry.getAbpId() != -1);
        button.setTag(kycEntry.getAbpId());

        button = view.findViewById(R.id.viewPan);
        button.setEnabled(kycEntry.getPpId() != -1);
        button.setTag(kycEntry.getPpId());
    }
}
