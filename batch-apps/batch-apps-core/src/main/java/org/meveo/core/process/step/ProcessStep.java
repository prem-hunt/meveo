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
package org.meveo.core.process.step;

import java.util.Map;

import org.meveo.core.inputhandler.TaskExecution;

/**
 * Process step to be executed in a chain.
 * 
 * @author Donatas Remeika
 * @created Mar 6, 2009
 */
public interface ProcessStep<T> {

    /**
     * Process in a Chain of Steps, starting from this step.
     */
    public void process(T ticket, TaskExecution<T> taskExecution, Map<String, Object> contextParameters);

}
