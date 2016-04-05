package cn.npt.util.el;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.greenpineyu.fel.Expression;
import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.context.FelContext;
/**
 * 执行一段表达式,主要用来求值,所以返回值都是double(亲测 ).
 * 有参数的时候推荐使用eval(String expression,MapString,Object param,FelEngine engine),速度更快
 * 如果事先已经知道要替换的参数则先将参数替换成值再调用eval(exp,engine)
 * @author leonardo_yang
 * @version 2014-09-24 23:50:00 v1.0
 */
public class EvalHelp {
	/**
	 * 执行表达式
	 * @param expression 表达式语句
	 * @return
	 */
	public static double eval(String expression){
		FelEngine engine=FelEngineHelp.getInstance();
		double result=Double.parseDouble(engine.eval(expression).toString());
		return result;
	}
	/**
	 * 推荐使用(解释执行)
	 * @param expression
	 * @param engine 将engine提到外部主要是为了减少创建engine的时间开销
	 * @return
	 */
	public static double eval(String expression,FelEngine engine){
		double result=Double.parseDouble(engine.eval(expression).toString());
		return result;
	}
	/**
	 * 推荐使用(解释执行,效率有所下降)
	 * @param expression
	 * @param param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static double eval(String expression,Map<String,Object> param,FelEngine engine){
		FelContext ctx=engine.getContext();
		@SuppressWarnings("rawtypes")
		Iterator it=param.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String,Object> entry=(Entry<String, Object>) it.next();
			ctx.set(entry.getKey(), entry.getValue());
		}
		double result=Double.parseDouble(engine.eval(expression,ctx).toString());
		return result;
	}
	/**
	 * 有bug(fel的classpath有问题)
	 * @param expression 在编译表达式之前必须设定好ctx里面的变量类型！！！
	 * @param param
	 * @param engine 将engine提到外部主要是为了减少创建engine的时间开销
	 * @return
	 */
	public static double eval(Expression expression,Map<String,Object> param,FelEngine engine){
		FelContext ctx=engine.getContext();
		@SuppressWarnings("rawtypes")
		Iterator it=param.entrySet().iterator();
		while(it.hasNext()){
			@SuppressWarnings("unchecked")
			Map.Entry<String,Object> entry=(Entry<String, Object>) it.next();
			ctx.set(entry.getKey(), entry.getValue());
		}
		double result=Double.parseDouble(expression.eval(ctx).toString());
		return result;
	}
}
