package com.ywh.train.logic;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.ywh.train.Config;
import com.ywh.train.Constants;
import com.ywh.train.ResManager;
import com.ywh.train.bean.Result;
import com.ywh.train.bean.TokenAndTicket;
import com.ywh.train.bean.TrainQueryInfo;
import com.ywh.train.bean.UserInfo;
import com.ywh.train.gui.RobTicket;

/**
 * 订票逻辑
 * 
 * @author cafebabe
 * @since 2011-11-27
 * @version 1.0
 */
public class LogicThread extends BaseThread {

	private volatile Thread blinker = this;
	/**
	 * 构造函数
	 * 
	 * @param robTicket
	 */
	public LogicThread(TrainClient client, RobTicket rob) {
		super(client,rob);
	}

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
			while (blinker == thisThread) {
				Result rs = new Result();
				
				while (Constants.isLoginSuc && blinker == thisThread) {//订票
					String randCode = null;
					rob.console(ResManager.getString("LogicThread.4")); //$NON-NLS-1$
					List<TrainQueryInfo> allTrain = client.queryTrain(
							rob.getFromCity(), rob.getToCity(), rob.getStartDate(),
							rob.getRangDate());
					
					AutoTrainAI ai = new AutoTrainAI(allTrain, rob.getTrainSet(),//查询出所有票
							rob.getTrainNo(), rob.getFromCity(), rob.getToCity());
					if (ai.getAllTrains().size() == 0) {
						rob.console(MessageFormat.format(ResManager.getString("LogicThread.5") ,rob.getStartDate(),rob.getRangDate(),rob.getFromCity(), rob.getToCity())); //$NON-NLS-1$
						if (rob.getTrainSet()[Constants.isLockTrain]) {
							Thread.sleep(5000);
							continue;
						}
					} else {
						rob.console(MessageFormat.format(ResManager.getString("LogicThread.6"),rob.getStartDate(),rob.getRangDate(),rob.getFromCity(),rob.getToCity(),ai.getTrainNoView(ai.getCandidateTrains()))); //$NON-NLS-1$
					}
					
					TrainQueryInfo goHomeTrain = null;//选中的车次
					Map<String, TrainQueryInfo> spTrains = ai.getSpecificTrains(); //指定车次
					for (String trainNo : rob.getTrainNo()) {//获取指定车次中的一辆
						TrainQueryInfo spTrain = spTrains.get(trainNo);
						if (spTrain == null) {
							rob.console(MessageFormat.format(ResManager.getString("LogicThread.7"), trainNo)); //$NON-NLS-1$
						} else if (Constants.getTrainSeatName(ai.getSpecificSeatAI(spTrain)) == null) {
							rob.console(MessageFormat.format(ResManager.getString("LogicThread.8"),spTrain.getTrainNo(), ai.getTrainSeatView(spTrain))); //$NON-NLS-1$
						} else {
							goHomeTrain = spTrain;
							break;
						}
					}
					
					if (goHomeTrain == null && rob.getTrainSet()[Constants.isLockTrain]){//锁定指定车次，重新查询
						Thread.sleep(Config.getSleepTime());
						continue;
					}  else if(!rob.getTrainSet()[Constants.isLockTrain] && goHomeTrain == null){//没锁定指定车次，从其他有票车中选择
						rob.console(ResManager.getString("LogicThread.9")); //$NON-NLS-1$
						Map<String, TrainQueryInfo> caTrains = ai.getCandidateTrains();
						for (TrainQueryInfo train : caTrains.values()) {
							if(ai.getSpecificSeatTrains().containsKey(train.getTrainNo())) {//其他车次中找有指定票的车
								goHomeTrain = train;
								break;
							} else {
								rob.console(MessageFormat.format(ResManager.getString("LogicThread.10"),train.getTrainNo(),ai.getTrainSeatView(train))); //$NON-NLS-1$
							}
						}
					}
					
					if (goHomeTrain == null) {
						rob.console(ResManager.getString("LogicThread.11")); //$NON-NLS-1$
						Thread.sleep(5000);
						continue;
					}
					
					rob.console(MessageFormat.format(ResManager.getString("LogicThread.12"),goHomeTrain.getTrainNo(),goHomeTrain.getFromStation(), goHomeTrain.getStartTime(),goHomeTrain.getToStation(), goHomeTrain.getEndTime(),goHomeTrain.getTakeTime())); //$NON-NLS-1$
					String seat = ai.getSpecificSeatAI(goHomeTrain);
					rob.console(MessageFormat.format(ResManager.getString("LogicThread.13"),Constants.getTrainSeatName(seat))); //$NON-NLS-1$
					if (seat.equals(Constants.NONE_SEAT)) {//用户车票座位
						for (UserInfo ui : rob.getSelectUsers()) {
							ui.setSeatType(Constants.HARD_SEAT);
						}
					} else {
						for (UserInfo ui : rob.getSelectUsers()) {
							ui.setSeatType(seat);
						}
					}
					Thread.sleep(1000);
					
					List<NameValuePair> formparams = null;
					TokenAndTicket token = null; 
					do{				//查询出车票后点击预定按钮及输入验证码点击提交按钮两步
						rob.console(ResManager.getString("LogicThread.15"));
						rs = client.book(rob.getRangDate(), rob.getStartDate(), goHomeTrain);
						Thread.sleep(2000);
						randCode = getRandCodeDailog(Constants.ORDER_CODE_URL);
						if(randCode == null || "".equals(randCode)){
							rob.console("RandCode: "+randCode);
							rs.setState(Result.FAIL);
							Thread.sleep(4000);
						}else{
							token = new TokenAndTicket(rs.getMsg());
							formparams = client.setOrderForm(randCode,token, rob.getSelectUsers(), goHomeTrain);
							rob.console("RandCode: "+randCode);
							rs = client.checkOrder(formparams,randCode);
							rob.console("QuerOrder = "+rs.getMsg());
							Thread.sleep(1000);
						}
					}while (rs.getState() == Result.FAIL && blinker == thisThread);
					
					if( blinker == thisThread){ //自动ajax 步骤，查询车票余量
						rob.console(ResManager.getString("LogicThread.28"));
						String seattype = rob.getSelectUsers().get(0).getSeatType();
						rs = client.getCount(token,seattype,goHomeTrain); //
						while(rs.getState() == Result.FAIL && blinker == thisThread){
							/*if(rs.getWaitTime() <= 0){
								int option = JOptionPane.showConfirmDialog(rob.getFrame(), ResManager.getString("RobTicket.needWait"),
										ResManager.getString("RobTicket.needWaitTitle"), JOptionPane.YES_NO_OPTION);
							    if(option ==  JOptionPane.YES_OPTION){
										Thread.sleep(1000*rs);
							    }
							}*/
							 Thread.sleep(3000);
							 rs = client.getCount(token,seattype,goHomeTrain);
						}
						
						rs = client.queryOrderQueue(formparams); //余量足够，点击确定，正式订票
						if(rs.getState()  == Result.FAIL) {
							rob.console(ResManager.getString("LogicThread.27"));
							Thread.sleep(5000);
							rob.clearMsg();
							continue;
						}
						//ajax 步骤 排队拿号
						rs = client.queryWaitTime(); 
						while(rs.getState() == Result.FAIL && blinker == thisThread){
							 rob.console(MessageFormat.format(ResManager.getString("LogicThread.29"),String.valueOf(rs.getWaitTime())));
							 rob.console(rs.getMsg());
							 Thread.sleep(5000);
							 rs = client.queryWaitTime();
						}
						
						//拿到订单号正式提交
						formparams.add(new BasicNameValuePair("orderSequence_no", rs.getMsg()));//增加订单号
						rs = client.payOrder(formparams,rs.getMsg());
				
						Thread.sleep(2000);
						rob.console(ResManager.getString("LogicThread.19")); 
						rs = client.queryOrder(); //查询未完成订单，确定是否成功
						rob.console(rs.getMsg());
						if (rs.getState() == Result.HAVE_NO_PAY_TICKET) {
							JOptionPane.showMessageDialog(rob.getFrame(),
								ResManager.getString("LogicThread.20")); //$NON-NLS-1$
							rob.reset(true);
							break;
						} else {
							rob.console(ResManager.getString("LogicThread.21")); //$NON-NLS-1$
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
			}
		} finally {
			rob.console(ResManager.getString("LogicThread.22")); //$NON-NLS-1$	
		}

	}
	/**
	 * @param isEnd
	 *            The isEnd to set.
	 */
	public void setEnd(boolean isEnd) {
		 blinker = null;
	}
}
