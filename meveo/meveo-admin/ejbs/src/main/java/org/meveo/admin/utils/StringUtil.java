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
package org.meveo.admin.utils;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.meveo.commons.utils.StringUtils;

/**
 * @author Tyshan(tyshan@manaty.net)
 */

@Name("stringUtil")
@Scope(ScopeType.EVENT)
public class StringUtil {

    public String merge(String... s) {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isBlank(s)) {
            for (String temp : s) {
                if (s == null || s.equals(""))
                    continue;
                sb.append(temp);
            }
        }
        return sb.toString();
    }
}
