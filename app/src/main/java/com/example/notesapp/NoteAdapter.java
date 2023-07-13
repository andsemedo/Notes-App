package com.example.notesapp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.notesapp.classes.Notes;
import com.example.notesapp.interfaces.RecyclerViewInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Notes> notesList;
    private Context context;

    private final RecyclerViewInterface recyclerViewInterface;
    private Timer timer;
    private List<Notes> notesSource;

    public NoteAdapter(Context context, List<Notes> notesList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.notesList = notesList;
        this.recyclerViewInterface = recyclerViewInterface;
        notesSource = notesList;
    }


    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.notes_recycler_view,
                        parent,
                        false
                ),
                recyclerViewInterface
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.noteTitleShow.setText(notesList.get(position).getTitle());
        holder.noteContentShow.setText(notesList.get(position).getContent());

        holder.noteTitleShow.setMaxLines(1);
        holder.noteContentShow.setMaxLines(1);

        holder.noteDateShow.setText(notesList.get(position).getDate());

        System.out.println("holder: "+notesList.get(position).getImageURL());

        if(notesList.get(position).getImageURL() != null && !notesList.get(position).getImageURL().isEmpty()) {
            String image = notesList.get(position).getImageURL();
            Glide.with(context).load(image).centerCrop().into(holder.noteImageShow);
            //Picasso.get().load(image).into(holder.noteImageShow);
            holder.noteImageShow.setVisibility(View.VISIBLE);
        } else {
            holder.noteImageShow.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView noteTitleShow, noteContentShow, noteDateShow;
        ImageView noteImageShow;

        public NoteViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            noteTitleShow = itemView.findViewById(R.id.noteTitleShow);
            noteContentShow = itemView.findViewById(R.id.noteContentShow);
            noteDateShow = itemView.findViewById(R.id.noteDateShow);
            noteImageShow = itemView.findViewById(R.id.noteImageShow);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recyclerViewInterface != null) {

                        Animation animation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.note_click_animation);
                        itemView.setAnimation(animation);

                        int position = getAdapterPosition();

                        if(position != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onNoteClick(position);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(recyclerViewInterface != null) {
                        int position = getAdapterPosition();

                        if(position != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onNoteLongClick(position);
                        }
                    }
                    return true ;
                }
            });
        }

    }

    public void searchNotes(final String searchKey) {
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(searchKey.trim().isEmpty()) {
                    notesList = notesSource;
                } else {
                    ArrayList<Notes> temp = new ArrayList<>();
                    for (Notes note: notesSource) {
                        if (
                                note.getTitle().toLowerCase().contains(searchKey.toLowerCase()) ||
                                note.getContent().toLowerCase().contains(searchKey.toLowerCase())
                        ) {
                            temp.add(note);
                        }
                    }
                    notesList = temp;
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });

            }
        }, 500);

    }

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }


}


