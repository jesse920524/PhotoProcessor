###说明###
本demo的编写是为了实现android端的拍照+图片选择+裁剪+压缩+预览(手势缩放)功能, 
集成了开源项目matisse && ucrop && luban && PhotoView
并做了一定的封装.

项目内部采用RxJava事件流思想处理 图片选择(拍照) -- 压缩 -- 裁剪 的整套流程.

###典型用法###

   1.在业务Activity中初始化ImageManager:
          mSelectManager = ImageManager.getInstance(this, selectConfig, null, null);
          
   2.调用ImageManager的api
          mSelectManager.pickFromGallary();
          或
          mSelectManager.pickAndCrop();
          
   3.在业务Activity#onActivityResult()回调监听图片选择()事件
   
   mSelectManager.onActivityResult(requestCode, resultCode, data)
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe();
                   
   具体使用细节请参考本demo的MainActivity
     
###踩坑记录###

1.ucrop bottom bar 控件颜色无法通过api来设置, 
必须通过colors.xml的方式配置.

解决方案: https://github.com/Yalantis/uCrop/issues/520
saidavdic的答案

2.matisse 在android7.0+的设备必须在manifest中添加fileprovider, 并在res/xml中添加file_paths_private.xml file_paths_public.xml 文件.
另外, matisse#captureStratety()也会使用manifest中定义的provider.
 Matisse.captureStrategy(new CaptureStrategy(true, "me.jessefu.matissedemo.fileprovider", null))
 
3.photoView集成2.3.0版本会报manifest merge failed, 原因是2.3.0版本必须使用androidx.
未迁移到androidx的项目, 解决方案是import低版本的photoView(2.1.5)

参考链接: https://github.com/chrisbanes/PhotoView/issues/654


 
 

