package com.telappoint.outlookdownload;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

public class SyncOutlook {
	private static Logger logger = Logger.getLogger(SyncOutlook.class);
	public static void main(String[] args) throws Exception {
		Properties properties = getProperties("config/syncoutlook-cron.properties");
		String cronExpression = properties.getProperty("CRON_EXPRESSION");
		logger.info("====Initializing====");	
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();
		logger.info("========== Scheduling Jobs ============");
		JobDetail job = new JobDetail("syncoutlook", "syncgroup", UpdateOutlookJob.class);
		CronTrigger cronTrigger = new CronTrigger("crontrigger", "syncgroup", "syncoutlook", "syncgroup", cronExpression);
		sched.scheduleJob(job, cronTrigger);
		logger.info("=========== Starting Scheduler ============");
		sched.start();
		logger.info("============ Started Scheduler ============");
	}

	private static Properties getProperties(String relativeFilePath) throws IOException {
		Properties properties = new Properties();
		try (InputStream inputStream = new FileInputStream(relativeFilePath)) {
			properties.load(inputStream);
			return properties;
		}
	}	
}
