package cn.z.util;
import cn.z.constant.ConStr;
import org.apache.log4j.Logger;
import sun.reflect.Reflection;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
public class LogUtil {
	public static String T = "	";
	public static String S = " ";
	static String logLocation = "/resources/log/";
	static String logpath_posRS = "receivesend/";
	static String logpath_sysErrs = "exception/";
	public static String logpath_wx = "wx/";
	public static String logpath_phonemsg = "phonemsg/";
	static String logpath_delLogFiles = "delFiles/";
	private static Map<Integer, Integer> map = new HashMap<>();
	/**
	 * 获取年月日时分秒等
	 */
	static Integer getT(int t) {
		Calendar calendar = Calendar.getInstance();
		// 年
		map.put(0, calendar.get(Calendar.YEAR));
		// 月
		map.put(1, calendar.get(Calendar.MONTH) + 1);
		// 日
		map.put(2, calendar.get(Calendar.DAY_OF_MONTH));
		// 时
		map.put(3, calendar.get(Calendar.HOUR_OF_DAY));
		// 分
		map.put(4, calendar.get(Calendar.MINUTE));
		// 秒
		map.put(5, calendar.get(Calendar.SECOND));
		// 周几  0~6
		map.put(6, calendar.get(Calendar.DAY_OF_WEEK));
		// 本年第N天
		map.put(7, calendar.get(Calendar.DAY_OF_YEAR));
		// 本月第N天
		map.put(8, calendar.get(Calendar.DAY_OF_MONTH));
		int i = -1;
		if (map.containsKey(t)) {
			i = map.get(t);
		}
		return i;
	}
	/**
	 * byte 转换为 十六进制string
	 */
	public static String bytes2HexString(byte[] b) {
		StringBuilder ret = new StringBuilder();
		for (byte b1 : b) {
			String hex = Integer.toHexString(b1 & 0xFF);
			if (hex.length() == 1) {
				hex = ConStr.STRZ0 + hex;
			}
			ret.append(hex.toUpperCase());
		}
		return ret.toString();
	}
	/**
	 * 组合路径之年月日
	 */
	public static String getPathWithTime(String path) {
		return path + getT(0) + "年/" + getT(1) + "月/" + getT(2) + "日/";
	}
	/**
	 * 组合路径之年月
	 */
	public static String getPathWithTimeToMonth(String path) {
		return path + getT(0) + "年/" + getT(1) + "月/";
	}
	/**
	 * 组合路径之年月
	 */
	public static String getPathWithTimeToYear(String path) {
		return path + getT(0) + "年/";
	}

	private static Logger logger =  null;
	public static Logger getLogger(){
		if (null == logger){
			//Java8 废弃了Reflection.getCallerClass()
			//https://blog.csdn.net/languobeibei/article/details/72461554
			logger = Logger.getLogger(Reflection.getCallerClass());
		}
		return logger;
	}
}
