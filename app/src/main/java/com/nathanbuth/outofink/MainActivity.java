package com.nathanbuth.outofink;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.melnykov.fab.FloatingActionButton;


public class MainActivity extends ListActivity {

    private static final int CREATE=0;
    private static final int EDIT=1;

    private DBAdapter DBHelper;

    FloatingActionButton addNote;//Floating action button as per material design guidelines

    boolean newNote;//Used to pass whether the user is editing a note or  making a new one so that the buttons can change

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBHelper = new DBAdapter(this);
        DBHelper.open();
        fillData();//Call fill data on create to fill listview with any existing notes
        registerForContextMenu(getListView());

        newNote = false;

        addNote = (FloatingActionButton) findViewById(R.id.addNote);
        //Onclick listener for FAB new note button
        addNote.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                createNote();
            }
        });
    }

    //Called when the FAB is clicked, first process of creating a new note
    private void createNote() {
        newNote = true;//Set boolean so that the dialog for a new note is displayed
        //Open dialog activity for creating a new note
        Intent i = new Intent(this, DialogActivity.class);
        i.putExtra("New Note", newNote);//Pass boolean value to dialog activity to be checked
        startActivityForResult(i, CREATE);//Start the dialog activity
    }

    //Onclick listener to allow updating and deletion of a note selected from the list
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //Open dialog activity for updating a current note
        Intent i = new Intent(this, DialogActivity.class);
        i.putExtra(DBAdapter.KEY_ID, id);//Pass id to dialog activity so that the note data can be loaded into the edit texts
        startActivityForResult(i, EDIT);//Start the dialog activity ready to update the note
    }

    //Get the note data from the database and put it into the textviews in the list
    private void fillData() {
        Cursor notesCursor = DBHelper.getAllNotes();//Get all of the notes that have been saved to the database and set them in a cursor
        startManagingCursor(notesCursor);
        //Get and set the data for the title and detail sections to their textviews in each list item
        String[] from = new String[]{DBAdapter.KEY_TITLE, DBAdapter.KEY_DETAIL};
        int[] to = new int[]{R.id.titleText, R.id.detailText};
        SimpleCursorAdapter notes = new SimpleCursorAdapter(this, R.layout.notes, notesCursor, from, to, 0);
        setListAdapter(notes);
    }

    //Call fill data whenever action is taken in the application such as creating a new note
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
}
