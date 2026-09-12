package com.pouriaquant.goldarb.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pouriaquant.goldarb.data.ArbitrageCalculator
import com.pouriaquant.goldarb.data.AccountUser
import com.pouriaquant.goldarb.data.CostPolicy
import com.pouriaquant.goldarb.data.MarketQuote
import com.pouriaquant.goldarb.data.MarketRepository
import com.pouriaquant.goldarb.data.Opportunity
import com.pouriaquant.goldarb.data.PortfolioSummary
import com.pouriaquant.goldarb.data.PublicFeedMarketRepository
import com.pouriaquant.goldarb.data.ServerOpportunityRun
import com.pouriaquant.goldarb.data.StrategyCalculationRun
import com.pouriaquant.goldarb.data.InventoryTrade
import com.pouriaquant.goldarb.data.VenuePosition
import com.pouriaquant.goldarb.security.AppPreferences
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
    val portfolio: PortfolioSummary? = null,
    val calculationRuns: List<StrategyCalculationRun> = emptyList(),
    val trades: List<InventoryTrade> = emptyList(),
    val account: AccountUser? = null,
    val accountMessage: String? = null,
)

class GoldArbViewModel(
    application: Application,
    private val repository: MarketRepository = PublicFeedMarketRepository(),
) : AndroidViewModel(application) {
    private val preferences = AppPreferences(application)
    var state by mutableStateOf(GoldArbUiState())
        private set

    init {
        preferences.sessionToken?.let { token ->
            viewModelScope.launch {
                val account = withContext(Dispatchers.IO) { repository.account(token) }
                if (account == null) preferences.sessionToken = null
                state = state.copy(account = account)
            }
        }
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
                    portfolio = strategyState?.portfolio ?: state.portfolio,
                    calculationRuns = snapshot.calculationRuns,
                    trades = strategyState?.trades ?: state.trades,
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
            val token = preferences.sessionToken ?: return@launch
            runCatching { withContext(Dispatchers.IO) { repository.updateMinimumProfitRate(rate, token) } }
                .onSuccess(::applyStrategyState)
                .onFailure { state = state.copy(errorMessage = "ذخیره درصد سود روی سرور ناموفق بود") }
        }
    }

    fun login(identifier: String, password: String) {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) { repository.login(identifier, password) }
            }.onSuccess {
                preferences.sessionToken = it.token
                state = state.copy(account = it.user, accountMessage = "ورود انجام شد")
            }.onFailure { state = state.copy(accountMessage = "اطلاعات ورود معتبر نیست") }
        }
    }

    fun register(username: String, email: String, displayName: String, password: String) {
        viewModelScope.launch {
            runCatching { withContext(Dispatchers.IO) { repository.register(username, email, displayName, password) } }
                .onSuccess {
                    preferences.sessionToken = it.token
                    state = state.copy(account = it.user, accountMessage = "حساب ساخته شد")
                }.onFailure { state = state.copy(accountMessage = "ثبت‌نام ناموفق بود") }
        }
    }

    fun saveProfile(displayName: String, phone: String) {
        val token = preferences.sessionToken ?: return
        viewModelScope.launch {
            runCatching { withContext(Dispatchers.IO) { repository.updateProfile(token, displayName, phone) } }
                .onSuccess { state = state.copy(account = it, accountMessage = "اطلاعات ذخیره شد") }
                .onFailure { state = state.copy(accountMessage = "ذخیره اطلاعات ناموفق بود") }
        }
    }

    fun logout() {
        val token = preferences.sessionToken
        preferences.sessionToken = null
        state = state.copy(account = null, accountMessage = null)
        if (token != null) viewModelScope.launch { runCatching { withContext(Dispatchers.IO) { repository.logout(token) } } }
    }

    private fun applyStrategyState(server: com.pouriaquant.goldarb.data.ServerStrategyState) {
        val policy = state.policy.copy(minimumNetProfitRate = server.minimumProfitRate)
        state = state.copy(
            policy = policy,
            positions = server.positions,
            portfolio = server.portfolio,
            trades = server.trades,
            serverConnected = true,
            serverUpdatedAt = server.updatedAt,
            opportunities = ArbitrageCalculator.evaluate(state.quotes, state.quantityGram, policy, server.positions),
            errorMessage = null,
        )
    }
}
