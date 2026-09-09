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
import com.pouriaquant.goldarb.security.AppPreferences
import java.time.Instant
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
    private val preferences = AppPreferences(application)
    var state by mutableStateOf(GoldArbUiState(
        policy = CostPolicy(minimumNetProfitRate = preferences.minimumNetProfitRate),
        positions = preferences.loadPositions(),
    ))
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
                state = state.copy(
                    isLoading = false,
                    quotes = snapshot.quotes,
                    opportunities = ArbitrageCalculator.evaluate(
                        snapshot.quotes,
                        state.quantityGram,
                        state.policy,
                        state.positions,
                    ),
                    receivedAt = snapshot.receivedAt,
                    failedVenueNames = snapshot.failedVenueNames,
                    serverRuns = snapshot.serverRuns,
                    serverConnected = snapshot.serverConnected,
                    serverUpdatedAt = snapshot.serverUpdatedAt,
                    errorMessage = if (snapshot.quotes.isEmpty()) "هنوز قیمتی دریافت نشده است" else null,
                )
            }.onFailure {
                state = state.copy(isLoading = false, errorMessage = "دریافت قیمت‌های تازه ناموفق بود")
            }
        }
    }

    fun setMinimumProfitPercent(percent: Double) {
        val rate = (percent / 100).coerceIn(0.0, 1.0)
        preferences.minimumNetProfitRate = rate
        val policy = state.policy.copy(minimumNetProfitRate = rate)
        state = state.copy(policy = policy, opportunities = ArbitrageCalculator.evaluate(state.quotes, state.quantityGram, policy, state.positions))
    }

    fun recordVenueConversion(venueId: String) {
        val quote = state.quotes.firstOrNull { it.venueId == venueId } ?: return
        val position = state.positions[venueId] ?: return
        val next = when {
            position.goldBalanceGram > 0 && quote.bidTomanPerGram != null -> position.copy(
                tomanBalance = position.tomanBalance + position.goldBalanceGram * quote.bidTomanPerGram,
                goldBalanceGram = 0.0,
                updatedAt = Instant.now().toString(),
            )
            position.tomanBalance > 0 && quote.askTomanPerGram != null -> position.copy(
                goldBalanceGram = position.goldBalanceGram + position.tomanBalance / quote.askTomanPerGram,
                tomanBalance = 0.0,
                updatedAt = Instant.now().toString(),
            )
            else -> return
        }
        val positions = state.positions + (venueId to next)
        preferences.savePositions(positions)
        state = state.copy(positions = positions, opportunities = ArbitrageCalculator.evaluate(state.quotes, state.quantityGram, state.policy, positions))
    }

    fun resetPositions() {
        preferences.resetPositions()
        val positions = preferences.loadPositions()
        state = state.copy(positions = positions, opportunities = ArbitrageCalculator.evaluate(state.quotes, state.quantityGram, state.policy, positions))
    }
}
