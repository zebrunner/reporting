## Zafira API

<b> Base URL: http://91.194.250.224:8080/zafira <b>

### POST: /jobs
```
Request:
{
	"jobURL": "http://stg.caronfly.com:8081/view/zafira/job/zafira-ws"
}

Response:
{
   "id": 1,
   "name": "zafira-ws",
   "jobURL": "http://91.194.250.224:8080/job/zafira-ws",
   "jenkinsHost": "http://stg.caronfly.com:8081"
}
```
#### POST: /tests/cases
```
Request:
{
	"name": "sanity",
	"description": "Test it!",
	"userName": "akhursevich"
}

Response:
{
   "id": 1,
   "name": "sanity",
   "description": "Test it!",
   "userName": "akhursevich"
}
```

#### POST: /tests/suites

