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
package org.meveo.admin.action.payments;

import javax.persistence.EntityExistsException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.payments.ActionPlanItem;
import org.meveo.model.payments.DunningPlan;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.local.ActionPlanItemServiceLocal;
import org.meveo.service.payments.local.DunningPlanServiceLocal;

/**
 * Standard backing bean for {@link ActionPlanItem} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 * 
 * @author Tyshan(tyshan@manaty.net)
 */
@Name("actionPlanItemBean")
@Scope(ScopeType.CONVERSATION)
public class ActionPlanItemBean extends BaseBean<ActionPlanItem> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link ActionPlanItem} service. Extends
     * {@link PersistenceService}.
     */
    @In
    private ActionPlanItemServiceLocal actionPlanItemService;

    /**
     * TODO
     */
    @In
    private DunningPlanServiceLocal dunningPlanService;

    /**
     * TODO
     */
    @In(required = false)
    private DunningPlan dunningPlan;

    /**
     * Constructor. Invokes super constructor and provides class type of this
     * bean for {@link BaseBean}.
     */
    public ActionPlanItemBean() {
        super(ActionPlanItem.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity
     * from database, otherwise create new.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Factory("actionPlanItem")
    @Begin(nested = true)
    public ActionPlanItem init() {
        if (dunningPlan != null && dunningPlan.getId() == null) {
            dunningPlanService.create(dunningPlan);
        }
        initEntity();
        entity.setDunningPlan(dunningPlan);
        return entity;
    }

    /**
     * Data model of entities for data table in GUI.
     * 
     * @return filtered entities.
     */
    @Out(value = "actionPlanItems", required = false)
    protected PaginationDataModel<ActionPlanItem> getDataModel() {
        return entities;
    }

    /**
     * Factory method, that is invoked if data model is empty. Invokes
     * BaseBean.list() method that handles all data model loading. Overriding is
     * needed only to put factory name on it.
     * 
     * @see org.meveo.admin.action.BaseBean#list()
     */
    @Override
    @Begin(join = true)
    @Factory("actionPlanItems")
    public void list() {
        super.list();
    }

    /**
     * Conversation is ended and user is redirected from edit to his previous
     * window.
     * 
     * @see org.meveo.admin.action.BaseBean#saveOrUpdate(org.meveo.model.IEntity)
     */
    @End(beforeRedirect = true, root=false)
    public String saveOrUpdate() {
        dunningPlan.getActions().add(entity);
        saveOrUpdate(entity);
        return "/pages/payments/dunning/dunningPlanDetail.xhtml?objectId=" + dunningPlan.getId() + "&edit=true";
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<ActionPlanItem> getPersistenceService() {
        return actionPlanItemService;
    }

    @Override
    public void delete(Long id) {
        try {
            entity = getPersistenceService().findById(id);
            log.info(String.format("Deleting entity %s with id = %s", entity.getClass().getName(), id));
            entity.getDunningPlan().getActions().remove(entity);
            getPersistenceService().remove(id);
            entity = null;
            statusMessages.addFromResourceBundle(Severity.INFO, "delete.successful");
        } catch (Throwable t) {
            if (t.getCause() instanceof EntityExistsException) {
                log.info("delete was unsuccessful because entity is used in the system", t);
                statusMessages.addFromResourceBundle(Severity.ERROR, "error.delete.entityUsed");
            } else {
                log.info("unexpected exception when deleting!", t);
                statusMessages.addFromResourceBundle(Severity.ERROR, "error.delete.unexpected");
            }
        }
    }
}
