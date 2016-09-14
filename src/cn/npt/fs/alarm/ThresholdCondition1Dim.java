package cn.npt.fs.alarm;

import java.util.Collections;
import java.util.List;

import cn.npt.util.algorithm.search.NumberIndexOfKit;
/**
 * 一维阈值判断
 * @author Leonardo
 *
 */
public class ThresholdCondition1Dim<T extends Number & Comparable<T>> implements IThresholdValue<T> {
	/**
	 * 阈值列表，升序
	 */
	private List<T> X;
	/**
	 * X对应的警报等级，levels.size==X.size+1,因为levels包含0这个正常等级
	 */
	private List<Integer> levels;
	/**
	 * 
	 * @param X 阈值列表，升序(为防止用户传递的X未按升序排列，构造函数里面对其进行升序操作)
	 * @param levels X对应的警报等级，levels.size==X.size+1,因为levels包含0这个正常等级
	 */
	public ThresholdCondition1Dim(List<T> X,List<Integer> levels) {
		this.X=X;
		Collections.sort(this.X);
		this.levels=levels;
	}
	
	public List<T> getX() {
		return X;
	}
	public void setX(List<T> x) {
		X = x;
	}
	public List<Integer> getLevels() {
		return levels;
	}
	public void setLevels(List<Integer> levels) {
		this.levels = levels;
	}

	/**
	 * 执行阈值比较，返回警报等级
	 * @param values
	 */
	@Override
	public int compare(List<T> values) {
		if(values.size()!=1){
			return -500;
		}
		return this.levels.get(NumberIndexOfKit.indexOf(this.X, values.get(0)));
	}
	
}
