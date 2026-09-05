package com.pouriaquant.goldarb.data

enum class QuoteQuality {
    COMPARABLE,
    QUARANTINED,
    REFERENCE_ONLY,
    UNAVAILABLE,
}

data class MarketQuote(
    val venueId: String,
    val venueName: String,
    val monogram: String,
    val askTomanPerGram: Double? = null,
    val bidTomanPerGram: Double? = null,
    val referenceTomanPerGram: Double? = null,
    val quality: QuoteQuality,
    val qualityLabel: String,
    val feeLabel: String,
    val sourceLabel: String,
    val sourceTimestamp: String? = null,
    val accent: Long,
    val buyCommissionRate: Double = 0.0,
    val sellCommissionRate: Double = 0.0,
    val pricesIncludeCommission: Boolean = true,
)

data class CostPolicy(
    val commissionVatRate: Double = 0.10,
    val buySlippageRate: Double = 0.001,
    val sellSlippageRate: Double = 0.001,
    val rebalanceRate: Double = 0.0003,
    val settlementToman: Double = 20_000.0,
    val minimumNetProfitToman: Double = 100_000.0,
)

data class Opportunity(
    val buyVenue: MarketQuote,
    val sellVenue: MarketQuote,
    val quantityGram: Double,
    val grossSpreadToman: Double,
    val commissionToman: Double,
    val commissionVatToman: Double,
    val slippageReserveToman: Double,
    val rebalanceReserveToman: Double,
    val settlementReserveToman: Double,
    val netProfitToman: Double,
) {
    val crossesSafetyThreshold: Boolean
        get() = netProfitToman >= 100_000.0
}

data class MarketSnapshot(
    val quotes: List<MarketQuote>,
    val receivedAt: String,
    val failedVenueNames: List<String>,
)

object ArbitrageCalculator {
    fun evaluate(
        quotes: List<MarketQuote>,
        quantityGram: Double,
        policy: CostPolicy = CostPolicy(),
    ): List<Opportunity> {
        if (!quantityGram.isFinite() || quantityGram <= 0) return emptyList()
        val comparable = quotes.filter {
            it.quality == QuoteQuality.COMPARABLE &&
                it.askTomanPerGram != null &&
                it.bidTomanPerGram != null
        }

        return comparable.flatMap { buy ->
            comparable.filter { it.venueId != buy.venueId }.map { sell ->
                val ask = requireNotNull(buy.askTomanPerGram)
                val bid = requireNotNull(sell.bidTomanPerGram)
                val grossBuy = ask * quantityGram
                val grossSell = bid * quantityGram
                val buyCommission = if (buy.pricesIncludeCommission) 0.0 else grossBuy * buy.buyCommissionRate
                val sellCommission = if (sell.pricesIncludeCommission) 0.0 else grossSell * sell.sellCommissionRate
                val commission = buyCommission + sellCommission
                val commissionVat = commission * policy.commissionVatRate
                val slippage = grossBuy * policy.buySlippageRate + grossSell * policy.sellSlippageRate
                val rebalance = ((ask + bid) / 2) * quantityGram * policy.rebalanceRate
                val grossSpread = grossSell - grossBuy
                Opportunity(
                    buyVenue = buy,
                    sellVenue = sell,
                    quantityGram = quantityGram,
                    grossSpreadToman = grossSpread,
                    commissionToman = commission,
                    commissionVatToman = commissionVat,
                    slippageReserveToman = slippage,
                    rebalanceReserveToman = rebalance,
                    settlementReserveToman = policy.settlementToman,
                    netProfitToman = grossSpread - commission - commissionVat - slippage - rebalance - policy.settlementToman,
                )
            }
        }.sortedByDescending { it.netProfitToman }
    }
}
