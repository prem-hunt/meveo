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

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.crm.local.CustomerBrandServiceLocal;

/**
 * Service Template service implementation.
 * 
 */
@Stateless
@Name("customerBrandService")
@AutoCreate
public class CustomerBrandService extends PersistenceService<CustomerBrand> implements CustomerBrandServiceLocal {
    public CustomerBrand findByCode(String code) {

        try {
            return (CustomerBrand) em.createQuery("from " + CustomerBrand.class.getSimpleName() + " where code=:code").setParameter("code", code).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}