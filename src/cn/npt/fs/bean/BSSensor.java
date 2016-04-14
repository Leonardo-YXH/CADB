package cn.npt.fs.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.npt.util.math.NptMath;

public class BSSensor {

	private double startValue;
	private double endValue;
	private long startTime;
	private long endTime;
	private double max;
	private double min;
	private long maxTime;
	private long minTime;
	private double avg;
	private int size;
	/**
	 * 方差
	 */
	private double sd;
	public double getStartValue() {
		return startValue;
	}
	public void setStartValue(double startValue) {
		this.startValue = startValue;
	}
	public double getEndValue() {
		return endValue;
	}
	public void setEndValue(double endValue) {
		this.endValue = endValue;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		this.min = min;
	}
	public long getMaxTime() {
		return maxTime;
	}
	public void setMaxTime(long maxTime) {
		this.maxTime = maxTime;
	}
	public long getMinTime() {
		return minTime;
	}
	public void setMinTime(long minTime) {
		this.minTime = minTime;
	}
	public double getAvg() {
		return avg;
	}
	public void setAvg(double avg) {
		this.avg = avg;
	}
	public double getSd() {
		return sd;
	}
	public void setSd(double sd) {
		this.sd = sd;
	}
	
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	/**
	 * 对data从fromIndex到toIndex做统计分析,如果其中包含NaN,
	 * @param data
	 * @param fromIndex
	 * @param toIndex
	 * @param currentTime
	 * @param currentIndex
	 * @param blockInterval
	 */
	public BSSensor(List<Double> data,int fromIndex,int toIndex,long currentTime,int currentIndex,long blockInterval){
		compute(data, fromIndex, toIndex, currentTime, currentIndex,blockInterval);
	}
	public void compute(List<Double> data,int fromIndex,int toIndex,long currentTime,int currentIndex,long blockInterval){
		this.max=Double.MIN_VALUE;
		this.min=Double.MAX_VALUE;
		this.size=toIndex-fromIndex;
		this.startValue=data.get(fromIndex);
		this.endValue=data.get(toIndex-1);
		this.startTime=currentTime-blockInterval*(currentIndex-fromIndex);
		this.endTime=currentTime-blockInterval*(currentIndex-toIndex);
		
		double sum=0,sum2=0;
		for(int i=fromIndex;i<toIndex;i++){
			double v=data.get(i);
			if(Double.isNaN(v)){
				this.size--;
				continue;
			}
			if(v>max){
				max=v;
				this.maxTime=currentTime-blockInterval*(currentIndex-i);
			}
			if(v<min){
				min=v;
				this.minTime=currentTime-blockInterval*(currentIndex-i);
			}
			sum+=v;
		}
		
		this.avg=sum/this.size;
		
		for(int i=fromIndex;i<toIndex;i++){
			double v=data.get(i);
			if(Double.isNaN(v)){
				continue;
			}
			
			sum2+=Math.pow((v-this.avg), 2);
		}
		this.sd=sum2/this.size;
	}
	public BSSensor(List<Double> data,int fromIndex,int toIndex){
		this.max=Double.MIN_VALUE;
		this.min=Double.MAX_VALUE;
		this.size=toIndex-fromIndex;
		this.startValue=data.get(fromIndex);
		this.endValue=data.get(toIndex-1);
		this.startTime=fromIndex;
		this.endTime=toIndex;
		for(int i=fromIndex;i<toIndex;i++){
			double v=data.get(i);
			if(v>max){
				max=v;
				this.maxTime=i;
			}
			if(v<min){
				min=v;
				this.minTime=i;
			}
		}
		
		this.avg=NptMath.avg(data, fromIndex, toIndex);
		
		this.sd=NptMath.sd(data, fromIndex, toIndex, this.avg);
	}
	public BSSensor(BSSensor s1,BSSensor s2){
		double avg1=s1.getAvg();
		double avg2=s2.getAvg();
		double s1Sum=s1.getSum();
		double s2Sum=s2.getSum();
		int s1Size=s1.getSize();
		int s2Size=s2.getSize();
		double avg12=(s1Sum+s2Sum)/(s1Size+s2Size);
		double s1Delta=2*s1Sum*(avg12-s1.getAvg())-s1Size*(avg12*avg12-avg1*avg1);
		double s2Delta=2*s2Sum*(avg12-s2.getAvg())-s2Size*(avg12*avg12-avg2*avg2);
		//System.out.println("s1delta:"+s1Delta);
		//System.out.println("s2delta:"+s2Delta);
		//System.out.println("s12sum:"+(s1Size*s1.getSd()-s1Delta+s2Size*s2.getSd()-s2Delta));
		//System.out.println("s12size:"+(s1Size+s2Size));
		
		sd=(s1Size*s1.getSd()-s1Delta+s2Size*s2.getSd()-s2Delta)/(s1Size+s2Size);
		this.size=s1.getSize()+s2.getSize();
		startTime=s1.getStartTime();
		endTime=s2.getEndTime();
		startValue=s1.getStartValue();
		endValue=s2.getEndValue();
		if(s1.getMax()>s2.getMax()){
			max=s1.getMax();
			maxTime=s1.getMaxTime();
		}
		else{
			max=s2.getMax();
			maxTime=s2.getMaxTime();
		}
		if(s1.getMin()<s2.getMin()){
			min=s1.getMin();
			minTime=s1.getMinTime();
		}
		else{
			min=s2.getMin();
			minTime=s2.getMinTime();
		}
		avg=avg12;
	}
	/**
	 * 从多个统计结果中统计出新的结果
	 * @param sensors
	 */
	public BSSensor(List<BSSensor> sensors){
		double sum=0;
		int size=0;
		this.max=Double.MIN_VALUE;
		this.min=Double.MAX_VALUE;
		boolean isFirst=true;
		for(BSSensor sensor:sensors){
			if(sensor!=null){
				if(isFirst){
					this.startTime=sensor.getStartTime();
					this.startValue=sensor.getStartValue();
					isFirst=false;
				}
				sum+=sensor.getSum();
				size+=sensor.getSize();
				if(this.max<sensor.getMax()){
					this.max=sensor.getMax();
					this.maxTime=sensor.getMaxTime();
				}
				if(this.min>sensor.getMin()){
					this.min=sensor.getMin();
					this.minTime=sensor.getMinTime();
				}
			}
		}
		this.avg=sum/size;
		
		this.endTime=sensors.get(sensors.size()-1).getEndTime();
		this.endValue=sensors.get(sensors.size()-1).getEndValue();
		
		//sd
		double sdSum=0;
		for(BSSensor sensor:sensors){
			if(sensor!=null){
				sdSum+=sensor.getSd()*sensor.getSize()-deltaMN_M(this.avg, sensor);
			}
		}
		this.sd=sdSum/size;
		this.size=size;
	}
	public double getSum(){
		return avg*size;
	}
	public int getSize(){
		return this.size;
	}
	public void print(){
		System.out.println("max:"+max);
		System.out.println("min:"+min);
		System.out.println("maxIndex:"+maxTime);
		System.out.println("minIndex:"+minTime);
		System.out.println("avg:"+avg);
		System.out.println("size:"+size);
		System.out.println("sum:"+avg*size);
		System.out.println("sd:"+sd);
	}
	private double deltaMN_M(double avg_mn,BSSensor sensor){
		double avg=sensor.getAvg();
		double sum=sensor.getSum();
		int s1Size=sensor.getSize();
		return 2*sum*(avg_mn-avg)-s1Size*(avg_mn*avg_mn-avg*avg);
	}
	/**
	 * startTime,startValue,endValue,max,maxTime,min,minTime,avg,sd
	 * 
	 */
	public StringBuilder toSqlInsert(){
		SimpleDateFormat sdf=new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		StringBuilder sb=new StringBuilder();
		sb.append("'").append(sdf.format(new Date(startTime))).append("',")
			.append(Double.isNaN(startValue)?"null":startValue).append(",")
			.append(Double.isNaN(endValue)?"null":endValue).append(",")
			.append(Double.isNaN(max)?"null":max).append(",'")
			.append(sdf.format(new Date(maxTime))).append("',")
			.append(Double.isNaN(min)?"null":min).append(",'")
			.append(sdf.format(new Date(minTime))).append("',")
			.append(Double.isNaN(avg)?"null":avg)
			.append(",").append(Double.isNaN(sd)?"null":sd);
		return sb;
	}
}
