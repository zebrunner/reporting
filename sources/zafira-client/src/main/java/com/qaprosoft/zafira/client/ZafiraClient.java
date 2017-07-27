package com.qaprosoft.zafira.client;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qaprosoft.zafira.config.CIConfig;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.TestRun.DriverMode;
import com.qaprosoft.zafira.models.db.TestRun.Initiator;
import com.qaprosoft.zafira.models.dto.EmailType;
import com.qaprosoft.zafira.models.dto.EventType;
import com.qaprosoft.zafira.models.dto.JobType;
import com.qaprosoft.zafira.models.dto.TestCaseType;
import com.qaprosoft.zafira.models.dto.TestRunType;
import com.qaprosoft.zafira.models.dto.TestSuiteType;
import com.qaprosoft.zafira.models.dto.TestType;
import com.qaprosoft.zafira.models.dto.auth.AuthTokenType;
import com.qaprosoft.zafira.models.dto.auth.CredentialsType;
import com.qaprosoft.zafira.models.dto.auth.RefreshTokenType;
import com.qaprosoft.zafira.models.dto.ua.UAInspectionType;
import com.qaprosoft.zafira.models.dto.user.UserType;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class ZafiraClient
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ZafiraClient.class);
	
	private static final String ANONYMOUS = "anonymous";
	
	private static final Integer CONNECT_TIMEOUT = 30000;
	private static final Integer READ_TIMEOUT = 30000;
	
	private static final String STATUS_PATH = "/api/status";
	private static final String PROFILE_PATH = "/api/users/profile";
	private static final String LOGIN_PATH = "/api/auth/login";
	private static final String REFRESH_TOKEN_PATH = "/api/auth/refresh";
	private static final String USERS_PATH = "/api/users";
	private static final String JOBS_PATH = "/api/jobs";
	private static final String TESTS_PATH = "/api/tests";
	private static final String TEST_FINISH_PATH = "/api/tests/%d/finish";
	private static final String TEST_BY_ID_PATH = "/api/tests/%d";
	private static final String TEST_WORK_ITEMS_PATH = "/api/tests/%d/workitems";
	private static final String TEST_SUITES_PATH = "/api/tests/suites";
	private static final String TEST_CASES_PATH = "/api/tests/cases";
	private static final String TEST_CASES_BATCH_PATH = "/api/tests/cases/batch";
	private static final String TEST_RUNS_PATH = "/api/tests/runs";
	private static final String TEST_RUNS_FINISH_PATH = "/api/tests/runs/%d/finish";
	private static final String TEST_RUNS_RESULTS_PATH = "/api/tests/runs/%d/results";
	private static final String TEST_RUNS_ABORT_PATH = "/api/tests/runs/abort?id=%d";
	private static final String TEST_RUN_BY_ID_PATH = "/api/tests/runs/%d";
	private static final String TEST_RUN_EMAIL_PATH = "/api/tests/runs/%d/email?filter=%s&showStacktrace=%s";
	private static final String EVENTS_PATH = "/api/events";
	private static final String EVENTS_RECEIVED_PATH = "/api/events/received";
	private static final String UA_INSPECTIONS_PATH = "/api/uainspections";

	private String serviceURL;
	private Client client;
	private String authToken;
	private String project;
	
	public ZafiraClient(String serviceURL)
	{
		this.serviceURL = serviceURL;
		this.client = Client.create();
		this.client.setConnectTimeout(CONNECT_TIMEOUT);
		this.client.setReadTimeout(READ_TIMEOUT);
	}
	
	public void setAuthToken(String authToken) 
	{
		this.authToken = authToken;
	}
	
	public boolean isAvailable()
	{
		boolean isAvailable = false;
		try
		{
			WebResource webResource = client.resource(serviceURL + STATUS_PATH);
			ClientResponse clientRS = webResource.get(ClientResponse.class);
			if (clientRS.getStatus() == 200)
			{
				isAvailable = true;
			}

		} catch (Exception e)
		{
			LOGGER.error("Unable to send ping", e);
		}
		return isAvailable;
	}
	
	public synchronized Response<UserType> getUserProfile()
	{
		Response<UserType> response = new Response<UserType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + PROFILE_PATH);
			ClientResponse clientRS =  initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(UserType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error("Unable to authorize user", e);
		}
		return response;
	}
	
	public synchronized Response<AuthTokenType> login(String username, String password)
	{
		Response<AuthTokenType> response = new Response<AuthTokenType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + LOGIN_PATH);
			ClientResponse clientRS =  initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, new CredentialsType(username, password));
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(AuthTokenType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error("Unable to login", e);
		}
		return response;
	}
	
	public synchronized Response<AuthTokenType> refreshToken(String token)
	{
		Response<AuthTokenType> response = new Response<AuthTokenType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + REFRESH_TOKEN_PATH);
			ClientResponse clientRS =  initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, new RefreshTokenType(token));
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(AuthTokenType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error("Unable to create user", e);
		}
		return response;
	}
	
	public synchronized Response<UserType> createUser(UserType user)
	{
		Response<UserType> response = new Response<UserType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + USERS_PATH);
			ClientResponse clientRS =  initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).put(ClientResponse.class, user);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(UserType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error("Unable to create user", e);
		}
		return response;
	}

	public synchronized Response<JobType> createJob(JobType job)
	{
		Response<JobType> response = new Response<JobType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + JOBS_PATH);
			ClientResponse clientRS = initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, job);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(JobType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error("Unable to create job", e);
		}
		return response;
	}
	
	public synchronized Response<TestSuiteType> createTestSuite(TestSuiteType testSuite)
	{
		Response<TestSuiteType> response = new Response<TestSuiteType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + TEST_SUITES_PATH);
			ClientResponse clientRS = initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, testSuite);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(TestSuiteType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error("Unable to create test suite", e);
		}
		return response;
	}
	
	public Response<TestRunType> startTestRun(TestRunType testRun)
	{
		Response<TestRunType> response = new Response<TestRunType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + TEST_RUNS_PATH);
			ClientResponse clientRS = initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, testRun);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(TestRunType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error("Unable to start test run", e);
		}
		return response;
	}
	
	public Response<TestRunType> updateTestRun(TestRunType testRun)
	{
		Response<TestRunType> response = new Response<TestRunType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + TEST_RUNS_PATH);
			ClientResponse clientRS = initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).put(ClientResponse.class, testRun);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(TestRunType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error("Unable to update test run", e);
		}
		return response;
	}
	
	public Response<TestRunType> finishTestRun(long id)
	{
		Response<TestRunType> response = new Response<TestRunType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + String.format(TEST_RUNS_FINISH_PATH, id));
			ClientResponse clientRS = initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(TestRunType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error("Unable to finish test run", e);
		}
		return response;
	}
	
	public Response<TestRunType> getTestRun(long id)
	{
		Response<TestRunType> response = new Response<TestRunType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + String.format(TEST_RUN_BY_ID_PATH, id));
			ClientResponse clientRS = initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(TestRunType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error("Unable to find test run by id", e);
		}
		return response;
	}
	
	public Response<String> sendTestRunReport(long id, String recipients, boolean showOnlyFailures, boolean showStacktrace)
	{
		Response<String> response = new Response<String>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + String.format(TEST_RUN_EMAIL_PATH, id, showOnlyFailures ? "failures" : "all", showStacktrace ? "true" : "false"));
			ClientResponse clientRS = initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.TEXT_HTML_TYPE).post(ClientResponse.class, new EmailType(recipients));
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(String.class));
			}

		} catch (Exception e)
		{
			LOGGER.error("Unable to send test run report", e);
		}
		return response;
	}
	
	public Response<TestRunType> getTestRunByCiRunId(String ciRunId)
	{
		Response<TestRunType> response = new Response<TestRunType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + TEST_RUNS_PATH);
			ClientResponse clientRS = initHeaders(webResource.queryParam("ciRunId", ciRunId).type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(TestRunType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error("Unable find test run by CI run id", e);
		}
		return response;
	}
	
	public Response<TestType> startTest(TestType test)
	{
		Response<TestType> response = new Response<TestType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + TESTS_PATH);
			ClientResponse clientRS = initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, test);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(TestType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error("Unable to start test", e);
		}
		return response;
	}
	
	public Response<TestType> finishTest(TestType test)
	{
		Response<TestType> response = new Response<TestType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + String.format(TEST_FINISH_PATH, test.getId()));
			ClientResponse clientRS = initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, test);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(TestType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error("Unable to finish test", e);
		}
		return response;
	}
	
	public void deleteTest(long id)
	{
		try
		{
			WebResource webResource = client.resource(serviceURL + String.format(TEST_BY_ID_PATH, id));
			webResource.delete(ClientResponse.class);

		} catch (Exception e)
		{
			LOGGER.error("Unable to finish test", e);
		}
	}
	
	public Response<TestType> createTestWorkItems(long testId, List<String> workItems)
	{
		Response<TestType> response = new Response<TestType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + String.format(TEST_WORK_ITEMS_PATH, testId));
			ClientResponse clientRS = initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, workItems);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(TestType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error("Unable to create test work items", e);
		}
		return response;
	}
	
	public synchronized Response<TestCaseType> createTestCase(TestCaseType testCase)
	{
		Response<TestCaseType> response = new Response<TestCaseType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + TEST_CASES_PATH);
			ClientResponse clientRS = initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, testCase);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(TestCaseType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error("Unable to create test case", e);
		}
		return response;
	}
	
	public Response<TestCaseType []> createTestCases(TestCaseType [] testCases)
	{
		Response<TestCaseType []> response = new Response<TestCaseType []>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + TEST_CASES_BATCH_PATH);
			ClientResponse clientRS = initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, testCases);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(TestCaseType [].class));
			}

		} catch (Exception e)
		{
			LOGGER.error("Unable to create test cases", e);
		}
		return response;
	}
	
	public Response<TestType []> getTestRunResults(long id)
	{
		Response<TestType []> response = new Response<TestType []>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + String.format(TEST_RUNS_RESULTS_PATH, id));
			ClientResponse clientRS = initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(TestType [].class));
			}

		} catch (Exception e)
		{
			LOGGER.error("Unable to find test run results", e);
		}
		return response;
	}
	
	public Response<EventType> logEvent(EventType event)
	{
		Response<EventType> response = new Response<EventType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + EVENTS_PATH);
			ClientResponse clientRS = initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, event);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(EventType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error("Unable to log event", e);
		}
		return response;
	}
	
	public void markEventReceived(EventType event)
	{
		try
		{
			WebResource webResource = client.resource(serviceURL + EVENTS_RECEIVED_PATH);
			initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).put(ClientResponse.class, event);
		} catch (Exception e)
		{
			LOGGER.error("Unable to mark event received", e);
		}
	}
	
	public class Response<T>
	{
		private int status;
		private T object;
		
		public Response(int status, T object)
		{
			this.status = status;
			this.object = object;
		}

		public int getStatus()
		{
			return status;
		}

		public void setStatus(int status)
		{
			this.status = status;
		}

		public T getObject()
		{
			return object;
		}

		public void setObject(T object)
		{
			this.object = object;
		}
	}
	
	private WebResource.Builder initHeaders(WebResource.Builder builder)
	{
		if(!StringUtils.isEmpty(authToken))
		{
			builder.header("Authorization", authToken);
		}
		if(!StringUtils.isEmpty(project))
		{
			builder.header("Project", project);
		}
		return builder;
	}

	public String getProject()
	{
		return project;
	}

	public ZafiraClient setProject(String project)
	{
		this.project = project;
		return this;
	}
	
	/**
	 * Registers user in Zafira, it may be a new one or existing returned by service.
	 * 
	 * @param userName - in general LDAP user name
	 * @param email
	 * @param firstName
	 * @param lastName
	 * @return registered user
	 */
	public UserType registerUser(String userName, String email, String firstName, String lastName) 
	{
		if (StringUtils.isEmpty(userName) || userName.equals("$BUILD_USER_ID"))
		{
			userName = ANONYMOUS;
		}
		userName = userName.toLowerCase();
		
		String userDetails = "userName: %s, email: %s, firstName: %s, lastName: %s";
		LOGGER.debug("User details for registration:" + String.format(userDetails, userName, email, firstName, lastName));
		
		UserType user = new UserType(userName, email, firstName, lastName);
		Response<UserType> response = createUser(user);
		user = response.getObject();

		if (user == null) 
		{
			throw new RuntimeException("Unable to register user '" + userName + "' for zafira service: " + serviceURL);
		} 
		else 
		{
			LOGGER.debug("Registered user details:" + String.format(userDetails, user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName()));
		}
		return user;
	}
	
	/**
	 * Registers test case in Zafira, it may be a new one or existing returned by service. 
	 * 
	 * @param suiteId
	 * @param primaryOwnerId
	 * @param secondaryOwnerId
	 * @param testClass
	 * @param testMethod
	 * @return registred test case
	 */
	public TestCaseType registerTestCase(Long suiteId, Long primaryOwnerId, Long secondaryOwnerId, String testClass, String testMethod) 
	{
		TestCaseType testCase = new TestCaseType(testClass, testMethod, "", suiteId, primaryOwnerId, secondaryOwnerId);
		String testCaseDetails = "testClass: %s, testMethod: %s, info: %s, testSuiteId: %d, primaryOwnerId: %d, secondaryOwnerId: %d";
		LOGGER.debug("Test Case details for registration:" + String.format(testCaseDetails, testClass, testMethod, "", suiteId, primaryOwnerId, secondaryOwnerId));
		Response<TestCaseType> response = createTestCase(testCase);
		testCase = response.getObject();
		if (testCase == null) 
		{
			throw new RuntimeException("Unable to register test case '"
					+ String.format(testCaseDetails, testClass, testMethod, "", suiteId, primaryOwnerId) + "' for zafira service: " + serviceURL);
		} 
		else 
		{
			LOGGER.debug("Registered test case details:" + String.format(testCaseDetails, testClass, testMethod, "", suiteId, primaryOwnerId, secondaryOwnerId));
		}
		return testCase;
	}
	
	/**
	 * Registers test work items.
	 * 
	 * @param testId
	 * @param workItems
	 * @return test for which we registers work items.
	 */
	public TestType registerWorkItems(Long testId, List<String> workItems) 
	{
		TestType test = null;
		if(workItems != null && workItems.size() > 0)
		{
			Response<TestType> response = createTestWorkItems(testId, workItems);
			test = response.getObject();
		}
		return test;
	}
	
	/**
	 * Registers test suite in Zafira, it may be a new one or existing returned by service.
	 * 
	 * @param suiteName - test suite name
	 * @param fileName - TestNG xml file name
	 * @param userId - suite owner user id
	 * @return created test suite
	 */
	public TestSuiteType registerTestSuite(String suiteName, String fileName, Long userId) 
	{
		TestSuiteType testSuite = new TestSuiteType(suiteName, fileName, userId);
		String testSuiteDetails = "suiteName: %s, fileName: %s, userId: %s";
		LOGGER.debug("Test Suite details for registration:" + String.format(testSuiteDetails, suiteName, fileName, userId));
		
		Response<TestSuiteType> response = createTestSuite(testSuite);
		testSuite = response.getObject();

		if (testSuite == null) 
		{
			throw new RuntimeException("Unable to register test suite '" + suiteName + "' for zafira service: " + serviceURL);
		} 
		else 
		{
			LOGGER.debug("Registered test suite details:" + String.format(testSuiteDetails, testSuite.getName(), testSuite.getFileName(), testSuite.getUserId()));
		}
		return testSuite;
	}
	
	/**
	 * Registers job in Zafira, it may be a new one or existing returned by service.
	 * 
	 * @param jobUrl - CI job URL
	 * @param userId - job owner user id 
	 * @return created job
	 */
	public JobType registerJob(String jobUrl, Long userId) 
	{
		String jobName = StringUtils.substringAfterLast(jobUrl, "/");
		String jenkinsHost = StringUtils.EMPTY;
		if(jobUrl.contains("/view/"))
		{
			jenkinsHost = jobUrl.split("/view/")[0];
		}
		else if(jobUrl.contains("/job/"))
		{
			jenkinsHost = jobUrl.split("/job/")[0];
		}
		
		String jobDetails = "jobName: %s, jenkinsHost: %s, userId: %s";
		LOGGER.debug("Job details for registration:" + String.format(jobDetails, jobName, jenkinsHost, userId));
		
		JobType job = new JobType(jobName, jobUrl, jenkinsHost, userId);
		Response<JobType> response = createJob(job);
		job = response.getObject();

		if (job == null) 
		{
			throw new RuntimeException("Unable to register job for zafira service: " + serviceURL);
		} else 
		{
			LOGGER.debug("Registered job details:" + String.format(jobDetails, job.getName(), job.getJenkinsHost(), job.getUserId()));
		}

		return job;
	}
	
	/**
	 * Registers new test run triggered by human.
	 * 
	 * @param testSuiteId
	 * @param userId
	 * @param configXML
	 * @param jobId
	 * @param ciConfig
	 * @param startedBy
	 * @param workItem
	 * @param classMode
	 * @return created test run
	 */
	public TestRunType registerTestRunByHUMAN(Long testSuiteId, Long userId, String configXML, Long jobId, CIConfig ciConfig, Initiator startedBy, String workItem, DriverMode driverMode) 
	{
		TestRunType testRun = new TestRunType(ciConfig.getCiRunId(), testSuiteId, userId, ciConfig.getGitUrl(), ciConfig.getGitBranch(), ciConfig.getGitCommit(), configXML, jobId, ciConfig.getCiBuild(), startedBy, workItem);
		testRun.setDriverMode(driverMode);
		String testRunDetails = "testSuiteId: %s, userId: %s, scmURL: %s, scmBranch: %s, scmCommit: %s, jobId: %s, buildNumber: %s, startedBy: %s, workItem";
		LOGGER.debug("Test Run details for registration:" + String.format(testRunDetails, testSuiteId, userId, ciConfig.getGitUrl(), ciConfig.getGitBranch(), ciConfig.getGitCommit(), jobId, ciConfig.getCiBuild(), startedBy, workItem));
		
		Response<TestRunType> response = startTestRun(testRun);
		testRun = response.getObject();
		if (testRun == null) 
		{
			throw new RuntimeException("Unable to register test run '" + String.format(testRunDetails, testSuiteId, userId,
					ciConfig.getGitUrl(), ciConfig.getGitBranch(), ciConfig.getGitCommit(), jobId, ciConfig.getCiBuild(), startedBy, workItem) + "' for zafira service: " + serviceURL);
		} else {
			LOGGER.debug("Registered test run details:" 
							+ String.format(testRunDetails, testSuiteId, userId, ciConfig.getGitUrl(), ciConfig.getGitBranch(), ciConfig.getGitCommit(), jobId, ciConfig.getCiBuild(), startedBy, workItem));
		}
		return testRun;
	}

	/**
	 * Registers new test run triggered by scheduler.
	 * 
	 * @param testSuiteId
	 * @param configXML
	 * @param jobId
	 * @param ciConfig
	 * @param startedBy
	 * @param workItem
	 * @param classMode
	 * @return created test run
	 */
	public TestRunType registerTestRunBySCHEDULER(Long testSuiteId, String configXML, Long jobId, CIConfig ciConfig, Initiator startedBy, String workItem, DriverMode driverMode) 
	{
		TestRunType testRun = new TestRunType(ciConfig.getCiRunId(), testSuiteId, ciConfig.getGitUrl(), ciConfig.getGitBranch(), ciConfig.getGitCommit(), configXML, jobId, ciConfig.getCiBuild(), startedBy, workItem);
		testRun.setDriverMode(driverMode);
		String testRunDetails = "testSuiteId: %s, scmURL: %s, scmBranch: %s, scmCommit: %s, jobId: %s, buildNumber: %s, startedBy: %s, workItem";
		LOGGER.debug("Test Run details for registration:" + String.format(testRunDetails, testSuiteId, ciConfig.getGitUrl(), ciConfig.getGitBranch(), ciConfig.getGitCommit(), jobId, ciConfig.getCiBuild(), startedBy, workItem));

		Response<TestRunType> response = startTestRun(testRun);
		testRun = response.getObject();
		if (testRun == null) 
		{
			throw new RuntimeException("Unable to register test run '"
							+ String.format(testRunDetails, testSuiteId, ciConfig.getGitUrl(), ciConfig.getGitBranch(), ciConfig.getGitCommit(), jobId, ciConfig.getCiBuild(), startedBy, workItem)
							+ "' for zafira service: " + serviceURL);
		} 
		else 
		{
			LOGGER.debug("Registered test run details:" + String.format(testRunDetails, testSuiteId, ciConfig.getGitUrl(), ciConfig.getGitBranch(), ciConfig.getGitCommit(), jobId, ciConfig.getCiBuild(), startedBy, workItem));
		}
		return testRun;
	}

	/**
	 * Registers new test run triggered by upstream job.
	 * 
	 * @param testSuiteId
	 * @param configXML
	 * @param jobId
	 * @param parentJobId
	 * @param ciConfig
	 * @param startedBy
	 * @param workItem
	 * @param classMode
	 * @return created test run
	 */
	public TestRunType registerTestRunUPSTREAM_JOB(Long testSuiteId, String configXML, Long jobId, Long parentJobId, CIConfig ciConfig, Initiator startedBy, String workItem, DriverMode driverMode) 
	{
		TestRunType testRun = new TestRunType(ciConfig.getCiRunId(), testSuiteId, ciConfig.getGitUrl(), ciConfig.getGitBranch(), ciConfig.getGitCommit(), configXML, jobId, parentJobId, ciConfig.getCiParentBuild(),
				ciConfig.getCiBuild(), startedBy, workItem);
		testRun.setDriverMode(driverMode);
		String testRunDetails = "testSuiteId: %s, scmURL: %s, scmBranch: %s, scmCommit: %s, jobId: %s, parentJobId: %s, parentBuildNumber: %s, buildNumber: %s, startedBy: %s, workItem";
		LOGGER.debug("Test Run details for registration:"
				+ String.format(testRunDetails, testSuiteId, ciConfig.getGitUrl(), ciConfig.getGitBranch(), ciConfig.getGitCommit(), jobId, parentJobId, ciConfig.getCiParentBuild(), ciConfig.getCiBuild(), startedBy, workItem));

		Response<TestRunType> response = startTestRun(testRun);
		testRun = response.getObject();
		if (testRun == null) 
		{
			throw new RuntimeException("Unable to register test run '"
					+ String.format(testRunDetails, testSuiteId, ciConfig.getGitUrl(), ciConfig.getGitBranch(), ciConfig.getGitCommit(), jobId, parentJobId, ciConfig.getCiParentBuild(), ciConfig.getCiBuild(), startedBy, workItem) 
							+ "' for zafira service: " + serviceURL);
		} 
		else 
		{
			LOGGER.debug("Registered test run details:" + String.format(testRunDetails, testSuiteId, ciConfig.getGitUrl(), ciConfig.getGitBranch(), ciConfig.getGitCommit(), jobId,parentJobId, ciConfig.getCiParentBuild(), ciConfig.getCiBuild(), startedBy, workItem));
		}
		return testRun;
	}
	
	/**
	 * Finalizes test run calculating test results.
	 * 
	 * @param testRun
	 * @return updated test run
	 */
	public TestRunType registerTestRunResults(TestRunType testRun)
	{
		updateTestRun(testRun); 
		Response<TestRunType> response = finishTestRun(testRun.getId());
		return response.getObject();
	}
	
	/**
	 * Registers test run in Zafira.
	 * 
	 * @param name
	 * @param group
	 * @param status
	 * @param testArgs
	 * @param testRunId
	 * @param testCaseId
	 * @param demoURL
	 * @param logURL
	 * @param retry
	 * @return registered test
	 */
	public TestType registerTestStart(String name, String group, Status status, String testArgs, Long testRunId, Long testCaseId, int retry, String configXML, String [] dependsOnMethods)
	{
		Long startTime = new Date().getTime();

		String testDetails = "name: %s, status: %s, testArgs: %s, testRunId: %s, testCaseId: %s, startTime: %s, retry: %d";

		TestType test = new TestType(name, status, testArgs, testRunId, testCaseId, startTime, null, retry, configXML);
		LOGGER.debug("Test details for startup registration:" + String.format(testDetails, name, status, testArgs, testRunId, testCaseId, startTime, retry));

		test.setTestGroup(group);
		if(dependsOnMethods != null)
		{
			StringBuilder sb = new StringBuilder();
			for(String method : Arrays.asList(dependsOnMethods))
			{
				sb.append(StringUtils.substringAfterLast(method, ".") + StringUtils.SPACE);
			}
			test.setDependsOnMethods(sb.toString());
		}
		
		Response<TestType> response = startTest(test);
		test = response.getObject();
		if (test == null) 
		{
			throw new RuntimeException("Unable to register test '" + String.format(testDetails, name, status, testArgs, testRunId, testCaseId, startTime, retry) + "' startup for zafira service: " + serviceURL);
		} 
		else 
		{
			LOGGER.debug("Registered test startup details:" + String.format(testDetails, name, status, testArgs, testRunId, testCaseId, startTime, retry));
		}
		return test;
	}
	
	/**
	 * Registers test re-run in Zafira.
	 * 
	 * @param test
	 * @return registered test
	 */
	public TestType registerTestRestart(TestType test) 
	{
		String testName = test.getName();
		Response<TestType> response = startTest(test);
		test = response.getObject();
		if (test == null) 
		{
			throw new RuntimeException("Unable to register test '" + testName + "' restart for zafira service: " + serviceURL);
		} 
		else 
		{
			LOGGER.debug("Registered test restart details:'" + testName + "'; startTime: " + new Date(test.getStartTime()));
		}
		return test;
	}
	
	/**
	 * Registers UI inspection.
	 * 
	 * @param uiInspection
	 * @return status
	 */
	public boolean createUAInspection(UAInspectionType uiInspection)
	{
		boolean created = false;
		try
		{
			WebResource webResource = client.resource(serviceURL + UA_INSPECTIONS_PATH);
			ClientResponse clientRS =  initHeaders(webResource.type(MediaType.APPLICATION_JSON)).post(ClientResponse.class, uiInspection);
			created = clientRS.getStatus() == 200;

		} catch (Exception e)
		{
			LOGGER.error("Unable to create UI inspection", e);
		}
		return created;
	}
	
	/**
	 * Aborts test run.
	 * 
	 * @param id of test run
	 * @return status
	 */
	public boolean abortTestRun(long id)
	{
		boolean aborted = false;
		try
		{
			WebResource webResource = client.resource(serviceURL + String.format(TEST_RUNS_ABORT_PATH, id));
			ClientResponse clientRS = initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
			aborted = clientRS.getStatus() == 200;
		} catch (Exception e)
		{
			LOGGER.error("Unable to find test run by id", e);
		}
		return aborted;
	}
}
