package com.nerdyandnoisy.kronos.controller

import com.nerdyandnoisy.kronos.entity.SchedulerJobInfo
import com.nerdyandnoisy.kronos.entity.ServerResponse
import com.nerdyandnoisy.kronos.service.JobService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.logging.Logger

@RestController
@RequestMapping("/scheduler/")
class JobController {

    @Autowired
    lateinit var jobService: JobService

    private var logger = Logger.getLogger(JobController::class.java.name)

    @PostMapping
    fun schedule(@RequestBody jobInfo: SchedulerJobInfo): ServerResponse {
        logger.info("Saving Job Info : "+jobInfo.jobData)
        return when(jobInfo.jobName.isEmpty() || jobInfo.jobName.isBlank()){
            true -> ServerResponse(JOB_NAME_NOT_PRESENT, JOB_NAME_NOT_PRESENT_MESSAGE)
            false -> {
                when(jobService.isJobWithNamePresent(jobInfo.jobName, jobInfo.jobGroup)){
                    true -> ServerResponse(JOB_WITH_SAME_NAME_EXISTS, JOB_WITH_SAME_NAME_EXISTS_MESSAGE)
                    false -> {
                        when(jobInfo.cronJob){
                            false -> {
                                when(jobService.scheduleOneTimeJob(jobInfo)){
                                    true -> ServerResponse(SUCCESS, SUCCESS_MESSAGE)
                                    false -> ServerResponse(ERROR, ERROR_MESSAGE)
                                }
                            }
                            true -> {
                                when(jobService.scheduleCronJob(jobInfo)){
                                    true -> ServerResponse(SUCCESS, SUCCESS_MESSAGE)
                                    false -> ServerResponse(ERROR, ERROR_MESSAGE)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @PostMapping("{group}/{jobName}/unschedule")
    fun unschedule(@PathVariable(value="jobName")jobName:String, @PathVariable(value="group")group: String): ServerResponse {
        return when(jobService.unscheduleJob(jobName,group)){
            true-> ServerResponse(SUCCESS, SUCCESS_MESSAGE)
            false-> ServerResponse(ERROR, ERROR_MESSAGE)
        }
    }

    @PostMapping("{group}/{jobName}/delete")
    fun delete(@PathVariable(value="jobName")jobName:String, @PathVariable(value = "group")group: String): ServerResponse {
        return when(jobService.isJobWithNamePresent(jobName,group)){
            false -> ServerResponse(JOB_NAME_NOT_PRESENT, JOB_NAME_NOT_PRESENT_MESSAGE)
            true -> {
                when(jobService.deleteJob(jobName,group)){
                    true -> ServerResponse(SUCCESS, SUCCESS_MESSAGE)
                    false -> ServerResponse(ERROR, ERROR_MESSAGE)
                }
            }
        }
    }

    @PostMapping("{group}/{jobName}/pause")
    fun pause(@PathVariable(value="jobName")jobName:String, @PathVariable(value="group")group: String): ServerResponse {
        return when(jobService.isJobWithNamePresent(jobName,group)){
            false -> ServerResponse(JOB_DOESNT_EXIST, JOB_DOESNT_EXIST_MESSAGE)
            true -> {
                when(jobService.isJobRunning(jobName,group)){
                    false -> ServerResponse(JOB_ALREADY_IN_RUNNING_STATE, JOB_ALREADY_IN_RUNNING_STATE_MESSAGE)
                    true -> {
                        when(jobService.pauseJob(jobName, group)){
                            true -> ServerResponse(SUCCESS, SUCCESS_MESSAGE)
                            false -> ServerResponse(ERROR, ERROR_MESSAGE)
                        }
                    }
                }
            }
        }
    }

    @PostMapping("{group}/{jobName}/resume")
    fun resume(@PathVariable(value="jobName")jobName:String, @PathVariable(value = "group") group: String): ServerResponse {
        return when(jobService.isJobWithNamePresent(jobName,group)){
            true -> return when(jobService.isJobRunning(jobName, group)){
                true -> ServerResponse(JOB_ALREADY_IN_RUNNING_STATE, JOB_ALREADY_IN_RUNNING_STATE_MESSAGE)
                false -> {
                    return when(jobService.resumeJob(jobName, group)){
                        true -> ServerResponse(SUCCESS, SUCCESS_MESSAGE)
                        false -> ServerResponse(ERROR, ERROR_MESSAGE)
                    }
                }
            }
            false -> ServerResponse(JOB_WITH_SAME_NAME_EXISTS, JOB_WITH_SAME_NAME_EXISTS_MESSAGE)
        }
    }

    @PostMapping("update")
    fun update(@RequestBody jobInfo: SchedulerJobInfo): ServerResponse {
        return when (jobInfo.jobName.isBlank() || jobInfo.jobName.isEmpty()){
            true -> ServerResponse(JOB_NAME_NOT_PRESENT, JOB_NAME_NOT_PRESENT_MESSAGE)
            false -> {
                when(jobService.isJobWithNamePresent(jobInfo.jobName,jobInfo.jobGroup)){
                    false -> ServerResponse(JOB_DOESNT_EXIST, JOB_DOESNT_EXIST_MESSAGE)
                    true -> {
                        when(jobInfo.cronExpression.isBlank() || jobInfo.cronExpression.isEmpty()){
                            true -> {
                                when(jobService.updateOneTimeJob(jobInfo)){
                                    true -> ServerResponse(SUCCESS, SUCCESS_MESSAGE)
                                    false -> ServerResponse(ERROR, ERROR_MESSAGE)
                                }
                            }
                            false -> {
                                when(jobService.updateCronJob(jobInfo)){
                                    true -> ServerResponse(SUCCESS, SUCCESS_MESSAGE)
                                    false -> ServerResponse(ERROR, ERROR_MESSAGE)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @GetMapping
    fun getAllJobs() = ServerResponse(SUCCESS, jobService.getAllJobs())

    @GetMapping("{group}/{jobName}/checkJobName")
    fun checkJobName(@PathVariable(value="jobName")jobName: String, @PathVariable(value="group")group: String) =
            ServerResponse(SUCCESS, jobService.isJobWithNamePresent(jobName, group))

    @GetMapping("{group}/{jobName}/isJobRunning")
    fun isJobRunning(@PathVariable(value="jobName")jobName:String, @PathVariable(value = "group")group: String) =
            ServerResponse(SUCCESS, jobService.isJobRunning(jobName, group))

    @GetMapping("{group}/{jobName}/jobState")
    fun getJobState(@PathVariable(value="jobName")jobName:String, @PathVariable(value ="group")group: String) =
            ServerResponse(SUCCESS, jobService.getJobState(jobName, group))

    @PostMapping("{group}/{jobName}/stop")
    fun stopJob(@PathVariable(value="jobName")jobName:String, @PathVariable(value = "group")group: String): ServerResponse {
        return when(jobService.isJobWithNamePresent(jobName,group)){
            false -> ServerResponse(JOB_NAME_NOT_PRESENT, JOB_NAME_NOT_PRESENT_MESSAGE)
            true -> {
                when(jobService.isJobRunning(jobName,group)){
                    false -> ServerResponse(JOB_NOT_IN_RUNNING_STATE, JOB_NOT_IN_RUNNING_STATE_MESSAGE)
                    true -> {
                        when(jobService.stopjob(jobName,group)){
                            true -> ServerResponse(SUCCESS, SUCCESS_MESSAGE)
                            false -> ServerResponse(ERROR, ERROR_MESSAGE)
                        }
                    }
                }
            }
        }
    }

    @PostMapping("{group}/{jobName}/start")
    fun startJob(@PathVariable(value="jobName") jobName:String, @PathVariable(value = "group")group: String): ServerResponse {
        return when (jobService.isJobWithNamePresent(jobName,group)) {
            false -> ServerResponse(JOB_NAME_NOT_PRESENT, JOB_NAME_NOT_PRESENT_MESSAGE)
            true -> {
                return when (jobService.isJobRunning(jobName,group)) {
                    true -> ServerResponse(JOB_ALREADY_IN_RUNNING_STATE, JOB_ALREADY_IN_RUNNING_STATE_MESSAGE)
                    false -> {
                        return when (jobService.startJobNow(jobName,group)) {
                            true -> ServerResponse(SUCCESS, SUCCESS_MESSAGE)
                            false -> ServerResponse(ERROR, ERROR_MESSAGE)
                        }
                    }
                }
            }
        }
    }
}