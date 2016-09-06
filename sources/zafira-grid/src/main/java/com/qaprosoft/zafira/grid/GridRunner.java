package com.qaprosoft.zafira.grid;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.tanukisoftware.wrapper.WrapperSimpleApp;

public class GridRunner extends WrapperSimpleApp
{
	protected GridRunner(String[] args)
	{
		super(args);
	}
	
	public static void main(String[] args)
	{
		@SuppressWarnings({ "resource", "unused" })
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:zafira-grid.xml");
	}
}
