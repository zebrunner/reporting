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
package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.db.AbstractEntity;
import com.zebrunner.reporting.domain.db.Job;
import com.zebrunner.reporting.domain.db.JobView;
import com.zebrunner.reporting.domain.db.TestRun;
import com.zebrunner.reporting.domain.dto.JobDTO;
import com.zebrunner.reporting.domain.dto.JobUrlType;
import com.zebrunner.reporting.domain.dto.JobViewDTO;
import com.zebrunner.reporting.service.JobsService;
import com.zebrunner.reporting.service.TestRunService;
import com.zebrunner.reporting.web.documented.JobDocumentedController;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin
@RequestMapping(path = "api/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class JobController extends AbstractController implements JobDocumentedController {

    private final Mapper mapper;
    private final JobsService jobsService;
    private final TestRunService testRunService;

    public JobController(Mapper mapper, JobsService jobsService, TestRunService testRunService) {
        this.mapper = mapper;
        this.jobsService = jobsService;
        this.testRunService = testRunService;
    }

    @PostMapping()
    @Override
    public JobDTO createJob(@RequestBody @Valid JobDTO jobDTO) {
        Job job = mapper.map(jobDTO, Job.class);
        Job updatedJob = jobsService.createOrUpdateJob(job);
        return mapper.map(updatedJob, JobDTO.class);
    }

    @PostMapping("/url")
    @Override
    public JobDTO createJobByUrl(@RequestBody @Valid JobUrlType jobUrl) {
        Long principalId = getPrincipalId();
        Job updatedJob = jobsService.createOrUpdateJobByURL(jobUrl.getJobUrlValue(), principalId);
        return mapper.map(updatedJob, JobDTO.class);
    }

    @GetMapping()
    @Override
    public List<Job> getAllJobs() {
        return jobsService.getAllJobs();
    }

    @PostMapping("/views/{id}/tests/runs")
    @Override
    public Map<Long, TestRun> getLatestJobTestRuns(@RequestParam("env") String env, @RequestBody @Valid List<JobViewDTO> jobViews) {
        List<Long> jobIds = jobViews.stream()
                                    .map(JobViewDTO::getJob)
                                    .map(AbstractEntity::getId)
                                    .collect(Collectors.toList());
        return testRunService.getLatestJobTestRuns(env, jobIds);
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping("/views")
    @Override
    public List<JobViewDTO> createJobViews(@RequestBody @Valid List<JobViewDTO> jobViewDTOs) {
        List<JobView> jobViews = jobViewDTOs.stream()
                                            .map(jobView -> mapper.map(jobView, JobView.class))
                                            .collect(Collectors.toList());
        jobsService.createJobViews(jobViews);
        return jobViewDTOs;
    }

    @Secured({"ROLE_ADMIN"})
    @PutMapping("/views/{id}")
    @Override
    public List<JobViewDTO> updateJobViews(@RequestBody @Valid List<JobViewDTO> jobViewDTOs,
                                           @PathVariable("id") long viewId,
                                           @RequestParam("env") String env) {
        List<JobView> jobViews = jobViewDTOs.stream()
                                            .map(jobView -> mapper.map(jobView, JobView.class))
                                            .collect(Collectors.toList());
        jobsService.updateJobViews(jobViews, viewId, env);
        return jobViewDTOs;
    }

    @GetMapping("/views/{id}")
    @Override
    public Map<String, List<JobViewDTO>> getJobViews(@PathVariable("id") long id) {
        List<JobView> jobViews = jobsService.getJobViewsByViewId(id);
        return jobViews.stream()
                       .map(jobView -> mapper.map(jobView, JobViewDTO.class))
                       .collect(Collectors.groupingBy(JobViewDTO::getEnv));
    }

    @DeleteMapping("views/{id}")
    @Override
    public void deleteJobViews(@PathVariable("id") long viewId, @RequestParam("env") String env) {
        jobsService.deleteJobView(viewId, env);
    }

}
