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
package org.meveo.admin.action.crm;

import java.io.IOException;

import javax.faces.event.ValueChangeEvent;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.faces.Redirect;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.AccountEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.local.AccountEntitySearchServiceLocal;

/**
 * Standard backing bean for {@link AccountEntity} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable. For this window create, edit, view, delete operations are not
 * used, because it just searches of all subtypes of AccountEntity. Crud
 * operations is dedicated to concrete entity window (e.g.
 * {@link CustomerAccount} window). Concrete windows also show more of the
 * fields and filters specific for that entity. This bean works with Manaty
 * custom JSF components.
 * 
 * @author Ignas Lelys
 * @created Dec 7, 2010
 * 
 */
@Name("customerSearchBean")
public class CustomerSearchBean extends BaseBean<AccountEntity> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link AccountEntity} service. Extends
     * {@link PersistenceService}.
     */
    @In
    private AccountEntitySearchServiceLocal accountEntitySearchService;

    /** TODO */
    private String customerPage;

    /**
     * Constructor. Invokes super constructor and provides class type of this
     * bean for {@link BaseBean}.
     */
    public CustomerSearchBean() {
        super(AccountEntity.class);
    }

    /**
     * Data model of entities for data table in GUI.
     * 
     * @return filtered entities.
     */
    @Out(value = "customerSearchResults", required = false)
    protected PaginationDataModel<AccountEntity> getDataModel() {
        return entities;
    }

    /**
     * Override get instance method because AccountEntity is abstract class and
     * can not be instantiated in {@link BaseBean}.
     */
    @Override
    public AccountEntity getInstance() throws InstantiationException, IllegalAccessException {
        return new AccountEntity() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getAccountType() {
                return "";
            }
        };
    }

    /**
     * Factory method, that is invoked if data model is empty. Invokes
     * BaseBean.list() method that handles all data model loading. Overriding is
     * needed only to put factory name on it.
     * 
     * @see org.meveo.admin.action.BaseBean#list()
     */
    @Begin(join = true)
    @Factory("customerSearchResults")
    public void list() {
        super.list();
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<AccountEntity> getPersistenceService() {
        return accountEntitySearchService;
    }

    /**
     * Because in customer search any type of customer can appear, this method
     * is used in UI to get link to concrete customer edit page.
     * 
     * @param type
     *            Account type of Customer
     * 
     * @return Edit page url.
     */
    public String getView(String type) {
        if (type.equals(Customer.ACCOUNT_TYPE)) {
            return "/pages/crm/customers/customerDetail.xhtml";
        } else if (type.equals(CustomerAccount.ACCOUNT_TYPE)) {
            return "/pages/payments/customerAccounts/customerAccountDetail.xhtml";
        }
        if (type.equals(BillingAccount.ACCOUNT_TYPE)) {
            return "/pages/billing/billingAccounts/billingAccountDetail.xhtml";
        }
        if (type.equals(UserAccount.ACCOUNT_TYPE)) {
            return "/pages/billing/userAccounts/userAccountDetail.xhtml";
        } else {
            throw new IllegalStateException("Wrong customer type provided in EL in .xhtml");
        }
    }

    /**
     * Redirect to view that is selected as type in search field. (for example
     * when user selects customer account type in search, he is redirected to
     * custmer accounts search page).
     */
    public void changeCustomerPage(ValueChangeEvent event) throws IOException {
        String page = (String) event.getNewValue();
        Redirect.instance().setViewId(page);
        Redirect.instance().execute();
    }

    public String getCustomerPage() {
        return customerPage;
    }

    public void setCustomerPage(String customerPage) {
        this.customerPage = customerPage;
    }

}
