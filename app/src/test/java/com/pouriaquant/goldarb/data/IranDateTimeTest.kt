package com.pouriaquant.goldarb.data

import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IranDateTimeTest {
    @Test
    fun `converts Goldis Jalali timestamp to UTC`() {
        assertEquals(
            "2026-09-03T13:52:22Z",
            IranDateTime.jalaliToIso("1405/06/12", "17:22:22"),
        )
        assertEquals(
            "2026-09-03T13:52:22Z",
            IranDateTime.jalaliToIso("۱۴۰۵/۰۶/۱۲", "۱۷:۲۲:۲۲"),
        )
    }

    @Test
    fun `rejects stale and malformed timestamps`() {
        assertTrue(
            IranDateTime.isFresh(
                "2026-09-03T13:52:22Z",
                Instant.parse("2026-09-03T13:52:24Z"),
            ),
        )
        assertFalse(
            IranDateTime.isFresh(
                "2026-09-03T13:40:00Z",
                Instant.parse("2026-09-03T13:52:24Z"),
            ),
        )
        assertEquals(null, IranDateTime.jalaliToIso("1405/13/12", "17:22:22"))
    }
}
