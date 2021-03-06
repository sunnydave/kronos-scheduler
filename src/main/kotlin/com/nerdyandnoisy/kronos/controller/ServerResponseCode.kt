package com.nerdyandnoisy.kronos.controller


const val JOB_WITH_SAME_NAME_EXISTS = 501
const val JOB_NAME_NOT_PRESENT = 502
const val JOB_ALREADY_IN_RUNNING_STATE = 510
const val JOB_NOT_IN_PAUSED_STATE = 520
const val JOB_NOT_IN_RUNNING_STATE = 521
const val JOB_DOESNT_EXIST = 500
const val ERROR = 600
const val SUCCESS  = 200

const val JOB_WITH_SAME_NAME_EXISTS_MESSAGE = "JOB WITH SAME NAME ALREADY EXISTS IN THE GROUP"
const val JOB_NAME_NOT_PRESENT_MESSAGE = "JOB WITH GIVEN NAME NOT FOUND IN GROUP"
const val JOB_ALREADY_IN_RUNNING_STATE_MESSAGE = "JOB IS ALREADY IN RUNNING STATE"
const val JOB_NOT_IN_PAUSED_STATE_MESSAGE = "JOB IS NOT IN PAUSED STATE"
const val JOB_NOT_IN_RUNNING_STATE_MESSAGE = "JOB NOT IN RUNNING STATE"
const val JOB_DOESNT_EXIST_MESSAGE = "JOB DOESN'T EXIST"
const val ERROR_MESSAGE = "UNEXPECTED ERROR"
const val SUCCESS_MESSAGE  = "SUCCESS"