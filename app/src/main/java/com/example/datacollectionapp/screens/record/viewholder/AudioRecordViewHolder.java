package com.example.datacollectionapp.screens.record.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.database.connectionmanagers.FirebaseStorageManager;
import com.example.datacollectionapp.models.RecordField;
import com.example.datacollectionapp.screens.record.NewRecordActivity;

import java.util.List;

public class AudioRecordViewHolder extends RecordViewHolder {

    private final FirebaseStorageManager firebaseStorageManager;
    private TextView audioName;
    private Button chooseButton;
    private Uri filePath;

    public AudioRecordViewHolder(@NonNull View itemView, Context context, List<RecordField> recordFields) {
        super(itemView, itemView.findViewById(R.id.textFieldName), context, recordFields);
        chooseButton = itemView.findViewById(R.id.buttonChooseAudio);
        audioName = itemView.findViewById(R.id.textView);
        chooseButton.setOnClickListener(this::chooseAudio);
        firebaseStorageManager = FirebaseStorageManager.getInstance();
    }

    public void chooseAudio(View view) {
        Intent imageIntent = new Intent();
        imageIntent.setType("audio/*");
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
        Activity activity = (Activity) getContext();
        activity.startActivityForResult(Intent.createChooser(imageIntent, "Choose audio file"),
                NewRecordActivity.CHOOSE_AUDIO_REQUEST + getAdapterPosition());
    }

    public void setAudio(Uri filepath, String audioName) {
        this.filePath = filepath;
        setAudioName(audioName);
    }

    public void showAudio(String downloadUri) {
        String fileName = firebaseStorageManager.getFileName(downloadUri);
        setAudio(null, fileName);
    }

    public void hideChooseButton() {
        chooseButton.setVisibility(View.GONE);
    }

    public Button getChooseButton() {
        return chooseButton;
    }

    public void setChooseButton(Button chooseButton) {
        this.chooseButton = chooseButton;
    }

    public Uri getFilePath() {
        return filePath;
    }

    public void setFilePath(Uri filePath) {
        this.filePath = filePath;
    }

    public void setAudioName(String audioName) {
        if (!audioName.trim().isEmpty()) {
            this.audioName.setText(audioName);
            this.audioName.setVisibility(View.VISIBLE);
        }
    }
}
