/**
 * 
 */
package com.tmser.train.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import com.tmser.train.logic.CaptchaClient;

/**
 *
 * @author tjx
 * @version 2.0
 * 2015-5-10
 */
public class DialogLabel extends JLabel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5628658249898253706L;
	
	protected CaptchaClient client;
	
	private String url;
	
	private static final int SIDELENGTH = 10;
	
	private List<Rectangle2D> squares;
	
	private Rectangle2D current;
	
	private JButton fleshBtn;
	
	public DialogLabel(CaptchaClient client,String url,String label,int horizontalAlignment){
		super(label,horizontalAlignment);
		this.client = client;
		this.url = url;
		fleshBtn = new JButton("刷新");
		fleshBtn.setMargin(new Insets(0,0,0,0));
		fleshBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fleshImage(); 
			}
		});
		fleshBtn.setBounds(250, 2, 40, 28);
		this.add(fleshBtn);
		
		squares = new ArrayList<Rectangle2D>();
		MouseAdapter ma = new MouserHandle();
		addMouseListener(ma);
		addMouseMotionListener(ma);
	}
		
	protected ImageIcon getImage(){
	byte[] image = client.getCodeByte(url);
		return new ImageIcon(image);
	}

	protected void fleshImage(){
		this.setIcon(getImage());
		squares.clear();
		current = null;
		repaint();
	}
	
		
	@Override
	protected void paintComponent(Graphics g) {
			// TODO Auto-generated method stub
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D)g;
			Stroke stroke=new BasicStroke(3.0f);//设置线宽为3.0
			g2.setColor(Color.RED);
			g2.setStroke(stroke);
			for(Rectangle2D r : squares){
				g2.draw(r);
			}
			
	}
		
	protected Rectangle2D find(Point2D p){
			for(Rectangle2D r : squares){
				if(r.contains(p)) return r;
			}
			
			return null;
	}
		
		
	protected void add(Point2D p){
			double x = p.getX();
			double y = p.getY();
			current = new Rectangle2D.Double(x-SIDELENGTH/2 ,y - SIDELENGTH /2 ,SIDELENGTH,SIDELENGTH);
			squares.add(current);
			repaint();
	}
		
	protected void remove(Rectangle2D r){
			if(r == null)  return;
			if(r == current) current = null;
			squares.remove(r);
			repaint();
	}
		
	private class MouserHandle extends MouseAdapter{
			 public void mousePressed(MouseEvent e) {
				 current = find(e.getPoint());
				 if(current == null){
					 add(e.getPoint());
				 }
			 }
			 
			 public void mouseClicked(MouseEvent e) {
				 current = find(e.getPoint());
				 if(current != null && e.getClickCount() >= 2){
					 remove(current);
				 }
			 }
			 
			 public void mouseMoved(MouseEvent e){
				 if(find(e.getPoint()) == null){
					 setCursor(Cursor.getDefaultCursor());
				 }else{
					 setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				 }
			 }
			 
		}
	
	public String getCode(){
		StringBuilder sb = new StringBuilder();
		for(Rectangle2D r : squares){
			sb.append(Double.valueOf(r.getCenterX()).intValue()).append(",")
			.append(Double.valueOf(r.getCenterX()).intValue()).append(",");
		}
		
		return sb.length() > 0 ? sb.substring(0,sb.length()-1):null;
	}
	
}
