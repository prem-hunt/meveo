/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PreInvoicingReportsDTO {

    private String billingCycleCode;
    private Integer billingAccountNumber;
    private Integer billableBillingAccountNumber;
    private BigDecimal amoutWitountTax;
    private Integer checkBANumber;
    private Integer directDebitBANumber;
    private Integer tipBANumber;
    private Integer wiretransferBANumber;

    private Integer checkBillableBANumber;
    private Integer directDebitBillableBANumber;
    private Integer tipBillableBANumber;
    private Integer wiretransferBillableBANumber;

    private BigDecimal checkBillableBAAmountHT;
    private BigDecimal directDebitBillableBAAmountHT;
    private BigDecimal tipBillableBAAmountHT;
    private BigDecimal wiretransferBillableBAAmountHT;
    List<InvoiceSubCategory> InvoiceSubCategories = new ArrayList<InvoiceSubCategory>();

    private BigDecimal SubCategoriesAmountHT = new BigDecimal(0);
    private BigDecimal TaxesAmount = new BigDecimal(0);

    public String getBillingCycleCode() {
        return billingCycleCode;
    }

    public void setBillingCycleCode(String billingCycleCode) {
        this.billingCycleCode = billingCycleCode;
    }

    public Integer getBillingAccountNumber() {
        return billingAccountNumber;
    }

    public void setBillingAccountNumber(Integer billingAccountNumber) {
        this.billingAccountNumber = billingAccountNumber;
    }

    public Integer getBillableBillingAccountNumber() {
        return billableBillingAccountNumber;
    }

    public void setBillableBillingAccountNumber(Integer billableBillingAccountNumber) {
        this.billableBillingAccountNumber = billableBillingAccountNumber;
    }

    public BigDecimal getAmoutWitountTax() {
        return amoutWitountTax;
    }

    public void setAmoutWitountTax(BigDecimal amoutWitountTax) {
        this.amoutWitountTax = amoutWitountTax;
    }

    public Integer getCheckBANumber() {
        return checkBANumber;
    }

    public void setCheckBANumber(Integer checkBANumber) {
        this.checkBANumber = checkBANumber;
    }

    public Integer getDirectDebitBANumber() {
        return directDebitBANumber;
    }

    public void setDirectDebitBANumber(Integer directDebitBANumber) {
        this.directDebitBANumber = directDebitBANumber;
    }

    public Integer getTipBANumber() {
        return tipBANumber;
    }

    public void setTipBANumber(Integer tipBANumber) {
        this.tipBANumber = tipBANumber;
    }

    public Integer getWiretransferBANumber() {
        return wiretransferBANumber;
    }

    public void setWiretransferBANumber(Integer wiretransferBANumber) {
        this.wiretransferBANumber = wiretransferBANumber;
    }

    public Integer getCheckBillableBANumber() {
        return checkBillableBANumber;
    }

    public void setCheckBillableBANumber(Integer checkBillableBANumber) {
        this.checkBillableBANumber = checkBillableBANumber;
    }

    public Integer getDirectDebitBillableBANumber() {
        return directDebitBillableBANumber;
    }

    public void setDirectDebitBillableBANumber(Integer directDebitBillableBANumber) {
        this.directDebitBillableBANumber = directDebitBillableBANumber;
    }

    public Integer getTipBillableBANumber() {
        return tipBillableBANumber;
    }

    public void setTipBillableBANumber(Integer tipBillableBANumber) {
        this.tipBillableBANumber = tipBillableBANumber;
    }

    public Integer getWiretransferBillableBANumber() {
        return wiretransferBillableBANumber;
    }

    public void setWiretransferBillableBANumber(Integer wiretransferBillableBANumber) {
        this.wiretransferBillableBANumber = wiretransferBillableBANumber;
    }

    public List<InvoiceSubCategory> getInvoiceSubCategories() {
        return InvoiceSubCategories;
    }

    public BigDecimal getCheckBillableBAAmountHT() {
        return checkBillableBAAmountHT;
    }

    public void setCheckBillableBAAmountHT(BigDecimal checkBillableBAAmountHT) {
        this.checkBillableBAAmountHT = checkBillableBAAmountHT;
    }

    public BigDecimal getDirectDebitBillableBAAmountHT() {
        return directDebitBillableBAAmountHT;
    }

    public void setDirectDebitBillableBAAmountHT(BigDecimal directDebitBillableBAAmountHT) {
        this.directDebitBillableBAAmountHT = directDebitBillableBAAmountHT;
    }

    public BigDecimal getTipBillableBAAmountHT() {
        return tipBillableBAAmountHT;
    }

    public void setTipBillableBAAmountHT(BigDecimal tipBillableBAAmountHT) {
        this.tipBillableBAAmountHT = tipBillableBAAmountHT;
    }

    public BigDecimal getWiretransferBillableBAAmountHT() {
        return wiretransferBillableBAAmountHT;
    }

    public void setWiretransferBillableBAAmountHT(BigDecimal wiretransferBillableBAAmountHT) {
        this.wiretransferBillableBAAmountHT = wiretransferBillableBAAmountHT;
    }

    public void setInvoiceSubCategories(List<InvoiceSubCategory> invoiceSubCategories) {
        InvoiceSubCategories = invoiceSubCategories;
    }

    public BigDecimal getSubCategoriesAmountHT() {
        return SubCategoriesAmountHT;
    }

    public void setSubCategoriesAmountHT(BigDecimal subCategoriesAmountHT) {
        SubCategoriesAmountHT = subCategoriesAmountHT;
    }

    public BigDecimal getTaxesAmount() {
        return TaxesAmount;
    }

    public void setTaxesAmount(BigDecimal taxesAmount) {
        TaxesAmount = taxesAmount;
    }

}
