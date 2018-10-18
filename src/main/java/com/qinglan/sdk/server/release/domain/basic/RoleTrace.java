package com.qinglan.sdk.server.release.domain.basic;

import java.util.Date;

import com.qinglan.sdk.server.common.DateUtils;
import com.qinglan.sdk.server.common.JsonMapper;

public class RoleTrace {
	private String rid;//角色ID
	private String rname;//角色名
	private Long fltime;//首次登录时间
	private Long lltime;//最后登录时间
	private Long fctime;//首次创建角色时间
	private Long lutime;//最后退出时间
	private Integer lttoday;//登录次数
	private Long lhtime;//最后心跳时间
	private Long fptime;//首次支付时间
	private Long lptime;//最后支付时间
	private Integer pttoday;//今天支付次数
	private Long precord;//支付35天记录
	private Long lrecord;//登录35天记录 

	/**
	 * @return the rid
	 */
	public String getRid() {
		return rid;
	}

	/**
	 * @param rid the rid to set
	 */
	public void setRid(String rid) {
		this.rid = rid;
	}

	/**
	 * @return the rname
	 */
	public String getRname() {
		return rname;
	}

	/**
	 * @param rname the rname to set
	 */
	public void setRname(String rname) {
		this.rname = rname;
	}

	
	/**
	 * @return the fltime
	 */
	public Long getFltime() {
		return fltime;
	}

	/**
	 * @param fltime the fltime to set
	 */
	public void setFltime(Long fltime) {
		this.fltime = fltime;
	}

	/**
	 * @return the lltime
	 */
	public Long getLltime() {
		return lltime;
	}

	/**
	 * @param lltime the lltime to set
	 */
	public void setLltime(Long lltime) {
		this.lltime = lltime;
	}

	/**
	 * @return the fctime
	 */
	public Long getFctime() {
		return fctime;
	}

	/**
	 * @param fctime the fctime to set
	 */
	public void setFctime(Long fctime) {
		this.fctime = fctime;
	}

	/**
	 * @return the lutime
	 */
	public Long getLutime() {
		return lutime;
	}

	/**
	 * @param lutime the lutime to set
	 */
	public void setLutime(Long lutime) {
		this.lutime = lutime;
	}

	/**
	 * @return the lttoday
	 */
	public Integer getLttoday() {
		return lttoday;
	}

	/**
	 * @param lttoday the lttoday to set
	 */
	public void setLttoday(Integer lttoday) {
		this.lttoday = lttoday;
	}

	/**
	 * @return the lhtime
	 */
	public Long getLhtime() {
		return lhtime;
	}

	/**
	 * @param lhtime the lhtime to set
	 */
	public void setLhtime(Long lhtime) {
		this.lhtime = lhtime;
	}

	/**
	 * @return the fptime
	 */
	public Long getFptime() {
		return fptime;
	}

	/**
	 * @param fptime the fptime to set
	 */
	public void setFptime(Long fptime) {
		this.fptime = fptime;
	}

	/**
	 * @return the lptime
	 */
	public Long getLptime() {
		return lptime;
	}

	/**
	 * @param lptime the lptime to set
	 */
	public void setLptime(Long lptime) {
		this.lptime = lptime;
	}

	/**
	 * @return the pttoday
	 */
	public Integer getPttoday() {
		return pttoday;
	}

	/**
	 * @param pttoday the pttoday to set
	 */
	public void setPttoday(Integer pttoday) {
		this.pttoday = pttoday;
	}

	/**
	 * @return the precord
	 */
	public Long getPrecord() {
		return precord;
	}

	/**
	 * @param precord the precord to set
	 */
	public void setPrecord(Long precord) {
		this.precord = precord;
	}

	/**
	 * @return the lrecord
	 */
	public Long getLrecord() {
		return lrecord;
	}

	/**
	 * @param lrecord the lrecord to set
	 */
	public void setLrecord(Long lrecord) {
		this.lrecord = lrecord;
	}

	//是否角色首次登陆游戏
	public int isFirstRoleLogin(){
		if (fltime == null || fltime == 0) {
			return 1;
		}
		return 0;
	}
	
	public int isFirstLoginMonth() {
		if (fltime == null || fltime == 0 || lltime == null || lltime == 0) {
			return 1;
		}
		if (DateUtils.sameMonth(lltime, new Date().getTime())) {
			return 0;
		}
		return 1;
	}

	//最近35天登陆情况
	public Long late35Login(){
		if (lrecord == null) {
			return 1L;
		}
		Integer loginDel = DateUtils.getIntervalDays(lltime, System.currentTimeMillis());
		if (loginDel > 0) {
			String record = Long.toBinaryString(lrecord);
			if (record.length() > 34) {
				record = record.substring(record.length() - 34);
			}
			return (Long.parseLong(record, 2) << loginDel) + 1;
		}
		return lrecord;
	}
	
	//最近35天支付情况
	public Long late35Pay(){
		if (precord == null) {
			return 1L;
		}
		Integer loginDel = DateUtils.getIntervalDays(lptime, System.currentTimeMillis());
		if (loginDel > 0) {
			String record = Long.toBinaryString(precord);
			if (record.length() > 34) {
				record = record.substring(record.length() - 34);
			}
			return (Long.parseLong(record, 2) << loginDel) + 1;
		}
		return precord;
	}
	
	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
