<?php

/**
 * Created by IntelliJ IDEA.
 * php 加解密
 * User: wscrlhs
 * Date: 2017/5/24
 * Time: 下午2:29
 */
class Rsa
{
    private static $publicKeyFilePath = "rsa_public_key.pem";

    private static $privateKeyFilePath = "rsa_private_key.pem";

    private static $pubKey;

    private static $priKey;

    /**
     *openssl扩展支持
     *读取公钥文件
     *读取私钥文件
     * Rsa constructor.
     */
    public function __construct()
    {
        extension_loaded('openssl') or die('php需要openssl扩展支持');

        self::$pubKey = file_get_contents(self::$publicKeyFilePath);
        self::$priKey = file_get_contents(self::$privateKeyFilePath);
    }

    /**
     * 加签
     * @param $data 要加签的数据
     * @return string 签名
     */
    public function sign($data)
    {
        $res = openssl_get_privatekey(self::$priKey);
        openssl_sign($data, $sign, $res, OPENSSL_ALGO_SHA1);
        openssl_free_key($res);
        $sign = base64_encode($sign);
        return $sign;
    }

    /**
     * 验签
     * @param $data 用来加签的数据
     * @param $sign 加签后的结果
     * @return bool 验签是否成功
     */
    public function verify($data, $sign)
    {

        //转换为openssl格式密钥
        $res = openssl_get_publickey(self::$pubKey);

        //调用openssl内置方法验签，返回bool值
        $result = (bool)openssl_verify($data, base64_decode($sign), $res);

        //释放资源
        openssl_free_key($res);

        return $result;
    }


    /**
     * rsa加密
     * @param $data 要加密的数据
     * @return string 加密后的密文
     */
    public function encrypt($data)
    {
        //转换为openssl格式密钥
        $res = openssl_get_publickey(self::$pubKey);

        $maxlength = $this->_getMaxEncryptBlockSize($res);
        $output = '';
        $split = str_split($data, $maxlength);
        foreach ($split as $part) {
            openssl_public_encrypt($part, $encrypted, self::$pubKey);
            $output .= $encrypted;
        }
        $encryptedData = base64_encode($output);
        return $encryptedData;
    }

    /**
     * 解密
     * @param $data 要解密的数据
     * @return string 解密后的明文
     */
    public function decrypt($data)
    {
        //转换为openssl格式密钥
        $res = openssl_get_privatekey(self::$priKey);
        $data = base64_decode($data);
        $maxlength = $this->_getMaxDecryptBlockSize($res);
        $output = '';
        $split = str_split($data, $maxlength);
        foreach ($split as $part) {
            openssl_private_decrypt($part, $encrypted, self::$priKey);
            $output .= $encrypted;
        }
        return $output;
    }

    /**
     *根据key的内容获取最大加密lock的大小，兼容各种长度的rsa keysize（比如1024,2048）
     * 对于1024长度的RSA Key，返回值为117
     * @param $keyRes
     * @return float
     */
    public function _getMaxEncryptBlockSize($keyRes)
    {
        $keyDetail = openssl_pkey_get_details($keyRes);
        $modulusSize = $keyDetail['bits'];
        return $modulusSize / 8 - 11;
    }

    /**
     * 根据key的内容获取最大解密block的大小，兼容各种长度的rsa keysize（比如1024,2048）
     * 对于1024长度的RSA Key，返回值为128
     * @param $keyRes
     * @return float
     */
    public function _getMaxDecryptBlockSize($keyRes)
    {
        $keyDetail = openssl_pkey_get_details($keyRes);
        $modulusSize = $keyDetail['bits'];
        return $modulusSize / 8;
    }
}
