package com.ywh.train.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.ywh.train.Config;
import com.ywh.train.DamaUtil;
import com.ywh.train.ResManager;
import com.ywh.train.Util;

class DamaSetAction extends AbstractAction{
	public static final String LOGIN_PANEL = "login_panel";
	public static final String SUCC_PANEL = "succ_panel";
	
	/** 字段注释 */
	private static final long serialVersionUID = -7920604352042404436L;
	private RobTicket rob;
	private JRadioButton rbEnableDama,rbDisableDama;
	private JButton btnApply,btnSch,btnReg,btnCharge,btnBuyCard;
	private CardLayout card;
	private JPanel panel;
	private JLabel lbMsg;
	private JDialog dialog;
	
	protected DamaSetAction(RobTicket rob) {
		this.rob = rob;
	}

	public void actionPerformed(ActionEvent e) {
       if(dialog == null){
    	   createViewer();
       }
       dialog.setVisible(true);
	}
	
	private void createViewer(){
		 dialog = new JDialog(rob.getFrame(),
	                ResManager.getString("RobTicket.miDama"),
	                true);
	        JPanel panelProxy = new JPanel();
	        panelProxy.setLayout(new BorderLayout(10,5));
			Box vBox = Box.createVerticalBox();
			vBox.setBorder(new TitledBorder(ResManager
					.getString("RobTicket.panelDama"))); 
			final ButtonGroup group = new ButtonGroup();
			
			rbEnableDama = new JRadioButton(ResManager
					.getString("RobTicket.rbEnableProxy"),Config.isUseDama());
			
			rbDisableDama = new JRadioButton(ResManager
					.getString("RobTicket.rbDisableProxy"),!Config.isUseDama());
			group.add(rbDisableDama);
			group.add(rbEnableDama);
			
			vBox.add(rbDisableDama);
			vBox.add(Box.createVerticalStrut(5));
			vBox.add(rbEnableDama);
			
			Box vBox1 = Box.createVerticalBox();
			
			card = new CardLayout();
			panel = new JPanel(card);
			panel.setBorder(new TitledBorder(ResManager
					.getString("RobTicket.panelBorderName")));
		
			Box hBox = Box.createHorizontalBox();
			JLabel lbDamaUsername = new JLabel(ResManager.getString("RobTicket.txtUsername"));
			final JTextField txtDamaUsername = new JTextField(Config.getUsername(),16);
			hBox.add(lbDamaUsername);
			hBox.add(Box.createHorizontalStrut(14));
			hBox.add(txtDamaUsername);
			
			Box hBoxport = Box.createHorizontalBox();
			JLabel lbDamaPass = new JLabel(ResManager.getString("RobTicket.txtPassword"));
			final JPasswordField txtDamaPass = new JPasswordField(Config.getPassword(),16);
			hBoxport.add(lbDamaPass);
			hBoxport.add(Box.createHorizontalStrut(20));
			hBoxport.add(txtDamaPass);
			
			JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,2));
			btnSch = new JButton(ResManager.getString("RobTicket.btnSch"));
			btnReg = new JButton(ResManager.getString("RobTicket.btnReg"));
			btnPanel.add(btnSch);
			btnPanel.add(btnReg);
			
			btnSch.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String username = txtDamaUsername.getText().trim();
					String password = String.valueOf(txtDamaPass.getPassword());
					int balance = 0;
					int ret = DamaUtil.login(username,password);
					if(ret == DamaUtil.SUCC){
						balance = DamaUtil.queryBalance();
						if(balance > 0){
							lbMsg.setText(ResManager.getString("RobTicket.lbBalance",new String[]{balance+""}));
							card.show(panel, SUCC_PANEL);
						}
					}else if(ret == DamaUtil.ERR_NOT_INIT ){
						JOptionPane.showMessageDialog(dialog, ResManager.getString("RobTicket.msg.damaUserFailture"),
								ResManager.getString("RobTicket.msg.tip"),JOptionPane.ERROR_MESSAGE);
					}else{
						JOptionPane.showMessageDialog(dialog, ResManager.getString("RobTicket.msg.damaFailture"),
								ResManager.getString("RobTicket.msg.tip"),JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			
			vBox1.add(hBox);
			vBox1.add(Box.createVerticalStrut(5));
			vBox1.add(hBoxport);
			vBox1.add(Box.createVerticalStrut(5));
			vBox1.add(btnPanel);
			vBox1.setBorder(new EmptyBorder(5,5,5,5));
			
			Box vBox2 = Box.createVerticalBox();
			
			lbMsg = new JLabel();
			btnCharge = new JButton(ResManager.getString("RobTicket.btnCharge"));
			btnCharge.setToolTipText(ResManager.getString("RobTicket.btnCharge.tip"));
			btnBuyCard = new JButton(ResManager.getString("RobTicket.btnBuyCard"));
			btnBuyCard.setToolTipText(ResManager.getString("RobTicket.btnBuyCard.tip"));
			btnCharge.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JLabel label = new JLabel(); 
					label.setIcon(ResManager.createImageIcon("chongzhi.png"));
					label.setText(ResManager.getString("RobTicket.lbCard")); //$NON-NLS-1$
					String cardno = JOptionPane.showInputDialog(dialog,label,
							ResManager.getString("RobTicket.btnCharge"),
							JOptionPane.DEFAULT_OPTION);
					if(cardno == null){
						return ;
					}
					if(!"".equals(cardno.trim())){
						int ret = DamaUtil.recharge(cardno);
						if(ret == DamaUtil.ERR_INVALID_CARDNO){
							JOptionPane.showMessageDialog(dialog, ResManager.getString("RobTicket.msg.invalidCard"),
									ResManager.getString("RobTicket.msg.tip"),JOptionPane.ERROR_MESSAGE);
						}else if(ret > 0){
							JOptionPane.showMessageDialog(dialog, ResManager.getString("RobTicket.msg.rechargeSucc"),
									ResManager.getString("RobTicket.msg.tip"),JOptionPane.OK_OPTION);
							lbMsg.setText(ResManager.getString("RobTicket.lbBalance",new String[]{ret+""}));
						}else{
							JOptionPane.showMessageDialog(dialog, ResManager.getString("RobTicket.msg.damaFailture"),
									ResManager.getString("RobTicket.msg.tip"),JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
			
			
			JPanel btnPanel1 = new JPanel(new FlowLayout(FlowLayout.CENTER,10,2));
			btnPanel1.add(btnCharge);
			btnPanel1.add(btnBuyCard);
			
			vBox2.add(lbMsg);
			vBox2.add(Box.createVerticalStrut(5));
			vBox2.add(btnPanel1);
			vBox2.setBorder(new EmptyBorder(5,5,5,5));
			
			panel.add(vBox1,LOGIN_PANEL);
			panel.add(vBox2,SUCC_PANEL);
			
			JPanel plApply = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			btnApply = new JButton(ResManager.getString("RobTicket.btnApply"));
			plApply.add(btnApply);
			
			btnApply.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					boolean isUseDama = group.isSelected(rbEnableDama.getModel());
					Config.setUseDama(isUseDama);
					rob.setAuto(isUseDama);
					Config.setPassword(new String(txtDamaPass.getPassword()).trim());
					Config.setUsername(txtDamaUsername.getText().trim());
					if(isUseDama && (Util.isBlank(Config.getUsername()) 
								|| Util.isBlank(Config.getPassword()))){
						JOptionPane.showMessageDialog(dialog, ResManager.getString("RobTicket.msg.damaNotSet"),
								ResManager.getString("RobTicket.msg.tip"),JOptionPane.ERROR_MESSAGE);
						return;
					}
					if(Config.saveConfig()){
						dialog.setVisible(false);
					}else{
						JOptionPane.showConfirmDialog(dialog, ResManager.getString("RobTicket.msg.failture"),
								ResManager.getString("RobTicket.msg.tip"),JOptionPane.OK_OPTION);
					}
					}
				}
			);
			
			panelProxy.setBorder(new EmptyBorder(10, 10, 10, 10));
			panelProxy.add(vBox,BorderLayout.NORTH);
			panelProxy.add(panel);
			panelProxy.add(plApply,BorderLayout.SOUTH);
			
			dialog.setContentPane(panelProxy);
			dialog.setSize(new Dimension(300,290));
	        dialog.setLocationRelativeTo(rob.getFrame());
	}
	
}