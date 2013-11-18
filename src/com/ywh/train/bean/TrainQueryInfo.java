package com.ywh.train.bean;


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
	 * @author cafebabe
	 * @since 2011-11-27 
	 * @version 1.0
	 */
	private String trainCode;// 序号
	private String trainNo; // 车次
	private String trainDate; // 出发日期
	// private String codeLink; // 车次链接
	private String fromStation;// 发站
	private String fromStationCode; // 发站code
	private String startTime;// 发时
	private String toStation;// 到站
	private String toStationCode;// 到站code
	private String endTime; // 到时
	private String takeTime;// 历时
	private String ypInfoDetail; //预定详细信息
	private String mmStr;//加密字符串
	private String selectedSeat;
	
	private String from_station_no; //起点站序号，始发站是01
	private String to_station_no; //终点站序号
	
	private String locationCode; //到站代号

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
	
	
	public String getRangeDate() {
		return rangeDate;
	}
	public void setRangeDate(String rangeDate) {
		this.rangeDate = rangeDate;
	}
	public String getTrainCode() {
		return trainCode;
	}
	public void setTrainCode(String trainCode) {
		this.trainCode = trainCode;
	}
	public String getTrainNo() {
		return trainNo;
	}
	public void setTrainNo(String trainNo) {
		this.trainNo = trainNo;
	}
	/**
	 * @return Returns the trainDate.
	 */
	public String getTrainDate() {
		return trainDate;
	}
	/**
	 * @param trainDate The trainDate to set.
	 */
	public void setTrainDate(String trainDate) {
		this.trainDate = trainDate;
	}
	public String getFromStation() {
		return fromStation;
	}
	public void setFromStation(String fromStation) {
		this.fromStation = fromStation;
	}
	public String getFromStationCode() {
		return fromStationCode;
	}
	public void setFromStationCode(String fromStationCode) {
		this.fromStationCode = fromStationCode;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getToStation() {
		return toStation;
	}
	public void setToStation(String toStation) {
		this.toStation = toStation;
	}
	public String getToStationCode() {
		return toStationCode;
	}
	public void setToStationCode(String toStationCode) {
		this.toStationCode = toStationCode;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getTakeTime() {
		return takeTime;
	}
	public void setTakeTime(String takeTime) {
		this.takeTime = takeTime;
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
	public String getMmStr() {
		return mmStr;
	}
	public void setMmStr(String mmStr) {
		this.mmStr = mmStr;
	}
	
	public String getFrom_station_no() {
		return from_station_no;
	}
	public void setFrom_station_no(String fromStationNo) {
		from_station_no = fromStationNo;
	}
	public String getTo_station_no() {
		return to_station_no;
	}
	public void setTo_station_no(String toStationNo) {
		to_station_no = toStationNo;
	}
	public String getLocationCode() {
		return locationCode;
	}
	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder()
				.append("TrainQueryInfo [trainCode=")
				.append(trainCode)
				.append(", trainNo=")
				.append(trainNo)
				.append(", fromStation=")
				.append(fromStation)
				.append(", fromStationCode=")
				.append(fromStationCode)
				.append(", startTime=")
				.append(startTime)
				.append(", toStation=")
				.append(toStation)
				.append(", toStationCode=")
				.append(toStationCode)
				.append(", endTime=")
				.append(endTime)
				.append(", takeTime=")
				.append(takeTime)
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
				.append(from_station_no)
				.append(", to_station_no=")
				.append(to_station_no)
				.append(", locationCode=")
				.append(locationCode)
				.append(", mmStr=")
				.append(mmStr)				
				.append("]");
		return builder.toString();
	}

	
}
