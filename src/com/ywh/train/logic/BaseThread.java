package com.ywh.train.logic;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.ywh.train.DamaUtil;
import com.ywh.train.ResManager;
import com.ywh.train.gui.RobTicket;

public abstract class BaseThread extends Thread {
	protected TrainClient client;
	protected RobTicket rob;
	/**
	 * 构造函数
	 * 
	 * @param robTicket
	 */
	public BaseThread(TrainClient client, RobTicket rob) {
		this.client = client;
		this.rob = rob;
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
			JLabel label = new JLabel(ResManager.getString("LogicThread.23"), JLabel.CENTER); //$NON-NLS-1$
			label.setIcon(new ImageIcon(image));
			label.setText(ResManager.getString("LogicThread.24") + randCodeByRob); //$NON-NLS-1$
			CodeMouseAdapter cma = new CodeMouseAdapter(randCodeByRob,url);
			label.addMouseListener(cma);
			String input = JOptionPane.showInputDialog(rob.getFrame(), label,
					ResManager.getString("LogicThread.25"), JOptionPane.DEFAULT_OPTION); //$NON-NLS-1$
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
		int ret = DamaUtil.dama2.d2Buf("694f40021a34f601b069a415cabf4d5d", "test", "test", image, (short)20, (long)42, vcode);
		if(ret > 0){
			randCodeByRob = vcode[0];
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
			label.setText(ResManager.getString("LogicThread.26") + randCodeByRob); //$NON-NLS-1$
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
