package cn.npt.util.algorithm.signal.probableDensity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 概率密度
 * @author leonardo
 *
 */
public class ProbableDensity {

	/**
	 * 概率密度
	 * @param X
	 * @param histogramLength 直方数量
	 * @param type 0--百分比；1--绝对值
	 * @return 返回数组结果最后一个元素是dt,即[Y,dt]
	 */
	public static List<Double> probableDensity(double[] X,int histogramLength,int type){
		List<Double> Xlist=new ArrayList<>(X.length);
		for(int i=0;i<X.length;i++){
			Xlist.add(X[i]);
		}
		Collections.sort(Xlist);
		Double dt=(Xlist.get(Xlist.size()-1)-Xlist.get(0))/histogramLength;
		int ii=0;
		double minX=Xlist.get(0);
		for(Double item:Xlist){
			item-=minX;
			item/=dt;
			item=(double) item.intValue();
			Xlist.set(ii, item);
			ii++;
		}
		List<Double> Y=new ArrayList<Double>();
		for(int i=0,len=histogramLength-1;i<len;i++){
			double sitem=i+1;
			int index=Collections.binarySearch(Xlist, sitem);
			if(index>0){
				Xlist=Xlist.subList(index,Xlist.size());
				Y.add(index*1d);
			}
			else{
				Y.add(0d);
			}
		}
		Y.add(Xlist.size()*1d);
		if(type==0){
			double sum=0d;
			for(Double y:Y){
				sum+=y;
			}
			for(int i=0,len=Y.size();i<len;i++){
				Y.set(i, Y.get(i)/sum);
			}
		}
		Y.add(dt);
		return Y;
	}
}
