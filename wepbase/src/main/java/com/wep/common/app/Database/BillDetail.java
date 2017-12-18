/*
 * **************************************************************************
 * Project Name		:	VAJRA
 * 
 * File Name		:	BillDetail
 * 
 * Purpose			:	Represents BillDetail table, takes care of intializing
 * 						assining and returning values of all the variables.
 * 
 * DateOfCreation	:	16-October-2012
 * 
 * Author			:	Balasubramanya Bharadwaj B S
 * 
 * **************************************************************************
 */

package com.wep.common.app.Database;

public class BillDetail {

	// Private variables
	String Custname, CustStateCode, POS,strDate, strTime, strUserId,BusinessType, Amount, GSTIN;
	int iBillNumber, iBillStatus, iCustId, iEmployeeId, iReprintCount, iTotalItems, iUserId;
	String BillingMode, TableNo, TableSplitNo; // richa_2012
	float fBillAmount, fCardPayment, fCashPayment, fCouponPayment, fPettyCashPayment, fPaidTotalPayment,  fWalletAmount,
			fDeliveryCharge, fTotalDiscountPercentage,fTotalDiscountAmount,IGSTAmount;
	double dBillAmount, CGSTAmount, SGSTAmount, cessAmount,  fTotalTaxAmount, fTotalServiceTaxAmount;
	float fRoundOff;
	double dPettyCashPayment, dChangePayment;
	double SubTotal;

	// Default Constructor
	public BillDetail(){
		this.GSTIN="";
		this.Custname="";
		this.BillingMode  = ""; // richa_2012
		this.TableNo  = ""; // richa_2012
		this.TableSplitNo  = ""; // richa_2012
		this.CustStateCode="";
		this.POS = "";
		this.Amount="";
		this.CGSTAmount=0;
		this.IGSTAmount=0;
		this.SGSTAmount=0;
		this.SubTotal=0;
		this.BusinessType="";
		this.strDate = "";
		this.strTime = "";
		this.iBillNumber = 0;
		this.iBillStatus = 0;
		this.iCustId = 0;
		this.iEmployeeId = 0;
		this.iReprintCount = 0;
		this.iTotalItems = 0;
		this.strUserId = "";
		this.iUserId = 0;
		this.fBillAmount = 0;
		this.dBillAmount = 0;
		this.fCardPayment = 0;
		this.fRoundOff = 0;
		this.fCashPayment = 0;
		this.fCouponPayment = 0;
		this.fDeliveryCharge = 0;
		this.fTotalDiscountAmount = 0;
		this.fTotalDiscountPercentage = 0;
		this.fTotalTaxAmount = 0;
		this.fTotalServiceTaxAmount = 0;
		this.fWalletAmount = 0;

		this.fPettyCashPayment = 0;
		this.dPettyCashPayment = 0;
		this.fPaidTotalPayment = 0;
		this.dChangePayment = 0;
		this.cessAmount =0;


	}



	// Parameterized Constructor


	public BillDetail(String custname, String custStateCode, String POS,
					  String strDate, String strTime, String strUserId,
					  String businessType, String amount, String GSTIN,
					  int iBillNumber, int iBillStatus, int iCustId, int iEmployeeId,
					  int iReprintCount, int iTotalItems, int iUserId, String billingMode,
					  String tableNo, String tableSplitNo, float fBillAmount, float fCardPayment,
					  float fCashPayment, float fCouponPayment, float fPettyCashPayment,
					  float fPaidTotalPayment, double fChangePayment, float fWalletAmount,
					  float fDeliveryCharge, float fTotalDiscountPercentage, float fTotalDiscountAmount,
					  float fTotalTaxAmount, float fTotalServiceTaxAmount, float IGSTAmount,
					  double CGSTAmount, double SGSTAmount, double cessAmount, double dBillAmount,
					  float fRoundOff, double dPettyCashPayment, double subTotal) {
		Custname = custname;
		CustStateCode = custStateCode;
		this.POS = POS;
		this.strDate = strDate;
		this.strTime = strTime;
		this.strUserId = strUserId;
		BusinessType = businessType;
		Amount = amount;
		this.GSTIN = GSTIN;
		this.iBillNumber = iBillNumber;
		this.iBillStatus = iBillStatus;
		this.iCustId = iCustId;
		this.iEmployeeId = iEmployeeId;
		this.iReprintCount = iReprintCount;
		this.iTotalItems = iTotalItems;
		this.iUserId = iUserId;
		BillingMode = billingMode;
		TableNo = tableNo;
		TableSplitNo = tableSplitNo;
		this.fBillAmount = fBillAmount;
		this.fCardPayment = fCardPayment;
		this.fCashPayment = fCashPayment;
		this.fCouponPayment = fCouponPayment;
		this.fPettyCashPayment = fPettyCashPayment;
		this.fPaidTotalPayment = fPaidTotalPayment;
		this.dChangePayment = fChangePayment;
		this.fWalletAmount = fWalletAmount;
		this.fDeliveryCharge = fDeliveryCharge;
		this.fTotalDiscountPercentage = fTotalDiscountPercentage;
		this.fTotalDiscountAmount = fTotalDiscountAmount;
		this.fTotalTaxAmount = fTotalTaxAmount;
		this.fTotalServiceTaxAmount = fTotalServiceTaxAmount;
		this.IGSTAmount = IGSTAmount;
		this.CGSTAmount = CGSTAmount;
		this.SGSTAmount = SGSTAmount;
		this.cessAmount = cessAmount;
		this.dBillAmount = dBillAmount;
		this.fRoundOff = fRoundOff;
		this.dPettyCashPayment = dPettyCashPayment;
		SubTotal = subTotal;
	}

	public double getdBillAmount() {
		return dBillAmount;
	}

	public void setdBillAmount(double dBillAmount) {
		this.dBillAmount = dBillAmount;
	}

	public float getfRoundOff() {
		return fRoundOff;
	}

	public void setfRoundOff(float fRoundOff) {
		this.fRoundOff = fRoundOff;
	}

	public double getCessAmount() {
		return cessAmount;
	}

	public void setCessAmount(double cessAmount) {
		this.cessAmount = cessAmount;
	}

	public String getGSTIN() {
		return GSTIN;
	}

	public void setGSTIN(String GSTIN) {
		this.GSTIN = GSTIN;
	}

	public String getTableNo() {
		return TableNo;
	}

	public void setTableNo(String tableNo) {
		TableNo = tableNo;
	}

	public String getTableSplitNo() {
		return TableSplitNo;
	}

	public void setTableSplitNo(String tableSplitNo) {
		TableSplitNo = tableSplitNo;
	}

	public float getTotalDiscountPercentage() {
		return fTotalDiscountPercentage;
	}

	public void setTotalDiscountPercentage(float fTotalDiscountPercentage) {
		this.fTotalDiscountPercentage = fTotalDiscountPercentage;
	}

	public float getWalletAmount() {
		return fWalletAmount;
	}

	public void setWalletAmount(float fWalletAmount) {
		this.fWalletAmount = fWalletAmount;
	}

	// richa_2012 starts

	public String getBillingMode() {
		return BillingMode;
	}

	public void setBillingMode(String billingMode) {
		BillingMode = billingMode;
	}


	// richa_2012 ends

	public String getCustname() {
		return Custname;
	}

	public void setCustname(String custname) {
		Custname = custname;
	}

	public String getCustStateCode() {
		return CustStateCode;
	}

	public void setCustStateCode(String custStateCode) {
		CustStateCode = custStateCode;
	}

	public double getSubTotal() {
		return SubTotal;
	}

	public void setSubTotal(double subTotal) {
		SubTotal = subTotal;
	}

	public float getIGSTAmount() {
		return IGSTAmount;
	}

	public void setIGSTAmount(float IGSTAmount) {
		this.IGSTAmount = IGSTAmount;
	}

	public double getCGSTAmount() {
		return CGSTAmount;
	}

	public void setCGSTAmount(double CGSTAmount) {
		this.CGSTAmount = CGSTAmount;
	}

	public double getSGSTAmount() {
		return SGSTAmount;
	}

	public void setSGSTAmount(double SGSTAmount) {
		this.SGSTAmount = SGSTAmount;
	}

	public String getAmount() {
		return Amount;
	}

	public void setAmount(String amount) {
		Amount = amount;
	}

	public String getBusinessType() {
		return BusinessType;
	}

	public void setBusinessType(String businessType) {
		BusinessType = businessType;
	}

	public String getPOS() {
		return POS;
	}

	public void setPOS(String POS) {
		this.POS = POS;
	}

	// getting Date
	public String getDate(){
		return this.strDate;
	}

	// getting Time
	public String getTime(){
		return this.strTime;
	}

	// getting BillNumber
	public int getBillNumber(){
		return this.iBillNumber;
	}

	// getting BillStatus
	public int getBillStatus(){
		return this.iBillStatus;
	}

	// getting CustId
	public int getCustId(){
		return this.iCustId;
	}

	// getting EmployeeId
	public int getEmployeeId(){
		return this.iEmployeeId;
	}

	// getting ReprintCount
	public int getReprintCount(){
		return this.iReprintCount;
	}

	// getting TotalItems
	public int getTotalItems(){
		return this.iTotalItems;
	}

	// getting UserId
	public String getUserId(){
		return this.strUserId;
	}

	// getting BillAmount
	public float getBillAmount(){
		return this.fBillAmount;
	}

	// getting CardPayment
	public float getCardPayment(){
		return this.fCardPayment;
	}

	// getting CashPayment
	public float getCashPayment(){
		return this.fCashPayment;
	}

	// getting CouponPayment
	public float getCouponPayment(){
		return this.fCouponPayment;
	}

	// getting DeliveryCharge
	public float getDeliveryCharge(){
		return this.fDeliveryCharge;
	}

	// getting TotalDiscountAmount
	public float getTotalDiscountAmount(){
		return this.fTotalDiscountAmount;
	}

	// getting TotalTaxAmount
	public double getTotalTaxAmount(){
		return this.fTotalTaxAmount;
	}

	// getting TotalServiceTaxAmount
	public double getTotalServiceTaxAmount(){
		return this.fTotalServiceTaxAmount;
	}

	// getting PettyCashPayment
	public float getPettyCashPayment(){
		return this.fPettyCashPayment;
	}

	// getting PaidTotalPayment
	public float getPaidTotalPayment(){
		return this.fPaidTotalPayment;
	}

	// getting ChangePayment
	public double getChangePayment(){
		return this.dChangePayment;
	}


	// setting Date
	public void setDate(String Date){
		this.strDate = Date;
	}

	// setting Time
	public void setTime(String Time){
		this.strTime = Time;
	}

	// setting BillNumber
	public void setBillNumber(int BillNumber){
		this.iBillNumber = BillNumber;
	}

	// setting BillStatus
	public void setBillStatus(int BillStatus){
		this.iBillStatus = BillStatus;
	}

	// setting CustId
	public void setCustId(int CustId){
		this.iCustId = CustId;
	}

	// setting EmployeeId
	public void setEmployeeId(int EmployeeId){
		this.iEmployeeId = EmployeeId;
	}

	// setting ReprintCount
	public void setReprintCount(int ReprintCount){
		this.iReprintCount = ReprintCount;
	}

	// setting TotalItems
	public void setTotalItems(int TotalItems){
		this.iTotalItems = TotalItems;
	}

	// setting UserId
	public void setUserId(String UserId){
		this.strUserId = UserId;
	}

	// setting BillAmount
	public void setBillAmount(float BillAmount){
		this.fBillAmount = BillAmount;
	}

	// setting CardPayment
	public void setCardPayment(float CardPayment){
		this.fCardPayment = CardPayment;
	}

	// setting CashPayment
	public void setCashPayment(float CashPayment){
		this.fCashPayment = CashPayment;
	}

	// setting CouponPayment
	public void setCouponPayment(float CouponPayment){
		this.fCouponPayment = CouponPayment;
	}

	// setting DeliveryCharge
	public void setDeliveryCharge(float DeliveryCharge){
		this.fDeliveryCharge = DeliveryCharge;
	}

	// setting TotalDiscountAmount
	public void setTotalDiscountAmount(float TotalDiscountAmount){
		this.fTotalDiscountAmount = TotalDiscountAmount;
	}

	// setting TotalTaxAmount
	public void setTotalTaxAmount(double TotalTaxAmount){
		this.fTotalTaxAmount = TotalTaxAmount;
	}

	// setting TotalServiceTaxAmount
	public void setTotalServiceTaxAmount(float TotalServiceTaxAmount){
		this.fTotalServiceTaxAmount = TotalServiceTaxAmount;
	}

	// setting PettyCashPayment
	public void setPettyCashPayment(float PettyCashPayment){
		this.fPettyCashPayment = PettyCashPayment;
	}

	// setting PaidTotalPayment
	public void setPaidTotalPayment(float PaidTotalPayment){
		this.fPaidTotalPayment = PaidTotalPayment;
	}

	// setting ChangePayment
	public void setChangePayment(double ChangePayment){
		this.dChangePayment = ChangePayment;
	}

	public double getdPettyCashPayment() {
		return dPettyCashPayment;
	}

	public void setdPettyCashPayment(double dPettyCashPayment) {
		this.dPettyCashPayment = dPettyCashPayment;
	}
}
