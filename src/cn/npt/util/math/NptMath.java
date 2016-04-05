package cn.npt.util.math;

import java.util.List;

public class NptMath {
	/**
	 * 
	 * @param data
	 * @param fromIndex
	 * @param toIndex excluded
	 * @return
	 */
	public static double sum(List<Double> data,int fromIndex,int toIndex){
		double sum=0;
		for(int i=fromIndex;i<toIndex;i++){
			sum+=data.get(i);
		}
		return sum;
	}
	public static double avg(List<Double> data,int fromIndex,int toIndex){
		double sum=sum(data, fromIndex, toIndex);
		return sum/(toIndex-fromIndex);
	}
	/**
	 * 方差
	 * @param data
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 */
	public static double sd(List<Double> data,int fromIndex,int toIndex){
		double avg=avg(data, fromIndex, toIndex);
		double sum=0;
		for(int i=fromIndex;i<toIndex;i++){
			sum+=Math.pow(avg-data.get(i), 2);
		}
		return sum/(toIndex-fromIndex);
	}
	public static double sd(List<Double> data,int fromIndex,int toIndex,double avg){
		double sum=0;
		for(int i=fromIndex;i<toIndex;i++){
			sum+=Math.pow(avg-data.get(i), 2);
		}
		return sum/(toIndex-fromIndex);
	}
	
}
