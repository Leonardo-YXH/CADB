package cn.npt.util.algorithm.integration;

import java.io.IOException;
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
	
	public static void main(String[] args) {
		//double[] X=new double[]{1d,2d,3d,4,5,6,7,8,9,10,11,12,13,14};
		List<String> dataLines=new ArrayList<String>();
		try {
			dataLines=Files.readAllLines(Paths.get("D://振动原始数据.txt"));
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
