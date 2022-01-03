package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class RetweetCounterApplication

fun main(args: Array<String>) {
    runApplication<RetweetCounterApplication>(*args)
}
