package cn.npt.util.algorithm.signal.cepstrum;

import cn.npt.util.algorithm.transform.ComplexNumber;
import cn.npt.util.algorithm.transform.FourierTransform;
import cn.npt.util.algorithm.transform.FourierTransform.Direction;

/**
 * 倒谱(复数的倒谱暂时未实现)
 * @author Leonardo
 *
 */
public class Cepstrum {

	/**
	 * 实数倒谱y=real(ifft(ln(abs(fft(x)))))
	 * @param X
	 * @return
	 */
	public static double[] rceps(double[] X){
		ComplexNumber[] Y=FourierTransform.double2Complex(X);
		FourierTransform.FFT(Y, Direction.Forward);
		for(ComplexNumber y:Y){
			//y.real=Math.log(Math.abs(y.real));
			y.real=Math.log(ComplexNumber.Abs(y));
			y.imaginary=0;
		}
		FourierTransform.FFT(Y, Direction.Backward);
		double[] r=new double[Y.length];
		for(int i=0;i<Y.length;i++){
			r[i]=ComplexNumber.Abs(Y[i]);
		}
		return r;
	}
	/**
	 * 复数倒谱(待完成)
	 * <br>h=fft(x)
	 * <br>logh=log(abs(h))+sqrt(-1)*rcunwrap(angle(h))
	 * <br>y=real(ifft(logh))
	 * @param X
	 * @return
	 */
	public static ComplexNumber[] cceps(ComplexNumber[] X){
		//TODO
		
		return null;
	}
	
	
	public static void main(String[] args) {
		double[] X=new double[]{1,2,3,4,5,6};
		rceps(X);
		
		System.out.println();
	}
}
