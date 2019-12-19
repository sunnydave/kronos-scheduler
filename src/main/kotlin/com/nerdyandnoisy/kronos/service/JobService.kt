package com.nerdyandnoisy.kronos.service

import com.nerdyandnoisy.kronos.entity.JobInfo
import com.nerdyandnoisy.kronos.entity.SchedulerJobInfo
import com.nerdyandnoisy.kronos.jobs.CronJob
import com.nerdyandnoisy.kronos.jobs.RestApiJob
import com.nerdyandnoisy.kronos.jobs.SimpleJob
import org.quartz.*
import org.quartz.impl.matchers.GroupMatcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Lazy
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import org.springframework.stereotype.Service
import java.util.logging.Level
import java.util.logging.Logger

@Service
class JobService {


    private var logger = Logger.getLogger(JobService::class.java.name)
    @Autowired
    @Lazy
    lateinit var schedulerFactoryBean: SchedulerFactoryBean

    @Autowired
    lateinit var context: ApplicationContext


    fun scheduleOneTimeJob(jobInfo: SchedulerJobInfo): Boolean{
        val triggerKey = JobKey(jobInfo.jobName,jobInfo.jobGroup)

        val jobDetail = createJob(getJobClass(jobInfo.jobClass), false, context, jobInfo.jobName,jobInfo.jobGroup,jobInfo.description)
        logger.info("Creating trigger for key : "+triggerKey+" at date : "+jobInfo.repeatTime)
        val jobTriggerBean = createSingleTrigger(triggerKey.name,triggerKey.group,jobInfo.repeatTime, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW,jobInfo.description)
        jobDetail?.jobDataMap?.put("url",jobInfo.jobData.url)
        jobDetail?.jobDataMap?.put("method",jobInfo.jobData.method)
        jobDetail?.jobDataMap?.put("body",jobInfo.jobData.body)
        try{
            schedulerFactoryBean.scheduler.scheduleJob(jobDetail, jobTriggerBean)
            return true
        }catch (exception: SchedulerException){
            logger.severe("Exception while scheduling one time job with key : "+triggerKey.name+" message: "+exception.message)
            logger.severe(exception.stackTrace.toString())
        }

        return false
    }

    fun scheduleCronJob(jobInfo:SchedulerJobInfo): Boolean{
        val jobKey = jobInfo.jobName
        val groupKey = jobInfo.jobGroup

        val jobDetail = createJob(getJobClass(jobInfo.jobClass), false, context, jobKey, groupKey,jobInfo.description)

        val cronTriggerBean = createCronTrigger(jobKey,groupKey, jobInfo.repeatTime, jobInfo.cronExpression, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW,jobInfo.description)
        jobDetail?.jobDataMap?.put("url",jobInfo.jobData.url)
        jobDetail?.jobDataMap?.put("method",jobInfo.jobData.method)
        jobDetail?.jobDataMap?.put("body",jobInfo.jobData.body)
        try {
            schedulerFactoryBean.scheduler.scheduleJob(jobDetail, cronTriggerBean)
            return true
        }catch (exception: SchedulerException){
            logger.severe("Exception while scheduling cron job with key : "+jobKey+" message: "+exception.message)
            logger.log(Level.SEVERE,exception.message,exception)
        }
        return false
    }

    fun updateOneTimeJob(jobInfo: SchedulerJobInfo): Boolean{

        try{
            val newTrigger = createSingleTrigger(jobInfo.jobName,jobInfo.jobGroup, jobInfo.repeatTime, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW,jobInfo.description)
            schedulerFactoryBean.scheduler.rescheduleJob(TriggerKey.triggerKey(jobInfo.jobName,jobInfo.jobGroup),newTrigger)
            return true
        }catch (exception: SchedulerException){
            logger.severe("Exception while updating one time job with key : "+jobInfo.jobName+" message: "+exception.message)
            logger.severe(exception.stackTrace.toString())
        }

        return false
    }

    fun updateCronJob(jobInfo: SchedulerJobInfo):Boolean{
        try {
            val newTrigger = createCronTrigger(jobInfo.jobName,jobInfo.jobGroup, jobInfo.repeatTime, jobInfo.cronExpression, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW,jobInfo.description)
            schedulerFactoryBean.scheduler.rescheduleJob(TriggerKey.triggerKey(jobInfo.jobName,jobInfo.jobGroup), newTrigger)
            return true
        }catch (exception: SchedulerException){
            logger.severe("Exception while updating cron job with key : "+jobInfo.jobName+" message: "+exception.message)
            logger.severe(exception.stackTrace.toString())
        }
        return false
    }

    fun deleteJob(jobName: String, group: String):Boolean{

        val jKey = JobKey(jobName,group)

        try {
            return schedulerFactoryBean.scheduler.deleteJob(jKey)
        }catch (exception: SchedulerException){
            logger.severe("Exception while deleting job with key : "+jobName+" message: "+exception.message)
            logger.severe(exception.stackTrace.toString())
        }
        return false
    }

    fun pauseJob(jobName: String, group: String):Boolean{
        val jKey = JobKey(jobName, group)

        try {
            schedulerFactoryBean.scheduler.pauseJob(jKey)
            return true
        }catch (exception: SchedulerException){
            logger.severe("Exception while pausing job with key : "+jobName+" message: "+exception.message)
            logger.severe(exception.stackTrace.toString())
        }

        return false
    }

    fun resumeJob(jobName:String, group: String): Boolean{
        val jKey = JobKey(jobName, group)
        try{
            schedulerFactoryBean.scheduler.resumeJob(jKey)
            return true
        }catch (exception: SchedulerException){
            logger.severe("Exception while resuming job with key : "+jobName+" message: "+exception.message)
            logger.severe(exception.stackTrace.toString())
        }

        return false
    }

    fun startJobNow(jobName: String, group: String):Boolean{
        val jKey = JobKey(jobName, group)
        try {
            schedulerFactoryBean.scheduler.triggerJob(jKey)
            return true
        } catch (exception: SchedulerException){
            logger.severe("Exception while starting job with key : "+jobName+" message: "+exception.message)
            logger.severe(exception.stackTrace.toString())
        }
        return false
    }

    fun isJobRunning(jobName: String, group: String): Boolean{
        try{
            val currentJobs = schedulerFactoryBean.scheduler.currentlyExecutingJobs
            if(currentJobs != null){
                for(job in currentJobs){
                    val jobNameDb = job.jobDetail.key.name
                    val groupNameDb = job.jobDetail.key.name
                    if(jobName.equals(jobNameDb, true) && group.equals(groupNameDb,true)){
                        return true
                    }
                }
            }
        }catch (exception: SchedulerException){
            logger.severe("Exception while checking job with key : "+jobName+" message: "+exception.message)
            logger.severe(exception.stackTrace.toString())
        }
        return false
    }

    fun getAllJobs():List<JobInfo>{
        val list = mutableListOf<JobInfo>()
        try{
            val scheduler = schedulerFactoryBean.scheduler
            for(groupName in scheduler.jobGroupNames){
                for(jobKey in scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))){
                    val triggers = scheduler.getTriggersOfJob(jobKey)
                    val jobStatus =
                            when(isJobRunning(jobKey.name,groupName)){
                                true -> "RUNNING"
                                false -> getJobState(jobKey.name,groupName)
                            }
                    val previousFireTime =
                            when(triggers[0].previousFireTime != null){
                                true -> dateString(triggers[0].previousFireTime)
                                false -> ""
                            }
                    val jobInfo = JobInfo(jobKey.name, jobKey.group, dateString(triggers[0].startTime),
                            previousFireTime, dateString(triggers[0].nextFireTime), jobStatus, triggers[0].description)
                    list.add(jobInfo)


                }
            }
        }catch(exception: SchedulerException){
            logger.severe("Error while getting all jobs : "+exception.message)
            logger.severe(exception.stackTrace.toString())
        }
        return list
    }

    fun getJobState(jobName: String, group: String):String{
        try{
            val jobKey = JobKey(jobName, group)

            val jobDetail = schedulerFactoryBean.scheduler.getJobDetail(jobKey)
            val triggers = schedulerFactoryBean.scheduler.getTriggersOfJob(jobDetail.key)
            if(triggers != null && triggers.size > 0){
                for (trigger in triggers){
                    val  triggerState = schedulerFactoryBean.scheduler.getTriggerState(trigger.key)
                    return  when(triggerState){
                        Trigger.TriggerState.PAUSED -> "PAUSED"
                        Trigger.TriggerState.BLOCKED -> "BLOCKED"
                        Trigger.TriggerState.COMPLETE -> "COMPLETE"
                        Trigger.TriggerState.ERROR -> "ERROR"
                        Trigger.TriggerState.NONE -> "NONE"
                        else -> "SCHEDULED"
                    }
                }
            }
        }catch (exception: SchedulerException){
            logger.severe("Error while cchecking job state : "+exception.message)
            logger.severe(exception.stackTrace.toString())
        }

        return "Error finding state"
    }

    private fun getJobClass(jobClass: String):Class<out QuartzJobBean>{
        return when(jobClass){
            REST_API_JOB_CLASS -> RestApiJob::class.java
            CRON_JOB_CLASS -> CronJob::class.java
            SIMPLE_JOB_CLASS -> SimpleJob::class.java
            else -> SimpleJob::class.java
        }
    }

    fun stopjob(jobName: String, group: String):Boolean{
        try {
            return schedulerFactoryBean.scheduler.interrupt(JobKey(jobName,group))
        }catch (exception: SchedulerException){
            logger.severe("Error while stopping job : "+exception.message)
            logger.severe(exception.stackTrace.toString())
        }
        return false
    }

    fun isJobWithNamePresent(jobName:String, group:String):Boolean{
        try{
            val jobCount = 0
            return when(jobCount > 0){
                true -> true
                false ->{
                    return when (schedulerFactoryBean.scheduler.checkExists(JobKey(jobName,group))){
                        true -> true
                        false -> false
                    }
                }
            }
        }catch (exception: SchedulerException){
            logger.severe("Error while checking job with name exists : "+exception.message)
            logger.severe(exception.stackTrace.toString())
        }

        return false
    }

    fun unscheduleJob(jobName:String, group: String):Boolean{
        try{
            return schedulerFactoryBean.scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, group))
        }catch(exception: SchedulerException){
            logger.severe("Error while unscheduling job : "+exception.message)
            logger.severe(exception.stackTrace.toString())
        }
        return false
    }
}