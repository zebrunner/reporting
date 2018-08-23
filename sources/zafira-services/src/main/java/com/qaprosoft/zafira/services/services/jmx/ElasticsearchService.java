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
package com.qaprosoft.zafira.services.services.jmx;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.qaprosoft.zafira.models.db.Setting;

@Service
@SuppressWarnings("rawtypes")
public class ElasticsearchService implements IJMXService {

    @Value("${zafira.elasticsearch.url}")
    private String url;

    @Value("${zafira.elasticsearch.user}")
    private String user;

    @Value("${zafira.elasticsearch.pass}")
    private String password;

    @Override
    public void init() {
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    public List<Setting> getSettings() {
        return new ArrayList<Setting>() {
            private static final long serialVersionUID = 7140283430898343120L;
            {
                add(new Setting() {
                    private static final long serialVersionUID = 658548604106441383L;
                    {
                        setName("URL");
                        setValue(url);
                    }
                });
                add(new Setting() {
                    private static final long serialVersionUID = 6585486043214259383L;
                    {
                        setName("user");
                        setValue(user);
                    }
                });
                add(new Setting() {
                    private static final long serialVersionUID = 6585486425564259383L;
                    {
                        setName("password");
                        setValue(password);
                    }
                });
            }
        };
    }
}
