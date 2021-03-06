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
package org.meveo.service.catalog.impl;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.Title;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.local.TitleServiceLocal;

/**
 * Title service implementation.
 * 
 * @author Sebastien Michea
 * @created Dec 20, 2010
 * 
 */
@Stateless
@Name("titleService")
@AutoCreate
public class TitleService extends PersistenceService<Title> implements TitleServiceLocal {

    public Title findByCode(Provider provider, String code) {
        Title title = null;
        if (StringUtils.isBlank(code)) {
            return null;
        }
        try {
            title = (Title) em.createQuery("from Title t where t.code=:code and t.provider=:provider").setParameter("code", code)
                    .setParameter("provider", provider).getSingleResult();
        } catch (Exception e) {
            return null;
        }
        return title;
    }

}
