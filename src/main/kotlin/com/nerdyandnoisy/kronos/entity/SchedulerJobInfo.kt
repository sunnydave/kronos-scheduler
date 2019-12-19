package com.nerdyandnoisy.kronos.entity

import java.io.Serializable
import java.util.*


data class SchedulerJobInfo (
        val jobName: String,
        val jobGroup: String,
        val jobClass: String,
        val description: String,
        val cronExpression: String,
        val repeatTime: Date,
        val cronJob: Boolean = false,
        val jobData: JobData
)

data class JobData(
        val url: String,
        val method: String,
        val body: Map<String, Any?>
): Serializable