package me.jessefu.matissedemo.img_select_crop;

import android.text.TextUtils;

import top.zibin.luban.CompressionPredicate;

/**
 * @author JesseFu
 * @date 2019/7/10 09 19
 * @description 图片压缩配置
 */
public class ImgCompressConfig {
    private static final String TAG = "ImgCompressConfig";

    private int ignoreBy;//忽略压缩阀值 单位K
    private CompressionPredicate compressionPredicate;//开启压缩的条件

    private ImgCompressConfig(){

    }

    public int getIgnoreBy() {
        return ignoreBy;
    }

    public CompressionPredicate getCompressionPredicate() {
        return compressionPredicate;
    }

    public static class Builder{
        private int ignoreThreshold;
        private CompressionPredicate predicate;

        public Builder(){

        }

        public Builder setThreshold(int threshold){
            this.ignoreThreshold = threshold;
            return this;
        }

        public Builder setFilter(CompressionPredicate predicate){
            this.predicate = predicate;
            return this;
        }

        public ImgCompressConfig build(){
            ImgCompressConfig config = new ImgCompressConfig();
            config.ignoreBy = this.ignoreThreshold;
            config.compressionPredicate = this.predicate;
            return config;
        }
    }

    /**过滤器 如果图片路径为空, 或是gif文件, 则跳过压缩.*/
    public static class GifFilter implements CompressionPredicate{

        @Override
        public boolean apply(String path) {
            return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
        }
    }
}
