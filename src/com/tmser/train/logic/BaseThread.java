package com.tmser.train.logic;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.tmser.train.DamaUtil;
import com.tmser.train.ResManager;
import com.tmser.train.gui.RobTicket;

public abstract class BaseThread extends Thread {
	protected TrainClient client;
	protected RobTicket rob;
	/**
	 * 构造函数
	 * 
	 * @param robTicket
	 */
	public BaseThread( RobTicket rob) {
		this.client = rob.getClient();
		this.rob = rob;
	}
	
	/**
	 * 是否自动验证
	 * @return
	 */
	public boolean getIsAuto() {
		return false;
	}

	/**
	 * 获得自动识别的验证or用户输入
	 */
	protected String getRandCodeDailog(String url) {
		byte[] image = client.getCodeByte(url);
		String randCodeByRob = "";
		int count = 10; // 避免死循环
		while (rob.isAutocode() && randCodeByRob.length() != 4 && count-- > 0) {
			randCodeByRob = getCode(image);
		}
		if (!rob.isAutocode()) {//手动输入验证码
			JLabel label = new JLabel(ResManager.getString("LogicThread.23"), JLabel.CENTER);
			label.setIcon(new ImageIcon(image));
			CodeMouseAdapter cma = new CodeMouseAdapter(randCodeByRob,url);
			label.addMouseListener(cma);
			String input = JOptionPane.showInputDialog(rob.getFrame(), label,
					ResManager.getString("LogicThread.25"), JOptionPane.DEFAULT_OPTION);
				//randCodeByRob = cma.getRandCodeByRob();
			//} else {
		    randCodeByRob = input;
		}
		return randCodeByRob;
	}
	
	/**
	 * 验证码识别
	 * @param image
	 * @return
	 */
	private String getCode(byte[] image){
		String randCodeByRob = "";
  	    String[] vcode = new String[1];
		int ret = DamaUtil.d2Buf(image, vcode);
		if(ret > 0){
			randCodeByRob = vcode[0];
		}else if(ret == DamaUtil.ERR_NOT_INIT){
			JOptionPane.showConfirmDialog(rob.getFrame(), ResManager.getString("RobTicket.msg.damaUserFailture"),
					ResManager.getString("RobTicket.msg.tip"),JOptionPane.OK_OPTION);
		}else{
			rob.console(ResManager.getString("dama2 error:"+ ret));
		}
		return randCodeByRob;
	}

	class CodeMouseAdapter extends MouseAdapter {
		private String randCodeByRob="";
		private String url;

		/**
		 * 构造函数
		 * 
		 * @param CodeMouseAdapter
		 */

		public CodeMouseAdapter(String randCodeByRob,String url) {
			this.randCodeByRob = randCodeByRob;
			this.url = url;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			byte[] image = client.getCodeByte(url);
			if(rob.isAutocode())
				randCodeByRob = getCode(image);
			JLabel label = (JLabel) e.getSource();
			label.setIcon(new ImageIcon(image));
		}

		public String getRandCodeByRob() {
			return randCodeByRob;
		}
	}

	/**
	 * @return Returns the client.
	 */
	public TrainClient getClient() {
		return client;
	}

	/**
	 * @param client
	 *            The client to set.
	 */
	public void setClient(TrainClient client) {
		this.client = client;
	}

}
