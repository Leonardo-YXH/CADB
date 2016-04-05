package cn.npt.fs.datarouter.history;

import java.util.ArrayList;
import java.util.List;
/**
 * 暂无用法
 * @author Leonardo
 *
 */
public class IotCvalueTable {

	/**
	 * iot_cvalue_in* 表按从大到小排列[year->season->month->...->second->millisecond]
	 */
	private static List<String> tables=new ArrayList<String>();
	static{
		tables.add("iot_cvalue_inyear");
		tables.add("iot_cvalue_inseason");
		tables.add("iot_cvalue_inmonth");
		tables.add("iot_cvalue_inweek");
		tables.add("iot_cvalue_inday");
		tables.add("iot_cvalue_inhour");
		tables.add("iot_cvalue_intenminute");
		tables.add("iot_cvalue_inminute");
		tables.add("iot_cvalue_intensecond");
		tables.add("iot_cvalue_insecond");
		tables.add("iot_cvalue_inms");
	}
	
	//public static List<String> getTable(String )
}
