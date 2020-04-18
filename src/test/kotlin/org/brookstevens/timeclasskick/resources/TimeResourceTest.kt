package org.brookstevens.timeclasskick.resources

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.text.SimpleDateFormat
import java.util.*

internal class TimeResourceTest{
    @Test
    fun `test date`() {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val hardCodedDate = formatter.parse("2018-11-30 10:01:00EDT")

        TimeResource.overrideDateGenerator(TestDataGenerator(hardCodedDate))
        val actual = TimeResource().time()
        assertEquals("2018-30-11", actual.date)
        assertEquals("140100", actual.time)
    }
}

class TestDataGenerator(val dateToReturn: Date): DateGenerator {
    override fun now(): Date {
        return dateToReturn;
    }
}