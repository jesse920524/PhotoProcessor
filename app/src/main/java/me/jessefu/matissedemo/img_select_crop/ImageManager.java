package me.jessefu.matissedemo.img_select_crop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.Nullable;
import android.util.Log;

import com.yalantis.ucrop.UCrop;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.jessefu.matissedemo.R;
import top.zibin.luban.Luban;

import static android.app.Activity.RESULT_OK;

/**
 * @author JesseFu
 * @date 2019/7/8 15 23
 * @description 图片选择 && 裁剪管理类
 */
public class ImageManager {
    private static final String TAG = "ImageManager";

    private static final int REQUEST_CODE_CHOOSE = 1024;


    private static volatile ImageManager INSTANCE;
    private static Matisse matisseInstance;

    private Context mContext;

    private ImgSelectConfig mSelectConfig;
    private ImgCropConfig mCropConfig;
    private ImgCompressConfig mCompressConfig;

    /**
     * @param context
     * @param imgSelectConfig 图片选择配置
     * @param imgCompressConfig 图片压缩配置
     * @param imgCropConfig 图片裁剪配置*/
    public static ImageManager getInstance(Context context,
                                           ImgSelectConfig imgSelectConfig,
                                           ImgCropConfig imgCropConfig,
                                           ImgCompressConfig imgCompressConfig){
        if (INSTANCE == null){
            synchronized (ImageManager.class){
                if (INSTANCE == null){
                    INSTANCE = new ImageManager(context, imgSelectConfig, imgCropConfig, imgCompressConfig);
                }
            }
        }
        return INSTANCE;
    }

    private ImageManager(final Context mContext,
                         final ImgSelectConfig mSelectConfig,
                         final ImgCropConfig cropConfig,
                         final ImgCompressConfig compressConfig){
        this.mContext = mContext;
        this.mSelectConfig = mSelectConfig;
        this.mCropConfig = cropConfig;
        this.mCompressConfig = compressConfig;
        matisseInstance = Matisse.from((Activity) mContext);
    }

    /**从图库选择图片*/
    public void pickFromGallary(){
        matisseInstance
                .choose(mSelectConfig.isSelecVideo() ? MimeType.ofAll() : MimeType.ofImage())
                .capture(mSelectConfig.isCaptureEnable() ? true : false)
                .captureStrategy(new CaptureStrategy(true, mSelectConfig.getAuthorityPath()))
                .theme(R.style.Matisse_Dracula)
                .countable(true)
                .maxSelectable(mSelectConfig.getMaxSelectable())
                .imageEngine(new GlideEngine())
                .setOnCheckedListener(mSelectConfig.getCheckedListener())
                .setOnSelectedListener(mSelectConfig.getSelectedListener())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    /**从图库选择图片,选择后裁剪.
     * 注意: 如果选择了不止一张图片, 就无法调用后续裁剪功能!*/
    public void pickAndCrop(){
        if (mSelectConfig.getMaxSelectable() > 1){
            throw new IllegalStateException("如果选择了不止一张图片, 就无法调用后续裁剪功能!");
        }

        pickFromGallary();
    }

    /**必须在业务Activity || Fragment # onActivityResult调用本方法, 以接收图片选择的结果.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     *
     * @return 当CropConfig为null时, 不需要裁剪, 返回选择的图片列表.
     *          当CropConfig不为null时, 需要裁剪, 跳转到裁剪页面, 返回裁剪后的一张图片的uri
     *
     *          当CompressConfig为null时, 不需压缩.
     *
     * */
    @SuppressLint("CheckResult")
    public Observable<List<Uri>> onActivityResult(final int requestCode,
                                                  final int resultCode,
                                                  final @Nullable Intent data){
        //处理选择图片
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            return Observable.create(new ObservableOnSubscribe<List<String>>() {
                @Override
                public void subscribe(ObservableEmitter<List<String>> emitter) throws Exception {

                    List<String> uriList = matisseInstance.obtainPathResult(data);
                    Log.d(TAG, "onActivityResult: " + uriList);
                    if (!uriList.isEmpty()){
                        emitter.onNext(uriList);
                    }

                }
            }).subscribeOn(Schedulers.io())
                    .map(new Function<List<String>, List<Uri>>() {
                        @Override
                        public List<Uri> apply(List<String> uris) throws Exception {
                            //压缩图片
                            if (uris.isEmpty()){
                                throw new IllegalStateException("data is null");
                            }
                            //如果CompressConfig为空, 跳过执行压缩,直接返回原图
                            List<Uri> uriList = new ArrayList<>();
                            if (mCompressConfig == null){
                                for (String path :
                                        uris) {
                                    uriList.add(Uri.parse(path));
                                }
                                return uriList;
                            }
                            Log.d(TAG, "apply: before compress: " + uris);
                            //压缩
                            List<File> compressedList = Luban.with(mContext)
                                    .setFocusAlpha(true)
                                    .ignoreBy(mCompressConfig.getIgnoreBy())
                                    .filter(mCompressConfig.getCompressionPredicate())
                                    .load(uris)
                                    .get();

                            Log.d(TAG, "after compress apply: " + compressedList);

                            for (File file :
                                    compressedList) {
                                file.renameTo(new File(mContext.getCacheDir(), System.currentTimeMillis() + ".jpeg"));
                                Uri uri = Uri.fromFile(file);
                                uriList.add(uri);
                            }
                            Log.d(TAG, "apply: " + uriList);
                            return uriList;
                        }
                    }).subscribeOn(Schedulers.io())
                    .map(new Function<List<Uri>, List<Uri>>() {
                        @Override
                        public List<Uri> apply(List<Uri> uris) throws Exception {

                            if (mCropConfig == null){//如果cropConfig为null,则直接将选择的图片返回给caller.
                                return uris;
                            }

                                //如果CropConfig不为null, 则下一步需要裁剪
                                ImgCropConfig.AspectRatio ratio = mCropConfig.getRatio();

                                UCrop.of(uris.get(0),
                                        Uri.fromFile(new File(mContext.getCacheDir(), "CropImage" + System.currentTimeMillis() + ".jpeg")))
                                        .withAspectRatio(ratio.getRatioX(), ratio.getRatioY())
                                        .start((Activity) mContext);
                                /**这里抛出的异常无需任何操作 仅通知下游观察者.*/
                                throw new NeedCropException("need crop");
                        }
                    });

        }else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK){//处理裁剪
            List<Uri> uriList = new ArrayList<>();
            Log.d(TAG, "onActivityResult: " + uriList);
            uriList.add(UCrop.getOutput(data));
            return Observable.just(uriList);
        }else{//处理错误
            return Observable.error(data == null ? new IllegalArgumentException("data is null") : UCrop.getError(data));
        }

    }
}
