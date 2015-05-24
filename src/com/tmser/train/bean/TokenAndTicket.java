package com.tmser.train.bean;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * 
 * @author tjx
 */
public class TokenAndTicket {

	private String token;
	private String ticket;
	private String keyIsChange;
	private String tourFlag;
	private String trainLocation;
	private NameValuePair valiadePair;
	
	public String getTourFlag() {
		return tourFlag;
	}
	public void setTourFlag(String tourFlag) {
		this.tourFlag = tourFlag;
	}
	public String getTrainLocation() {
		return trainLocation;
	}
	public void setTrainLocation(String trainLocation) {
		this.trainLocation = trainLocation;
	}
	public String getKeyIsChange() {
		return keyIsChange;
	}
	public void setKeyIsChange(String keyIsChange) {
		this.keyIsChange = keyIsChange;
	}
	public TokenAndTicket(String tak){
		if(tak == null){
			throw new IllegalArgumentException("token and ticket can be null!");
		}
		String[] tokens = tak.split(",");
		if(tokens.length >1){
			this.token = tokens[0];
			this.ticket = tokens[1];
			this.keyIsChange = tokens[2];
			this.tourFlag = tokens[3];
			this.trainLocation = tokens[4];
			this.valiadePair = new BasicNameValuePair(tokens[5], tokens[6]);
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
		return "token = "+token+", ticket = "+ ticket+", validatePair : "+ valiadePair.toString();
	}
	public NameValuePair getValiadePair() {
		return valiadePair;
	}
	public void setValiadePair(NameValuePair valiadePair) {
		this.valiadePair = valiadePair;
	}
	
	
	
}
