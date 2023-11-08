package com.example.noteapplab2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList note_id, note_title, note_description, note_color, image_uri;
    int position;

    CustomAdapter(Context context, ArrayList note_id, ArrayList note_title, ArrayList note_description, ArrayList note_color, ArrayList image_uri){
        this.context = context;
        this.note_id = note_id;
        this.note_title = note_title;
        this.note_description = note_description;
        this.note_color = note_color;
        this.image_uri = image_uri;

    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.note_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        this.position = position;
        holder.note_id_txt.setText(String.valueOf(note_id.get(position)));
        holder.note_title_txt.setText(String.valueOf(note_title.get(position)));
        holder.note_description_txt.setText(String.valueOf(note_description.get(position)));

        String cardColor = String.valueOf(note_color.get(position));
        System.out.println(cardColor);
        holder.cardView.setCardBackgroundColor(Color.parseColor(cardColor));



        holder.deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper myDB = new DatabaseHelper(context);
                myDB.deleteNote(holder.note_id_txt.getText().toString());

                int deletedPosition = holder.getAdapterPosition(); // Get the position of the item being deleted

                // Remove the item from the ArrayLists
                note_id.remove(deletedPosition);
                note_title.remove(deletedPosition);
                note_description.remove(deletedPosition);
                note_color.remove(deletedPosition);
                image_uri.remove(deletedPosition);

                // Notify the adapter that the item has been removed
                notifyItemRemoved(deletedPosition);
                notifyItemRangeChanged(deletedPosition, getItemCount());

            }
        });

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateNoteActivity.class);
                intent.putExtra("note_id",String.valueOf(note_id.get(position)));
                intent.putExtra("note_title",String.valueOf(note_title.get(position)));
                intent.putExtra("note_description",String.valueOf(note_description.get(position)));
                intent.putExtra("note_color",String.valueOf(note_color.get(position)));
                intent.putExtra("note_image",String.valueOf(image_uri.get(position)));

                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return note_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView note_id_txt, note_title_txt, note_description_txt;
        CardView cardView;
        LinearLayout mainLayout;
        Button deletebtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            note_id_txt = itemView.findViewById(R.id.note_id_txt);
            note_title_txt = itemView.findViewById(R.id.note_title_txt);
            note_description_txt = itemView.findViewById(R.id.note_description_txt);
            cardView = itemView.findViewById(R.id.noteCardView);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            deletebtn = itemView.findViewById(R.id.deleteButton);

        }
    }

 //method to filter the notes
    public void filterNotes(ArrayList filteredNoteId, ArrayList filteredNoteTitle, ArrayList filteredNoteDescription, ArrayList filteredNoteColor){
        note_id = filteredNoteId;
        note_title = filteredNoteTitle;
        note_description = filteredNoteDescription;
        note_color = filteredNoteColor;
        notifyDataSetChanged();
    }
}
