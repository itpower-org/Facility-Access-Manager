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
package de.knurt.fam.test.unit.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.knurt.fam.connector.BasicDataSourceFactory;
import de.knurt.fam.connector.FamConnector;

/**
 * 
 * @author Daniel Oltmanns <info@knurt.de>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/test-dependencies.xml" })
public class AutoreconnectMustBeConfiguredTest {

	private final String REGEX = ".*mysql.*autoReconnect=true$";

	@Test
	public void assertItIsNotConfigured() {
		assertFalse(FamConnector.getGlobalProperty("sql_url").matches(REGEX));
	}

	/**
	 * this fails on not using mysql
	 */
	@Test
	public void addAutoreconnectIfNotConfigured() {
		BasicDataSourceFactory fbdsw = new BasicDataSourceFactory();
		assertTrue(fbdsw.getBds().getUrl().matches(REGEX));
	}
}
