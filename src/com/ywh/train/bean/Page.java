package com.ywh.train.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page<T> implements Serializable
{

	   private static final long serialVersionUID = -6868950745522147470L;
	   private List<T> datalist;
	   private int startRecode = 0;	//起始记录数
	   private int pageSize = 10;		//每页记录数
	   private int totalCount = 0;    //总记录数
	   
	   /**
	    * 构造分页对象
	    * @param datalist 当前页数据列表
	    * 起始记录数，每页记录数，总记录数使用默认值
	    */
	   public Page(List<T> datalist)
	   {
		    this(datalist, 0, 10, 0);
	   }
	   
	   /**
	    * 构造分页对象
	    * @param datalist 当前页数据列表
	    * @param pageSize 每页记录条数
	    * 起始记录数，每页记录数，总记录数使用默认值
	    */
	   public Page(List<T> datalist,int pageSize)
	   {
		    this(datalist, 0, pageSize, 0);
	   }
	   
	 /**
	  * 构造分页对象
	  * @param datalist 当前页数据列表，不能为null
	  * @param startRecode 起始记录数，必须大于等于0，且不大于 totalCount
	  * @param pageSize 没页记录数 ，必须大于等于0
	  * @param totalCount 总记录数，必须大于等于0
	  */
	   public Page(List<T> datalist, int startRecode, int pageSize, int totalCount)
	   {
		   if(startRecode < 0 || pageSize < 1 || totalCount < 0 || startRecode > totalCount)
			  throw new IllegalArgumentException();
		  
		   if(datalist == null){
			    throw new NullPointerException("datalist must not be null!");
		   }
		   this.datalist = datalist;
		   this.startRecode = startRecode;
		   this.pageSize = pageSize;
		   this.totalCount = totalCount;
	   }
	   
	   public void setTotalCount(int totalCount){
		   if( totalCount < 0 || startRecode > totalCount)
				  throw new IllegalArgumentException();
		   this.totalCount = totalCount;
	   }
	 
	   public List<T> getDatalist()
	   {
	     return datalist;
	   }
	 
	   public void setDatalist(ArrayList<T> datalist) {
		   if(datalist == null){
			    throw new NullPointerException("datalist must not be null!");
		   }
		   this.datalist = datalist;
	   }
	   /**
	    * 当前页记录数
	    * @return
	    */
	   public int getPageCount()
	   {
	     return datalist.size();
	   }
	   
	 /**
	  * 是否有下一页
	  * @return
	  */
	   public boolean isHasNextPage()
	   {
	     return startRecode + pageSize < totalCount;
	   }
	   
	 /**
	  * 是否有前一页，因startOfCurPage 本页起始记录，只要其大于零说明本页大于pageSize，则上页存在
	  * @return
	  */
	   public boolean isHasPreviousPage()
	   {
	     return startRecode > pageSize;
	   }
	   
	 /**
	  * 获取当前页
	  * @return
	  */
	   public int getCurPage()
	   {
	     if (getTotalCount() == 0) {
	       return 0;
	     }
	     return startRecode / pageSize + 1;
	   }
	 
	   /**
	    * 获取下页页数
	    * @return
	    */
	   public int getNextPage()
	   {
	     return getCurPage() + 1;
	   }
	 
	   /**
	    * 获取上一页
	    * @return
	    */
	   public int getPreviousPage()
	   {
	     if (getCurPage() == 0) {
	       return 0;
	     }
	     return getCurPage() - 1;
	   }
	 
	   /**
	    * 本页起始记录索引,从0 开始
	    * @return
	    */
	   public int getStartRecodeIndex()
	   {
	     return startRecode;
	   }
	 
	   /**
	    * 本页第一条记录序号
	    * @return
	    */
	   public int getStartRecode()
	   {
	     if (getTotalCount() == 0) {
	       return 0;
	     }
	 
	     return startRecode + 1;
	   }
	 
	   /**
	    * 本页末条记录数
	    * @return
	    */
	   public int getEndOfCurPage()
	   {
	     return startRecode + getPageCount();
	   }
	 
	   /**
	    * 下一页的起始记录数
	    * @return
	    */
	   public int getStartOfNextPage()
	   {
	     return startRecode + pageSize;
	   }
	 /**
	  * 前一页起始记录
	  * @return
	  */
	   public int getStartOfPreviousPage()
	   {
	     return Math.max(startRecode - pageSize, 0);
	   }
	 
	   /**
	    * 最后一页起始记录
	    * @return
	    */
	   public int getStartOfLastPage()
	   {
	     if (this.totalCount % getPageSize() == 0) {
	       return this.totalCount - getPageSize();
	     }
	 
	     return this.totalCount - this.totalCount % getPageSize();
	   }
	 
	   /**
	    * 总记录数
	    * @return
	    */
	   public int getTotalCount()
	   {
	     return totalCount;
	   }
	   
	 /**
	  * 总页数
	  * @return
	  */
	   public int getTotalPages()
	   {
	     return totalCount % pageSize ==0 ? 
	    		 totalCount / pageSize : totalCount / pageSize + 1;
	   }
	   
	 /** 
	  * 每页记录数
	  * @return
	  */
	   public int getPageSize()
	   {
	     return pageSize;
	   }
	 
	   
	 /**
	  * 给定页数之前的总记录数
	  * 如：page =1 ,pagesize =10 则返回0，因为只有一页
	  * @param page
	  * @return
	  */
	   public int getStartCount(int page) {
		   return (page - 1) > 0 ? (page - 1) * pageSize : 0;
	   }
	 
	 }
