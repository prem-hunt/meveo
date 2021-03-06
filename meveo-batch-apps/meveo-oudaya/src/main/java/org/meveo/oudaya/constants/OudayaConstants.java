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
package org.meveo.oudaya.constants;

import org.meveo.core.inputhandler.TaskExecution;
import org.meveo.core.process.step.StepExecution;

/**
 * String constants that is used as keys in context parameters in {@link StepExecution}
 * and {@link TaskExecution} objects. Using this constants user can get value from context parameter Map.
 * For example ACCESS_KEY would return {@link Access}.
 * Of course putting value to context should be of same type as expected when getting it using same key.
 * 
 * @author Ignas Lelys
 * @created Jul 1, 2010
 *
 */
public class OudayaConstants {

    public static final String INVOICE_KEY = "INVOICE";
    public static final String INVOICE_TASK_KEY = "INVOICE_TASK";
    public static final String LIST_OF_INVOICES_KEY = "INVOICES";
    public static final String LIST_OF_UNPAID_INVOICES_KEY = "UNPAID_INVOICES";
    
}
