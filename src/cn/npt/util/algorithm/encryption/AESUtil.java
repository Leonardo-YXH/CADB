package cn.npt.util.algorithm.encryption;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
/**
 * AES加密解密，采用CBC算法PKCS5模式填充
 * @author Leonardo
 *
 */
public class AESUtil {

	/**
	 * 加密
	 * @param data utf-8编码的byte数组
	 * @param password 密钥
	 * @return
	 */
	public static byte[] enCoder(byte[] data,String password){
		try {
            SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");// 转换为AES专用密钥

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// 创建密码器
            IvParameterSpec iv = new IvParameterSpec(password.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度  
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);// 初始化为加密模式的密码器

            byte[] result = cipher.doFinal(data);// 加密

            return result;

        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
        return null;
	}
	/**
	 * 
	 * @param data
	 * @param password
	 * @return
	 */
	public static byte[] deCoder(byte[] data,String password){
		try {
            SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");// 转换为AES专用密钥
            IvParameterSpec iv = new IvParameterSpec(password.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度  
            
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// 创建密码器
            
            cipher.init(Cipher.DECRYPT_MODE, key, iv);// 初始化为解密模式的密码器
            byte[] result = cipher.doFinal(data);  
            return result; // 明文   
            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
        return null;
	}
	
	public static void main(String[] args) {
		String content="hello,world!";
		String password="chinashanghaijky";
		try {
			byte[] data=content.getBytes("utf-8");
			byte[] encryptionData=enCoder(data, password);
			System.out.println("encoder:");
			for(byte b:encryptionData){
				System.out.print(b+",");
			}
			byte[] decryptionData=deCoder(encryptionData, password);
			System.out.println("\ndecoder:"+new String(decryptionData));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
