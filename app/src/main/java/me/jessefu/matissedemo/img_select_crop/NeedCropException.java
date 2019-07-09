package me.jessefu.matissedemo.img_select_crop;

/**
 * @author JesseFu
 * @date 2019/7/9 14 26
 * @description 需要裁剪异常 无需用户任何处理
 */
public class NeedCropException extends RuntimeException {

    public NeedCropException() {
    }

    public NeedCropException(String message) {
        super(message);
    }
}
