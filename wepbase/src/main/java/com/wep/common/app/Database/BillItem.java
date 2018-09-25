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

package com.wep.common.app.Database;

public class BillItem {

	// Private variables
	String CustName, CustStateCode,Uom,strItemName, HSNCode, BusinessType, iBillNumber,  SupplyType,
			SupplierPhone, SupplierName, SupplierAddress,SupplierGSTIN,
			GSTIN;
	int  iItemNumber, iDeptCode, iCategCode, iKitchenCode, iTaxType;
	double dDiscountAmount, dDiscountPercent, dQuantity, dvalue, dTaxAmount, dTaxPercent, dServiceTaxPercent, dServiceTaxAmount, dModifierAmount,
			IGSTRate, CGSTRate, SGSTRate,dblCessAmountPerUnit,dblAdditionalCessAmount,dblTotalAdditionalCessAmount;
	double SGSTAmount, CGSTAmount, IGSTAmount;
	String TaxationType,SupplierType ;
	String InvoiceDate;
	String BillingMode; // richa_2012
	int isGoodInwarded, billStatus;
	int Suppliercode, PurchaseOrderNo;
	String additionalChargeName, isReverTaxEnabled;
	float additionalChargeAmount, cessRate;

	double TaxableValue, originalRate;
	double cessAmount,fAmount, SubTotal;

	String strOnlineOrderNo;

	// Default constructor
	public BillItem(){
		this.Suppliercode=-1;
		this.PurchaseOrderNo=0;
		this.originalRate = 0.00;
		this.SupplierAddress = "";
		this.additionalChargeAmount = 0;
		this.additionalChargeName = "";
		this.Uom="";
		this.isReverTaxEnabled="";
		this.CustStateCode="";
		this.CustName="";
		this.SupplierName="";
		this.GSTIN ="";
		this.SupplierPhone="";
		this.TaxationType="";
		this.SubTotal = 0f;
		this.TaxableValue = 0f;
		this.strItemName = "";
		this.SupplyType = "";
		this.iCategCode = 0;
		this.iDeptCode = 0;
		this.iItemNumber = 0;
		this.iKitchenCode = 0;
		this.iBillNumber = "";
		this.iTaxType = 0;
		this.fAmount = 0;
		this.dDiscountAmount = 0;
		this.dDiscountPercent = 0;
		this.dQuantity = 0;
		this.dvalue = 0;
		this.dTaxAmount = 0;
		this.dTaxPercent = 0;
		this.dServiceTaxAmount = 0;
		this.dServiceTaxPercent = 0;
		this.dModifierAmount = 0;
		this.BusinessType= "";
		this.SupplierGSTIN= "";
		this.InvoiceDate= "";
		this.HSNCode="";
		this.IGSTAmount=0;
		this.IGSTRate= 0;
		this.CGSTRate=0;
		this.CGSTAmount=0;
		this.SGSTRate=0;
		this.SGSTAmount=0;
		this.cessRate=0;
		this.cessAmount=0;
		this.isGoodInwarded=0;
		this.billStatus=0;
		this.SupplierType="";
		this.strOnlineOrderNo = "";
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

	public double getDblCessAmountPerUnit() {
		return dblCessAmountPerUnit;
	}

	public void setDblCessAmountPerUnit(double dblCessAmountPerUnit) {
		this.dblCessAmountPerUnit = dblCessAmountPerUnit;
	}

	public double getOriginalRate() {
		return originalRate;
	}

	public void setOriginalRate(double originalRate) {
		this.originalRate = originalRate;
	}

	public String getIsReverTaxEnabled() {
		return isReverTaxEnabled;
	}

	public void setIsReverTaxEnabled(String isReverTaxEnabled) {
		this.isReverTaxEnabled = isReverTaxEnabled;
	}

	public int getBillStatus() {
		return billStatus;
	}

	public void setBillStatus(int billStatus) {
		this.billStatus = billStatus;
	}

	public float getCessRate() {
		return cessRate;
	}

	public void setCessRate(float cessRate) {
		this.cessRate = cessRate;
	}

	public double getCessAmount() {
		return cessAmount;
	}

	public void setCessAmount(double cessAmount) {
		this.cessAmount = cessAmount;
	}

	public String getSupplierGSTIN() {
		return SupplierGSTIN;
	}

	public void setSupplierGSTIN(String supplierGSTIN) {
		SupplierGSTIN = supplierGSTIN;
	}

	public int getIsGoodInwarded() {
		return isGoodInwarded;
	}

	public void setIsGoodInwarded(int isGoodInwarded) {
		this.isGoodInwarded = isGoodInwarded;
	}

	public int getPurchaseOrderNo() {
		return PurchaseOrderNo;
	}

	public void setPurchaseOrderNo(int purchaseOrderNo) {
		PurchaseOrderNo = purchaseOrderNo;
	}

	public float getAdditionalChargeAmount() {
		return additionalChargeAmount;
	}

	public void setAdditionalChargeAmount(float additionalChargeAmount) {
		this.additionalChargeAmount = additionalChargeAmount;
	}

	public String getAdditionalChargeName() {
		return additionalChargeName;
	}

	public void setAdditionalChargeName(String additionalChargeName) {
		this.additionalChargeName = additionalChargeName;
	}

	public String getSupplierAddress() {
		return SupplierAddress;
	}

	public void setSupplierAddress(String supplierAddress) {
		SupplierAddress = supplierAddress;
	}

	public int getSuppliercode() {
		return Suppliercode;
	}

	public void setSuppliercode(int suppliercode) {
		Suppliercode = suppliercode;
	}

	public String getCustName() {
		return CustName;
	}

	public void setCustName(String custName) {
		CustName = custName;
	}

	public String getCustStateCode() {
		return CustStateCode;
	}

	public void setCustStateCode(String custStateCode) {
		CustStateCode = custStateCode;
	}

	public String getSupplierType() {
		return SupplierType;
	}

	public void setSupplierType(String supplierType) {
		SupplierType = supplierType;
	}

	// Parameterized constructor
	public BillItem(String ItemName,int CategCode,int DeptCode,int ItemNumber,
					int KitchenCode,String BillNumber,int TaxType,float Amount,float DiscountAmount,
					float DiscountPercent,float Quantity,float Value,float TaxAmount,float TaxPercent,
					float ServiceTaxAmount,float ServiceTaxPercent,float ModifierAmount,
					String BusinessType, String InvoiceDate, String hsn, String TaxationType){

		this.TaxationType=TaxationType;
		this.HSNCode = hsn;
		this.strItemName = ItemName;
		this.iCategCode = CategCode;
		this.iDeptCode = DeptCode;
		this.iItemNumber = ItemNumber;
		this.iKitchenCode = KitchenCode;
		this.iBillNumber = BillNumber;
		this.iTaxType = TaxType;
		this.fAmount = Amount;
		this.dDiscountAmount = DiscountAmount;
		this.dDiscountPercent = DiscountPercent;

		this.dQuantity = Quantity;
		this.dvalue = Value;
		this.dTaxAmount = TaxAmount;
		this.dTaxPercent = TaxPercent;
		this.dServiceTaxAmount = ServiceTaxAmount;
		this.dServiceTaxPercent = ServiceTaxPercent;
		this.dModifierAmount = ModifierAmount;
		this.BusinessType = BusinessType;
		this.InvoiceDate = InvoiceDate;

	}

	//richa_2012 starts

	public String getBillingMode() {
		return BillingMode;
	}

	public void setBillingMode(String billingMode) {
		BillingMode = billingMode;
	}


	//richa_2012 ends

	public String getSupplierName() {
		return SupplierName;
	}

	public void setSupplierName(String supplierName) {
		SupplierName = supplierName;
	}

	public String getGSTIN() {
		return GSTIN;
	}

	public void setGSTIN(String GSTIN) {
		this.GSTIN = GSTIN;
	}

	public String getSupplierPhone() {
		return SupplierPhone;
	}

	public void setSupplierPhone(String supplierPhone) {
		SupplierPhone = supplierPhone;
	}

	public String getTaxationType() {
		return TaxationType;
	}

	public void setTaxationType(String taxationType) {
		TaxationType = taxationType;
	}

	public double getSubTotal() {
		return SubTotal;
	}

	public void setSubTotal(double subTotal) {
		SubTotal = subTotal;
	}

	public double getTaxableValue() {
		return TaxableValue;
	}

	public void setTaxableValue(double taxableValue) {
		TaxableValue = taxableValue;
	}

	public String getUom() {
		return Uom;
	}

	public void setUom(String uom) {
		Uom = uom;
	}

	public String getSupplyType() {
		return SupplyType;
	}

	public void setSupplyType(String supplyType) {
		SupplyType = supplyType;
	}

	public String getHSNCode() {
		return HSNCode;
	}

	public void setHSNCode(String HSNCode) {
		this.HSNCode = HSNCode;
	}

	public String getInvoiceDate() {
		return InvoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		InvoiceDate = invoiceDate;
	}

	public String getBusinessType() {
		return BusinessType;
	}

	public void setBusinessType(String businessType) {
		BusinessType = businessType;
	}

	// getting ItemName
	public String getItemName(){
		return this.strItemName;
	}

	// getting BillNumber
	public String getBillNumber(){
		return this.iBillNumber;
	}

	// getting CategCode
	public int getCategCode(){
		return this.iCategCode;
	}

	public double getIGSTRate() {
		return IGSTRate;
	}

	public void setIGSTRate(float IGSTRate) {
		this.IGSTRate = IGSTRate;
	}

	public double getIGSTAmount() {
		return IGSTAmount;
	}

	public void setIGSTAmount(double IGSTAmount) {
		this.IGSTAmount = IGSTAmount;
	}

	public double getCGSTRate() {
		return CGSTRate;
	}

	public void setCGSTRate(float CGSTRate) {
		this.CGSTRate = CGSTRate;
	}

	public double getCGSTAmount() {
		return CGSTAmount;
	}

	public void setCGSTAmount(double CGSTAmount) {
		this.CGSTAmount = CGSTAmount;
	}

	public double getSGSTRate() {
		return SGSTRate;
	}

	public void setSGSTRate(float SGSTRate) {
		this.SGSTRate = SGSTRate;
	}

	public double getSGSTAmount() {
		return SGSTAmount;
	}

	public void setSGSTAmount(double SGSTAmount) {
		this.SGSTAmount = SGSTAmount;
	}

	// getting DeptCode
	public int getDeptCode(){
		return this.iDeptCode;
	}

	// getting ItemNumber
	public int getItemNumber(){
		return this.iItemNumber;
	}

	// getting KitchenCode
	public int getKitchenCode(){
		return this.iKitchenCode;
	}

	// getting TaxType
	public int getTaxType(){
		return this.iTaxType;
	}

	// getting Amount
	public double getAmount(){
		return this.fAmount;
	}

	// getting DiscountAmount
	public double getDiscountAmount(){
		return this.dDiscountAmount;
	}

	// getting DiscountPercent
	public double getDiscountPercent(){
		return this.dDiscountPercent;
	}

	// getting Quantity
	public double getQuantity(){
		return this.dQuantity;
	}

	// getting Value
	public double getValue(){
		return this.dvalue;
	}

	// getting TaxAmount
	public double getTaxAmount(){
		return this.dTaxAmount;
	}

	// getting TaxPercent
	public double getTaxPercent(){
		return this.dTaxPercent;
	}

	// getting ServiceTaxAmount
	public double getServiceTaxAmount(){
		return this.dServiceTaxAmount;
	}

	// getting ServiceTaxPercent
	public double getServiceTaxPercent(){
		return this.dServiceTaxPercent;
	}

	// getting ModifierAmount
	public double getModifierAmount(){
		return this.dModifierAmount;
	}

	// setting ItemName
	public void setItemName(String ItemName){
		this.strItemName = ItemName;
	}

	// setting BillNumber
	public void setBillNumber(String BillNumber){
		this.iBillNumber = BillNumber;
	}

	// setting CategCode
	public void setCategCode(int CategCode){
		this.iCategCode = CategCode;
	}

	// setting DeptCode
	public void setDeptCode(int DeptCode){
		this.iDeptCode = DeptCode;
	}

	// setting ItemNumber
	public void setItemNumber(int ItemNumber){
		this.iItemNumber = ItemNumber;
	}

	// setting KitchenCode
	public void setKitchenCode(int KitchenCode){
		this.iKitchenCode = KitchenCode;
	}

	// setting TaxType
	public void setTaxType(int TaxType){
		this.iTaxType = TaxType;
	}

	// setting Amount
	public void setAmount(double Amount){
		this.fAmount = Amount;
	}

	// setting DiscountAmount
	public void setDiscountAmount(double DiscountAmount){
		this.dDiscountAmount = DiscountAmount;
	}

	// setting DiscountPercent
	public void setDiscountPercent(double DiscountPercent){
		this.dDiscountPercent = DiscountPercent;
	}

	// setting Quantity
	public void setQuantity(double Quantity){
		this.dQuantity = Quantity;
	}

	// setting Value
	public void setValue(double Value){
		this.dvalue = Value;
	}

	// setting TaxAmount
	public void setTaxAmount(double TaxAmount){
		this.dTaxAmount = TaxAmount;
	}

	// setting TaxPercent
	public void setTaxPercent(double TaxPercent){
		this.dTaxPercent = TaxPercent;
	}

	// setting ServiceTaxAmount
	public void setServiceTaxAmount(double ServiceTaxAmount){
		this.dServiceTaxAmount = ServiceTaxAmount;
	}

	// setting ServiceTaxPercent
	public void setServiceTaxPercent(double ServiceTaxPercent){
		this.dServiceTaxPercent = ServiceTaxPercent;
	}

	// setting ModifierAmount
	public void setModifierAmount(double ModifierAmount){
		this.dModifierAmount = ModifierAmount;
	}

	public String getStrOnlineOrderNo() {
		return strOnlineOrderNo;
	}

	public void setStrOnlineOrderNo(String strOnlineOrderNo) {
		this.strOnlineOrderNo = strOnlineOrderNo;
	}
}
