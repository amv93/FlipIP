package com.flipip.boot;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.flipip.db.DataSource;

public class FlipIPBooter implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("Servlet Context is destroyed....");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("Servlet Context is initialized....");
		DataSource.getInstance().initialize();
		
	}

}
