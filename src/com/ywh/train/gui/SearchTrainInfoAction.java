package com.ywh.train.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import com.ywh.train.Constants;
import com.ywh.train.NetConnectException;
import com.ywh.train.ResManager;
import com.ywh.train.bean.TrainQueryInfo;

/**
 * 查询车次信息
 * @author tmser
 *
 */
 class SearchTrainInfoAction extends AbstractAction{
	private static final long serialVersionUID = -798538352042404436L;
	private RobTicket rob;
	JRadioButton rbEnableProxy,rbDisableProxy;
	JButton btnApply;
	JDialog dialog;
	JTable table;
	JLabel imgLabel;
	DefaultTableModel model;

	protected SearchTrainInfoAction(RobTicket robTicket) {
		this.rob = robTicket;
	}

	public void actionPerformed(ActionEvent e) {
		if ("".equals(rob.getFromCity()) || 
				"".equals(rob.getToCity())){
			JOptionPane.showMessageDialog(rob.getFrame(),
					ResManager.getString("RobTicket.SOptionPane")); 
			return;
		}
		
		createDiolag();
		
		TrainQueryInfo trainInfo = new TrainQueryInfo();
		trainInfo.setFromStation(rob.getFromCity());
		trainInfo.setToStation(rob.getToCity());
		trainInfo.setStartTime(rob.getStartDate());
		trainInfo.setRangeDate(rob.getRangDate());
/*		trainInfo.setTrainNo("T145");
		trainInfo.setStartTime("11:00");
		List<TrainQueryInfo> lst = new ArrayList<TrainQueryInfo>();
		lst.add(trainInfo);
		addTrainInfoColumn(lst);*/
		queryTrainInfo(trainInfo);
		dialog.setVisible(true);
	}
	
	void addTrainInfoColumn(List<TrainQueryInfo> lst ){
		model.setRowCount(0);
		
		for(TrainQueryInfo train : lst){
			 model.addRow(new Object[]{train.getTrainNo(),"<html>"+train.getFromStation()+"<br/>"+train.getStartTime()+"</html>",
					 "<html>"+train.getToStation()+"<br/>"+ train.getEndTime()+"</html>",train.getTakeTime(),Boolean.FALSE});
		}
		
	}
	
	void createDiolag(){
        dialog = new JDialog(rob.getFrame(),ResManager.getString("RobTicket.btnSearch"),true);
        Object[][] p={};
        Object[] n={ResManager.getString("RobTicket.label_10"),ResManager.getString("RobTicket.txtFromStation"),
        		ResManager.getString("RobTicket.txtToStation"),ResManager.getString("RobTicket.txtTakeTime"),ResManager.getString("RobTicket.tbTitle.opt")};
        model = new DefaultTableModel(p,n){
			private static final long serialVersionUID = -6680116928362316922L;

			@Override
        	 public boolean isCellEditable(int row, int col){
				if(col < 4){  
		            return false;  
		        }else {  
		            return true;  
		        }
        	 }
			
			@Override
			public Class<?> getColumnClass(int c) {
				return getValueAt(0, c) != null ?getValueAt(0, c).getClass() : Object.class;
			}
        };
        
        table = new JTable(model);
        table.setPreferredScrollableViewportSize(new Dimension(380,160));
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setRowHeight(36);

        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
        tcr.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        DefaultTableCellRenderer hr = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
        hr.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        table.setDefaultRenderer(Object.class, tcr);
       	
        model.addTableModelListener(new TableModelListener(){
        	public void tableChanged(TableModelEvent e) {  
                TableModel model = (TableModel)e.getSource();  
                StringBuilder output = new StringBuilder();
                for (int c =0; c < model.getRowCount(); c++) {
                	Boolean data = (Boolean) model.getValueAt(c, 4);
                	if(data){
                		output.append(model.getValueAt(c, 0));
                		output.append("|");
                	}
                }
	            if(output.length() > 0){
	            	rob.setTrainNo(output.substring(0,output.length() - 1));
	            }else{
	            	rob.setTrainNo("");
	            }
            
        }
    });
        


        //RobTicket.labSearchInfo
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel panel = new JPanel();
        
        panel.add(scrollPane);
        
/*		imgLabel = new JLabel();
		imgLabel.setHorizontalAlignment(JLabel.CENTER);
		imgLabel.setIcon(ResManager.createImageIcon("loading.gif"));
		imgLabel.setVisible(false);
		
        panel.add(imgLabel);*/
        scrollPane.setBackground(Color.WHITE);
        panel.setBackground(Color.WHITE);
		dialog.setContentPane(panel);
		dialog.setResizable(false);
		dialog.setSize(new Dimension(400,250));
        dialog.setLocationRelativeTo(rob.getFrame());
       
	}
	
	void queryTrainInfo(TrainQueryInfo trainInfo){
		SwingUtilities.invokeLater(new SearchThread(trainInfo));
		
	}
	
	class SearchThread extends Thread {
		private final Logger log = Logger.getLogger(SearchThread.class);
		private TrainQueryInfo trainInfo;
		
		SearchThread(TrainQueryInfo trainInfo){
			this.trainInfo = trainInfo;
		}
		/**
		 * override 方法<p>
		 * 登陆线程，登陆成功后才进行购票。
		 */
		@Override
		public void run() {
			try {
				if(Constants.isLoginSuc){
				//	imgLabel.setVisible(true);
					List<TrainQueryInfo> allTrain = rob.getClient().queryTrain(
							trainInfo.getFromStation(), trainInfo.getToStation(), trainInfo.getStartTime(),
							trainInfo.getRangeDate());
							
					addTrainInfoColumn(allTrain);
				//	imgLabel.setVisible(false);
				}else{
					JOptionPane.showMessageDialog(dialog,
							ResManager.getString("RobTicket.msg.notLogin")); 
					dialog.setVisible(false);
					return;
				}

				}catch(NetConnectException e) {
					//rob.console(ResManager.getString("RobTicket.err.net"));
					JOptionPane.showMessageDialog(dialog,
							ResManager.getString("RobTicket.err.net")); 
				} catch(Exception e){
					log.error(e);
					e.printStackTrace();
					JOptionPane.showMessageDialog(dialog,
							ResManager.getString("RobTicket.err.unkwnow")); 
				}
			
			}
		
	}
}
