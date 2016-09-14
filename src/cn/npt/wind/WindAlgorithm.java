package cn.npt.wind;

import java.util.ArrayList;
import java.util.List;

import cn.npt.util.algorithm.array.ArrayUtil;
import cn.npt.util.algorithm.integration.Integration;
import cn.npt.util.algorithm.integration.NumericIntegration;
import cn.npt.util.algorithm.signal.cepstrum.Cepstrum;
import cn.npt.util.algorithm.signal.correlation.Correlation;
import cn.npt.util.algorithm.signal.featurevalue.FeatureValue;
import cn.npt.util.algorithm.signal.probableDensity.ProbableDensity;
import cn.npt.util.algorithm.signal.trend.Detrend;
import cn.npt.util.algorithm.signal.zoomdlg.ZoomDlg;
import cn.npt.util.algorithm.transform.FourierTransform.Direction;
import cn.npt.util.algorithm.transform.HilbertTransform;

/**
 * 汉能华
 * @author Leonardo
 *
 */
public class WindAlgorithm {

	/**
	 * 积分
	 * @param X
	 * @return
	 */
	public static double[] integrate(double[] X){
		double[] rs=Integration.simpson(X);
		int length=rs.length;
		double start=1,end=length;
		double[] x=NumericIntegration.rampSignal(length, start, end, true, 0);
		double[] kc=NumericIntegration.Inclination(x, rs);
		
		for(int i=0;i<length;i++){
			rs[i]=rs[i]-(kc[0]*x[i]+kc[1]);
		}
		return rs;
	}
	/**
	 * 包络
	 * @param X
	 * @return
	 */
	public static double[] envelope(double[] X){
		double[] Y=new double[X.length];
		double sum=0;
		for(double v:X){
			sum+=v;
		}
		double mean=sum/X.length;
		for(int i=0;i<X.length;i++){
			Y[i]=X[i]-mean;
		}
		double[] Yi=new double[Y.length];
		System.arraycopy(Y, 0, Yi, 0, Y.length);
		HilbertTransform.FHT(Yi, Direction.Forward);
		
		for(int i=0;i<Yi.length;i++){
			//System.out.println(Yi[i]);
			Yi[i]=Math.sqrt(Yi[i]*Yi[i]+Y[i]*Y[i]);
		}
		//ComplexNumber[] data=FourierTransform.double2Complex(Yi);
		//FourierTransform.FFT(data, Direction.Forward);
		for(int i=0;i<X.length;i++){
			//Y[i]=ComplexNumber.Abs(data[i]);
			Y[i]=Yi[i]/X.length;
		}
		double[] Y1=ArrayUtil.shiftDim(Y, X.length/2);
		for(int i=0;i<Y1.length;i++){
			Y1[i]*=2;
		}
		Y1[0]=0;
		return Y1;
	}
	/**
	 * 包络
	 * @param dt
	 * @param length
	 * @return
	 */
	public static double envelopeDt(double dt,int length){
		
		return 1/(dt*length);
	}
	/**
	 * 一维自相关
	 * @param X
	 * @return
	 */
	public static double[] correlation(double[] X){
		return Correlation.correlate_dim1_dbl(X);
	}
	/**
	 * 一维自相关第一个簇元素
	 * @param xLength
	 * @param dt
	 * @return
	 */
	public static double correlation_first_elem(int xLength,double dt){
		return (1-xLength)*dt;
	}
	/**
	 * 去趋势
	 * @param X
	 * @param method 计算方法0--linear；1--quadratic；2--cube
	 * @return
	 */
	public static double[] trend(double[] X,int method){
		double[] X1=Integration.rampSignal(X.length, 1, X.length, false, 0);
		switch(method){
		case 0:{
			double[] coefficient=Detrend.polynomialFitter(X1, X, 1);
			return Detrend.polynomialY(X1, coefficient);
		}
		case 1:{
			double[] coefficient=Detrend.polynomialFitter(X1, X, 2);
			return Detrend.polynomialY(X1, coefficient);
		}
		case 2:{
			double[] coefficient=Detrend.polynomialFitter(X1, X, 3);
			return Detrend.polynomialY(X1, coefficient);
		}
		default:{
			double[] coefficient=Detrend.polynomialFitter(X1, X, 1);
			return Detrend.polynomialY(X1, coefficient);
		}
		}
	}
	/**
	 * 去趋势
	 * @param X 原始值
	 * @param polynomialY 拟合后的值
	 * @return X-polynomialY
	 */
	public static double[] detrend(double[] X,double[] polynomialY){
		double[] Y=new double[X.length];
		for (int i = 0; i < Y.length; i++) {
			Y[i]=X[i]-polynomialY[i];
		}
		return Y;
	}
	/**
	 * 去趋势
	 * @param X
	 * @param method 计算方法0--linear（去一次）；1--quadratic（去二次）；2--cube（去三次）
	 * @return
	 */
	public static double[] detrend_cal(double[] X,int method){
		double[] py=trend(X, method);
		return detrend(X, py);
	}
	/**
	 * 特征值计算
	 * @param X
	 * @return 三个元素：[max(abs(X)),max(X)+abs(min(X)),rms]
	 */
	public static List<Double> static_calc(double[] X){
		List<Double> rs=new ArrayList<Double>();
		double[] rt=FeatureValue.maxAbs(X);
		rs.add(rt[0]);
		rs.add(rt[1]);
		rs.add(FeatureValue.rms(X));
		return rs;
	}
	/**
	 * 细化分析
	 * @param sampleFreq 采样频率
	 * @param centerFreq 中心频率
	 * @param zoom 细化倍数
	 * @return 上截止频率，下截止频率
	 */
	public static List<Double> zoomDlg(double sampleFreq,double centerFreq,double zoom){
		return ZoomDlg.zoomdlg(sampleFreq, centerFreq, zoom);
	}
	public static void main(String[] args) {
		double[] X=new double[]{1d,3d,-5d,4d};
		double[] Y=null;
		List<Double> Ylist=null;
		
		//your test code
		//Y=detrend_cal(X, 2);
		//Ylist=ProbableDensity.probableDensity(X, 6, 0);
		//Ylist=static_calc(X);
		Ylist=zoomDlg(2000, 3000, 2);
		//print Y
//		for (int i = 0; i < Y.length; i++) {
//			System.out.println(Y[i]);
//		}
		for(Double y:Ylist){
			System.out.println(y);
		}
	}
}
