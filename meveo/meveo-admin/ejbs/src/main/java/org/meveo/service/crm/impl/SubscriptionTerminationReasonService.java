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
package org.meveo.service.crm.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.crm.local.SubscriptionTerminationReasonServiceLocal;

/**
 * Service SubscriptionTerminationReason implementation.
 * 
 */
@Stateless
@Name("subscriptionTerminationReasonService")
@AutoCreate
public class SubscriptionTerminationReasonService extends PersistenceService<SubscriptionTerminationReason> implements
        SubscriptionTerminationReasonServiceLocal {

    public SubscriptionTerminationReason findByCodeReason(String codeReason, String providerCode) throws Exception {
        return (SubscriptionTerminationReason) em
                .createQuery("from " + SubscriptionTerminationReason.class.getSimpleName() + " where code=:codeReason and provider.code=:providerCode")
                .setParameter("codeReason", codeReason).setParameter("providerCode", providerCode).getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public List<SubscriptionTerminationReason> listReasons() {
        Provider currentProvider = (Provider) Component.getInstance("currentProvider");
        Query query = new QueryBuilder(SubscriptionTerminationReason.class, "c", null).startOrClause().addCriterionEntity("c.provider", currentProvider)
                .addSql("c.provider is null").endOrClause().getQuery(em);
        return query.getResultList();
    }
}
