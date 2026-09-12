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
    val minimumNetProfitRate: Double = 0.005,
)

const val DEFAULT_VENUE_TOMAN_BALANCE = 50_000_000.0

data class VenuePosition(
    val venueId: String,
    val tomanBalance: Double = DEFAULT_VENUE_TOMAN_BALANCE,
    val goldBalanceGram: Double = 0.0,
    val updatedAt: String,
    val initialTomanBalance: Double = DEFAULT_VENUE_TOMAN_BALANCE,
    val initialGoldBalanceGram: Double = 0.0,
    val latestPriceToman: Double? = null,
    val currentEquityToman: Double = DEFAULT_VENUE_TOMAN_BALANCE,
    val profitLossToman: Double = 0.0,
    val returnRate: Double = 0.0,
)

data class PortfolioSummary(
    val initialToman: Double,
    val currentToman: Double,
    val profitLossToman: Double,
    val returnRate: Double,
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
    val minimumRequiredProfitToman: Double,
    val inventoryReady: Boolean,
) {
    val crossesSafetyThreshold: Boolean
        get() = inventoryReady && netProfitToman >= minimumRequiredProfitToman
}

data class MarketSnapshot(
    val quotes: List<MarketQuote>,
    val receivedAt: String,
    val failedVenueNames: List<String>,
    val serverRuns: List<ServerOpportunityRun> = emptyList(),
    val serverConnected: Boolean = false,
    val serverUpdatedAt: String? = null,
    val strategyState: ServerStrategyState? = null,
    val calculationRuns: List<StrategyCalculationRun> = emptyList(),
    val assetSignals: List<AssetSignal> = emptyList(),
    val assetTrades: List<AssetTrade> = emptyList(),
    val assetPositions: List<AssetPosition> = emptyList(),
    val assetHeartbeats: List<AssetHeartbeat> = emptyList(),
)

data class AssetSignal(
    val id: String,
    val asset: String,
    val routeKey: String,
    val buyVenueId: String,
    val sellVenueId: String,
    val quantity: Double,
    val unit: String,
    val buyPriceToman: Double,
    val sellPriceToman: Double,
    val totalCostsToman: Double,
    val netProfitToman: Double,
    val minimumRequiredProfitToman: Double,
    val decision: String,
    val executionStatus: String,
    val sampledAt: String,
)

data class AssetTrade(
    val id: String,
    val signalId: String,
    val asset: String,
    val venueId: String,
    val side: String,
    val quantity: Double,
    val unit: String,
    val totalToman: Double,
    val occurredAt: String,
)

data class AssetPosition(
    val asset: String,
    val venueId: String,
    val unit: String,
    val initialTomanBalance: Double,
    val initialAssetBalance: Double,
    val tomanBalance: Double,
    val assetBalance: Double,
    val lastPriceToman: Double?,
    val updatedAt: String,
)

data class AssetHeartbeat(
    val asset: String,
    val status: String,
    val sourceCount: Int,
    val errorCount: Int,
    val checkedAt: String,
)

data class ServerStrategyState(
    val schemaVersion: Int,
    val storage: String,
    val minimumProfitRate: Double,
    val revision: Long,
    val updatedAt: String?,
    val positions: Map<String, VenuePosition>,
    val portfolio: PortfolioSummary,
    val trades: List<InventoryTrade> = emptyList(),
)

data class InventoryTrade(
    val id: String,
    val venueId: String,
    val side: String,
    val quantityGram: Double,
    val totalToman: Double,
    val routeKey: String?,
    val occurredAt: String,
)

data class StrategyCalculationRun(
    val id: String,
    val routeKey: String,
    val buyVenueId: String,
    val sellVenueId: String,
    val decision: String,
    val quantityGram: Double,
    val buyPriceToman: Double,
    val sellPriceToman: Double,
    val netProfitToman: Double,
    val minimumRequiredProfitToman: Double,
    val sampledAt: String,
    val executionStatus: String,
)

data class AccountUser(
    val id: String,
    val username: String,
    val email: String,
    val displayName: String,
    val phone: String?,
    val role: String,
    val licensePlan: String,
)

data class AuthSession(val user: AccountUser, val token: String)

data class ServerOpportunityRun(
    val routeKey: String,
    val buyVenueId: String,
    val sellVenueId: String,
    val status: String,
    val mode: String,
    val startedAt: String,
    val endedAt: String?,
    val durationMs: Long?,
    val sampleCount: Int,
    val peakNetProfitToman: Double,
    val latestNetProfitToman: Double,
    val updatedAt: String,
)

object ArbitrageCalculator {
    fun evaluate(
        quotes: List<MarketQuote>,
        quantityGram: Double,
        policy: CostPolicy = CostPolicy(),
        positions: Map<String, VenuePosition> = emptyMap(),
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
                val minimumRequiredProfit = minOf(ask, bid) * quantityGram * policy.minimumNetProfitRate
                val buyPosition = positions[buy.venueId]
                val sellPosition = positions[sell.venueId]
                val inventoryReady = buyPosition != null && sellPosition != null &&
                    buyPosition.tomanBalance >= grossBuy && sellPosition.goldBalanceGram >= quantityGram
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
                    minimumRequiredProfitToman = minimumRequiredProfit,
                    inventoryReady = inventoryReady,
                )
            }
        }.sortedByDescending { it.netProfitToman }
    }
}
