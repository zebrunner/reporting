# Zafira's minimal http flow
---
Step 1: Preconditions and refresh token
---

First, login into Zafira under your creds **via UI**. Then navigate to user settings (just click on your username on
top-right corner of the screen and click **"My profile"** button), where you can find **"Access token"** section.
Click on **"Generate"** button and you will obtain access token, whish expires in 5 hours.

Then, refresh your access token by **http request**, like this:

**Request**

**Method:** POST,
**URI:** ~/zafira-ws/api/auth/refresh
**Headers:** No
**Data:**

|Field|Datatype|Description|
|:---:|:------:|:---------:|
|refreshToken|String|token obtained by generation|

**Body example:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyIiwicGFzc3dvcmQiOiJPUG1zRDFrbTdvaGlIOGtWNjJ3TmRRb3Awb3BCWlBkdiIsInRlbmFudCI6InphZmlyYSIsImV4cCI6MTMwMzkzNjM5OTQ5fQ.pE7d-yr14QxlGSDaBQOn7mRR-2SGgr8RXvdTtZU3JHOEDw3QIVyEJYp-2sTlYiHzG5T_mfuvk7QnccdQZB0CHA"
}
```

**Response**

**Status code:** 200
**Response Body:**

|Field   | Datatype |  Description |
|:--------:|:----------:|:----------:|
|type| String   | Type of authorization |
|accessToken| String   |  Refreshed token we need for headers |
|expiresIn| int   | Expiration time of new token|
|refreshToken| String   | Token |
|tenant|String|Nodename of kubernetes|

**Body example:**
```json
{
  "type": "Bearer",
  "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyIiwidXNlcm5hbWUiOiJxcHNkZW1vIiwiZ3JvdXBJZHMiOlszXSwidGVuYW50IjoiemFmaXJhIiwiZXhwIjoxNTQ0NjM5MTQyfQ.H4JE7TEUodDEXpfipP7NjDWE0ORjGOCFgSFlQ0X5LfcUSVmQbebazh7C0_UBVmIZeBb6hqLNwEtqW07-mchwrQ",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyIiwicGFzc3dvcmQiOiJPUG1zRDFrbTdvaGlIOGtWNjJ3TmRRb3Awb3BCWlBkdiIsInRlbmFudCI6InphZmlyYSIsImV4cCI6MTU0NTIyNTk0Mn0.DcYuX4gQYuLeCFqf-fyZhr2Oe0D0ebsHmHPFyqFVWrlV6WtNEfDNjmjkm4XPumfcMD9lytWzzGwGecan3APf2g",
  "expiresIn": 300,
  "tenant": "zafira"
}
```

---
Step 2: Get user profile
---

**Request**

**Method:** GET,
**URI:** ~/zafira-ws/api/users/profile?username={username}
**Headers:**

|Field   | Datatype | Description |
|:--------:|:----------:|:----------:|
|Authorization| String   | Type + Access token |


**Data:** No

**URI example:** ~/zafira-ws/api/users/profile?username=qpsdemo

**Response**

**Status code:** 200
**Response Body:**

|Field   | Datatype |  Description |
|:--------:|:----------:|:----------:|
|id| int   | Unique user identifier |
|username| String   | Credentials |
|password| String   | Credentials|
|roles| String array   | user roles("ROLE_ADMIN", "ROLE_ADMIN", "ROLE_SUPERADMIN") |
|permissions|Object List   | List of user permissions |
|preferences|Object List   | List of user preferences |
|lastLogin|Date   | Date of users last login |
|source|Object   | "INTERNAL", "LDAP" |
|status|Object   | Logged in or not("ACTIVE","INACTIVE") |

**Body example:**
```json
{
  "id":2,
  "username":"admin",
  "password":"zRNkEjrbdBB70DVZA1leMqdJy+9uJCpK",
  "roles":["ROLE_ADMIN"],
  "permissions":[{
                    "id":1,
                    "modifiedAt":null,
                    "createdAt":null,
                    "name":"VIEW_HIDDEN_DASHBOARDS",
                    "block":null
                    }],
  "preferences":[{
                    "id":6,
                    "modifiedAt":1544195439376,
                    "createdAt":1544195439376,
                    "name":"THEME",
                    "value":"32"
                    }],
 "lastLogin":1544617640554,
 "source":"INTERNAL",
 "status":"ACTIVE"
 }
```

---
Step 3: Create a test suite
---
**Request**

**Method:** POST,
**URI:** ~/zafira-ws/api/tests/suites
**Headers:**

|Field   | Datatype | Description |
|:--------:|:----------:|:----------:|
|Authorization| String   | Type + Access token |

**Data:**

|Field   | Datatype | Description |
|:--------:|:----------:|:----------:|
|userId| int   | Unique user identifier|
|fileName| String   | Name of XML suite|
|name| String   | Suite name |

**Body example:**
```json
{
   "fileName": "ExampleFileName",
   "name": "ExampleSuiteName",
   "userId": 2
 }
```

**Response**

**Status code:** 200
**Response Body:**

|Field   | Datatype | Description |
|:--------:|:----------:|:----------:|
|id| int   | Unique test suite identifier|
|name| String   | Suite name|
|fileName| String   | Name of XML suite|
|userId| int   | Unique user identifier|

**Body example:**
```json
{
  "id": 8,
  "name": "ExampleSuiteName",
  "fileName": "ExampleFileName",
  "userId": 2
}
```

---
Step 4: Create a job
---
**Request**

**Method:** POST,
**URI:** ~/zafira-ws/api/jobs
**Headers:**

|Field   | Datatype | Description |
|:--------:|:----------:|:----------:|
|Authorization| String   | Type + Access token |

**Data:**

|Field   | Datatype | Description |
|:--------:|:----------:|:----------:|
|jenkinsHost| String   | Jenkins host |
|jobURL| String   | Job url |
|name| String   | Job name|
|userId| int   |Unique test suite identifier |

**Body example:**
```json
{
   "jenkinsHost": "ExampleJenkinsHost",
   "jobURL": "ExampleJobURL",
   "name": "ExampleJobName",
   "userId": 2
 }
```

**Response**

**Status code:** 200
**Response Body:**

|Field   | Datatype | Description |
|:--------:|:----------:|:----------:|
|id| int   | Unique job identifier|
|jenkinsHost| String   | Jenkins host |
|jobURL| String   | Job url |
|name| String   | Job name |
|userId| int   | Unique user identifier|

**Body example:**
```json
{
  "id": 8,
  "name": "ExampleJobName",
  "jobURL": "ExampleJobURL",
  "jenkinsHost": "ExampleJenkinsHost",
  "userId": 2
}
```

---
Step 5: Create a test run
---
Registers a test run on Zafira UI
**Request**

**Method:** POST,
**URI:** ~/zafira-ws/api/tests/runs
**Headers:**

|Field   | Datatype | Description |
|:--------:|:----------:|:----------:|
|Authorization| String   | Type + Access token|

**Data:**

|Field   | Datatype | Description |
|:--------:|:----------:|:----------:|
|jobId| int   |  Unique job identifier |
|testSuiteId| int   | Unique test suite identifier  |
|buildNumber| int   | Build number|
|startedBy| String   | One of the values : "SCHEDULER", "UPSTREAM_JOB", "HUMAN" |
|driverMode| String  | One of the values :  METHOD_MODE", "CLASS_MODE", "SUITE_MODE" |

**Body example:**
```json
{
   "buildNumber": 0,
   "driverMode": "METHOD_MODE",
   "jobId": 8,
   "startedBy": "HUMAN",
   "testSuiteId": 8
 }
```

**Response**

**Status code:** 200
**Response Body:**

|Field   | Datatype | Description |
|:--------:|:----------:|:----------:|
|id| int   | Unique test run identifier|
|ciRunId| String   | CI run id |
|jobId| int   | Job id |
|testSuiteId| int   | Unique test suite identifier |
|buildNumber| int   | Build number |
|blocker| bool   | Unexpected behavior that keeps you from performing all test case steps |
|driverMode| String   | Optional value : "METHOD_MODE", "CLASS_MODE", "SUITE_MODE"|
|knownIssue| bool   | Failing reason in ticket|
|reviewed|bool| Shows whether a test has been reviewed |
|status| String   | Test run status ("UNKNOWN", "IN_PROGRESS", "PASSED", "FAILED", "SKIPPED", "ABORTED", "QUEUED") |
|driverMode| String   | One of the values :  METHOD_MODE", "CLASS_MODE", "SUITE_MODE"|

**Body example:**
```json
{
   "id": 62,
   "ciRunId": "e6d88798f-d833-5sgfd-9127-56b602d82599",
   "testSuiteId": 8,
   "status": "IN_PROGRESS",
   "jobId": 8,
   "buildNumber": 0,
   "startedBy": "HUMAN",
   "knownIssue": false,
   "blocker": false,
   "driverMode": "METHOD_MODE",
   "reviewed": false
 }
```

---
Step 6: Create a test case
---
**Request**

**Method:** POST,
**URI:** ~/zafira-ws/api/tests/cases
**Headers:**

|Field   | Datatype | Description |
|:--------:|:----------:|:----------:|
|Authorization| String   |   Type + Access token |

**Data:**

|Field   | Datatype | Description |
|:--------:|:----------:|:----------:|
|primaryOwnerId| int   | Unique identifier of test case creator |
|testClass| String   | Name of test class |
|testMethod| String   | Name of test method|
|testSuiteId| int   | Unique test suite identifier |

**Body example:**
```json
{
   "primaryOwnerId": 2,
   "testClass": "ExampleTestClassName",
   "testMethod": "ExampleTestName",
   "testSuiteId": 8
 }
```

**Response**

**Status code:** 200
**Response Body:**

|Field   | Datatype | Description |
|:--------:|:----------:|:----------:|
|id| int   | Unique identifier of test case|
|primaryOwnerId| int   | Unique identifier of test case creator |
|testClass| String   | Name of test class  |
|testMethod| String   | Name of test method |
|testSuiteId| int   | Unique test suite identifier |

**Body example:**
```json
{
   "id": 23,
   "testClass": "ExampleTestClassName",
   "testMethod": "ExampleTestMethod",
   "testSuiteId": 8,
   "primaryOwnerId": 2
 }
```

---
Step 7: Start a test
---
Registers a test start on Zafira UI
**Request**

**Method:** POST,
**URI:** ~/zafira-ws/api/tests
**Headers:**

|Field   | Datatype | Description |
|:--------:|:----------:|:----------:|
|Authorization| String   |   Type + Access token |

**Data:**

|Field   | Datatype | Description |
|:--------:|:----------:|:----------:|
|testRunId| int   | Unique test run identifier |
|testCaseId| int   | Unique identifier of test case |
|name|String| Test name  |
|status|String| Test run status ("UNKNOWN", "IN_PROGRESS", "PASSED", "FAILED", "SKIPPED", "ABORTED", "QUEUED")  |

**Body example:**
```json
{
   "name": "ExampleTestName",
   "status": "IN_PROGRESS",
   "testCaseId": 23,
   "testRunId": 62
 }
```

**Response**

**Status code:** 200
**Response Body:**

|Field   | Datatype | Description |
|:--------:|:----------:|:----------:|
|id| int   | Unique test identifier|
|name|String|Test name |
|status|String|Test run status ("UNKNOWN", "IN_PROGRESS", "PASSED", "FAILED", "SKIPPED", "ABORTED", "QUEUED")  |
|testRunId| int   | Unique test run identifier|
|testCaseId| int   |  Unique identifier of test case |
|retry|int| Shows the count the test ran |
|knownIssue| bool   | Failing reason in ticket |
|blocker| bool   | Unexpected behavior that keeps you from performing all test case steps  |
|needRerun|bool| Flag that indicates whether a reran is needed |
|artifacts| Object array| List of test artifacts(logs, screenshots etc) |

**Body example:**
```json
{
  "id": 222,
  "name": "ExampleTestName",
  "status": "IN_PROGRESS",
  "testRunId": 62,
  "testCaseId": 8,
  "retry": 0,
  "knownIssue": false,
  "blocker": false,
  "needRerun": false,
  "artifacts": []
}
```

---
Step 8: Finish a test
---
**Request**

**Method:** POST,
**URI:** ~/zafira-ws/api/tests/{testId}/finish
**Headers:**

|Field   | Datatype | Description |
|:--------:|:----------:|:----------:|
|Authorization| String   |  Type + Access token |

**Data:**

|Field   | Datatype | Description |
|:--------:|:----------:|:----------:|
|id| int   |  Unique test identifier  |
|name|String| Test name |
|status|String| Test run status ("UNKNOWN", "IN_PROGRESS", "PASSED", "FAILED", "SKIPPED", "ABORTED", "QUEUED") |
|testRunId| int   | Unique test run identifier |
|testCaseId| int   |  Unique identifier of test case|
|retry|int| Shows the count the test ran |
|knownIssue| bool   | Failing reason in ticket   |
|blocker| bool   | Unexpected behavior that keeps you from performing all test case steps  |
|needRerun|bool| Flag that indicates whether a reran is needed |
|artifacts|Object array| List of test artifacts(logs, screenshots etc)|

**Request URI example:** ~/zafira-ws/api/tests/runs/222/finish

**Body example:**
```json
{
   "id": 222,
   "name": "ExampleTestName",
   "status": "FAILED",
   "testRunId": 62,
   "testCaseId": 8,
   "retry": 0,
   "knownIssue": true,
   "blocker": false,
   "needRerun": false,
   "artifacts": []
 }
```

**Response**

**Status code:** 200
**Response Body:**

|Field   | Datatype | Description |
|:--------:|:----------:|:----------:|
|id| int   | Unique test identifier |
|name|String|Test name |
|status|String| Test run status ("UNKNOWN", "IN_PROGRESS", "PASSED", "FAILED", "SKIPPED", "ABORTED", "QUEUED") |
|testRunId| int   | Unique test run identifier|
|testCaseId| int   | Unique identifier of test case|
|retry|int| Shows the count the test ran|
|knownIssue| bool   |Failing reason in ticket |
|blocker| bool   |Unexpected behavior that keeps you from performing all test case steps |
|needRerun|bool| Flag that indicates whether a reran is needed |
|artifacts|Object array| List of test artifacts(logs, screenshots etc)|

**Body example:**
```json
{
   "id": 222,
   "name": "ExampleTestName",
   "status": "FAILED",
   "testRunId": 62,
   "testCaseId": 8,
   "retry": 0,
   "knownIssue": true,
   "blocker": false,
   "needRerun": false,
   "artifacts": []
}
```
---
Step 9: Finish a test run
---
**Request**

**Method:** POST,
**URI:** ~/zafira-ws/api/tests/runs/{testRunId}/finish
**Headers:**

|Field   | Datatype | Description |
|:--------:|:----------:|:----------:|
|Authorization| String |  Type + Access token |

**URI example:** ~/zafira-ws/api/tests/runs/62/finish

**Body:** No


**Response**

**Status code:** 200
**Response Body:**

|Field   | Datatype | Description |
|:--------:|:----------:|:----------:|
|id| int   | Unique test run identifier|
|ciRunId| String   | CI run id |
|testSuiteId| int   |Unique test suite identifier |
|status| String   | Test run status ("UNKNOWN", "IN_PROGRESS", "PASSED", "FAILED", "SKIPPED", "ABORTED", "QUEUED")|
|jobId| int   | Job id|
|buildNumber| int   | Build number |
|startedBy|String| One of the values : "SCHEDULER", "UPSTREAM_JOB", "HUMAN"|
|knownIssue| bool   | Failing reason in ticket|
|blocker| bool   |Unexpected behavior that keeps you from performing all test case steps |
|driverMode| String   | One of the values :  METHOD_MODE", "CLASS_MODE", "SUITE_MODE"|
|reviewed|bool| Shows whether a test has been reviewed|

**Body example:**
```json
{
  "id": 62,
  "ciRunId": "e6d88798f-d833-5sgfd-9127-56b602d82599",
  "testSuiteId": 8,
  "status": "FAILED",
  "jobId": 8,
  "buildNumber": 0,
  "startedBy": "HUMAN",
  "knownIssue": true,
  "blocker": false,
  "driverMode": "METHOD_MODE",
  "reviewed": false
}
