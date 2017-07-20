package com.qaprosoft.zafira.dbaccess.dao.mysql;

import java.util.List;

import com.qaprosoft.zafira.models.db.Dashboard;
import com.qaprosoft.zafira.models.db.Setting;

public interface SettingsMapper
{
	void createSetting(Setting setting);

	Setting getSettingById(long id);

	Setting getSettingByName(String name);

	List<Setting> getSettingsByTool (String tool);

	List<Setting> getAllSettings();

	List<String> getTools();

	void updateSetting(Setting setting);

	void deleteSetting(Setting setting);

	void deleteSettingById(long id);
}
