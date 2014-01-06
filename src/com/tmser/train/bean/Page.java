package com.tmser.train.bean;

import java.io.Serializable;
import java.util.List;

public class Page<T> implements Serializable
{

	   private static final long serialVersionUID = -6868950745522147470L;
	   private List<T> datalist;
	   private int currentPage = 1;	//当前页索引
	   private int pageSize = 10;		//每页记录数
	   private int pageTotal = 0;    //总页数
	   

	   /**
	    * 构造分页对象
	    * @param datalist 当前页数据列表
	    * @param pageSize 每页记录条数
	    * 起始记录数，每页记录数，总记录数使用默认值
	    */
	   public Page(List<T> datalist,int pageTotal)
	   {
		    this.pageTotal = pageTotal;
		    this.datalist = datalist;
	   }
	   
	 /**
	  * 构造分页对象
	  * @param datalist 当前页数据列表，不能为null
	  * @param startRecode 起始记录数，必须大于等于0，且不大于 totalCount
	  * @param pageSize 没页记录数 ，必须大于等于0
	  * @param totalCount 总记录数，必须大于等于0
	  */
	   public Page(List<T> datalist, int currentPage, int pageSize)
	   {
		   if(currentPage < 1 || pageSize < 1 )
			  throw new IllegalArgumentException();
		  
		   if(datalist == null){
			    throw new NullPointerException("datalist must not be null!");
		   }
		   this.datalist = datalist;
		   this.currentPage = currentPage;
		   this.pageSize = pageSize;
	   }
	   
	   public int getCurrentPage() {
			return currentPage;
		}

		public void setCurrentPage(int currentPage) {
			this.currentPage = currentPage;
		}

		public void setPageSize(int pageSize) {
				this.pageSize = pageSize;
		}
	 
	   public List<T> getDatalist()
	   {
	     return datalist;
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
	   public boolean hasNextPage()
	   {
	     return  currentPage < pageTotal;
	   }
	   
	 /**
	  * 是否有上一页
	  */
	   public boolean hasPreviousPage()
	   {
	     return currentPage > 0;
	   }
	   
 
	   /**
	    * 获取下页页数
	    * 如果有下一页返回下页页数，没有返回-1
	    * @return
	    */
	   public int getNextPage()
	   {
	     return hasNextPage()?currentPage + 1:-1;
	   }
	 
		 
	   
	 /**
	  * 总页数
	  * @return
	  */
	   public int getTotalPages()
	   {
	     return pageTotal;
	   }
	   
	 /** 
	  * 每页记录数
	  * @return
	  */
	   public int getPageSize()
	   {
	     return pageSize;
	   }
}
