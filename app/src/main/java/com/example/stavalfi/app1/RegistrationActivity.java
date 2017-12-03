package com.example.stavalfi.app1;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class RegistrationActivity extends AppCompatActivity {

    Bitmap image = null;

    public static final int PICK_IMAGE = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 2;

    @Override
    public void onResume() {
        super.onResume();
        if (UserManager.getInstance().getLoggedInUser() != null) {
            startActivity(new Intent(this, MenuActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        final EditText username = (EditText) findViewById(R.id.enter_username_editText);
        final EditText password = (EditText) findViewById(R.id.enter_password_editText);
        final EditText emailAddress = (EditText) findViewById(R.id.enter_email_editText);
        final EditText firstName = (EditText) findViewById(R.id.enter_first_name_editText);
        final EditText lastName = (EditText) findViewById(R.id.enter_last_name_editText);
        final EditText cityName = (EditText) findViewById(R.id.enter_city_name_editText);
        final EditText streetAddress = (EditText) findViewById(R.id.enter_street_address_editText);

        final TextView showErrorInRegistration = (TextView) findViewById(R.id.show_error_in_registration);


        Button registerButton = (Button) findViewById(R.id.register_button);
        final RegistrationActivity me = this;
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User(
                        username.getText().toString(),
                        password.getText().toString(),
                        emailAddress.getText().toString(),
                        firstName.getText().toString(),
                        lastName.getText().toString(),
                        cityName.getText().toString(),
                        streetAddress.getText().toString(),
                        image);

                if (username.getText().toString().length() < 1 || password.getText().toString().length() < 1 ||
                        image == null) {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
                    return;
                }

                User newUser = UserManager.getInstance().register(user);

                if (!newUser.isFakeUser()) {
                    // use successfully registered with good Id.
                    startActivity(new Intent(me, MenuActivity.class));
                    return;
                }
                // user name exist
                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.username_exist), Toast.LENGTH_SHORT).show();
            }
        });

        Button pickImage = (Button) findViewById(R.id.get_image);
        pickImage.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        Button takePictureButton = (Button) findViewById(R.id.take_a_picture_button);
        takePictureButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final TextView showErrorInRegistration = (TextView) findViewById(R.id.show_error_in_registration);

        if (resultCode != RESULT_OK) {
            // something went deeply wrong.
            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
        }

        switch (requestCode) {
            case PICK_IMAGE: {
                Uri chosenImageUri = data.getData();

                // check image file extension

                String extension = getContentResolver().getType(chosenImageUri);
                extension = extension.substring(extension.lastIndexOf("/") + 1); // Without dot jpg, png

                if (extension.equals("jpg") || extension.equals("png") || extension.equals("jpeg")) {
                    // print: image added successfully
                    Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.image_added_successfully), Toast.LENGTH_SHORT).show();
                } else {
                    // print: we do not accept any other extensions
                    Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.wrong_extension) + ": " + extension, Toast.LENGTH_SHORT).show();
                    return;
                }

                // save selected image file

                try {
                    this.image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), chosenImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case REQUEST_IMAGE_CAPTURE: {
                Bundle extras = data.getExtras();
                this.image = (Bitmap) extras.get("data");
                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.image_added_successfully), Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

}
