package com.blooddonation.blooddonation;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.blooddonation.blooddonation.Common.Common;
import com.blooddonation.blooddonation.Model.Banner;
import com.blooddonation.blooddonation.Model.MyResponse;
import com.blooddonation.blooddonation.Model.Notification;
import com.blooddonation.blooddonation.Model.Sender;
import com.blooddonation.blooddonation.Model.Token;
import com.blooddonation.blooddonation.Remote.APIService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class bannerRequest extends Fragment {

    MaterialEditText edtPhone, edtName, edtLastDate, edtBannerName, edtBannerDetail;
    FButton upload, request;
    ImageView Bannerimg;
    Uri filePath;
    private DatePickerDialog.OnDateSetListener mage;
    Banner banner;
    APIService mService;
    ProgressDialog mDialog;
    String days, LastDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.request_banner, container, false);

        banner = new Banner();
        mService = Common.getFCMClient();
        edtPhone = v.findViewById(R.id.edtPhone);
        edtName = v.findViewById(R.id.edtName);
        edtLastDate = v.findViewById(R.id.removeBanner);
        edtBannerName = v.findViewById(R.id.edtBannerName);
        edtBannerDetail = v.findViewById(R.id.edtBannerDetail);

        Bannerimg = v.findViewById(R.id.loadImage);

        upload = v.findViewById(R.id.btnUpload);
        request = v.findViewById(R.id.btnRequest);

        edtPhone.setText(Common.currentUser.getPhone());
        edtName.setText(Common.currentUser.getName());

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        edtLastDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Calendar ca = Calendar.getInstance();
                    int ye = ca.get(Calendar.YEAR);
                    int mo = ca.get(Calendar.MONTH);
                    int da = ca.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(getContext(),
                            android.R.style.Theme_Holo_Dialog_MinWidth,
                            mage,
                            ye, mo, da);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            }
        });

        edtLastDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar ca = Calendar.getInstance();
                int ye = ca.get(Calendar.YEAR);
                int mo = ca.get(Calendar.MONTH);
                int da = ca.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getContext(),
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mage,
                        ye, mo, da);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mage = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int y, int m, int d) {
                m = m + 1;
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, y);
                c.set(Calendar.MONTH, m);
                c.set(Calendar.DAY_OF_MONTH, d);
                days = String.valueOf(Common.calDate(c.getTimeInMillis()));
                LastDate = d + "/" + m + "/" + y;
                edtLastDate.setText(LastDate);
            }
        };

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog = new ProgressDialog(getContext());
                mDialog.setMessage("Please waiting....");
                mDialog.setCancelable(false);
                mDialog.show();

                if (checkData()) {
                    banner.setUserPhone(Common.currentUser.getPhone());
                    banner.setBannerName(edtBannerName.getText().toString());
                    banner.setBannerDetail(edtBannerDetail.getText().toString());
                    banner.setStatus("0");
                    banner.setBannerLastDate(edtLastDate.getText().toString());
                    FirebaseDatabase.getInstance().getReference("Banner").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String requested = String.valueOf(System.currentTimeMillis());
                            FirebaseDatabase.getInstance().getReference("Banner").child(requested).setValue(banner);
                            Common.IMGURL = null;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    sendNotification();
                    mDialog.dismiss();
                    startActivity(new Intent(getContext(), Home.class));
                } else {
                    mDialog.dismiss();
                }
            }
        });

        return v;
    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        if (filePath != null) {
            final ProgressDialog mDialog = new ProgressDialog(getContext());
            mDialog.setMessage("Uploading....");
            mDialog.setCancelable(false);
            mDialog.show();
            String requested = String.valueOf(System.currentTimeMillis());
            final StorageReference imageFolder = FirebaseStorage.getInstance().getReference().child("banner/" + Common.currentUser.getPhone() + "_" + requested);
            imageFolder.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    upload.setText("Uploaded !");
                    Toast.makeText(getContext(), "Uploaded !!!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //set vale for newCategory if image upload and we can get download link
                            banner.setBannerImage(uri.toString());
                            Common.IMGURL = banner.getBannerImage();
                            Picasso.with(getContext()).load(banner.getBannerImage()).into(Bannerimg);

                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            int progress = (int) (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded (" + progress + "%)");
                        }
                    });
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            uploadImage();
        }
    }

    private boolean checkData() {
        int phone = 1, name = 1, bannerName = 1, bannerLastDate = 1, BannerDetail = 1, bannerImage = 1;
        if (edtPhone.getText().toString().isEmpty() || edtPhone.getText().toString().length() != 10) {
            edtPhone.setError("Enter Phone Number");
            phone = 0;
        }
        if (edtName.getText().toString().isEmpty()) {
            edtName.setError("Enter Name");
            name = 0;
        }
        if (edtLastDate.getText().toString().isEmpty()) {
            edtLastDate.setError("Enter Last Date");
            bannerLastDate = 0;
        }
        if (Integer.parseInt(days) >= 16 || Integer.parseInt(days) <= 0) {
            edtLastDate.setError("Last Date Should Be Between 1-15 Days");
            bannerLastDate = 0;
        }

        if (edtBannerName.getText().toString().isEmpty()) {
            edtBannerName.setError("Enter Banner Name");
            bannerName = 0;
        }
        if (edtBannerDetail.getText().toString().isEmpty()) {
            edtBannerDetail.setError("Enter Banner Details");
            BannerDetail = 0;
        }
        if (filePath == null) {
            Toast.makeText(getContext(), "Select Image", Toast.LENGTH_SHORT).show();
            bannerImage = 0;
        }
        if (phone == 0 || name == 0 || bannerLastDate == 0 || bannerName == 0 || BannerDetail == 0 || bannerImage == 0)
            return false;
        else
            return true;
    }

    private void sendNotification() {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        tokens.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    Token token = post.getValue(Token.class);
                    if (token.isAdmin()) {
                        Notification notification = new Notification("Message", "Request For Banner");
                        Sender content = new Sender(token.getToken(), notification);
                        mService.sendNotification(content)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                        if (response.body().success == 1) {
                                            Toast.makeText(getContext(), "Request Sent!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), "Failed To Send Notification !", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {
                                        Log.e("ERROR", t.getMessage());
                                    }
                                });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
