package com.nerdyandnoisy.kronos

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KronosSchedulerApplication

fun main(args: Array<String>) {
	runApplication<KronosSchedulerApplication>(*args)
}
