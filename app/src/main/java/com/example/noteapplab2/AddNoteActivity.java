package com.example.noteapplab2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import android.Manifest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddNoteActivity extends AppCompatActivity {
    EditText note_titleInput, note_descriptionInput;
    Button saveNoteButton, redNote, blueNote, greenNote, uploadBtn, captureBtn;
    String noteColor;
    CardView noteBG;
    ImageView imgView;
    DatabaseHelper myDB;
    Uri imageUri;

    private static final int SELECT_IMG = 100;
    private static final int CAPTURE_IMG = 101;
    private static final int CAMERA_PERMISSION_CODE = 105;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        setSupportActionBar(findViewById(R.id.toolbar2));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myDB = new DatabaseHelper(AddNoteActivity.this);

        note_titleInput = findViewById(R.id.noteTitle);
        note_descriptionInput = findViewById(R.id.noteDescription);
        saveNoteButton = findViewById(R.id.saveNoteButton);
        redNote = findViewById(R.id.redNote);
        blueNote = findViewById(R.id.blueNote);
        greenNote = findViewById(R.id.greenNote);
        noteBG = findViewById(R.id.noteView);
        uploadBtn = findViewById(R.id.uploadImgBtn);
        captureBtn = findViewById(R.id.captureImgBtn);
        imgView = findViewById(R.id.noteImage);

        //set noteColor string to hexcode for red when button is clicked
        redNote.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {
                noteColor = "#E53935";
                noteBG.setCardBackgroundColor(Color.parseColor(noteColor));
            }
        });

        //set noteColor string to hexcode for blue when button is clicked
        blueNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteColor = "#1E88E5";
                noteBG.setCardBackgroundColor(Color.parseColor(noteColor));
            }
        });

        //set noteColor string to hexcode for green when button is clicked
        greenNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteColor = "#43A047";
                noteBG.setCardBackgroundColor(Color.parseColor(noteColor));

            }
        });

        //send values to the database help
        saveNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = note_titleInput.getText().toString().trim();
                if(title.equals("")) {
                    Snackbar snackbar = Snackbar.make(v, "Title is empty", Snackbar.LENGTH_LONG);  //prints error message if title is empty
                    snackbar.show();
                } else {
                    myDB.addNote(note_titleInput.getText().toString().trim(),
                            note_descriptionInput.getText().toString().trim(),
                            noteColor.trim(), imageUri.toString());
                    Intent intent = new Intent(AddNoteActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        //function to upload the image from gallery
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,SELECT_IMG);
            }
        });

        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, CAPTURE_IMG);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){

            if(requestCode==SELECT_IMG){
                imageUri = data.getData();
                imgView.setImageURI(imageUri);
            }
            else if(requestCode==CAPTURE_IMG){
                Bitmap img = (Bitmap)(data.getExtras().get("data"));
                imgView.setImageBitmap(img);

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

                    imageUri = contentUri;
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}