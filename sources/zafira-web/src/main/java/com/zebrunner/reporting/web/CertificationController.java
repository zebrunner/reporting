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

import com.zebrunner.reporting.domain.dto.CertificationType;
import com.zebrunner.reporting.service.CertificationService;
import com.zebrunner.reporting.web.documented.CertificationDocumentedController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path = "api/certification", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class CertificationController extends AbstractController implements CertificationDocumentedController {

    private final CertificationService certificationService;

    public CertificationController(CertificationService certificationService) {
        this.certificationService = certificationService;
    }

    @GetMapping("/details")
    @Override
    public CertificationType getCertificationDetails(
            @RequestParam("upstreamJobId") Long upstreamJobId,
            @RequestParam("upstreamJobBuildNumber") Integer upstreamJobBuildNumber
    ) {
        return certificationService.getCertificationDetails(upstreamJobId, upstreamJobBuildNumber);
    }

}
