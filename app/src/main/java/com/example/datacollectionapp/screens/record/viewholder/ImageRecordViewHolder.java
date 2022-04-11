package com.example.datacollectionapp.screens.record.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.database.connectionmanagers.FirebaseAuthentication;
import com.example.datacollectionapp.database.connectionmanagers.FirebaseStorageManager;
import com.example.datacollectionapp.models.Record;
import com.example.datacollectionapp.models.RecordField;
import com.example.datacollectionapp.screens.record.NewRecordActivity;

import java.util.List;

public class ImageRecordViewHolder extends RecordViewHolder {

    private Button chooseButton;
    private ImageView imageView;
    private Uri filePath;
    private final FirebaseStorageManager firebaseStorageManager;

    public ImageRecordViewHolder(@NonNull View itemView, Context context, List<RecordField> recordFields) {
        super(itemView, itemView.findViewById(R.id.textFieldName), context, recordFields);
        chooseButton = itemView.findViewById(R.id.buttonChooseAudio);
        imageView = itemView.findViewById(R.id.imageView);
        chooseButton.setOnClickListener(this::chooseImage);
        firebaseStorageManager = FirebaseStorageManager.getInstance();
    }

    public void chooseImage(View view) {
        Intent imageIntent = new Intent();
        imageIntent.setType("image/*");
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
        Activity activity = (Activity) getContext();
        activity.startActivityForResult(Intent.createChooser(imageIntent, "Choose image"),
                NewRecordActivity.CHOOSE_IMAGE_REQUEST + getAdapterPosition());
    }

    public void setImage(Bitmap bitmap, Uri filepath) {
        this.filePath = filepath;
        imageView.setImageBitmap(bitmap);
    }

    public void hideChooseButton() {
        chooseButton.setVisibility(View.GONE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, 300, 2f);
        imageView.setLayoutParams(layoutParams);
    }

    public void downloadAndShowImage(String downloadUri) {
        firebaseStorageManager.downloadImage(downloadUri, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                byte[] bytes = task.getResult();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                setImage(bitmap, null);
            } else {
                Log.e(NewRecordActivity.TAG, "Failed to load image");
            }
        });
    }

    public Button getChooseButton() {
        return chooseButton;
    }

    public void setChooseButton(Button chooseButton) {
        this.chooseButton = chooseButton;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public Uri getFilePath() {
        return filePath;
    }

    public void setFilePath(Uri filePath) {
        this.filePath = filePath;
    }
}
