package cn.npt.util.algorithm.integration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 一元离散数值积分，采样点X均匀分布
 * @author Leonardo
 *
 */
public class NumericIntegration {

	/**
	 * 
	 * @param X 输入数据
	 * @param K 阶数,K<X.length
	 * @param dt 不能为0
	 * @return
	 */
	public static double[] integrate(double[] X,int K,double dt){
		double[] Y=new double[X.length];
		
		int type=K%4;
		if(0==type){//K-1/4Bode,1 simpson_3_8
			double[] Y_Bode=bode(X);
			double[] Y_Simpson_3_8=simpson_3_8(X);
			for(int i=0;i<X.length;i++){
				Y[i]=0;
				int size=(K-1)/4;
				int y_bode_index=i;
				for(int j=0;j<size;j++){
					if(y_bode_index<Y_Bode.length){
						Y[i]+=Y_Bode[y_bode_index];
					}
					y_bode_index+=4;
				}
				if(y_bode_index<Y_Simpson_3_8.length){
					Y[i]+=Y_Simpson_3_8[y_bode_index];
				}
			}
			
		}
		else if(1==type){//K-1/4Bode
			double[] Y_Bode=bode(X);
			
			for(int i=0;i<X.length;i++){
				Y[i]=0;
				int size=(K-1)/4;
				int y_bode_index=i;
				for(int j=0;j<size;j++){
					if(y_bode_index<Y_Bode.length){
						Y[i]+=Y_Bode[y_bode_index];
					}
					y_bode_index+=4;
				}
			}
		}
		else if(2==type){//K-1/4Bode,trapz
			double[] Y_Bode=bode(X);
			double[] Y_Trapz=trapz(X);
			for(int i=0;i<X.length;i++){
				Y[i]=0;
				int size=(K-1)/4;
				int y_bode_index=i;
				for(int j=0;j<size;j++){
					if(y_bode_index<Y_Bode.length){
						Y[i]+=Y_Bode[y_bode_index];
					}
					y_bode_index+=4;
				}
				if(y_bode_index<Y_Trapz.length){
					Y[i]+=Y_Trapz[y_bode_index];
				}
			}
		}
		else if(3==type){//K-1/4Bode,1 simpson
			double[] Y_Bode=bode(X);
			double[] Y_Simpson=simpson(X);
			for(int i=0;i<X.length;i++){
				Y[i]=0;
				int size=(K-1)/4;
				int y_bode_index=i;
				for(int j=0;j<size;j++){
					if(y_bode_index<Y_Bode.length){
						Y[i]+=Y_Bode[y_bode_index];
					}
					y_bode_index+=4;
				}
				if(y_bode_index<Y_Simpson.length){
					Y[i]+=Y_Simpson[y_bode_index];
				}
			}
		}
		for(int i=0;i<Y.length;i++){
			Y[i]*=dt;
			//System.out.print(Y[i]+"  \n");
		}
		return Y;
	}
	/**
	 * 梯形计算公式
	 * @param X
	 * @return
	 */
	public static double[] trapz(double[] X){
		double[] Y=new double[X.length];
		
		for(int i=0;i<X.length-1;i++){
			Y[i]=(X[i+1]+X[i])/2;
		}
		Y[X.length-1]=X[X.length-1]/2;
		return Y;
	}
	/**
	 * simpson
	 * @param X
	 * @return
	 */
	public static double[] simpson(double[] X){
		double[] Y=new double[X.length];
		
		for(int i=0;i<X.length-2;i++){
			Y[i]=(X[i]+4*X[i+1]+X[i+2])/3;
		}
		Y[X.length-1]=X[X.length-1]/3;
		Y[X.length-2]=(4*X[X.length-2]+X[X.length-1])/3;
		return Y;
	}
	/**
	 * 
	 * @param X
	 * @return
	 */
	public static double[] simpson_3_8(double[] X){
		double[] Y=new double[X.length];
		
		for(int i=0;i<X.length-3;i++){
			Y[i]=(3*X[i]+9*X[i+1]+9*X[i+2]+3*X[i+3])/8;
		}
		Y[X.length-1]=3*X[X.length-1]/8;
		Y[X.length-2]=(3*X[X.length-2]+9*X[X.length-1])/8;
		Y[X.length-3]=(3*X[X.length-3]+9*X[X.length-2]+9*X[X.length-1])/8;
		return Y;
	}
	/**
	 * 
	 * @param X
	 * @return
	 */
	public static double[] bode(double[] X){
		double[] Y=new double[X.length];
		
		for(int i=0;i<X.length-4;i++){
			Y[i]=(14*X[i]+64*X[i+1]+24*X[i+2]+64*X[i+3]+14*X[i+4])/45;
		}
		Y[X.length-1]=14*X[X.length-1]/45;
		Y[X.length-2]=(14*X[X.length-2]+64*X[X.length-1])/45;
		Y[X.length-3]=(14*X[X.length-3]+64*X[X.length-2]+24*X[X.length-1])/45;
		Y[X.length-4]=(14*X[X.length-4]+64*X[X.length-3]+24*X[X.length-2]+64*X[X.length-1])/45;
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
	public static void main(String[] args) {
		//double[] X=new double[]{1d,2d,3d,4,5,6,7,8,9,10,11,12,13,14};
		List<String> dataLines=new ArrayList<String>();
		try {
			dataLines=Files.readAllLines(Paths.get("D://振动原始数据.txt"),StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<Double> datas=new ArrayList<Double>();
		for(String line:dataLines){
			String[] ls=line.split(",");
			for(String l:ls){
				datas.add(Double.parseDouble(l));
			}
		}
		double[] as=new double[datas.size()];
		for(int i=0;i<datas.size();i++){
			as[i]=datas.get(i)*9.80665;
		}
		System.out.println(datas.size()+"===============\n");
		double[] rs=integrate(as, 25600, 1000d/25600);
		for(int i=0;i<10;i++){
			System.out.println(i+":"+rs[i]);
		}
		/*double[] X=new double[]{1d,2d,3d,4,5,6};
		int K=6;
		for(int k=K;k<K+1;k++){
			System.out.println("\nK:="+k+"=======================");
			integrate(X, k, 1);
		}*/
	}
	
}
