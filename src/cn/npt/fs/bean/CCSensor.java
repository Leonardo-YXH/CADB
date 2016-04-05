package cn.npt.fs.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.npt.util.el.FelEngineHelp;

import com.greenpineyu.fel.Expression;
import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.context.FelContext;
/**
 * 通道合并虚拟sensor
 * @author Leonardo
 *
 */
public class CCSensor {
	private long sensorId;
	private Expression formula;
	private FelContext felCtx;
	/**
	 * 原始表达式。eg:{1}+{2}*{3}
	 */
	private String expStr;
	/**
	 * 参数名:_sensorId
	 */
	private List<String> paramNames;
	/**
	 * 公式里面的SensorId
	 */
	private List<Long> sensorIds;
	
	
	//private static Logger log=Logger.getLogger(CCSensor.class);
	
	public CCSensor(long sensorId, String expStr) {
		this.paramNames=new ArrayList<String>();
		this.sensorIds=new ArrayList<Long>();
		this.sensorId = sensorId;
		getParamList(expStr);
		this.expStr=expStr;
		setExpStr();
		
		
		FelEngine engine=FelEngineHelp.getInstance();
		this.felCtx=engine.getContext();
		for(String param:paramNames){//init felcontext
			felCtx.set(param, 1.0);
		}
		this.formula = engine.compile(this.expStr,felCtx);
		
	}
	
	/**
	 * 将参数中的{}替换，并且SensorId前加_
	 */
	private void setExpStr(){
		this.expStr=this.expStr.replaceAll("\\{", "_");
		this.expStr=this.expStr.replaceAll("\\}", "");
	}
	public double eval(FelContext ctx){
		//log.info("formula："+expStr);
		return (double) this.formula.eval(ctx);
	}
	/**
	 * 从表达式中提取出所有的参数.标志：{}
	 * @param expStr
	 * 
	 */
	public List<String> getParamList(String expStr){
		List<String> params=new ArrayList<String>();
		Pattern p=Pattern.compile("\\{[%:.\\-\\w\\s\\(\\)]+\\}");//有%,空格,.,:
		Matcher m=p.matcher(expStr);
		while(m.find()){
			String temp=m.group();
			temp=temp.replaceAll("[\\{\\}]", "");
			if(params.indexOf(temp)==-1){
				params.add(temp);
				this.paramNames.add("_"+temp);
				this.sensorIds.add(Long.parseLong(temp));
			}
		}
		return params;
	}

	public long getSensorId() {
		return sensorId;
	}

	public void setSensorId(long sensorId) {
		this.sensorId = sensorId;
	}

	public List<Long> getSensorIds() {
		return sensorIds;
	}

	public void setSensorIds(List<Long> sensorIds) {
		this.sensorIds = sensorIds;
	}

	public Expression getFormula() {
		return formula;
	}

	public void setFormula(Expression formula) {
		this.formula = formula;
	}

	public FelContext getFelCtx() {
		return felCtx;
	}

	public void setFelCtx(FelContext felCtx) {
		this.felCtx = felCtx;
	}
	
	
}
