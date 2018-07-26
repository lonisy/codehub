<?php
$file_name = "big_file";     //下载文件名
$file_dir = "/path/to/dir/"; //下载文件存放目录
//检查文件是否存在
if (!file_exists($file_dir . $file_name)) {
    header('HTTP/1.1 404 NOT FOUND');
} else {
    //设置脚本的最大执行时间，设置为0则无时间限制
    set_time_limit(0);

    //获得文件大小
    $file_size = filesize($file_dir . $file_name);

    //告诉浏览器这是一个文件流格式的文件
    Header("Content-type: application/octet-stream");
    //请求范围的度量单位
    Header("Accept-Ranges: bytes");
    //Content-Length是指定包含于请求或响应中数据的字节长度
    Header("Accept-Length: " . $file_size);
    //用来告诉浏览器，文件是可以当做附件被下载，下载后的文件名称为$file_name该变量的值。
    Header("Content-Disposition: attachment; filename=" . $file_name);

    var_dump($file_name);
    //针对大文件，规定每次读取文件的字节数为4096字节，直接输出数据
    $read_buffer = 4096;
    $handle = fopen($file_name, 'rb');
    //总的缓冲的字节数
    $sum_buffer = 0;

    //只要没到文件尾，就一直读取
    while (!feof($handle) && $sum_buffer < $file_size) {
        echo fread($handle, $read_buffer);
        $sum_buffer += $read_buffer;
    }

    //关闭句柄
    fclose($handle);
    exit;
}
