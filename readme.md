###说明###
本demo的编写是为了实现android端的拍照+图片选择+裁剪功能, 
集成了开源项目matisse && ucrop.
并做了一定的封装.


###踩坑记录###

1.ucrop bottom bar 控件颜色无法通过api来设置, 
必须通过colors.xml的方式配置.

解决方案: https://github.com/Yalantis/uCrop/issues/520
saidavdic的答案

2.matisse 在android7.0+的设备必须在manifest中添加fileprovider, 并在res/xml中添加file_paths_private.xml file_paths_public.xml 文件.
另外, matisse#captureStratety()也会使用manifest中定义的provider.
 Matisse.captureStrategy(new CaptureStrategy(true, "me.jessefu.matissedemo.fileprovider", null))
 
 

