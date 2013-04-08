/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.test.context.junit4.spr9645;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.tests.transaction.CallCountingTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests that verify the behavior requested in
 * <a href="https://jira.springsource.org/browse/SPR-9645">SPR-9645</a>.
 *
 * @author Sam Brannen
 * @since 3.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class LookUpTxMgrByTypeAndQualifierAtMethodLevelTests {

	private static final CallCountingTransactionManager txManager1 = new CallCountingTransactionManager();
	private static final CallCountingTransactionManager txManager2 = new CallCountingTransactionManager();

	@Configuration
	static class Config {

		@Bean
		public PlatformTransactionManager txManager1() {
			return txManager1;
		}

		@Bean
		public PlatformTransactionManager txManager2() {
			return txManager2;
		}
	}

	@BeforeTransaction
	public void beforeTransaction() {
		txManager1.clear();
		txManager2.clear();
	}

	@Transactional("txManager1")
	@Test
	public void transactionalTest() {
		assertEquals(1, txManager1.begun);
		assertEquals(1, txManager1.inflight);
		assertEquals(0, txManager1.commits);
		assertEquals(0, txManager1.rollbacks);
	}

	@AfterTransaction
	public void afterTransaction() {
		assertEquals(1, txManager1.begun);
		assertEquals(0, txManager1.inflight);
		assertEquals(0, txManager1.commits);
		assertEquals(1, txManager1.rollbacks);
	}

}
