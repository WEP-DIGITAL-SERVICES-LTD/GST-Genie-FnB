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
	double   dblDeliveryCharge, dblTotalDiscountPercentage,dblTotalDiscountAmount;
	double dblPettyCashPayment,dblCardPayment, dblCashPayment, dblCouponPayment, fChangePayment, dblWalletAmount, IGSTAmount, dblBillAmount;
	double dBillAmount, CGSTAmount, SGSTAmount, cessAmount,  dblTotalTaxAmount, dblTotalServiceTaxAmount;
	double dblRoundOff;
	double dPettyCashPayment, dblPaidTotalPayment,dChangePayment;
	double SubTotal;
	double dblAEPSAmount;
	double dblRewardPoints =0;
	double dblMSwipeAmount = 0;
	double dblPaytmAmount = 0;

	String strOnlineOrderNo;

	// Default Constructor
	public BillDetail(){
		dblAEPSAmount = 0;
		dblRewardPoints = 0;
		dblMSwipeAmount = 0;
		dblPaytmAmount = 0;
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
		this.dblBillAmount = 0;
		this.dBillAmount = 0;
		this.dblCardPayment = 0;
		this.dblRoundOff = 0;
		this.dblCashPayment = 0;
		this.dblCouponPayment = 0;
		this.dblDeliveryCharge = 0;
		this.dblTotalDiscountAmount = 0;
		this.dblTotalDiscountPercentage = 0;
		this.dblTotalTaxAmount = 0;
		this.dblTotalServiceTaxAmount = 0;
		this.dblWalletAmount = 0;

		this.dblPettyCashPayment = 0;
		this.dPettyCashPayment = 0;
		this.dblPaidTotalPayment = 0;
		this.dChangePayment = 0;
		this.cessAmount =0;
		this.strOnlineOrderNo = "";

	}



	// Parameterized Constructor


	public BillDetail(String custname, String custStateCode, String POS,
					  String strDate, String strTime, String strUserId,
					  String businessType, String amount, String GSTIN,
					  int iBillNumber, int iBillStatus, int iCustId, int iEmployeeId,
					  int iReprintCount, int iTotalItems, int iUserId, String billingMode,
					  String tableNo, String tableSplitNo, double dblBillAmount, double dblCardPayment,
					  double dblCashPayment, double dblCouponPayment, float dblPettyCashPayment,
					  double dblPaidTotalPayment, double dblChangePayment, double dblWalletAmount,
					  double dblAEPSAmount, double dblRewardPoints, double dblMSwipeAmount, double dblPaytmAmount,
					  double dblDeliveryCharge, double dblTotalDiscountPercentage, double dblTotalDiscountAmount,
					  double dblTotalTaxAmount, double dblTotalServiceTaxAmount, double IGSTAmount,
					  double CGSTAmount, double SGSTAmount, double cessAmount, double dBillAmount,
					  double dblRoundOff, double dPettyCashPayment, double subTotal) {
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
		this.dblBillAmount = dblBillAmount;
		this.dblCardPayment = dblCardPayment;
		this.dblCashPayment = dblCashPayment;
		this.dblCouponPayment = dblCouponPayment;
		this.dblPettyCashPayment = dblPettyCashPayment;
		this.dblPaidTotalPayment = dblPaidTotalPayment;
		this.dChangePayment = dblChangePayment;
		this.dblWalletAmount = dblWalletAmount;
		this.dblDeliveryCharge = dblDeliveryCharge;
		this.dblTotalDiscountPercentage = dblTotalDiscountPercentage;
		this.dblTotalDiscountAmount = dblTotalDiscountAmount;
		this.dblTotalTaxAmount = dblTotalTaxAmount;
		this.dblTotalServiceTaxAmount = dblTotalServiceTaxAmount;
		this.IGSTAmount = IGSTAmount;
		this.CGSTAmount = CGSTAmount;
		this.SGSTAmount = SGSTAmount;
		this.cessAmount = cessAmount;
		this.dBillAmount = dBillAmount;
		this.dblRoundOff = dblRoundOff;
		this.dPettyCashPayment = dPettyCashPayment;
		SubTotal = subTotal;
	}

	public double getDblWalletAmount() {
		return dblWalletAmount;
	}

	public void setDblWalletAmount(double dblWalletAmount) {
		this.dblWalletAmount = dblWalletAmount;
	}

	public double getDblAEPSAmount() {
		return dblAEPSAmount;
	}

	public void setDblAEPSAmount(double dblAEPSAmount) {
		this.dblAEPSAmount = dblAEPSAmount;
	}

	public double getDblRewardPoints() {
		return dblRewardPoints;
	}

	public void setDblRewardPoints(double dblRewardPoints) {
		this.dblRewardPoints = dblRewardPoints;
	}

	public double getDblMSwipeAmount() {
		return dblMSwipeAmount;
	}

	public void setDblMSwipeAmount(double dblMSwipeAmount) {
		this.dblMSwipeAmount = dblMSwipeAmount;
	}

	public double getDblPaytmAmount() {
		return dblPaytmAmount;
	}

	public void setDblPaytmAmount(double dblPaytmAmount) {
		this.dblPaytmAmount = dblPaytmAmount;
	}

	public double getdBillAmount() {
		return dBillAmount;
	}

	public void setdBillAmount(double dBillAmount) {
		this.dBillAmount = dBillAmount;
	}

	public double getfRoundOff() {
		return dblRoundOff;
	}

	public void setfRoundOff(double fRoundOff) {
		this.dblRoundOff = fRoundOff;
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

	public double getTotalDiscountPercentage() {
		return dblTotalDiscountPercentage;
	}

	public void setTotalDiscountPercentage(double fTotalDiscountPercentage) {
		this.dblTotalDiscountPercentage = dblTotalDiscountPercentage;
	}

	public double getWalletAmount() {
		return dblWalletAmount;
	}

	public void setWalletAmount(double fWalletAmount) {
		this.dblWalletAmount = fWalletAmount;
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

	public double getIGSTAmount() {
		return IGSTAmount;
	}

	public void setIGSTAmount(double IGSTAmount) {
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
	public double getBillAmount(){
		return this.dblBillAmount;
	}

	// getting CardPayment
	public double getCardPayment(){
		return this.dblCardPayment;
	}

	// getting CashPayment
	public double getCashPayment(){
		return this.dblCashPayment;
	}

	// getting CouponPayment
	public double getCouponPayment(){
		return this.dblCouponPayment;
	}

	// getting DeliveryCharge
	public double getDeliveryCharge(){
		return this.dblDeliveryCharge;
	}

	// getting TotalDiscountAmount
	public double getTotalDiscountAmount(){
		return this.dblTotalDiscountAmount;
	}

	// getting TotalTaxAmount
	public double getTotalTaxAmount(){
		return this.dblTotalTaxAmount;
	}

	// getting TotalServiceTaxAmount
	public double getTotalServiceTaxAmount(){
		return this.dblTotalServiceTaxAmount;
	}

	// getting PettyCashPayment
	public double getPettyCashPayment(){
		return this.dblPettyCashPayment;
	}

	// getting PaidTotalPayment
	public double getPaidTotalPayment(){
		return this.dblPaidTotalPayment;
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
	public void setBillAmount(double BillAmount){
		this.dblBillAmount = BillAmount;
	}

	// setting CardPayment
	public void setCardPayment(double CardPayment){
		this.dblCardPayment = CardPayment;
	}

	// setting CashPayment
	public void setCashPayment(double CashPayment){
		this.dblCashPayment = CashPayment;
	}

	// setting CouponPayment
	public void setCouponPayment(double CouponPayment){
		this.dblCouponPayment = CouponPayment;
	}

	// setting DeliveryCharge
	public void setDeliveryCharge(double DeliveryCharge){
		this.dblDeliveryCharge = DeliveryCharge;
	}

	// setting TotalDiscountAmount
	public void setTotalDiscountAmount(double TotalDiscountAmount){
		this.dblTotalDiscountAmount = TotalDiscountAmount;
	}

	// setting TotalTaxAmount
	public void setTotalTaxAmount(double TotalTaxAmount){
		this.dblTotalTaxAmount = TotalTaxAmount;
	}

	// setting TotalServiceTaxAmount
	public void setTotalServiceTaxAmount(double TotalServiceTaxAmount){
		this.dblTotalServiceTaxAmount = TotalServiceTaxAmount;
	}

	// setting PettyCashPayment
	public void setPettyCashPayment(double PettyCashPayment){
		this.dblPettyCashPayment = PettyCashPayment;
	}

	// setting PaidTotalPayment
	public void setPaidTotalPayment(double PaidTotalPayment){
		this.dblPaidTotalPayment = PaidTotalPayment;
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

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

	public String getStrOnlineOrderNo() {
		return strOnlineOrderNo;
	}

	public void setStrOnlineOrderNo(String strOnlineOrderNo) {
		this.strOnlineOrderNo = strOnlineOrderNo;
	}
}
