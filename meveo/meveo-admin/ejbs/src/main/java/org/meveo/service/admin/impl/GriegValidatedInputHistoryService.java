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
package org.meveo.service.admin.impl;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.meveo.model.admin.GriegValidatedInvoiceInputHistory;
import org.meveo.service.admin.local.GriegValidatedInputHistoryServiceLocal;
import org.meveo.service.base.PersistenceService;

/**
 * GriegValidatedInputHistoryService service implementation.
 * 
 * @author Ignas
 * @created Apr 11, 2011
 */
@Stateless
@Name("griegValidatedInputHistoryService")
@AutoCreate
public class GriegValidatedInputHistoryService extends PersistenceService<GriegValidatedInvoiceInputHistory> implements
        GriegValidatedInputHistoryServiceLocal {

}
