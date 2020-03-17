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
package com.zebrunner.reporting.service;

import com.zebrunner.reporting.persistence.dao.mysql.application.search.SearchResult;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.TestSessionSearchCriteria;
import com.zebrunner.reporting.persistence.repository.TestSessionRepository;
import com.zebrunner.reporting.persistence.utils.TenancyContext;
import com.zebrunner.reporting.domain.dto.testsession.SearchParameter;
import com.zebrunner.reporting.domain.entity.TestSession;
import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.domain.entity.integration.IntegrationParam;
import com.zebrunner.reporting.domain.push.events.EventMessage;
import com.zebrunner.reporting.domain.push.events.ZbrHubTokenRefreshMessage;
import com.zebrunner.reporting.service.exception.IllegalOperationException;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.util.EventPushService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.TOKEN_REFRESH_IS_NOT_SUPPORTED;
import static com.zebrunner.reporting.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.TEST_SESSION_NOT_FOUND;
import static com.zebrunner.reporting.service.util.EventPushService.Type.ZBR_EVENTS;

@Service
public class TestSessionService {

    private static final String ERR_MSG_TEST_SESSION_NOT_EXISTS_BY_SESSION_ID = "Test session does not exist by sessionId '%s'";
    private static final String ERR_MSG_FOR_INTEGRATION_TOKEN_REFRESH_IS_NOT_SUPPORTED = "For integration of type %s token refresh is not supported";

    private final TestSessionRepository testSessionRepository;
    private final IntegrationService integrationService;
    private EventPushService<EventMessage> eventPushService;

    public TestSessionService(TestSessionRepository testSessionRepository, IntegrationService integrationService, EventPushService<EventMessage> eventPushService) {
        this.testSessionRepository = testSessionRepository;
        this.integrationService = integrationService;
        this.eventPushService = eventPushService;
    }

    @Transactional(readOnly = true)
    public TestSession retrieveBySessionId(String sessionId) {
        return testSessionRepository.findBySessionId(sessionId)
                                    .orElseThrow(() -> new ResourceNotFoundException(TEST_SESSION_NOT_FOUND, String.format(ERR_MSG_TEST_SESSION_NOT_EXISTS_BY_SESSION_ID, sessionId)));
    }

    @Transactional(readOnly = true)
    public SearchResult<TestSession> search(TestSessionSearchCriteria criteria) {
        Pageable pageable = buildPageable(criteria);
        Specification<TestSession> specification = buildSpecification(criteria.getQuery(), criteria.getStatus(), criteria.getPlatform(), criteria.getDate(), criteria.getFromDate(), criteria.getToDate());
        Page<TestSession> page = testSessionRepository.findAll(specification, pageable);
        return SearchResult.<TestSession>builder()
                .results(page.getContent())
                .totalResults((int) page.getTotalElements())
                .pageSize(page.getSize())
                .page(page.getNumber())
                .build();
    }

    @Transactional(readOnly = true)
    public SearchParameter collectSearchParameters() {
        List<TestSession.Status> statuses = testSessionRepository.findDistinctByStatus();
        List<String> platforms = testSessionRepository.findDistinctByBrowserName();
        return new SearchParameter(statuses, platforms);
    }

    public String refreshToken(Long integrationId) {
        Integration integration = integrationService.retrieveById(integrationId);

        if (!"ZEBRUNNER".equals(integration.getType().getName())) {
            throw new IllegalOperationException(TOKEN_REFRESH_IS_NOT_SUPPORTED, String.format(ERR_MSG_FOR_INTEGRATION_TOKEN_REFRESH_IS_NOT_SUPPORTED, integration.getType()));
        }

        String token = RandomStringUtils.randomAlphanumeric(16);
        updateZbrHubPassword(integration, token);

        String tenantName = TenancyContext.getTenantName();
        ZbrHubTokenRefreshMessage message = new ZbrHubTokenRefreshMessage(tenantName, token);

        eventPushService.convertAndSend(ZBR_EVENTS, message, "Type", "ZBR_HUB_TOKEN_REFRESH");
        return token;
    }

    private void updateZbrHubPassword(Integration integration, String token) {
        IntegrationParam password = integration.getType()
                                               .getParams()
                                               .stream()
                                               .filter(integrationParam -> isParamPresent("ZEBRUNNER_PASSWORD", integrationParam))
                                               .findFirst()
                                               .orElse(new IntegrationParam());
        updateIntegrationSetting(token, integration, password);
        integrationService.update(integration);
    }

    private void updateIntegrationSetting(String value, Integration integration, IntegrationParam integrationParam) {
        integration.getSettings().forEach(integrationSetting -> {
            if (integrationSetting.getParam().getId().equals(integrationParam.getId())) {
                integrationSetting.setValue(value);
                integrationSetting.setEncrypted(false);
            }
        });
    }

    private boolean isParamPresent(String paramName, IntegrationParam integrationParam) {
        return paramName.equals(integrationParam.getName());
    }

    private Pageable buildPageable(TestSessionSearchCriteria criteria) {
        if (StringUtils.isEmpty(criteria.getOrderBy())) {
            criteria.setOrderBy("id");
        }
        Sort sortBy = Sort.by(criteria.getOrderBy());
        sortBy = criteria.getSortOrder().equals(TestSessionSearchCriteria.SortOrder.ASC) ? sortBy.ascending() : sortBy.descending();
        return PageRequest.of(criteria.getPage(), criteria.getPageSize(), sortBy);
    }

    private Specification<TestSession> buildSpecification(String query, TestSession.Status status, String platform, LocalDateTime date, LocalDateTime startedAfter, LocalDateTime endedBefore) {
        Specification<TestSession> specification = Specification.where(null);
        if (query != null) {
            specification = specification
                    .and((root, q, builder) -> builder.like(
                            builder.lower(root.get("testName")), "%" + query.toLowerCase() + "%")
                    )
                    .or((root, q, builder) -> builder.like(
                            builder.lower(root.get("buildNumber")), "%" + query.toLowerCase() + "%")
                    );
        }
        if (status != null) {
            specification = specification.and((root, q, builder) -> builder.equal(root.get("status"), status));
        }
        if (platform != null) {
            specification = specification.and((root, q, builder) -> builder.equal(root.get("browserName"), platform));
        }
        if (date != null) {
            specification = specification.and((root, q, builder) -> builder.greaterThan(root.get("startedAt"), date.minusDays(1)))
                                         .and((root, q, builder) -> builder.lessThanOrEqualTo(root.get("endedAt"), date));
        }
        if (startedAfter != null) {
            specification = specification.and((root, q, builder) -> builder.greaterThanOrEqualTo(root.get("startedAt"), startedAfter));
        }
        if (endedBefore != null) {
            specification = specification.and((root, q, builder) -> builder.lessThanOrEqualTo(root.get("endedAt"), endedBefore));
        }
        return specification;
    }

}
