package cn.npt.util.algorithm.signal.cepstrum;

import cn.npt.util.algorithm.transform.ComplexNumber;
import cn.npt.util.algorithm.transform.FourierTransform;
import cn.npt.util.algorithm.transform.FourierTransform.Direction;

/**
 * 功率谱
 * @author leonardo
 *
 */
public class PowerSpectrum {

	/**
	 * 功率谱Sxx=|F(X)|^2/N^2
	 * @param X
	 * @return
	 */
	public static double[] powerSpectrum(double[] X){
		ComplexNumber[] data=FourierTransform.double2Complex(X);
		FourierTransform.FFT(data, Direction.Forward);
		double[] Y=new double[X.length];
		for(int i=0,len=data.length,nsquar=len*len;i<len;i++){
			Y[i]=data[i].getSquaredMagnitude()/nsquar;
			//System.out.println(Y[i]);
		}
		return Y;
	}
	/**
	 * 倒谱
	 * @param X
	 * @return
	 */
	public static double[] cepstrum(double[] X){
		double[] Y=null;
		Y=powerSpectrum(X);
		ComplexNumber[] data=new ComplexNumber[X.length-1];
		for(int i=1;i<Y.length;i++){
			data[i-1]=new ComplexNumber(Math.log(Y[i]),0);
		}
		
		FourierTransform.FFT(data, Direction.Backward);
		for(int i=0;i<data.length;i++){
			Y[i]=data[i].getMagnitude();
			System.out.println(Y[i]);
		}
		Y[0]=0;
		return Y;
	}
	/**
	 * 去均值
	 * @param X
	 * @return
	 */
	public static double[] fmeanfilt(double[] X){
		double sum=0;
		for(double x:X){
			sum+=x;
		}
		double mean=sum/X.length;
		double[] Y=new double[X.length];
		for(int i=0;i<X.length;i++){
			Y[i]=X[i]-mean;
		}
		return Y;
	}
	public static void main(String[] args) {
		double[] X=new double[]{1d,2d,3d,4d};
		//powerSpectrum(X);
		cepstrum(X);
	}
}
