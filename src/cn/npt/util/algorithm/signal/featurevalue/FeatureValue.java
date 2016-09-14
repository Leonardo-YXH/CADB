package cn.npt.util.algorithm.signal.featurevalue;
/**
 * 特征值计算
 * @author leonardo
 *
 */
public class FeatureValue {

	/**
	 * 均方根
	 * @param X
	 * @return
	 */
	public static double rms(double[] X){
		double sum=0;
		for(double x:X){
			sum+=x*x;
		}
		sum/=X.length;
		return Math.sqrt(sum);
	}
	/**
	 * 找出数组中绝对值最大的;并加和
	 * @param X
	 * @return [数组中绝对值最大的,并加和]
	 */
	public static double[] maxAbs(double[] X){
		double maxX=Double.MIN_VALUE;
		double minX=Double.MAX_VALUE;
		for(double x:X){
			if(maxX<x){
				maxX=x;
			}
			if(minX>x){
				minX=x;
			}
		}
		//maxX=Math.abs(maxX);//假如maxX<0,???
		minX=Math.abs(minX);
		double[] rs=new double[2];
		
		rs[0]=maxX>minX?maxX:minX;
		rs[1]=maxX+minX;
		return rs;
	}
}
