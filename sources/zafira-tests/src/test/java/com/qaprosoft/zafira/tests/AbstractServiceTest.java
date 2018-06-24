package com.qaprosoft.zafira.tests;

import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.dto.TestRunStatistics;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Random;

@ContextConfiguration("classpath:services-test.xml")
public class AbstractServiceTest<T> extends AbstractTestNGSpringContextTests
{

	protected static final Logger LOGGER = Logger.getLogger(AbstractServiceTest.class);

	@Autowired
	private SimpleCacheManager cacheManager;

	private CacheType cacheType;

	private Cache cache;

	private Class<T> clazz;

	private Random random = new Random();

	public AbstractServiceTest(Class<T> clazz)
	{
		this.clazz = clazz;
	}

	@PostConstruct
	private void setup()
	{
		this.cacheType = Arrays.stream(CacheType.values()).filter(ct -> ct.getClazz().isAssignableFrom(clazz)).findFirst().orElse(CacheType.TEST_RUN_STATISTICS);
		this.cache = cacheManager.getCache(cacheType.getName());
	}

	@BeforeMethod
	public void start(ITestContext context)
	{
		PropertyConfigurator.configure(ClassLoader.getSystemResource("log4j.properties"));
	}

	enum CacheType
	{
		TEST_RUN_STATISTICS("testRunStatistics", TestRunStatistics.class);

		private String name;
		private Class<?> clazz;

		CacheType(String name, Class<?> clazz)
		{
			this.name = name;
			this.clazz = clazz;
		}

		public String getName()
		{
			return name;
		}

		public Class<?> getClazz()
		{
			return clazz;
		}
	}

	public T getCachedValue(Object key)
	{
		return clazz.cast(cache.get(key, clazz));
	}

	protected Object getRandomClassValue(Class<?> clazz, Object... availableValues)
	{
		Object result = null;
		if(clazz.isAssignableFrom(Long.class))
		{
			result = ! ArrayUtils.isEmpty(availableValues) ? availableValues[RandomUtils.nextInt(0, availableValues.length)] : RandomUtils.nextLong(1, 100);
		} else if(clazz.isAssignableFrom(Boolean.class))
		{
			result = random.nextBoolean();
		} else if(clazz.isAssignableFrom(Status.class))
		{

			result = Status.values()[RandomUtils.nextInt(0, Status.values().length)];
		} else if(clazz.isAssignableFrom(TestRunStatistics.Action.class))
		{
			result = TestRunStatistics.Action.values()[RandomUtils.nextInt(0, TestRunStatistics.Action.values().length)];
		}
		return result;
	}

	public SimpleCacheManager getCacheManager()
	{
		return cacheManager;
	}

	public Cache getCache()
	{
		return cache;
	}

	public Class<T> getClazz()
	{
		return clazz;
	}

	public CacheType getCacheType()
	{
		return cacheType;
	}
}
