package com.nerdyandnoisy.kronos.service

import org.quartz.JobExecutionContext
import org.quartz.Trigger
import org.quartz.TriggerListener
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class TriggerListener: TriggerListener {

    private var logger = Logger.getLogger(TriggerListener::class.java.name)
    override fun getName(): String {
        return "globalTrigger"
    }

    override fun triggerFired(trigger: Trigger?, context: JobExecutionContext?) {
        logger.info("TriggerListener triggerFired")
    }

    override fun vetoJobExecution(p0: Trigger?, p1: JobExecutionContext?): Boolean {
        logger.info("TriggerListener vetoJobExecution")
        return false
    }

    override fun triggerMisfired(trigger: Trigger?) {
        logger.info("TriggerListener triggerMisfired")
        var jobName: String? = trigger?.jobKey?.name
        logger.info("Job name : "+jobName+" is misfired")
    }

    override fun triggerComplete(p0: Trigger?, p1: JobExecutionContext?, p2: Trigger.CompletedExecutionInstruction?) {
        logger.info("TriggerListener triggerComplete")
    }
}