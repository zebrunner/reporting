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
package com.qaprosoft.zafira.log.log4j.level;

import org.apache.log4j.Level;

public class MetaInfoLevel extends Level {

    private static final long serialVersionUID = -6035169096201540353L;

    private static final String META_NAME = "META_INFO";
    public static final int META_INT = INFO_INT + 10;

    public static final Level META_INFO = new MetaInfoLevel(META_INT, META_NAME, 7);

    protected MetaInfoLevel(int level, String levelStr, int syslogEquivalent) {
        super(level, levelStr, syslogEquivalent);
    }

    public static Level toLevel(String sArg) {
        if (sArg != null && sArg.toUpperCase().equals(META_NAME)) {
            return META_INFO;
        }
        return toLevel(sArg, Level.DEBUG);
    }

    public static Level toLevel(int val) {
        if (val == META_INT) {
            return META_INFO;
        }
        return toLevel(val, Level.DEBUG);
    }

    public static Level toLevel(int val, Level defaultLevel) {
        if (val == META_INT) {
            return META_INFO;
        }
        return Level.toLevel(val, defaultLevel);
    }

    public static Level toLevel(String sArg, Level defaultLevel) {
        if (sArg != null && sArg.toUpperCase().equals(META_NAME)) {
            return META_INFO;
        }
        return Level.toLevel(sArg, defaultLevel);
    }
}
