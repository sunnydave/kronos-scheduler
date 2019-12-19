package com.nerdyandnoisy.kronos.jobs

import com.sun.istack.internal.logging.Logger
import org.quartz.DisallowConcurrentExecution
import org.quartz.JobExecutionContext
import org.springframework.scheduling.quartz.QuartzJobBean

@DisallowConcurrentExecution
class CronJob: QuartzJobBean() {
    private val logger = Logger.getLogger(CronJob::class.java)
    override fun executeInternal(context: JobExecutionContext) {
        logger.info("Sample job start")
        logger.info("Sample job end")
    }

}