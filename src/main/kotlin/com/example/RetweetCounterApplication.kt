package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RetweetCounterApplication

fun main(args: Array<String>) {
	runApplication<RetweetCounterApplication>(*args)
}
