package cn.npt.util.algorithm.signal.trend;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

/**
 * 去趋势
 * @author leonardo
 *
 */
public class Detrend {

	/**
	 * 去趋势
	 * @param X
	 * @param method 计算方法0--linear；1--quadratic；2--cube
	 * @return
	 */
	public static double[] detrend(double[] X,int method){
		
		return null;
	}
	/**
	 * 去一次
	 * @param X
	 * @return
	 */
	public static double[] detrend_cal(double[] X){
		
		return null;
	}
	/**
	 * 去高次
	 * @param X
	 * @return
	 */
	public static double[] detrend_order(double[] X){
	
		return null;
	}
	/**
	 * 广义多项式拟合（最小二乘法）
	 * @param X
	 * @param Y
	 * @param degree 阶数
	 * @return 多项式系数，按阶数升序
	 */
	public static double[] polynomialFitter(double[] X,double[] Y,int degree){
		WeightedObservedPoints wps=new WeightedObservedPoints();
		int length=X.length>Y.length?Y.length:X.length;
		for(int i=0;i<length;i++){
			wps.add(X[i], Y[i]);
		}
		PolynomialCurveFitter pcf=PolynomialCurveFitter.create(degree);
		double[] coefficient=pcf.fit(wps.toList());
		return coefficient;
	}
	/**
	 * 多项式拟合的值
	 * @param X
	 * @param coefficient 多项式系数，按阶数升序
	 * @return
	 */
	public static double[] polynomialY(double[] X,double[] coefficient){
		double[] Y=new double[X.length];
		for(int i=0;i<X.length;i++){
			Y[i]=0;
			for(int j=coefficient.length-1;j>0;j--){
				Y[i]*=X[i];
				Y[i]+=X[i]*coefficient[j];
			}
			Y[i]+=coefficient[0];//最后的常数项
		}
		return Y;
	}
	public static void main(String[] args) {
		double[] X=new double[]{1,2,3};
		double[] Y=new double[]{3,5,7};
		double[] cs=polynomialFitter(X, Y, 2);
		double[] Y1=polynomialY(X, cs);
		for (int i = 0; i < Y1.length; i++) {
			System.out.println(Y1[i]);
		}
	}
}
