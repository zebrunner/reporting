package com.qaprosoft.zafira.client;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qaprosoft.zafira.client.model.JobType;
import com.qaprosoft.zafira.client.model.TestCaseType;
import com.qaprosoft.zafira.client.model.TestRunType;
import com.qaprosoft.zafira.client.model.TestSuiteType;
import com.qaprosoft.zafira.client.model.TestType;
import com.qaprosoft.zafira.client.model.UserType;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class ZafiraClient
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ZafiraClient.class);
	
	private static final Integer TIMEOUT = 15 * 1000;
	
	private static final String STATUS_PATH = "/status";
	private static final String USERS_PATH = "/users";
	private static final String JOBS_PATH = "/jobs";
	private static final String TESTS_PATH = "/tests";
	private static final String TEST_WORK_ITEMS_PATH = "/tests/%d/workitems";
	private static final String TEST_SUITES_PATH = "/tests/suites";
	private static final String TEST_CASES_PATH = "/tests/cases";
	private static final String TEST_CASES_BATCH_PATH = "/tests/cases/batch";
	private static final String TEST_RUNS_PATH = "/tests/runs";
	private static final String TEST_RUNS_FINISH_PATH = "/tests/runs/%d/finish";

	private String serviceURL;
	private Client client;
	
	public ZafiraClient(String serviceURL)
	{
		this.serviceURL = serviceURL;
		this.client = Client.create();
		this.client.setConnectTimeout(TIMEOUT);
		this.client.setReadTimeout(TIMEOUT);
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
			LOGGER.error(e.getMessage());
		}
		return isAvailable;
	}
	
	public synchronized Response<UserType> createUser(UserType user)
	{
		Response<UserType> response = new Response<UserType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + USERS_PATH);
			ClientResponse clientRS = webResource.type(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, user);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(UserType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error(e.getMessage());
		}
		return response;
	}

	public synchronized Response<JobType> createJob(JobType job)
	{
		Response<JobType> response = new Response<JobType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + JOBS_PATH);
			ClientResponse clientRS = webResource.type(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, job);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(JobType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error(e.getMessage());
		}
		return response;
	}
	
	public synchronized Response<TestSuiteType> createTestSuite(TestSuiteType testSuite)
	{
		Response<TestSuiteType> response = new Response<TestSuiteType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + TEST_SUITES_PATH);
			ClientResponse clientRS = webResource.type(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, testSuite);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(TestSuiteType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error(e.getMessage());
		}
		return response;
	}
	
	public Response<TestRunType> createTestRun(TestRunType testRun)
	{
		Response<TestRunType> response = new Response<TestRunType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + TEST_RUNS_PATH);
			ClientResponse clientRS = webResource.type(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, testRun);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(TestRunType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error(e.getMessage());
		}
		return response;
	}
	
	public Response<TestRunType> finishTestRun(long id)
	{
		Response<TestRunType> response = new Response<TestRunType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + String.format(TEST_RUNS_FINISH_PATH, id));
			ClientResponse clientRS = webResource.type(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(TestRunType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error(e.getMessage());
		}
		return response;
	}
	
	public Response<TestType> createTest(TestType test)
	{
		Response<TestType> response = new Response<TestType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + TESTS_PATH);
			ClientResponse clientRS = webResource.type(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, test);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(TestType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error(e.getMessage());
		}
		return response;
	}
	
	public Response<TestType> createTestWorkItems(long testId, List<String> workItems)
	{
		Response<TestType> response = new Response<TestType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + String.format(TEST_WORK_ITEMS_PATH, testId));
			ClientResponse clientRS = webResource.type(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, workItems);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(TestType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error(e.getMessage());
		}
		return response;
	}
	
	public synchronized Response<TestCaseType> createTestCase(TestCaseType testCase)
	{
		Response<TestCaseType> response = new Response<TestCaseType>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + TEST_CASES_PATH);
			ClientResponse clientRS = webResource.type(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, testCase);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(TestCaseType.class));
			}

		} catch (Exception e)
		{
			LOGGER.error(e.getMessage());
		}
		return response;
	}
	
	public Response<TestCaseType []> createTestCases(TestCaseType [] testCases)
	{
		Response<TestCaseType []> response = new Response<TestCaseType []>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + TEST_CASES_BATCH_PATH);
			ClientResponse clientRS = webResource.type(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, testCases);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(TestCaseType [].class));
			}

		} catch (Exception e)
		{
			LOGGER.error(e.getMessage());
		}
		return response;
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
}
