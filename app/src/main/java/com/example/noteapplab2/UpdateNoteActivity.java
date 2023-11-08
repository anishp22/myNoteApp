package com.example.noteapplab2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UpdateNoteActivity extends AppCompatActivity {

    EditText update_title, update_description;
    Button saveNoteButtonUpdate, redNoteUpdate, blueNoteUpdate, greenNoteUpdate, uploadBtnUpdate, captureBtnUpdate;
    String noteColorUpdate, id, title, description, color, imageURI, imageIntent;
    CardView noteBGUpdate;
    ImageView imgViewUpdate;
    DatabaseHelper myDBUpdate;
    Uri imageUriUpdate;
    private static final int SELECT_IMG = 100;
    private static final int CAPTURE_IMG = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_note);
        setSupportActionBar(findViewById(R.id.toolbar2Update));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myDBUpdate = new DatabaseHelper(UpdateNoteActivity.this);

        update_title = findViewById(R.id.noteTitleUpdate);
        update_description = findViewById(R.id.noteDescriptionUpdate);
        saveNoteButtonUpdate = findViewById(R.id.saveNoteButtonUpdate);
        redNoteUpdate = findViewById(R.id.redNoteUpdate);
        blueNoteUpdate = findViewById(R.id.blueNoteUpdate);
        greenNoteUpdate = findViewById(R.id.greenNoteUpdate);
        uploadBtnUpdate = findViewById(R.id.uploadImgBtnUpdate);
        captureBtnUpdate = findViewById(R.id.captureImgBtnUpdate);
        noteBGUpdate = findViewById(R.id.noteViewUpdate);
        imgViewUpdate = findViewById(R.id.noteImageUpdate);


        //when update note button clicked, send data to database
        saveNoteButtonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = update_title.getText().toString().trim();
                if(title.equals("")) {
                    Snackbar snackbar = Snackbar.make(v, "Title is empty", Snackbar.LENGTH_LONG);  //prints error message if title is empty
                    snackbar.show();
                } else {
                    noteColorUpdate = color;
                    myDBUpdate.updateData(id, update_title.getText().toString().trim(),
                            update_description.getText().toString().trim(),
                            noteColorUpdate.trim(), imageUriUpdate.toString());
                    Intent intent = new Intent(UpdateNoteActivity.this, MainActivity.class);
                    startActivity(intent);
                }

            }
        });

        //set noteColor string to hexcode for red when button is clicked
        redNoteUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteColorUpdate = "#E53935";
                noteBGUpdate.setCardBackgroundColor(Color.parseColor(noteColorUpdate));
            }
        });

        //set noteColor string to hexcode for blue when button is clicked
        blueNoteUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteColorUpdate = "#1E88E5";
                noteBGUpdate.setCardBackgroundColor(Color.parseColor(noteColorUpdate));
            }
        });

        //set noteColor string to hexcode for green when button is clicked
        greenNoteUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteColorUpdate = "#43A047";
                noteBGUpdate.setCardBackgroundColor(Color.parseColor(noteColorUpdate));

            }
        });

        //function to upload the image from gallery
        uploadBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,SELECT_IMG);
            }
        });

        captureBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, CAPTURE_IMG);
            }
        });

        getData();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){

            if(requestCode==SELECT_IMG){
                imageUriUpdate = data.getData();
                imgViewUpdate.setImageURI(imageUriUpdate);
            }
            else if(requestCode==CAPTURE_IMG){
                Bitmap img = (Bitmap)(data.getExtras().get("data"));
                imgViewUpdate.setImageBitmap(img);

                try {
                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                    String imageFileName = "JPEG_" + timestamp + ".jpg";
                    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    // create a new file for the image
                    File imageFile = new File(storageDir, imageFileName);

                    // compress the image and save it to the file
                    try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                        img.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    }

                    // notify the media scanner about the new image so it appears in the gallery
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(imageFile);
                    mediaScanIntent.setData(contentUri);
                    sendBroadcast(mediaScanIntent);

                    imageUriUpdate = contentUri;
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    void getData(){
        if(getIntent().hasExtra("note_id") && getIntent().hasExtra("note_title")){
            id = getIntent().getStringExtra("note_id");
            title = getIntent().getStringExtra("note_title");
            description = getIntent().getStringExtra("note_description");
            color = getIntent().getStringExtra("note_color");
            imageURI = getIntent().getStringExtra("note_image");

            //sets the data to the edit text fields
            update_title.setText(title);
            update_description.setText(description);


            if(color.equals("#43A047")) {
                noteBGUpdate.setCardBackgroundColor(Color.parseColor(color));
            } else if (color.equals("#E53935")) {
                noteBGUpdate.setCardBackgroundColor(Color.parseColor(color));
            } else if (color.equals("#1E88E5")) {
                noteBGUpdate.setCardBackgroundColor(Color.parseColor(color));
            }


            imageIntent = imageURI;
            if(imageIntent != null) {
                imgViewUpdate.setImageURI(Uri.parse(imageURI));
            }
            else {
                Toast.makeText(this,"No Image in Note", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this,"No data entered.", Toast.LENGTH_SHORT).show();
        }
    }


    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}