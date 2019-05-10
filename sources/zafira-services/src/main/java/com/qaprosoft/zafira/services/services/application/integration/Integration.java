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
package com.qaprosoft.zafira.services.services.application.integration;

import com.qaprosoft.zafira.models.db.Setting.Tool;
import com.qaprosoft.zafira.services.exceptions.IntegrationException;
import com.qaprosoft.zafira.services.services.application.integration.context.AbstractContext;
import com.qaprosoft.zafira.services.services.application.integration.context.AdditionalProperty;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public interface Integration<T extends AbstractContext> {

    void init();

    boolean isConnected();

    Tool getTool();

    Map<? extends AdditionalProperty, String> additionalContextProperties();

    default Optional<T> getContext() {
        return IntegrationTenancyStorage.getContext(getTool());
    }

    default T context() {
        return getContext().orElseThrow(() -> new IntegrationException("Integration for tool '" + getTool().name() + "' is not initialized"));
    }

    default <R> Optional<R> mapContext(Function<T, R> mapper) {
        return getContext().map(mapper);
    }

    default void putContext(T t) {
        IntegrationTenancyStorage.putContext(getTool(), t);
    }

    default void removeContext() {
        IntegrationTenancyStorage.removeContext(getTool());
    }

    default boolean isEnabledAndConnected() {
        return isEnabled() && isConnected();
    }

    default boolean isEnabled() {
        Optional<T> context = getContext();
        return context.isPresent() && context.get().isEnabled();
    }

}
