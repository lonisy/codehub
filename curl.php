<?php

/**
 * Created by IntelliJ IDEA.
 * User: wscrlhs
 * Date: 2017/6/8
 * Time: 上午9:53
 */
class curl
{

    /**
     * curl传递pdf文件
     * @param null $url
     * @param array $data
     * @return bool|mixed
     */
    public function postFile($url = null, $data = array('file_path'))
    {
        if (!$url) return false;
        if (empty($data['file_path'])) return false;
        $cfile = new CURLFile($data['file_path'], 'application/pdf', ''); // try adding
        $pdfdata = array('inputStream' => $cfile);
        $ch = curl_init();

        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_USERAGENT, 'Opera/9.80 (Windows NT 6.2; Win64; x64) Presto/2.12.388 Version/12.15');
        curl_setopt($ch, CURLOPT_HTTPHEADER, array('User-Agent: Opera/9.80 (Windows NT 6.2; Win64; x64) Presto/2.12.388 Version/12.15', 'Content-Type: multipart/form-data'));
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $pdfdata);
        curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);

        $response = curl_exec($ch);
        curl_close($ch);
        return $response;
    }


}
