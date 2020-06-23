package com.example.cloudapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.services.drive.DriveScopes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    // набор данных, которые свяжем со списком
    ListView notesList;

    ArrayList<Note> notes;
    NoteAdapter noteAdapter;
    int selected = -1;
    private static final String TAG = "MainActivity";

    private String mOpenFileId;

    private EditText mFileTitleEditText;
    private EditText mDocContentEditText;
    String saveFile;
    boolean combine = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("saveFiles")) combine = true;
            else
            saveFile = extras.getString("saveFile");
        }


        ActivityCompat.requestPermissions(MainActivity2.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.GET_ACCOUNTS},
                1);


        notes = loadData();
        if (notes == null) {
            notes = new ArrayList<>();
            setInitialData();
            saveData();
        }
        if (extras!=null && extras.containsKey("uri")){
            Uri uri = Uri.parse(extras.getString("uri"));
            notes.add(new Note(getFileName(uri), null, Importance.Low,new Date(), uri, getMimeType(uri)));
            saveData();
        }
        // получаем элемент ListView
        notesList = findViewById(R.id.notesList);
        // создаем адаптер
        noteAdapter = new NoteAdapter(this, R.layout.list_item, notes);
        // устанавливаем адаптер
        notesList.setAdapter(noteAdapter);
        // слушатель выбора в списке
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                selected = noteAdapter.getConstNotePosition(position);
                Note selectedState = (Note) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
                intent.putExtra("Note", selectedState);
                startActivityForResult(intent, 1);

                // получаем выбранный пункт

            }
        };
        notesList.setOnItemClickListener(itemListener);


        registerForContextMenu(notesList);

        }


    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getApplicationContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.context_importance, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.high:
                        noteAdapter.getIFilter().filter("High");
                        return true;
                    case R.id.medium:
                        noteAdapter.getIFilter().filter("Medium");
                        return true;
                    case R.id.low:
                        noteAdapter.getIFilter().filter("Low");
                        return true;
                    case R.id.notset:
                        noteAdapter.getFilter().filter("");
                        return true;
                    default:
                        return false;
                }
            }
        });


        popup.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar2, menu);

        MenuItem item = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                noteAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
//                Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
//                selected = -1;
//                startActivityForResult(intent, 1);

                pickFiles();
                return true;
            case R.id.action_filter:
                showPopup(findViewById(R.id.action_filter));
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    void pickFiles(){
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        // Launching the Intent
        startActivityForResult(intent.createChooser(intent, "Choose files"),2);
    }


    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        if(data.getData() != null){
            Uri uri = data.getData();
            notes.add(new Note(getFileName(uri), null, Importance.Low,new Date(), uri, getMimeType(uri)));
        }
        else if (data.getClipData() != null){
            for(int i =0;i<data.getClipData().getItemCount();i++) {
            Uri uri = data.getClipData().getItemAt(i).getUri();
            notes.add(new Note(getFileName(uri), null, Importance.Low,new Date(), uri, getMimeType(uri)));
            }
        }
        else {
            //extraNote = (Note) data.getSerializableExtra("Note");
        }

        if (selected == -1) {
           // notes.add(extraNote);
        } else {
            //notes.set(selected, extraNote);
            noteAdapter.notifyListChanged();
        }
        noteAdapter.notifyDataSetChanged();
        saveData();

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        if (v.getId() == R.id.action_filter) {
            inflater.inflate(R.menu.context_importance, menu);
        } else {
            inflater.inflate(R.menu.context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.edit:
                return true;
            case R.id.delete:
                deleteNote(noteAdapter.getConstNotePosition((int) info.id));
                noteAdapter.notifyListChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deleteNote(long id) {
        notes.remove((int) id);
        noteAdapter.notifyDataSetChanged();
        saveData();
    }
    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getApplicationContext().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }
    private void setInitialData() {


//        notes.add(new Note("SSH.txt", "d", Importance.Low, new Date(123254632L), 9188));
//        notes.add(new Note("Notes.txt", "d", Importance.Low, new Date(123254632L), 9188));
//        notes.add(new Note("Cover.png", "d", Importance.Low, new Date(123254632L), 9188));
//        notes.add(new Note("archive-master.zip", "d", Importance.Low, new Date(123254632L), 9188));
//        notes.add(new Note("moon_circle (1).mp4", "d", Importance.Low, new Date(123254632L), 9188));
//        notes.add(new Note("instructions.txt", "d", Importance.Low, new Date(123254632L), 9188));

    }

    private void saveData() {
        FileOutputStream fos = null;
        try {
            fos = getApplicationContext().openFileOutput(saveFile, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(notes);
            os.close();
            fos.close();
            Toast.makeText(getApplicationContext(), "data saved", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "data save fail", Toast.LENGTH_SHORT).show();
            ;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "data save fail", Toast.LENGTH_SHORT).show();
            ;
        }

    }



    private ArrayList<Note> loadData() {

        try {
            ArrayList<Note> notes;
            if(combine){
                FileInputStream fis = getApplicationContext().openFileInput("notes1");
                ObjectInputStream is = new ObjectInputStream(fis);
                notes = (ArrayList<Note>) is.readObject();
                fis = getApplicationContext().openFileInput("notes2");
                is = new ObjectInputStream(fis);
                notes.addAll((ArrayList<Note>) is.readObject());

                is.close();
                fis.close();
            }
            else {
                FileInputStream fis = getApplicationContext().openFileInput(saveFile);
                ObjectInputStream is = new ObjectInputStream(fis);
                notes = (ArrayList<Note>) is.readObject();

                is.close();
                fis.close();
            }

            Toast.makeText(getApplicationContext(), "data loaded", Toast.LENGTH_SHORT).show();
            ;
            return notes;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "no data", Toast.LENGTH_SHORT).show();
            ;
            return null;
        }
    }

}


