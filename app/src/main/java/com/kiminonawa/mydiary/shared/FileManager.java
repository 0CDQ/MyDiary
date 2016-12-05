package com.kiminonawa.mydiary.shared;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Created by daxia on 2016/11/18.
 */

public class FileManager {

    private static final String TAG = "FileManager";
    //Min free space is 50 MB
    public static final int MIN_FREE_SPACE = 50;

    public final static int ROOT_DIR = 0;
    public final static int TEMP_DIR = 1;
    public final static int DIARY_EDIT_CACHE_DIR = 2;
    public final static int DIARY_ROOT_DIR = 3;
    public final static int MEMO_ROOT_DIR = 4;
    public final static int CONTACTS_ROOT_DIR = 5;
    public final static int SETTING_DIR = 6;

    /**
     * The path is :
     * 1.diary & setting temp
     * /sdcard/Android/data/com.kiminonawa.mydiary/files/temp
     * 2.diary edit temp
     * /sdcard/Android/data/com.kiminonawa.mydiary/files/diary/editCache
     * 3.diary saved
     * /sdcard/Android/data/com.kiminonawa.mydiary/files/diary/typeId/diaryId/
     * 4.memo path
     * /sdcard/Android/data/com.kiminonawa.mydiary/files/memo/typeId/
     * 5.contacts path
     * /sdcard/Android/data/com.kiminonawa.mydiary/files/contacts/typeId/
     * 6.Setting path
     * /sdcard/Android/data/com.kiminonawa.mydiary/files/setting/
     */
    private File fileDir;
    private Context mContext;
    private final static String TEMP_DIR_STR = "temp/";
    private final static String DIARY_ROOT_DIR_STR = "diary/";
    private final static String MEMO_ROOT_DIR_STR = "memo/";
    private final static String CONTACTS_ROOT_DIR_STR = "contacts/";
    private final static String EDIT_CACHE_DIARY_DIR_STR = "diary/editCache/";
    private final static String SETTING_DIR_STR = "setting/";

    /**
     * Create trem dir file manager
     *
     * @param context
     */
    public FileManager(Context context, int dir) {
        this.mContext = context;
        switch (dir) {
            case ROOT_DIR:
                this.fileDir = mContext.getExternalFilesDir("");
                break;
            case TEMP_DIR:
                this.fileDir = mContext.getExternalFilesDir(TEMP_DIR_STR);
                break;
            case DIARY_ROOT_DIR:
                this.fileDir = mContext.getExternalFilesDir(DIARY_ROOT_DIR_STR);
                break;
            case MEMO_ROOT_DIR:
                this.fileDir = mContext.getExternalFilesDir(MEMO_ROOT_DIR_STR);
                break;
            case CONTACTS_ROOT_DIR:
                this.fileDir = mContext.getExternalFilesDir(CONTACTS_ROOT_DIR_STR);
                break;
            case DIARY_EDIT_CACHE_DIR:
                this.fileDir = mContext.getExternalFilesDir(EDIT_CACHE_DIARY_DIR_STR);
                break;
            case SETTING_DIR:
                this.fileDir = mContext.getExternalFilesDir(SETTING_DIR_STR);
                break;
        }
    }

    /**
     * Create diary  dir file manager
     */
    public FileManager(Context context, long topicId, long diaryId) {
        this.mContext = context;
        this.fileDir = mContext.getExternalFilesDir(DIARY_ROOT_DIR_STR + "/" + topicId + "/" + diaryId + "/");
    }

    /**
     * Create topic dir file manager for delete
     */
    public FileManager(Context context, long topicId) {
        this.mContext = context;
        this.fileDir = mContext.getExternalFilesDir(DIARY_ROOT_DIR_STR + "/" + topicId + "/");
    }


    public File getDiaryDir() {
        return fileDir;
    }

    public String getDiaryDirAbsolutePath() {
        return fileDir.getAbsolutePath();
    }

    public void clearDiaryDir() {
        if (fileDir.isDirectory()) {
            String[] children = fileDir.list();
            for (int i = 0; i < children.length; i++) {
                new File(fileDir, children[i]).delete();
            }
        }
    }

    public static String getFileNameByUri(Context context, Uri uri) {
        String displayName = "";
        if (uri.getScheme().toString().startsWith("content")) {
            Cursor cursor = context.getContentResolver()
                    .query(uri, null, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = cursor.getString(
                            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    cursor.close();
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

        } else if (uri.getScheme().toString().startsWith("file")) {
            try {
                File file = new File(new URI(uri.toString()));
                if (file.exists()) {
                    displayName = file.getName();
                }
            } catch (URISyntaxException e) {
                Log.e(TAG, e.toString());
            }
        } else {
            File file = new File(uri.getPath());
            if (file.exists()) {
                displayName = file.getName();
            }
        }
        return displayName;
    }

    public static void startBrowseImageFile(Fragment fragment, int requestCode) {
        try {
            Intent intentImage = new Intent();
            intentImage.setType("image/*");
            intentImage.setAction(Intent.ACTION_GET_CONTENT);
            fragment.startActivityForResult(Intent.createChooser(intentImage, "Select Picture"), requestCode);
        } catch (android.content.ActivityNotFoundException ex) {
            Log.e(TAG, ex.toString());
        }
    }

    public static void startBrowseImageFile(Activity activity, int requestCode) {
        try {
            Intent intentImage = new Intent();
            intentImage.setType("image/*");
            intentImage.setAction(Intent.ACTION_GET_CONTENT);
            activity.startActivityForResult(Intent.createChooser(intentImage, "Select Picture"), requestCode);
        } catch (android.content.ActivityNotFoundException ex) {
            Log.e(TAG, ex.toString());
        }
    }

    public static String createRandomFileName() {
        return UUID.randomUUID().toString();
    }


    public static boolean isImage(String fileName) {
        return fileName.toLowerCase().endsWith(".jpeg") || fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".png");
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }


    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }


    /**
     * Gets the real path from file
     *
     * @param context
     * @param contentUri
     * @return path
     * @see:http://stackoverflow.com/questions/28452591/android-get-exif-rotation-from-uri
     */
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, contentUri)) {
            return getPathForV19AndUp(context, contentUri);
        } else {
            return getPathForPreV19(context, contentUri);
        }
    }

    /**
     * Handles pre V19 uri's
     *
     * @param context
     * @param contentUri
     * @return
     */
    private static String getPathForPreV19(Context context, Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();

        return res;
    }

    /**
     * Handles V19 and up uri's
     *
     * @param context
     * @param contentUri
     * @return path
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getPathForV19AndUp(Context context, Uri contentUri) {
        String wholeID = DocumentsContract.getDocumentId(contentUri);

        // Split at colon, use second item in the array
        //Fix split id fail
        String[] wholeIDSpilt = wholeID.split(":");
        String id;
        if (wholeIDSpilt.length > 1) {
            id = wholeID.split(":")[1];
        } else {
            id = wholeID;
        }
        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = context.getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

        String filePath = "";
        int columnIndex = cursor.getColumnIndex(column[0]);
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        cursor.close();
        return filePath;
    }

    /**
     * @return MB
     */
    public static long getSDCardFreeSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        long blockSize, freeBlocks;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = sf.getBlockSize();
            freeBlocks = sf.getAvailableBlocks();
        } else {
            blockSize = sf.getBlockSizeLong();
            freeBlocks = sf.getAvailableBlocksLong();
        }
        return (freeBlocks * blockSize) / 1024 / 1024;
    }


}
