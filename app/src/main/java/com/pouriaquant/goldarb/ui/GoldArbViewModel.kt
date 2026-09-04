package com.pouriaquant.goldarb.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pouriaquant.goldarb.data.ArbitrageCalculator
import com.pouriaquant.goldarb.data.CostPolicy
import com.pouriaquant.goldarb.data.MarketQuote
import com.pouriaquant.goldarb.data.MarketRepository
import com.pouriaquant.goldarb.data.Opportunity
import com.pouriaquant.goldarb.data.PublicFeedMarketRepository
import com.pouriaquant.goldarb.data.TokenizedGoldComparison
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class GoldArbUiState(
    val isLoading: Boolean = true,
    val quotes: List<MarketQuote> = emptyList(),
    val opportunities: List<Opportunity> = emptyList(),
    val receivedAt: String? = null,
    val failedVenueNames: List<String> = emptyList(),
    val tokenizedGold: TokenizedGoldComparison? = null,
    val tokenizedGoldError: String? = null,
    val quantityGram: Double = 1.0,
    val policy: CostPolicy = CostPolicy(),
    val errorMessage: String? = null,
)

class GoldArbViewModel(
    private val repository: MarketRepository = PublicFeedMarketRepository(),
) : ViewModel() {
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
                state = state.copy(
                    isLoading = false,
                    quotes = snapshot.quotes,
                    opportunities = ArbitrageCalculator.evaluate(
                        snapshot.quotes,
                        state.quantityGram,
                        state.policy,
                    ),
                    receivedAt = snapshot.receivedAt,
                    failedVenueNames = snapshot.failedVenueNames,
                    tokenizedGold = snapshot.tokenizedGold,
                    tokenizedGoldError = snapshot.tokenizedGoldError,
                    errorMessage = if (snapshot.quotes.isEmpty()) "هیچ feed عمومی پاسخ نداد" else null,
                )
            }.onFailure {
                state = state.copy(isLoading = false, errorMessage = "به‌روزرسانی feedها ناموفق بود")
            }
        }
    }
}
