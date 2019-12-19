package com.nerdyandnoisy.kronos.jobs

import com.nerdyandnoisy.kronos.service.JobService
import org.quartz.JobExecutionContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean
import java.util.logging.Logger

class SimpleJob: QuartzJobBean() {
    var logger = Logger.getLogger(SimpleJob::class.java.name)

    override fun executeInternal(jobExecutionContext: JobExecutionContext) {
        logger.info("Simple Job Start")
        logger.info("Simple Job End")

    }
}