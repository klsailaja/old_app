package com.ab.telugumoviequiz.games;

import java.io.Serializable;

public class Question implements Serializable {
	
	private int questionNumber;
	private String statement;
	private String optionA;
	private String optionB;
	private String optionC;
	private String optionD;
	private String nStatement;
	private String nOptionA;
	private String nOptionB;
	private String nOptionC;
	private String nOptionD;
	private int correctOption;
	private long questionStartTime;
	private boolean isFlipUsed;
	
	public int getQuestionNumber() {
		return questionNumber;
	}
	public void setQuestionNumber(int questionNumber) {
		this.questionNumber = questionNumber;
	}
	
	public String getStatement() {
		return statement;
	}
	public void setStatement(String statement) {
		this.statement = statement;
	}
	public String getOptionA() {
		return optionA;
	}
	public void setOptionA(String optionA) {
		this.optionA = optionA;
	}
	public String getOptionB() {
		return optionB;
	}
	public void setOptionB(String optionB) {
		this.optionB = optionB;
	}
	public String getOptionC() {
		return optionC;
	}
	public void setOptionC(String optionC) {
		this.optionC = optionC;
	}
	public String getOptionD() {
		return optionD;
	}
	public void setOptionD(String optionD) {
		this.optionD = optionD;
	}
	public String getnStatement() {
		return nStatement;
	}
	public void setnStatement(String nStatement) {
		this.nStatement = nStatement;
	}
	public String getnOptionA() {
		return nOptionA;
	}
	public void setnOptionA(String nOptionA) {
		this.nOptionA = nOptionA;
	}
	public String getnOptionB() {
		return nOptionB;
	}
	public void setnOptionB(String nOptionB) {
		this.nOptionB = nOptionB;
	}
	public String getnOptionC() {
		return nOptionC;
	}
	public void setnOptionC(String nOptionC) {
		this.nOptionC = nOptionC;
	}
	public String getnOptionD() {
		return nOptionD;
	}
	public void setnOptionD(String nOptionD) {
		this.nOptionD = nOptionD;
	}
	public int getCorrectOption() {
		return correctOption;
	}
	public void setCorrectOption(int correctOption) {
		this.correctOption = correctOption;
	}
	public long getQuestionStartTime() {
		return questionStartTime;
	}
	public void setQuestionStartTime(long questionStartTime) {
		this.questionStartTime = questionStartTime;
	}
	public boolean isFlipUsed() { return isFlipUsed; }
	public void setFlipUsed(boolean isFlipUsed) { this.isFlipUsed = isFlipUsed; }
}
