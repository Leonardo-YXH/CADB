package cn.npt.util.algorithm.signal.correlation;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;

/**
 * 相关性分析
 * @author leonardo
 *
 */
public class Correlation {
	/**
	 * 一维自相关
	 * @param X
	 * @return
	 */
	public static double[] correlate_dim1_dbl(double[] X){
		double[] Y=new double[2*X.length-1];
		for(int i=0,j=i-X.length+1;i<Y.length;i++,j++){
			Y[i]=0;
			for(int k=0;k<X.length;k++){
				int jk=j+k;
				if(jk>=0&&jk<X.length){
					Y[i]+=X[k]*X[jk];
				}
			}
			//System.out.println(Y[i]);
		}
		return Y;
	}
	
	
	public static void main(String[] args) {
		double[] X=new double[]{1,2,3};
		correlate_dim1_dbl(X);
	}
}
