/*
 * **************************************************************************
 * Project Name		:	VAJRA
 * 
 * File Name		:	BillItem
 * 
 * Purpose			:	Represents BillItem table, takes care of intializing
 * 						assining and returning values of all the variables.
 * 
 * DateOfCreation	:	16-October-2012
 * 
 * Author			:	Balasubramanya Bharadwaj B S
 * 
 * **************************************************************************
 */

package com.wep.common.app.models;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

@SuppressLint("ParcelCreator")
public class BillItemBean implements Parcelable{

	public static final String BILL_ITEM_BEAN_PARCELABLE_KEY ="bill_item_bean";

	String strGSTIN, strCustName, strCustStateCode, strCustAddress, strCustPhone, strInvoiceNo, strInvoiceDate, strCustId,
			strSupplyType, strBusinessType, strTaxationType, strHSNCode,
			strItemName, strBarcode, strUOM, strIsReverseTaxEnabled, strBillingMode, strSalesManId, strItemLongName;
	int iID, iItemId, iTaxType, iCategCode, iDeptCode, iBrandCode,iBillStatus, tempId, isRupeeBilling;
	double dblQty, dblOriginalRate, dblValue, dblTaxbleValue, dblAmount,
			dblIGSTRate, dblIGSTAmount, dblCGSTRate, dblCGSTAmount,
			dblSGSTRate, dblSGSTAmount, dblCessRate, dblCessAmount, dblTotalAdditionalCessAmount,
			dblSubTotal, dblOtherCharges, dblCessAmountPerUnit, dblAdditionalCessAmount,
			dblModifierAmount, dblDiscountPercent, dblDiscountAmount,
			dblTaxAmount, dblTaxPercent;

	double dblMRP, dblRetailPrice, dblWholeSalePrice, dblRupeeBillingReferenceRate;
	double dblReturnCGSTAmount, dblReturnSGSTAmount, dblReturnIGSTAmount, dblReturncessAmount, dblReturnAmount, dblReturnQuantity, dblReturnedQuantity;

	protected BillItemBean(Parcel in) {
		strGSTIN = in.readString();
		strCustName = in.readString();
		strCustStateCode = in.readString();
		strCustAddress = in.readString();
		strCustPhone = in.readString();
		strInvoiceNo = in.readString();
		strInvoiceDate = in.readString();
		strCustId = in.readString();
		strSupplyType = in.readString();
		strBusinessType = in.readString();
		strTaxationType = in.readString();
		strHSNCode = in.readString();
		strItemName = in.readString();
		strBarcode = in.readString();
		strUOM = in.readString();
		strIsReverseTaxEnabled = in.readString();
		strBillingMode = in.readString();
		strSalesManId = in.readString();
		strItemLongName = in.readString();
		iID = in.readInt();
		iItemId = in.readInt();
		iTaxType = in.readInt();
		iCategCode = in.readInt();
		iDeptCode = in.readInt();
		iBrandCode = in.readInt();
		iBillStatus = in.readInt();
		tempId = in.readInt();
		isRupeeBilling = in.readInt();
		dblQty = in.readDouble();
		dblOriginalRate = in.readDouble();
		dblValue = in.readDouble();
		dblTaxbleValue = in.readDouble();
		dblAmount = in.readDouble();
		dblIGSTRate = in.readDouble();
		dblIGSTAmount = in.readDouble();
		dblCGSTRate = in.readDouble();
		dblCGSTAmount = in.readDouble();
		dblSGSTRate = in.readDouble();
		dblSGSTAmount = in.readDouble();
		dblCessRate = in.readDouble();
		dblCessAmount = in.readDouble();
		dblTotalAdditionalCessAmount = in.readDouble();
		dblSubTotal = in.readDouble();
		dblOtherCharges = in.readDouble();
		dblCessAmountPerUnit = in.readDouble();
		dblAdditionalCessAmount = in.readDouble();
		dblModifierAmount = in.readDouble();
		dblDiscountPercent = in.readDouble();
		dblDiscountAmount = in.readDouble();
		dblTaxAmount = in.readDouble();
		dblTaxPercent = in.readDouble();
		dblMRP = in.readDouble();
		dblRetailPrice = in.readDouble();
		dblWholeSalePrice = in.readDouble();
		dblRupeeBillingReferenceRate = in.readDouble();
		this.dblReturnCGSTAmount=0;
		this.dblReturnSGSTAmount=0;
		this.dblReturnIGSTAmount=0;
		this.dblReturncessAmount=0;
		this.dblReturnAmount=0;
		this.dblReturnQuantity=0;
		this.dblReturnedQuantity = 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(strGSTIN);
		dest.writeString(strCustName);
		dest.writeString(strCustStateCode);
		dest.writeString(strCustAddress);
		dest.writeString(strCustPhone);
		dest.writeString(strInvoiceNo);
		dest.writeString(strInvoiceDate);
		dest.writeString(strCustId);
		dest.writeString(strSupplyType);
		dest.writeString(strBusinessType);
		dest.writeString(strTaxationType);
		dest.writeString(strHSNCode);
		dest.writeString(strItemName);
		dest.writeString(strBarcode);
		dest.writeString(strUOM);
		dest.writeString(strIsReverseTaxEnabled);
		dest.writeString(strBillingMode);
		dest.writeString(strSalesManId);
		dest.writeString(strItemLongName);
		dest.writeInt(iID);
		dest.writeInt(iItemId);
		dest.writeInt(iTaxType);
		dest.writeInt(iCategCode);
		dest.writeInt(iDeptCode);
		dest.writeInt(iBrandCode);
		dest.writeInt(iBillStatus);
		dest.writeInt(tempId);
		dest.writeInt(isRupeeBilling);
		dest.writeDouble(dblQty);
		dest.writeDouble(dblOriginalRate);
		dest.writeDouble(dblValue);
		dest.writeDouble(dblTaxbleValue);
		dest.writeDouble(dblAmount);
		dest.writeDouble(dblIGSTRate);
		dest.writeDouble(dblIGSTAmount);
		dest.writeDouble(dblCGSTRate);
		dest.writeDouble(dblCGSTAmount);
		dest.writeDouble(dblSGSTRate);
		dest.writeDouble(dblSGSTAmount);
		dest.writeDouble(dblCessRate);
		dest.writeDouble(dblCessAmount);
		dest.writeDouble(dblTotalAdditionalCessAmount);
		dest.writeDouble(dblSubTotal);
		dest.writeDouble(dblOtherCharges);
		dest.writeDouble(dblCessAmountPerUnit);
		dest.writeDouble(dblAdditionalCessAmount);
		dest.writeDouble(dblModifierAmount);
		dest.writeDouble(dblDiscountPercent);
		dest.writeDouble(dblDiscountAmount);
		dest.writeDouble(dblTaxAmount);
		dest.writeDouble(dblTaxPercent);
		dest.writeDouble(dblMRP);
		dest.writeDouble(dblRetailPrice);
		dest.writeDouble(dblWholeSalePrice);
		dest.writeDouble(dblRupeeBillingReferenceRate);
		dest.writeDouble(dblReturnCGSTAmount);
		dest.writeDouble(dblReturnSGSTAmount);
		dest.writeDouble(dblReturnIGSTAmount);
		dest.writeDouble(dblReturncessAmount);
		dest.writeDouble(dblReturnAmount);
		dest.writeDouble(dblReturnQuantity);
		dest.writeDouble(dblReturnedQuantity);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<BillItemBean> CREATOR = new Creator<BillItemBean>() {
		@Override
		public BillItemBean createFromParcel(Parcel in) {
			return new BillItemBean(in);
		}

		@Override
		public BillItemBean[] newArray(int size) {
			return new BillItemBean[size];
		}
	};

	public double getDblReturnedQuantity() {
		return dblReturnedQuantity;
	}

	public void setDblReturnedQuantity(double dblReturnedQuantity) {
		this.dblReturnedQuantity = dblReturnedQuantity;
	}

	public double getDblReturnCGSTAmount() {
		return dblReturnCGSTAmount;
	}

	public void setDblReturnCGSTAmount(double dblReturnCGSTAmount) {
		this.dblReturnCGSTAmount = dblReturnCGSTAmount;
	}

	public double getDblReturnSGSTAmount() {
		return dblReturnSGSTAmount;
	}

	public void setDblReturnSGSTAmount(double dblReturnSGSTAmount) {
		this.dblReturnSGSTAmount = dblReturnSGSTAmount;
	}

	public double getDblReturnIGSTAmount() {
		return dblReturnIGSTAmount;
	}

	public void setDblReturnIGSTAmount(double dblReturnIGSTAmount) {
		this.dblReturnIGSTAmount = dblReturnIGSTAmount;
	}

	public double getDblReturncessAmount() {
		return dblReturncessAmount;
	}

	public void setDblReturncessAmount(double dblReturncessAmount) {
		this.dblReturncessAmount = dblReturncessAmount;
	}

	public double getDblReturnAmount() {
		return dblReturnAmount;
	}

	public void setDblReturnAmount(double dblReturnAmount) {
		this.dblReturnAmount = dblReturnAmount;
	}

	public double getDblReturnQuantity() {
		return dblReturnQuantity;
	}

	public void setDblReturnQuantity(double dblReturnQuantity) {
		this.dblReturnQuantity = dblReturnQuantity;
	}

	public double getDblRupeeBillingReferenceRate() {
		return dblRupeeBillingReferenceRate;
	}

	public void setDblRupeeBillingReferenceRate(double dblRupeeBillingReferenceRate) {
		this.dblRupeeBillingReferenceRate = dblRupeeBillingReferenceRate;
	}

	public int getIsRupeeBilling() {
		return isRupeeBilling;
	}

	public void setIsRupeeBilling(int isRupeeBilling) {
		this.isRupeeBilling = isRupeeBilling;
	}

	public int getTempId() {
		return tempId;
	}

	public void setTempId(int tempId) {
		this.tempId = tempId;
	}

	public int getiBrandCode() {
		return iBrandCode;
	}

	public void setiBrandCode(int iBrandCode) {
		this.iBrandCode = iBrandCode;
	}

	public String getStrGSTIN() {
		return strGSTIN;
	}

	public void setStrGSTIN(String strGSTIN) {
		this.strGSTIN = strGSTIN;
	}

	public String getStrCustName() {
		return strCustName;
	}

	public void setStrCustName(String strCustName) {
		this.strCustName = strCustName;
	}

	public String getStrCustStateCode() {
		return strCustStateCode;
	}

	public void setStrCustStateCode(String strCustStateCode) {
		this.strCustStateCode = strCustStateCode;
	}

	public String getStrCustId() {
		return strCustId;
	}

	public void setStrCustId(String strCustId) {
		this.strCustId = strCustId;
	}

	public String getStrCustAddress() {
		return strCustAddress;
	}

	public void setStrCustAddress(String strCustAddress) {
		this.strCustAddress = strCustAddress;
	}

	public String getStrCustPhone() {
		return strCustPhone;
	}

	public void setStrCustPhone(String strCustPhone) {
		this.strCustPhone = strCustPhone;
	}

	public String getStrInvoiceNo() {
		return strInvoiceNo;
	}

	public void setStrInvoiceNo(String strInvoiceNo) {
		this.strInvoiceNo = strInvoiceNo;
	}

	public String getStrInvoiceDate() {
		return strInvoiceDate;
	}

	public void setStrInvoiceDate(String strInvoiceDate) {
		this.strInvoiceDate = strInvoiceDate;
	}

	public String getStrSupplyType() {
		return strSupplyType;
	}

	public void setStrSupplyType(String strSupplyType) {
		this.strSupplyType = strSupplyType;
	}

	public String getStrBusinessType() {
		return strBusinessType;
	}

	public void setStrBusinessType(String strBusinessType) {
		this.strBusinessType = strBusinessType;
	}

	public String getStrTaxationType() {
		return strTaxationType;
	}

	public void setStrTaxationType(String strTaxationType) {
		this.strTaxationType = strTaxationType;
	}

	public String getStrHSNCode() {
		return strHSNCode;
	}

	public void setStrHSNCode(String strHSNCode) {
		this.strHSNCode = strHSNCode;
	}

	public String getStrItemName() {
		return strItemName;
	}

	public void setStrItemName(String strItemName) {
		this.strItemName = strItemName;
	}

	public String getStrBarcode() {
		return strBarcode;
	}

	public void setStrBarcode(String strBarcode) {
		this.strBarcode = strBarcode;
	}

	public String getStrUOM() {
		return strUOM;
	}

	public void setStrUOM(String strUOM) {
		this.strUOM = strUOM;
	}

	public String getStrIsReverseTaxEnabled() {
		return strIsReverseTaxEnabled;
	}

	public void setStrIsReverseTaxEnabled(String strIsReverseTaxEnabled) {
		this.strIsReverseTaxEnabled = strIsReverseTaxEnabled;
	}

	public String getStrBillingMode() {
		return strBillingMode;
	}

	public void setStrBillingMode(String strBillingMode) {
		this.strBillingMode = strBillingMode;
	}

	public int getiID() {
		return iID;
	}

	public void setiID(int iID) {
		this.iID = iID;
	}

	public int getiItemId() {
		return iItemId;
	}

	public void setiItemId(int iItemId) {
		this.iItemId = iItemId;
	}

	public int getiTaxType() {
		return iTaxType;
	}

	public void setiTaxType(int iTaxType) {
		this.iTaxType = iTaxType;
	}

	public int getiCategCode() {
		return iCategCode;
	}

	public void setiCategCode(int iCategCode) {
		this.iCategCode = iCategCode;
	}

	public int getiDeptCode() {
		return iDeptCode;
	}

	public void setiDeptCode(int iDeptCode) {
		this.iDeptCode = iDeptCode;
	}

	public int getiBillStatus() {
		return iBillStatus;
	}

	public void setiBillStatus(int iBillStatus) {
		this.iBillStatus = iBillStatus;
	}

	public double getDblQty() {
		return dblQty;
	}

	public void setDblQty(double dblQty) {
		this.dblQty = dblQty;
	}

	public double getDblOriginalRate() {
		return dblOriginalRate;
	}

	public void setDblOriginalRate(double dblOriginalRate) {
		this.dblOriginalRate = dblOriginalRate;
	}

	public double getDblValue() {
		return dblValue;
	}

	public void setDblValue(double dblValue) {
		this.dblValue = dblValue;
	}

	public double getDblTaxbleValue() {
		return dblTaxbleValue;
	}

	public void setDblTaxbleValue(double dblTaxbleValue) {
		this.dblTaxbleValue = dblTaxbleValue;
	}

	public double getDblAmount() {
		return dblAmount;
	}

	public void setDblAmount(double dblAmount) {
		this.dblAmount = dblAmount;
	}

	public double getDblIGSTRate() {
		return dblIGSTRate;
	}

	public void setDblIGSTRate(double dblIGSTRate) {
		this.dblIGSTRate = dblIGSTRate;
	}

	public double getDblIGSTAmount() {
		return dblIGSTAmount;
	}

	public void setDblIGSTAmount(double dblIGSTAmount) {
		this.dblIGSTAmount = dblIGSTAmount;
	}

	public double getDblCGSTRate() {
		return dblCGSTRate;
	}

	public void setDblCGSTRate(double dblCGSTRate) {
		this.dblCGSTRate = dblCGSTRate;
	}

	public double getDblCGSTAmount() {
		return dblCGSTAmount;
	}

	public void setDblCGSTAmount(double dblCGSTAmount) {
		this.dblCGSTAmount = dblCGSTAmount;
	}

	public double getDblSGSTRate() {
		return dblSGSTRate;
	}

	public void setDblSGSTRate(double dblSGSTRate) {
		this.dblSGSTRate = dblSGSTRate;
	}

	public double getDblSGSTAmount() {
		return dblSGSTAmount;
	}

	public void setDblSGSTAmount(double dblSGSTAmount) {
		this.dblSGSTAmount = dblSGSTAmount;
	}

	public double getDblCessRate() {
		return dblCessRate;
	}

	public void setDblCessRate(double dblCessRate) {
		this.dblCessRate = dblCessRate;
	}

	public double getDblCessAmount() {
		return dblCessAmount;
	}

	public void setDblCessAmount(double dblCessAmount) {
		this.dblCessAmount = dblCessAmount;
	}

	public double getDblSubTotal() {
		return dblSubTotal;
	}

	public void setDblSubTotal(double dblSubTotal) {
		this.dblSubTotal = dblSubTotal;
	}

	public double getDblOtherCharges() {
		return dblOtherCharges;
	}

	public void setDblOtherCharges(double dblOtherCharges) {
		this.dblOtherCharges = dblOtherCharges;
	}

	public double getDblModifierAmount() {
		return dblModifierAmount;
	}

	public void setDblModifierAmount(double dblModifierAmount) {
		this.dblModifierAmount = dblModifierAmount;
	}

	public double getDblDiscountPercent() {
		return dblDiscountPercent;
	}

	public void setDblDiscountPercent(double dblDiscountPercent) {
		this.dblDiscountPercent = dblDiscountPercent;
	}

	public double getDblDiscountAmount() {
		return dblDiscountAmount;
	}

	public void setDblDiscountAmount(double dblDiscountAmount) {
		this.dblDiscountAmount = dblDiscountAmount;
	}

	public double getDblTaxAmount() {
		return dblTaxAmount;
	}

	public void setDblTaxAmount(double dblTaxAmount) {
		this.dblTaxAmount = dblTaxAmount;
	}

	public double getDblTaxPercent() {
		return dblTaxPercent;
	}

	public void setDblTaxPercent(double dblTaxPercent) {
		this.dblTaxPercent = dblTaxPercent;
	}

	public double getDblMRP() {
		return dblMRP;
	}

	public void setDblMRP(double dblMRP) {
		this.dblMRP = dblMRP;
	}

	public double getDblRetailPrice() {
		return dblRetailPrice;
	}

	public void setDblRetailPrice(double dblRetailPrice) {
		this.dblRetailPrice = dblRetailPrice;
	}

	public double getDblWholeSalePrice() {
		return dblWholeSalePrice;
	}

	public void setDblWholeSalePrice(double dblWholeSalePrice) {
		this.dblWholeSalePrice = dblWholeSalePrice;
	}

	public String getStrSalesManId() {
		return strSalesManId;
	}

	public void setStrSalesManId(String strSalesManId) {
		this.strSalesManId = strSalesManId;
	}

	public double getDblCessAmountPerUnit() {
		return dblCessAmountPerUnit;
	}

	public void setDblCessAmountPerUnit(double dblCessAmountPerUnit) {
		this.dblCessAmountPerUnit = dblCessAmountPerUnit;
	}

	public String getStrItemLongName() {
		return strItemLongName;
	}

	public void setStrItemLongName(String strItemLongName) {
		this.strItemLongName = strItemLongName;
	}

	public double getDblAdditionalCessAmount() {
		return dblAdditionalCessAmount;
	}

	public void setDblAdditionalCessAmount(double dblAdditionalCessAmount) {
		this.dblAdditionalCessAmount = dblAdditionalCessAmount;
	}

	public double getDblTotalAdditionalCessAmount() {
		return dblTotalAdditionalCessAmount;
	}

	public void setDblTotalAdditionalCessAmount(double dblTotalAdditionalCessAmount) {
		this.dblTotalAdditionalCessAmount = dblTotalAdditionalCessAmount;
	}



	// Default constructor
	public BillItemBean(){
		this.dblOriginalRate = 0.00;
		this.strCustAddress = "";
		this.strUOM="";
		this.strIsReverseTaxEnabled="";
		this.strCustStateCode="";
		this.strCustName="";
		this.strGSTIN ="";
		this.strCustPhone="";
		this.strTaxationType="";
		this.dblSubTotal = 0;
		this.dblTaxbleValue = 0;
		this.strItemName = "";
		this.strSupplyType = "";
		this.iCategCode = 0;
		this.iDeptCode = 0;
		this.iBrandCode = 0;
		this.iItemId = 0;
		this.tempId = 0;
		this.dblReturnedQuantity = 0;
		//this.iKitchenCode = 0;
		this.strInvoiceNo = "";
		this.iTaxType = 0;
		this.isRupeeBilling = 0;
		this.dblAmount = 0;
		this.dblDiscountAmount = 0;
		this.dblDiscountPercent = 0;
		this.dblQty = 0;
		this.dblValue = 0;
		this.dblTaxAmount = 0;
		this.dblTaxPercent = 0;
		this.dblOtherCharges = 0;
		this.dblModifierAmount = 0;
		this.strBusinessType= "";
		//this.SupplierGSTIN= "";
		this.strInvoiceDate= "";
		this.strHSNCode="";
		this.dblIGSTAmount=0;
		this.dblIGSTRate= 0;
		this.dblCGSTRate=0;
		this.dblCGSTAmount=0;
		this.dblSGSTRate=0;
		this.dblSGSTAmount=0;
		this.dblCessRate=0;
		this.dblCessAmount=0;
		this.dblCessAmountPerUnit=0;
		this.dblAdditionalCessAmount=0;
		this.dblTotalAdditionalCessAmount=0;
		this.dblRupeeBillingReferenceRate = 0;
		//this.isGoodInwarded=0;
		this.iBillStatus=0;
		this.strSupplyType="";
		this.strSalesManId = "";
		this.strCustId = "-1";
		this.strItemLongName = this.strItemName;
	}


}
