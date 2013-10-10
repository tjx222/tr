package com.ywh.train.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.ywh.train.Config;
import com.ywh.train.ResManager;

class DamaSetAction extends AbstractAction{
	public static final String LOGIN_PANEL = "login_panel";
	public static final String SUCC_PANEL = "succ_panel";
	
	/** 字段注释 */
	private static final long serialVersionUID = -7920604352042404436L;
	JFrame parentsFrame;
	JRadioButton rbEnableDama,rbDisableDama;
	JButton btnApply,btnLogin,btnReg,btnCharge,btnBuyCard;
	CardLayout card;
	JPanel panel;
	JLabel lbMsg;
	
	protected DamaSetAction(JFrame frame) {
		this.parentsFrame = frame;
	}

	public void actionPerformed(ActionEvent e) {
        final JDialog dialog = new JDialog(parentsFrame,
                ResManager.getString("RobTicket.miDama"),
                true);
        JPanel panelProxy = new JPanel();
        panelProxy.setLayout(new BorderLayout(10,5));
		Box vBox = Box.createVerticalBox();
		vBox.setBorder(new TitledBorder(ResManager
				.getString("RobTicket.panelDama"))); 
		ButtonGroup group = new ButtonGroup();
		
		rbEnableDama = new JRadioButton(ResManager
				.getString("RobTicket.rbEnableProxy"),Config.isUseProxy());
		
		rbDisableDama = new JRadioButton(ResManager
				.getString("RobTicket.rbDisableProxy"),!Config.isUseProxy());
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
		JTextField txtDamaUsername = new JTextField(Config.getUserName(),16);
		hBox.add(lbDamaUsername);
		hBox.add(Box.createHorizontalStrut(14));
		hBox.add(txtDamaUsername);
		
		Box hBoxport = Box.createHorizontalBox();
		JLabel lbDamaPass = new JLabel(ResManager.getString("RobTicket.txtPassword"));
		JPasswordField txtDamaPass = new JPasswordField(Config.getPassword(),16);
		hBoxport.add(lbDamaPass);
		hBoxport.add(Box.createHorizontalStrut(20));
		hBoxport.add(txtDamaPass);
		
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,2));
		btnLogin = new JButton(ResManager.getString("RobTicket.btnLogin"));
		btnReg = new JButton(ResManager.getString("RobTicket.btnReg"));
		btnPanel.add(btnLogin);
		btnPanel.add(btnReg);
		
		vBox1.add(hBox);
		vBox1.add(Box.createVerticalStrut(5));
		vBox1.add(hBoxport);
		vBox1.add(Box.createVerticalStrut(5));
		vBox1.add(btnPanel);
		vBox1.setBorder(new EmptyBorder(5,5,5,5));
		
		Box vBox2 = Box.createVerticalBox();
		
		lbMsg = new JLabel();
		btnCharge = new JButton();
		btnBuyCard = new JButton();
		
		JPanel btnPanel1 = new JPanel(new FlowLayout(FlowLayout.CENTER,10,2));
		btnPanel1.add(btnCharge);
		btnPanel1.add(btnBuyCard);
		
		vBox2.add(lbMsg);
		vBox2.add(Box.createVerticalStrut(5));
		vBox2.add(btnPanel1);
		vBox2.setBorder(new EmptyBorder(5,5,5,5));
		
		panel.add(vBox1,LOGIN_PANEL);
		panel.add(vBox2,SUCC_PANEL);
		card.show(panel,SUCC_PANEL);
		
		JPanel plApply = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnApply = new JButton(ResManager.getString("RobTicket.btnApply"));
		plApply.add(btnApply);
		
		panelProxy.setBorder(new EmptyBorder(10, 10, 10, 10));
		panelProxy.add(vBox,BorderLayout.NORTH);
		panelProxy.add(panel);
		panelProxy.add(plApply,BorderLayout.SOUTH);
		
		dialog.setContentPane(panelProxy);
		dialog.setSize(new Dimension(300,290));
        dialog.setLocationRelativeTo(parentsFrame);
        dialog.setVisible(true);
	}

}