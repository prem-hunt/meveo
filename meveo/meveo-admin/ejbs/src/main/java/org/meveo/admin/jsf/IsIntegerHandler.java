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
package org.meveo.admin.jsf;

import java.math.BigInteger;

import com.sun.facelets.tag.TagConfig;

public class IsIntegerHandler extends IsTypeHandler {
	/**
	 * Create tag.
	 * 
	 * @param config
	 *            TagConfig
	 */
	public IsIntegerHandler(final TagConfig config) {
		super(config);
	}

	/**
	 * Is Integer
	 * 
	 * @param type
	 *            Type class.
	 * 
	 * @return True if this is Integer.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected boolean isType(Class type) {
		/*
		 * If the type is a string, process the body of the tag.
		 */
		if (type == Integer.class || type == BigInteger.class
				|| (type.isPrimitive() && type.getName().equals("int"))) {
			return true;
		} else {
			return false;
		}
	}
}
