
/**
 * Created by wscrlhs on 2017/3/28.
 * 签名
 * 加解密
 */

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @Auther wscrlhs
 */
public class Rsa {

    /**
     * String to hold name of the encryption algorithm.
     */
    public static final String ALGORITHM = "RSA";

    /**
     * 签名算法
     */
    public static final String signAlgorithm = "SHA1WithRSA";

    /**
     * 加密算法
     */
    public static final String cipherAlgorithm = "RSA/ECB/PKCS1Padding";

    /**
     * String to hold the name of the private key file.
     */

    public static final String PRIVATE_KEY_FILE = "rsa_private_key.pem";
    /**
     * String to hold name of the public key file.
     */

    public static final String PUBLIC_KEY_FILE = "rsa_public_key.pem";

    public static PublicKey publicKey;

    public static PrivateKey privateKey;


    /**
     * 密钥长度
     */
    public static final int keyLength = 1024;

    /**
     * 填充块长度
     */
    public static final int reserveSize = 11;

    /**
     *加载公钥和私钥
     */
    static {
        try {
            publicKey = Rsa.getPublicKey(Rsa.class.getClassLoader().getResourceAsStream(PUBLIC_KEY_FILE), "RSA");
            privateKey = Rsa.getPrivateKey(Rsa.class.getClassLoader().getResourceAsStream(PRIVATE_KEY_FILE), "RSA");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取公钥对象
     *
     * @param inputStream  公钥输入流
     * @param keyAlgorithm 密钥算法
     * @return 公钥对象
     * @throws Exception
     */
    public static PublicKey getPublicKey(InputStream inputStream, String keyAlgorithm) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String readLine;
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            X509EncodedKeySpec pubX509 = new X509EncodedKeySpec(Base64.decodeBase64(sb.toString()));
            KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
            PublicKey publicKey = keyFactory.generatePublic(pubX509);

            return publicKey;
        } catch (FileNotFoundException e) {
            throw new Exception("公钥路径文件不存在");
        } catch (IOException e) {
            throw new Exception("读取公钥异常");
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(String.format("生成密钥工厂时没有[%s]此类算法", keyAlgorithm));
        } catch (InvalidKeySpecException e) {
            throw new Exception("生成公钥对象异常");
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
            }
        }
    }

    /**
     * 获取私钥对象
     *
     * @param inputStream  私钥输入流
     * @param keyAlgorithm 密钥算法
     * @return 私钥对象
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    public static PrivateKey getPrivateKey(InputStream inputStream, String keyAlgorithm) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String readLine;
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(sb.toString()));
            KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
            PrivateKey privateKey = keyFactory.generatePrivate(priPKCS8);

            return privateKey;
        } catch (FileNotFoundException e) {
            throw new Exception("私钥路径文件不存在");
        } catch (IOException e) {
            throw new Exception("读取私钥异常");
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("生成私钥对象异常");
        } catch (InvalidKeySpecException e) {
            throw new Exception("生成私钥对象异常");
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
            }
        }
    }


    /**
     * 数字签名入口
     *
     * @param data 要签名的数据
     * @return 返回签名后的数据
     */
    public static String sign(String data) {
        byte[] text;
        String signData = null;
        try {
            text = Rsa._digitalSign(data.getBytes("UTF-8"), privateKey, signAlgorithm);
            signData = new String(Base64.encodeBase64(text));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signData;
    }


    /**
     * 验证签名入口
     *
     * @param data     待签名数据
     * @param signData 已签名数据
     * @return
     */
    public static boolean verify(String data, String signData) {
        boolean flag = false;
        try {
            flag = Rsa._verifyDigitalSign(data.getBytes("UTF-8"), Base64.decodeBase64(signData), publicKey, signAlgorithm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 加密入口
     *
     * @param data
     * @return
     */
    public static String encode(String data) {
        byte[] cipherText;// 加密
        String encodeData = null;
        try {
            cipherText = Rsa._encrypt(data.getBytes("UTF-8"), publicKey, keyLength, reserveSize, cipherAlgorithm);
            encodeData = new String(Base64.encodeBase64(cipherText));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodeData;
    }

    /**
     * 解密入口
     *
     * @param data
     * @return
     */
    public static String decode(String data) {
        byte[] plainText;
        String decodeData = null;
        try {
            plainText = Rsa._decrypt(Base64.decodeBase64(data), privateKey, keyLength, reserveSize, cipherAlgorithm);// 解密
            decodeData = new String(plainText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decodeData;
    }


    /**
     * 加密
     *
     * @param plainBytes      明文字节数组
     * @param publicKey       公钥
     * @param keyLength       密钥bit长度
     * @param reserveSize     padding填充字节数，预留11字节
     * @param cipherAlgorithm 加解密算法，一般为RSA/ECB/PKCS1Padding
     * @return 加密后字节数组，不经base64编码
     * @throws Exception
     */
    public static byte[] _encrypt(byte[] plainBytes, PublicKey publicKey, int keyLength, int reserveSize, String cipherAlgorithm) throws Exception {
        int keyByteSize = keyLength / 8; // 密钥字节数
        int encryptBlockSize = keyByteSize - reserveSize; // 加密块大小=密钥字节数-padding填充字节数
        int nBlock = plainBytes.length / encryptBlockSize;// 计算分段加密的block数，向上取整
        if ((plainBytes.length % encryptBlockSize) != 0) { // 余数非0，block数再加1
            nBlock += 1;
        }
        try {
            Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            // 输出buffer，大小为nBlock个keyByteSize
            ByteArrayOutputStream outbuf = new ByteArrayOutputStream(nBlock * keyByteSize);
            // 分段加密
            for (int offset = 0; offset < plainBytes.length; offset += encryptBlockSize) {
                int inputLen = plainBytes.length - offset;
                if (inputLen > encryptBlockSize) {
                    inputLen = encryptBlockSize;
                }
                // 得到分段加密结果
                byte[] encryptedBlock = cipher.doFinal(plainBytes, offset, inputLen);
                // 追加结果到输出buffer中
                outbuf.write(encryptedBlock);
            }
            outbuf.flush();
            outbuf.close();
            return outbuf.toByteArray();
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(String.format("没有[%s]此类加密算法", cipherAlgorithm));
        } catch (NoSuchPaddingException e) {
            throw new Exception(String.format("没有[%s]此类填充模式", cipherAlgorithm));
        } catch (InvalidKeyException e) {
            throw new Exception("无效密钥");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("加密块大小不合法");
        } catch (BadPaddingException e) {
            throw new Exception("错误填充模式");
        } catch (IOException e) {
            throw new Exception("字节输出流异常");
        }
    }

    /**
     * RSA解密
     *
     * @param encryptedBytes  加密后字节数组
     * @param privateKey      私钥
     * @param keyLength       密钥bit长度
     * @param reserveSize     padding填充字节数，预留11字节
     * @param cipherAlgorithm 加解密算法，一般为RSA/ECB/PKCS1Padding
     * @return 解密后字节数组，不经base64编码
     * @throws Exception
     */
    public static byte[] _decrypt(byte[] encryptedBytes, PrivateKey privateKey, int keyLength, int reserveSize, String cipherAlgorithm) throws Exception {
        int keyByteSize = keyLength / 8; // 密钥字节数
        int decryptBlockSize = keyByteSize - reserveSize; // 解密块大小=密钥字节数-padding填充字节数
        int nBlock = encryptedBytes.length / keyByteSize;// 计算分段解密的block数，理论上能整除
        try {
            Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            // 输出buffer，大小为nBlock个decryptBlockSize
            ByteArrayOutputStream outbuf = new ByteArrayOutputStream(nBlock * decryptBlockSize);
            // 分段解密
            for (int offset = 0; offset < encryptedBytes.length; offset += keyByteSize) {
                // block大小: decryptBlock 或 剩余字节数
                int inputLen = encryptedBytes.length - offset;
                if (inputLen > keyByteSize) {
                    inputLen = keyByteSize;
                }
                // 得到分段解密结果
                byte[] decryptedBlock = cipher.doFinal(encryptedBytes, offset, inputLen);
                // 追加结果到输出buffer中
                outbuf.write(decryptedBlock);
            }
            outbuf.flush();
            outbuf.close();
            return outbuf.toByteArray();
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(String.format("没有[%s]此类解密算法", cipherAlgorithm));
        } catch (NoSuchPaddingException e) {
            throw new Exception(String.format("没有[%s]此类填充模式", cipherAlgorithm));
        } catch (InvalidKeyException e) {
            throw new Exception("无效密钥");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("解密块大小不合法");
        } catch (BadPaddingException e) {
            throw new Exception("错误填充模式");
        } catch (IOException e) {
            throw new Exception("字节输出流异常");
        }
    }


    /**
     * 数字签名函数
     *
     * @param plainBytes    待签名明文字节数组
     * @param privateKey    签名使用私钥
     * @param signAlgorithm 签名算法
     * @return 签名后的字节数组
     * @throws Exception
     */
    public static byte[] _digitalSign(byte[] plainBytes, PrivateKey privateKey, String signAlgorithm) throws Exception {
        try {
            Signature signature = Signature.getInstance(signAlgorithm);
            signature.initSign(privateKey);
            signature.update(plainBytes);
            byte[] signBytes = signature.sign();
            return signBytes;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(String.format("数字签名时没有[%s]此类算法", signAlgorithm));
        } catch (InvalidKeyException e) {
            throw new Exception("数字签名时私钥无效");
        } catch (SignatureException e) {
            throw new Exception("数字签名时出现异常");
        }
    }


    /**
     * 验证数字签名函数
     *
     * @param plainBytes    待验签明文字节数组
     * @param signBytes     待验签签名后字节数组
     * @param publicKey     验签使用公钥
     * @param signAlgorithm 签名算法
     * @return 验签是否通过
     * @throws Exception
     */
    public static boolean _verifyDigitalSign(byte[] plainBytes, byte[] signBytes, PublicKey publicKey, String signAlgorithm) throws Exception {
        boolean isValid = false;
        try {
            Signature signature = Signature.getInstance(signAlgorithm);
            signature.initVerify(publicKey);
            signature.update(plainBytes);
            isValid = signature.verify(signBytes);
            return isValid;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(String.format("验证数字签名时没有[%s]此类算法", signAlgorithm));
        } catch (InvalidKeyException e) {
            throw new Exception("验证数字签名时公钥无效");
        } catch (SignatureException e) {
            throw new Exception("验证数字签名时出现异常");
        }
    }

    /**
     * 二行制转十六进制字符串
     *
     * @param b 2进制byte数组
     * @return 16进制字符串
     */
    private static String byte2hex(byte[] b) {

        StringBuffer sb = new StringBuffer();
        String stmp;
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                sb.append("0" + stmp);
            else
                sb.append(stmp);
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 十六进制转二行制
     *
     * @param b 字节数组
     * @return 字节数组
     */
    public static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException("SecretUtil.hex2byte() length is not even number!");
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    /**
     * Test the Rsa
     */
    public static void main(String[] args) {

        try {
            final String originalText = "RSA公钥加密算法是1977年由罗纳德·李维斯特（Ron Rivest）、阿迪·萨莫尔（Adi Shamir）和伦纳德·阿德曼（Leonard Adleman）一起提出的。" +
                    "今天只有短的RSA钥匙才可能被强力方式解破。到2008年为止，世界上还没有任何可靠的攻击RSA算法的方式。只要其钥匙的长度足够长，用RSA加密的信息实际上是不能被解破的。";
            //测试
            String code1 = encode(originalText);
            String code2 = decode(code1);
            System.out.println("origin:" + originalText);
            System.out.println("encry:" + code1.toString());
            System.out.println("decry:" + code2.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
