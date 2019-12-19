package com.nerdyandnoisy.kronos.config

import com.nerdyandnoisy.kronos.service.TriggerListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class SchedulerConfig {

    @Autowired
    lateinit var datasource: DataSource

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Autowired
    lateinit var triggerListener: TriggerListener



}