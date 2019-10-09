/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.web;

import com.qaprosoft.zafira.models.db.AbstractEntity;
import com.qaprosoft.zafira.models.db.Job;
import com.qaprosoft.zafira.models.db.JobView;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.dto.JobType;
import com.qaprosoft.zafira.models.dto.JobUrlType;
import com.qaprosoft.zafira.models.dto.JobViewType;
import com.qaprosoft.zafira.service.JobsService;
import com.qaprosoft.zafira.service.TestRunService;
import com.qaprosoft.zafira.service.UserService;
import com.qaprosoft.zafira.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api("Jobs API")
@CrossOrigin
@RequestMapping(path = "api/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class JobController extends AbstractController {

    private final Mapper mapper;
    private final JobsService jobsService;
    private final TestRunService testRunService;
    private final UserService userService;

    public JobController(Mapper mapper, JobsService jobsService, TestRunService testRunService, UserService userService) {
        this.mapper = mapper;
        this.jobsService = jobsService;
        this.testRunService = testRunService;
        this.userService = userService;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Create job", nickname = "createJob", httpMethod = "POST", response = JobType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PostMapping()
    public JobType createJob(@RequestBody @Valid JobType job) {
        return mapper.map(jobsService.createOrUpdateJob(mapper.map(job, Job.class)), JobType.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Create job by url", nickname = "createJobByUrl", httpMethod = "POST", response = JobType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PostMapping("/url")
    public JobType createJobByUrl(@RequestBody @Valid JobUrlType jobUrl) {
        User user = userService.getUserById(getPrincipalId());
        return mapper.map(jobsService.createOrUpdateJobByURL(jobUrl.getJobUrlValue(), user), JobType.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get all jobs", nickname = "getAllJobs", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping()
    public List<Job> getAllJobs() {
        return jobsService.getAllJobs();
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get latest job test runs", nickname = "getLatestJobTestRuns", httpMethod = "POST", response = Map.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PostMapping("/views/{id}/tests/runs")
    public Map<Long, TestRun> getLatestJobTestRuns(@RequestParam("env") String env, @RequestBody @Valid List<JobViewType> jobViews) {
        List<Long> jobIds = jobViews.stream()
                                    .map(JobViewType::getJob)
                                    .map(AbstractEntity::getId)
                                    .collect(Collectors.toList());
        return testRunService.getLatestJobTestRuns(env, jobIds);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Create job view", nickname = "createJobViews", httpMethod = "POST", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PostMapping("/views")
    @Secured({ "ROLE_ADMIN" })
    public List<JobViewType> createJobViews(@RequestBody @Valid List<JobViewType> jobViews) {
        jobViews.forEach(jobView -> jobsService.createJobView(mapper.map(jobView, JobView.class)));
        return jobViews;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Update job view", nickname = "updateJobViews", httpMethod = "PUT", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PutMapping("/views/{id}")
    @Secured({ "ROLE_ADMIN" })
    public List<JobViewType> updateJobViews(
            @RequestBody @Valid List<JobViewType> jobViews,
            @PathVariable("id") long viewId,
            @RequestParam("env") String env
    ) {
        if (jobViews != null && !jobViews.isEmpty()) {
            jobsService.deleteJobViews(viewId, env);
            jobViews.forEach(jobView -> jobsService.createJobView(mapper.map(jobView, JobView.class)));
        }
        return jobViews;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get job views", nickname = "getJobViews", httpMethod = "GET", response = Map.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/views/{id}")
    public Map<String, List<JobViewType>> getJobViews(@PathVariable("id") long id) {
        Map<String, List<JobViewType>> jobViews = new LinkedHashMap<>();
        for (JobView jobView : jobsService.getJobViewsByViewId(id)) {
            if (!jobViews.containsKey(jobView.getEnv())) {
                jobViews.put(jobView.getEnv(), new ArrayList<>());
            }
            jobViews.get(jobView.getEnv()).add(mapper.map(jobView, JobViewType.class));
        }
        return jobViews;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Delete job views", nickname = "deleteJobViews", httpMethod = "DELETE")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @DeleteMapping("views/{id}")
    public void deleteJobViews(@PathVariable("id") long viewId, @RequestParam("env") String env) {
        jobsService.deleteJobViews(viewId, env);
    }

}
