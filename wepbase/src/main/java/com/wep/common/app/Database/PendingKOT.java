/*
 * **************************************************************************
 * Project Name		:	VAJRA
 * 
 * File Name		:	PendingKOT
 * 
 * Purpose			:	Represents PendingKOT table, takes care of intializing
 * 						assining and returning values of all the variables.
 * 
 * DateOfCreation	:	15-October-2012
 * 
 * Author			:	Balasubramanya Bharadwaj B S
 * 
 * **************************************************************************
 */

package com.wep.common.app.Database;

public class PendingKOT {

    // Private Variable
    String strItemName, strTime , HSNCode , POS , SupplyType,UOM;
    int iTokenNumber, iItemNumber, iTableNumber, iSubUdfNumber, iDeptCode, iTableSplitNo, iPrintKOTStatus,
            iCategCode, iKitchenCode, iEmployeeId, iCustId, iTaxType, iOrderMode, iIsCheckedOut;
    double dQuantity, dTaxPercent, dblTotalAdditionalCessAmount, dblAdditionalCessAmount, dblCessAmountPerUnit,
            dDiscountPercent, dDiscountAmount, dModifierAmount, dServiceTaxPercent;
    double cessRate, IGSTRate;
    double  originalrate, dRate, dAmount,taxableValue, fServiceTaxAmount, fTaxAmount, cessAmount,IGSTAmount;

    // Default constructor
    public PendingKOT() {
        this.SupplyType="";
        this.strItemName = "";
        this.strTime = "";
        this.originalrate=0.00;
        this.iCategCode = 0;
        this.iCustId = 0;
        this.iDeptCode = 0;
        this.iEmployeeId = 0;
        this.iItemNumber = 0;
        this.iKitchenCode = 0;
        this.iSubUdfNumber = 0;
        this.iTableNumber = 0;
        this.iTokenNumber = 0;
        this.dAmount = 0;
        this.dDiscountAmount = 0;
        this.dDiscountPercent = 0;
        this.dQuantity = 0;
        this.dRate = 0;
        this.fTaxAmount = 0;
        this.taxableValue = 0;
        this.dTaxPercent = 0;
        this.iTaxType = 0;
        this.dModifierAmount = 0;
        this.fServiceTaxAmount = 0;
        this.dServiceTaxPercent = 0;
        this.iOrderMode = 0;
        this.iIsCheckedOut = 0;
        this.iTableSplitNo = 0;
        this.HSNCode= "";
        this.POS ="";
        this.iPrintKOTStatus = 0;
        this.UOM="";
        this.IGSTRate=0;
        this.IGSTAmount=0;
        this.cessRate=0;
        this.cessAmount=0;
    }

    // Parameterized constructor
    public PendingKOT(String ItemName, String Time, int CategCode, int CustId, int DeptCode, int EmployeeId,
                      int ItemNumber, int KitchenCode, int SubUdfNumber, int TableNumber, int TokenNumber, double Amount,
                      double DiscountAmount, double DiscountPercent, double Quantity, double Rate, double TaxAmount, double TaxPercent,
                      int TaxType, double ModifierAmount, double ServiceTaxAmount, double ServiceTaxPercent, int OrderMode,
                      int IsCheckedOut, int TableSplitNo, String hsn,String pos, int PrintKOTStatus) {
        this.strItemName = ItemName;
        this.strTime = Time;
        this.iCategCode = CategCode;
        this.iCustId = CustId;
        this.iDeptCode = DeptCode;
        this.iEmployeeId = EmployeeId;
        this.iItemNumber = ItemNumber;
        this.iKitchenCode = KitchenCode;
        this.iSubUdfNumber = SubUdfNumber;
        this.iTableNumber = TableNumber;
        this.iTokenNumber = TokenNumber;
        this.dAmount = Amount;
        this.dDiscountAmount = DiscountAmount;
        this.dDiscountPercent = DiscountPercent;
        this.dQuantity = Quantity;
        this.dRate = Rate;
        this.fTaxAmount = TaxAmount;
        this.dTaxPercent = TaxPercent;
        this.iTaxType = TaxType;
        this.dModifierAmount = ModifierAmount;
        this.fServiceTaxAmount = ServiceTaxAmount;
        this.dServiceTaxPercent = ServiceTaxPercent;
        this.iOrderMode = OrderMode;
        this.iIsCheckedOut = IsCheckedOut;
        this.iTableSplitNo = TableSplitNo;
        this.HSNCode = hsn;
        this.POS=pos;
        this.iPrintKOTStatus = PrintKOTStatus;
    }

    public double getDblCessAmountPerUnit() {
        return dblCessAmountPerUnit;
    }

    public void setDblCessAmountPerUnit(double dblCessAmountPerUnit) {
        this.dblCessAmountPerUnit = dblCessAmountPerUnit;
    }

    public double getDblTotalAdditionalCessAmount() {
        return dblTotalAdditionalCessAmount;
    }

    public void setDblTotalAdditionalCessAmount(double dblTotalAdditionalCessAmount) {
        this.dblTotalAdditionalCessAmount = dblTotalAdditionalCessAmount;
    }

    public double getDblAdditionalCessAmount() {
        return dblAdditionalCessAmount;
    }

    public void setDblAdditionalCessAmount(double dblAdditionalCessAmount) {
        this.dblAdditionalCessAmount = dblAdditionalCessAmount;
    }

    public double getTaxableValue() {
        return taxableValue;
    }

    public void setTaxableValue(double taxableValue) {
        this.taxableValue = taxableValue;
    }

    public double getOriginalrate() {
        return originalrate;
    }

    public void setOriginalrate(double originalrate) {
        this.originalrate = originalrate;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public double getCessRate() {
        return cessRate;
    }

    public void setCessRate(double cessRate) {
        this.cessRate = cessRate;
    }

    public double getCessAmount() {
        return cessAmount;
    }

    public void setCessAmount(double cessAmount) {
        this.cessAmount = cessAmount;
    }

    public double getIGSTRate() {
        return IGSTRate;
    }

    public void setIGSTRate(double IGSTRate) {
        this.IGSTRate = IGSTRate;
    }

    public double getIGSTAmount() {
        return IGSTAmount;
    }

    public void setIGSTAmount(double IGSTAmount) {
        this.IGSTAmount = IGSTAmount;
    }

    public int getPrintKOTStatus() {
        return iPrintKOTStatus;
    }

    public void setPrintKOTStatus(int PrintKOTStatus) {
        this.iPrintKOTStatus = PrintKOTStatus;
    }

    public String getSupplyType() {
        return SupplyType;
    }

    public void setSupplyType(String supplyType) {
        SupplyType = supplyType;
    }

    public String getPOS() {
        return POS;
    }

    public void setPOS(String POS) {
        this.POS = POS;
    }

    public String getHSNCode() {
        return HSNCode;
    }

    public void setHSNCode(String HSNCode) {
        this.HSNCode = HSNCode;
    }

    // getting ItemNumber
    public int getItemNumber() {
        return this.iItemNumber;
    }

    // getting ItemName
    public String getItemName() {
        return this.strItemName;
    }

    // getting Time
    public String getTime() {
        return this.strTime;
    }

    // getting CategCode
    public int getCategCode() {
        return this.iCategCode;
    }

    // getting CustId
    public int getCusId() {
        return this.iCustId;
    }

    // getting DeptCode
    public int getDeptCode() {
        return this.iDeptCode;
    }

    // getting EmployeeId
    public int getEmployeeId() {
        return this.iEmployeeId;
    }

    // getting KitchenCode
    public int getKitchenCode() {
        return this.iKitchenCode;
    }

    // getting SubUdfNumber
    public int getSubUdfNumber() {
        return this.iSubUdfNumber;
    }

    // getting TableNumber
    public int getTableNumber() {
        return this.iTableNumber;
    }

    // getting TokenNumber
    public int getTokenNumber() {
        return this.iTokenNumber;
    }

    // getting Rate
    public double getRate() {
        return this.dRate;
    }

    // getting Amount
    public double getAmount() {
        return this.dAmount;
    }

    // getting DiscountAmount
    public double getDiscountAmount() {
        return this.dDiscountAmount;
    }

    // getting DiscountPercent
    public double getDiscountPercent() {
        return this.dDiscountPercent;
    }

    // getting Quantity
    public double getQuantity() {
        return this.dQuantity;
    }

    // getting TaxAmount
    public double getTaxAmount() {
        return this.fTaxAmount;
    }

    // getting TaxPercent
    public double getTaxPercent() {
        return this.dTaxPercent;
    }

    // getting TaxType
    public int getTaxType() {
        return this.iTaxType;
    }

    // getting ModifierAmount
    public double getModifierAmount() {
        return this.dModifierAmount;
    }

    // getting ServiceTaxAmount
    public double getServiceTaxAmount() {
        return this.fServiceTaxAmount;
    }

    // getting TaxPercent
    public double getServiceTaxPercent() {
        return this.dServiceTaxPercent;
    }

    // getting OrderMode
    public int getOrderMode() {
        return this.iOrderMode;
    }

    // getting IsCheckedOut
    public int getIsCheckedOut() {
        return this.iIsCheckedOut;
    }

    // getting Table Split No
    public int getTableSplitNo() {
        return this.iTableSplitNo;
    }

    // setting ItemNumber
    public void setItemNumber(int ItemNumber) {
        this.iItemNumber = ItemNumber;
    }

    // setting ItemName
    public void setItemName(String ItemName) {
        this.strItemName = ItemName;
    }

    // setting Time
    public void setTime(String Time) {
        this.strTime = Time;
    }

    // setting CategCode
    public void setCategCode(int CategCode) {
        this.iCategCode = CategCode;
    }

    // setting CustId
    public void setCusId(int CustId) {
        this.iCustId = CustId;
    }

    // setting DeptCode
    public void setDeptCode(int DeptCode) {
        this.iDeptCode = DeptCode;
    }

    // setting EmployeeId
    public void setEmployeeId(int EmployeeId) {
        this.iEmployeeId = EmployeeId;
    }

    // setting KitchenCode
    public void setKitchenCode(int KitchenCode) {
        this.iKitchenCode = KitchenCode;
    }

    // setting SubUdfNumber
    public void setSubUdfNumber(int SubUdfNumber) {
        this.iSubUdfNumber = SubUdfNumber;
    }

    // setting TableNumber
    public void setTableNumber(int TableNumber) {
        this.iTableNumber = TableNumber;
    }

    // setting TokenNumber
    public void setTokenNumber(int TokenNumber) {
        this.iTokenNumber = TokenNumber;
    }

    // setting Rate
    public void setRate(double Rate) {
        this.dRate = Rate;
    }

    // setting Amount
    public void setAmount(double Amount) {
        this.dAmount = Amount;
    }

    // setting DiscountAmount
    public void setDiscountAmount(double DiscountAmount) {
        this.dDiscountAmount = DiscountAmount;
    }

    // setting DiscountPercent
    public void setDiscountPercent(double DiscountPercent) {
        this.dDiscountPercent = DiscountPercent;
    }

    // setting Quantity
    public void setQuantity(double Quantity) {
        this.dQuantity = Quantity;
    }

    // setting TaxAmount
    public void setTaxAmount(double TaxAmount) {
        this.fTaxAmount = TaxAmount;
    }

    // setting TaxPercent
    public void setTaxPercent(double TaxPercent) {
        this.dTaxPercent = TaxPercent;
    }

    // setting TaxType
    public void setTaxType(int TaxType) {
        this.iTaxType = TaxType;
    }

    // setting ModifierAmount
    public void setModifierAmount(double ModifierAmount) {
        this.dModifierAmount = ModifierAmount;
    }

    // setting TaxAmount
    public void setServiceTaxAmount(double ServiceTaxAmount) {
        this.fServiceTaxAmount = ServiceTaxAmount;
    }

    // setting TaxPercent
    public void setServiceTaxPercent(double ServiceTaxPercent) {
        this.dServiceTaxPercent = ServiceTaxPercent;
    }

    // setting OrderMode
    public void setOrderMode(int OrderMode) {
        this.iOrderMode = OrderMode;
    }

    // setting IsCheckedOut
    public void setIsCheckedOut(int IsCheckedOut) {
        this.iIsCheckedOut = IsCheckedOut;
    }

    // setting TableSplitNo
    public void setTableSplitNo(int TableSplitNo) {
        this.iTableSplitNo = TableSplitNo;
    }

}
