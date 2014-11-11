## Zafira API

<b>Base URL: http://91.194.250.224:8080/zafira<b>

### POST: /jobs

```
>>>
{
	"jobURL": "http://stg.caronfly.com:8081/view/zafira/job/zafira-ws",
	"userName": "akhursevich"
}

<<<
{
   "id": 1,
   "name": "zafira-ws",
   "jobURL": "http://91.194.250.224:8080/job/zafira-ws",
   "jenkinsHost": "http://stg.caronfly.com:8081",
   "userName": "akhursevich"
}
```
#### POST: /tests/suites

```
>>>
{
	"name": "sanity",
	"description": "Test it!",
	"userName": "akhursevich"
}

<<<
{
   "id": 1,
   "name": "sanity",
   "description": "Test it!",
   "userName": "akhursevich"
}
```

#### POST: /tests/cases
```
>>>
[
	{
		"testClass": "com.qaprosoft.Zafira",
		"testMethod": "testMe",
		"info": "Haha!",
		"testSuiteId": 1,
		"userName": "akhursevich"
	},
	{
		"testClass": "com.qaprosoft.Zafira1",
		"testMethod": "testMe1",
		"info": "Haha!",
		"testSuiteId": 1,
		"userName": "akhursevich"
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
      "userName": "akhursevich"
   },
      {
      "id": 2,
      "testClass": "com.qaprosoft.Zafira1",
      "testMethod": "testMe1",
      "info": "Haha!",
      "testSuiteId": 1,
      "userName": "akhursevich"
   }
]
```

#### POST: /tests/runs
```
>>>
{
	"testSuiteId": 1,
	"jobId": 1,
	"buildNumber": 2,
	"scmURL": "git@github.com:qaprosoft/zafira.git",
	"scmBranch": "master",
	"scmRevision": "uk2s34f2s44s23hhjsfsdf",
	"configXML": "<config><arg><key>url</key><value>http://localhost:8080</value></arg></config>",
	"startedBy": "UPSTREAM_JOB",
	"upstreamJobBuildNumber": 1,
	"upstreamJobId": 2
}

<<<
{
   "id": 2,
   "testSuiteId": 1,
   "scmURL": "git@github.com:qaprosoft/zafira.git",
   "scmBranch": "master",
   "scmRevision": "uk2s34f2s44s23hhjsfsdf",
   "configXML": "<config><arg><key>url<\/key><value>http://localhost:8080<\/value><\/arg><\/config>",
   "jobId": 1,
   "upstreamJobId": 2,
   "upstreamJobBuildNumber": 1,
   "buildNumber": "2",
   "startedBy": "UPSTREAM_JOB"
}
```

#### POST: /tests/runs/{id}/finish
```
>>>
// no body

<<<
{
   "id": 2,
   "testSuiteId": 1,
   "status": "PASSED",
   "scmURL": "git@github.com:qaprosoft/zafira.git",
   "scmBranch": "master",
   "scmRevision": "uk2s34f2s44s23hhjsfsdf",
   "configXML": "<config><arg><key>url<\/key><value>http://localhost:8080<\/value><\/arg><\/config>",
   "jobId": 1,
   "upstreamJobId": 2,
   "upstreamJobBuildNumber": 1,
   "buildNumber": "2",
   "startedBy": "UPSTREAM_JOB"
}
```
