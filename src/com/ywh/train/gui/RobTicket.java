package com.ywh.train.gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;

import mitm.MITMProxyServer;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;


import com.ice.jni.registry.RegDWordValue;
import com.ice.jni.registry.RegStringValue;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryKey;
import com.ice.jni.registry.RegistryValue;
import com.ywh.train.Config;
import com.ywh.train.Constants;
import com.ywh.train.ResManager;
import com.ywh.train.Util;
import com.ywh.train.bean.LoginInfo;
import com.ywh.train.bean.UserInfo;
import com.ywh.train.logic.LogicThread;
import com.ywh.train.logic.LoginThread;
import com.ywh.train.logic.TrainClient;

/**
 * 订票机器人
 * 
 * @author Tmser
 * @since 2013-01-11
 * @version 1.0
 */
public class RobTicket {
	public static final String LOGIN_BEGIN = "f1";
	public static final String LOGIN_SUCC = "f2";
	
	private JFrame frame;
	private JTextField txtUsername;//用户名
	private JTextField txtPassword;//密码
	
	/**
	 * 购票人 省份证号码
	 */
	private JTextField txtUserID; 
	
	/**
	 * 姓名
	 */
	private JTextField txtName;
	
	/**
	 * 手机号码
	 */
	private JTextField txtPhone;
	/**
	 * 日期
	 */
	private JTextField txtStartDate;
	/**
	 * 起点站
	 */
	private JTextField txtFromStation;
	/**
	 * 终点站
	 */
	private JTextField txtToStation;
	
	/**
	 * 自动识别验证码
	 */
	private JCheckBox boxkIsAuto;
	/**
	 * 乘车时间段
	 */
	private JComboBox boxoRang;
	
	/**
	 * 车票类型
	 */
	private JComboBox ticketRang;
	/**
	 * 输出信息
	 */
	private JTextArea textArea;
	
	/**
	 * 开始
	 */
	private JButton btnSORE;
	
	/**
	 * 登陆
	 */
	private JButton btnLOGIN;
	
	private JButton exitBtn;
	
	/**
	 * 打开ie
	 */
	private JButton btnOpenIE;
	
	/**
	 * 清空
	 */
	private JButton clearBtn;
	private CardLayout card;
	
	private HttpClient httpClient = null;
	
	private TrainClient client = null;//订票请求解析
	
	private LogicThread logic; //订票逻辑线程
	
	private LoginThread loginTread; //订票逻辑线程
	
	private JTextField txtTrainNo;
	private JList list;//订票人列表
	private DefaultListModel dlm;
	private JCheckBox boxkLockTrain;
	private JCheckBox boxkOneSeat;
	private JCheckBox boxkTwoSeat;
	private JCheckBox boxkHardSleeper;
	private JCheckBox boxkHardSeat;
	private JCheckBox boxkSoftSleeper;
	private JCheckBox boxkSoftSeat;
	private JCheckBox boxkBussSeat;
	private JCheckBox boxkBestSeat;
	private JCheckBox boxkNoneSeat;
	private JCheckBox boxkOther;
	private JCheckBox boxNeedRember;
	private JCheckBox boxkStrinStation; //精确匹配
	private JComboBox boxoCardType; //证件类型
	private JPanel panel;//登陆信息面板
	private JLabel labLoginInfo;
	
	private LoginInfo li;
	
	//<option value="1" selected="">成人票</option><option value="2">儿童票</option><option value="3">学生票</option><option value="4">残军票</option>
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
					RobTicket window = new RobTicket();
					window.frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public RobTicket() {
		initNetwork();
		installCert();
		initProxy();
		initialize();
		Runtime.getRuntime().addShutdownHook(new ExitThread());
	}

	
	public void clearMsg(){
		this.textArea.setText("");
	}
	/**
	 * 初始化网络模块
	 */
	private void initNetwork() {
		try {
			PoolingClientConnectionManager tcm = new PoolingClientConnectionManager();
			tcm.setMaxTotal(10);
			//**
			SSLContext ctx = SSLContext.getInstance("TLS"); //$NON-NLS-1$
			X509TrustManager tm = new X509TrustManager() {
				
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {

				}

				public void checkServerTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx,
					SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			Scheme sch = new Scheme("https", 443, ssf); //$NON-NLS-1$
			tcm.getSchemeRegistry().register(sch);
			// */
			this.httpClient = new DefaultHttpClient(tcm);
			if (Config.isUseProxy()) {
				HttpHost proxy = new HttpHost(Config.getProxyIp(),
						Config.getProxyPort(), HttpHost.DEFAULT_SCHEME_NAME);
				this.httpClient.getParams().setParameter(
						ConnRoutePNames.DEFAULT_PROXY, proxy);
			}
			//this.httpClient.getParams().setParameter(HTTP.USER_AGENT,
			//		"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; TEN)"); //$NON-NLS-1$
			this.client = new TrainClient(this.httpClient);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void installCert() {
		// Win7中安装根证书需要管理员权限
		try {
			Runtime.getRuntime().exec(
					"certutil -addstore -enterprise \"root\" \"" + new File("FakeCA.cer").getAbsolutePath() + "\"");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化代理服务器
	 */
	private void initProxy() {
		Thread proxyThread = new Thread(new Runnable() {
			public void run() {
				MITMProxyServer.main(new String[] { "-keyStore", "FakeCAStore", "-keyStorePassword", "passphrase" });
			}
		});
		proxyThread.setDaemon(true);
		proxyThread.start();
	}
	
	/**
	 * 登陆信息面板 
	 */
	private void addLoginFace(){
		card = new CardLayout();
		panel = new JPanel(card);
		panel.setBounds(10, 22, 498, 54);
		frame.getContentPane().add(panel);
		panel.setBorder(new TitledBorder(ResManager
				.getString("RobTicket.panelBorderName"))); 

		JPanel p1 = new JPanel();
		JPanel p2 = new JPanel();
		
		p1.setLayout(null);
		p2.setLayout(null);
		JLabel label = new JLabel(ResManager.getString("RobTicket.txtUsername"));
		label.setBounds(12, 4, 43, 15);
		p1.add(label);
		label.setHorizontalAlignment(SwingConstants.RIGHT);

		txtUsername = new JTextField(Config.getAccount());
		txtUsername.setToolTipText(ResManager
				.getString("RobTicket.txtUsernameTip")); //$NON-NLS-1$
		txtUsername.setBounds(59, 2, 91, 21);
		p1.add(txtUsername);
		txtUsername.setColumns(10);

		JLabel label_1 = new JLabel(
				ResManager.getString("RobTicket.txtPassword")); //$NON-NLS-1$
		label_1.setBounds(174, 4, 43, 15);
		p1.add(label_1);
		label_1.setHorizontalAlignment(SwingConstants.RIGHT);

		txtPassword = new JPasswordField(Config.getPassword());
		txtPassword.setToolTipText(ResManager
				.getString("RobTicket.txtPasswordTip")); //$NON-NLS-1$
		txtPassword.setBounds(225, 2, 84, 21);
		p1.add(txtPassword);
		txtPassword.setColumns(10);
		
		boxNeedRember = new JCheckBox(ResManager.getString("RobTicket.boxNeedRember")); //$NON-NLS-1$
		boxNeedRember.setBounds(355, 2, 69, 23);
		boxNeedRember.setToolTipText(ResManager.getString("RobTicket.boxNeedRember.Tip")); //$NON-NLS-1$
		p1.add(boxNeedRember);
		
		btnLOGIN = new JButton(ResManager.getString("RobTicket.login")); //$NON-NLS-1$
		btnLOGIN.setBounds(430, 2, 56, 23);
		p1.add(btnLOGIN);
		btnLOGIN.addActionListener(new LoginAction());
		
		panel.add(p1,LOGIN_BEGIN); //登陆界面
		
		labLoginInfo = new JLabel();
		labLoginInfo.setBounds(12, 4, 343, 15);
		p2.add(labLoginInfo);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		
		exitBtn = new JButton(ResManager.getString("RobTicket.miExit")); //$NON-NLS-1$
		exitBtn.setBounds(430, 2, 56, 23);
		p2.add(exitBtn);
		exitBtn.addActionListener(new LoginOutAction());
		
		panel.add(p2,LOGIN_SUCC); //登陆界面
		
		card.show(panel, LOGIN_BEGIN);
	}

	/**
	 * 添加乘车人信息面板
	 */
	private void addPassengersFace(){
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(10, 81, 498, 82);
		panel_1.setBorder(new TitledBorder(
				UIManager.getBorder("TitledBorder.border"), ResManager.getString("RobTicket.panel_1BorderTitle"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0))); //$NON-NLS-1$ //$NON-NLS-2$
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(null);

		JLabel label_5 = new JLabel(ResManager.getString("RobTicket.txtName")); //$NON-NLS-1$
		label_5.setBounds(20, 20, 36, 15);
		panel_1.add(label_5);
		label_5.setHorizontalAlignment(SwingConstants.RIGHT);

		txtName = new JTextField();
		txtName.setToolTipText(ResManager.getString("RobTicket.txtNameTip")); //$NON-NLS-1$
		txtName.setBounds(78, 19, 84, 21);
		panel_1.add(txtName);
		txtName.setColumns(10);
		
		JLabel label_8 = new JLabel(ResManager.getString("RobTicket.txtPhone")); //$NON-NLS-1$
		label_8.setBounds(182, 20, 61, 15);
		panel_1.add(label_8);
		label_8.setHorizontalAlignment(SwingConstants.RIGHT);
		MaskFormatter mf = null;
		try {
			mf = new MaskFormatter("###########"); //$NON-NLS-1$
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		txtPhone = new JFormattedTextField(mf);
		txtPhone.setToolTipText(ResManager.getString("RobTicket.txtPhoneTip")); //$NON-NLS-1$
		txtPhone.setBounds(244, 20, 92, 21);
		panel_1.add(txtPhone);
		txtPhone.setColumns(10);
		
		JLabel label_12 = new JLabel(		//乘车人行
				ResManager.getString("RobTicket.ticketType")); //$NON-NLS-1$
		label_12.setBounds(345, 20, 54, 15);
		label_12.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_1.add(label_12);

		ticketRang = new JComboBox(Constants.getTicketType());
		ticketRang.setBounds(395, 19, 92, 21);
		ticketRang.setSelectedItem(Config.getTicketType());
		panel_1.add(ticketRang);
		
		
		JLabel label_2 = new JLabel(ResManager.getString("RobTicket.label_2")); //$NON-NLS-1$
		label_2.setHorizontalAlignment(SwingConstants.RIGHT);
		label_2.setBounds(0, 49, 73, 15);
		panel_1.add(label_2);
		
		boxoCardType = new JComboBox(Constants.getCardType());
		boxoCardType.setToolTipText(ResManager.getString("RobTicket.comboBoxTip")); //$NON-NLS-1$
		boxoCardType.setBounds(79, 49, 90, 21);
		panel_1.add(boxoCardType);
		
		JLabel label_3 = new JLabel(ResManager.getString("RobTicket.txtUserID")); //$NON-NLS-1$
		label_3.setBounds(170, 49, 73, 15);
		panel_1.add(label_3);
		label_3.setHorizontalAlignment(SwingConstants.RIGHT);

		txtUserID = new JTextField();
		txtUserID
				.setToolTipText(ResManager.getString("RobTicket.txtUserIDTip")); //$NON-NLS-1$
		txtUserID.setBounds(245, 49, 127, 21);
		panel_1.add(txtUserID);
		txtUserID.setColumns(10);
		
		JButton button = new JButton(ResManager.getString("RobTicket.addBtn")); //$NON-NLS-1$
		button.addActionListener(new AddAction());
		button.setToolTipText(ResManager.getString("RobTicket.addBtnTip")); //$NON-NLS-1$
		button.setBounds(415, 49, 73, 23);
		panel_1.add(button);
	}
	
	/**
	 * 车票及车次信息
	 */
	private void addTrainAndTicketInfo(){
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(10, 163, 498, 180);
		panel_1.setBorder(new TitledBorder(
				UIManager.getBorder("TitledBorder.border"), ResManager.getString("RobTicket.panelTrainAndTicket"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0))); //$NON-NLS-1$ //$NON-NLS-2$
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JLabel label_9 = new JLabel(ResManager.getString("RobTicket.boxoRang")); //$NON-NLS-1$
		label_9.setBounds(0, 24, 73, 15);
		panel_1.add(label_9);
		label_9.setHorizontalAlignment(SwingConstants.RIGHT);
		boxoRang = new JComboBox(Constants.getRangTime());
		boxoRang.setToolTipText(ResManager.getString("RobTicket.boxoRangTip")); //$NON-NLS-1$
		boxoRang.setBounds(79, 21, 92, 21);
		boxoRang.setSelectedItem(Config.getRangTime());
		panel_1.add(boxoRang);
		
		JLabel label_6 = new JLabel(
				ResManager.getString("RobTicket.txtStartDate")); //$NON-NLS-1$
		label_6.setBounds(273, 24, 61, 15);
		panel_1.add(label_6);
		label_6.setHorizontalAlignment(SwingConstants.RIGHT);

		MaskFormatter mf = null;
		try {
			mf = new MaskFormatter("####-##-##"); //$NON-NLS-1$
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		txtStartDate = new JFormattedTextField(mf);
		txtStartDate.setToolTipText(ResManager
				.getString("RobTicket.txtStartDateTip")); //$NON-NLS-1$
		txtStartDate.setBounds(342, 21, 84, 21);
		panel_1.add(txtStartDate);
		txtStartDate.setColumns(10);
		txtStartDate.setText(Config.getStartDate());

		JLabel label_4 = new JLabel(
				ResManager.getString("RobTicket.txtFromStation")); //$NON-NLS-1$
		label_4.setBounds(32, 49, 43, 15);
		panel_1.add(label_4);
		label_4.setHorizontalAlignment(SwingConstants.RIGHT);

		txtFromStation = new JTextField();
		txtFromStation.setToolTipText(ResManager
				.getString("RobTicket.txtFromStationTip")); //$NON-NLS-1$
		txtFromStation.setBounds(79, 46, 92, 21);
		panel_1.add(txtFromStation);
		txtFromStation.setColumns(10);
		txtFromStation.setText(Config.getFromStation());

		JLabel label_7 = new JLabel(
				ResManager.getString("RobTicket.txtToStation")); //$NON-NLS-1$
		label_7.setBounds(298, 49, 36, 15);
		panel_1.add(label_7);
		label_7.setHorizontalAlignment(SwingConstants.RIGHT);

		txtToStation = new JTextField();
		txtToStation.setToolTipText(ResManager
				.getString("RobTicket.txtToStationTip")); //$NON-NLS-1$
		txtToStation.setBounds(342, 46, 84, 21);
		panel_1.add(txtToStation);
		txtToStation.setColumns(10);
		txtToStation.setText(Config.getToStation());
		
		JLabel label_10 = new JLabel(ResManager.getString("RobTicket.label_10")); //$NON-NLS-1$
		label_10.setBounds(10, 73, 63, 15);
		label_10.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_1.add(label_10);
		
		txtTrainNo = new JTextField();
		txtTrainNo.setBounds(79, 73, 84, 21);
		txtTrainNo.setToolTipText(ResManager.getString("RobTicket.txtTrainNoTip")); //$NON-NLS-1$
		panel_1.add(txtTrainNo);
		
		boxkLockTrain = new JCheckBox(ResManager.getString("RobTicket.boxkLockTrain")); //$NON-NLS-1$
		boxkLockTrain.setBounds(342, 73, 73, 23);
		boxkLockTrain.setToolTipText(ResManager.getString("RobTicket.boxkLockTrainTip")); //$NON-NLS-1$
		panel_1.add(boxkLockTrain);
		
		boxkStrinStation = new JCheckBox(ResManager.getString("RobTicket.boxkStrinStation")); //$NON-NLS-1$
		boxkStrinStation.setToolTipText(ResManager.getString("RobTicket.boxkStrinStationTip")); //$NON-NLS-1$
		boxkStrinStation.setBounds(50, 100, 73, 23);
		panel_1.add(boxkStrinStation);

		boxkIsAuto = new JCheckBox(ResManager.getString("RobTicket.boxkIsAuto")); //$NON-NLS-1$
		boxkIsAuto.setBounds(342, 100, 109, 23);
		boxkIsAuto.setToolTipText(ResManager
				.getString("RobTicket.boxkIsAutoTip")); //$NON-NLS-1$
		panel_1.add(boxkIsAuto);
		
		boxkOneSeat = new JCheckBox(ResManager.getString("RobTicket.boxkOneSeat")); //$NON-NLS-1$
		boxkOneSeat.setBounds(30, 127, 63, 23);
		boxkOneSeat.setToolTipText(ResManager.getString("RobTicket.boxkOneSeatTip")); //$NON-NLS-1$
		panel_1.add(boxkOneSeat);
		
		boxkTwoSeat = new JCheckBox(ResManager.getString("RobTicket.boxkTwoSeat")); //$NON-NLS-1$
		boxkTwoSeat.setSelected(true);
		boxkTwoSeat.setBounds(120, 127, 63, 23);
		boxkTwoSeat.setToolTipText(ResManager.getString("RobTicket.boxkTwoSeatTip")); //$NON-NLS-1$
		panel_1.add(boxkTwoSeat);
		
		boxkHardSleeper = new JCheckBox(ResManager.getString("RobTicket.boxkHardSleeper")); //$NON-NLS-1$
		boxkHardSleeper.setSelected(true);
		boxkHardSleeper.setBounds(210, 127, 49, 23);
		boxkHardSleeper.setToolTipText(ResManager.getString("RobTicket.boxkHardSleeperTip")); //$NON-NLS-1$
		panel_1.add(boxkHardSleeper);
		
		boxkHardSeat = new JCheckBox(ResManager.getString("RobTicket.boxkHardSeat")); //$NON-NLS-1$
		boxkHardSeat.setSelected(true);
		boxkHardSeat.setBounds(300, 127, 49, 23);
		boxkHardSeat.setToolTipText(ResManager.getString("RobTicket.boxkHardSeatTip")); //$NON-NLS-1$
		panel_1.add(boxkHardSeat);
		
		boxkSoftSleeper = new JCheckBox(ResManager.getString("RobTicket.boxkSoftSleeper")); //$NON-NLS-1$
		boxkSoftSleeper.setBounds(390, 127, 49, 23);
		boxkSoftSleeper.setToolTipText(ResManager.getString("RobTicket.boxkSoftSleeperTip")); //$NON-NLS-1$
		panel_1.add(boxkSoftSleeper);
		
		boxkSoftSeat = new JCheckBox(ResManager.getString("RobTicket.boxkSoftSeat")); //$NON-NLS-1$
		boxkSoftSeat.setBounds(30, 154, 49, 23);
		boxkSoftSeat.setToolTipText(ResManager.getString("RobTicket.boxkSoftSeatTip")); //$NON-NLS-1$
		panel_1.add(boxkSoftSeat);
		
		boxkBussSeat = new JCheckBox(ResManager.getString("RobTicket.boxkBussSeat")); //$NON-NLS-1$
		boxkBussSeat.setBounds(120, 154, 63, 23);
		boxkBussSeat.setToolTipText(ResManager.getString("RobTicket.boxkBussSeatTip")); //$NON-NLS-1$
		panel_1.add(boxkBussSeat);
		
		boxkBestSeat = new JCheckBox(ResManager.getString("RobTicket.boxkBestSeat")); //$NON-NLS-1$
		boxkBestSeat.setBounds(210, 154, 63, 23);
		boxkBestSeat.setToolTipText(ResManager.getString("RobTicket.boxkBestSeatTip")); //$NON-NLS-1$
		panel_1.add(boxkBestSeat);
		
		boxkNoneSeat = new JCheckBox(ResManager.getString("RobTicket.boxkNoneSeat")); //$NON-NLS-1$
		boxkNoneSeat.setBounds(300, 154, 49, 23);
		boxkNoneSeat.setToolTipText(ResManager.getString("RobTicket.boxkNoneSeatTip")); //$NON-NLS-1$
		panel_1.add(boxkNoneSeat);
		
		boxkOther = new JCheckBox(ResManager.getString("RobTicket.boxkOther")); //$NON-NLS-1$
		boxkOther.setBounds(390, 154, 49, 23);
		boxkOther.setToolTipText(ResManager.getString("RobTicket.boxkOtherTip")); //$NON-NLS-1$
		panel_1.add(boxkOther);
	}
	
	private void addConsoleFace(){
		JLabel label_11 = new JLabel(ResManager.getString("RobTicket.label_11.text")); //$NON-NLS-1$
		label_11.setBounds(20, 383, 73, 15);
		frame.getContentPane().add(label_11);
		
		dlm = new DefaultListModel();
		list = new JList(dlm);
		list.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		list.setCellRenderer(new RobTicket.UserInfoCellRenderer());
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 1){
					selectUser(list);
				}else if (e.getClickCount() == 2) {
					int index = list.getSelectedIndex();
					if (index >= 0) {
						dlm.remove(index);
					}
				}
			}

		});
	//	list.setToolTipText(ResManager.getString("RobTicket.userListTip")); //$NON-NLS-1$
		list.setBounds(10, 406, 130, 136);
		frame.getContentPane().add(list);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(146, 383, 362, 162);
		scrollPane.setViewportBorder(new BevelBorder(BevelBorder.LOWERED, null,
				null, null, null));
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(scrollPane);

		textArea = new JTextArea();
		textArea.setText(ResManager.getString("RobTicket.textAreaContent")); //$NON-NLS-1$
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		scrollPane.setViewportView(textArea);
	}
	
	private void addMenu(){
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 518, 21);
		frame.getContentPane().add(menuBar);

		JMenu mnOpt = new JMenu(ResManager.getString("RobTicket.mnOpt")); //$NON-NLS-1$
		menuBar.add(mnOpt);

		JMenuItem miOpt = new JMenuItem(ResManager.getString("RobTicket.miOpt")); //$NON-NLS-1$
		mnOpt.add(miOpt);
		miOpt.addActionListener(new UseSkillAction(frame));

		JMenuItem miExit = new JMenuItem(
				ResManager.getString("RobTicket.miExit")); //$NON-NLS-1$
		mnOpt.add(miExit);
		miExit.addActionListener(new ExitAction());

		JMenu mnHelp = new JMenu(ResManager.getString("RobTicket.mnHelp")); //$NON-NLS-1$
		menuBar.add(mnHelp);

		JMenuItem miAbout = new JMenuItem(
				ResManager.getString("RobTicket.miAbout")); //$NON-NLS-1$
		miAbout.addActionListener(new AboutAction(frame));
		mnHelp.add(miAbout);
	}
	
	private void addButtons(){
		btnSORE = new JButton(ResManager.getString("RobTicket.btnSORE")); //$NON-NLS-1$
		btnSORE.setBounds(100, 350, 60, 23);
		btnSORE.setToolTipText(ResManager.getString("RobTicket.btnSORETip")); //$NON-NLS-1$
		frame.getContentPane().add(btnSORE);
		btnSORE.setEnabled(false);
		
		clearBtn = new JButton(ResManager.getString("RobTicket.btnClear"));
		clearBtn.setBounds(200, 350, 60, 23);
		clearBtn.setToolTipText(ResManager.getString("RobTicket.btnClear.tip"));
		frame.getContentPane().add(clearBtn);

		btnOpenIE = new JButton(ResManager.getString("RobTicket.btnOpenIE"));
		btnOpenIE.setBounds(300, 350, 110, 23);
		btnOpenIE.setToolTipText(ResManager.getString("RobTicket.btnOpenIE.tip"));
		btnOpenIE.setEnabled(false);
		frame.getContentPane().add(btnOpenIE);
		
		btnSORE.addActionListener(new StartAction());
		btnOpenIE.addActionListener(new OpenIeAction());
		clearBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearMsg();
			}
		});
	}
	
	/**
	 * 创建主面板
	 */
	private void createMainFrame(){
		frame = new JFrame(ResManager.getString("RobTicket.frameName")); //$NON-NLS-1$
     	ImageIcon ico = ResManager.createImageIcon("logo.jpg"); //$NON-NLS-1$
		frame.setIconImage(ico.getImage());
		frame.setBounds(100, 100, 524, 583);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null); 
		ToolTipManager.sharedInstance().setInitialDelay(0);
		frame.getContentPane().setLayout(null);
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {  
		
		/**
		 * 创建主面板
		 */
		createMainFrame();

	   // 添加登陆信息面板
		addLoginFace();
		
		//乘车人信息
		addPassengersFace();
		
		//车次及车票信息
		addTrainAndTicketInfo();
		
		//创建按钮
		addButtons();
		
		/**
		 *创建控制台
		 */
		addConsoleFace();
		
		readUserInfo();//读取上次登录时记住的用户和密码
		
		/**
		 * 创建菜单
		 */
		addMenu();
	}

	private void setBtnEnable(boolean canUse){
		btnLOGIN.setEnabled(!canUse);
		btnOpenIE.setEnabled(canUse);
		btnSORE.setEnabled(canUse);
	}
	/**
	 * 复位操作
	 * @param isEnd 是否是结束复位,false为普通复位
	 */
	public void reset(boolean isEnd) {
		if (isEnd) {
			btnSORE.setText(ResManager.getString("RobTicket.btnSORE")); //$NON-NLS-1$
			if (logic != null) {
				logic.setEnd(true);
			}
			logic = null;
		} else {
			try {
				Thread.sleep(Config.getSleepTime());
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * 启动方法
	 */	
	protected void action() {		
		//this.client.getStr(Constants.RANDSTR_URL);
		if (getFromCity().isEmpty()
				|| getFromCity().isEmpty() || getToCity().isEmpty()
				|| getStartDate().isEmpty()) {
			JOptionPane.showMessageDialog(frame,
					ResManager.getString("RobTicket.JOptionPane")); //$NON-NLS-1$
			reset(true);
			return;
		}
		if (dlm.getSize() == 0 || getSelectUsers().size() == 0) {
			JOptionPane.showMessageDialog(frame,ResManager.getString("RobTicket.showMessageDialog")); //$NON-NLS-1$
			reset(true);
			return;
		}
		btnSORE.setText(ResManager.getString("RobTicket.btnSORE.end")); //$NON-NLS-1$
		textArea.setText(""); //$NON-NLS-1$
		logic = new LogicThread(client, this);
		logic.start();
	}
	
	/**
	 * 登陆方法
	 */	
	protected void login() {
		this.btnLOGIN.setEnabled(false);
		textArea.setText(""); //$NON-NLS-1$
		if (getUsername().isEmpty() || getPassword().isEmpty()) {
			JOptionPane.showMessageDialog(frame,
					ResManager.getString("RobTicket.JOptionPane")); //$NON-NLS-1$
			return;
		}
				
		loginTread = new LoginThread(client, this);
		loginTread.start();
	}
	
	/**
	 * 退出登陆方法
	 */	
	protected void loginOut() {
		this.client.loginOut();
		Constants.isLoginSuc = false;
		reset(true);
		card.show(panel, LOGIN_BEGIN);
		setBtnEnable(false);
		this.console(ResManager.getString("RobTicket.txtLoginout"));
	}
	
	/**
	 * 获取乘车人姓名
	 * @return 
	 */
	private String getName() {
		return txtName.getText().trim();
	}

	/**
	 * 获取乘车人ID
	 * @return 
	 */
	private String getUserID() {
		return txtUserID.getText().trim();
	}
	
	/**
	 * 获取乘车人电话
	 * @return
	 */
	private String getPhone() {
		return txtPhone.getText().trim();
	}
	
	/**
	 * 获取乘车时间段
	 * @return 
	 */
	public String getRangDate() {
		String key = (String) boxoRang.getSelectedItem();
		return Constants.trainRang.get(key);
	}
	
	/**
	 * 获取乘车时间段
	 * @return 
	 */
	public String getTicketType() {
		String key = (String) ticketRang.getSelectedItem();
		if(key==null || "".equals(key.trim())){
			key = "成人票";
		}
		return Constants.ticketType.get(key);
	}
	
	/**
	 * 获取证件类型
	 * @return 
	 */
	public String getCardType() {
		String key = (String) boxoCardType.getSelectedItem();
		return Constants.cardType.get(key);
	}

	/**
	 * 获取乘车日期
	 * @return 
	 */
	public String getStartDate() {
		return txtStartDate.getText().trim();
	}

	/**
	 * 获取出发站
	 * @return 
	 */
	public String getToCity() {
		return txtToStation.getText().trim();
	}

	/**
	 * 获取目的站
	 * @return 
	 */
	public String getFromCity() {
		return txtFromStation.getText().trim();
	}
	
	/**
	 * 获取登录密码
	 * 
	 * @return
	 */
	public String getPassword() {
		return txtPassword.getText().trim();
	}

	/**
	 * 登陆成功，切换面板
	 * 
	 * @return
	 */
	public void changePanel(String state) {
		if(state.equals(LOGIN_SUCC)){
			card.show(panel,state);
			labLoginInfo.setText(
					ResManager.getString("RobTicket.txtWelcome")+txtUsername.getText());
			card.show(panel,state);
			setBtnEnable(true);
		}else{
			card.show(panel,LOGIN_BEGIN);
		}
	}
	/**
	 * 获取登录用户名
	 * 
	 * @return
	 */
	public String getUsername() {
		return txtUsername.getText().trim();
	}

	/**
	 * 是否自动识别验证码 
	 * @return
	 */
	public boolean isAutocode() {
		return boxkIsAuto.isSelected();
	}
	
	/**
	 * 是否记住用户名和密码
	 * @return
	 */
	public boolean needRemberMe() {
		return boxNeedRember.isSelected();
	}
	
	/**
	 * 获取乘车人信息
	 */
	public List<UserInfo> getSelectUsers() {
		 List<UserInfo> users = new ArrayList<UserInfo>();
		for (Object o:list.getSelectedValues()) {
			users.add((UserInfo)o);
		}
		return users;
	}
	
	/**
	 * 读取列车设置信息 
	 * @return
	 */
	public boolean[] getTrainSet() {
		boolean trainSet[] = new boolean[] { false, false, false, false, false,
				false, false, false, false, false, false, false };
		trainSet[Constants.isLockTrain] = boxkLockTrain.isSelected();
		trainSet[Constants.isStrinStation] = boxkStrinStation.isSelected();
		trainSet[Constants.isNeed_BEST_SEAT] = boxkBestSeat.isSelected();
		trainSet[Constants.isNeed_BUSS_SEAT] = boxkBussSeat.isSelected();
		trainSet[Constants.isNeed_HARD_SEAT] = boxkHardSeat.isSelected();
		trainSet[Constants.isNeed_HARD_SLEEPER] = boxkHardSleeper.isSelected();
		trainSet[Constants.isNeed_NONE_SEAT] = boxkNoneSeat.isSelected();
		trainSet[Constants.isNeed_ONE_SEAT] = boxkOneSeat.isSelected();
		trainSet[Constants.isNeed_VAG_SLEEPER] = boxkOther.isSelected();
		trainSet[Constants.isNeed_SOFT_SEAT] = boxkSoftSeat.isSelected();
		trainSet[Constants.isNeed_SOFT_SLEEPER] = boxkSoftSleeper.isSelected();
		trainSet[Constants.isNeed_TWO_SEAT] = boxkTwoSeat.isSelected();
		return trainSet;
	}
	

	/**
	 * 获取指定列车信息
	 * @return 
	 */
	public Set<String> getTrainNo() {
		Set<String> ans = new HashSet<String>();
		String trainStr  = txtTrainNo.getText().trim();
		String[] trainNo = trainStr.split("\\|"); //$NON-NLS-1$
		for (String str : trainNo) {
			if (!str.trim().isEmpty()) {
				ans.add(str.trim().toUpperCase());
			}
		}
		return ans;
	}
	
	/**
	 * 保存用户信息
	 * 
	 * @param ui
	 */
	public void writeUserInfo() {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(new File("li"))); //$NON-NLS-1$
			oos.writeObject(li);
			oos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (oos != null)
					oos.close();
			} catch (Exception e) {
				//cann't do anything
			}
		}
	}
	
	/**
	 * 在下拉框选择用户
	 */
private void selectUser(JList list){
	list.getSelectedIndex();
	Object o = list.getSelectedValue();
	//System.out.println(o.toString());
	if(o!=null){
		UserInfo user = (UserInfo)o;
		txtUserID.setText(user.getID());
		txtName.setText(user.getName());
		txtPhone.setText(user.getPhone());
		boxoCardType.setSelectedItem(Constants.typeCard.get(user.getCardType()));
		ticketRang.setSelectedItem(Constants.typeTicket.get(user.getTickType()));
	}
}
	
	
	/**
	 * 读取用户信息
	 */
	private void readUserInfo() {
		ObjectInputStream ois = null;
		try {
			File loginFile =  new File("li");
			if(loginFile.exists()){
				ois = new ObjectInputStream(new FileInputStream(loginFile)); //$NON-NLS-1$
				LoginInfo loginInfo = (LoginInfo) ois.readObject();
				if (loginInfo != null) {
					txtUsername.setText(loginInfo.getLoginName());
					txtPassword.setText(loginInfo.getPassword());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ois != null)
					ois.close();
			} catch (IOException e) {
					//cann't do anything;
			}
		}
	}

	public void console(String content) {
		textArea.append(Util.formatInfo(content));
		textArea.setCaretPosition(textArea.getText().length());
	}

	public JFrame getFrame() {
		return frame;
	}

	/**
	 * 功能描述
	 * @author cafebabe
	 * @since 2012-1-11 
	 * @version 1.0
	 */
	private final class AddAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (getUserID().isEmpty() || getName().isEmpty()) {
				JOptionPane.showMessageDialog(frame,ResManager.getString("RobTicket.addNone")); //$NON-NLS-1$
			} else if (dlm.getSize() >= 5) {
				JOptionPane.showMessageDialog(frame,ResManager.getString("RobTicket.addFive")); //$NON-NLS-1$
			} else {
				UserInfo user = new UserInfo();
				user.setID(getUserID());
				user.setName(getName());
				user.setPhone(getPhone());
				user.setCardType(getCardType());
				user.setTickType(getTicketType());
				dlm.addElement(user);
				txtName.setText(""); //$NON-NLS-1$
				txtUserID.setText(""); //$NON-NLS-1$
//				System.out.println(user);
			}
		}
	}

	/**
	 * 功能描述
	 * 
	 * @author YAOWENHAO
	 * @since 2011-12-21
	 * @version 1.0
	 */
	static class ExitAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}


	/**
	 * 开始按钮的监听
	 * 
	 * @author YAOWENHAO
	 * @since 2011-12-21
	 * @version 1.0
	 */
	class StartAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			JButton btn = (JButton) e.getSource();
			if (ResManager.getString("RobTicket.btnSORE").equals(btn.getText())) {
				if(li == null){
					li = new LoginInfo();
					li.setLoginName(getUsername());
					li.setPassword(getPassword());
				}
				action();
			} else {
				reset(true);
			}
		}
	}
	
	/**
	 * 开始按钮的监听
	 * 
	 * @author YAOWENHAO
	 * @since 2011-12-21
	 * @version 1.0
	 */
	class LoginAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton) e.getSource();
			if (ResManager.getString("RobTicket.login").equals(btn.getText())) {
				login();
			} else {
				loginTread.setEnd(true);
			}
		}
	}
	
	/**
	 * 开始按钮的监听
	 * 
	 * @author YAOWENHAO
	 * @since 2011-12-21
	 * @version 1.0
	 */
	class LoginOutAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			loginOut();
		}
	}
	
	/**
	 * 打开浏览器
	 * 
	 * @author belerweb
	 */
	class OpenIeAction implements ActionListener {
		Process ieProcess = null;
		public void actionPerformed(ActionEvent event) {
			if (TrainClient.JSESSIONID ==null || TrainClient.BIGipServerotsweb==null) {
				JOptionPane.showMessageDialog(frame, ResManager.getString("RobTicket.btnOpenIE.wait"));
				return;
			}

			JButton btn = (JButton) event.getSource();
			if (ResManager.getString("RobTicket.btnOpenIE").equals(btn.getText())) {
				btn.setText(ResManager.getString("RobTicket.btnOpenIE.close"));
				try {
					RegistryKey ieSettings = Registry.HKEY_CURRENT_USER.openSubKey(
							"Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", RegistryKey.ACCESS_ALL);
					RegistryValue proxyEnable = new RegDWordValue(ieSettings, "ProxyEnable", RegistryValue.REG_DWORD, 1);
					RegistryValue proxyServer = new RegStringValue(ieSettings, "ProxyServer", "https=127.0.0.1:9999");
					ieSettings.setValue(proxyEnable);
					ieSettings.setValue(proxyServer);
					ieSettings.flushKey();
					ieProcess = Runtime.getRuntime().exec("explorer \"" + Constants.QUERY_ORDER_URL + "\"");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				if (ieProcess!=null) {
					ieProcess.destroy();
					ieProcess = null;
				}
				try {
					RegistryKey ieSettings = Registry.HKEY_CURRENT_USER.openSubKey(
							"Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", RegistryKey.ACCESS_ALL);
					RegistryValue proxyEnable = new RegDWordValue(ieSettings, "ProxyEnable", RegistryValue.REG_DWORD, 0);
					ieSettings.setValue(proxyEnable);
					ieSettings.flushKey();
				} catch (Exception e) {
					e.printStackTrace();
				}
				btn.setText(ResManager.getString("RobTicket.btnOpenIE"));
			}
		}
	}

	/**
	 * 退出线程
	 * 
	 * @author YAOWENHAO
	 * @since 2011-12-21
	 * @version 1.0
	 */
	protected class ExitThread extends Thread {
		public void run() {
			httpClient.getConnectionManager().shutdown();

			// 清除代理设置
			try {
				RegistryKey ieSettings = Registry.HKEY_CURRENT_USER.openSubKey(
						"Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", RegistryKey.ACCESS_ALL);
				RegistryValue proxyEnable = new RegDWordValue(ieSettings, "ProxyEnable", RegistryValue.REG_DWORD, 0);
				ieSettings.setValue(proxyEnable);
				ieSettings.flushKey();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * 用户信息渲染类
	 * 
	 * @author jhezjkp
	 * @since 2012-1-12
	 * @version 1.0
	 */
	static class UserInfoCellRenderer extends JLabel implements
			ListCellRenderer {

		/**字段注释*/
		private static final long serialVersionUID = -5357035693572356253L;

		public UserInfoCellRenderer() {
			setOpaque(true);
			setIconTextGap(12);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			UserInfo userInfo = (UserInfo) value;
			String msg = userInfo.getName()+" "+userInfo.getID();
			setText(msg); //$NON-NLS-1$
			if (isSelected) {
				setBackground(Color.BLUE);
				setForeground(Color.WHITE);
				list.setToolTipText(ResManager.getString("RobTicket.userListTip"));
			} else {
				setBackground(Color.WHITE);
				setForeground(Color.BLACK);
				list.setToolTipText(msg);
			}
			return this;
		}

	}

	/**
	 * 菜单关于窗口
	 * 
	 * @author YAOWENHAO
	 * @since 2011-12-21
	 * @version 1.0
	 */
	static class AboutAction extends AbstractAction {

		/** 字段注释 */
		private static final long serialVersionUID = -1097396738396411124L;

		JFrame parentsFrame;
		URL img = ResManager.getFileURL("logo.jpg"); //$NON-NLS-1$
		String imagesrc = "<img src=" + img + " width=\"50\" height=\"50\">"; //$NON-NLS-1$ //$NON-NLS-2$
		String message = Constants.ABOUNT_CONTENT;

		protected AboutAction(JFrame frame) {
			this.parentsFrame = frame;
		}

		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(
				parentsFrame,
				"<html><center>" + imagesrc + "</center><br><center>" + message + "</center><br></html>", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				ResManager.getString("RobTicket.AboutAction.title"), //$NON-NLS-1$
				JOptionPane.DEFAULT_OPTION);
		}
	}

	/**
	 * 使用技巧
	 * 
	 * @author YAOWENHAO
	 * @since 2011-12-21
	 * @version 1.0
	 */
	static class UseSkillAction extends AbstractAction {

		/** 字段注释 */
		private static final long serialVersionUID = -7920608352042404436L;

		JFrame parentsFrame;
		//URL img = ResManager.getFileURL("logo.jpg"); //$NON-NLS-1$
	//	String imagesrc = "<img src=\"" + img + "\" width=\"50\" height=\"50\">"; //$NON-NLS-1$ //$NON-NLS-2$
		String message = ResManager
				.getString("RobTicket.UseSkillAction.message"); //$NON-NLS-1$

		protected UseSkillAction(JFrame frame) {
			this.parentsFrame = frame;
		}

		public void actionPerformed(ActionEvent e) {
			StringBuilder msg = new StringBuilder();
			msg.append("<html><body><strong>")
				.append(ResManager.getString("RobTicket.UseSkillAction.title"))
				.append("</strong><br/>")
				.append(ResManager.getString("RobTicket.UseSkillAction.message"))
				.append("</body></html>");
				
			JOptionPane.showMessageDialog(
				parentsFrame,
				msg.toString(),ResManager.getString("RobTicket.UseSkillAction.title"),
				JOptionPane.DEFAULT_OPTION);
		}
	}
}
