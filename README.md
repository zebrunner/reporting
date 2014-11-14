## Zafira API

<b>Base URL: http://91.194.250.224:8080/zafira<b>

#### POST: /users

```
>>>
{
	"userName": "akhursevich",
	"email": "hursevich@gmail.com",
	"firstName": "Alex",
	"lastName": "Khursevich"
}

<<<
{
   "id": 1,
   "userName": "akhursevich",
   "email": "hursevich@gmail.com",
   "firstName": "Alex",
   "lastName": "Khursevich"
}
```

#### POST: /jobs

```
>>>
{
	"name": "copper-admin",
	"jobURL": "http://stg.caronfly.com:8081/view/copper/job/copper-admin",
	"jenkinsHost": "http://stg.caronfly.com:8081",
	"userId": 1
}

<<<
{
   "id": 1,
   "name": "copper-admin",
   "jobURL": "http://stg.caronfly.com:8081/view/copper/job/copper-admin",
   "jenkinsHost": "http://stg.caronfly.com:8081",
   "userId": 1
}
```

#### POST: /tests/suites

```
>>>
{
	"name": "regression",
	"description": "Regression tests",
	"userId": 1
}

<<<
{
   "id": 2,
   "name": "regression",
   "description": "Regression tests",
   "userId": 1
}
```

#### POST: /tests/cases
```
>>>
{
	"testClass": "com.qaprosoft.Zafira",
	"testMethod": "testZafira",
	"info": "Initial Zafira test!",
	"testSuiteId": 1,
	"userId": 1
}

<<<
{
   "id": 1,
   "testClass": "com.qaprosoft.Zafira",
   "testMethod": "testZafira",
   "info": "Initial Zafira test!",
   "testSuiteId": 1,
   "userId": 1
}
```

#### POST: /tests/cases/batch
```
>>>
[
	{
		"testClass": "com.qaprosoft.Zafira",
		"testMethod": "testMe",
		"info": "Haha!",
		"testSuiteId": 1,
		"userId": 1
	},
	{
		"testClass": "com.qaprosoft.Zafira1",
		"testMethod": "testMe1",
		"info": "Haha!",
		"testSuiteId": 1,
		"userId": 1
	}
]

<<<
[
      {
      "id": 1,
      "testClass": "com.qaprosoft.Zafira",
      "testMethod": "testMe",
      "info": "Haha!",
      "testSuiteId": 1,
      "userId": 1
   },
      {
      "id": 2,
      "testClass": "com.qaprosoft.Zafira1",
      "testMethod": "testMe1",
      "info": "Haha!",
      "testSuiteId": 1,
      "userId": 1
   }
]
```

#### POST: /tests/runs
```
>>>
{
	"testSuiteId": 1,
	"jobId": 1,
	"buildNumber": 3,
	"workItemJiraId": "JIRA-2142",
	"scmURL": "git@github.com:qaprosoft/zafira.git",
	"scmBranch": "master",
	"scmRevision": "uk2s34f2s44s23hhjsfsdf",
	"configXML": "<config><arg><key>url</key><value>http://localhost:8080</value></arg></config>",
	"startedBy": "HUMAN",
	"userId": 1
}

<<<
{
   "id": 1,
   "testSuiteId": 1,
   "status": "IN_PROGRESS",
   "scmURL": "git@github.com:qaprosoft/zafira.git",
   "scmBranch": "master",
   "scmRevision": "uk2s34f2s44s23hhjsfsdf",
   "configXML": "<config><arg><key>url<\/key><value>http://localhost:8080<\/value><\/arg><\/config>",
   "jobId": 1,
   "buildNumber": 3,
   "startedBy": "HUMAN",
   "userId": 1,
   "workItemJiraId": "JIRA-2142"
}
```

#### POST: /tests/runs/{id}/finish
```
>>>
// no body

<<<
{
   "id": 1,
   "testSuiteId": 1,
   "status": "PASSED",
   "scmURL": "git@github.com:qaprosoft/zafira.git",
   "scmBranch": "master",
   "scmRevision": "uk2s34f2s44s23hhjsfsdf",
   "configXML": "<config><arg><key>url<\/key><value>http://localhost:8080<\/value><\/arg><\/config>",
   "jobId": 1,
   "buildNumber": 3,
   "startedBy": "HUMAN",
   "userId": 1
}
```

#### POST: /tests

```
>>>
{
	"name": "Carina login test",
	"status": "FAILED",
	"testArgs": "<config><arg><key>password</key><value>qwerty</value></arg></config>",
	"testRunId": 1,
	"testCaseId": 2,
	"message": "Login failed!",
	"startTime": 1415746157780,
	"finishTime": 1415746154490,
	"demoURL": "http://localhost:8080/lc/demo",
	"logURL": "http://localhost:8080/lc/log"
}

<<<
{
   "id": 2,
   "name": "Carina login test",
   "status": "FAILED",
   "testArgs": "<config><arg><key>password</key><value>qwerty</value></arg></config>",
   "testRunId": 1,
   "testCaseId": 2,
   "message": "Login failed!",
   "startTime": 1415746157780,
   "finishTime": 1415746154490,
   "demoURL": "http://localhost:8080/lc/demo",
   "logURL": "http://localhost:8080/lc/log"
}
```
