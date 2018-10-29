package com.qaprosoft.zafira.tests.util;

import org.apache.log4j.Logger;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class TestListener extends TestListenerAdapter {

    private static final Logger LOGGER = Logger.getLogger(TestListener.class);

    @Override
    public void onTestFailure(ITestResult tr) {
        LOGGER.info(tr.getName() + " WAS FAILED.");
        LOGGER.error(tr.getThrowable().getMessage(), tr.getThrowable());
    }
}
