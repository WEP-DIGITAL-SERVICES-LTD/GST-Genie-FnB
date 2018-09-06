/*
 * **************************************************************************
 * Project Name		:	VAJRA
 * 
 * File Name		:	PaymentReceipt
 * 
 * Purpose			:	Represents PaymentReceipt table, takes care of intializing
 * 						assining and returning values of all the variables.
 * 
 * DateOfCreation	:	16-October-2012
 * 
 * Author			:	Balasubramanya Bharadwaj B S
 * 
 * **************************************************************************
 */

package com.wep.common.app.Database;

public class PaymentReceipt {
	
	// Private variables
	String strDate, strReason;
	int iBillType, iDescriptionId1, iDescriptionId2, iDescriptionId3, paymentReceiptNo;
	double dAmount;
	String DescriptionText;
	private String isDuplicate;
	private int headerPrintBold;
	private String HeaderLine1;
	private String HeaderLine2;
	private String HeaderLine3;
	private String HeaderLine4;
	private String HeaderLine5;
	private String FooterLine1;
	private String FooterLine2;
	private String FooterLine3;
	private String FooterLine4;
	private String FooterLine5;
	
	// Default constructor
	public PaymentReceipt(){
		
		this.strDate = "";
		this.strReason = "";
		this.iBillType = 0;
		this.iDescriptionId1 = 0;
		this.iDescriptionId2 = 0;
		this.iDescriptionId3 = 0;
		this.dAmount = 0;
		this.DescriptionText= "";
		
	}
	
	// Parameterized constructor
	public PaymentReceipt(String Date,String Reason,int BillType,String DescriptionText,int DescriptionId1,int DescriptionId2,int DescriptionId3,float Amount){
		
		this.strDate = Date;
		this.strReason = Reason;
		this.iBillType = BillType;
		this.iDescriptionId1 = DescriptionId1;
		this.iDescriptionId2 = DescriptionId2;
		this.iDescriptionId3 = DescriptionId3;
		this.dAmount = Amount;
		this.DescriptionText = DescriptionText;
			
	}

	public int getPaymentReceiptNo() {
		return paymentReceiptNo;
	}

	public void setPaymentReceiptNo(int paymentReceiptNo) {
		this.paymentReceiptNo = paymentReceiptNo;
	}

	public void setdAmount(double dAmount) {
		this.dAmount = dAmount;
	}

	public double getdAmount() {
		return dAmount;
	}

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

	public String getStrReason() {
		return strReason;
	}

	public void setStrReason(String strReason) {
		this.strReason = strReason;
	}

	public int getiBillType() {
		return iBillType;
	}

	public void setiBillType(int iBillType) {
		this.iBillType = iBillType;
	}

	public int getiDescriptionId1() {
		return iDescriptionId1;
	}

	public void setiDescriptionId1(int iDescriptionId1) {
		this.iDescriptionId1 = iDescriptionId1;
	}

	public int getiDescriptionId2() {
		return iDescriptionId2;
	}

	public void setiDescriptionId2(int iDescriptionId2) {
		this.iDescriptionId2 = iDescriptionId2;
	}

	public int getiDescriptionId3() {
		return iDescriptionId3;
	}

	public void setiDescriptionId3(int iDescriptionId3) {
		this.iDescriptionId3 = iDescriptionId3;
	}

	public String getIsDuplicate() {
		return isDuplicate;
	}

	public void setIsDuplicate(String isDuplicate) {
		this.isDuplicate = isDuplicate;
	}

	public int getHeaderPrintBold() {
		return headerPrintBold;
	}

	public void setHeaderPrintBold(int headerPrintBold) {
		this.headerPrintBold = headerPrintBold;
	}

	public String getHeaderLine1() {
		return HeaderLine1;
	}

	public void setHeaderLine1(String headerLine1) {
		HeaderLine1 = headerLine1;
	}

	public String getHeaderLine2() {
		return HeaderLine2;
	}

	public void setHeaderLine2(String headerLine2) {
		HeaderLine2 = headerLine2;
	}

	public String getHeaderLine3() {
		return HeaderLine3;
	}

	public void setHeaderLine3(String headerLine3) {
		HeaderLine3 = headerLine3;
	}

	public String getHeaderLine4() {
		return HeaderLine4;
	}

	public void setHeaderLine4(String headerLine4) {
		HeaderLine4 = headerLine4;
	}

	public String getHeaderLine5() {
		return HeaderLine5;
	}

	public void setHeaderLine5(String headerLine5) {
		HeaderLine5 = headerLine5;
	}

	public String getFooterLine1() {
		return FooterLine1;
	}

	public void setFooterLine1(String footerLine1) {
		FooterLine1 = footerLine1;
	}

	public String getFooterLine2() {
		return FooterLine2;
	}

	public void setFooterLine2(String footerLine2) {
		FooterLine2 = footerLine2;
	}

	public String getFooterLine3() {
		return FooterLine3;
	}

	public void setFooterLine3(String footerLine3) {
		FooterLine3 = footerLine3;
	}

	public String getFooterLine4() {
		return FooterLine4;
	}

	public void setFooterLine4(String footerLine4) {
		FooterLine4 = footerLine4;
	}

	public String getFooterLine5() {
		return FooterLine5;
	}

	public void setFooterLine5(String footerLine5) {
		FooterLine5 = footerLine5;
	}

	public String getDescriptionText() {
		return DescriptionText;
	}

	public void setDescriptionText(String descriptionText) {
		DescriptionText = descriptionText;
	}

	// getting Date
	public String getDate(){
		return this.strDate;
	}
	
	// getting Reason
	public String getReason(){
		return this.strReason;
	}
	
	// getting BillType
	public int getBillType(){
		return this.iBillType;
	}
	
	// getting DescriptionId1
	public int getDescriptionId1(){
		return this.iDescriptionId1;
	}
	
	// getting DescriptionId2
	public int getDescriptionId2(){
		return this.iDescriptionId2;
	}
		
	// getting DescriptionId3
	public int getDescriptionId3(){
		return this.iDescriptionId3;
	}
	
	// setting Date
	public void setDate(String Date){
		this.strDate = Date;
	}
	
	// setting Reason
	public void setReason(String Reason){
		this.strReason = Reason;
	}
		
	// setting BillType
	public void setBillType(int BillType){
		this.iBillType = BillType;
	}
		
	// setting DescriptionId1
	public void setDescriptionId1(int DescriptionId1){
		this.iDescriptionId1 = DescriptionId1;
	}
		
	// setting DescriptionId2
	public void setDescriptionId2(int DescriptionId2){
		this.iDescriptionId2 = DescriptionId2;
	}
			
	// setting DescriptionId3
	public void setDescriptionId3(int DescriptionId3){
		this.iDescriptionId3 = DescriptionId3;
	}
			
	// setting Amount
	public void setAmount(float Amount){
		this.dAmount = Amount;
	}

}
