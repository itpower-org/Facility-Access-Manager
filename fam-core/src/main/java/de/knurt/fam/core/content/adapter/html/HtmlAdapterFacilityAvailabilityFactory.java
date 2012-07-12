/*
 * Copyright 2009-2012 by KNURT Systeme (http://www.knurt.de)
 *
 * Licensed under the Creative Commons License Attribution-NonCommercial-ShareAlike 3.0 Unported;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://creativecommons.org/licenses/by-nc-sa/3.0/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.knurt.fam.core.content.adapter.html;

import de.knurt.fam.core.model.persist.FacilityAvailability;
import de.knurt.fam.core.model.persist.User;

/**
 * factory for a html adapter for a {@link FacilityAvailability}.
 * 
 * @author Daniel Oltmanns
 * @since 0.20090710 (07/10/2009)
 */
@Deprecated
public class HtmlAdapterFacilityAvailabilityFactory extends DasHtmlAdapterAbstractFactory<FacilityAvailability> {

	/**
	 * empty constructor.
	 */
	public HtmlAdapterFacilityAvailabilityFactory() {
	}

	/**
	 * produce and return a {@link HtmlAdapterFacilityAvailability}.
	 * 
	 * @param current
	 *            user being authenticated
	 * @param mappedObject
	 *            the availability being adapted.
	 * @return a {@link HtmlAdapterFacilityAvailability}.
	 */
	@Override
	protected HtmlAdapterFacilityAvailability getInstance(User current, FacilityAvailability mappedObject) {
		return new HtmlAdapterFacilityAvailability(current, mappedObject);
	}
}
