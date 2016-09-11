package cn.npt.util.algorithm.integration;

/**
 * 一元离散数值积分，采样点X均匀分布
 * @author Leonardo
 *
 */
public class Integration {

	
	/**
	 * 梯形计算公式
	 * @param X
	 * @return
	 */
	public static double[] _trapz(double[] X){
		double[] Y=new double[X.length];
		Y[0]=X[0]/2;
		for(int i=1;i<X.length;i++){
			Y[i]=(X[i-1]+X[i])/2;
		}
		return Y;
	}
	/**
	 * 梯形计算公式
	 * @param X
	 * @return
	 */
	public static double[] trapz(double[] X){
		return accmulate(_trapz(X));
	}
	/**
	 * simpson
	 * @param X
	 * @return
	 */
	public static double[] _simpson(double[] X){
		double[] Y=new double[X.length];
		Y[0]=(4*X[0]+X[1])/6;
		for(int i=1;i<X.length-1;i++){
			Y[i]=(X[i-1]+4*X[i]+X[i+1])/6;
		}
		Y[X.length-1]=(X[X.length-2]+4*X[X.length-1])/6;
		return Y;
	}
	
	/**
	 * simpson
	 * @param X
	 * @return
	 */
	public static double[] simpson(double[] X){
		return accmulate(_simpson(X));
	}
	/**
	 * 
	 * @param X
	 * @return
	 */
	public static double[] _simpson_3_8(double[] X){
		double[] Y=new double[X.length];
		Y[0]=(3*X[0]+X[1])/8;
		Y[1]=(3*X[0]+3*X[1]+X[2])/8;
		
		Y[X.length-1]=(X[X.length-3]+3*X[X.length-2]+3*X[X.length-1])/8;
		for(int i=0;i<X.length-3;i++){
			Y[i]=(X[i-2]+3*X[i-1]+3*X[i]+X[i+1])/8;
		}
		
		return Y;
	}
	/**
	 * simpson_3_8
	 * @param X
	 * @return
	 */
	public static double[] simpson_3_8(double[] X){
		return accmulate(_simpson_3_8(X));
	}
	/**
	 * 
	 * @param X
	 * @return
	 */
	public static double[] _bode(double[] X){
		double[] Y=new double[X.length];
		Y[0]=(12*X[0]+32*X[1]+7*X[2])/90;
		Y[1]=(32*X[0]+12*X[1]+32*X[2]+7*X[3])/90;
		for(int i=0;i<X.length-2;i++){
			Y[i]=(7*X[i-2]+32*X[i-1]+12*X[i]+32*X[i+1]+7*X[i+2])/90;
		}
		Y[X.length-1]=(7*X[X.length-3]+32*X[X.length-2]+12*X[X.length-1])/90;
		Y[X.length-2]=(7*X[X.length-4]+32*X[X.length-3]+12*X[X.length-2]+32*X[X.length-1])/90;
		return Y;
	}
	/**
	 * bode
	 * @param X
	 * @return
	 */
	public static double[] bode(double[] X){
		return accmulate(_bode(X));
	}
	/**
	 * 累加
	 * @param X
	 * @return
	 */
	private static double[] accmulate(double[] X){
		double[] Y=new double[X.length];
		Y[0]=X[0];
		for(int i=1;i<X.length;i++){
			Y[i]=Y[i-1]+X[i];
		}
		return Y;
	}
	
	/**
	 * 斜坡信号生成
	 * @param length 信号长度
	 * @param start 开始值
	 * @param end 结束值
	 * @param containEnd 是否包含末端值
	 * @param type 0--线性；1--对数
	 * @return
	 */
	public static double[] rampSignal(int length,double start,double end,boolean containEnd,int type){
		double[] rs=new double[length];
		
		if(type==0){
			if(containEnd){
				double dx=(end-start)/(length-1);
				rs[0]=start;
				for(int i=1;i<length;i++){//Xi=X0+i*dx=X(i-1)+dx
					rs[i]=rs[i-1]+dx;//减少乘法的计算
				}
			}
			else{
				double dx=(end-start)/length;
				rs[0]=start;
				for(int i=1;i<length;i++){//Xi=X0+i*dx=X(i-1)+dx
					rs[i]=rs[i-1]+dx;
				}
			}
		}
		else if(type==1){
			if(containEnd){
				double dx=(Math.log(end)-Math.log(start))/(length-1);
				double edx=Math.pow(Math.E, dx);
				rs[0]=start;
				for(int i=1;i<length;i++){//Xi=exp[ln(X0)+i*dx]=X(i-1)*exp(dx)
					rs[i]=rs[i-1]*edx;//减少对数的计算量
				}
			}
			else{
				double dx=(Math.log(end)-Math.log(start))/length;
				double edx=Math.pow(Math.E, dx);
				rs[0]=start;
				for(int i=1;i<length;i++){//Xi=exp[ln(X0)+i*dx]
					rs[i]=rs[i-1]*edx;
				}
			}
		}
		
		return rs;
	}
	
	 /**
     * 最小二乘法。y=kx+C
     * @param x Vector.
     * @param y Vector.
     * @return Inclination,Interception between the vector x and y.返回斜率k和C
     */
    public static double[] Inclination(double[] x, double[] y){
        if (x.length != y.length)
            throw new IllegalArgumentException("The size of both matrix needs be equal");
        
        double meanX = 0; double meanY = 0;
        for (int i = 0; i < x.length; i++) {
            meanX += x[i];
            meanY += y[i];
        }
        
        meanX /= x.length;
        meanY /= y.length;
        
        double num = 0, den = 0;
        for (int i = 0; i < x.length; i++) {
            num += (x[i] - meanX) * (y[i] - meanY);
            den += Math.pow((x[i] - meanX),2);
        }
        double[] rs=new double[2];
        rs[0]=num/den;
        rs[1]=meanY-meanX*rs[0];
        return rs;
    }
	
}
