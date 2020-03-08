package com.iphayao.linebot.model;

import java.security.Timestamp;

import lombok.Data;

@Data
public class UserLog {
	
	public enum status {DEFAULT, SAVE, Q11, FINDEMP, FINDCONFIRM ,SELECT_EVENT, VOTE_FOODS};
	
	
	
	public status getStatusBot() {
		return statusBot;
	}
	
	public void setStatusBot(status statusBot) {
		this.statusBot = statusBot;
	}
	
	public UserLog(String userID, status statusBot) {
		this.userID = userID;
		this.statusBot = statusBot;
	}
	
	public UserLog() {
	}
	private String countVote;
	private String userID;
	private Integer leaveID;
	private status statusBot;
	private String leaveType;
	private String detail;
	private Timestamp startDate;
	private Timestamp end_Date;
	private String empCode;
	private String getempName;
	private String foodName;
	private String foodId;
	private Integer CountVout_CheckPossilibity;
	private String TextInputFromUser;
	
	//➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤➤


}
