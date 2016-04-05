package cn.npt.util.algorithm.statistic;

import java.util.ArrayList;
import java.util.List;

/**
 * 滑动窗口
 * @author Leonardo
 *
 */
public class SlideWindow {
	/**
	 * 
	 * @param data
	 * @param windowSize
	 * @return
	 */
	@Deprecated
	public static <E extends Number> List<E> slideWindowAvg(List<E> data,int windowSize){
		List<E> rs=new ArrayList<E>();
		
		
		return rs;
	}
	/**
	 * 滑膜平均(处理double)
	 * @param data
	 * @param windowSize 窗口大小为奇数(若为偶数则自动加1)
	 * @return
	 */
	public static List<Double> slideWindowAvg4Double(List<Double> data,int windowSize){
		List<Double> rs=new ArrayList<Double>();
		if(windowSize<=0||windowSize>=data.size()){
			return data;
		}
		if(windowSize%2==0){
			windowSize+=1;
		}
		int leftHalf=-windowSize/2;
		int rightHalf=windowSize/2;
		int endIndex=data.size()-rightHalf;
		//
		double sumTmp=0;
		for(int i=0;i<windowSize;i++){
			sumTmp+=data.get(i);
		}
		for(int i=-leftHalf;i<endIndex;i++){
			
			if(i==-leftHalf){//处理头部窗口
				double value=sumTmp/windowSize;
				for(int j=0;j<=-leftHalf;j++){
					rs.add(value);
				}
			}
			else if(i==endIndex-1){//处理尾部窗口
				sumTmp=sumTmp+data.get(i+rightHalf)-data.get(i+leftHalf-1);
				double value=sumTmp/windowSize;
				for(int j=endIndex-1;j<data.size();j++){
					rs.add(value);
				}
			}
			else{
				sumTmp=sumTmp+data.get(i+rightHalf)-data.get(i+leftHalf-1);
				double value=sumTmp/windowSize;
				rs.add(value);
			}
			
		}
		return rs;
	}
	/**
	 * 滑膜平均(处理float)
	 * @param data
	 * @param windowSize 窗口大小为奇数(若为偶数则自动加1)
	 * @return
	 */
	public static List<Float> slideWindowAvg4Float(List<Float> data,int windowSize){
		List<Float> rs=new ArrayList<Float>();
		if(windowSize<=0||windowSize>=data.size()){
			return data;
		}
		if(windowSize%2==0){
			windowSize+=1;
		}
		int leftHalf=-windowSize/2;
		int rightHalf=windowSize/2;
		int endIndex=data.size()-rightHalf;
		//
		float sumTmp=0;
		for(int i=0;i<windowSize;i++){
			sumTmp+=data.get(i);
		}
		for(int i=-leftHalf;i<endIndex;i++){
			
			if(i==-leftHalf){//处理头部窗口
				float value=sumTmp/windowSize;
				for(int j=0;j<=-leftHalf;j++){
					rs.add(value);
				}
			}
			else if(i==endIndex-1){//处理尾部窗口
				sumTmp=sumTmp+data.get(i+rightHalf)-data.get(i+leftHalf-1);
				float value=sumTmp/windowSize;
				for(int j=endIndex-1;j<data.size();j++){
					rs.add(value);
				}
			}
			else{
				sumTmp=sumTmp+data.get(i+rightHalf)-data.get(i+leftHalf-1);
				float value=sumTmp/windowSize;
				rs.add(value);
			}
			
		}
		return rs;
	}
	public static void main(String[] args){
		List<Double> data=new ArrayList<Double>();
		for(int i=0;i<10;i++){
			data.add(1.5*i);
		}
		List<Double> rs=slideWindowAvg4Double(data, 1);
		System.out.println("============================");
		for(Double v:rs){
			System.out.print(v+" ");
		}
		System.out.println();
		
		rs=slideWindowAvg4Double(data, 2);
		System.out.println("============================");
		for(Double v:rs){
			System.out.print(v+" ");
		}
		System.out.println();
		
		rs=slideWindowAvg4Double(data, 3);
		System.out.println("============================");
		for(Double v:rs){
			System.out.print(v+" ");
		}
		System.out.println();
		
		rs=slideWindowAvg4Double(data, 4);
		System.out.println("============================");
		for(Double v:rs){
			System.out.print(v+" ");
		}
		System.out.println();
		
	}
}
