package com.example.datacollectionapp.screens.record;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.models.RecordField;

import java.util.List;

public class AudioRecordViewHolder extends RecordViewHolder{
    private ImageView audioView;
    private Button chooseButton;
    private Uri filePath;

    public AudioRecordViewHolder(@NonNull View itemView, Context context, List<RecordField> recordFields) {
        super(itemView, itemView.findViewById(R.id.textFieldName), context, recordFields);
    }

    public void chooseAudio(View view) {
        Intent imageIntent = new Intent();
        imageIntent.setType("audio/*");
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
        Activity activity = (Activity) getContext();
        activity.startActivityForResult(Intent.createChooser(imageIntent, "Choose audio file"),
                NewRecordActivity.CHOOSE_IMAGE_REQUEST + getAdapterPosition());
    }

    public void setAudio(Bitmap bitmap, Uri filepath) {
        this.filePath = filepath;
//        audioView.setImageBitmap(bitmap);
    }

    public Button getChooseButton() {
        return chooseButton;
    }

    public void setChooseButton(Button chooseButton) {
        this.chooseButton = chooseButton;
    }

    public ImageView getAudioView() {
        return audioView;
    }

    public void setAudioView(ImageView audioView) {
        this.audioView = audioView;
    }

    public Uri getFilePath() {
        return filePath;
    }

    public void setFilePath(Uri filePath) {
        this.filePath = filePath;
    }
}
