package com.example.cloudapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteActivity extends AppCompatActivity {

    EditText name;
    EditText description;
    Spinner importance;
    Note note;
    Button save;
    TextView datetime;
    ImageView imageView;
    int imageId;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        datetime = findViewById(R.id.date);
        imageView = findViewById(R.id.imageView);
        importance = findViewById(R.id.importance);
        save = findViewById(R.id.save);

        ArrayAdapter<Importance> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Importance.values());
        importance.setAdapter(adapter);


        if (getIntent().getSerializableExtra("Note") != null) {
            note = (Note) getIntent().getSerializableExtra("Note");
            imageId = note.getPicResource();
            name.setText(note.getName());
            description.setText(note.getDescription());
            importance.setSelection(note.getImportance().ordinal());
            imageView.setImageURI(Uri.parse("content://media/external/images/media/" + note.getPicResource()));
            datetime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(note.getDate()));
    }
        else{
            datetime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                Note extraNote = null;
                try {
                    extraNote = new Note(name.getText().toString(),description.getText().toString(), (Importance) importance.getSelectedItem(),
                            new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(datetime.getText().toString()), imageId);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                data.putExtra("Note", extraNote);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromGallery();
            }
        });
    }
    private void pickFromGallery(){
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,1);

    }
    public void onActivityResult(int requestCode,int resultCode,Intent data){

        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case 1:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    imageView.setImageURI(selectedImage);
                    String[] split = selectedImage.toString().split("/");
                    imageId = Integer.valueOf(split[split.length-1]);
                    break;
            }

    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("imageId", imageId);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageView.setImageURI(Uri.parse("content://media/external/images/media/" + savedInstanceState.getInt("imageId")));
        imageId = savedInstanceState.getInt("imageId");

    }
}
