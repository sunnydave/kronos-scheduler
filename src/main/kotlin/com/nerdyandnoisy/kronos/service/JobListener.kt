package com.nerdyandnoisy.kronos.service

import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.quartz.JobListener
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class JobListener: JobListener {
    private var logger = Logger.getLogger(JobListener::class.java.name)
    override fun getName(): String {
        return "globalJob"
    }

    override fun jobToBeExecuted(p0: JobExecutionContext?) {
        logger.info("jobToBeExecuted")
    }

    override fun jobWasExecuted(p0: JobExecutionContext?, p1: JobExecutionException?) {
        logger.info("jobWasExecuted")
    }

    override fun jobExecutionVetoed(p0: JobExecutionContext?) {
        logger.info("jobExecutionVeteod")
    }
}