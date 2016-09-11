package cn.npt.util.algorithm.array;

/**
 * 
 * @author Leonardo
 *
 */
public class ArrayUtil {

	public static double[] shiftDim(double[] src,int dim1Size){
		double[] dest=new double[dim1Size];
		if(src.length>=dim1Size){
			System.arraycopy(src, 0, dest, 0, dim1Size);
		}
		else{
			System.arraycopy(src, 0, dest, 0, src.length);
			
			for(int i=src.length;i<dim1Size;i++){
				dest[i]=0;
			}
		}
		return dest;
	}
	
	public static double[][] shiftDim(double[] src,int dim1Size,int dim2Size){
		double[][] dest = new double[dim1Size][dim2Size];
		for(int i=0;i<dim1Size;i++){
			for(int j=0;j<dim2Size;j++){
				int index=i*dim2Size+j;
				if(index<src.length){
					dest[i][j]=src[index];
				}
				else{
					dest[i][j]=0;
				}
			}
		}
		return dest;
	}
	
	public static double[][][] shiftDim(double[] src,int dim1Size,int dim2Size,int dim3Size){
		double[][][] dest=new double[dim1Size][dim2Size][dim3Size];
		for(int i=0;i<dim1Size;i++){
			for(int j=0;j<dim2Size;j++){
				for(int k=0;k<dim3Size;k++){
					int index=i*dim2Size+j*dim3Size+k;
					if(index<src.length){
						dest[i][j][k]=src[index];
					}
					else{
						dest[i][j][k]=0;
					}
				}
			}
		}
		return dest;
	}
	
	
	
}
