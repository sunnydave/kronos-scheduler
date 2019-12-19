package com.nerdyandnoisy.kronos.config

import org.springframework.scheduling.quartz.CronTriggerFactoryBean

class PersistableCronTriggerFactoryBean: CronTriggerFactoryBean() {

   var JOB_DETAIL_KEY = "jobDetail"

    override fun afterPropertiesSet() {
        super.afterPropertiesSet()
        jobDataMap.remove(JOB_DETAIL_KEY)
    }
}