/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
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
 ******************************************************************************/
package com.qaprosoft.zafira.services.services.application.jmx.google.models;

import com.google.api.services.sheets.v4.model.*;
import com.qaprosoft.zafira.models.db.Test;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.config.Argument;
import com.qaprosoft.zafira.models.db.config.Configuration;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.TestRunService;
import com.qaprosoft.zafira.services.services.application.TestService;
import com.qaprosoft.zafira.services.services.application.jmx.google.GoogleDriveService;
import com.qaprosoft.zafira.services.services.application.jmx.google.GoogleService;
import com.qaprosoft.zafira.services.services.application.jmx.google.GoogleSpreadsheetsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class TestRunSpreadsheetService
{

	private static final Logger LOGGER = Logger.getLogger(TestRunSpreadsheetService.class);

	private static final String TEST_RUN_INFO_SHEET_NAME = "INFO";
	
	private static final String TEST_RUN_RESULTS_SHEET_NAME = "RESULT";

	@Autowired
	private GoogleService googleService;

	@Autowired
	private TestService testService;

	@Autowired
	private TestRunService testRunService;

	public String createTestRunResultSpreadsheet(TestRun testRun, String... accessRecipients) throws ServiceException
	{
		String result = null;
		GoogleSpreadsheetsService spreadsheetsService = googleService.getSpreadsheetsService();
		GoogleDriveService driveService = googleService.getDriveService();
		try
		{
			List<List<Object>> testRunInfo = collectTestRunInfo(testRun);
			Spreadsheet spreadsheet = spreadsheetsService.createSpreadsheet(testRun.getTestSuite().getName(), TEST_RUN_INFO_SHEET_NAME);
			result = spreadsheet.getSpreadsheetUrl();
			Sheet infoSheet = spreadsheet.getSheets().get(0);
			String spreadsheetId = spreadsheet.getSpreadsheetId();
			Request infoHeaderCellsStyleRequest = spreadsheetsService.setCellsStyle(infoSheet.getProperties().getSheetId(),
					0, testRunInfo.size(), 0, 1, true, 10, "Arial", true);
			Request infoCellsStyleRequest = spreadsheetsService.setCellsStyle(infoSheet.getProperties().getSheetId(),
					1, testRunInfo.size(), 1, testRunInfo.get(0).size(), true, 10, "Arial", false);
			spreadsheetsService.batchUpdate(spreadsheet.getSpreadsheetId(), Arrays.asList(infoHeaderCellsStyleRequest, infoCellsStyleRequest));
			SheetProperties infoSheetProperties = infoSheet.getProperties();
			spreadsheetsService.writeValuesIntoSpreadsheet(spreadsheet.getSpreadsheetId(), testRunInfo, TEST_RUN_INFO_SHEET_NAME);
			Request infoColumnsAndRowsCountRequest = spreadsheetsService.setColumnAndRowsCounts(infoSheetProperties, testRunInfo.get(0).size(), testRunInfo.size());
			Request infoAutoResizeRequest = spreadsheetsService.setAutoResizeDimensionToGrid(spreadsheet, infoSheetProperties.getSheetId());
			Request infoTabColorRequest = spreadsheetsService.setTabColor(infoSheetProperties, 1F, 0, 0);
			spreadsheetsService.batchUpdate(spreadsheet.getSpreadsheetId(), Arrays.asList(infoColumnsAndRowsCountRequest, infoTabColorRequest, infoAutoResizeRequest));

			List<List<Object>> testRunResults = collectTestRunResults(testRun);
			spreadsheetsService.createGrid(spreadsheet.getSpreadsheetId(), TEST_RUN_RESULTS_SHEET_NAME);
			spreadsheet = spreadsheetsService.getSpreadsheetById(spreadsheet.getSpreadsheetId());
			Sheet sheet = spreadsheet.getSheets().stream().filter(s -> s.getProperties().getTitle().equalsIgnoreCase(TEST_RUN_RESULTS_SHEET_NAME)).findFirst().orElse(new Sheet());
			Request cellsStyleRequest = spreadsheetsService.setCellsStyle(sheet.getProperties().getSheetId(),
					0, 1, 0, testRunResults.get(0).size(), true, 10, "Arial", true);
			spreadsheetsService.batchUpdate(spreadsheet.getSpreadsheetId(), Collections.singletonList(cellsStyleRequest));
			SheetProperties sheetProperties = sheet.getProperties();
			spreadsheetsService.writeValuesIntoSpreadsheet(spreadsheet.getSpreadsheetId(), testRunResults, TEST_RUN_RESULTS_SHEET_NAME);
			Request columnsAndRowsCountRequest = spreadsheetsService.setColumnAndRowsCounts(sheetProperties, testRunResults.get(0).size(), testRunResults.size());
			Request autoResizeRequest = spreadsheetsService.setAutoResizeDimensionToGrid(spreadsheet, sheetProperties.getSheetId());
			Request tabColorRequest = spreadsheetsService.setTabColor(sheetProperties, 1F, 0, 0);
			spreadsheetsService.batchUpdate(spreadsheet.getSpreadsheetId(), Arrays.asList(columnsAndRowsCountRequest, tabColorRequest, autoResizeRequest));
			Arrays.asList(accessRecipients).forEach(recipient -> driveService
					.shareFile(spreadsheetId, GoogleDriveService.GranteeType.USER,
							GoogleDriveService.GranteeRoleType.READER, recipient));
		} catch (IOException e)
		{
			LOGGER.error(e);
		}
		return result;
	}

	public List<List<Object>> collectTestRunResults(final TestRun testRun) throws ServiceException
	{
		List<List<Object>> result = new ArrayList<>();
		result.add(Arrays.asList("Status", "Message", "Title", "Owner", "Secondary owner", "Device", "Elapsed", "Started at", ""));
		List<Test> tests = testService.getTestsByTestRunId(testRun.getId());
		tests.forEach(test -> {
			List<Object> testResult = new ArrayList<>();
			testResult.add(getNotNullSpreadsheetValue(test.getStatus().name()));
			testResult.add(getNotNullSpreadsheetValue(test.getMessage()));
			testResult.add(getNotNullSpreadsheetValue(test.getName()));
			testResult.add(getNotNullSpreadsheetValue(test.getOwner()));
			testResult.add(getNotNullSpreadsheetValue(test.getSecondaryOwner()));
			testResult.add(getNotNullSpreadsheetValue(test.getTestConfig().getDevice()));
			Period period = new Interval(test.getStartTime().getTime(), test.getFinishTime().getTime()).toPeriod();
			StringBuilder elapsedTime = new StringBuilder();
			elapsedTime.append(period.getDays() > 0 ? String.format("%d days", period.getDays()) : "");
			elapsedTime.append(period.getHours() > 0 ? String.format(" %d hours", period.getHours()) : "");
			elapsedTime.append(period.getMinutes() > 0 ? String.format(" %d minutes", period.getMinutes()) : "");
			elapsedTime.append(period.getSeconds() > 0 ? String.format(" %d seconds", period.getSeconds()) : "");
			testResult.add(getNotNullSpreadsheetValue(elapsedTime.toString()));
			testResult.add(getNotNullSpreadsheetValue(new SimpleDateFormat("E, MM/dd/yyyy HH:mm:ss Z").format(test.getStartTime())));
			String hyperLink = "=HYPERLINK(\"%s\", \"%s\")\n";
			StringBuilder hyperLinks = new StringBuilder();
			test.getArtifacts().forEach(artifact -> hyperLinks.append(String.format(hyperLink, artifact.getLink(), artifact.getName())));
			testResult.add(hyperLinks.toString());
			result.add(testResult);
		});
		return result;
	}

	public List<List<Object>> collectTestRunInfo(final TestRun testRun)
	{
		String[] sideTitles = {"Environment", "Version", "Platform", "Finished", "Elapsed", "Test job URL", "Passed", "Failed | Known | Blockers", "Skipped", "Success rate"};
		List<List<Object>> result = new ArrayList<>();
		Arrays.asList(sideTitles).forEach(title ->
		{
			List<Object> testResult = new ArrayList<>();
			testResult.add(title);
			String hyperLink = "=HYPERLINK(\"%s\", \"%s\")\n";
			switch(title)
			{
				case "Environment":
					String environment = getConfigValueByName("url", testRun.getConfigXML());
					testResult.add(testRun.getEnv() != null ? String.format("%s - %s", getNotNullSpreadsheetValue(testRun.getEnv()), environment) : "");
					break;
				case "Version":
					testResult.add(getNotNullSpreadsheetValue(getConfigValueByName("app_version", testRun.getConfigXML())));
					break;
				case "Platform":
					String platform = getConfigValueByName("platform", testRun.getConfigXML());
					String mobileDeviceName = getConfigValueByName("mobile_device_name", testRun.getConfigXML());
					String mobilePlatformName = getConfigValueByName("mobile_platform_name", testRun.getConfigXML());
					String mobilePlatformVersion = getConfigValueByName("mobile_platform_version", testRun.getConfigXML());
					String browser = getConfigValueByName("browser", testRun.getConfigXML());
					String browserVersion = getConfigValueByName("browser_version", testRun.getConfigXML());
					testResult.add(getNotNullSpreadsheetValue(
							(! isConfigValueIsEmpty(platform) ? platform + "\n" : "") +
							(! isConfigValueIsEmpty(mobileDeviceName) ? mobileDeviceName + " - " : "") +
							(! isConfigValueIsEmpty(mobilePlatformName) ? mobilePlatformName + "\n" : "") +
							(! isConfigValueIsEmpty(mobilePlatformVersion) ? mobilePlatformVersion + "\n" : "") +
							(! isConfigValueIsEmpty(platform) && ! platform.equalsIgnoreCase("api") ? browser + "\n" : "" ) +
							(! isConfigValueIsEmpty(platform) && ! platform.equalsIgnoreCase("api") ? browserVersion + "\n" : "" )
					));
					break;
				case "Finished":
					testResult.add(getNotNullSpreadsheetValue(new SimpleDateFormat("E, MM/dd/yyyy HH:mm:ss Z").format(testRun.getModifiedAt())));
					break;
				case "Elapsed":
					testResult.add(getElapsed(testRun));
					break;
				case "Test job URL":
					String zafiraServiceUrl = getConfigValueByName("zafira_service_url", testRun.getConfigXML());
					if(!StringUtils.isBlank(zafiraServiceUrl) && ! zafiraServiceUrl.equalsIgnoreCase("null"))
					{
						testResult.add(
								String.format("%s\n%s",
										String.format(hyperLink, String.format("%s/#!/tests/runs/%d",
												getNotNullSpreadsheetValue(getConfigValueByName("zafira_service_url", testRun.getConfigXML())), testRun.getId()), "Zafira"),
										String.format(hyperLink,
												String.format("%s/%s/eTAF_Report", testRun.getJob().getJobURL(), testRun.getBuildNumber()), "Jenkins")));
					}
					break;
				case "Passed":
					testResult.add(testRun.getPassed());
					break;
				case "Failed | Known | Blockers":
					testResult.add(testRun.getFailed() + " | " + testRun.getFailedAsKnown() + " | " + testRun.getFailedAsBlocker());
					break;
				case "Skipped":
					testResult.add(testRun.getSkipped());
					break;
				case "Success rate":
					testResult.add(TestRunService.calculateSuccessRate(testRun));
					break;
				default:
					break;
			}
			result.add(testResult);
		});
		return result;
	}

	private Object getNotNullSpreadsheetValue(String value)
	{
		return value != null ? ! value.equalsIgnoreCase("null") ? value : "" : "";
	}

	private String getConfigValueByName(String name, String configurationXML)
	{
		Configuration configuration = null;
		try
		{
			configuration = testRunService.readConfiguration(configurationXML);
		} catch (JAXBException e)
		{
			LOGGER.error(e);
		}
		return configuration.getArg().stream().filter(arg -> arg.getKey().equalsIgnoreCase(name)).findFirst().orElse(new Argument()).getValue();
	}

	private boolean isConfigValueIsEmpty(String value)
	{
		return StringUtils.isBlank(value) || value.equalsIgnoreCase("NULL") || value.equals("*");
	}

	private String getElapsed(TestRun testRun)
	{
		String result = null;
		if(testRun.getElapsed() != null)
		{
			int s = testRun.getElapsed() % 60;
			int m = (testRun.getElapsed() / 60) % 60;
			int h = (testRun.getElapsed() / (60 * 60)) % 24;
			result = String.format("%02d:%02d:%02d", h,m,s);
		}
		return result;
	}
}
