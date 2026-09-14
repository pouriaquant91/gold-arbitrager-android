@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.pouriaquant.goldarb.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Brightness6
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pouriaquant.goldarb.R
import com.pouriaquant.goldarb.data.AssetPosition
import com.pouriaquant.goldarb.data.AssetSignal
import com.pouriaquant.goldarb.data.AssetTrade
import com.pouriaquant.goldarb.data.ReferencePrice
import com.pouriaquant.goldarb.data.MarketCatalog
import com.pouriaquant.goldarb.data.QuoteQuality
import com.pouriaquant.goldarb.security.AppBrightness
import com.pouriaquant.goldarb.security.AppFontScale
import com.pouriaquant.goldarb.security.AppFontFamily
import com.pouriaquant.goldarb.security.AppThemeMode
import com.pouriaquant.goldarb.security.AppVisualStyle
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.Duration
import java.time.Instant
import java.util.Locale

private val Gold = Color(0xFFE9B949)
private val Silver = Color(0xFFAEBBC8)
private val Copper = Color(0xFFC96F3B)
private val Tether = Color(0xFF26A17B)
private val Positive = Color(0xFF48D7A2)
private val Negative = Color(0xFFFF7D77)
private val Caution = Color(0xFFF4C862)
private val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("fa-IR"))

private enum class AssetPage(
    val key: String,
    val label: String,
    val symbol: String,
    val description: String,
    val color: Color,
    val venues: List<Pair<String, String>>,
    val referenceId: String,
    val referenceName: String,
) {
    GOLD("gold", "طلا", "Au", "طلای ۱۸ عیار", Gold, emptyList(), "tgju-gold", "TGJU طلا"),
    SILVER("silver", "نقره", "Ag", "نقره ۹۹۹", Silver, listOf("talanex-silver" to "طلانکس نقره", "noghresea-silver" to "نقره‌سی · پژوهشی", "iran-silver" to "بازار تخصصی نقره ایران", "silver-store" to "فروشگاه‌های شمش نقره", "ime-silver" to "بورس کالای ایران", "zarminex-silver" to "زرین‌مکس", "tehran-silver" to "بازار نقره تهران"), "tgju-silver", "TGJU نقره"),
    COPPER("copper", "مس", "Cu", "مس کاتد", Copper, listOf("meschi-copper" to "مس‌چی · پژوهشی", "ime-copper" to "بورس کالای ایران", "lme-copper" to "بورس فلزات لندن", "ahanonline-copper" to "آهن آنلاین", "ahanprice-copper" to "آهن پرایس", "markazeahan-copper" to "مرکزآهن", "iranmetals-copper" to "بازار فلزات ایران"), "tgju-copper", "TGJU مس"),
    USDT("usdt", "تتر", "₮", "USDT / تومان", Tether, listOf("wallex" to "والکس", "tabdeal" to "تبدیل", "exir" to "اکسیر", "raastin" to "راستین", "ramzinex" to "رمزینکس", "ompfinex" to "اوام‌پی‌فینکس", "nobitex" to "نوبیتکس", "sarrafex" to "صرافکس", "bitpin" to "بیت‌پین", "tetherland" to "تترلند", "bit24" to "بیت۲۴", "aban-tether" to "آبان‌تتر", "ok-exchange" to "اوکی‌اکسچنج", "arzplus" to "ارزپلاس", "toobit-fa" to "توبیت فارسی", "sarmayex" to "سرمایکس", "excoino" to "اکسکوینو", "arzpa" to "ارزپا", "farhad-exchange" to "فرهاد اکسچنج"), "tgju-usdt", "TGJU تتر"),
    SETTINGS("settings", "تنظیمات", "⚙", "ظاهر و امنیت دستگاه", Color(0xFF8EB8E7), emptyList(), "", ""),
}

private data class DisplaySignal(
    val id: String, val asset: String, val buyVenueId: String, val sellVenueId: String,
    val quantity: Double, val unit: String, val buyPrice: Double, val sellPrice: Double,
    val totalCosts: Double, val slippage: Double?, val netProfit: Double,
    val decision: String, val executionStatus: String, val sampledAt: String,
)

private data class KpiPosition(
    val initialToman: Double,
    val initialAsset: Double,
    val toman: Double,
    val asset: Double,
    val lastPrice: Double?,
)

private data class KpiMetric(val label: String, val value: String, val positive: Boolean = false, val warning: Boolean = false)

private data class VenuePriceItem(
    val id: String,
    val name: String,
    val ask: Double?,
    val bid: Double?,
    val last: Double?,
    val status: String,
    val source: String,
    val isReference: Boolean = false,
)

@Composable
fun GoldArbApp(
    biometricAvailable: Boolean,
    biometricEnabled: Boolean,
    themeMode: AppThemeMode,
    visualStyle: AppVisualStyle,
    brightness: AppBrightness,
    fontScale: AppFontScale,
    fontFamily: AppFontFamily,
    onBiometricChanged: (Boolean) -> Unit,
    onThemeModeChanged: (AppThemeMode) -> Unit,
    onVisualStyleChanged: (AppVisualStyle) -> Unit,
    onBrightnessChanged: (AppBrightness) -> Unit,
    onFontScaleChanged: (AppFontScale) -> Unit,
    onFontFamilyChanged: (AppFontFamily) -> Unit,
    viewModel: GoldArbViewModel = viewModel(),
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var page by remember { mutableStateOf(AssetPage.GOLD) }
    val state = viewModel.state
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.surface) {
                    Row(Modifier.fillMaxWidth().padding(22.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        RasadMark()
                        Column { Text("رصد", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black); Text("RASAD", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, letterSpacing = 1.5.sp) }
                        Spacer(Modifier.weight(1f))
                        LabeledIconButton("بستن منوی اصلی", { scope.launch { drawerState.close() } }) {
                            Icon(Icons.Rounded.Close, "بستن منوی اصلی")
                        }
                    }
                    HorizontalDivider()
                    Text(
                        "بازارها",
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                    )
                    AssetPage.entries.filterNot { it == AssetPage.SETTINGS }.forEach { item ->
                        NavigationDrawerItem(
                            label = { Column { Text(item.label, fontWeight = FontWeight.Bold); Text(item.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
                            icon = { ElementBadge(item.symbol, item.color, 42) },
                            selected = page == item,
                            onClick = { page = item; scope.launch { drawerState.close() } },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp),
                        )
                    }
                    HorizontalDivider(Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
                    NavigationDrawerItem(
                        label = {
                            Column {
                                Text(AssetPage.SETTINGS.label, fontWeight = FontWeight.Bold)
                                Text(
                                    "تم، روشنایی، اندازه فونت و امنیت",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        },
                        icon = { Icon(Icons.Rounded.Settings, "تنظیمات برنامه") },
                        selected = page == AssetPage.SETTINGS,
                        onClick = {
                            page = AssetPage.SETTINGS
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp),
                    )
                    Spacer(Modifier.weight(1f))
                    Row(Modifier.padding(20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Security, null, tint = Positive)
                        Column { Text("دفتر فرضی سرور", fontWeight = FontWeight.Bold); Text("بدون سفارش واقعی", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp) }
                    }
                }
            },
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) { RasadMark(30); Text("رصد", fontWeight = FontWeight.Black) } },
                        navigationIcon = {
                            LabeledIconButton("باز کردن منوی اصلی", { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Rounded.Menu, "باز کردن منوی اصلی")
                            }
                        },
                        actions = {
                            StatusPill(
                                connected = state.serverConnected,
                                loading = state.isLoading,
                                reportUpdatedAt = state.receivedAt,
                                updatedAt = if (page == AssetPage.GOLD || page == AssetPage.SETTINGS) {
                                    state.serverUpdatedAt
                                } else {
                                    state.assetHeartbeats.firstOrNull { it.asset == page.key }?.checkedAt
                                },
                            )
                            LabeledIconButton("دریافت تازه‌ترین گزارش از سرور", viewModel::refresh) {
                                if (state.isLoading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                                else Icon(Icons.Rounded.Refresh, "به‌روزرسانی گزارش سرور")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background.copy(alpha = .96f)),
                    )
                },
            ) { padding ->
                if (page == AssetPage.SETTINGS) SettingsPage(
                    padding = padding,
                    biometricAvailable = biometricAvailable,
                    biometricEnabled = biometricEnabled,
                    themeMode = themeMode,
                    visualStyle = visualStyle,
                    brightness = brightness,
                    fontScale = fontScale,
                    fontFamily = fontFamily,
                    minimumProfitPercent = state.policy.minimumNetProfitRate * 100,
                    settingsMessage = state.errorMessage,
                    onBiometricChanged = onBiometricChanged,
                    onThemeModeChanged = onThemeModeChanged,
                    onVisualStyleChanged = onVisualStyleChanged,
                    onBrightnessChanged = onBrightnessChanged,
                    onFontScaleChanged = onFontScaleChanged,
                    onFontFamilyChanged = onFontFamilyChanged,
                    onMinimumProfitPercentChanged = viewModel::setMinimumProfitPercent,
                )
                else AssetMarketPage(page, state, padding)
            }
        }
    }
}

@Composable
private fun AssetMarketPage(page: AssetPage, state: GoldArbUiState, padding: PaddingValues) {
    var historyTab by remember(page) { mutableIntStateOf(0) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val allSignals = if (page == AssetPage.GOLD) state.calculationRuns.map {
        DisplaySignal(it.id, "gold", it.buyVenueId, it.sellVenueId, it.quantityGram, "گرم", it.buyPriceToman, it.sellPriceToman, it.totalCostsToman, (it.buyPriceToman + it.sellPriceToman) * it.quantityGram * it.executionReserveRate, it.netProfitToman, it.decision, it.executionStatus, it.sampledAt)
    } else state.assetSignals.filter { it.asset == page.key }.map(AssetSignal::toDisplay)
    val signals = allSignals
        .filter { it.netProfit > 0.0 }
        .sortedByDescending { it.sampledAt }
    val trades = state.assetTrades.filter { it.asset == page.key }
    val positions = state.assetPositions.filter { it.asset == page.key }
    val kpiPositions = if (page == AssetPage.GOLD) state.positions.values.map {
        KpiPosition(it.initialTomanBalance, it.initialGoldBalanceGram, it.tomanBalance, it.goldBalanceGram, it.latestPriceToman)
    } else positions.map {
        KpiPosition(it.initialTomanBalance, it.initialAssetBalance, it.tomanBalance, it.assetBalance, it.lastPriceToman)
    }
    val priceItems = buildVenuePrices(page, state, allSignals, positions)
    val referencePrice = state.referencePrices.firstOrNull { it.asset == page.key }
    val usdReference = state.referencePrices.firstOrNull { it.asset == "usd" }
    val best = signals.firstOrNull()
    val executed = signals.firstOrNull { it.executionStatus != "calculated" }
    val venueNames = priceItems.associate { it.id to it.name }
    val venueName: (String) -> String = { id -> venueNames[id] ?: id }
    Box(Modifier.fillMaxSize()) {
      LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().background(Brush.radialGradient(listOf(page.color.copy(alpha = .16f), MaterialTheme.colorScheme.background), radius = 900f)),
        contentPadding = PaddingValues(top = padding.calculateTopPadding() + 14.dp, bottom = 92.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        item { HeroCard(page, if (page == AssetPage.GOLD) state.serverUpdatedAt else state.assetHeartbeats.firstOrNull { it.asset == page.key }?.checkedAt) }
        item { KpiDashboard(page, signals, kpiPositions) }
        item { SignalCard(page, best, venueName) }
        item { PaperTradeCard(page, executed, venueName) }
        item {
            ReferenceMarketCard(page, referencePrice, usdReference)
        }
        item { VenuePriceList(page, priceItems, state.isLoading, state.serverConnected) }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) { Icon(Icons.Rounded.History, null, tint = page.color); Text("تاریخچه سفارش‌ها و سیگنال‌ها", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
                    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(4.dp)) {
                        listOf("سیگنال‌ها", "سفارش‌های فرضی", "دارایی‌ها").forEachIndexed { index, label -> TabButton(label, historyTab == index, page.color, Modifier.weight(1f)) { historyTab = index } }
                    }
                }
            }
        }
        when (historyTab) {
            0 -> if (signals.isEmpty()) item { EmptyCard("هنوز سیگنالی ثبت نشده است.") } else items(signals.take(30), key = { it.id }) { HistorySignalRow(it, venueName, page.color) }
            1 -> if (trades.isEmpty() && page == AssetPage.GOLD && state.trades.isEmpty()) item { EmptyCard("سفارش فرضی ثبت نشده است.") } else if (page == AssetPage.GOLD) items(state.trades.take(30), key = { it.id }) { HistoryTradeRow(it.venueId, it.side, it.quantityGram, "گرم", it.totalToman, it.occurredAt, venueName) } else items(trades.take(30), key = { it.id }) { HistoryTradeRow(it.venueId, it.side, it.quantity, it.unit, it.totalToman, it.occurredAt, venueName) }
            else -> if (page == AssetPage.GOLD) items(state.positions.values.toList().take(60), key = { it.venueId }) { PositionRow(it.venueId, it.tomanBalance, it.goldBalanceGram, "گرم", venueName) } else if (positions.isEmpty()) item { EmptyCard("دفتر دارایی این بازار هنوز ایجاد نشده است.") } else items(positions, key = { it.venueId }) { PositionRow(it.venueId, it.tomanBalance, it.assetBalance, it.unit, venueName) }
        }
      }
      if (listState.firstVisibleItemIndex > 0) {
        SmallFloatingActionButton(
          onClick = { scope.launch { listState.animateScrollToItem(0) } },
          modifier = Modifier.align(Alignment.BottomStart).padding(18.dp),
          containerColor = page.color,
          contentColor = Color(0xFF111318),
        ) { Icon(Icons.Rounded.ArrowUpward, "بازگشت به ابتدای صفحه") }
      }
    }
}

private fun buildVenuePrices(
    page: AssetPage,
    state: GoldArbUiState,
    signals: List<DisplaySignal>,
    positions: List<AssetPosition>,
): List<VenuePriceItem> {
    if (page == AssetPage.GOLD) {
        val quoteById = state.quotes.associateBy { it.venueId }
        val catalog = MarketCatalog.entries.map { it.id to it.name }
        val dynamic = state.quotes.map { it.venueId to it.venueName }
        return (catalog + dynamic).distinctBy { it.first }.map { (id, name) ->
            val quote = quoteById[id]
            VenuePriceItem(
                id = id,
                name = name,
                ask = quote?.askTomanPerGram,
                bid = quote?.bidTomanPerGram,
                last = quote?.referenceTomanPerGram,
                status = quote?.qualityLabel ?: "قیمت زنده در دسترس نیست",
                source = quote?.sourceLabel ?: "در صف اتصال",
                isReference = id == page.referenceId || quote?.quality == QuoteQuality.REFERENCE_ONLY,
            )
        }.sortedWith(compareBy({ it.ask == null && it.bid == null && it.last == null }, { it.name }))
    }

    val recentSignals = signals.sortedByDescending { it.sampledAt }
    val quoteById = state.assetVenueQuotes.filter { it.asset == page.key }.associateBy { it.venueId }
    val knownVenues = buildList {
        addAll(page.venues)
        addAll(quoteById.values.map { it.venueId to it.displayName })
        recentSignals.forEach { signal ->
            add(signal.buyVenueId to signal.buyVenueId)
            add(signal.sellVenueId to signal.sellVenueId)
        }
        positions.forEach { add(it.venueId to it.venueId) }
    }.distinctBy { it.first }

    return knownVenues.map { (id, fallbackName) ->
        val liveQuote = quoteById[id]
        val buySignal = recentSignals.firstOrNull { it.buyVenueId == id }
        val sellSignal = recentSignals.firstOrNull { it.sellVenueId == id }
        val position = positions.firstOrNull { it.venueId == id }
        val ask = liveQuote?.askToman ?: buySignal?.buyPrice
        val bid = liveQuote?.bidToman ?: sellSignal?.sellPrice
        val last = position?.lastPriceToman
        VenuePriceItem(
            id = id,
            name = page.venues.firstOrNull { it.first == id }?.second ?: liveQuote?.displayName ?: fallbackName,
            ask = ask,
            bid = bid,
            last = last,
            status = when {
                ask != null && bid != null -> if (liveQuote?.executableDepth == true) "دوطرفه با عمق اجرا" else "دوطرفه؛ عمق اجرا تأیید نشده"
                ask != null || bid != null || last != null -> "آخرین گزارش پایش سرور"
                else -> "قیمت زنده در دسترس نیست"
            },
            source = if (liveQuote != null) "فید زنده سرور" else "در صف اتصال ${page.label}",
            isReference = false,
        )
    }.sortedWith(compareBy({ it.ask == null && it.bid == null && it.last == null }, { it.name }))
}

private fun AssetSignal.toDisplay() = DisplaySignal(id, asset, buyVenueId, sellVenueId, quantity, unit, buyPriceToman, sellPriceToman, totalCostsToman, slippageToman, netProfitToman, decision, executionStatus, sampledAt)

@Composable private fun HeroCard(page: AssetPage, updatedAt: String?) { Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = page.color.copy(alpha = .12f)), modifier = Modifier.border(1.dp, page.color.copy(alpha=.3f), RoundedCornerShape(28.dp))) { Row(Modifier.fillMaxWidth().padding(24.dp), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) { Text("بازار ${page.label}", color = page.color, fontWeight = FontWeight.Bold); Text(page.description, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black); Text(updatedAt ?: "در انتظار اولین پایش", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp) }; ElementBadge(page.symbol, page.color, 88) } } }

@Composable
private fun KpiDashboard(page: AssetPage, signals: List<DisplaySignal>, positions: List<KpiPosition>) {
    val tradingCapital = positions.sumOf { it.initialToman + it.initialAsset * (it.lastPrice ?: 0.0) }
    val currentEquity = positions.sumOf { it.toman + it.asset * (it.lastPrice ?: 0.0) }
    val engagedCapital = positions.sumOf { maxOf(0.0, it.asset * (it.lastPrice ?: 0.0)) }
    val accepted = signals.count { it.decision == "accepted" }
    val average = signals.map { it.netProfit }.average().takeIf { !it.isNaN() } ?: 0.0
    val profitLoss = currentEquity - tradingCapital
    var cumulative = 0.0
    var peak = 0.0
    var drawdown = 0.0
    signals.filter { it.executionStatus == "buy-and-sell" }.sortedBy { it.sampledAt }.forEach {
        cumulative += it.netProfit
        peak = maxOf(peak, cumulative)
        drawdown = maxOf(drawdown, peak - cumulative)
    }
    drawdown = maxOf(drawdown, maxOf(0.0, -profitLoss))
    val drawdownRate = if (tradingCapital > 0) drawdown / tradingCapital else 0.0
    val metrics = listOf(
        KpiMetric("سرمایه معاملاتی", money(tradingCapital)),
        KpiMetric("سرمایه درگیر", money(engagedCapital)),
        KpiMetric("فرصت شناسایی‌شده", formatter.format(signals.size)),
        KpiMetric("فرصت قابل اجرا", formatter.format(accepted), positive = true),
        KpiMetric("میانگین سود خالص", money(average), positive = average > 0),
        KpiMetric("سود و زیان", money(profitLoss), positive = profitLoss >= 0, warning = profitLoss < 0),
        KpiMetric("بیشترین افت سرمایه", "${money(drawdown)} · ${percent(drawdownRate)}", warning = drawdown > 0),
    )
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("تصویر مدیریتی", fontWeight = FontWeight.Black)
                Spacer(Modifier.weight(1f))
                Text("دفتر فرضی سرور", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            LazyRow(contentPadding = PaddingValues(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(metrics) { metric ->
                    Surface(
                        modifier = Modifier.width(154.dp),
                        shape = RoundedCornerShape(15.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .72f),
                    ) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                            Text(metric.label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(metric.value, fontWeight = FontWeight.Bold, color = when { metric.warning -> Negative; metric.positive -> Positive; else -> page.color })
                        }
                    }
                }
            }
        }
    }
}

@Composable private fun SummaryRow(page: AssetPage, signals: List<DisplaySignal>, tradeCount: Int, connected: Boolean) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { MiniStat("وضعیت", if (connected) "متصل" else "قطع", Icons.Rounded.Autorenew, page.color, Modifier.weight(1f)); MiniStat("سیگنال", formatter.format(signals.size), Icons.Rounded.Analytics, page.color, Modifier.weight(1f)); MiniStat("معامله", formatter.format(tradeCount), Icons.Rounded.SwapVert, page.color, Modifier.weight(1f)) } }
@Composable private fun MiniStat(label:String,value:String,icon:androidx.compose.ui.graphics.vector.ImageVector,color:Color,modifier:Modifier){ Card(modifier,shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){Column(Modifier.padding(12.dp),verticalArrangement=Arrangement.spacedBy(7.dp)){Icon(icon,null,tint=color,modifier=Modifier.size(19.dp));Text(label,fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant);Text(value,fontWeight=FontWeight.Bold)}} }

@Composable
private fun ReferenceMarketCard(page: AssetPage, reference: ReferencePrice?, usdReference: ReferencePrice?) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = page.color.copy(alpha = .11f)),
        modifier = Modifier.border(1.dp, page.color.copy(alpha = .28f), RoundedCornerShape(24.dp)),
    ) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ElementBadge(page.symbol, page.color, 42)
                Column(Modifier.weight(1f)) {
                    Text("مرجع زنده بازار", fontWeight = FontWeight.Black)
                    Text(reference?.label ?: page.referenceName, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
                Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(999.dp)) {
                    Text("فقط مقایسه", Modifier.padding(horizontal = 10.dp, vertical = 6.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text(
                reference?.let(::formatReferencePrice) ?: "در انتظار خوراک معتبر مرجع",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = if (reference == null) MaterialTheme.colorScheme.onSurfaceVariant else page.color,
            )
            if (usdReference != null) Text("دلار آزاد: ${money(usdReference.value)}", fontWeight = FontWeight.Bold)
            if (reference != null) Text("به‌روزرسانی: ${reference.fetchedAt}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                "این نرخ برای سنجش جهت بازار است و در مسیر خرید یا فروش فرضی استفاده نمی‌شود.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp,
            )
        }
    }
}

private fun formatReferencePrice(reference: ReferencePrice): String = when (reference.currency) {
    "USD" -> "${formatter.format(reference.value)} دلار / تن"
    else -> money(reference.value)
}

@Composable
private fun VenuePriceList(
    page: AssetPage,
    prices: List<VenuePriceItem>,
    loading: Boolean,
    connected: Boolean,
) {
    val platformPrices = prices.filterNot { it.isReference }
    val priced = platformPrices.filter { it.ask != null || it.bid != null || it.last != null }
    val waiting = platformPrices.filter { it.ask == null && it.bid == null && it.last == null }
    var waitingExpanded by remember(page, prices) { mutableStateOf(false) }
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("قیمت همه پلتفرم‌ها", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                Text(
                    "${formatter.format(priced.size)} قیمت معتبر از ${formatter.format(platformPrices.size)} منبع ${page.label}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                )
            }
            if (priced.isEmpty()) {
                val message = when {
                    loading -> "در حال دریافت قیمت‌های ${page.label} از سرور…"
                    !connected -> "ارتباط با سرور قیمت برقرار نیست؛ قیمت ساختگی نمایش داده نمی‌شود."
                    else -> "هنوز قیمت معتبر دریافت نشده؛ پایش ادامه دارد."
                }
                Surface(
                    modifier = Modifier.fillMaxWidth().semantics { stateDescription = message },
                    color = page.color.copy(alpha = .10f),
                    shape = RoundedCornerShape(15.dp),
                ) {
                    Text(message, Modifier.padding(14.dp), color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 22.sp)
                }
            }
            priced.forEachIndexed { index, item ->
                if (index > 0) HorizontalDivider()
                VenuePriceRow(page, item)
            }
            if (waiting.isNotEmpty()) {
                HorizontalDivider()
                TextButton(
                    onClick = { waitingExpanded = !waitingExpanded },
                    modifier = Modifier.fillMaxWidth().semantics {
                        stateDescription = if (waitingExpanded) "باز" else "بسته"
                    },
                ) {
                    Text(
                        if (waitingExpanded) "بستن منابع بدون قیمت" else "نمایش ${formatter.format(waiting.size)} منبع بدون قیمت",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start,
                    )
                    Icon(
                        if (waitingExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = if (waitingExpanded) "بستن فهرست" else "باز کردن فهرست",
                    )
                }
                if (waitingExpanded) waiting.forEachIndexed { index, item ->
                    if (index > 0) HorizontalDivider()
                    VenuePriceRow(page, item)
                }
            }
        }
    }
}

@Composable
private fun VenuePriceRow(page: AssetPage, item: VenuePriceItem) {
    Column(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            ElementBadge(item.name.take(1), page.color, 36)
            Column(Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold)
                Text(item.source, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
            }
            if (item.isReference) {
                Surface(color = page.color.copy(alpha = .15f), shape = RoundedCornerShape(999.dp)) {
                    Text("مرجع", Modifier.padding(horizontal = 9.dp, vertical = 5.dp), color = page.color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        PriceLine("قیمت خرید", item.ask)
        PriceLine("قیمت فروش", item.bid)
        if (item.last != null && item.ask == null && item.bid == null) PriceLine("آخرین قیمت", item.last)
        Text(item.status, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
    }
}

@Composable
private fun PriceLine(label: String, value: Double?) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        Spacer(Modifier.weight(1f))
        Text(value?.let(::money) ?: "—", fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
    }
}

@Composable
private fun SignalCard(page: AssetPage, signal: DisplaySignal?, venueName: (String) -> String) {
    var expanded by remember(signal?.id) { mutableStateOf(false) }
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("سیگنال مثبت آخر", color = page.color, fontSize = 12.sp)
                    Text(if (signal == null) "فعلاً سیگنال مثبتی نداریم" else "${venueName(signal.buyVenueId)} ← ${venueName(signal.sellVenueId)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Rounded.Analytics, null, tint = page.color)
            }
            if (signal == null) EmptyContent(page) else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RouteBox("خرید از", venueName(signal.buyVenueId), signal.buyPrice, page.color, Modifier.weight(1f))
                    Icon(Icons.Rounded.SwapVert, null, tint = page.color, modifier = Modifier.align(Alignment.CenterVertically))
                    RouteBox("فروش در", venueName(signal.sellVenueId), signal.sellPrice, page.color, Modifier.weight(1f))
                }
                HorizontalDivider()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = if (signal.decision == "accepted") Positive.copy(alpha = .14f) else Caution.copy(alpha = .14f), shape = RoundedCornerShape(999.dp)) {
                        Text(if (signal.decision == "accepted") "قابل معامله فرضی" else "مثبت، زیر آستانه معامله", color = if (signal.decision == "accepted") Positive else Caution, modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp), fontSize = 12.sp)
                    }
                    Spacer(Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.End) { Text("سود خالص", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(money(signal.netProfit), fontWeight = FontWeight.Black, color = Positive) }
                }
                TextButton(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
                    Icon(if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore, null)
                    Text(if (expanded) "بستن جزئیات مسیر" else "مسیر کامل فرصت")
                }
                if (expanded) OpportunityBreakdown(signal, page.color)
            }
        }
    }
}

@Composable
private fun OpportunityBreakdown(signal: DisplaySignal, color: Color) {
    val buyNotional = signal.buyPrice * signal.quantity
    val sellNotional = signal.sellPrice * signal.quantity
    val capital = minOf(buyNotional, sellNotional)
    val grossSpread = sellNotional - buyNotional
    val slippage = signal.slippage ?: 0.0
    val fees = maxOf(0.0, signal.totalCosts - slippage)
    fun rate(value: Double) = if (capital > 0) value / capital else 0.0
    val metrics = listOf(
        "قیمت خرید" to money(signal.buyPrice),
        "قیمت فروش" to money(signal.sellPrice),
        "اختلاف قیمت خام" to "${money(grossSpread)} · ${percent(rate(grossSpread))}",
        "کارمزد و هزینه اجرا" to "${money(fees)} · ${percent(rate(fees))}",
        "اسلیپیج" to (signal.slippage?.let { "${money(it)} · ${percent(rate(it))}" } ?: "از رکورد بعدی ثبت می‌شود"),
        "سود خالص قابل اجرا" to "${money(signal.netProfit)} · ${percent(rate(signal.netProfit))}",
        "سرمایه درگیر" to money(capital),
        "سود نهایی معامله" to "${money(signal.netProfit)} · ${percent(rate(signal.netProfit))}",
    )
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        metrics.forEach { (label, value) ->
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(11.dp)).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .65f)).padding(10.dp)) {
                Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.weight(1f))
                Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        val status = signal.executionStatus
        val steps = listOf(
            "فرصت شناسایی شد" to true,
            "خرید انجام شد" to (status == "buy" || status == "buy-and-sell"),
            "فروش انجام شد" to (status == "sell" || status == "buy-and-sell"),
            "معامله بسته شد" to (status == "buy-and-sell"),
            "سود ثبت شد" to (status == "buy-and-sell"),
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            items(steps) { (label, done) ->
                Surface(shape = RoundedCornerShape(999.dp), color = (if (done) Positive else color).copy(alpha = .12f)) {
                    Text(label, modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp), color = if (done) Positive else MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                }
            }
        }
    }
}
@Composable private fun RouteBox(label:String,venue:String,price:Double,color:Color,modifier:Modifier){Column(modifier.clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(14.dp),verticalArrangement=Arrangement.spacedBy(5.dp)){Text(label,fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant);Text(venue,fontWeight=FontWeight.Bold);Text(money(price),fontSize=12.sp,color=color)}}

@Composable private fun PaperTradeCard(page:AssetPage, signal:DisplaySignal?, venueName:(String)->String){Card(shape=RoundedCornerShape(24.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("معامله فرضی سرور",color=page.color,fontSize=12.sp);Text(if(signal==null)"معامله‌ای انجام نشده" else "در دفتر دارایی اعمال شد",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)};Icon(Icons.Rounded.Wallet,null,tint=page.color)};if(signal==null) Text("پس از عبور سود خالص از آستانه و کافی بودن موجودی هر دو طرف، معامله فرضی ثبت می‌شود.",color=MaterialTheme.colorScheme.onSurfaceVariant,lineHeight=24.sp) else {Text("${venueName(signal.buyVenueId)} ↔ ${venueName(signal.sellVenueId)}",fontWeight=FontWeight.Bold);Text("${formatter.format(signal.quantity)} ${signal.unit} خرید و فروش فرضی شد.",color=MaterialTheme.colorScheme.onSurfaceVariant);Text(money(signal.netProfit),color=Positive,fontWeight=FontWeight.Black)}}}}

@Composable private fun TabButton(label:String,selected:Boolean,color:Color,modifier:Modifier,onClick:()->Unit){Surface(modifier=modifier.semantics { contentDescription = label; this.selected = selected }.clickable(onClick=onClick),shape=RoundedCornerShape(11.dp),color=if(selected)color.copy(alpha=.18f) else Color.Transparent){Text(label,modifier=Modifier.padding(vertical=11.dp),textAlign=TextAlign.Center,color=if(selected)color else MaterialTheme.colorScheme.onSurfaceVariant,fontSize=12.sp,fontWeight=if(selected)FontWeight.Bold else FontWeight.Normal)}}
@Composable private fun HistorySignalRow(signal:DisplaySignal,venueName:(String)->String,color:Color){Card(shape=RoundedCornerShape(17.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){Row(Modifier.fillMaxWidth().padding(14.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(11.dp)){Box(Modifier.size(9.dp).clip(CircleShape).background(if(signal.decision=="accepted")Positive else Caution));Column(Modifier.weight(1f)){Text("${venueName(signal.buyVenueId)} ← ${venueName(signal.sellVenueId)}",fontWeight=FontWeight.Bold);Text(signal.sampledAt,fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)};Column(horizontalAlignment=Alignment.End){Text(money(signal.netProfit),color=Positive,fontWeight=FontWeight.Bold);Text(if(signal.executionStatus=="buy-and-sell")"اجرا در دفتر" else "سیگنال مثبت",fontSize=11.sp,color=color)}}}}
@Composable private fun HistoryTradeRow(venueId:String,side:String,quantity:Double,unit:String,total:Double,time:String,venueName:(String)->String){Card(shape=RoundedCornerShape(17.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){Row(Modifier.fillMaxWidth().padding(14.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(11.dp)){Surface(color=(if(side=="buy")Positive else Negative).copy(alpha=.14f),shape=RoundedCornerShape(9.dp)){Text(if(side=="buy")"خرید" else "فروش",color=if(side=="buy")Positive else Negative,modifier=Modifier.padding(8.dp),fontSize=11.sp)};Column(Modifier.weight(1f)){Text(venueName(venueId),fontWeight=FontWeight.Bold);Text(time,fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)};Column(horizontalAlignment=Alignment.End){Text("${formatter.format(quantity)} $unit",fontWeight=FontWeight.Bold);Text(money(total),fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)}}}}
@Composable private fun PositionRow(venueId:String,toman:Double,asset:Double,unit:String,venueName:(String)->String){Card(shape=RoundedCornerShape(17.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){Row(Modifier.fillMaxWidth().padding(14.dp),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(venueName(venueId),fontWeight=FontWeight.Bold);Text("دارایی سرور",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)};Column(horizontalAlignment=Alignment.End){Text("${formatter.format(asset)} $unit",fontWeight=FontWeight.Bold);Text(money(toman),fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)}}}}

@Composable private fun EmptyContent(page:AssetPage){Column(Modifier.fillMaxWidth().padding(vertical=34.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(8.dp)){Icon(Icons.Rounded.CloudOff,null,tint=page.color,modifier=Modifier.size(38.dp));Text(if(page==AssetPage.SILVER||page==AssetPage.COPPER)"خوراک دوطرفه معتبر لازم است" else "فرصت قابل‌قبولی ثبت نشده",fontWeight=FontWeight.Bold);Text("تا اتصال منبع معتبر، قیمت یا سود ساختگی نمایش داده نمی‌شود.",textAlign=TextAlign.Center,color=MaterialTheme.colorScheme.onSurfaceVariant,lineHeight=22.sp)}}
@Composable private fun EmptyCard(text:String){Card(shape=RoundedCornerShape(17.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){Text(text,Modifier.fillMaxWidth().padding(25.dp),textAlign=TextAlign.Center,color=MaterialTheme.colorScheme.onSurfaceVariant)}}

@Composable
private fun SettingsPage(
    padding: PaddingValues,
    biometricAvailable: Boolean,
    biometricEnabled: Boolean,
    themeMode: AppThemeMode,
    visualStyle: AppVisualStyle,
    brightness: AppBrightness,
    fontScale: AppFontScale,
    fontFamily: AppFontFamily,
    minimumProfitPercent: Double,
    settingsMessage: String?,
    onBiometricChanged: (Boolean) -> Unit,
    onThemeModeChanged: (AppThemeMode) -> Unit,
    onVisualStyleChanged: (AppVisualStyle) -> Unit,
    onBrightnessChanged: (AppBrightness) -> Unit,
    onFontScaleChanged: (AppFontScale) -> Unit,
    onFontFamilyChanged: (AppFontFamily) -> Unit,
    onMinimumProfitPercentChanged: (Double) -> Unit,
) {
    var thresholdDraft by remember(minimumProfitPercent) { mutableFloatStateOf(minimumProfitPercent.toFloat().coerceIn(0f, 10f)) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    Box(Modifier.fillMaxSize()) {
      LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = padding.calculateTopPadding() + 16.dp, bottom = 92.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("تنظیمات", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                Text(
                    "ظاهر، خوانایی و امنیت برنامه را از همین صفحه کنترل کنید.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (settingsMessage != null) {
            item {
                Surface(color = MaterialTheme.colorScheme.error.copy(alpha = .12f), shape = RoundedCornerShape(14.dp)) {
                    Text(
                        settingsMessage,
                        modifier = Modifier.fillMaxWidth().padding(13.dp),
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        item { SettingSwitch(Icons.Rounded.Fingerprint, "قفل اثر انگشت", if (biometricAvailable) "قفل محلی برنامه" else "در این دستگاه در دسترس نیست", biometricEnabled, biometricAvailable, onBiometricChanged) }
        item {
            SettingsGroup(Icons.Rounded.Analytics, "آستانه معامله فرضی", "سیگنال‌های مثبت مستقل از آستانه نمایش داده می‌شوند؛ آستانه فقط اجرای فرضی را کنترل می‌کند.") {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("حداقل سود خالص")
                    Spacer(Modifier.weight(1f))
                    Text("${String.format(Locale.US, "%.1f", thresholdDraft)}٪", fontWeight = FontWeight.Black)
                }
                Slider(
                    value = thresholdDraft,
                    onValueChange = { thresholdDraft = it },
                    valueRange = 0f..10f,
                    steps = 99,
                )
                Button(
                    onClick = { onMinimumProfitPercentChanged(thresholdDraft.toDouble()) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("ذخیره آستانه روی سرور")
                }
            }
        }
        item {
            SettingsGroup(Icons.Rounded.DarkMode, "تم نمایش", "تم سیستم، روشن یا تیره را انتخاب کنید.") {
                AppThemeMode.entries.forEach { mode ->
                    ChoiceRow(mode.displayName(), mode.description(), mode == themeMode) { onThemeModeChanged(mode) }
                }
            }
        }
        item {
            SettingsGroup(Icons.Rounded.Palette, "بستهٔ ظاهری", "رنگ هر دارایی در تمام بسته‌ها ثابت می‌ماند.") {
                AppVisualStyle.entries.forEach { style ->
                    ChoiceRow(style.displayName(), style.description(), style == visualStyle) { onVisualStyleChanged(style) }
                }
            }
        }
        item {
            SettingsGroup(Icons.Rounded.Brightness6, "روشنایی صفحه", "روشنایی فقط هنگام استفاده از رصد تغییر می‌کند.") {
                AppBrightness.entries.forEach { value ->
                    ChoiceRow(value.displayName(), value.description(), value == brightness) {
                        onBrightnessChanged(value)
                    }
                }
            }
        }
        item {
            SettingsGroup(Icons.Rounded.FormatSize, "اندازه فونت", "همه نوشته‌ها و کنترل‌های برنامه با این انتخاب مقیاس می‌گیرند.") {
                AppFontScale.entries.forEach { value ->
                    ChoiceRow(value.displayName(), value.description(), value == fontScale) {
                        onFontScaleChanged(value)
                    }
                }
            }
        }
        item {
            SettingsGroup(Icons.Rounded.FormatSize, "نوع قلم", "انتخاب شما ذخیره می‌شود؛ تا زمان افزوده‌شدن فایل‌های دارای مجوز، قلم امن سیستم نمایش داده می‌شود.") {
                AppFontFamily.entries.forEach { value ->
                    ChoiceRow(value.displayName(), value.description(), value == fontFamily) {
                        onFontFamilyChanged(value)
                    }
                }
            }
        }
      }
      if (listState.firstVisibleItemIndex > 0) {
        SmallFloatingActionButton(
          onClick = { scope.launch { listState.animateScrollToItem(0) } },
          modifier = Modifier.align(Alignment.BottomStart).padding(18.dp),
        ) { Icon(Icons.Rounded.ArrowUpward, "بازگشت به ابتدای تنظیمات") }
      }
    }
}

@Composable
private fun SettingsGroup(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, content: @Composable () -> Unit) {
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null)
                Column { Text(title, fontWeight = FontWeight.Bold); Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp) }
            }
            content()
        }
    }
}

@Composable
private fun ChoiceRow(title: String, subtitle: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().semantics { this.selected = selected }.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.Bold); Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            if (selected) Icon(Icons.Rounded.CheckCircle, "انتخاب‌شده", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

private fun AppThemeMode.displayName() = when (this) { AppThemeMode.SYSTEM -> "سیستم"; AppThemeMode.LIGHT -> "روشن"; AppThemeMode.DARK -> "تیره" }
private fun AppThemeMode.description() = when (this) { AppThemeMode.SYSTEM -> "هماهنگ با تنظیم دستگاه"; AppThemeMode.LIGHT -> "پس‌زمینه روشن و خوانا"; AppThemeMode.DARK -> "مناسب محیط کم‌نور" }
private fun AppVisualStyle.displayName() = when (this) { AppVisualStyle.GRAPHITE -> "گرافیت"; AppVisualStyle.AURORA -> "شفق"; AppVisualStyle.PAPER -> "کاغذ" }
private fun AppVisualStyle.description() = when (this) { AppVisualStyle.GRAPHITE -> "خنثی و حرفه‌ای"; AppVisualStyle.AURORA -> "مدرن با آبی و فیروزه‌ای"; AppVisualStyle.PAPER -> "گرم و پُرکنتراست" }
private fun AppBrightness.displayName() = when (this) { AppBrightness.SYSTEM -> "روشنایی دستگاه"; AppBrightness.LOW -> "کم"; AppBrightness.MEDIUM -> "متوسط"; AppBrightness.HIGH -> "زیاد" }
private fun AppBrightness.description() = when (this) { AppBrightness.SYSTEM -> "بدون تغییر روشنایی سیستم"; AppBrightness.LOW -> "۳۵٪، مناسب محیط تاریک"; AppBrightness.MEDIUM -> "۶۵٪، مناسب استفاده روزمره"; AppBrightness.HIGH -> "۱۰۰٪، مناسب محیط روشن" }
private fun AppFontScale.displayName() = when (this) { AppFontScale.SMALL -> "کوچک"; AppFontScale.NORMAL -> "معمولی"; AppFontScale.LARGE -> "بزرگ" }
private fun AppFontScale.description() = when (this) { AppFontScale.SMALL -> "۹۰٪ برای نمایش اطلاعات بیشتر"; AppFontScale.NORMAL -> "۱۰۰٪، اندازه پیشنهادی"; AppFontScale.LARGE -> "۱۱۵٪ برای خوانایی بیشتر" }
private fun AppFontFamily.displayName() = when (this) { AppFontFamily.VAZIRMATN -> "وزیرمتن"; AppFontFamily.ESTEDAD -> "استعداد"; AppFontFamily.SAHEL -> "ساحل"; AppFontFamily.SYSTEM -> "قلم سیستم" }
private fun AppFontFamily.description() = when (this) {
    AppFontFamily.VAZIRMATN -> "انتخاب پیشنهادی؛ اکنون با fallback امن سیستم"
    AppFontFamily.ESTEDAD -> "فشرده و مناسب داده؛ اکنون با fallback امن سیستم"
    AppFontFamily.SAHEL -> "نرم و خوانا؛ اکنون با fallback امن سیستم"
    AppFontFamily.SYSTEM -> "قلم پیش‌فرض دستگاه، بدون فایل اضافی"
}
@Composable private fun SettingSwitch(icon:androidx.compose.ui.graphics.vector.ImageVector,title:String,subtitle:String,checked:Boolean,enabled:Boolean,onChecked:(Boolean)->Unit){Card(shape=RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){Row(Modifier.fillMaxWidth().semantics(mergeDescendants=true){contentDescription=title;stateDescription=if(!enabled)"در دسترس نیست" else if(checked)"روشن" else "خاموش"}.padding(18.dp),verticalAlignment=Alignment.CenterVertically){Icon(icon,null);Spacer(Modifier.width(12.dp));Column(Modifier.weight(1f)){Text(title,fontWeight=FontWeight.Bold);Text(subtitle,fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)};Switch(checked,onCheckedChange=onChecked,enabled=enabled)}}}

@Composable
private fun LabeledIconButton(label: String, onClick: () -> Unit, content: @Composable () -> Unit) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        tooltip = { PlainTooltip { Text(label) } },
        state = rememberTooltipState(),
    ) {
        IconButton(onClick = onClick, content = content)
    }
}

@Composable
private fun StatusPill(connected: Boolean, loading: Boolean, reportUpdatedAt: String?, updatedAt: String?) {
    val fresh = updatedAt?.let { value ->
        runCatching { Duration.between(Instant.parse(value), Instant.now()).abs() < Duration.ofMinutes(12) }
            .getOrDefault(false)
    } ?: false
    val reportFresh = reportUpdatedAt?.let { value ->
        runCatching { Duration.between(Instant.parse(value), Instant.now()).abs() < Duration.ofMinutes(3) }
            .getOrDefault(false)
    } ?: false
    val (label, color) = when {
        loading -> "در حال دریافت" to Caution
        !connected -> "قطع ارتباط" to Negative
        updatedAt == null -> "در انتظار داده" to Caution
        fresh -> "پایش تازه" to Positive
        reportFresh -> "گزارش تازه · پایش در انتظار" to Positive
        else -> "آخرین گزارش قدیمی" to Caution
    }
    Surface(
        modifier = Modifier.semantics { stateDescription = label },
        shape = RoundedCornerShape(999.dp),
        color = color.copy(alpha = .12f),
    ) {
        Row(
            Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Box(Modifier.size(7.dp).clip(CircleShape).background(color))
            Text(label, fontSize = 11.sp, color = color)
        }
    }
}
@Composable private fun RasadMark(size:Int=42){Image(painter=painterResource(R.drawable.ic_rasad_mark),contentDescription="نشان رصد",modifier=Modifier.size(size.dp))}
@Composable private fun ElementBadge(symbol:String,color:Color,size:Int){Box(Modifier.size(size.dp).clip(RoundedCornerShape((size/3).dp)).background(color.copy(alpha=.9f)),contentAlignment=Alignment.Center){Text(symbol,color=Color(0xFF11141B),fontWeight=FontWeight.Black)}}
private fun money(value:Double)="${formatter.format(value.toLong())} تومان"
private fun percent(value:Double)="${formatter.format(value * 100)}٪"

@Composable
fun LockScreen(biometricAvailable: Boolean, onUnlock: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(MaterialTheme.colorScheme.primaryContainer.copy(alpha=.72f), MaterialTheme.colorScheme.background),radius=900f)).padding(28.dp), contentAlignment=Alignment.Center) {
        Card(shape=RoundedCornerShape(28.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)) { Column(Modifier.padding(28.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(14.dp)) { RasadMark(72); Text("رصد قفل است",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Black); Text(if(biometricAvailable)"برای مشاهده گزارش سرور قفل دستگاه را باز کنید." else "قفل امن در این دستگاه در دسترس نیست.",textAlign=TextAlign.Center,color=MaterialTheme.colorScheme.onSurfaceVariant); Surface(modifier=Modifier.fillMaxWidth().clickable(enabled=biometricAvailable,onClick=onUnlock),shape=RoundedCornerShape(16.dp),color=if(biometricAvailable)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant){Row(Modifier.padding(15.dp),horizontalArrangement=Arrangement.Center,verticalAlignment=Alignment.CenterVertically){Icon(if(biometricAvailable)Icons.Rounded.Fingerprint else Icons.Rounded.Lock,null,tint=if(biometricAvailable)MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant);Spacer(Modifier.width(8.dp));Text("باز کردن",color=if(biometricAvailable)MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,fontWeight=FontWeight.Bold)}} } }
    }
}
