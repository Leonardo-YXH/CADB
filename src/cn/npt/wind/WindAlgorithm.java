package cn.npt.wind;

import cn.npt.util.algorithm.array.ArrayUtil;
import cn.npt.util.algorithm.integration.Integration;
import cn.npt.util.algorithm.integration.NumericIntegration;
import cn.npt.util.algorithm.signal.cepstrum.Cepstrum;
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
	public static void main(String[] args) {
		double[] X=new double[9];
		for(int i=0;i<9;i++){
			X[i]=i+1;
		}
		double[] Y=envelope(X);
		for(double v:Y){
			//System.out.println(v+" ");
		}
		double[] Z=Cepstrum.rceps(X);
		for (int i = 0; i < Z.length; i++) {
			System.out.println(Z[i]);
		}
		
	}
}
