/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.services.exceptions;

import com.qaprosoft.zafira.services.util.LocaleContext;

@SuppressWarnings("rawtypes")
public class EntityNotExistsException extends ServiceException {

    private static final long serialVersionUID = 419121635807145174L;

    public EntityNotExistsException(Class entityClass, boolean showStacktrace) {
        super(buildMessage(entityClass), null, showStacktrace);
    }

    public EntityNotExistsException(Throwable cause, boolean showStacktrace) {
        super(null, cause, showStacktrace);
    }

    public EntityNotExistsException(Class entityClass, Throwable cause, boolean showStacktrace) {
        super(buildMessage(entityClass), cause, showStacktrace);
    }

    private static String buildMessage(Class entityClass) {
        return String.format(LocaleContext.getMessage("error.message.entityNotExistsTemplate"), LocaleContext.getMessage(entityClass));
    }
}
