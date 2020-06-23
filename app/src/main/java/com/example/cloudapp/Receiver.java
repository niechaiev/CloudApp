package com.example.cloudapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.pm.ShortcutManagerCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;

public class Receiver extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Get intent, action and MIME type
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {

                handleSendImage(intent); // Handle single image being sent

        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {

                handleSendMultipleImages(intent); // Handle multiple images being sent

            // Handle other intents, such as being started from the home screen
        }
    }



    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            Intent intent1 = new Intent(getApplicationContext(), MainActivity2.class);
            intent1.putExtra("saveFile", intent.getStringExtra(ShortcutManagerCompat.EXTRA_SHORTCUT_ID));
            intent1.putExtra("uri", intent.getParcelableExtra(Intent.EXTRA_STREAM).toString());
            startActivity(intent1);
            finish();
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
        }
    }


}