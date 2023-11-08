package com.example.noteapplab2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton add_button;
    DatabaseHelper myDB;
    ArrayList<String> note_id, note_title, note_description, note_color, note_image;
    CustomAdapter customAdapter;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));
        recyclerView = findViewById(R.id.noteList);
        add_button = findViewById(R.id.addNoteButton);
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterNotes(newText);
                return true;
            }
        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivity(intent);
            }
        });

        myDB = new DatabaseHelper(MainActivity.this);
        note_id = new ArrayList<>();
        note_title = new ArrayList<>();
        note_description = new ArrayList<>();
        note_color = new ArrayList<>();
        note_image = new ArrayList<>();


        storeNotesinArray();
        customAdapter = new CustomAdapter(MainActivity.this, note_id, note_title, note_description, note_color, note_image);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    private void filterNotes(String text) {
        customAdapter.filterNotes(note_id,note_title,note_description,note_color);

        ArrayList filteredNoteId = new ArrayList<>();
        ArrayList filteredNoteTitle = new ArrayList<>();
        ArrayList filteredNoteDescription = new ArrayList<>();
        ArrayList filteredNoteColor = new ArrayList<>();
        for (int i = 0; i < note_title.size(); i++) {
            //if statement to check if the words entered in the search view match the note title/description
            if (note_title.get(i).toLowerCase().contains(text.toLowerCase()) || note_description.get(i).toLowerCase().contains(text.toLowerCase())) {
                filteredNoteId.add(note_id.get(i));
                filteredNoteTitle.add(note_title.get(i));
                filteredNoteDescription.add(note_description.get(i));
                filteredNoteColor.add(note_color.get(i));
            }
        }

        customAdapter.filterNotes(filteredNoteId, filteredNoteTitle, filteredNoteDescription, filteredNoteColor); // Update the adapter with the filtered data

    }

    void storeNotesinArray(){
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0){
            Toast.makeText(this,"No notes added.", Toast.LENGTH_SHORT).show();
        } else{
            while(cursor.moveToNext()){
                note_id.add(cursor.getString(0));
                note_title.add(cursor.getString(1));
                note_description.add(cursor.getString(2));
                note_color.add(cursor.getString(3));
                note_image.add(cursor.getString(4));
            }
        }
    }
}
