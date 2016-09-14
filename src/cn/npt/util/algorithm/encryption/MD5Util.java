package cn.npt.util.algorithm.encryption;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密
 * @author leonardo_yang
 * @version 2014-09-03 v1.0
 */
public class MD5Util {

	/**
	 * 对字符串进行MD5加密
	 * @param inputFile 需要加密的字符串
	 * @param digit 输出密文为16位或者32位
	 * @return
	 */
	public static String stringMD5(String input,int digit) {

		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] inputByteArray = input.getBytes();
			messageDigest.update(inputByteArray);
			byte[] resultByteArray = messageDigest.digest();
			String result=byteArrayToHex(resultByteArray);
			if(digit==16){
				return result.substring(8, 24);
			}
			return result;
		} catch (NoSuchAlgorithmException e) {
			return null;		
		}
	}
	private static String byteArrayToHex(byte[] byteArray) {
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		char[] resultCharArray = new char[byteArray.length * 2];
		int index = 0;
		for (byte b : byteArray) {
			resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
			resultCharArray[index++] = hexDigits[b & 0xf];
		}
		return new String(resultCharArray);
	}
	/**
	 * 对文件进行MD5加密
	 * @param inputFile 需要加密的文件
	 * @param digit 输出密文为16位或者32位
	 * @return
	 */
	public static String fileMD5(String inputFile,int digit) {
		int bufferSize = 256 * 1024;
		FileInputStream fileInputStream = null;
		DigestInputStream digestInputStream = null;
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			try {
				fileInputStream = new FileInputStream(inputFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			digestInputStream = new DigestInputStream(fileInputStream,
					messageDigest);
			byte[] buffer = new byte[bufferSize];
			try {
				while (digestInputStream.read(buffer) > 0)
					;
			} catch (IOException e) {
				e.printStackTrace();
			}
			messageDigest = digestInputStream.getMessageDigest();
			byte[] resultByteArray = messageDigest.digest();
			String result=byteArrayToHex(resultByteArray);
			if(digit==16){
				return result.substring(8, 24);
			}
			return result;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				digestInputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				fileInputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
