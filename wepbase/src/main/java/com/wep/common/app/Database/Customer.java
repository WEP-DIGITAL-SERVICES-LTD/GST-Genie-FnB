/*
 * **************************************************************************
 * Project Name		:	VAJRA
 * 
 * File Name		:	Customer
 * 
 * Purpose			:	Represents Customer table, takes care of intializing
 * 						assining and returning values of all the variables.
 * 
 * DateOfCreation	:	15-October-2012
 * 
 * Author			:	Balasubramanya Bharadwaj B S
 * 
 * **************************************************************************
 */

package com.wep.common.app.Database;

import android.os.Parcel;
import android.os.Parcelable;

public class Customer implements Parcelable {

	// Private Variable
	String strCustName, strCustPhone, strCustAddress, strCustGSTIN, strEmailId;
	int iCustId, iDepositAmtStatus, iRewardPoints,isActive,_id, isDelete;
	double dLastTransaction, dTotalTransaction;
	double dCreditAmount, dCreditLimit;
	double openingBalance, dblDepositAmt;

	private String isDuplicate;
	private String businessDate;
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

	private String strCustState, strCustCity;
	private int iCustPincode;

	// Default constructor
	public Customer() {
		this.strCustName = "";
		this.strCustPhone = "";
		this.strCustAddress = "";
		this.strCustGSTIN = "";
		this.strEmailId = "";
		this.iCustId = 0;
		this.iDepositAmtStatus = 0;
		this.iRewardPoints = 0;
		this.isActive = 0;
		this.isDelete = 0;
		this._id = 0;
		this.headerPrintBold = 0;
		this.dLastTransaction = 0;
		this.dTotalTransaction = 0;
		this.dCreditAmount = 0;
		this.dCreditLimit = 0;
		this.dblDepositAmt = 0;
		this.openingBalance = 0;
		this.HeaderLine1= "";
		this.HeaderLine2= "";
		this.HeaderLine3= "";
		this.HeaderLine4= "";
		this.HeaderLine5= "";
		this.FooterLine1= "";
		this.FooterLine2= "";
		this.FooterLine3= "";
		this.FooterLine4= "";
		this.FooterLine5= "";
		this.businessDate= "";
		this.strCustState = "";
		this.strCustCity = "";
		this.iCustPincode = -1;
	}

	// Parameterized construcor
	public Customer(String CustAddress, String CustName, String CustContactNumber, double LastTransaction,
					double TotalTransaction, double CreaditAmount,String gstin, double dCreditLimit, double dblDepositAmt) {
		this.strCustAddress = CustAddress;
		this.strCustName = CustName;
		this.strCustPhone = CustContactNumber;
		this.dLastTransaction = LastTransaction;
		this.dTotalTransaction = TotalTransaction;
		this.dCreditAmount = CreaditAmount;
		this.dCreditLimit = dCreditLimit;
		this.dblDepositAmt = dblDepositAmt;
		this.strCustGSTIN= gstin;
	}

	protected Customer(Parcel in) {
		strCustName = in.readString();
		strCustPhone = in.readString();
		strCustAddress = in.readString();
		strCustGSTIN = in.readString();
		strEmailId = in.readString();
		iCustId = in.readInt();
		iDepositAmtStatus = in.readInt();
		iRewardPoints = in.readInt();
		isActive = in.readInt();
		_id = in.readInt();
		isDelete = in.readInt();
		dLastTransaction = in.readDouble();
		dTotalTransaction = in.readDouble();
		dCreditAmount = in.readDouble();
		dCreditLimit = in.readDouble();
		openingBalance = in.readDouble();
		dblDepositAmt = in.readDouble();
		isDuplicate = in.readString();
		businessDate = in.readString();
		headerPrintBold = in.readInt();
		HeaderLine1 = in.readString();
		HeaderLine2 = in.readString();
		HeaderLine3 = in.readString();
		HeaderLine4 = in.readString();
		HeaderLine5 = in.readString();
		FooterLine1 = in.readString();
		FooterLine2 = in.readString();
		FooterLine3 = in.readString();
		FooterLine4 = in.readString();
		FooterLine5 = in.readString();
		strCustState = in.readString();
		strCustCity = in.readString();
		iCustPincode = in.readInt();
	}

	public static final Creator<Customer> CREATOR = new Creator<Customer>() {
		@Override
		public Customer createFromParcel(Parcel in) {
			return new Customer(in);
		}

		@Override
		public Customer[] newArray(int size) {
			return new Customer[size];
		}
	};

	public String getStrCustName() {
		return strCustName;
	}

	public void setStrCustName(String strCustName) {
		this.strCustName = strCustName;
	}

	public String getStrCustPhone() {
		return strCustPhone;
	}

	public void setStrCustPhone(String strCustPhone) {
		this.strCustPhone = strCustPhone;
	}

	public String getStrCustAddress() {
		return strCustAddress;
	}

	public void setStrCustAddress(String strCustAddress) {
		this.strCustAddress = strCustAddress;
	}

	public String getStrCustGSTIN() {
		return strCustGSTIN;
	}

	public void setStrCustGSTIN(String strCustGSTIN) {
		this.strCustGSTIN = strCustGSTIN;
	}

	public String getStrEmailId() {
		return strEmailId;
	}

	public void setStrEmailId(String strEmailId) {
		this.strEmailId = strEmailId;
	}

	public int getiCustId() {
		return iCustId;
	}

	public void setiCustId(int iCustId) {
		this.iCustId = iCustId;
	}

	public int getiDepositAmtStatus() {
		return iDepositAmtStatus;
	}

	public void setiDepositAmtStatus(int iDepositAmtStatus) {
		this.iDepositAmtStatus = iDepositAmtStatus;
	}

	public int getiRewardPoints() {
		return iRewardPoints;
	}

	public void setiRewardPoints(int iRewardPoints) {
		this.iRewardPoints = iRewardPoints;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(int isDelete) {
		this.isDelete = isDelete;
	}

	public double getdLastTransaction() {
		return dLastTransaction;
	}

	public void setdLastTransaction(double dLastTransaction) {
		this.dLastTransaction = dLastTransaction;
	}

	public double getdTotalTransaction() {
		return dTotalTransaction;
	}

	public void setdTotalTransaction(double dTotalTransaction) {
		this.dTotalTransaction = dTotalTransaction;
	}

	public double getdCreditAmount() {
		return dCreditAmount;
	}

	public void setdCreditAmount(double dCreditAmount) {
		this.dCreditAmount = dCreditAmount;
	}

	public double getdCreditLimit() {
		return dCreditLimit;
	}

	public void setdCreditLimit(double dCreditLimit) {
		this.dCreditLimit = dCreditLimit;
	}

	public double getOpeningBalance() {
		return openingBalance;
	}

	public void setOpeningBalance(double openingBalance) {
		this.openingBalance = openingBalance;
	}

	public double getDblDepositAmt() {
		return dblDepositAmt;
	}

	public void setDblDepositAmt(double dblDepositAmt) {
		this.dblDepositAmt = dblDepositAmt;
	}

	public String getIsDuplicate() {
		return isDuplicate;
	}

	public void setIsDuplicate(String isDuplicate) {
		this.isDuplicate = isDuplicate;
	}

	public String getBusinessDate() {
		return businessDate;
	}

	public void setBusinessDate(String businessDate) {
		this.businessDate = businessDate;
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

	public String getStrCustState() {
		return strCustState;
	}

	public void setStrCustState(String strCustState) {
		this.strCustState = strCustState;
	}

	public String getStrCustCity() {
		return strCustCity;
	}

	public void setStrCustCity(String strCustCity) {
		this.strCustCity = strCustCity;
	}

	public int getiCustPincode() {
		return iCustPincode;
	}

	public void setiCustPincode(int iCustPincode) {
		this.iCustPincode = iCustPincode;
	}

	public static Creator<Customer> getCREATOR() {
		return CREATOR;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(strCustName);
		parcel.writeString(strCustPhone);
		parcel.writeString(strCustAddress);
		parcel.writeString(strCustGSTIN);
		parcel.writeString(strEmailId);
		parcel.writeInt(iCustId);
		parcel.writeInt(iDepositAmtStatus);
		parcel.writeInt(iRewardPoints);
		parcel.writeInt(isActive);
		parcel.writeInt(_id);
		parcel.writeInt(isDelete);
		parcel.writeDouble(dLastTransaction);
		parcel.writeDouble(dTotalTransaction);
		parcel.writeDouble(dCreditAmount);
		parcel.writeDouble(dCreditLimit);
		parcel.writeDouble(openingBalance);
		parcel.writeDouble(dblDepositAmt);
		parcel.writeString(isDuplicate);
		parcel.writeString(businessDate);
		parcel.writeInt(headerPrintBold);
		parcel.writeString(HeaderLine1);
		parcel.writeString(HeaderLine2);
		parcel.writeString(HeaderLine3);
		parcel.writeString(HeaderLine4);
		parcel.writeString(HeaderLine5);
		parcel.writeString(FooterLine1);
		parcel.writeString(FooterLine2);
		parcel.writeString(FooterLine3);
		parcel.writeString(FooterLine4);
		parcel.writeString(FooterLine5);
		parcel.writeString(strCustState);
		parcel.writeString(strCustCity);
		parcel.writeInt(iCustPincode);
	}
}
