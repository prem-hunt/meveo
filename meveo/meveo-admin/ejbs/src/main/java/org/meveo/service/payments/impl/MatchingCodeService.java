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
package org.meveo.service.payments.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.log.Log;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.model.AuditableEntity;
import org.meveo.model.MatchingReturnObject;
import org.meveo.model.PartialMatchingOccToSelect;
import org.meveo.model.admin.User;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingCode;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.MatchingTypeEnum;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.payments.local.AccountOperationServiceLocal;
import org.meveo.service.payments.local.CustomerAccountServiceLocal;
import org.meveo.service.payments.local.MatchingCodeServiceLocal;
import org.meveo.service.payments.remote.MatchingCodeServiceRemote;

/**
 * MatchingCode service implementation.
 * 
 * @author anasseh
 * @created 28.11.2010
 */
@Stateless
@Name("matchingCodeService")
@AutoCreate
public class MatchingCodeService extends PersistenceService<MatchingCode> implements MatchingCodeServiceLocal, MatchingCodeServiceRemote {

	@In(create = true)
	private CustomerAccountServiceLocal customerAccountService;
	@In
	private AccountOperationServiceLocal accountOperationService;

	@Logger
	protected Log log;

	@Transactional
	public MatchingReturnObject matchOperations(Long customerAccountId, String customerAccountCode, List<Long> operationIds,
			Long operationIdForPartialMatching, User user) throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException, Exception {
		return matchOperations(customerAccountId, customerAccountCode, operationIds, operationIdForPartialMatching, MatchingTypeEnum.M, user);
	}

	private void matching(List<AccountOperation> listOcc, BigDecimal amount, AccountOperation partialOcc, MatchingTypeEnum matchingTypeEnum, User user)
			throws Exception {
		MatchingCode matchingCode = new MatchingCode();
		BigDecimal amountToMatch = BigDecimal.ZERO;
		for (AccountOperation accountOperation : listOcc) {
			if (partialOcc != null && accountOperation.getId().equals(partialOcc.getId())) {
				amountToMatch = accountOperation.getUnMatchingAmount().subtract(amount);
				accountOperation.setMatchingAmount(accountOperation.getMatchingAmount().add(amountToMatch));
				accountOperation.setUnMatchingAmount(accountOperation.getUnMatchingAmount().subtract(amountToMatch));
				accountOperation.setMatchingStatus(MatchingStatusEnum.P);
			} else {
				amountToMatch = accountOperation.getUnMatchingAmount();
				accountOperation.setMatchingAmount(accountOperation.getMatchingAmount().add(amountToMatch));
				accountOperation.setUnMatchingAmount(accountOperation.getUnMatchingAmount().subtract(amountToMatch));
				accountOperation.setMatchingStatus(MatchingStatusEnum.L);

			}
			accountOperationService.update(accountOperation, user);
			MatchingAmount matchingAmount = new MatchingAmount();
			matchingAmount.setProvider(accountOperation.getProvider());
			((AuditableEntity) matchingAmount).updateAudit(user);
			matchingAmount.setAccountOperation(accountOperation);
			matchingAmount.setMatchingCode(matchingCode);
			matchingAmount.setMatchingAmount(amountToMatch);
			accountOperation.getMatchingAmounts().add(matchingAmount);
			matchingCode.getMatchingAmounts().add(matchingAmount);
		}
		matchingCode.setMatchingAmountDebit(amount);
		matchingCode.setMatchingAmountCredit(amount);
		matchingCode.setMatchingDate(new Date());
		matchingCode.setMatchingType(matchingTypeEnum);
		create(matchingCode, user);

	}

	@Transactional
	public void unmatching(Long idMatchingCode, User user) throws BusinessException {
		log.info("start cancelMatching with id:#0,user:#1", idMatchingCode, user);
		if (idMatchingCode == null)
			throw new BusinessException("Error when idMatchingCode is null!");
		if (user == null || user.getId() == null) {
			throw new BusinessException("Error when user is null!");
		}
		MatchingCode matchingCode = findById(idMatchingCode);
		if (matchingCode == null) {
			log.warn("Error when found a null matchingCode!");
			throw new BusinessException("Error when found a null matchingCode!");
		}
		List<MatchingAmount> matchingAmounts = matchingCode.getMatchingAmounts();

		if (matchingAmounts != null) {
			log.info("matchingAmounts.size:" + matchingAmounts.size());
			for (MatchingAmount matchingAmount : matchingAmounts) {
				AccountOperation operation = matchingAmount.getAccountOperation();
				if (operation.getMatchingStatus() != MatchingStatusEnum.P && operation.getMatchingStatus() != MatchingStatusEnum.L) {
					throw new BusinessException("Error:matchingCode containt unMatching operation");
				}
				operation.setUnMatchingAmount(operation.getUnMatchingAmount().add(matchingAmount.getMatchingAmount()));
				operation.setMatchingAmount(operation.getMatchingAmount().subtract(matchingAmount.getMatchingAmount()));
				if (BigDecimal.ZERO.compareTo(operation.getMatchingAmount()) == 0) {
					operation.setMatchingStatus(MatchingStatusEnum.O);
				} else {
					operation.setMatchingStatus(MatchingStatusEnum.P);
				}
				operation.getMatchingAmounts().remove(matchingAmount);
				accountOperationService.update(operation, user);
				log.info("cancel one accountOperation!");
			}
		}
		log.info("remove matching code ....");
		remove(matchingCode);
		log.info("successfully end cancelMatching!");
	}

	public MatchingReturnObject matchOperations(Long customerAccountId, String customerAccountCode, List<Long> operationIds,
			Long operationIdForPartialMatching, MatchingTypeEnum matchingTypeEnum, User user) throws BusinessException, NoAllOperationUnmatchedException,
			UnbalanceAmountException, Exception {
		log.info("matchOperations   customerAccountId:{0}  customerAccountCode:{1} operationIds:{2} user:{3}", customerAccountId, customerAccountCode,
				operationIds, user == null ? "null" : user.getName());
		CustomerAccount customerAccount = customerAccountService.findCustomerAccount(customerAccountId, customerAccountCode);

		BigDecimal amoutDebit = new BigDecimal(0);
		BigDecimal amoutCredit = new BigDecimal(0);
		List<AccountOperation> listAccountOperationOfAccountCustomer = customerAccount.getAccountOperations();
		List<AccountOperation> listOcc = new ArrayList<AccountOperation>();
		MatchingReturnObject matchingReturnObject = new MatchingReturnObject();
		matchingReturnObject.setOk(false);

		int cptOccDebit = 0, cptOccCredit = 0, cptPartialAllowed = 0;
		AccountOperation accountOperationForPartialMatching = null;

		for (Long id : operationIds) {
			AccountOperation accountOperation = accountOperationService.findById(id);
			listOcc.add(accountOperation);

		}

		for (AccountOperation accountOperation : listOcc) {
			if (!listAccountOperationOfAccountCustomer.contains(accountOperation)) {
				log.warn("matchOperations The operationId " + accountOperation.getId()
						+ " is not for the customerAccount");
				throw new BusinessException("The operationId " + accountOperation.getId()
						+ " is not for the customerAccount");
			}
			if (accountOperation.getMatchingStatus() != MatchingStatusEnum.O
					&& accountOperation.getMatchingStatus() != MatchingStatusEnum.P) {
				log.warn("matchOperations The operationId " + accountOperation.getId()
						+ " is already matching");
				throw new NoAllOperationUnmatchedException("The operationId " + accountOperation.getId()
						+ " is already matching");
			}
			if (accountOperation.getTransactionCategory() == OperationCategoryEnum.DEBIT) {
				cptOccDebit++;
				amoutDebit = amoutDebit.add(accountOperation.getUnMatchingAmount());
			}
			if (accountOperation.getTransactionCategory() == OperationCategoryEnum.CREDIT) {
				cptOccCredit++;
				amoutCredit = amoutCredit.add(accountOperation.getUnMatchingAmount());
			}
		}
		if (cptOccCredit == 0) {
			throw new BusinessException("matchingService.noCreditOps");
		}
		if (cptOccDebit == 0) {
			throw new BusinessException("matchingService.noDebitOps");
		}
		BigDecimal balance = amoutDebit.subtract(amoutCredit);
		balance = balance.abs();

		log.info("matchOperations  balance:" + balance);

		if (balance.compareTo(BigDecimal.ZERO) == 0) {
			matching(listOcc, amoutDebit, null, matchingTypeEnum, user);
			matchingReturnObject.setOk(true);
			log.info("matchOperations successful : no partial");
			return matchingReturnObject;
		}

		if (balance.compareTo(BigDecimal.ZERO) != 0 && operationIdForPartialMatching != null) {
			matching(listOcc, balance, accountOperationService.findById(operationIdForPartialMatching), matchingTypeEnum, user);
			matchingReturnObject.setOk(true);
			log.info("matchOperations successful :  partial ok (idPartial recu)");
			return matchingReturnObject;
		}
		// debit 200,60 ; credit 150 => balance = 110
		for (AccountOperation accountOperation : listOcc) {
			PartialMatchingOccToSelect p = new PartialMatchingOccToSelect();
			p.setAccountOperation(accountOperation);
			p.setPartialMatchingAllowed(false);
			if (amoutCredit.compareTo(amoutDebit) > 0) {
				if (accountOperation.getTransactionCategory() == OperationCategoryEnum.CREDIT) {
					if (balance.compareTo(accountOperation.getUnMatchingAmount()) < 0) {
						p.setPartialMatchingAllowed(true);
						cptPartialAllowed++;
						accountOperationForPartialMatching = accountOperation;
					}
				}
			}
			if (amoutDebit.compareTo(amoutCredit) > 0) {
				if (accountOperation.getTransactionCategory() == OperationCategoryEnum.DEBIT) {
					if (balance.compareTo(accountOperation.getUnMatchingAmount()) < 0) {
						p.setPartialMatchingAllowed(true);
						cptPartialAllowed++;
						accountOperationForPartialMatching = accountOperation;

					}
				}
			}
			matchingReturnObject.getPartialMatchingOcc().add(p);
		}

		if (balance.compareTo(BigDecimal.ZERO) != 0 && cptPartialAllowed == 1) {
			matching(listOcc, balance, accountOperationForPartialMatching, matchingTypeEnum, user);
			matchingReturnObject.setOk(true);
			log.info("matchOperations successful :  partial ok (un idPartial possible)");
			return matchingReturnObject;
		}

		if (cptPartialAllowed == 0) {
			throw new BusinessException("matchingService.matchingImpossible");
		}
		log.info("matchOperations successful :  partial  (plusieurs idPartial possible)");
		matchingReturnObject.setOk(false);

		// log.info("matchOperations successful  customerAccountId:{}  customerAccountCode:{} operationIds:{} user:{}",
		// customerAccountId, customerAccountCode,
		// operationIds, user == null ? "null" : user.getName());

		return matchingReturnObject;
	}
}