package com.ywh.train.bean;

/**
 * 
 * @author tjx
 */
public class TokenAndTicket {

	private String token;
	private String ticket;
	
	public TokenAndTicket(String tak){
		if(tak == null){
			throw new IllegalArgumentException("token and ticket can be null!");
		}
		String[] tokens = tak.split(",");
		if(tokens.length >1){
			this.token = tokens[0];
			this.ticket = tokens[1];
		}
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	
	@Override
	public String toString(){
		return "token = "+token+", ticket = "+ ticket;
	}
	
}
