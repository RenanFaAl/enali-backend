package br.com.enali

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EnaliApplication

fun main(args: Array<String>) {
	runApplication<EnaliApplication>(*args)
}
