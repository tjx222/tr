package com.tmser.train.logic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.tmser.train.DamaUtil;
import com.tmser.train.ResManager;
import com.tmser.train.gui.DialogLabel;
import com.tmser.train.gui.RobTicket;

public abstract class BaseThread extends Thread {
	protected CaptchaClient client;
	public void setClient(CaptchaClient client) {
		this.client = client;
	}

	protected RobTicket rob;
	/**
	 * 构造函数
	 * 
	 * @param robTicket
	 */
	public BaseThread(CaptchaClient client,RobTicket rob) {
		this.rob = rob;
		this.client = client;
	}
	
	/**
	 * 构造函数
	 * 
	 * @param robTicket
	 */
	public BaseThread(RobTicket rob) {
		this.rob = rob;
		this.client = rob.getClient();
	}
	
	/**
	 * 是否自动验证
	 * @return
	 */
	public boolean getIsAuto() {
		return false;
	}

    /**
     * Writes a byte array to a file creating the file if it does not exist.
     *
     * @param file  the file to write to
     * @param data  the content to write to the file
     * @param append if {@code true}, then bytes will be added to the
     * end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     * @since IO 2.1
     */
    public static void writeByteArrayToFile(File file, byte[] data) throws IOException {
        OutputStream out = null;
        try {
            out = openOutputStream(file, false);
            out.write(data);
            out.close(); // don't swallow close Exception if copy completes normally
        } finally {
        	 try {
                 if (out != null) {
                	 out.close();
                 }
             } catch (IOException ioe) {
                 // ignore
             }
        }
    }
    
    
    /**
     * Opens a {@link FileOutputStream} for the specified file, checking and
     * creating the parent directory if it does not exist.
     * <p>
     * At the end of the method either the stream will be successfully opened,
     * or an exception will have been thrown.
     * <p>
     * The parent directory will be created if it does not exist.
     * The file will be created if it does not exist.
     * An exception is thrown if the file object exists but is a directory.
     * An exception is thrown if the file exists but cannot be written to.
     * An exception is thrown if the parent directory cannot be created.
     * 
     * @param file  the file to open for output, must not be {@code null}
     * @param append if {@code true}, then bytes will be added to the
     * end of the file rather than overwriting
     * @return a new {@link FileOutputStream} for the specified file
     * @throws IOException if the file object is a directory
     * @throws IOException if the file cannot be written to
     * @throws IOException if a parent directory needs creating but that fails
     * @since 2.1
     */
    public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canWrite() == false) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file, append);
    }
	/**
	 * 获得自动识别的验证or用户输入
	 */
	protected String getRandCodeDailog(String url) {
		byte[] image = client.getCodeByte(url);
/*		File imgFile = new File("randcode.jpg");
		try {
			writeByteArrayToFile(imgFile, image);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		String randCodeByRob = "";
		int count = 10; // 避免死循环
		while (rob.isAutocode() && randCodeByRob.length() <= 3 && count-- > 0) {
			randCodeByRob = getCode(image);
			rob.console("RandCode: "+randCodeByRob);
		}
		if (!rob.isAutocode() || count == 0) {//手动输入验证码
			if(count == 0){
				rob.console(ResManager.getString("LogicThread.err.code"));
			}
			

			DialogLabel label = new DialogLabel(client,url,"", JLabel.CENTER);
			label.setIcon(new ImageIcon(image));
			//CodeMouseAdapter cma = new CodeMouseAdapter(randCodeByRob,url);
			//label.addMouseListener(cma);
			int rs = JOptionPane.showConfirmDialog(rob.getFrame(), label,
					ResManager.getString("LogicThread.25"), JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
			
				//randCodeByRob = cma.getRandCodeByRob();
			//} else {
		   // randCodeByRob = input;
			if(JOptionPane.OK_OPTION == rs){
				randCodeByRob = label.getCode();
			}else{
				randCodeByRob = null;
			}
		}
		return randCodeByRob;
	}
	
	/**
	 * 验证码识别
	 * @param image
	 * @return
	 */
	private String getCode(byte[] image){
		String randCodeByRob = "";
  	    String[] vcode = new String[1];
		int ret = DamaUtil.d2Buf(image, vcode);
		if(ret > 0){
			randCodeByRob = vcode[0];
		}else if(ret == DamaUtil.ERR_NOT_INIT){
			JOptionPane.showConfirmDialog(rob.getFrame(), ResManager.getString("RobTicket.msg.damaUserFailture"),
					ResManager.getString("RobTicket.msg.tip"),JOptionPane.OK_OPTION);
		}else{
			rob.console(ResManager.getString("dama2 error:"+ ret));
		}
		return randCodeByRob;
	}


}
