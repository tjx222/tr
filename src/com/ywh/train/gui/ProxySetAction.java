package com.ywh.train.gui;

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

import com.ywh.train.Config;
import com.ywh.train.ResManager;

/**
 * 代理设置菜单类
 * @author tmser
 *
 */
 class ProxySetAction extends AbstractAction{
	private static final long serialVersionUID = -798538352042404436L;
	JFrame parentsFrame;
	JRadioButton rbEnableProxy,rbDisableProxy;
	JButton btnApply;

	protected ProxySetAction(JFrame frame) {
		this.parentsFrame = frame;
	}

	public void actionPerformed(ActionEvent e) {
        final JDialog dialog = new JDialog(parentsFrame,
                ResManager.getString("RobTicket.miProxy"),
                true);
        JPanel panelProxy = new JPanel();
        panelProxy.setLayout(new BorderLayout(10,5));
		Box vBox = Box.createVerticalBox();
		vBox.setBorder(new TitledBorder(ResManager
				.getString("RobTicket.panelProxyName"))); 
		final ButtonGroup group = new ButtonGroup();
		
		rbEnableProxy = new JRadioButton(ResManager
				.getString("RobTicket.rbEnableProxy"),Config.isUseProxy());
		
		rbDisableProxy = new JRadioButton(ResManager
				.getString("RobTicket.rbDisableProxy"),!Config.isUseProxy());
		group.add(rbDisableProxy);
		group.add(rbEnableProxy);
		
		vBox.add(rbDisableProxy);
		vBox.add(Box.createVerticalStrut(5));
		vBox.add(rbEnableProxy);
		
		Box vBox1 = Box.createVerticalBox();
		
		Box hBox = Box.createHorizontalBox();
		JLabel lbProxyIp = new JLabel(ResManager.getString("RobTicket.lbProxyIp")+":");
		final JTextField txtProxyIp = new JTextField(Config.getProxyIp(),16);
		hBox.add(lbProxyIp);
		hBox.add(Box.createHorizontalStrut(32));
		hBox.add(txtProxyIp);
		
		Box hBoxport = Box.createHorizontalBox();
		JLabel lbProxyPort = new JLabel(ResManager.getString("RobTicket.lbProxyPort")+":");
		MaskFormatter mf = null;
		try {
			mf = new MaskFormatter("*****");
			mf.setValidCharacters(" 0123456789");
			mf.setPlaceholder(" ");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		final JTextField txtProxyPort = new JFormattedTextField(mf);
		txtProxyPort.setText(Config.getProxyPort()+"");
		hBoxport.add(lbProxyPort);
		hBoxport.add(Box.createHorizontalStrut(20));
		hBoxport.add(txtProxyPort);
		
		vBox1.add(hBox);
		vBox1.add(Box.createVerticalStrut(5));
		vBox1.add(hBoxport);
		
		JPanel plApply = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnApply = new JButton(ResManager.getString("RobTicket.btnApply"));
		plApply.add(btnApply);
		btnApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!checkFormat(txtProxyIp.getText())){
					JOptionPane.showConfirmDialog(dialog, ResManager.getString("RobTicket.msg.Ipfailture"),
							ResManager.getString("RobTicket.msg.tip"),JOptionPane.OK_OPTION);
					return;
				}
				Config.setUseProxy(group.isSelected(rbEnableProxy.getModel()));
				Config.setProxyIp(txtProxyIp.getText().trim());
				Config.setProxyPort(txtProxyPort.getText().trim());
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
				v.matches("((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)")){
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
		dialog.setSize(new Dimension(300,220));
        dialog.setLocationRelativeTo(parentsFrame);
        dialog.setVisible(true);
	}

}
