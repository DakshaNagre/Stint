package com.oosd.project.taskmanagementapp.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.oosd.project.taskmanagementapp.Controllers.AccountSettingsController;
import com.oosd.project.taskmanagementapp.R;
import com.oosd.project.taskmanagementapp.util.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class AccountSettings extends Fragment {

    private EditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private TextInputLayout textInputLayoutName, textInputLayoutEmail, textInputLayoutPassword, textInputLayoutConfirmPassword;
    private ImageView buttonName, buttonEmail, buttonPassword, buttonConfirmPassword;
    private TextView name, nameTitle, email, emailTitle, password, passwordTitle, confirmPassword, confirmPasswordTitle;
    private MaterialButton update, cancel;
    private androidx.appcompat.widget.SwitchCompat emailSwitch, pushSwitch;
    private ImageView profilePhoto;
    private AlertDialog.Builder dialogToChooseProfilePhoto;
    private AlertDialog alertDialog;
    private LayoutInflater li;
    private View viewToChooseProfilePhoto;
    private static final int IMAGE_PICKER_SELECT = 1;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 1888;

    private String image;
    public ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.account_settings_layout, container, false);
        profilePhoto = root.findViewById(R.id.profilePhoto);
        pushSwitch = root.findViewById(R.id.pushToggle);
        emailSwitch = root.findViewById(R.id.emailToggle);
        progressDialog = new ProgressDialog(getContext());


        final AccountSettingsController accountSettingsController = new AccountSettingsController(getContext());
        List<Object> data = accountSettingsController.getAccountSettings();

        if(data.get(4)!=null && Boolean.valueOf(data.get(4).toString()))
        {
                Picasso.get()
                        .load(Constants.S3_IMAGE_BUCKET + "/" + data.get(5).toString())
                        .fit().centerCrop()
                        .into(profilePhoto);
        }
        if(data.get(2)!=null && Boolean.valueOf(data.get(2).toString()))
        {
            emailSwitch.setChecked(true);
        }
        if(data.get(3)!=null && Boolean.valueOf(data.get(3).toString()))
        {
            pushSwitch.setChecked(true);
        }


        editTextName = root.findViewById(R.id.editTextName);
        editTextEmail = root.findViewById(R.id.editTextEmail);
        editTextPassword = root.findViewById(R.id.editTextPassword);
        editTextConfirmPassword = root.findViewById(R.id.editTextConfirmPassword);

        textInputLayoutName = root.findViewById(R.id.editTextNameMaterial);
        textInputLayoutEmail = root.findViewById(R.id.editTextEmailMaterial);
        textInputLayoutPassword = root.findViewById(R.id.editTextPasswordMaterial);
        textInputLayoutConfirmPassword = root.findViewById(R.id.editTextConfirmPasswordMaterial);

        buttonName = root.findViewById(R.id.editNameButton);
        buttonEmail=root.findViewById(R.id.editEmailButton);
        buttonPassword = root.findViewById(R.id.editPasswordButton);
        buttonConfirmPassword = root.findViewById(R.id.editConfirmPasswordButton);


        name = root.findViewById(R.id.nameTextView);
        nameTitle = root.findViewById(R.id.profileName);
        nameTitle.setText(data.get(0).toString());

        email = root.findViewById(R.id.email);
        emailTitle = root.findViewById(R.id.profileEmail);
        emailTitle.setText(data.get(1).toString());

        password = root.findViewById(R.id.password);
        passwordTitle = root.findViewById(R.id.profilePassword);
        confirmPassword = root.findViewById(R.id.confirmPassword);
        confirmPasswordTitle = root.findViewById(R.id.confirmPasswordProfile);

        update = root.findViewById(R.id.updateButton);
        cancel = root.findViewById(R.id.cancelButton);





        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //alert dialog open to select camera or gallery
                li = LayoutInflater.from(getContext());
                viewToChooseProfilePhoto= li.inflate(R.layout.choose_profile_photo, null);
                dialogToChooseProfilePhoto = new AlertDialog.Builder(getContext(),R.style.MyDialogTheme);
                dialogToChooseProfilePhoto.setView(viewToChooseProfilePhoto);
                alertDialog = dialogToChooseProfilePhoto.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                MaterialButton chooseGallery = viewToChooseProfilePhoto.findViewById(R.id.chooseGallery);
                MaterialButton chooseCamera = viewToChooseProfilePhoto.findViewById(R.id.chooseCamera);

                alertDialog.show();

                chooseGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //call the add card api here
                        Intent i = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, IMAGE_PICKER_SELECT);
                    }
                });
                chooseCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                        }
                        else
                        {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        }
                    }
                });
                dialogToChooseProfilePhoto.setCancelable(true);

            }
        });

        buttonName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textInputLayoutName.setVisibility(View.VISIBLE);
                editTextName.setText(nameTitle.getText().toString());
                name.setVisibility(View.INVISIBLE);
                nameTitle.setVisibility(View.INVISIBLE);
                buttonName.setVisibility(View.INVISIBLE);
            }
        });

        buttonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textInputLayoutEmail.setVisibility(View.VISIBLE);
                editTextEmail.setText(emailTitle.getText().toString());
                email.setVisibility(View.INVISIBLE);
                emailTitle.setVisibility(View.INVISIBLE);
                buttonEmail.setVisibility(View.INVISIBLE);
            }
        });

        buttonPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textInputLayoutPassword.setVisibility(View.VISIBLE);
                password.setVisibility(View.INVISIBLE);
                passwordTitle.setVisibility(View.INVISIBLE);
                buttonPassword.setVisibility(View.INVISIBLE);
            }
        });

        buttonConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textInputLayoutConfirmPassword.setVisibility(View.VISIBLE);
                confirmPassword.setVisibility(View.INVISIBLE);
                confirmPasswordTitle.setVisibility(View.INVISIBLE);
                buttonConfirmPassword.setVisibility(View.INVISIBLE);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                org.json.JSONObject jsonBody = new org.json.JSONObject();
                progressDialog.setTitle("Loading");
                progressDialog.setMessage("Updating Profile Data..");
                progressDialog.show();
                try {
                    if(editTextName.getText().toString().trim() == null || editTextName.getText().toString().trim().equals("") || editTextName.getText().toString().length()==0)
                    {
                        jsonBody.put("name",nameTitle.getText().toString());
                    }
                    else
                    {
                        jsonBody.put("name",editTextName.getText().toString().trim());
                    }

                    if(editTextEmail.getText().toString().trim() == null || editTextEmail.getText().toString().trim() == "" || editTextEmail.getText().toString().length()==0)
                    {
                        jsonBody.put("email" , emailTitle.getText().toString().trim());
                    }
                    else
                    {
                        jsonBody.put("email",editTextEmail.getText().toString().trim());
                    }

                    jsonBody.put("emailNotification", emailSwitch.isChecked());
                    jsonBody.put("pushNotification", pushSwitch.isChecked());
                    jsonBody.put("image", image);
                    if(editTextPassword.getText().toString().length() != 0)
                    {
                        jsonBody.put("password", editTextPassword.getText().toString());
                        jsonBody.put("confirmPassword", editTextConfirmPassword.getText().toString());
                    }
                    else
                    {
                        jsonBody.put("password", "");
                        jsonBody.put("confirmPassword", "");
                    }
                }
                catch(Exception e){e.printStackTrace();}
                finally {
                    accountSettingsController.updateData(jsonBody,progressDialog);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textInputLayoutName.setVisibility(View.INVISIBLE);
                name.setVisibility(View.VISIBLE);
                nameTitle.setVisibility(View.VISIBLE);
                buttonName.setVisibility(View.VISIBLE);

                textInputLayoutEmail.setVisibility(View.INVISIBLE);
                email.setVisibility(View.VISIBLE);
                emailTitle.setVisibility(View.VISIBLE);
                buttonEmail.setVisibility(View.VISIBLE);

                textInputLayoutPassword.setVisibility(View.INVISIBLE);
                password.setVisibility(View.VISIBLE);
                passwordTitle.setVisibility(View.VISIBLE);
                buttonPassword.setVisibility(View.VISIBLE);

                textInputLayoutConfirmPassword.setVisibility(View.INVISIBLE);
                confirmPassword.setVisibility(View.VISIBLE);
                confirmPasswordTitle.setVisibility(View.VISIBLE);
                buttonConfirmPassword.setVisibility(View.VISIBLE);
            }
        });
        return root;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(getContext(), "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(getContext(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICKER_SELECT && resultCode == Activity.RESULT_OK)
        {
            try {
                Uri imageUri = data.getData();
                final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                convertImageToBase64(selectedImage);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            convertImageToBase64(photo);
        }
    }
    private void convertImageToBase64(Bitmap image){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        this.image = "data:image/png;base64," + encoded;
        this.profilePhoto.setImageBitmap(image);
        alertDialog.cancel();
    }
}