package me.jessefu.matissedemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * @author JesseFu
 * @date 2019/7/11 13 48
 * @description 照片预览页
 */
public class PhotoPreviewActivity extends AppCompatActivity {
    private static final String TAG = "PhotoPreviewActivity";

    private static final String KEY_URI = "key_uri";
    /**
     * @param context
     *
     * @param uri */
    public static void actionStart(@NonNull Context context, Uri uri){
        Intent intent = new Intent(context, PhotoPreviewActivity.class);
        intent.putExtra(KEY_URI, uri);
        context.startActivity(intent);
    }

    private PhotoView mPhotoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initViews() {
        mPhotoView = findViewById(R.id.pv);
        Uri uri = getIntent().getParcelableExtra(KEY_URI);
        Log.d(TAG, "initViews: " + uri);

        Glide.with(this)
                .load(uri)
                .dontAnimate()
                .into(mPhotoView);
    }

}
