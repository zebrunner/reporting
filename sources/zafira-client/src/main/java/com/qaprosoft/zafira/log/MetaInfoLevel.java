package com.qaprosoft.zafira.log;

import org.apache.log4j.Level;

public class MetaInfoLevel extends Level {

    private static final long serialVersionUID = -6035169096201540353L;

    private static final String META_NAME = "META_INFO";
    public static final int META_INT = INFO_INT + 10;

    public static final Level META_INFO = new MetaInfoLevel(META_INT,META_NAME,7);

    protected MetaInfoLevel(int level, String levelStr, int syslogEquivalent) {
        super(level, levelStr, syslogEquivalent);
    }

    public static Level toLevel(String sArg) {
        if (sArg != null && sArg.toUpperCase().equals(META_NAME)) {
            return META_INFO;
        }
        return (Level) toLevel(sArg, Level.DEBUG);
    }

    public static Level toLevel(int val) {
        if (val == META_INT) {
            return META_INFO;
        }
        return (Level) toLevel(val, Level.DEBUG);
    }

    public static Level toLevel(int val, Level defaultLevel) {
        if (val == META_INT) {
            return META_INFO;
        }
        return Level.toLevel(val,defaultLevel);
    }

    public static Level toLevel(String sArg, Level defaultLevel) {
        if(sArg != null && sArg.toUpperCase().equals(META_NAME)) {
            return META_INFO;
        }
        return Level.toLevel(sArg,defaultLevel);
    }
}
