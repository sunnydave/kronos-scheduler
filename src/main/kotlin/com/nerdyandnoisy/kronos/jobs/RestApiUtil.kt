package com.nerdyandnoisy.kronos.jobs

import org.quartz.JobExecutionContext
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import java.util.logging.Logger

fun callRestApi(jobExecutionContext: JobExecutionContext):ResponseEntity<String>{
    val logger = Logger.getLogger("RestApi")
    if(jobExecutionContext.mergedJobDataMap.containsKey("method")){
        val method = jobExecutionContext.mergedJobDataMap.getString("method")
        val restTemplate = RestTemplate()
        return when(method){
            "GET" -> {
                return restTemplate.getForEntity(jobExecutionContext.mergedJobDataMap.getString("url"), String::class.java)
            }
            "POST" -> {
                val headers = HttpHeaders()
                headers.contentType= MediaType.APPLICATION_JSON
                val requestBody = jobExecutionContext.mergedJobDataMap["body"]!!
                logger.info("Request body : "+requestBody)
                var request = HttpEntity(requestBody)
                return restTemplate.postForEntity(jobExecutionContext.mergedJobDataMap.getString("url"),request, String::class.java)
            }
            else -> {
                return ResponseEntity(HttpStatus.BAD_REQUEST)
            }
        }
    }
    return ResponseEntity(HttpStatus.BAD_REQUEST)
}