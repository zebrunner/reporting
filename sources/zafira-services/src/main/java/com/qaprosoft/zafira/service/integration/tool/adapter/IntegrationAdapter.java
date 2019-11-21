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
package com.qaprosoft.zafira.service.integration.tool.adapter;

/**
 * Represents adapter for specific integration instance of certain type.
 * Adapter is created per integration rather than per integration type or group.
 */
public interface IntegrationAdapter extends IntegrationGroupAdapter {

    /**
     * Returns current adapter connectivity state.
     * @return {@code true} if adapter is connected
     */
    boolean isConnected();

}
