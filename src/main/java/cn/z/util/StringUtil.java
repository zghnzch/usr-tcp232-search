package cn.z.util;
import cn.z.constant.ConNum;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class StringUtil {
	public static String T = "	";
	public static String S = " ";
	public static String windows = "windows";
	public static String linux = "linux";
	private static final Pattern PATTERN = Pattern.compile("[0-9]*");
	/**
	 获取请求基路径
	 */
	public static String requestBase(HttpServletRequest request){
		String url = request.getRequestURI();
		url = url.replace("/WEB-INF/page","");
		int last = url.lastIndexOf("/");
		url = url.substring(0,last);
		return url;
	}
	/**
	 获取请求路径
	 */
	public static String requestPath(HttpServletRequest request){
		String url = request.getRequestURI();
		url = url.replace("/WEB-INF/page","");
		int last = url.lastIndexOf(".");
		url = url.substring(0,last);
		return url;
	}
	/**
	 获取转发路径
	 */
	public static String requestPath(HttpServletRequest request,String path){
		String url = request.getRequestURI();
		url = url.replace("/WEB-INF/page","");
		int last = url.lastIndexOf("/");
		url = url.substring(0,last + 1) + path;
		return url;
	}
	/**
	 byte转int
	 */
	public static Integer byteToInt(byte b){
		return b & 0xFF;
	}
	/**
	 byte转16进制字符
	 */
	private static String toHexString(byte b){
		String hex = Integer.toHexString(b & 0xFF);
		if(hex.length() == 1){
			hex = "0" + hex;
		}
		return hex;
	}
	/**
	 返回数字左补零字符串，如1返回"01"
	 */
	public static String leftPad(Integer str,Integer num){
		String temp = Integer.toString(str);
		num = num - temp.length();
		for(int i = 0;i < num;i++){
			temp = "0" + temp;
		}
		return temp;
	}
	/**
	 转换成16进制字符串再左补零
	 */
	public static String hexLeftPad(BigDecimal str,Integer num){
		String temp = Integer.toHexString(str.intValue()).replace("ffffff","");
		num = num - temp.length();
		for(int i = 0;i < num;i++){
			temp = "0" + temp;
		}
		return temp;
	}
	/**
	 转换成16进制字符串再左补零
	 */
	public static String hexLeftPad(int str,Integer num){
		String temp = Integer.toHexString(str).replace("ffffff","");
		num = num - temp.length();
		for(int i = 0;i < num;i++){
			temp = "0" + temp;
		}
		return temp;
	}
	/**
	 long转换成16进制字符串再左补零
	 */
	public static String hexLeftPad(long str,Integer num){
		String temp = Long.toHexString(str).replace("ffffff","");
		num = num - temp.length();
		for(int i = 0;i < num;i++){
			temp = "0" + temp;
		}
		return temp;
	}
	/**
	 16进制数字右补零
	 */
	public static String hexRightPad(Integer str,Integer num){
		String temp = Integer.toHexString(str);
		num = num - temp.length();
		for(int i = 0;i < num;i++){
			temp += "0";
		}
		return temp;
	}
	/**
	 字符串左补零
	 */
	public static String strLeftPad(String str,Integer num){
		num = num - str.length();
		for(int i = 0;i < num;i++){
			str = "0" + str;
		}
		return str;
	}
	/**
	 字符串左补字符
	 */
	public static String strLeftPadWithChar(String str,Integer num,String c){
		num = num - str.length();
		for(int i = 0;i < num;i++){
			str = c + str;
		}
		return str;
	}
	/**
	 字符串右补零
	 */
	public static String strRightPad(String str,Integer num){
		num = num - str.length();
		for(int i = 0;i < num;i++){
			str += "0";
		}
		return str;
	}
	/**
	 转换日期格式为16进制字符串 例：“2009-12-04” -> “9C04”
	 修改2009-07-06：二进制前7位表示年，下4位月，最后5位表示日如"2009-07-06" -> "12E6"
	 */
	public static String dateToHexStr(Calendar date){
		String year = String.valueOf(date.get(Calendar.YEAR));
		byte tmYear = (byte)((int)Integer.valueOf(year.substring(2)));
		byte tmMonth = (byte)(date.get(Calendar.MONTH) + 1);
		byte tmDay = (byte)date.get(Calendar.DAY_OF_MONTH);
		String tmpStr = Integer.toHexString((tmYear << 1) | (tmMonth >> 3));
		if(tmpStr.length() == 1){
			tmpStr = "0" + tmpStr;
		}
		String tmpStr2 = Integer.toHexString((byte)(tmMonth << 5 | tmDay));
		if(tmpStr2.length() >= 2){
			tmpStr2 = tmpStr2.substring(tmpStr2.length() - 2);
		}
		if(tmpStr2.length() == 1){
			tmpStr2 = "0" + tmpStr2;
		}
		tmpStr += tmpStr2;
		if(tmpStr.length() == 4){
			return tmpStr;
		}
		return "0000";
	}
	/**
	 转换日期格式为16进制字符串 例：“2009-12-04” -> “9C04”
	 修改2009-07-06：二进制前7位表示年，下4位月，最后5位表示日如"2009-07-06" -> "12E6"
	 */
	public static String dateStrToHexStr(String dateStr){
		Calendar date = Calendar.getInstance();
		String[] dateStrs = dateStr.split("-");
		date.set(StringUtil.objToInt(dateStrs[0]),StringUtil.objToInt(dateStrs[1]),StringUtil.objToInt(dateStrs[2]));
		date.add(Calendar.MONTH,-1);
		String year = String.valueOf(date.get(Calendar.YEAR));
		byte tmYear = (byte)((int)Integer.valueOf(year.substring(2)));
		byte tmMonth = (byte)(date.get(Calendar.MONTH) + 1);
		byte tmDay = (byte)date.get(Calendar.DAY_OF_MONTH);
		String tmpStr = Integer.toHexString((tmYear << 1) | (tmMonth >> 3));
		if(tmpStr.length() == 1){
			tmpStr = "0" + tmpStr;
		}
		String tmpStr2 = Integer.toHexString(tmMonth << 5 | tmDay);
		tmpStr2 = tmpStr2.substring(tmpStr2.length() - 2);
		if(tmpStr2.length() == 1){
			tmpStr2 = "0" + tmpStr2;
		}
		tmpStr += tmpStr2;
		if(tmpStr.length() == 4){
			return tmpStr;
		}
		return "0000";
	}
	/**
	 转换16进制字符串为日期格式 例：“9C04” -> “2009-12-04”
	 修改2009-07-06：二进制前7位表示年，下4位月，最后5位表示日如"12E6" -> "2009-07-06"
	 */
	public static String dateFromHexStr(String hexStr){
		int tm1 = Integer.parseInt(hexStr.substring(0,2),16);
		int tm2 = Integer.parseInt(hexStr.substring(2),16);
		int tmYear = (tm1 & 0XFE) >> 1;
		int tmMonth = (tm1 & 0X01) << 3 | (tm2 >> 5);
		int tmDay = tm2 & 0X1F;
		String year = "20" + leftPad(tmYear,2);
		String month = leftPad(tmMonth,2);
		String day = leftPad(tmDay,2);
		String tmpDate = year + "-" + month + "-" + day;
		if(tmpDate.length() == 10){
			return tmpDate;
		}
		return "0000-00-00";
	}
	/**
	 2进制字符串转16进制字符串
	 */
	public static String binaryHexStr(String binary){
		//		String[] strs = binary.split(" ");
		//		String result = "";
		//		for(String s : strs){
		//			String hex = Integer.toString(Integer.parseInt(s,2),16);
		//			result += hex;
		//		}
		//		if(result.length() == 1){
		//			result = "0" + result;
		//		}
		String[] strs = binary.split(" ");
		StringBuilder result = new StringBuilder();
		for(String s : strs){
			String hex = Integer.toString(Integer.parseInt(s,2),16);
			result.append(hex);
		}
		if(result.length() == 1){
			result.insert(0,"0");
		}
		return result.toString();
	}
	/**
	 四字节时间戳
	 */
	public static String timeToHexStr(){
		//		String tempStr = "";
		//		Date date = new Date("2000/01/01");
		//		long time = (System.currentTimeMillis() - date.getTime()) / 1000;
		//		for(int i = 24;i >= 0;i -= 8){
		//			String tmpStr1 = Integer.toHexString((byte)((time >> i) & 0xff)).replace("ffffff","");
		//			if(tmpStr1.length() == 1){
		//				tempStr = tempStr + "0" + tmpStr1;
		//			} else{
		//				tempStr += tmpStr1;
		//			}
		//		}
		StringBuilder tempStr = new StringBuilder();
		Date date = new Date("2000/01/01");
		long time = (System.currentTimeMillis() - date.getTime()) / 1000;
		for(int i = 24;i >= 0;i -= 8){
			String tmpStr1 = Integer.toHexString((byte)((time >> i) & 0xff)).replace("ffffff","");
			if(tmpStr1.length() == 1){
				tempStr.append("0").append(tmpStr1);
			} else{
				tempStr.append(tmpStr1);
			}
		}
		return tempStr.toString();
	}
	/**
	 得到字节数组
	 */
	public static byte[] strTobytes(String str){
		System.out.println("get1:"+str);
		byte[] b = new byte[str.length() / 2];
		for(int i = 0;i < str.length() / 2 - 2;i++){
			String s = str.substring(i * 2,i * 2 + 2);
			b[i] = (byte)Integer.parseInt(s,16);
		}
		System.out.println("get2:"+LogUtil.bytes2HexString(b));
		return b;
	}
	/**
	 得到字节数组
	 */
	public static byte[] strTobytes2(String str){
		// System.out.println("get1:"+str);
		byte[] b = new byte[str.length() / 2];
		for(int i = 0;i < str.length() / 2;i++){
			String s = str.substring(i * 2,i * 2 + 2);
			b[i] = (byte)Integer.parseInt(s,16);
		}
		// System.out.println("get2:"+LogUtil.bytes2HexString(b));
		return b;
	}
	/**
	 获取国标码
	 */
	public static String strToGB2312(String str){
		//		String result = "";
		//		byte[] strByte;
		//		try{
		//			strByte = str.getBytes("GB2312");
		//			for(byte b : strByte){
		//				result += StringUtil.hexLeftPad(b,2);
		//			}
		//		} catch(UnsupportedEncodingException e){
		//			e.printStackTrace();
		//		}
		StringBuilder result = new StringBuilder();
		byte[] strByte;
		try{
			strByte = str.getBytes("GB2312");
			for(byte b : strByte){
				result.append(StringUtil.hexLeftPad(b,2));
			}
		} catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
		return StringUtil.strRightPad(result.toString(),32);
	}
	/**
	 获取BCD码int
	 */
	public static Integer byteToBCDInt(byte b){
		return Integer.parseInt(toHexString(b));
	}
	/**
	 日期格式化
	 */
	public static String dateFormat(Date date,String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
	/**
	 从字节数组中获取指定位置的hex字符形式数据 byte[]b= {00, 11, 22, 33, 44}; byteToHexStr(1, 3, byte[] b) 结果11 22 33
	 */
	public static String getHexStrFromBytes(Integer begin,Integer end,byte[] b){
		String baseInfoStr = "";
		for(int i = begin;i <= end;i++){
			String hex = Integer.toHexString(b[i] & 0xFF);
			if(hex.length() == 1){
				hex = '0' + hex;
			}
			baseInfoStr += hex;
		}
		return baseInfoStr;
	}
	/**
	 转换卡hex字节数据到int
	 */
	public static Integer hexToInt(Integer begin,Integer end,byte[] b){
		return Integer.parseInt(getHexStrFromBytes(begin,end,b),16);
	}
	/**
	 转换卡hex字节数据到long
	 */
	public static Long hexToLong(Integer begin,Integer end,byte[] b){
		return Long.parseLong(getHexStrFromBytes(begin,end,b),16);
	}
	/**
	 * 十六进制字符串转字符串
	 */
	public static String hexToString(String hex,int step) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < hex.length() - 1; i += step) {
			//grab the hex in pairs
			String output = hex.substring(i, (i + step));
			//convert hex to decimal
			int decimal = Integer.parseInt(output, ConNum.P16);
			//convert the decimal to character
			sb.append((char) decimal);
		}
		return sb.toString();
	}

	/**
	 object类型int字符串转Integer
	 */
	public static Integer objToInt(Object obj){
		if(obj == null){
			return 0;
		}
		return Integer.valueOf(obj.toString());
	}
	/**
	 object类型long字符串转Long
	 */
	public static Long objToLong(Object obj){
		if(obj == null){
			return (long)0;
		}
		return Long.valueOf(obj.toString());
	}
	/**
	 object类型float字符串转Float
	 */
	public static Float objToFloat(Object obj){
		if(obj == null){
			return (float)0;
		}
		return Float.valueOf(obj.toString());
	}
	/**
	 Object转String
	 */
	public static String objToString(Object obj){
		if(obj != null){
			return obj.toString();
		} else{
			return "";
		}
	}
	/**
	 姓名简拼
	 */
	public static String toJP(String c) throws UnsupportedEncodingException{
		char[] chars = c.toCharArray();
		StringBuilder sb = new StringBuilder("");
		for(char aChar : chars){
			sb.append(getJP(aChar));
		}
		return sb.toString().toUpperCase();
	}
	/**
	 获取简拼
	 */
	private static String getJP(char c) throws UnsupportedEncodingException{
		byte[] array = String.valueOf(c).getBytes("gbk");
		if(array.length < 2){
			return String.valueOf(c);
		}
		int i = (short)(array[0] - '\0' + 256) * 256 + ((short)(array[1] - '\0' + 256));
		if(i < 0xB0A1){
			return String.valueOf(c);
		}
		if(i < 0xB0C5){
			return "a";
		}
		if(i < 0xB2C1){
			return "b";
		}
		if(i < 0xB4EE){
			return "c";
		}
		if(i < 0xB6EA){
			return "d";
		}
		if(i < 0xB7A2){
			return "e";
		}
		if(i < 0xB8C1){
			return "f";
		}
		if(i < 0xB9FE){
			return "g";
		}
		if(i < 0xBBF7){
			return "h";
		}
		if(i < 0xBFA6){
			return "j";
		}
		if(i < 0xC0AC){
			return "k";
		}
		if(i < 0xC2E8){
			return "l";
		}
		if(i < 0xC4C3){
			return "m";
		}
		if(i < 0xC5B6){
			return "n";
		}
		if(i < 0xC5BE){
			return "o";
		}
		if(i < 0xC6DA){
			return "p";
		}
		if(i < 0xC8BB){
			return "q";
		}
		if(i < 0xC8F6){
			return "r";
		}
		if(i < 0xCBFA){
			return "s";
		}
		if(i < 0xCDDA){
			return "t";
		}
		if(i < 0xCEF4){
			return "w";
		}
		if(i < 0xD1B9){
			return "x";
		}
		if(i < 0xD4D1){
			return "y";
		}
		if(i < 0xD7FA){
			return "z";
		}
		return String.valueOf(c);
	}
	/**
	 获取水控时间段hex
	 */
	public static String getWaterRateTime(String beginTime,String endTime){
		String hex = "";
		String[] time = beginTime.split(":");
		hex += StringUtil.hexLeftPad(Integer.valueOf(time[0]),2);
		hex += StringUtil.hexLeftPad(Integer.valueOf(time[1]),2);
		time = endTime.split(":");
		hex += StringUtil.hexLeftPad(Integer.valueOf(time[0]),2);
		hex += StringUtil.hexLeftPad(Integer.valueOf(time[1]),2);
		return hex;
	}

	/**
	 水控授权16个卡类型转hex字符串2字节
	 */
	public static String getWaterCardTypesHexStr(String str){
		String result = "";
		for(int i = 7;i >= 0;i--){
			if(str.indexOf("," + i + ",") == -1){
				result += "0";
			} else{
				result += "1";
			}
		}
		for(int i = 15;i >= 8;i--){
			if(str.indexOf("," + i + ",") == -1){
				result += "0";
			} else{
				result += "1";
			}
		}
		return StringUtil.strLeftPadWithChar(StringUtil.binaryHexStr(result),4,"0");
	}
	/**
	 消费机授权16个卡类型转hex字符串2字节
	 */
	public static String getPosCardTypesHexStr(String str){
		String result = "";
		for(int i = 15;i >= 8;i--){
			if(str.indexOf("," + i + ",") == -1){
				result += "0";
			} else{
				result += "1";
			}
		}
		for(int i = 7;i >= 0;i--){
			if(str.indexOf("," + i + ",") == -1){
				result += "0";
			} else{
				result += "1";
			}
		}
		return StringUtil.strLeftPadWithChar(StringUtil.binaryHexStr(result),4,"0");
	}

	/**
	 获取当前系统时间 zhaochunhui
	 */
	public static String getNowTime(){
		// 设置日期格式
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		return df.format(new Date()).toString();
	}
	public static String getNowTimeFortest(){
		// 设置日期格式
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return df.format(new Date()).toString();
	}
	/**
	 * 自动注册用户 生成用户名 名字缩写 编号
	 */
	public static String getAboutName() {
		// 设置日期格式
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return df.format(new Date());
	}
	/**
	 结算中心查询开始时间
	 */
	public static String getNowTimeForDataCenterBg(){
		// 设置日期格式
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		return df.format(new Date());
	}
	/**
	 结算中心查询结束时间
	 */
	public static String getNowTimeForDataCenterEd(){
		// 设置日期格式
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		return df.format(new Date());
	}
	/**
	 结算用
	 */
	public static String getDateStr(String day,int dayAddNum){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date nowDate = null;
		try{
			nowDate = df.parse(day);
		} catch(ParseException e){
			e.printStackTrace();
		}
		if(null != nowDate){
			Date newDate2 = new Date(nowDate.getTime() + dayAddNum * 1000);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return simpleDateFormat.format(newDate2);
		} else{
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}
	}
	/**
	 发送手机短信
	 */
	public static void sendMobileShortMessage(String mobile,String message) throws Exception{
		//region abandon msg log method
		//endregion
		HttpPost httpost = new HttpPost("http://sms.cloud.hbsmservice.com:8080/post_sms.do");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id","xb1157"));
		params.add(new BasicNameValuePair("MD5_td_code","1dd777dc8473e9ec6d4361609ffb10db"));
		params.add(new BasicNameValuePair("mobile",mobile));
		params.add(new BasicNameValuePair("msg_content",message));
		params.add(new BasicNameValuePair("msg_id",""));
		params.add(new BasicNameValuePair("ext","004"));
		httpost.setEntity(new UrlEncodedFormEntity(params,"gbk"));
		HttpClients.createDefault().execute(httpost);
	}
	/**
	 比较日期
	 */
	public static int daysBetween(String beginDate,String endDate){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		long time1 = 0;
		long time2 = 0;
		try{
			cal.setTime(sdf.parse(beginDate));
			time1 = cal.getTimeInMillis();
			cal.setTime(sdf.parse(endDate));
			time2 = cal.getTimeInMillis();
		} catch(Exception e){
			e.printStackTrace();
		}
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	}
	/**
	 获取日期年月日
	 */
	public static String getNowTimeForSmartSchoolPackage(){
		// 设置日期格式
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(new Date());
	}


	/**
	 金额空值判断
	 */
	public static Object judgeNullObj(Object obj){
		if(obj == null){
			obj = new BigDecimal(0);
		} else{
			obj = new BigDecimal(obj.toString()).divide(new BigDecimal(100),2,4);
		}
		return obj;
	}
	/**
	 结算中心 单月第一天
	 */
	public static String getMonthFirstDay(){
		// 获取当前月第一天：
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH,0);
		// 设置为1号,当前日期既为本月第一天
		c.set(Calendar.DAY_OF_MONTH,1);
		return format.format(c.getTime());
	}
	/**
	 当月最后一天
	 */
	public static String getMonthLastDay(){
		// 获取当前月最后一天
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.DAY_OF_MONTH,ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		return format.format(ca.getTime());
	}
	/**
	 获取系统根路径
	 */
	public static String getLogRootPath(){
		String osName = getOsNameLowerCase();
		String rootPath = "";
		if(osName.contains(windows)){
			rootPath = "E:/LogMessage";
		} else if(osName.contains(linux)){
			rootPath = "/usr/singbon/logs";
		}
		return rootPath;
	}
	/**
	 获取系统名称
	 */
	public static String getOsNameLowerCase(){
		return System.getProperties().getProperty("os.name").toLowerCase();
	}
	/**
	 @param getSthCode 0 获取二维码缓存路径
	 */
	public static String getSthDependOnOs(int getSthCode){
		String osName = getOsNameLowerCase();
		String sth = "";
		if(windows.equals(osName)){
			if(getSthCode == 0){
				sth = "E:/LogMessage";
			}
		} else if(linux.equals(osName)){
			if(getSthCode == 0){
				sth = "/usr/singbon/logs";
			}
		}
		return sth;
	}
	/**
	 利用正则表达式判断字符串是否是数字
	 */
	public static boolean isNumeric(String str){
		Matcher isNum = PATTERN.matcher(str);
		return isNum.matches();
	}
	/**
	 uuid 注册物理卡号
	 */
	public static String getAutoGenerateCardSN(){
		return "FF" + UUID.randomUUID().toString().replace("-","").toUpperCase().substring(0,8);
	}
	/**
	 打印
	 */
	public static void println(Object obj){
	}
}
