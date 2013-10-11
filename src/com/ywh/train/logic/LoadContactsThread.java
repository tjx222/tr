package com.ywh.train.logic;

import java.util.ArrayList;
import java.util.List;

import com.ywh.train.Config;
import com.ywh.train.Constants;
import com.ywh.train.bean.Page;
import com.ywh.train.bean.UserInfo;
import com.ywh.train.gui.RobTicket;

/**
 * 登陆线程
 * 
 * @author tmser
 * @since 2013-10-07
 * @version 1.0
 */
public class LoadContactsThread extends BaseThread {

	private volatile Thread blinker = this;
	/**
	 * 构造函数
	 * 
	 * @param robTicket
	 */
	public LoadContactsThread(TrainClient client, RobTicket rob) {
		super(client,rob);
	}

	/**
	 * override 方法<p>
	 * 登陆线程，登陆成功后才进行购票。
	 */
	@Override
	public void run() {
		Thread thisThread = Thread.currentThread();
		try {
				List<UserInfo> ls = new ArrayList<UserInfo>();
				int pageIndex = 0;
				boolean hasNext = true;
				rob.console("start load contact...");
				while (hasNext && Constants.isLoginSuc && blinker == thisThread ){//循环加載
					rob.console("load page "+ pageIndex);
					Page<UserInfo> page = client.loadContacts(pageIndex, Config.getPageSize());
					if(page!=null){
						hasNext = page.hasNextPage();
						pageIndex = page.getNextPage();
						ls.addAll(page.getDatalist());
					}else{
						break;
					}
					
				}
				rob.addContacts(ls);	
				rob.console("contacts load success.");
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				rob.enableLoadBtn();
			}
		}
}
