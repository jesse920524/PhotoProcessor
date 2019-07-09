package me.jessefu.matissedemo.img_select_crop;

import com.zhihu.matisse.listener.OnCheckedListener;
import com.zhihu.matisse.listener.OnSelectedListener;

/**
 * @author JesseFu
 * @date 2019/7/8 15 28
 * @description 图片选择配置
 */
public class ImgSelectConfig {
    private static final String TAG = "ImgSelectConfig";

    private boolean selecVideo;//是否读取视频
    private boolean captureEnable;//是否拍照
    private String authorityPath;//fileProvider的授权路径
    private int maxSelectable;//最多选择数

    private OnCheckedListener checkedListener;
    private OnSelectedListener selectedListener;//选择监听

    private ImgSelectConfig(Builder builder){
        this.selecVideo = builder.selectVideo;
        this.captureEnable = builder.captureEnable;
        this.authorityPath = builder.authorityPath;
        this.maxSelectable = builder.maxSelectable;
        this.checkedListener = builder.checkedListener;
        this.selectedListener = builder.selectedListener;
    }

    public boolean isSelecVideo() {
        return selecVideo;
    }

    public boolean isCaptureEnable() {
        return captureEnable;
    }

    public String getAuthorityPath() {
        return authorityPath;
    }

    public int getMaxSelectable() {
        return maxSelectable;
    }

    public OnCheckedListener getCheckedListener() {
        return checkedListener;
    }

    public OnSelectedListener getSelectedListener() {
        return selectedListener;
    }

    @Override
    public String toString() {
        return "ImgSelectConfig{" +
                "selecVideo=" + selecVideo +
                ", captureEnable=" + captureEnable +
                ", authorityPath='" + authorityPath + '\'' +
                ", maxSelectable=" + maxSelectable +
                ", checkedListener=" + checkedListener +
                ", selectedListener=" + selectedListener +
                '}';
    }

    public static class Builder{
        private boolean selectVideo;
        private boolean captureEnable;
        private String authorityPath;
        private int maxSelectable;

        private OnCheckedListener checkedListener;
        private OnSelectedListener selectedListener;

        public Builder(){

        }

        public Builder selectVideo(boolean selectVideo){
            this.selectVideo = selectVideo;
            return this;
        }

        public Builder captureEnable(boolean captureEnable){
            this.captureEnable = captureEnable;
            return this;
        }

        public Builder authorityPath(String path){
            this.authorityPath = path;
            return this;
        }

        public Builder maxSelectable(int max){
            this.maxSelectable = max;
            return this;
        }

        public Builder setCheckListener(OnCheckedListener listener){
            this.checkedListener = listener;
            return this;
        }

        public Builder setSelectListener(OnSelectedListener listener){
            this.selectedListener = listener;
            return this;
        }

        public ImgSelectConfig build(){
            return new ImgSelectConfig(this);
        }
    }
}
