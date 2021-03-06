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
package org.meveo.admin.action.billing;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.international.StatusMessage.Severity;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.DuplicateDefaultAccountException;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.admin.utils.ListItemsSelector;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.local.BillingAccountServiceLocal;
import org.meveo.service.billing.local.BillingRunServiceLocal;
import org.meveo.service.billing.local.InvoiceServiceLocal;
import org.meveo.service.payments.local.CustomerAccountServiceLocal;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

/**
 * Standard backing bean for {@link BillingAccount} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 * 
 * @author Ignas Lelys
 * @created Dec 7, 2010
 * 
 */
@Name("billingAccountBean")
@Scope(ScopeType.CONVERSATION)
public class BillingAccountBean extends BaseBean<BillingAccount> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected
	 * 
	 * @{link BillingAccount} service. Extends {@link PersistenceService}.
	 */
	@In(create = true)
	private BillingAccountServiceLocal billingAccountService;

	@In
	private InvoiceServiceLocal invoiceService;

	@In
	private BillingRunServiceLocal billingRunService;

	// SEB: i think it is required
	// @In(required = false)
	@In
	private User currentUser;

	@In
	private Provider currentProvider;

	@RequestParameter
	private Long customerAccountId;

	public boolean returnToAgency;

	// TODO: SEB: pls explain why you write create=true ?
	@In(create = true)
	private CustomerAccountServiceLocal customerAccountService;

	/** Selected billing account in exceptionelInvoicing page. */
	private ListItemsSelector<BillingAccount> itemSelector;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public BillingAccountBean() {
		super(BillingAccount.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Begin(nested = true)
	@Factory("billingAccount")
	public BillingAccount init() {
		initEntity();
		returnToAgency = !(entity.getInvoicePrefix() == null);
		if (entity.getId() == null && customerAccountId != null) {
			CustomerAccount customerAccount = customerAccountService
					.findById(customerAccountId);
			entity.setCustomerAccount(customerAccountService
					.findById(customerAccountId));
			populateAccounts(customerAccount);
		}
		return entity;
	}

	/**
	 * Data model of entities for data table in GUI.
	 * 
	 * @return filtered entities.
	 */
	@Out(value = "billingAccounts", required = false)
	protected PaginationDataModel<BillingAccount> getDataModel() {
		return entities;
	}

	/**
	 * Factory method, that is invoked if data model is empty. Invokes
	 * BaseBean.list() method that handles all data model loading. Overriding is
	 * needed only to put factory name on it.
	 * 
	 * @see org.meveo.admin.action.BaseBean#list()
	 */
	@Begin(join = true)
	@Factory("billingAccounts")
	public void list() {
		super.list();
	}

	/**
	 * Conversation is ended and user is redirected from edit to his previous
	 * window.
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(org.meveo.model.IEntity)
	 */
	@End(beforeRedirect = true, root = false)
	public String saveOrUpdate() {
		try {
			if (entity.getDefaultLevel() != null && entity.getDefaultLevel()) {
				if (billingAccountService.isDuplicationExist(entity)) {
					entity.setDefaultLevel(false);
					throw new DuplicateDefaultAccountException();
				}
			}

			saveOrUpdate(entity);
			CustomerAccount customerAccount = entity.getCustomerAccount();
			if (customerAccount != null
					&& !customerAccount.getBillingAccounts().contains(entity)) {
				customerAccount.getBillingAccounts().add(entity);
			}
			Redirect.instance().setParameter("edit", "false");
			Redirect.instance().setParameter("objectId", entity.getId());
			Redirect.instance()
					.setViewId(
							"/pages/billing/billingAccounts/billingAccountDetail.xhtml");
			Redirect.instance().execute();
		} catch (DuplicateDefaultAccountException e1) {
			statusMessages.addFromResourceBundle(Severity.ERROR,
					"error.account.duplicateDefautlLevel");
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.addFromResourceBundle(Severity.ERROR,
					"javax.el.ELException");

		}
		return null;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<BillingAccount> getPersistenceService() {
		return billingAccountService;
	}

	public String saveOrUpdate(BillingAccount entity) {
		try {

			if (entity.isTransient()) {
				billingAccountService.createBillingAccount(entity, null);
				statusMessages.addFromResourceBundle("save.successful");
			} else {
				billingAccountService.updateBillingAccount(entity, null);
				statusMessages.addFromResourceBundle("update.successful");
			}

		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(e.getMessage());
		}

		return back();
	}

	public String terminateAccount() {
		log.info("terminateAccount billingAccountId:" + entity.getId());
		try {
			billingAccountService.billingAccountTermination(entity.getCode(),
					new Date(), currentUser);
			statusMessages
					.addFromResourceBundle("resiliation.resiliateSuccessful");
			return "/pages/billing/billingAccounts/billingAccountDetail.seam?objectId="
					+ entity.getId() + "&edit=false";
		} catch (BusinessException e) {
			e.printStackTrace();
			statusMessages.add(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(e.getMessage());
		}
		return null;
	}

	public String cancelAccount() {
		log.info("cancelAccount billingAccountId:" + entity.getId());
		try {
			billingAccountService.billingAccountCancellation(entity.getCode(),
					new Date(), currentUser);
			statusMessages
					.addFromResourceBundle("cancellation.cancelSuccessful");
			return "/pages/billing/billingAccounts/billingAccountDetail.seam?objectId="
					+ entity.getId() + "&edit=false";
		} catch (BusinessException e) {
			e.printStackTrace();
			statusMessages.add(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(e.getMessage());
		}
		return null;
	}

	public String closeAccount() {
		log.info("closeAccount billingAccountId:" + entity.getId());
		try {
			billingAccountService.closeBillingAccount(entity.getCode(),
					currentUser);
			statusMessages.addFromResourceBundle("close.closeSuccessful");
			return "/pages/billing/billingAccounts/billingAccountDetail.seam?objectId="
					+ entity.getId() + "&edit=false";
		} catch (BusinessException e) {
			e.printStackTrace();
			statusMessages.add(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(e.getMessage());
		}
		return null;
	}

	@Factory("getInvoices")
	public List<Invoice> getInvoices() {

		return entity != null ? entity.getInvoices() : null;
	}

	public void generatePDF(long invoiceId) {
		Invoice invoice = invoiceService.findById(invoiceId);
		byte[] invoicePdf = invoice.getPdf();
		FacesContext context = FacesContext.getCurrentInstance();
		String invoiceFilename = null;
		if (invoice.getBillingRun().getStatus() == BillingRunStatusEnum.VALIDATED) {
			invoiceFilename = invoice.getInvoiceNumber() + ".pdf";
		}
		else {invoiceFilename="unvalidated-invoice.pdf";}
		HttpServletResponse response = (HttpServletResponse) context
				.getExternalContext().getResponse();
		response.setContentType("application/pdf"); // fill in
		response.setHeader("Content-disposition", "attachment; filename="
				+ invoiceFilename);

		try {
			OutputStream os = response.getOutputStream();
			Document document = new Document(PageSize.A4);
			if (invoice.getBillingRun().getStatus() != BillingRunStatusEnum.VALIDATED) {
				// Add watemark image
				PdfReader reader = new PdfReader(invoicePdf);
				int n = reader.getNumberOfPages();
				PdfStamper stamp = new PdfStamper(reader, os);
				PdfContentByte over = null;
				BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA,
						BaseFont.WINANSI, BaseFont.EMBEDDED);
				PdfGState gs = new PdfGState();
				gs.setFillOpacity(0.5f);
				int i = 1;
				while (i <= n) {
					over = stamp.getOverContent(i);
					over.setGState(gs);
					over.beginText();
					System.out.println("top=" + document.top() + ",bottom="
							+ document.bottom());
					over.setTextMatrix(document.top(), document.bottom());
					over.setFontAndSize(bf, 150);
					over.setColorFill(Color.GRAY);
					over.showTextAligned(Element.ALIGN_CENTER, "TEST", document
							.getPageSize().getWidth() / 2, document
							.getPageSize().getHeight() / 2, 45);
					over.endText();
					i++;
				}

				stamp.close();
			} else {
				os.write(invoicePdf); // fill in PDF with bytes
			}

			// contentType
			os.flush();
			os.close();
			context.responseComplete();
		} catch (IOException e) {
			log.error(e);
		} catch (DocumentException e) {
			log.error(e);
		}
	}

	public boolean pdfExists(long invoiceId) {
		Invoice invoice = invoiceService.findById(invoiceId);
		if (invoice.getPdf() == null)
			return false;
		return true;
	}

	public String launchExceptionalInvoicing() {
		log.info("launchExceptionelInvoicing...");
		try {
			ParamBean param = ParamBean.getInstance("meveo-admin.properties");
			String allowManyInvoicing = param.getProperty(
					"billingRun.allowManyInvoicing", "true");
			boolean isAllowed = Boolean.parseBoolean(allowManyInvoicing);
			log.info("lunchInvoicing allowManyInvoicing=#", isAllowed);
			if (billingRunService.isActiveBillingRunsExist(currentProvider)
					&& !isAllowed) {
				statusMessages.addFromResourceBundle(Severity.ERROR,
						"error.invoicing.alreadyLunched");
				return null;
			}

			BillingRun billingRun = new BillingRun();
			billingRun.setStatus(BillingRunStatusEnum.NEW);
			billingRun.setProcessDate(new Date());
			billingRun.setProcessType(BillingProcessTypesEnum.MANUAL);

			for (BillingAccount billingAccount : itemSelector.getList()) {
				log.debug("lunchExceptionelInvoicing id=#0",
						billingAccount.getId());
				billingAccount.setBillingRun(billingRun);
				billingAccountService.update(billingAccount);
			}
			billingRunService.create(billingRun);
			return "/pages/billing/invoicing/billingRuns.seam?edit=false";
		} catch (Exception e) {
			log.error("lunchExceptionelInvoicing", e);
			statusMessages
					.addFromResourceBundle(Severity.ERROR, e.getMessage());
		}
		return null;
	}

	/**
	 * This is workaround for #594. For some reason when leaving
	 * billingAccountDetail.xhtml, the invoices datalist somehow asks for
	 * #{billingAccount} when we are not in billingAccountDetails conversation,
	 * that invokes factory method init() which starts new new conversation,
	 * which later cause a bug.
	 */
	public BillingAccount getBillingAccount() {
		return entity != null ? entity : new BillingAccount();
	}

	/**
	 * Item selector getter. Item selector keeps a state of multiselect
	 * checkboxes.
	 */
	@BypassInterceptors
	public ListItemsSelector<BillingAccount> getItemSelector() {
		if (itemSelector == null) {
			itemSelector = new ListItemsSelector<BillingAccount>(false);
		}
		return itemSelector;
	}

	/**
	 * Check/uncheck all select boxes.
	 */
	public void checkUncheckAll(ValueChangeEvent event) {
		itemSelector.switchMode();
	}

	/**
	 * Listener of select changed event.
	 */
	public void selectChanged(ValueChangeEvent event) {
		if (entities != null) {
			BillingAccount entity = entities.getRowDataT();
			if (entity != null) {
				itemSelector.check(entity);
			}
		}
	}

	/**
	 * Resets item selector.
	 */
	public void resetSelection() {
		if (itemSelector == null) {
			itemSelector = new ListItemsSelector<BillingAccount>(false);
		} else {
			itemSelector.reset();
		}
	}

	public boolean isReturnToAgency() {
		return returnToAgency;
	}

	public void setReturnToAgency(boolean returnToAgency) {
		this.returnToAgency = returnToAgency;
	}

	public void setInvoicePrefix() {
		if (returnToAgency) {
			String invoicePrefix = null;
			if (entity.getProvider().isEntreprise()) {
				invoicePrefix = "R_PRO_";
			} else {
				invoicePrefix = "R_PART_";
			}
			entity.setInvoicePrefix(invoicePrefix + entity.getExternalRef2());
		} else
			entity.setInvoicePrefix(null);
	}

	public void processValueChange(ValueChangeEvent value) {
		if (value != null) {
			if (value.getNewValue() instanceof String) {
				entity.setExternalRef2((String) value.getNewValue());
				setInvoicePrefix();
			}

		}
	}

	public void populateAccounts(CustomerAccount customerAccount) {
		entity.setCustomerAccount(customerAccount);
		if (billingAccountService.isDuplicationExist(entity)) {
			entity.setDefaultLevel(false);
		} else {
			entity.setDefaultLevel(true);
		}
		if (customerAccount != null && customerAccount.getProvider() != null
				&& customerAccount.getProvider().isLevelDuplication()) {

			entity.setCode(customerAccount.getCode());
			entity.setDescription(customerAccount.getDescription());
			entity.setEmail(customerAccount.getContactInformation().getEmail());
			entity.setAddress(customerAccount.getAddress());
			entity.setExternalRef1(customerAccount.getExternalRef1());
			entity.setExternalRef2(customerAccount.getExternalRef2());
			entity.setProviderContact(customerAccount.getProviderContact());
			entity.setName(customerAccount.getName());
			entity.setPaymentMethod(customerAccount.getPaymentMethod());
			entity.setProvider(customerAccount.getProvider());
			entity.setPrimaryContact(customerAccount.getPrimaryContact());
		}
	}

}
