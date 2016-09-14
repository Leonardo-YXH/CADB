package cn.npt.util.algorithm.compress;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * zip格式压缩和解压(不依赖第三方jar,jdk原生api)
 * @author Leonardo
 * @see ZipUtilUseAnt
 */
public class ZipUtil4Npt {

	/**
	 * 压缩文件或者文件夹
	 * @param sourceFile
	 * @param destPath
	 * @see #zipUseAnt(String, String)
	 */
	public static void zip(String sourceFile,String destPath){
		BufferedOutputStream bos=null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(destPath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		ZipOutputStream zos=new ZipOutputStream(bos);
		File file=new File(sourceFile);
		if(file.isFile()){
			compressFile(zos, file, "");
		}
		else{
			compressDirectory(zos, file, "");
		}
		try {
			zos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 压缩文件或者文件夹
	 * @param sourceFile
	 */
	public static void zip(String sourceFile){
		File f=new File(sourceFile);
		if(f.exists()){
			if(f.isFile()){
				String name=f.getName();
				zip(sourceFile, f.getParent()+File.separator+name.substring(0, name.lastIndexOf("."))+".zip");
			}
			else{
				zip(sourceFile, f.getAbsolutePath()+".zip");
			}
		}
	}
	/**
	 * 递归压缩文件夹
	 * @param zos
	 * @param source
	 * @param baseDir
	 */
	private static void compressDirectory(ZipOutputStream zos,File source,String baseDir){
		File[] listFile=source.listFiles();
		for(File f:listFile){
			if(f.isFile()){
				compressFile(zos, f, baseDir);
			}
			else{
				compressDirectory(zos, f, baseDir+File.separator+f.getName());
			}
		}
	}
	/**
	 * 压缩文件
	 * @param zos
	 * @param source
	 * @param baseDir
	 */
	private static void compressFile(ZipOutputStream zos,File source,String baseDir){
		ZipEntry zipEntry=new ZipEntry(baseDir+File.separator+source.getName());
		try {
			zos.putNextEntry(zipEntry);
			
			BufferedInputStream bis=new BufferedInputStream(new FileInputStream(source));
			int length=1024;
			byte[] buf=new byte[length];
			while((length=bis.read(buf))!=-1){
				zos.write(buf, 0, length);
			}
			bis.close();
			zos.closeEntry();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	/**
	 * 解压文件
	 * @param file 要解压的文件
	 * @param destPath 目标路径.eg:D://data
	 */
	public static void unZip(String file,String destPath){
		try {
			BufferedInputStream bis=new BufferedInputStream(new FileInputStream(file));
			ZipInputStream zis=new ZipInputStream(bis);
			BufferedOutputStream bos=null;
			ZipEntry zipEntry=null;
			int length=1024;
			byte[] buf=new byte[length];
			while((zipEntry=zis.getNextEntry())!=null){
				String entryName=zipEntry.getName();
				File ef=new File(destPath+File.separator+entryName);
				
				if(zipEntry.isDirectory()){
					if(!ef.exists()){
						ef.mkdirs();
					}
				}
				else{
					bos=new BufferedOutputStream(new FileOutputStream(ef));
					while((length=zis.read(buf))!=-1){
						bos.write(buf, 0, length);
					}
					bos.flush();
					bos.close();
				}
				zis.closeEntry();
			}
			zis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 将file解压到当前文件夹
	 * @param file 要解压的文件
	 * @param isRoot 是否以该文件名为根路径名（默认为true） eg:data.zip解压后/data/...
	 */
	public static void unZip(String file,boolean isRoot){
		File f=new File(file);
		if(f.exists()&&f.isFile()){
			if(isRoot){
				String name=f.getName();
				unZip(file, f.getParent()+File.separator+name.substring(0,name.lastIndexOf(".")));
			}
			else{
				unZip(file, f.getParent());
			}
		}
	}
	
}
