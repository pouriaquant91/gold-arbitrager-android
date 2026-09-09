package com.pouriaquant.goldarb.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pouriaquant.goldarb.data.ArbitrageCalculator
import com.pouriaquant.goldarb.data.CostPolicy
import com.pouriaquant.goldarb.data.MarketQuote
import com.pouriaquant.goldarb.data.MarketRepository
import com.pouriaquant.goldarb.data.Opportunity
import com.pouriaquant.goldarb.data.PublicFeedMarketRepository
import com.pouriaquant.goldarb.data.ServerOpportunityRun
import com.pouriaquant.goldarb.data.VenuePosition
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class GoldArbUiState(
    val isLoading: Boolean = true,
    val quotes: List<MarketQuote> = emptyList(),
    val opportunities: List<Opportunity> = emptyList(),
    val receivedAt: String? = null,
    val failedVenueNames: List<String> = emptyList(),
    val quantityGram: Double = 1.0,
    val policy: CostPolicy = CostPolicy(),
    val errorMessage: String? = null,
    val serverRuns: List<ServerOpportunityRun> = emptyList(),
    val serverConnected: Boolean = false,
    val serverUpdatedAt: String? = null,
    val positions: Map<String, VenuePosition> = emptyMap(),
)

class GoldArbViewModel(
    application: Application,
    private val repository: MarketRepository = PublicFeedMarketRepository(),
) : AndroidViewModel(application) {
    var state by mutableStateOf(GoldArbUiState())
        private set

    init {
        refresh()
    }

    fun refresh() {
        if (state.isLoading && state.quotes.isNotEmpty()) return
        state = state.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) { repository.refresh() }
            }.onSuccess { snapshot ->
                val strategyState = snapshot.strategyState
                val positions = strategyState?.positions ?: state.positions
                val policy = strategyState?.let {
                    state.policy.copy(minimumNetProfitRate = it.minimumProfitRate)
                } ?: state.policy
                state = state.copy(
                    isLoading = false,
                    quotes = snapshot.quotes,
                    opportunities = ArbitrageCalculator.evaluate(
                        snapshot.quotes,
                        state.quantityGram,
                        policy,
                        positions,
                    ),
                    receivedAt = snapshot.receivedAt,
                    failedVenueNames = snapshot.failedVenueNames,
                    serverRuns = snapshot.serverRuns,
                    serverConnected = snapshot.serverConnected,
                    serverUpdatedAt = strategyState?.updatedAt ?: snapshot.serverUpdatedAt,
                    positions = positions,
                    policy = policy,
                    errorMessage = if (snapshot.quotes.isEmpty()) "هنوز قیمتی دریافت نشده است" else null,
                )
            }.onFailure {
                state = state.copy(isLoading = false, errorMessage = "دریافت قیمت‌های تازه ناموفق بود")
            }
        }
    }

    fun setMinimumProfitPercent(percent: Double) {
        val rate = (percent / 100).coerceIn(0.0, 1.0)
        viewModelScope.launch {
            runCatching { withContext(Dispatchers.IO) { repository.updateMinimumProfitRate(rate) } }
                .onSuccess(::applyStrategyState)
                .onFailure { state = state.copy(errorMessage = "ذخیره درصد سود روی سرور ناموفق بود") }
        }
    }

    fun recordVenueConversion(venueId: String) {
        val quote = state.quotes.firstOrNull { it.venueId == venueId } ?: return
        val position = state.positions[venueId] ?: return
        val selling = position.goldBalanceGram > 0
        val price = if (selling) quote.bidTomanPerGram else quote.askTomanPerGram
        if (price == null) return
        val quantity = if (selling) position.goldBalanceGram else position.tomanBalance / price
        val occurredAt = Instant.now().toString()
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    repository.recordTrade(
                        id = UUID.randomUUID().toString(),
                        venueId = venueId,
                        side = if (selling) "sell" else "buy",
                        quantityGram = quantity,
                        totalToman = quantity * price,
                        occurredAt = occurredAt,
                    )
                }
            }.onSuccess(::applyStrategyState)
                .onFailure { state = state.copy(errorMessage = "ثبت معامله آزمایشی روی سرور ناموفق بود") }
        }
    }

    fun resetPositions() {
        viewModelScope.launch {
            runCatching { withContext(Dispatchers.IO) { repository.resetStrategyState() } }
                .onSuccess(::applyStrategyState)
                .onFailure { state = state.copy(errorMessage = "بازنشانی حساب آزمایشی روی سرور ناموفق بود") }
        }
    }

    private fun applyStrategyState(server: com.pouriaquant.goldarb.data.ServerStrategyState) {
        val policy = state.policy.copy(minimumNetProfitRate = server.minimumProfitRate)
        state = state.copy(
            policy = policy,
            positions = server.positions,
            serverConnected = true,
            serverUpdatedAt = server.updatedAt,
            opportunities = ArbitrageCalculator.evaluate(state.quotes, state.quantityGram, policy, server.positions),
            errorMessage = null,
        )
    }
}
