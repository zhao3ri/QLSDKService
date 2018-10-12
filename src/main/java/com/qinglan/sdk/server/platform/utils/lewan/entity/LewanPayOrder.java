package com.qinglan.sdk.server.platform.utils.lewan.entity;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 乐玩支付记录
 * @author admin
 *
 */
public class LewanPayOrder  implements  RowMapper{
	 private int payId;//主键
	 private String payCode;//乐玩支付订单号
	 private String payType;//支付类型ap:支付宝 bw:银联 cd:充值卡 gcd:游戏点卡
	 private String payChannelCode;//支付通道号
	 private String yiBaoOrderId;//易宝支付订单号
	 private String gameOrderId;//游戏商的订单号
	 private String gameId;//游戏标识
	 private String gameMac;//硬件终端标识
	 private String userIP;//用户IP
	 private String userId;//用户标识
	 private String userCreateTime;//用户创建时间
	 private int userIdType;//0:IMEI 1:MAC地址 2:用户ID 3:用户Email 4:用户手机号 5:用户身份证号 6:用户纸质订单协议号
	 private String userRole;//用户角色
	 private String gameUA;//终端UA
	 private int payMoneyType;//交易货币的类型，默认156人民币(当前仅支持人民币)
	 private double payMoney;//支付金额
	 private String productType;//商品类型，1:虚拟产品2:信用卡还款3:公共事业缴费4:手机充值5:普通商品6:慈善和社会公益服务7:实物商品
	 private String productName;//游戏名称
	 private String productDesc;//游戏描述
	 private String bindId;//绑定卡ID
	 private int bindEnabledDate;//绑卡的有效期
	 private String bankName;//银行名称
	 private String lastNo;//卡号后4位
	 private int payState;//0：待付（创建的订单未支付成功） 1 : 已经提交给易宝成功 2：已付成功（订单已经支付成功）3： 付款失败 4:提交易宝失败

	 private String orderTime;//订单时间
	 private String yiBaoCallbackTime;//易宝回调时间
	 private String callbackTime;//乐玩回调游戏的时间
	 private String callbackURL;//游戏的后台回调地址
	 private String frontCallbackUrl;//游戏的前台回调地址
	 private int isCallback;//是否已经回调 0：否 1：是
	 private int isCallbackSuccess;//是否回调成功 0：否 1：是
	 private String errorCode;//错误号码
	 private String errorMsg;//错误信息
	 private int isDelete;//是否删除 0：否 1：是
	 private double paySuccessMoney;//支付成功的金额
	 
	 private String confirmAmount;//确认金额（易宝非卡号回调）
	 private String realAmount;//实际金额组（易宝非卡号回调）
	 private String cardStatus;//卡组状态（易宝非卡号回调）
	 private String expandMsg;//扩展信息
	 private String balanceAmt;//支付余额（易宝非卡号回调）
	 private String balanceAct;//余额卡号（易宝非卡号回调）
	 private String gameServerId;//游戏的服务器
	 
	 private String cardNo;//充值卡号
	 private String channelId;//渠道号 
	 private String countDay;//统计日期，年年-月月-日日
	 private int isGiveBack;//是否已经返回折扣 0：没有 1：有
	 
	 
	 
	 
	public int getIsGiveBack() {
		return isGiveBack;
	}


	public void setIsGiveBack(int isGiveBack) {
		this.isGiveBack = isGiveBack;
	}


	public String getChannelId() {
		return channelId;
	}


	public String getCountDay() {
		return countDay;
	}


	public void setCountDay(String countDay) {
		this.countDay = countDay;
	}


	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}


	public String getCardNo() {
		return cardNo;
	}


	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}


	public int getPayId() {
		return payId;
	}


	public void setPayId(int payId) {
		this.payId = payId;
	}


	public String getFrontCallbackUrl() {
		return frontCallbackUrl;
	}


	public void setFrontCallbackUrl(String frontCallbackUrl) {
		this.frontCallbackUrl = frontCallbackUrl;
	}


	public String getPayCode() {
		return payCode;
	}


	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}


	public String getPayType() {
		return payType;
	}


	public void setPayType(String payType) {
		this.payType = payType;
	}


	public String getPayChannelCode() {
		return payChannelCode;
	}


	public void setPayChannelCode(String payChannelCode) {
		this.payChannelCode = payChannelCode;
	}


	public String getYiBaoOrderId() {
		return yiBaoOrderId;
	}


	public void setYiBaoOrderId(String yiBaoOrderId) {
		this.yiBaoOrderId = yiBaoOrderId;
	}


	public String getGameOrderId() {
		return gameOrderId;
	}


	public void setGameOrderId(String gameOrderId) {
		this.gameOrderId = gameOrderId;
	}


	public String getGameId() {
		return gameId;
	}


	public void setGameId(String gameId) {
		this.gameId = gameId;
	}


	public String getGameMac() {
		return gameMac;
	}


	public void setGameMac(String gameMac) {
		this.gameMac = gameMac;
	}


	public String getUserIP() {
		return userIP;
	}


	public void setUserIP(String userIP) {
		this.userIP = userIP;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getUserCreateTime() {
		return userCreateTime;
	}


	public void setUserCreateTime(String userCreateTime) {
		this.userCreateTime = userCreateTime;
	}


	public int getUserIdType() {
		return userIdType;
	}


	public void setUserIdType(int userIdType) {
		this.userIdType = userIdType;
	}


	public String getUserRole() {
		return userRole;
	}


	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}


	public String getGameUA() {
		return gameUA;
	}


	public void setGameUA(String gameUA) {
		this.gameUA = gameUA;
	}


	public int getPayMoneyType() {
		return payMoneyType;
	}


	public void setPayMoneyType(int payMoneyType) {
		this.payMoneyType = payMoneyType;
	}


	public double getPayMoney() {
		return payMoney;
	}


	public void setPayMoney(double payMoney) {
		this.payMoney = payMoney;
	}


	public String getProductType() {
		return productType;
	}


	public void setProductType(String productType) {
		this.productType = productType;
	}


	public String getProductName() {
		return productName;
	}


	public void setProductName(String productName) {
		this.productName = productName;
	}


	public String getProductDesc() {
		return productDesc;
	}


	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}


	public String getBindId() {
		return bindId;
	}


	public void setBindId(String bindId) {
		this.bindId = bindId;
	}


	public int getBindEnabledDate() {
		return bindEnabledDate;
	}


	public void setBindEnabledDate(int bindEnabledDate) {
		this.bindEnabledDate = bindEnabledDate;
	}


	public String getBankName() {
		return bankName;
	}


	public void setBankName(String bankName) {
		this.bankName = bankName;
	}


	public String getLastNo() {
		return lastNo;
	}


	public void setLastNo(String lastNo) {
		this.lastNo = lastNo;
	}


	public int getPayState() {
		return payState;
	}


	public void setPayState(int payState) {
		this.payState = payState;
	}


	public String getOrderTime() {
		return orderTime;
	}


	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}


	public String getYiBaoCallbackTime() {
		return yiBaoCallbackTime;
	}


	public void setYiBaoCallbackTime(String yiBaoCallbackTime) {
		this.yiBaoCallbackTime = yiBaoCallbackTime;
	}


	public String getCallbackTime() {
		return callbackTime;
	}


	public void setCallbackTime(String callbackTime) {
		this.callbackTime = callbackTime;
	}


	public String getCallbackURL() {
		return callbackURL;
	}


	public void setCallbackURL(String callbackURL) {
		this.callbackURL = callbackURL;
	}


	public int getIsCallback() {
		return isCallback;
	}


	public void setIsCallback(int isCallback) {
		this.isCallback = isCallback;
	}


	public int getIsCallbackSuccess() {
		return isCallbackSuccess;
	}


	public void setIsCallbackSuccess(int isCallbackSuccess) {
		this.isCallbackSuccess = isCallbackSuccess;
	}


	public String getErrorCode() {
		return errorCode;
	}


	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}


	public String getErrorMsg() {
		return errorMsg;
	}


	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}


	public int getIsDelete() {
		return isDelete;
	}


	public void setIsDelete(int isDelete) {
		this.isDelete = isDelete;
	}


	public double getPaySuccessMoney() {
		return paySuccessMoney;
	}


	public void setPaySuccessMoney(double paySuccessMoney) {
		this.paySuccessMoney = paySuccessMoney;
	}


	public String getConfirmAmount() {
		return confirmAmount;
	}


	public void setConfirmAmount(String confirmAmount) {
		this.confirmAmount = confirmAmount;
	}


	public String getRealAmount() {
		return realAmount;
	}


	public void setRealAmount(String realAmount) {
		this.realAmount = realAmount;
	}


	public String getCardStatus() {
		return cardStatus;
	}


	public void setCardStatus(String cardStatus) {
		this.cardStatus = cardStatus;
	}


	public String getExpandMsg() {
		return expandMsg;
	}


	public void setExpandMsg(String expandMsg) {
		this.expandMsg = expandMsg;
	}


	public String getBalanceAmt() {
		return balanceAmt;
	}


	public void setBalanceAmt(String balanceAmt) {
		this.balanceAmt = balanceAmt;
	}


	public String getBalanceAct() {
		return balanceAct;
	}


	public void setBalanceAct(String balanceAct) {
		this.balanceAct = balanceAct;
	}


	public String getGameServerId() {
		return gameServerId;
	}


	public void setGameServerId(String gameServerId) {
		this.gameServerId = gameServerId;
	}


	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		LewanPayOrder lpo=new LewanPayOrder();
		lpo.setPayId(rs.getInt("payId"));
		lpo.setPayCode(rs.getString("payCode"));
		lpo.setPayType(rs.getString("payType"));
		lpo.setPayChannelCode(rs.getString("payChannelCode"));
		lpo.setYiBaoOrderId(rs.getString("yiBaoOrderId"));
		lpo.setGameOrderId(rs.getString("gameOrderId"));
		lpo.setGameId(rs.getString("gameId"));
		lpo.setGameMac(rs.getString("gameMac"));
		lpo.setUserIP(rs.getString("userIP"));
		lpo.setUserId(rs.getString("userId"));
		lpo.setUserIdType(rs.getInt("userIdType"));
		lpo.setUserCreateTime(rs.getString("userCreateTime"));
		lpo.setUserRole(rs.getString("userRole"));
		lpo.setGameUA(rs.getString("gameUA"));
		lpo.setPayMoneyType(rs.getInt("payMoneyType"));
		lpo.setPayMoney(rs.getDouble("payMoney"));
		lpo.setProductType(rs.getString("productType"));
		lpo.setProductName(rs.getString("productName"));
		lpo.setProductDesc(rs.getString("productDesc"));
		lpo.setBindId(rs.getString("bindId"));
		lpo.setBindEnabledDate(rs.getInt("bindEnabledDate"));
		lpo.setBankName(rs.getString("bankName"));
		lpo.setLastNo(rs.getString("lastNo"));
		lpo.setPayState(rs.getInt("payState"));
		lpo.setOrderTime(rs.getString("orderTime"));
		lpo.setYiBaoCallbackTime(rs.getString("yiBaoCallbackTime"));
		lpo.setCallbackTime(rs.getString("callbackTime"));
		lpo.setCallbackURL(rs.getString("callbackUrl"));
		lpo.setIsCallback(rs.getInt("isCallback"));
		lpo.setIsCallbackSuccess(rs.getInt("isCallbackSuccess"));
		lpo.setErrorCode(rs.getString("errorCode"));
		lpo.setErrorMsg(rs.getString("errorMsg"));
		lpo.setIsDelete(rs.getInt("isDelete"));
		lpo.setPaySuccessMoney(rs.getDouble("paySuccessMoney"));
		lpo.setConfirmAmount(rs.getString("confirmAmount"));
		lpo.setRealAmount(rs.getString("realAmount"));
		lpo.setCardStatus(rs.getString("cardStatus"));
		lpo.setExpandMsg(rs.getString("expandMsg"));
		lpo.setBalanceAmt(rs.getString("balanceAmt"));
		lpo.setBalanceAct(rs.getString("balanceAct"));
		lpo.setGameServerId(rs.getString("gameServerId"));
		lpo.setFrontCallbackUrl(rs.getString("frontCallbackUrl"));
		lpo.setCardNo(rs.getString("cardNo"));
		lpo.setChannelId(rs.getString("channelId"));
		lpo.setCountDay(rs.getString("countDay"));
		lpo.setIsGiveBack(rs.getInt("isGiveBack"));
		return lpo;
	}
	
	
}
