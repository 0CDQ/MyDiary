package com.kiminonawa.mydiary.entries.diary.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.kiminonawa.mydiary.shared.gui.DiaryPhotoLayout;

import java.io.File;

/**
 * Created by daxia on 2016/11/19.
 */

public class DiaryPhoto implements IDairyRow {

    private DiaryPhotoLayout DiaryPhotoLayout;
    private String photoFileName;


    public DiaryPhoto(Context context, int position, View.OnClickListener clickListener) {
        DiaryPhotoLayout = new DiaryPhotoLayout(context);
        DiaryPhotoLayout.setDeleteOnClick(clickListener, position);
    }

    public void setPhoto(Bitmap bitmap, String photoFileName) {
        this.photoFileName = photoFileName;
        DiaryPhotoLayout.setPhotoBitmap(bitmap);
    }

    public ImageView getPhoto() {
        return DiaryPhotoLayout.getPhoto();
    }

    @Override
    public void setContent(String content) {
        //This content is path
        Log.e("test",content);
        File imgFile = new File(content);
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(content);
            DiaryPhotoLayout.getPhoto().setImageBitmap(bitmap);
        }
    }

    @Override
    public int getType() {
        return TYPE_PHOTO;
    }

    @Override
    public View getView() {
        return DiaryPhotoLayout;
    }

    @Override
    public String getContent() {
        return photoFileName;
    }

}
