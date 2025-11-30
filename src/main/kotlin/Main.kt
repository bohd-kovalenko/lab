package com.quaterion

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * Main Spring Boot application class for the Quaterion Fuel Management System.
 *
 * This application provides a REST API for tracking vehicle fuel consumption,
 * managing vehicles, and analyzing fuel statistics.
 *
 * @see com.quaterion.controller for REST endpoints
 * @see com.quaterion.service for business logic
 */
@SpringBootApplication
@EntityScan("com.quaterion.entity")
@EnableJpaRepositories("com.quaterion.repository")
class Application

/**
 * Application entry point.
 *
 * @param args command-line arguments passed to the application
 */
fun main(args: Array<String>) {
    runApplication<Application>(*args)
}