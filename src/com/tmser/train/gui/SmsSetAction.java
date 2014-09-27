package com.tmser.train.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;

import com.tmser.train.Config;
import com.tmser.train.ResManager;

/**
 * 短信设置菜单类
 * @author tmser
 *
 */
 class SmsSetAction extends AbstractAction{
	private static final long serialVersionUID = -798538352042404436L;
	JFrame parentsFrame;
	JRadioButton rbEnableProxy,rbDisableProxy;
	JButton btnApply;

	protected SmsSetAction(JFrame frame) {
		this.parentsFrame = frame;
	}

	public void actionPerformed(ActionEvent e) {
        final JDialog dialog = new JDialog(parentsFrame,
                ResManager.getString("RobTicket.sms.setting"),
                true);
        JPanel panelProxy = new JPanel();
        panelProxy.setLayout(new BorderLayout(10,5));
		Box vBox = Box.createVerticalBox();
		vBox.setBorder(new TitledBorder(ResManager
				.getString("RobTicket.sms.labtxt"))); 
		final ButtonGroup group = new ButtonGroup();
		
		boolean smsEnable = Boolean.valueOf(Config.getProperty("sms.enable"));
		rbEnableProxy = new JRadioButton(ResManager
				.getString("RobTicket.rbEnableProxy"),smsEnable);
		
		rbDisableProxy = new JRadioButton(ResManager
				.getString("RobTicket.rbDisableProxy"),!smsEnable);
		group.add(rbDisableProxy);
		group.add(rbEnableProxy);
		
		vBox.add(rbDisableProxy);
		vBox.add(Box.createVerticalStrut(5));
		vBox.add(rbEnableProxy);
		
		Box vBox1 = Box.createVerticalBox();
		
		Box hBoxport = Box.createHorizontalBox();
		JLabel lbProxyPort = new JLabel(ResManager.getString("RobTicket.sms.phone")+":");
		MaskFormatter mf = null;
		try {
			mf = new MaskFormatter("***********");
			mf.setValidCharacters(" 0123456789");
			mf.setPlaceholder(" ");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		final JTextField txtProxyPort = new JFormattedTextField(mf);
		txtProxyPort.setText(Config.getProperty("sms.phone"));
		hBoxport.add(lbProxyPort);
		hBoxport.add(Box.createHorizontalStrut(20));
		hBoxport.add(txtProxyPort);
		
		Box hBox = Box.createHorizontalBox();
		JLabel lbDamaUsername = new JLabel(ResManager.getString("RobTicket.sms.pguid"));
		final JTextField txtSmsPgUid = new JTextField(Config.getProperty("sms.pguid"),16);
		txtSmsPgUid.setToolTipText(ResManager.getString("RobTicket.sms.pglabel"));
		hBox.add(lbDamaUsername);
		hBox.add(Box.createHorizontalStrut(14));
		hBox.add(txtSmsPgUid);
		
		vBox1.add(Box.createVerticalStrut(5));
		vBox1.add(hBoxport);
		vBox1.add(Box.createVerticalStrut(5));
		vBox1.add(hBox);
		
		JPanel plApply = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnApply = new JButton(ResManager.getString("RobTicket.btnApply"));
		plApply.add(btnApply);
		btnApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!checkFormat(txtProxyPort.getText())){
					JOptionPane.showConfirmDialog(dialog, ResManager.getString("RobTicket.sms.validphone"),
							ResManager.getString("RobTicket.msg.tip"),JOptionPane.OK_OPTION);
					return;
				}
				Config.setProperty("sms.enable",String.valueOf(group.isSelected(rbEnableProxy.getModel())));
				Config.setProperty("sms.phone",txtProxyPort.getText().trim());
				Config.setProperty("sms.pguid",txtSmsPgUid.getText().trim());
				if(Config.saveConfig()){
					dialog.setVisible(false);
				}else{
					JOptionPane.showConfirmDialog(dialog, ResManager.getString("RobTicket.msg.failture"),
							ResManager.getString("RobTicket.msg.tip"),JOptionPane.OK_OPTION);
				}
			}
		
		boolean checkFormat(String value){
			String v = value.trim();
			if(value.trim().length() > 0 &&
				v.matches("^1[3|4|5|7|8][0-9]\\d{8}")){
				return true;
			}
			return false;
		}
		}
		);
		
		panelProxy.setBorder(new EmptyBorder(10, 10, 10, 10));
		panelProxy.add(vBox,BorderLayout.NORTH);
		panelProxy.add(vBox1);
		panelProxy.add(plApply,BorderLayout.SOUTH);
		
		dialog.setResizable(false);
		dialog.setContentPane(panelProxy);
		dialog.setSize(new Dimension(300,240));
        dialog.setLocationRelativeTo(parentsFrame);
        dialog.setVisible(true);
	}
}
