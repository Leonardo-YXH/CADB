package cn.npt.fs.alarm;

import java.util.Collections;
import java.util.List;

import cn.npt.util.algorithm.search.NumberIndexOfKit;
/**
 * 二维阈值判断
 * @author Leonardo
 *
 */
public class ThresholdCondition2Dim<T extends Number & Comparable<T>> implements IThresholdValue<T> {

	/**
	 * 阈值依赖条件列表，升序
	 */
	private List<T> X;
	/**
	 * 多条件阈值列表，升序.YX.size=X.size+1且目前YX[i].size都相等
	 */
	private List<List<T>> YX;
	/**
	 * YX对应的警报等级，levels.size==YX[i].size+1,因为levels包含0这个正常等级
	 */
	private List<List<Integer>> levels;
	
	
	public ThresholdCondition2Dim(List<T> X,List<List<T>> YX,List<List<Integer>> levels) {
		this.X=X;
		Collections.sort(this.X);
		this.YX=YX;
		for(List<T> Y:this.YX){
			Collections.sort(Y);
		}
		this.levels=levels;
	}
	
	
	@Override
	public int compare(List<T> values) {
		if(values.size()!=2){
			return -500;
		}
		int i=NumberIndexOfKit.indexOf(X, values.get(0));
		List<T> Y=YX.get(i);
		List<Integer> level=this.levels.get(i);
		return level.get(NumberIndexOfKit.indexOf(Y, values.get(1)));
	}
}
