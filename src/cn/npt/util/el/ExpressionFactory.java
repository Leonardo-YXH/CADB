package cn.npt.util.el;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.greenpineyu.fel.Expression;
import com.greenpineyu.fel.FelEngine;
/**
 * 表达式集合，容量为5000
 * @author leonardo_yang
 *
 */
public class ExpressionFactory {

	private static int maxSize=5000;
	private static List<String> keys=new LinkedList<String>();
	private static Map<String,Expression> expressions=new HashMap<String,Expression>();
	public synchronized static Expression build(FelEngine engine,String expStr){
		Expression exp=expressions.get(expStr);
		if(exp==null){
			if(keys.size()>maxSize){//
				reallocate();
			}
			keys.add(expStr);
			exp=engine.compile(expStr, engine.getContext());
			expressions.put(expStr, exp);
		}
		return exp;
	}
	/**
	 * 减少元素，避免无限增大,清理1/2
	 */
	private static void reallocate(){
		for(int i=0;i<maxSize/2;i++){
			String key=keys.remove(0);
			expressions.remove(key);
		}
	}
}
