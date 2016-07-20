package edu.dartmouth.cs.reshmi.myruns1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * ProfileActivity shows up the users profile information. It saves the users details like-
 * Name, Email, Phone, Class, Major, Gender and the Photo. The photo is saved in the internal
 * storage whereas all the other details are stored in shared preferences.
 *
 * @author Reshmi Suresh
 */

public class ProfileActivity extends Activity
{
    //Declaration of public variables.
    EditText mETname, mETemail, mETphone, mETclass, mETmajor;
    Button mBSave, mBChange, mBCancel;
    RadioGroup mRGenderGroup;
    ImageView mIVimage;

    String name, email, phone, clas, major;
    Integer gender;
    boolean isTakenFromCamera;
    Uri mImageCapturedUri, mCroppedImageUri;

    //Declaration of public static keys, that will be used as identifiers for storing in
    //the shared preference and the internal storage.
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String NAME = "NameKey";
    public static final String EMAIL = "EmailKey";
    public static final String PHONE = "PhoneKey";
    public static final String CLASS = "ClassKey";
    public static final String MAJOR = "MajorKey";
    public static final String GENDER = "GenderKey";
    private static final String URI_CROPPED_KEY = "cropped_image";

    //Declaration of request codes for using in startActivityForResult and onActivityResult()
    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 100;
    public static final int SELECT_FILE = 10;

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //Setting up of the genreal UI Framework for the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Check if there has been an orientation change. If so, then get the temporary image uri.
        if(savedInstanceState != null)
        {
            mCroppedImageUri = savedInstanceState.getParcelable(URI_CROPPED_KEY);
        }

        mETname = (EditText) findViewById(R.id.editText);
        mETemail = (EditText) findViewById(R.id.editText2);
        mETphone = (EditText) findViewById(R.id.editText3);
        mETclass = (EditText) findViewById(R.id.editText4);
        mETmajor = (EditText) findViewById(R.id.editText5);
        mRGenderGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mIVimage = (ImageView) findViewById(R.id.imageView);
        mBSave = (Button) findViewById(R.id.button);
        mBChange = (Button) findViewById(R.id.button3);
        mBCancel = (Button) findViewById(R.id.button2);

        //Method to load the image- Either from the saved image, or the temporarily clicked image
        //or the default resource image.
        loadSnap();

        //Profile picture change button handler.
        mBChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"Open Camera", "Select from Gallery"};
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Pick Profile Picture");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Open Camera")) {
                            //Call method to open camera
                            onImageChange();
                        } else {
                            //Call method to open gallery
                            onImageChangeFromGallery();
                        }
                    }
                });
                builder.show();
            }
        });

        //Save button handler.
        mBSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call method to save the profile information in the shared preferences
                saveProfile();

                //Call method to save the profile picture into the internal storage.
                saveSnap();

                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        //Call method to load the profile information from the shared preference.
        loadProfile();

        //Cancel button handler.
        mBCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Method to save the text related entries in the shared preference.
     *
     * @author Reshmi Suresh
     */
    private void saveProfile()
    {
        //Get the data from the EditTexts
        name = mETname.getText().toString();
        email = mETemail.getText().toString();
        phone = mETphone.getText().toString();
        clas = mETclass.getText().toString();
        major = mETmajor.getText().toString();

        // get selected radio button from radioGroup
        gender = mRGenderGroup.getCheckedRadioButtonId();

        //Initialize the sharedPreferences for the MyPreferences variable.
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        //Put all the values into the shared preference.
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(PHONE, phone);
        editor.putString(CLASS, clas);
        editor.putString(MAJOR, major);
        editor.putInt(GENDER, gender);

        //Commit the values.
        editor.commit();
    }

    /**
     * Method to load the text related entries in the shared preference.
     *
     * @author Reshmi Suresh
     */
    private void loadProfile()
    {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences != null)
        {
            //Get the values from the shared preference. If the value doesn't exist then get NULL.
            name = sharedpreferences.getString(NAME, null);
            email = sharedpreferences.getString(EMAIL, null);
            phone = sharedpreferences.getString(PHONE, null);
            clas = sharedpreferences.getString(CLASS, null);
            major = sharedpreferences.getString(MAJOR, null);
            gender = sharedpreferences.getInt(GENDER, R.id.radioGroup);

            //Set the values in the EditTexts and the RadioGroup to the values retrieved.
            mETname.setText(name);
            mETemail.setText(email);
            mETphone.setText(phone);
            mETclass.setText(clas);
            mETmajor.setText(major);
            mRGenderGroup.check(gender);
        }
    }

    /**
     * Method called when the user selects to click image from the camera.
     * This function opens the camera and allows the user click and image.
     *
     * @author Reshmi Suresh
     */
    private void onImageChange()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Create a remporary path name in which the clicked image will be saved and returned.
        mImageCapturedUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCapturedUri);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_CAMERA);

        isTakenFromCamera = true;
    }

    /**
     * Method called when the user selects the option to choose the image from gallery.
     * This method would open the gallery, letting the user to choose an image.
     *
     * @author Reshmi Suresh
     */
    private void onImageChangeFromGallery()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //Set the type for intent, to look for images.
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //Called when the Camera, the Crop intent or the Gallery return some data.
        //We check for the type of data return and handle the cases accordingly.

        //Check if the result is OK, i.e. the user doesnt click on the cancel button in the
        //camera activity or the crop activity.
        if(resultCode==RESULT_OK)
        {
            switch (requestCode)
            {
                case REQUEST_CODE_TAKE_FROM_CAMERA:
                    //If result is from image clicked from camera, then start the crop intent.
                    beginCrop(mImageCapturedUri);
                    break;

                case SELECT_FILE:
                    //If result is from image selected from gallery, then start the crop intent.
                    Uri selected = data.getData();
                    beginCrop(selected);
                    break;

                case Crop.REQUEST_CROP:
                    //If the result is from the crop intent, then call the method handle crop
                    //to set the crop image in the imageviewer.
                    handleCrop(resultCode, data);

                    //Delete the temporary file which was created when we clicked the image from
                    //the camera.
                    if (isTakenFromCamera)
                    {
                        File f = new File(mImageCapturedUri.getPath());
                        if (f.exists())
                            f.delete();
                    }
                    break;
            }
        }
    }

    /**
     * This method starts the custom crop activity on the image URI passed wither from the camera
     * activity or the select from gallery activity.
     *
     * Adapted from the Lecture Notes
     *
     * @param source
     */
    private void beginCrop(Uri source)
    {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    /**
     * This method handles the crop image being returned from the custom crop activity. It uses
     * the returned image and sets it into the imageView for display.
     *
     * Adapted from the lecture notes.
     *
     * @param resultCode
     * @param result
     */
    private void handleCrop(int resultCode, Intent result)
    {
        if(resultCode==RESULT_OK)
        {
            //save the cropped image result URI in a temporary URI.
            mCroppedImageUri = Crop.getOutput(result);

            //Clear the imageViewer cache, such that everytime a new image is clicked, it can
            //load the new image instead of waiting for the imageviewer to be initialised again.
            mIVimage.setImageResource(0);

            //Set the image URI as the cropped image uri.
            mIVimage.setImageURI(mCroppedImageUri);
        }
    }

    /**
     * Method to save the image to the internal storage, when the save button is clicked.
     *
     * @author Reshmi Suresh
     */
    private void saveSnap()
    {
        mIVimage.buildDrawingCache();
        //Get bitmap image for the currently displayed image in the image viewer.
        Bitmap bmap = mIVimage.getDrawingCache();
        try
        {
            //Open file output stream for a fixed path and write the data.
            FileOutputStream fos = openFileOutput("profile_photo.png", MODE_PRIVATE);
            bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    /**
     * Method to load the image when the app is opened or orientation changed.
     *
     * @author Reshmi Suresh
     */
    private void loadSnap()
    {
        try
        {
            //Check if temporary cropped image URI exist, this would be the case if there was an
            //orientation change.
            if(mCroppedImageUri != null)
                mIVimage.setImageURI(mCroppedImageUri);
            else
            {
                //If there is no orientation change, just try and load the last saved image from
                //internal storage.
                FileInputStream fis = openFileInput("profile_photo.png");
                Bitmap bmap = BitmapFactory.decodeStream(fis);
                mIVimage.setImageBitmap(bmap);
                fis.close();
            }
        }
        catch (IOException ioe)
        {
            //If no image is available in the internal storage, just load the default photo
            //from the resources.
            mIVimage.setImageResource(R.drawable.default_profile);
        }
    }

    protected void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        //Save the temporary cropped image uri, so that if there is an orientation change,
        //the app would be able to maintain the currently displayed image.
        savedInstanceState.putParcelable(URI_CROPPED_KEY, mCroppedImageUri);
    }
}
