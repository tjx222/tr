package com.tmser.train.bean;


/**
 * 功能描述
 * 
 * @author Tmser
 * @since 2011-11-23
 * @version 1.0
 */
public class TrainQueryInfo {
	/**
	 * 功能描述
	 * @author tjx1222
	 * @version 2.0
	 */
	
	/**
	 * 车次代码
	 */
	private String trainNo;// 序号
	
	/**
	 * 车次，如T145
	 */
	private String stationTrainCode;
	
	private String startStationTelecode;
	private String startStationName;
	private String endStationTelecode;
	private String endStationName;
	
	private String fromStationTelecode;
	private String fromStationName;
	private String toStationTelecode;
	private String toStationName;
	private String dayDifference;
	private String trainClassName;
	private Boolean canWebBuy;

	private String lishiValue;
	private String ypInfo;
	private String controlTrainDay;
	private String startTrainDate; // 出发日期
	private String seatFeature;
	private String ypEx;
	private String trainSeatFeature;
	private String seatTypes;
	private String locationCode;//到站代号
	private String fromStationNo; //起点站序号，始发站是01
	private String toStationNo; //终点站序号
	private Integer controlDay;
	private String saleTime;
	private String isSupportCard;
	
	// private String codeLink; // 车次链接
	private String startTime;// 发时
	private String arriveTime; // 到时
	private String lishi;// 历时
	private String ypInfoDetail; //预定详细信息
	private String secretStr;//加密字符串
	private String selectedSeat;

	
	private String buss_seat; // 商务座
	private String best_seat;// 特等座(余票)
	private String one_seat;// 一等座(余票)
	private String two_seat;// 二等座(余票)
	private String vag_sleeper;// 高级软卧(余票)
	private String soft_sleeper;// 软卧(余票)
	private String hard_sleeper;// 硬卧(余票)
	private String soft_seat;// 软座(余票)
	private String hard_seat;// 硬座(余票)
	private String none_seat;// 无座(余票)
	private String other_seat;// 其他
	
	private String rangeDate;//乘车时间段
	public String getStartStationTelecode() {
		return startStationTelecode;
	}
	public void setStartStationTelecode(String startStationTelecode) {
		this.startStationTelecode = startStationTelecode;
	}
	public String getStartStationName() {
		return startStationName;
	}
	public void setStartStationName(String startStationName) {
		this.startStationName = startStationName;
	}
	public String getEndStationTelecode() {
		return endStationTelecode;
	}
	public void setEndStationTelecode(String endStationTelecode) {
		this.endStationTelecode = endStationTelecode;
	}
	public String getEndStationName() {
		return endStationName;
	}
	public void setEndStationName(String endStationName) {
		this.endStationName = endStationName;
	}
	public String getFromStationTelecode() {
		return fromStationTelecode;
	}
	public void setFromStationTelecode(String fromStationTelecode) {
		this.fromStationTelecode = fromStationTelecode;
	}
	public String getFromStationName() {
		return fromStationName;
	}
	public void setFromStationName(String fromStationName) {
		this.fromStationName = fromStationName;
	}
	public String getToStationTelecode() {
		return toStationTelecode;
	}
	public void setToStationTelecode(String toStationTelecode) {
		this.toStationTelecode = toStationTelecode;
	}
	public String getToStationName() {
		return toStationName;
	}
	public void setToStationName(String toStationName) {
		this.toStationName = toStationName;
	}
	public String getDayDifference() {
		return dayDifference;
	}
	public void setDayDifference(String dayDifference) {
		this.dayDifference = dayDifference;
	}
	public String getTrainClassName() {
		return trainClassName;
	}
	public void setTrainClassName(String trainClassName) {
		this.trainClassName = trainClassName;
	}
	public Boolean getCanWebBuy() {
		return canWebBuy;
	}
	public void setCanWebBuy(String canWebBuy) {
		this.canWebBuy = "Y".equals(canWebBuy);
	}
	public String getLishiValue() {
		return lishiValue;
	}
	public void setLishiValue(String lishiValue) {
		this.lishiValue = lishiValue;
	}
	public String getYpInfo() {
		return ypInfo;
	}
	public void setYpInfo(String ypInfo) {
		this.ypInfo = ypInfo;
	}
	public String getControlTrainDay() {
		return controlTrainDay;
	}
	public void setControlTrainDay(String controlTrainDay) {
		this.controlTrainDay = controlTrainDay;
	}
	public String getStartTrainDate() {
		return startTrainDate;
	}
	public void setStartTrainDate(String startTrainDate) {
		this.startTrainDate = startTrainDate;
	}
	public String getSeatFeature() {
		return seatFeature;
	}
	public void setSeatFeature(String seatFeature) {
		this.seatFeature = seatFeature;
	}
	public String getYpEx() {
		return ypEx;
	}
	public void setYpEx(String ypEx) {
		this.ypEx = ypEx;
	}
	public String getTrainSeatFeature() {
		return trainSeatFeature;
	}
	public void setTrainSeatFeature(String trainSeatFeature) {
		this.trainSeatFeature = trainSeatFeature;
	}
	public String getSeatTypes() {
		return seatTypes;
	}
	public void setSeatTypes(String seatTypes) {
		this.seatTypes = seatTypes;
	}
	public String getFromStationNo() {
		return fromStationNo;
	}
	public void setFromStationNo(String fromStationNo) {
		this.fromStationNo = fromStationNo;
	}
	public String getToStationNo() {
		return toStationNo;
	}
	public void setToStationNo(String toStationNo) {
		this.toStationNo = toStationNo;
	}
	public String getRangeDate() {
		return rangeDate;
	}
	public void setRangeDate(String rangeDate) {
		this.rangeDate = rangeDate;
	}
	public String getTrainNo() {
		return trainNo;
	}
	public void setTrainNo(String trainNo) {
		this.trainNo = trainNo;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getSelectedSeat() {
		return selectedSeat;
	}
	public void setSelectedSeat(String selectedSeat) {
		this.selectedSeat = selectedSeat;
	}
	public String getBuss_seat() {
		return buss_seat;
	}
	public void setBuss_seat(String buss_seat) {
		this.buss_seat = buss_seat;
	}
	
	public String getBest_seat() {
		return best_seat;
	}
	public void setBest_seat(String best_seat) {
		this.best_seat = best_seat;
	}
	public String getOne_seat() {
		return one_seat;
	}
	public void setOne_seat(String one_seat) {
		this.one_seat = one_seat;
	}
	public String getTwo_seat() {
		return two_seat;
	}
	public void setTwo_seat(String two_seat) {
		this.two_seat = two_seat;
	}
	public String getVag_sleeper() {
		return vag_sleeper;
	}
	public void setVag_sleeper(String vag_sleeper) {
		this.vag_sleeper = vag_sleeper;
	}
	public String getSoft_sleeper() {
		return soft_sleeper;
	}
	public void setSoft_sleeper(String soft_sleeper) {
		this.soft_sleeper = soft_sleeper;
	}
	public String getHard_sleeper() {
		return hard_sleeper;
	}
	public void setHard_sleeper(String hard_sleeper) {
		this.hard_sleeper = hard_sleeper;
	}
	public String getSoft_seat() {
		return soft_seat;
	}
	public void setSoft_seat(String soft_seat) {
		this.soft_seat = soft_seat;
	}
	public String getHard_seat() {
		return hard_seat;
	}
	public void setHard_seat(String hard_seat) {
		this.hard_seat = hard_seat;
	}
	public String getNone_seat() {
		return none_seat;
	}
	public void setNone_seat(String none_seat) {
		this.none_seat = none_seat;
	}
	public String getOther_seat() {
		return other_seat;
	}
	public void setOther_seat(String other_seat) {
		this.other_seat = other_seat;
	}
	
	public String getYpInfoDetail() {
		return ypInfoDetail;
	}
	public void setYpInfoDetail(String ypInfoDetail) {
		this.ypInfoDetail = ypInfoDetail;
	}
	public String getSecretStr() {
		return secretStr;
	}
	public void setSecretStr(String secretStr) {
		this.secretStr = secretStr;
	}
	
	public String getLocationCode() {
		return locationCode;
	}
	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}
	
	public String getStationTrainCode() {
		return stationTrainCode;
	}
	public void setStationTrainCode(String stationTrainCode) {
		this.stationTrainCode = stationTrainCode;
	}
	
	public Integer getControlDay() {
		return controlDay;
	}
	public void setControlDay(Integer controlDay) {
		this.controlDay = controlDay;
	}
	public String getSaleTime() {
		return saleTime;
	}
	public void setSaleTime(String saleTime) {
		this.saleTime = saleTime;
	}
	public String getIsSupportCard() {
		return isSupportCard;
	}
	public void setIsSupportCard(String isSupportCard) {
		this.isSupportCard = isSupportCard;
	}
	public String getArriveTime() {
		return arriveTime;
	}
	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}
	
	public String getLishi() {
		return lishi;
	}
	public void setLishi(String lishi) {
		this.lishi = lishi;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder()
				.append("TrainQueryInfo [trainCode=")
				.append(stationTrainCode)
				.append(", trainNo=")
				.append(trainNo)
				.append(", fromStation=")
				.append(fromStationName)
				.append(", fromStationCode=")
				.append(fromStationTelecode)
				.append(", startTime=")
				.append(startTime)
				.append(", toStation=")
				.append(toStationName)
				.append(", toStationCode=")
				.append(toStationTelecode)
				.append(", endTime=")
				.append(arriveTime)
				.append(", takeTime=")
				.append(lishi)
				.append(", buss_seat=")
				.append(buss_seat)
				.append(", best_seat=")
				.append(best_seat)
				.append(", one_seat=")
				.append(one_seat)
				.append(", two_seat=")
				.append(two_seat)
				.append(", vag_sleeper=")
				.append(vag_sleeper)
				.append(", soft_sleeper=")
				.append(soft_sleeper)
				.append(", hard_sleeper=")
				.append(hard_sleeper)
				.append(", soft_seat=")
				.append(soft_seat)
				.append(", hard_seat=")
				.append(hard_seat)
				.append(", none_seat=")
				.append(none_seat)
				.append(", other_seat=")
				.append(other_seat)
				.append(", ypInfoDetail=")
				.append(ypInfoDetail)
				.append(", from_station_no=")
				.append(fromStationNo)
				.append(", to_station_no=")
				.append(toStationNo)
				.append(", locationCode=")
				.append(locationCode)
				.append(", secretStr=")
				.append(secretStr)				
				.append("]");
		return builder.toString();
	}

	
}
