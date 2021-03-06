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
package org.meveo.service.payments.local;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Local;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.service.base.local.IPersistenceService;

/**
 * OtherCreditAndCharge service local interface.
 * 
 * @author Ignas
 * @created 2009.09.04
 */
@Local
public interface OtherCreditAndChargeServiceLocal extends IPersistenceService<OtherCreditAndCharge> {
    public void addOCC(String codeOCCTemplate, String descToAppend, Long customerAccountId, String customerAccountCode, BigDecimal amount, Date dueDate,
            User user) throws BusinessException, Exception;

    public void addOCC(String codeOCCTemplate, String descToAppend, CustomerAccount customerAccount, BigDecimal amount, Date dueDate, User user)
            throws BusinessException, Exception;
}
