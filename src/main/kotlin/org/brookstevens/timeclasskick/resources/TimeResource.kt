package org.brookstevens.timeclasskick.resources

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.text.SimpleDateFormat
import java.util.*

val sdfDate = SimpleDateFormat("yyyy-dd-MM")
val sdfTime = SimpleDateFormat("HHmmss")

@RestController
class TimeResource {
    companion object TimeResource {
        init {
            sdfDate.timeZone = TimeZone.getTimeZone("UTC")
            sdfTime.timeZone = TimeZone.getTimeZone("UTC")
        }
        var dateGenerator: DateGenerator = DefaultDataGenerator()
        fun overrideDateGenerator(override: DateGenerator) {
            dateGenerator = override
        }
    }

    @GetMapping("/date-time")
    fun time(): DateTime {
        val now = dateGenerator.now()
        return DateTime(sdfDate.format(now), sdfTime.format(now))
    }
}

// API Representation of date/time
data class DateTime (val date: String, val time: String)

// Used for testing
interface DateGenerator {
    fun now(): Date
}

class DefaultDataGenerator: DateGenerator {
    override fun now(): Date {
        return Date()
    }
}