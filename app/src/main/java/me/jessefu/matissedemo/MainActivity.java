package me.jessefu.matissedemo;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.view.TransformImageView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.listener.OnCheckedListener;
import com.zhihu.matisse.listener.OnSelectedListener;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import me.jessefu.matissedemo.img_select_crop.GifSizeFilter;
import me.jessefu.matissedemo.img_select_crop.ImgCompressConfig;
import me.jessefu.matissedemo.img_select_crop.ImgCropConfig;
import me.jessefu.matissedemo.img_select_crop.ImgSelectConfig;
import me.jessefu.matissedemo.img_select_crop.ImageManager;
import me.jessefu.matissedemo.img_select_crop.NeedCropException;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_CHOOSE = 1024;

    private Button mBtnChoose;
    private ImageView mIvResult;
    private TransformImageView mTivResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

    }

    private void initViews() {
        mBtnChoose = findViewById(R.id.btn_choose);
        mIvResult = findViewById(R.id.iv_result);
        mTivResult = findViewById(R.id.tiv_result);

        mBtnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPermissions();
            }
        });
        mIvResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPreviewActivity.actionStart(MainActivity.this, resultUri);
            }
        });
    }

    private ImgSelectConfig selectConfig;
    private ImgCompressConfig compressConfig;
    private ImgCropConfig cropConfig;
    private void initPermissions() {

        selectConfig = new ImgSelectConfig.Builder()
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


        compressConfig = new ImgCompressConfig.Builder()
                .setThreshold(100)
                .setFilter(new ImgCompressConfig.GifFilter())
                .build();

        cropConfig = new ImgCropConfig.Builder()
                .aspectRatio(ImgCropConfig.AspectRatio.ALL)
                .build();

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



    private ImageManager mSelectManager;
    private void pickFromGallary(){


        mSelectManager = ImageManager.getInstance(this, selectConfig, null, null);
        mSelectManager.pickFromGallary();
    }

    private void pickAndCrop(){


        mSelectManager = ImageManager.getInstance(this, selectConfig, cropConfig, compressConfig);
        mSelectManager.pickAndCrop();
    }

    private Uri resultUri;
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
                        resultUri = uris.get(0);
                        mIvResult.setImageURI(resultUri);
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

    private void compressPhoto(final Uri uri) {
        Observable.just(uri)
                .observeOn(Schedulers.io())
                .map(new Function<Uri, List<File>>() {
                    @Override
                    public List<File> apply(Uri uri) throws Exception {

                        return Luban.with(MainActivity.this)
                                .load(uri)
                                .filter(new CompressionPredicate() {
                                    @Override
                                    public boolean apply(String path) {
                                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                                    }
                                })
                                .get();

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<File>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<File> files) {
                Uri uri1 = Uri.parse(files.get(0).getAbsolutePath());
                mIvResult.setImageURI(uri1);
                Log.d(TAG, "onNext: after compress: " + uri1);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: " + e.getLocalizedMessage());
            }

            @Override
            public void onComplete() {

            }
        });
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
