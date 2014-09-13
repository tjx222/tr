package com.tmser.train.logic;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;

import com.tmser.train.Constants;
import com.tmser.train.NetConnectException;
import com.tmser.train.ResManager;
import com.tmser.train.bean.Result;
import com.tmser.train.bean.TokenAndTicket;
import com.tmser.train.bean.TrainQueryInfo;
import com.tmser.train.bean.UserInfo;
import com.tmser.train.gui.RobTicket;

/**
 * 订票逻辑
 * 
 * @author Tmser
 * @since 2011-11-27
 * @version 1.0
 */
public class LogicThread extends BaseThread {
	public LogicThread(RobTicket rob) {
		super(rob);
	}

	private static final Logger log = Logger.getLogger(LogicThread.class);
	private volatile Thread blinker = this;

	/**
	 * override 方法<p>
	 * 对run方法进行改进:<p>
	 * 1.增加帐号密码错误的判断，防止做无用的登录操作<p>
	 * 2.改进验证码输入，不必每次都输，大大提高登录效率，可以在短时间内进行大量登录 <p>
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Thread thisThread = Thread.currentThread();
		try {
			Result rs = null;
			start: 
			while (Constants.isLoginSuc && blinker == thisThread) {//订票
					Thread.sleep(2000);
					String randCode = null;
					rob.console(ResManager.getString("LogicThread.4")); 
					boolean isLocked = rob.getTrainSet()[Constants.isLockTrain];
					
					AutoTrainAI ai = queryTrain();//查询出所有票
					if (ai.getAllTrains().size() == 0 ) {
						rob.console(MessageFormat.format(ResManager.getString("LogicThread.5") ,rob.getStartDate(),rob.getRangDate(),rob.getFromCity(), rob.getToCity())); 
						continue;			//锁定定车次没有车票，直接返回
					} else {
						rob.console(MessageFormat.format(ResManager.getString("LogicThread.6"),rob.getStartDate(),rob.getRangDate(),rob.getFromCity(),rob.getToCity(),ai.getTrainNoView(ai.getCandidateTrains()))); 
					}
					
					List<TrainQueryInfo> ghs = new ArrayList<TrainQueryInfo>();
					
					Map<String, TrainQueryInfo> spTrains = ai.getSpecificTrains(); //指定车次
					for (String stationTrainCode : rob.getStationTrainCode()) { //遍历用户指定车次
						TrainQueryInfo spTrain = spTrains.get(stationTrainCode);
						if (spTrain == null) {
							rob.console(MessageFormat.format(ResManager.getString("LogicThread.7"), stationTrainCode)); 
						} else if (Constants.getTrainSeatName(ai.getSpecificSeatAI(spTrain)) == null) {
							rob.console(MessageFormat.format(ResManager.getString("LogicThread.8"),spTrain.getStationTrainCode(), ai.getTrainSeatView(spTrain))); 
						} else {
							ghs.add(spTrain);
						}
					}
					
					if (ghs.size() == 0 && isLocked){//锁定指定车次，重新查询
						continue;
					} 
					
					//没锁定指定车次，添加其他有票车
					if(!isLocked){
						rob.console(ResManager.getString("LogicThread.9")); 
						Map<String, TrainQueryInfo> caTrains = ai.getCandidateTrains();
						for (TrainQueryInfo train : caTrains.values()) {
							if(ai.getSpecificSeatTrains().containsKey(train.getStationTrainCode())) {//其他车次中找有指定票的车
								ghs.add(train);
							} else {
								rob.console(MessageFormat.format(ResManager.getString("LogicThread.10"),train.getStationTrainCode(),ai.getTrainSeatView(train))); 
							}
						}
					}
					
					
					if (ghs.size() == 0) {
						rob.console(ResManager.getString("LogicThread.11")); 
						continue;
					}
					
					for(TrainQueryInfo goHomeTrain : ghs){
						rob.console(MessageFormat.format(ResManager.getString("LogicThread.12"),goHomeTrain.getStationTrainCode(),goHomeTrain.getFromStationName(), goHomeTrain.getStartTime(),goHomeTrain.getToStationName(), goHomeTrain.getArriveTime(),goHomeTrain.getLishi())); 
						String seat = ai.getSpecificSeatAI(goHomeTrain);
						rob.console(MessageFormat.format(ResManager.getString("LogicThread.13"),Constants.getTrainSeatName(seat))); 
						if (seat.equals(Constants.NONE_SEAT)) {//用户车票座位
							for (UserInfo ui : rob.getSelectUsers()) {
								ui.setSeatType(Constants.HARD_SEAT);
							}
						} else {
							for (UserInfo ui : rob.getSelectUsers()) {
								ui.setSeatType(seat);
							}
						}
						List<NameValuePair> formparams = null;
						TokenAndTicket token = null; 
						Thread.sleep(1500);
						//查询出车票后点击预定按钮及输入验证码点击提交按钮两步
						rob.console(ResManager.getString("LogicThread.15"));
						rs = client.book(rob.getTicketType(), rob.getStartDate(), goHomeTrain);
							
						if(rs.getState() != Result.SUCC){
							rob.console(rs.getMsg());
							if(Result.UNLOGIN == rs.getState()){
								SwingUtilities.invokeAndWait(new LoginThread(rob));	
								continue start;
							}else if(Result.HAVE_NO_PAY_TICKET== rs.getState()){
								blinker = null;
								break start;
							}
							continue;
						}
						
						token = new TokenAndTicket(rs.getMsg());
						do{
							Thread.sleep(500);
							randCode = getRandCodeDailog(Constants.ORDER_CODE_URL);
							if(randCode == null){
									rob.console(ResManager.getString("LogicThread.30"));
									blinker = null;
									break;
							}
							formparams = client.setOrderForm(randCode,token, rob.getSelectUsers(), goHomeTrain);
							Thread.sleep(500);
							rs = client.checkOrder(formparams,token);
						}while (rs.getState() == Result.FAIL && blinker == thisThread);
						
						if( blinker == thisThread){ //自动ajax 步骤，查询车票余量
							Thread.sleep(500);
							rob.console(ResManager.getString("LogicThread.33"));
							String seattype = rob.getSelectUsers().get(0).getSeatType();
							rs = client.getCount(token,seattype,rob.getTicketType(),goHomeTrain); //
							if(rs.getState() != Result.SUCC) {
								rob.console(rs.getMsg());
								continue;
							}else{
								rob.console(MessageFormat.format(ResManager.getString("LogicThread.35"),rs.getWaitTime()));
							}
							
							Thread.sleep(500);
							rs = client.queryOrderQueue(formparams,token,rob.getTicketType()); //余量足够，点击确定，正式订票
							if(rs.getState()  == Result.FAIL) {
								rob.console(ResManager.getString("LogicThread.27"));
								rob.clearMsg();
								continue;
							}
							
							if(Result.REPEAT == rs.getState()){//有未完成订单，直接结束
								rob.console(rs.getMsg());
								break start;
							}
							//ajax 步骤 排队拿号
							Thread.sleep(500);
							rob.console(ResManager.getString("LogicThread.28"));
							rs = client.queryWaitTime(token); 
							while(rs.getState() == Result.FAIL && blinker == thisThread){
								 rob.console(MessageFormat.format(ResManager.getString("LogicThread.29"),String.valueOf(rs.getWaitTime())));
								 Thread.sleep(500);
								 rs = client.queryWaitTime(token);
							}
							
							if(Result.HASORDER == rs.getState()){
								 rob.console(rs.getMsg());
								 continue;
							}
							
							if(blinker != thisThread){
								break start;
							}
							
							//拿到订单号正式提交
							Thread.sleep(500);
							rs = client.payOrder(rs.getMsg());
							if(Result.SUCC == rs.getState()){
								JOptionPane.showMessageDialog(rob.getFrame(),
										ResManager.getString("LogicThread.20")); 
								rob.console(ResManager.getString("LogicThread.20"));
								rob.reset(true);
							}else{
								rob.console(ResManager.getString("LogicThread.19")); 
								rs = client.queryOrder(); //查询未完成订单，确定是否成功
								rob.console(rs.getMsg());
								if (rs.getState() == Result.SUCC) {
									JOptionPane.showMessageDialog(rob.getFrame(),
										ResManager.getString("LogicThread.20")); 
									rob.reset(true);
									rob.console(ResManager.getString("LogicThread.20"));
								} else {
									rob.console(ResManager.getString("LogicThread.21")); 
								}
							}
						}
					}
			}
		}catch(NetConnectException e) {
			rob.console(ResManager.getString("RobTicket.err.net"));
		} catch(Exception e){
			log.error(e);
			e.printStackTrace();
			rob.console(ResManager.getString("RobTicket.err.unkwnow"));
		} finally {
			rob.console(ResManager.getString("LogicThread.22")); 
			rob.reset(true);
		}
	}
	
	/**
	 * 查询所有车次
	 * @return
	 */
	private AutoTrainAI queryTrain(){
		List<TrainQueryInfo> allTrain = client.queryTrain(
				rob.getFromCity(), rob.getToCity(), rob.getStartDate(),
				rob.getRangDate(),rob.getLandDate(),rob.getTicketType());
		
		return new AutoTrainAI(allTrain, rob.getTrainSet(),//查询出所有票
				rob.getStationTrainCode(), rob.getFromCity(), rob.getToCity());
		
	}
	/**
	 * @param isEnd
	 *            The isEnd to set.
	 */
	public void setEnd(boolean isEnd) {
		 blinker = null;
	}

	@Override
	public boolean getIsAuto() {
		return rob.isAutocode();
	}
}
