package com.qinglan.sdk.server.common;

/**
 * IMSI = MCC(Mobile Country Code) + MNC(Mobile Network Code) + MIN(Mobile Subscriber Identification Number) </p>
 * China MCC : 460 </p>
 * <b>ImsiParser only support China region imsi parser</b>
 */
public class ImsiParser {
	
	public static int OPERATORS_MOBILE = 1;
	public static int OPERATORS_UNICOM = 2;
	public static int OPERATORS_TELECOM = 3;
	public static int OPERATORS_UNKNOWN = -1;

	public static int getOperator(String imsi) {
		if(null == imsi || imsi.trim().isEmpty()) return OPERATORS_UNKNOWN;
		
		if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007"))
			return OPERATORS_MOBILE;
		
		if (imsi.startsWith("46001") || imsi.startsWith("46006"))
			return OPERATORS_UNICOM;
		
	    if (imsi.startsWith("46003") || imsi.startsWith("46005"))
	    	return OPERATORS_TELECOM;
	    
		return OPERATORS_UNKNOWN;
	}
	
	/**
	 * check the imsi is belong China or not
	 * @param imsi
	 * @return true/false
	 */
	public static boolean isCN(String imsi) {
		if(imsi.startsWith("460")) {
			return true;
		}
		return false;
	}
}
