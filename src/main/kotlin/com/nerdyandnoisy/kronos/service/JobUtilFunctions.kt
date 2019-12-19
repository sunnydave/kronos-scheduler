package com.nerdyandnoisy.kronos.service

import com.nerdyandnoisy.kronos.config.PersistableCronTriggerFactoryBean
import org.quartz.JobDataMap
import org.quartz.JobDetail
import org.quartz.Trigger
import org.springframework.context.ApplicationContext
import org.springframework.scheduling.quartz.JobDetailFactoryBean
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

var SIMPLE_JOB_CLASS = "Simple"
var CRON_JOB_CLASS = "Cron"
var REST_API_JOB_CLASS = "RestApi"

fun createJob(jobClass: Class<out QuartzJobBean>, isDurable: Boolean,
              context: ApplicationContext, jobName:String, jobGroup:String, description:String): JobDetail?{
    var factoryBean =  JobDetailFactoryBean()
    factoryBean.setJobClass(jobClass)
    factoryBean.setDurability(isDurable)
    factoryBean.setApplicationContext(context)
    factoryBean.setName(jobName)
    factoryBean.setGroup(jobGroup)
    factoryBean.setDescription(description)

    var jobDataMap = JobDataMap()
    factoryBean.setJobDataAsMap(jobDataMap)
    factoryBean.afterPropertiesSet()
    return factoryBean.`object`
}

fun createCronTrigger(triggerName: String, triggerGroup: String, startTime: Date, cronExpression: String,
                      misFireInstruction: Int, description: String): Trigger?{
    var factoryBean = PersistableCronTriggerFactoryBean()
    factoryBean.setName(triggerName)
    factoryBean.setGroup(triggerGroup)
    factoryBean.setStartTime(startTime)
    factoryBean.setCronExpression(cronExpression)
    factoryBean.setMisfireInstruction(misFireInstruction)
    factoryBean.setDescription(description)
    var logger = Logger.getLogger("JobUtilFunctions")
    try {
        factoryBean.afterPropertiesSet()
    }catch (exception: ParseException){
        logger.log(Level.SEVERE,exception.message,exception)
    }

    return factoryBean.`object`
}

fun createSingleTrigger(triggerName: String, triggerGroup: String, startTime: Date, misFireInstruction: Int, description: String): Trigger?{
    var factoryBean = SimpleTriggerFactoryBean()
    factoryBean.setName(triggerName)
    factoryBean.setGroup(triggerGroup)
    factoryBean.setDescription(description)
    factoryBean.setStartTime(startTime)
    factoryBean.setMisfireInstruction(misFireInstruction)
    factoryBean.setRepeatCount(0)
    factoryBean.afterPropertiesSet()
    return factoryBean.`object`
}

fun dateString(date: Date) = SimpleDateFormat("dd-MM-yy hh:mm:ss Z").format(date)
