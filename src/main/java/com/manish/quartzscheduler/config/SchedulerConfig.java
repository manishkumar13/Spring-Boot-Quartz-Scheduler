package com.manish.quartzscheduler.config;

import com.manish.quartzscheduler.helper.AutowiringSpringBeanJobFactory;
import com.manish.quartzscheduler.scheduler.JobFailureHandler;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Configuration
public class SchedulerConfig {

    private static final List<Trigger> triggers = new ArrayList<>();

    @Value("${org.quartz.job-store-type}")
    private String jobStoreType;

    @Value("${org.quartz.jobStore.class}")
    private String jobStoreClass;

    @Value("${org.quartz.jobStore.driverDelegateClass}")
    private String jobStoreDriverDelegateClass;

    @Value("${org.quartz.jobStore.dataSource}")
    private String jobStoreDataSource;

    @Value("${org.quartz.dataSource.quartzDataSource.driver}")
    private String quartzDataSourceDriver;

    @Value("${org.quartz.dataSource.jdbc-url}")
    private String url;

    @Value("${org.quartz.dataSource.username}")
    private String user;

    @Value("${org.quartz.dataSource.password}")
    private String password;

    @Value("${org.quartz.dataSource.quartzDataSource.provider}")
    private String provider;

    @Value("${org.quartz.dataSource.quartzDataSource.maximumPoolSize}")
    private String maximumPoolSize;

    @Value("${org.quartz.dataSource.quartzDataSource.connectionTestQuery}")
    private String connectionTestQuery;

    @Value("${org.quartz.dataSource.quartzDataSource.validationTimeout}")
    private String validationTimeout;

    @Value("${org.quartz.dataSource.quartzDataSource.idleTimeout}")
    private String idleTimeout;

    @Value("${org.quartz.enabled}")
    private Boolean quartzEnabled;

    @Value("${org.quartz.threadPool.threadCount}")
    private String threadCount;

    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    @Qualifier("schedulerFactoryBean")
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory) throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setOverwriteExistingJobs(true);
        factory.setAutoStartup(quartzEnabled);
        factory.setJobFactory(jobFactory);
        factory.setQuartzProperties(quartzProperties());
        factory.setTriggers(triggers.toArray(new Trigger[triggers.size()]));
        factory.setGlobalJobListeners(new JobFailureHandler());
        return factory;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setProperties(setQuartzProperties());
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    @Bean
    public Properties setQuartzProperties() {
        Properties properties = new Properties();
        properties.put("org.quartz.job-store-type", jobStoreType);
        properties.put("org.quartz.jobStore.class", jobStoreClass);
        properties.put("org.quartz.jobStore.driverDelegateClass", jobStoreDriverDelegateClass);
        properties.put("org.quartz.jobStore.dataSource", jobStoreDataSource);
        properties.put("org.quartz.dataSource.quartzDataSource.driver", quartzDataSourceDriver);
        properties.put("org.quartz.dataSource.quartzDataSource.URL", url);
        properties.put("org.quartz.dataSource.quartzDataSource.user", user);
        properties.put("org.quartz.dataSource.quartzDataSource.password", password);
        properties.put("org.quartz.dataSource.quartzDataSource.provider", provider);
        properties.put("org.quartz.dataSource.quartzDataSource.maximumPoolSize", maximumPoolSize);
        properties.put("org.quartz.dataSource.quartzDataSource.connectionTestQuery", connectionTestQuery);
        properties.put("org.quartz.dataSource.quartzDataSource.validationTimeout", validationTimeout);
        properties.put("org.quartz.dataSource.quartzDataSource.idleTimeout", idleTimeout);
        properties.put("org.quartz.threadPool.threadCount", threadCount);
        return properties;
    }

    public static JobDetailFactoryBean createJobDetail(Class jobClass) {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        // job has to be durable to be stored in DB
        factoryBean.setDurability(true);
        return factoryBean;
    }

    public static CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail, String cronExpression,
                                                           String triggerName, String groupName, JobDataMap map) {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setCronExpression(cronExpression);
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        factoryBean.setName(triggerName);
        factoryBean.setJobDataMap(map);
        factoryBean.setGroup(groupName);
        factoryBean.setStartDelay(3000);
        try {
            factoryBean.afterPropertiesSet();
        } catch (ParseException e) {

        }
        triggers.add(factoryBean.getObject());
        return factoryBean;
    }
}
