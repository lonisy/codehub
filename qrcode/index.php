<?php

// 一：直接输出
include "./phpqrcode.php";
$value="http://www.baidu.com";
$errorCorrectionLevel = "L"; // 纠错级别：L、M、Q、H
$matrixPointSize = "6"; // 点的大小：1到10
QRcode::png($value, false, $errorCorrectionLevel, $matrixPointSize);


//// 二：图片文件输出
//include('./phpqrcode.php');
//$data = 'http://www.useryx.com';
//$filename = 'useryx.png';  //  生成的文件名
//$errorCorrectionLevel = 'L';  // 纠错级别：L、M、Q、H
//$matrixPointSize = 4; // 点的大小：1到10
//QRcode::png($data, $filename, $errorCorrectionLevel, $matrixPointSize, 2);


////三： 生成中间带logo的二维码
//include('./phpqrcode.php');
//$value='http://www.useryx.com';
//$logo = 'logo.jpg'; // 中间的logo
//$QR = "base.png"; // 自定义生成的。结束后可以删除
//$last = "last.png"; // 最终生成的图片
//$errorCorrectionLevel = 'L';
//$matrixPointSize = 10;
//QRcode::png($value, $QR, $errorCorrectionLevel, $matrixPointSize, 2);
//if($logo !== FALSE){
//    $QR = imagecreatefromstring(file_get_contents($QR));
//    $logo = imagecreatefromstring(file_get_contents($logo));
//    $QR_width = imagesx($QR);
//    $QR_height = imagesy($QR);
//    $logo_width = imagesx($logo);
//    $logo_height = imagesy($logo);
//    $logo_qr_width = $QR_width / 5;
//    $scale = $logo_width / $logo_qr_width;
//    $logo_qr_height = $logo_height / $scale;
//    $from_width = ($QR_width - $logo_qr_width) / 2;
//    imagecopyresampled($QR, $logo, $from_width, $from_width, 0, 0, $logo_qr_width, $logo_qr_height, $logo_width, $logo_height);
//}
//imagepng($QR,$last); // 生成最终的文件


//include './phpqrcode.php';
//$value = 'http://blog.csdn.net/luoshengkim?viewmode=contents'; //二维码内容
//$errorCorrectionLevel = 'L'; //容错级别
//$matrixPointSize = 6; //生成图片大小
//// 生成二维码图片
//QRcode::png($value, 'qrcode.png', $errorCorrectionLevel, $matrixPointSize, 2);
//// 输出二维码图片
//echo '<img src="qrcode.png">';
