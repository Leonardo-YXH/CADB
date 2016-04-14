package cn.npt.util.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import com.google.common.base.Preconditions;
/**
 * 对文件的各种操作
 * @author leonardo_yang
 *
 */
public class FileOperator {

	public static boolean createFolder(String path){
		File file=new File(path);
		if(!file.exists()){
			return file.mkdirs();
		}
		return false;
	}
	public static boolean createNewFile(String fileName){
		File file=new File(fileName);
		String path=file.getParent();
		if(createFolder(path)){
			if(!file.exists()){
				try {
					return file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	/**
	 * 区别于createNewFile,返回文件句柄
	 * @param fileName
	 * @return File
	 */
	public static File createFile(String fileName){
		File file=new File(fileName);
		if(file.isDirectory()){
			createFolder(fileName);
			return file;
		}
		String path=file.getParent();
		if(createFolder(path)){
			if(!file.exists()){
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return file;
	}
	/**
	 * 拷贝文件内容
	 * @param srcFile
	 * @param destFile
	 */
	public static void copyFile(String srcFile,String destFile){
		//String content=FileReaderUtil.readAllText(srcFile);
		//write(destFile,content);
		try {
			FileInputStream fis=new FileInputStream(new File(srcFile));
			FileOutputStream fos=new FileOutputStream(createFile(destFile));
			BufferedInputStream bis=new BufferedInputStream(fis);
			BufferedOutputStream bos=new BufferedOutputStream(fos);
			byte[] content=new byte[1024*10];
			int len=0;
			while(-1!=(len=bis.read(content))){
				bos.write(content,0,len);
			}
			fis.close();
			fos.close();
			bos.flush();
			bis.close();
			bos.close();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	public static void write(String fileName,String content){
		File file=new File(fileName);
		try {
			if(!file.exists()){
				createNewFile(fileName);
			}
			FileWriter fw=new FileWriter(file);
			fw.write(content);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}	
	}
	public static void append(String fileName,String content){
		File file=new File(fileName);
		try {
			if(!file.exists()){
				createNewFile(fileName);
			}
			FileWriter fw=new FileWriter(file,true);
			
			fw.append(content);
			fw.flush();
			fw.close();
		} catch (IOException e) {
		
			e.printStackTrace();
		}	
	}
	/**
	 * 拷贝某个目录下及子目录下的所有的文件到其他的workspace中去
	 * @param srcFolder 要拷贝的文件父目录
	 * @param destWorkSpace 目标父目录
	 * @param isRoot 表示是否把当前根目录也拷贝，true:拷贝，false:不拷贝
	 */
	public static void copyFolderAndAllChild(String srcFolder,String destWorkSpace,boolean isRoot){
		File file=new File(srcFolder);
		//不允许将父目录拷贝到子目录中
		if(isChild(file,new File(destWorkSpace))){
			return ;
		}
		if(isRoot){
			destWorkSpace=destWorkSpace+File.separator+file.getName();
		}
		if(file.isDirectory()){
			File[] fileList=file.listFiles();
			for(File fileItem:fileList){
				if(fileItem.isFile()){
					String destFile=destWorkSpace+File.separator+fileItem.getName();
					copyFile(fileItem.getAbsolutePath(), destFile);
				}
				else{
					String src=fileItem.getAbsolutePath();
					copyFolderAndAllChild(src, destWorkSpace, true);
				}
			}
		}
	}
	/**
	 * 判断目标目录是否为源目录的子目录
	 * @param src
	 * @param dest
	 */
	public static boolean isChild(File src,File dest){
		if(dest.getAbsolutePath().startsWith(src.getAbsolutePath())){
			return dest.getParentFile().getAbsolutePath().startsWith(src.getAbsolutePath())?true:false;
		}
		return false;
	}
	/**
	 * 删除文件，如果是文件夹则递归删除子文件及子文件夹
	 * @param file
	 */
	public static void delete(File file){
		if(file.exists()){
			if(file.isFile()){
				file.delete();
			}
			else{
				File[] listf=file.listFiles();
				for (File file2 : listf) {
					if(file2.isFile()){
						file2.delete();
					}
					else{
						delete(file2);
					}
				}
				file.delete();
			}
		}
	}
	public static void deleteRecursively(File file)
		    throws IOException{
		if (file.isDirectory()) {
			deleteDirectoryContents(file);
		}
		if (!(file.delete()))
			throw new IOException("Failed to delete " + file);
	}
	/**
	 * 删除文件夹
	 * @param directory
	 * @throws IOException
	 */
	public static void deleteDirectoryContents(File directory)
	    throws IOException{
	    Preconditions.checkArgument(directory.isDirectory(), "Not a directory: %s", new Object[] { directory });

	    if (!(directory.getCanonicalPath().equals(directory.getAbsolutePath()))) {
	    	return;
	    }
	    File[] files = directory.listFiles();
	    if (files == null) {
	    	throw new IOException("Error listing files for " + directory);
	    }
	    for (File file : files)
	    	deleteRecursively(file);
	 }
}
