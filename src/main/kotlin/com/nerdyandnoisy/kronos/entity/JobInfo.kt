package com.nerdyandnoisy.kronos.entity

data class JobInfo (
        val jobName: String,
        val groupName: String,
        val scheduleTime: String,
        val lastFiredTime: String,
        val nextFireTime: String,
        val jobStatus: String,
        val description: String
)