package cn.npt.util.algorithm.compress;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;
/**
 * zip压缩,解压（依赖ant.jar）
 * @author Leonardo
 * @see ZipUtil4Npt
 */
public class ZipUtilUseAnt {

	/**
	 * 压缩文件
	 * @param sourceFile 要压缩的原始文件或文件夹
	 * @param destPath 目标文件名
	 */
	public static void zipUseAnt(String sourceFile,String destPath){
		Project prj=new Project();
		Zip zip=new Zip();
		zip.setProject(prj);
		zip.setDestFile(new File(destPath));
		FileSet fileSet=new FileSet();
		fileSet.setProject(prj);
		File source=new File(sourceFile);
		if(source.isDirectory()){
			fileSet.setDir(source);
		}
		else{
			fileSet.setFile(source);
		}
		zip.addFileset(fileSet);
		zip.execute();
	}
	/**
	 * 压缩文件或文件夹到当前路径
	 * @param sourceFile 要压缩的原始文件或文件夹
	 */
	public static void zipUseAnt(String sourceFile){
		File f=new File(sourceFile);
		if(f.exists()){
			if(f.isFile()){
				String name=f.getName();
				zipUseAnt(sourceFile, f.getParent()+File.separator+name.substring(0, name.lastIndexOf("."))+".zip");
			}
			else{
				zipUseAnt(sourceFile, f.getAbsolutePath()+".zip");
			}
		}
	}
	/**
	 * 解压文件
	 * @param file 要解压的文件
	 * @param destPath 目标路径
	 */
	public static void unZipUseAnt(String file,String destPath){
		Project prj=new Project();
		Expand expand=new Expand();
		expand.setProject(prj);
		expand.setSrc(new File(file));
		expand.setOverwrite(false);
		expand.setDest(new File(destPath));
		expand.execute();
	}
	
	/**
	 * 将file解压到当前文件夹
	 * @param file 要解压的文件
	 * @param isRoot 是否以该文件名为根路径名（默认为true） eg:data.zip解压后/data/...
	 * @see #unZipUseAnt(String, String)
	 */
	public static void unZipUseAnt(String file,boolean isRoot){
		File f=new File(file);
		if(f.exists()&&f.isFile()){
			if(isRoot){
				String name=f.getName();
				unZipUseAnt(file, f.getParent()+File.separator+name.substring(0,name.lastIndexOf(".")));
			}
			else{
				unZipUseAnt(file, f.getParent());
			}
		}
	}
}
