package me.jessefu.matissedemo;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yalantis.ucrop.UCrop;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.listener.OnCheckedListener;
import com.zhihu.matisse.listener.OnSelectedListener;

import java.io.File;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import me.jessefu.matissedemo.img_select_crop.GifSizeFilter;
import me.jessefu.matissedemo.img_select_crop.ImgCropConfig;
import me.jessefu.matissedemo.img_select_crop.ImgSelectConfig;
import me.jessefu.matissedemo.img_select_crop.ImgSelectManager;
import me.jessefu.matissedemo.img_select_crop.NeedCropException;
import top.zibin.luban.Luban;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_CHOOSE = 1024;

    private Button mBtnChoose;
    private ImageView mIvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

    }

    private void initViews() {
        mBtnChoose = findViewById(R.id.btn_choose);
        mIvResult = findViewById(R.id.iv_result);

        mBtnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPermissions();
            }
        });
    }

    private void initPermissions() {
        new RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        return aBoolean == true;
                    }
                })
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        Log.d(TAG, "onNext: " + aBoolean);
//                        pickFromGallary();
                        pickAndCrop();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }



    private ImgSelectManager mSelectManager;
    private void pickFromGallary(){
        ImgSelectConfig selectConfig = new ImgSelectConfig.Builder()
                .selectVideo(false)
                .authorityPath("me.jessefu.matissedemo.fileprovider")
                .captureEnable(true)
                .maxSelectable(1)
                .setCheckListener(new OnCheckedListener() {
                    @Override
                    public void onCheck(boolean isChecked) {
                        Log.d(TAG, "onCheck: " + isChecked);
                    }
                })
                .build();

        mSelectManager = ImgSelectManager.getInstance(this, selectConfig, null);
        mSelectManager.pickFromGallary();
    }

    private void pickAndCrop(){
        ImgSelectConfig selectConfig = new ImgSelectConfig.Builder()
                .selectVideo(false)
                .authorityPath("me.jessefu.matissedemo.fileprovider")
                .captureEnable(true)
                .maxSelectable(1)
                .setCheckListener(new OnCheckedListener() {
                    @Override
                    public void onCheck(boolean isChecked) {
                        Log.d(TAG, "onCheck: " + isChecked);
                    }
                })
                .build();
        ImgCropConfig cropConfig = new ImgCropConfig.Builder()
                .aspectRatio(ImgCropConfig.AspectRatio.ALL)
                .build();
        mSelectManager = ImgSelectManager.getInstance(this, selectConfig, cropConfig);
        mSelectManager.pickAndCrop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mSelectManager.onActivityResult(requestCode, resultCode, data)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Uri>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Uri> uris) {
                        Log.d(TAG, "onNext: " + uris);
                        mIvResult.setImageURI(uris.get(0));
                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.d(TAG, "onError: " + e.getLocalizedMessage());
                        if (!(e instanceof NeedCropException)){
                            Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        //        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK){
//            List<Uri> uriList = Matisse.obtainResult(data);
//            Log.d(TAG, "onActivityResult: " + uriList);
//            if (!uriList.isEmpty()){
//                mIvResult.setImageURI(uriList.get(0));
//                pickAndCrop(uriList.get(0));
//            }
//
//        }else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK){
//            final Uri resultUri = UCrop.getOutput(data);
//            mIvResult.setImageURI(resultUri);
//        }else if (resultCode == UCrop.RESULT_ERROR){
//            Log.d(TAG, "onActivityResult: " + UCrop.getError(data));
//        }
    }

    @Deprecated
    private void initCropImg(Uri originalUri) {
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(Color.parseColor("#3C3F41"));
        options.setStatusBarColor(Color.parseColor("#3C3F41"));
        options.setToolbarWidgetColor(Color.WHITE);
        options.setRootViewBackgroundColor(Color.parseColor("#3C3F41"));
        options.setLogoColor(Color.parseColor("#3C3F41"));
//        options.setActiveControlsWidgetColor(Color.parseColor("#3C3F41"));
        options.setActiveWidgetColor(Color.parseColor("#3C3F41"));
//        options.setDimmedLayerColor(Color.parseColor("#3C3F41"));
//        options.setActiveWidgetColor(Color.parseColor("#3C3F41"));
        UCrop.of(originalUri, Uri.fromFile(new File(getCacheDir(), "CropImage" + System.currentTimeMillis() + ".jpeg")))
                .withOptions(options)
                .withAspectRatio(1, 1)
                .start(this);
    }

    @Deprecated
    private void initPhotoChoose() {
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, "me.jessefu.matissedemo.fileprovider", null))
                .theme(R.style.Matisse_Dracula)
                .countable(false)
                .addFilter(new GifSizeFilter(320, 320, 5* Filter.K * Filter.K))
                .maxSelectable(1)
                .imageEngine(new GlideEngine())
                .setOnCheckedListener(new OnCheckedListener() {
                    @Override
                    public void onCheck(boolean isChecked) {
                        Log.d(TAG, "onCheck: " + isChecked);
                    }
                })
                .setOnSelectedListener(new OnSelectedListener() {
                    @Override
                    public void onSelected(@NonNull List<Uri> uriList, @NonNull List<String> pathList) {
                        Log.d(TAG, "onSelected: uriList: " + uriList + " pathList: " + pathList);
                    }
                })
                .forResult(REQUEST_CODE_CHOOSE);
    }
}
