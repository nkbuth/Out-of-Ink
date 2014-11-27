package com.nathanbuth.outofink;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class DialogActivity extends Activity {

    private DBAdapter DBHelper;
    private Long rId;
    boolean newNote, wasCanceled;//newNote is used to determine buttons and wasCancelled is to keep the cancel button from saving data to the database
    EditText titleText, detailText;
    Button saveButton, cancelButton, updateButton, deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        //Prevents activity from being closed by touching outside of it thus requiring a button to be pressed
        this.setFinishOnTouchOutside(false);

        DBHelper = new DBAdapter(this);
        DBHelper.open();

        rId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(DBAdapter.KEY_ID);

        //Get id if one does
        if (rId == null) {
            Bundle extras = getIntent().getExtras();
            rId = extras != null ? extras.getLong(DBAdapter.KEY_ID)
                    : null;
        }

        //Get the value of newNote so theat the dialog ui can be determined
        Intent intent = getIntent();
        if (null != intent) {
            newNote = intent.getBooleanExtra("New Note", false);
        }

        wasCanceled = false;

        titleText = (EditText) findViewById(R.id.title);
        detailText = (EditText) findViewById(R.id.detail);

        saveButton = (Button) findViewById(R.id.saveButton);
        updateButton = (Button) findViewById(R.id.updateButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);

        //If a new note is being created then set the visibility of the update and delete buttons to gone and display the save button
        if(newNote){
            deleteButton.setVisibility(View.GONE);
            updateButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
        } else {//If a note is being updated then display the update and delete buttons and hide the save button
            deleteButton.setVisibility(View.VISIBLE);
            updateButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.GONE);
        }

        //Call populate to fill the text inputs with their original contents if needed
        populate();

        //Set onclick listeners for the save, update, delete, and cancel buttons
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Check if text is entered into both inputs. If it is then go to the next step of saving the note, otherwise post a toast requesting them to be filled
                if (titleText.getText().toString().length() != 0 && detailText.getText().toString().length() != 0){
                    setResult(RESULT_OK);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Please fill out both sections in order to save your note.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Check if text is entered into both inputs. If it is then go to the next step of saving the note, otherwise post a toast requesting them to be filled
                if (titleText.getText().toString().length() != 0 && detailText.getText().toString().length() != 0){
                    setResult(RESULT_OK);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Please fill out both sections in order to save your note.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Sets boolean to true so that nothing can be accidentally saved when cancel is pressed
                wasCanceled = true;
                finish();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Calls the delete method for the database and passes it the id of the note that is to be removed
                DBHelper.deleteNote(rId);
                finish();
            }
        });
    }

    //Populate text fields with preexisting data if needed.
    private void populate() {
        if (rId >= 1) {
            //Retrieves a note's data by using its id
            Cursor note = DBHelper.getNote(rId);
            startManagingCursor(note);
            //Set the edit texts to contain the text for the title and detail of the note
            titleText.setText(note.getString(
                    note.getColumnIndexOrThrow(DBAdapter.KEY_TITLE)));
            detailText.setText(note.getString(
                    note.getColumnIndexOrThrow(DBAdapter.KEY_DETAIL)));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(DBAdapter.KEY_ID, rId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populate();
    }

    private void saveState() {
        //Sets the contents of the edit texts to strings that can be saved
        //Checks that both string are longer than 0 to make sure that the save function does not fire unless save/update is pressed
        //Checks for if wasCanceled true so that the cancel button does not accidentally save an item
        String title = titleText.getText().toString();
        String detail = detailText.getText().toString();
        if(title.length() != 0 && detail.length() != 0 && !wasCanceled){
            Log.i("NKB", "Save happened");
            //Using the id value a note is determined as either being new and saved or being update for a preexisting note in the database
            //Note is then saved ot the database and the list is updated to contain the new note or show the changes made to a note
            if (rId == 0) {
                long id = DBHelper.createNote(title, detail);
                if (id > 0) {
                    rId = id;
                }
            } else {
                DBHelper.updateNote(rId, title, detail);
            }
        }else{
            wasCanceled = false;
            Log.i("NKB", "Save didn't happen");
        }

    }

}
