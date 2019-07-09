package me.jessefu.matissedemo.img_select_crop;

import android.net.Uri;

/**
 * @author JesseFu
 * @date 2019/7/8 15 28
 * @description 图片裁剪配置
 */
public class ImgCropConfig {
    private static final String TAG = "ImgCropConfig";

    /**裁剪宽高比 */
    public enum AspectRatio{
        ALL(0, 0),
        ONLY_ONE_TO_ONE(1, 1);

        private int ratioX;
        private int ratioY;
        AspectRatio(int ratioX, int ratioY){
            this.ratioX = ratioX;
            this.ratioY = ratioY;
        }

        public int getRatioX() {
            return ratioX;
        }

        public int getRatioY() {
            return ratioY;
        }}

    private AspectRatio ratio;

    private ImgCropConfig(Builder builder){
        this.ratio =builder.ratio;
    }

    public AspectRatio getRatio() {
        return ratio;
    }

    public static class Builder{
        private AspectRatio ratio;

        public Builder aspectRatio(AspectRatio ratio){
            this.ratio = ratio;
            return this;
        }

        public ImgCropConfig build(){
            return new ImgCropConfig(this);
        }
    }
}
