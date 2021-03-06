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
package org.meveo.persistence;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;

/**
 * Persistence provider.
 * 
 * @author Donatas Remeika
 * @created Mar 3, 2009
 */
public class MeveoPersistence {

    private static final Logger logger = Logger.getLogger(MeveoPersistence.class);

    private static EntityManagerFactory factory;

    private static ThreadLocal<EntityManager> threadLocalManager = new ThreadLocal<EntityManager>();

    public static void init(String persistenceUnitName, Map<String, String> persistenceProperties) {
        factory  = Persistence.createEntityManagerFactory(persistenceUnitName, persistenceProperties);
    }
    
    /**
     * Return EntityManager instance.
     */
    public static final EntityManager getEntityManager() {
        EntityManager entityManager = threadLocalManager.get();
        if (entityManager == null) {
            entityManager = factory.createEntityManager();
            threadLocalManager.set(entityManager);
            if (logger.isDebugEnabled()) {
                logger.debug("Created new EntityManager");
            }
        }
        return entityManager;
    }

    /**
     * Close EntityManager.
     */
    public static final void closeEntityManager() {
        EntityManager entityManager = threadLocalManager.get();
        if (entityManager != null) {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
            threadLocalManager.set(null);
            if (logger.isDebugEnabled()) {
                logger.debug("Closed EntityManager");
            }
        }
    }
    
    /**
     * Close entity manager factory. All EntityManagers will be considered
     * closed after this method is invoked.
     */
    public static final void closeAll() {
        if (factory.isOpen()) {
            factory.close();
        }
    }

}
