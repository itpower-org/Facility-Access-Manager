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
package de.knurt.fam.test.unit.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.knurt.fam.core.persistence.dao.FamDaoProxy;
import de.knurt.fam.core.persistence.dao.UserDao;
import de.knurt.fam.core.persistence.dao.config.RoleConfigDao;
import de.knurt.fam.core.persistence.dao.ibatis.UserDao4ibatis;

/**
 * 
 * @author Daniel Oltmanns <info@knurt.de>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/test-dependencies.xml" })
public class FactoryTest {

    /**
     *
     */
    public FactoryTest() {
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     *
     */
    @Before
    public void setUp() {
    }

    /**
     *
     */
    @After
    public void tearDown() {
    }

    /**
     *
     */
    @Test
    public void factoriesProducesRightProducts_RoleAccess() {
        RoleConfigDao rp = RoleConfigDao.getInstance();
        assertEquals("[extern, intern, admin, operator]", rp.getRoleIds().toString());
        assertEquals("extern", rp.getStandardId());
    }

    /**
     *
     */
    @Test
    public void factoriesProducesRightProducts_FamDaoProxy() {
        FamDaoProxy ddg = FamDaoProxy.getInstance();
        Class<? extends UserDao> productClass = ddg.getUserDao().getClass();
        assertTrue(productClass.equals(UserDao4ibatis.class));
    }
}
