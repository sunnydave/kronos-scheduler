package com.nerdyandnoisy.kronos.jobs

import org.quartz.JobExecutionContext
import org.springframework.scheduling.quartz.QuartzJobBean
import java.util.logging.Logger

class RestApiJob: QuartzJobBean() {

    private val logger = Logger.getLogger(RestApiJob::class.java.name)
    override fun executeInternal(context: JobExecutionContext) {
        logger.info("Rest Api Job Start")
        val response = callRestApi(context)
        logger.info("Rest Api Job End")
    }
}