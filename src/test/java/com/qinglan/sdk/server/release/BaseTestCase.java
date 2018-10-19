package com.qinglan.sdk.server.release;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath*:/application-context.xml" })
public abstract class BaseTestCase extends AbstractJUnit4SpringContextTests {

	protected Logger logger = LoggerFactory.getLogger(getClass());

}
