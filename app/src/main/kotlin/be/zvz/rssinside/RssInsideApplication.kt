package be.zvz.rssinside

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RssInsideApplication

fun main(args: Array<String>) {
    runApplication<RssInsideApplication>(*args)
}
