package com.pouriaquant.goldarb.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ArbitrageCalculatorTest {
    @Test
    fun `deducts commission vat slippage rebalance and settlement`() {
        val buy = quote("buy", ask = 22_000_000.0, bid = 21_900_000.0)
        val sell = quote("sell", ask = 23_200_000.0, bid = 23_000_000.0)

        val result = ArbitrageCalculator.evaluate(listOf(buy, sell), quantityGram = 1.0).first()

        assertEquals(1_000_000.0, result.grossSpreadToman, 0.001)
        assertEquals(45_000.0, result.slippageReserveToman, 0.001)
        assertEquals(6_750.0, result.rebalanceReserveToman, 0.001)
        assertEquals(928_250.0, result.netProfitToman, 0.001)
        assertTrue(result.crossesSafetyThreshold)
    }

    @Test
    fun `never compares quarantined or reference-only quotes`() {
        val comparable = quote("ok", ask = 22_000_000.0, bid = 21_900_000.0)
        val quarantined = quote("q", ask = 20_000_000.0, bid = 24_000_000.0)
            .copy(quality = QuoteQuality.QUARANTINED)
        val reference = quote("r", ask = 1.0, bid = 1.0)
            .copy(quality = QuoteQuality.REFERENCE_ONLY)

        assertTrue(ArbitrageCalculator.evaluate(listOf(comparable, quarantined, reference), 1.0).isEmpty())
    }

    private fun quote(id: String, ask: Double, bid: Double) = MarketQuote(
        venueId = id,
        venueName = id,
        monogram = id.first().toString(),
        askTomanPerGram = ask,
        bidTomanPerGram = bid,
        quality = QuoteQuality.COMPARABLE,
        qualityLabel = "verified",
        feeLabel = "included",
        sourceLabel = "test",
        accent = 0xFFFFFFFF,
    )
}
