package com.example.cloudapp;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.services.drive.DriveScopes;


import java.util.Collections;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // набор данных, которые свяжем со списком
    ListView notesList;

    ArrayList<Note> notes;
    NoteAdapter noteAdapter;
    int selected = -1;
    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_OPEN_DOCUMENT = 2;

    private DriveServiceHelper mDriveServiceHelper;
    private String mOpenFileId;

    private EditText mFileTitleEditText;
    private EditText mDocContentEditText;

    int counter = 0;
    boolean googleSignIn = false;
    private boolean condition = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.GET_ACCOUNTS},
                1);


        notes = loadData();
        if (notes == null) {
            notes = new ArrayList<>();
            setInitialData();
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
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);

                intent.putExtra("saveFile", ((Note)notesList.getItemAtPosition(position)).getName());
                startActivityForResult(intent, 1);

                // получаем выбранный пункт

            }
        };
        notesList.setOnItemClickListener(itemListener);


        registerForContextMenu(notesList);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestSignIn();
            }
        });

        ShortcutManagerCompat.removeAllDynamicShortcuts(getApplication());
        for (Note note: notes) addShareShortcuts(this, note.getName());


        }

     public void addShareShortcuts (Context context, String id) {

         ShortcutInfoCompat shortcutInfoList = new ShortcutInfoCompat.Builder(context, id)
                 .setShortLabel(id)
                 .setIcon(IconCompat.createWithResource(context, R.drawable.gdrive))
                 .setIntent(new Intent(Intent.ACTION_SEND_MULTIPLE))
                 .setLongLived()
                 .setCategories(Collections.singleton("DEFAULT"))
                 .build();

         ShortcutManagerCompat.addDynamicShortcuts(context, Collections.singletonList(shortcutInfoList));
    }

    public void requestSignIn() {
        Log.d(TAG, "Requesting sign-in");
        googleSignIn = true;
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
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
        inflater.inflate(R.menu.action_bar, menu);

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


    void pickFiles(){
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        // Launching the Intent
        startActivity(intent.createChooser(intent, "Choose files"));

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
            case R.id.combine:
                Intent intent1 = new Intent(getApplicationContext(), MainActivity2.class);
                intent1.putExtra("saveFiles", 2);
                startActivityForResult(intent1, 1);
                return true;
            case R.id.logout:
                Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent2);
                return true;
            case R.id.action_filter:
                showPopup(findViewById(R.id.action_filter));
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;

        Note extraNote = null;
        if (googleSignIn) {
                extraNote = new Note("2another.one.bach@gmail.com", "Google Drive", Importance.Low, new Date(), 9188);

            googleSignIn = false;
        }
        else { extraNote = (Note) data.getSerializableExtra("Note"); }


        if (selected == -1) {
            notes.add(extraNote);
        } else {
            notes.set(selected, extraNote);
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
                Intent intent = new Intent("sample.intent.action.Launch");
                if(condition) {
                    intent.putExtra("drive", "2another.one.bach@gmail.com");
                }else if(!condition) {
                    intent.putExtra("drive", "another.one.bach@gmail.com");
                }condition = true;
                startActivity(intent);
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

    private void setInitialData() {

        notes.add(new Note("2another.one.bach@gmail.com", "drive.for.deeplom@gmail.com", Importance.Low, new Date(123254632L), 9188));
        notes.add(new Note("another.one.bach@gmail.com", "drive.for.deeplom@gmail.com", Importance.Low, new Date(123254632L), 9188));

    }



    private void saveData() {
        FileOutputStream fos = null;
        try {
            fos = getApplicationContext().openFileOutput("notes", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(notes);
            os.close();
            fos.close();
            Toast.makeText(getApplicationContext(), "data saved", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "data save fail", Toast.LENGTH_SHORT).show();
            ;
        }

    }

    private ArrayList<Note> loadData() {

        try {
            FileInputStream fis = getApplicationContext().openFileInput("notes");
            ObjectInputStream is = new ObjectInputStream(fis);
            ArrayList<Note> notes = (ArrayList<Note>) is.readObject();
            is.close();
            fis.close();
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


