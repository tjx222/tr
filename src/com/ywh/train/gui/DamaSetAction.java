package com.ywh.train.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.ywh.train.Config;
import com.ywh.train.ResManager;

class DamaSetAction extends AbstractAction{
	/** 字段注释 */
	private static final long serialVersionUID = -7920604352042404436L;
	JFrame parentsFrame;
	JRadioButton rbEnableDama,rbDisableDama;
	JButton btnApply;

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
				.getString("RobTicket.panelProxyName"))); 
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
		
		Box hBox = Box.createHorizontalBox();
		JLabel lbDamaUsername = new JLabel(ResManager.getString("RobTicket.txtUsername"));
		JTextField txtDamaUsername = new JTextField(Config.getProxyIp(),16);
		hBox.add(lbDamaUsername);
		hBox.add(Box.createHorizontalStrut(14));
		hBox.add(txtDamaUsername);
		
		Box hBoxport = Box.createHorizontalBox();
		JLabel lbDamaPass = new JLabel(ResManager.getString("RobTicket.txtPassword"));
		JTextField txtDamaPass = new JTextField(Config.getProxyPort()+"",16);
		hBoxport.add(lbDamaPass);
		hBoxport.add(Box.createHorizontalStrut(20));
		hBoxport.add(txtDamaPass);
		
		vBox1.add(hBox);
		vBox1.add(Box.createVerticalStrut(5));
		vBox1.add(hBoxport);
		
		btnApply = new JButton(ResManager.getString("RobTicket.btnApply"));
		
		panelProxy.setBorder(new EmptyBorder(10, 10, 10, 10));
		panelProxy.add(vBox,BorderLayout.NORTH);
		panelProxy.add(vBox1);
		panelProxy.add(btnApply,BorderLayout.SOUTH);
		
		dialog.setContentPane(panelProxy);
		dialog.setSize(new Dimension(300,240));
        dialog.setLocationRelativeTo(parentsFrame);
        dialog.setVisible(true);
	}

}