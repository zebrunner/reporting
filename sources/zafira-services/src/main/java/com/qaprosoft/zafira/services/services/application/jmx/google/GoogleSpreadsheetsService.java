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
package com.qaprosoft.zafira.services.services.application.jmx.google;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.qaprosoft.zafira.services.services.application.jmx.google.auth.GoogleSheetsAuthService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoogleSpreadsheetsService extends AbstractGoogleService
{

	private Sheets sheetsService;

	public GoogleSpreadsheetsService()
	{
		try
		{
			this.sheetsService = GoogleSheetsAuthService.getService();
		} catch (IOException e)
		{
			LOGGER.error(e);
		}
	}

	private enum InputOption
	{
		INPUT_VALUE_OPTION_UNSPECIFIED, RAW, USER_ENTERED
	}

	private enum AutoDimensionType
	{
		ROWS, COLUMNS
	}

	public Spreadsheet createSpreadsheet(String title, String sheetTitle) throws IOException
	{
		return createSpreadsheet(title, new Sheet().setProperties(new SheetProperties().setTitle(sheetTitle)));
	}

	public Spreadsheet createSpreadsheet(String title, Sheet... sheets) throws IOException
	{
		return sheetsService.spreadsheets().create(new Spreadsheet().setSheets(Arrays.asList(sheets))
				.setProperties(new SpreadsheetProperties().setTitle(title))).execute();
	}

	public BatchUpdateValuesResponse writeValuesIntoSpreadsheet(String spreadsheetId, String range, List<List<Object>> insertValues) throws IOException
	{
		List<ValueRange> data = new ArrayList<>();

		data.add(new ValueRange().setRange(range).setValues(insertValues));

		BatchUpdateValuesRequest body = new BatchUpdateValuesRequest().setValueInputOption(InputOption.USER_ENTERED.name()).setData(data);

		return sheetsService.spreadsheets().values().batchUpdate(spreadsheetId, body).execute();
	}

	public BatchUpdateValuesResponse writeValuesIntoSpreadsheet(String spreadsheetId, List<List<Object>> insertValues, String sheetName) throws IOException
	{
		return writeValuesIntoSpreadsheet(spreadsheetId, buildRange(sheetName, insertValues.get(0).size(), insertValues.size()), insertValues);
	}

	public BatchUpdateSpreadsheetResponse createGrid(String spreadsheetId, String sheetTitle) throws IOException
	{
		List<Request> requests = new ArrayList<>();
		requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties()
				.setTitle(sheetTitle))));
		BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
		return sheetsService.spreadsheets().batchUpdate(spreadsheetId, body).execute();
	}

	public Request setAutoResizeDimensionToGrid(Spreadsheet spreadsheet, Integer sheetId) throws IOException
	{
		return new Request().setAutoResizeDimensions(new AutoResizeDimensionsRequest().setDimensions(new DimensionRange()
				.setDimension(AutoDimensionType.COLUMNS.name()).setSheetId(getSheetById(spreadsheet, sheetId).getProperties().getSheetId())));
	}

	public Request setCellsStyle(int sheetId, int startRowIndex, int endRowIndex, int startColumnIndex, int endColumnIndex, boolean center, int fontSize, String fontFamily, boolean bold)
	{
		return new Request().setRepeatCell(new RepeatCellRequest()
				.setCell(new CellData().setUserEnteredFormat(new CellFormat().setHorizontalAlignment(center ? "CENTER" : null).setTextFormat(
								new TextFormat().setFontSize(fontSize).setFontFamily(fontFamily).setBold(bold))))
				.setRange(new GridRange().setSheetId(sheetId)
						.setStartRowIndex(startRowIndex).setEndRowIndex(endRowIndex)
						.setStartColumnIndex(startColumnIndex).setEndColumnIndex(endColumnIndex)).setFields("*"));
	}

	public Spreadsheet getSpreadsheetById(String spreadsheetId) throws IOException
	{
		return sheetsService.spreadsheets().get(spreadsheetId).execute();
	}

	public Sheet getSheetById(Spreadsheet spreadsheet, Integer sheetId)
	{
		return spreadsheet.getSheets().stream().filter(sheet -> sheet.getProperties().getSheetId().equals(sheetId))
				.findFirst().orElse(null);
	}

	public Request setColumnAndRowsCounts(SheetProperties sheetProperties, Integer columnCount, Integer rowCount)
	{
		sheetProperties = sheetProperties.setGridProperties(new GridProperties()
				.setColumnCount(columnCount)
				.setRowCount(rowCount));
		return new Request().setUpdateSheetProperties(new UpdateSheetPropertiesRequest().setProperties(sheetProperties).setFields("*"));
	}

	public Request setTabColor(SheetProperties sheetProperties, float red, float green, float blue)
	{
		sheetProperties = sheetProperties.setTabColor(new Color()
				.setRed(red)
				.setGreen(green)
				.setBlue(blue));
		return new Request().setUpdateSheetProperties(new UpdateSheetPropertiesRequest().setProperties(sheetProperties).setFields("*"));
	}

	private String buildRange(String sheetName, int columnsCount, int rowsCount)
	{
		return String.format("%s!%s%d:%s%d", sheetName, "A", 1, Character.toString((char) (columnsCount + 96)).toUpperCase(), rowsCount);
	}

	public BatchUpdateSpreadsheetResponse batchUpdate(String spreadSheetId, List<Request> requests) throws IOException
	{
		return sheetsService.spreadsheets().batchUpdate(spreadSheetId, new BatchUpdateSpreadsheetRequest().setRequests(requests)).execute();
	}
}
