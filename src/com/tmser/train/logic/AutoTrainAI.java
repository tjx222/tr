package com.tmser.train.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tmser.train.Constants;
import com.tmser.train.bean.TrainQueryInfo;

/**
 * 选择车次及席别的AI
 * 
 * @author cafebabe
 * @since 2012-1-12
 * @version 1.0
 */
public class AutoTrainAI {
	/** 所有车 */
	private Map<String, TrainQueryInfo> allTrains = new HashMap<String, TrainQueryInfo>();
	/** 指定的车 */
	private Map<String, TrainQueryInfo> specificTrains = new HashMap<String, TrainQueryInfo>();
	/** 指定以外的车 */
	private Map<String, TrainQueryInfo> candidateTrains = new HashMap<String, TrainQueryInfo>();
	/** 所有有票的车 */
	private Map<String, TrainQueryInfo> specificSeatTrains = new HashMap<String, TrainQueryInfo>();
	private boolean[] trainSet;

	/**
	 * 构造函数
	 * 
	 * @param toCity
	 * @param fromCity
	 */
	public AutoTrainAI(List<TrainQueryInfo> trains, boolean trainSet[],
			Set<String> trainNos, String fromCity, String toCity) {
		this.trainSet = trainSet;
		for (TrainQueryInfo train : trains) {
			if (trainSet[Constants.isStrinStation]) { //精确匹配起点和终点站站点
				if (!train.getFromStationName().equals(fromCity.trim())
						|| !train.getToStationName().equals(toCity.trim())) {
					continue;
				}
			}
			allTrains.put(train.getStationTrainCode(), train);
			if (trainNos.contains(train.getStationTrainCode())) {//指定车次的车
				specificTrains.put(train.getStationTrainCode(), train);
			} else {
				if (getSeatAI(train) != null) {//不是指定车次，但有票的车
					candidateTrains.put(train.getStationTrainCode(), train);
				}
			}
			if (getSpecificSeatAI(train) != null) {//所有指定座位票的车，包括没有指定的车次
				specificSeatTrains.put(train.getStationTrainCode(), train);
			}
		}
	}

	/**
	 * 所有列车
	 * 
	 * @return Returns the allTrains.
	 */
	public Map<String, TrainQueryInfo> getAllTrains() {
		return allTrains;
	}

	/**
	 * 获取指定的列车
	 * 
	 * @return Returns the specificTrains.
	 */
	public Map<String, TrainQueryInfo> getSpecificTrains() {
		return specificTrains;
	}

	/**
	 * 获取非指定列车中有票的列车
	 * 
	 * @return Returns the candidateTrains.
	 */
	public Map<String, TrainQueryInfo> getCandidateTrains() {
		return candidateTrains;
	}

	/**
	 * 获取指定席别的列车
	 * 
	 * @return Returns the specificSeatTrains.
	 */
	public Map<String, TrainQueryInfo> getSpecificSeatTrains() {
		return specificSeatTrains;
	}

	/**
	 * 检查该车是否有指定的席别信息
	 * 
	 * @param train
	 * @return
	 */
	public String getSpecificSeatAI(TrainQueryInfo train) {
		if (trainSet[Constants.isNeed_TWO_SEAT]
				&& getTrainSeat(train.getTwo_seat()) > 0) {
			return Constants.TWO_SEAT;
		}

		if (trainSet[Constants.isNeed_ONE_SEAT]
				&& getTrainSeat(train.getOne_seat()) > 0) {
			return Constants.ONE_SEAT;
		}

		if (trainSet[Constants.isNeed_HARD_SEAT]
				&& getTrainSeat(train.getHard_seat()) > 0) {
			return Constants.HARD_SEAT;
		}

		if (trainSet[Constants.isNeed_HARD_SLEEPER]
				&& getTrainSeat(train.getHard_sleeper()) > 0) {
			return Constants.HARD_SLEEPER;
		}

		if (trainSet[Constants.isNeed_SOFT_SLEEPER]
				&& getTrainSeat(train.getSoft_sleeper()) > 0) {
			return Constants.SOFT_SLEEPER;
		}

		if (trainSet[Constants.isNeed_VAG_SLEEPER]
				&& getTrainSeat(train.getVag_sleeper()) > 0) {
			return Constants.VAG_SLEEPER;
		}

		if (trainSet[Constants.isNeed_NONE_SEAT]
				&& getTrainSeat(train.getNone_seat()) > 0) {
			if (train.getTrainNo().startsWith(Constants.TRAIN_D)) {
				return Constants.TWO_SEAT;
			}
			return Constants.HARD_SEAT;
		}

		return null;
	}

	/**
	 * 获取座位数量
	 * 
	 * @param key
	 * @return
	 */
	public int getTrainSeat(String key) {
		Integer seat = Constants.getTrainSeatMap().get(key);
		if (seat == null) {
			return Integer.parseInt(key);
		} else {
			return seat;
		}
	}

	public String getTrainNoView(Map<String, TrainQueryInfo> trains) {
		StringBuilder ans = new StringBuilder();
		for (String train : trains.keySet()) {
			ans.append("[" + train + "]");
		}
		return ans.toString();
	}

	public String getTrainSeatView(TrainQueryInfo train) {
		StringBuilder ans = new StringBuilder();
		if (getTrainSeat(train.getTwo_seat()) > 0) {
			ans.append("[" + Constants.getTrainSeatName(Constants.TWO_SEAT)
					+ "]");
		}

		if (getTrainSeat(train.getOne_seat()) > 0) {
			ans.append("[" + Constants.getTrainSeatName(Constants.ONE_SEAT)
					+ "]");
		}

		if (getTrainSeat(train.getHard_seat()) > 0) {
			ans.append("[" + Constants.getTrainSeatName(Constants.HARD_SEAT)
					+ "]");
		}

		if (getTrainSeat(train.getHard_sleeper()) > 0) {
			ans.append("[" + Constants.getTrainSeatName(Constants.HARD_SLEEPER)
					+ "]");
		}

		if (getTrainSeat(train.getSoft_sleeper()) > 0) {
			ans.append("[" + Constants.getTrainSeatName(Constants.SOFT_SLEEPER)
					+ "]");
		}

		if (getTrainSeat(train.getVag_sleeper()) > 0) {
			ans.append("[" + Constants.getTrainSeatName(Constants.VAG_SLEEPER)
					+ "]");
		}

		if (getTrainSeat(train.getNone_seat()) > 0) {
			ans.append("[" + Constants.getTrainSeatName(Constants.NONE_SEAT)
					+ "]");
		}
		if (getTrainSeat(train.getOther_seat()) > 0) {
			ans.append("[" + Constants.getTrainSeatName(Constants.OTHER_SEAT)
					+ "]");
		}
		return ans.toString();
	}

	/**
	 * 检查是否有票(二等座->一等座->硬座->硬卧->软卧->高级->无座->无票)
	 * 
	 * @param train
	 * @return
	 */
	public String getSeatAI(TrainQueryInfo train) {

		if (getTrainSeat(train.getTwo_seat()) > 0) {
			return Constants.TWO_SEAT;
		}

		if (getTrainSeat(train.getOne_seat()) > 0) {
			return Constants.ONE_SEAT;
		}

		if (getTrainSeat(train.getHard_seat()) > 0) {
			return Constants.HARD_SEAT;
		}

		if (getTrainSeat(train.getHard_sleeper()) > 0) {
			return Constants.HARD_SLEEPER;
		}

		if (getTrainSeat(train.getSoft_sleeper()) > 0) {
			return Constants.SOFT_SLEEPER;
		}

		if (getTrainSeat(train.getVag_sleeper()) > 0) {
			return Constants.VAG_SLEEPER;
		}

		if (getTrainSeat(train.getNone_seat()) > 0) {
			if (train.getTrainNo().startsWith(Constants.TRAIN_D)) {
				return Constants.TWO_SEAT;
			}
			return Constants.HARD_SEAT;
		}
		return null;
	}

}
