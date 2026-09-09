@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.pouriaquant.goldarb.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pouriaquant.goldarb.data.MarketQuote
import com.pouriaquant.goldarb.data.Opportunity
import com.pouriaquant.goldarb.data.QuoteQuality
import com.pouriaquant.goldarb.data.ServerOpportunityRun
import com.pouriaquant.goldarb.data.VenuePosition
import com.pouriaquant.goldarb.security.AppThemeMode
import com.pouriaquant.goldarb.security.AppVisualStyle
import com.pouriaquant.goldarb.ui.theme.Coral400
import com.pouriaquant.goldarb.ui.theme.Gold400
import com.pouriaquant.goldarb.ui.theme.Ink200
import com.pouriaquant.goldarb.ui.theme.Ink400
import com.pouriaquant.goldarb.ui.theme.Mint400
import com.pouriaquant.goldarb.ui.theme.Outline
import com.pouriaquant.goldarb.ui.theme.Pine800
import com.pouriaquant.goldarb.ui.theme.Pine850
import com.pouriaquant.goldarb.ui.theme.Pine900
import com.pouriaquant.goldarb.ui.theme.Pine950
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private enum class AppSection(val label: String, val icon: ImageVector) {
    MARKET("قیمت‌ها", Icons.Rounded.Dashboard),
    OPPORTUNITIES("فرصت‌ها", Icons.Rounded.SwapVert),
    PORTFOLIO("دارایی‌ها", Icons.Rounded.AccountBalanceWallet),
    COVERAGE("منابع", Icons.Rounded.Storage),
    SETTINGS("تنظیمات", Icons.Rounded.Settings),
}

@Composable
fun GoldArbApp(
    biometricAvailable: Boolean,
    biometricEnabled: Boolean,
    themeMode: AppThemeMode,
    visualStyle: AppVisualStyle,
    onBiometricChanged: (Boolean) -> Unit,
    onThemeModeChanged: (AppThemeMode) -> Unit,
    onVisualStyleChanged: (AppVisualStyle) -> Unit,
    viewModel: GoldArbViewModel = viewModel(),
) {
    var sectionIndex by remember { mutableIntStateOf(0) }
    val state = viewModel.state

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets.statusBars,
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    windowInsets = WindowInsets.navigationBars,
                ) {
                    AppSection.entries.forEachIndexed { index, section ->
                        NavigationBarItem(
                            selected = index == sectionIndex,
                            onClick = { sectionIndex = index },
                            icon = { Icon(section.icon, contentDescription = section.label) },
                            label = { Text(section.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTextColor = Gold400,
                                indicatorColor = Gold400,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                    }
                }
            },
        ) { padding ->
            when (AppSection.entries[sectionIndex]) {
                AppSection.MARKET -> MarketScreen(state, viewModel::refresh, padding)
                AppSection.OPPORTUNITIES -> OpportunityScreen(state, padding)
                AppSection.PORTFOLIO -> PortfolioScreen(state, viewModel::recordVenueConversion, viewModel::resetPositions, padding)
                AppSection.COVERAGE -> CoverageScreen(padding)
                AppSection.SETTINGS -> SettingsScreen(
                    padding = padding,
                    biometricAvailable = biometricAvailable,
                    biometricEnabled = biometricEnabled,
                    themeMode = themeMode,
                    visualStyle = visualStyle,
                    onBiometricChanged = onBiometricChanged,
                    onThemeModeChanged = onThemeModeChanged,
                    onVisualStyleChanged = onVisualStyleChanged,
                    minimumProfitRate = state.policy.minimumNetProfitRate,
                    onMinimumProfitPercentChanged = viewModel::setMinimumProfitPercent,
                )
            }
        }
    }
}

@Composable
private fun ScreenHeader(
    title: String,
    eyebrow: String,
    isLoading: Boolean = false,
    onRefresh: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(eyebrow, style = MaterialTheme.typography.labelMedium, color = Gold400)
            Text(title, style = MaterialTheme.typography.headlineMedium)
        }
        if (onRefresh != null) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.clickable(enabled = !isLoading, onClick = onRefresh),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(19.dp), strokeWidth = 2.dp, color = Gold400)
                    } else {
                        Icon(Icons.Rounded.Refresh, contentDescription = "به‌روزرسانی", tint = Gold400)
                    }
                    Text(if (isLoading) "در حال دریافت" else "به‌روزرسانی", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun MarketScreen(state: GoldArbUiState, onRefresh: () -> Unit, padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f), MaterialTheme.colorScheme.background),
                    radius = 900f,
                ),
            )
            .padding(padding),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            ScreenHeader("زرگَرد", "دیده‌بان قیمت طلا", state.isLoading, onRefresh)
        }
        item { SafetyHero(state.opportunities.firstOrNull(), state.quotes.size, state.failedVenueNames.size) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.fillMaxWidth()) {
                MetricCard("منابع", "۵۳", "بررسی‌شده", Modifier.weight(1f))
                MetricCard("قیمت‌ها", toPersianDigits(state.quotes.size), "دریافت‌شده", Modifier.weight(1f))
                MetricCard("ناموفق", toPersianDigits(state.failedVenueNames.size), "این نوبت", Modifier.weight(1f), Coral400)
            }
        }
        item {
            SectionTitle("قیمت‌های بازار", "زمان هر قیمت کنار آن نوشته شده است")
        }
        if (state.errorMessage != null) {
            item { NoticeCard(Icons.Rounded.CloudOff, "داده زنده در دسترس نیست", state.errorMessage, Coral400) }
        }
        items(state.quotes, key = { it.venueId }) { quote -> QuoteCard(quote) }
        if (state.failedVenueNames.isNotEmpty()) {
            item {
                NoticeCard(
                    Icons.Rounded.WarningAmber,
                    "${state.failedVenueNames.size} پاسخ ناموفق در این نوبت",
                    state.failedVenueNames.joinToString("، "),
                    Coral400,
                )
            }
        }
        item {
            Text(
                text = state.receivedAt?.let { "آخرین دریافت: ${formatInstant(it)}" } ?: "در حال دریافت نخستین قیمت‌ها…",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun MonitoringPlanCard() {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(22.dp), border = CardDefaults.outlinedCardBorder()) {
        Column(modifier = Modifier.padding(17.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("پایش برگشت جهت", style = MaterialTheme.typography.titleMedium)
                    Text("72h initial screening · Iranian 18k gold", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusPill("فعال", Mint400)
            }
            Text("فقط جفت‌های دارای bid/ask مستقیم، دو نمونهٔ پیوسته و سود بالاتر از درصد تنظیم‌شدهٔ قیمت کمتر جفت بررسی می‌شوند.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("تغییر جهت بین همان دو پلتفرم شرط تکرارپذیری است؛ تترگلد فعلاً متوقف شده.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SafetyHero(best: Opportunity?, receivedCount: Int, failedCount: Int) {
    val safe = best?.crossesSafetyThreshold == true
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(26.dp),
        border = CardDefaults.outlinedCardBorder(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(Gold400.copy(alpha = 0.13f), Mint400.copy(alpha = 0.03f)),
                    ),
                )
                .padding(20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                StatusPill(if (safe) "فرصت ثبت‌شده" else "در حال پایش", if (safe) Mint400 else Gold400)
                Text(
                    if (safe) formatToman(best!!.netProfitToman) else "قیمت‌ها در حال به‌روزرسانی‌اند",
                    style = MaterialTheme.typography.displaySmall,
                    color = if (safe) Mint400 else MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    if (best == null) {
                        "آخرین قیمت‌های دریافت‌شده را ببینید و چند لحظه بعد دوباره بررسی کنید."
                    } else {
                        "خرید از ${best.buyVenue.venueName} و فروش در ${best.sellVenue.venueName} برای ${best.quantityGram} گرم."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("قیمت دریافت‌شده: ${toPersianDigits(receivedCount)}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("ناموفق: ${toPersianDigits(failedCount)}", style = MaterialTheme.typography.labelMedium, color = if (failedCount > 0) Coral400 else Mint400)
                }
            }
        }
    }
}

@Composable
private fun QuoteCard(quote: MarketQuote) {
    val accent = Color(quote.accent)
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder(),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.size(42.dp).clip(RoundedCornerShape(14.dp)).background(accent.copy(alpha = 0.16f)).border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center,
                ) { Text(quote.monogram, color = accent, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
                Spacer(Modifier.width(11.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(quote.venueName, style = MaterialTheme.typography.titleMedium)
                    Text(quote.sourceLabel, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusPill(
                    when (quote.quality) {
                        QuoteQuality.COMPARABLE -> "قابل مقایسه"
                        QuoteQuality.REFERENCE_ONLY -> "قیمت مرجع"
                        QuoteQuality.QUARANTINED -> "در حال بررسی"
                        QuoteQuality.UNAVAILABLE -> "در حال دریافت"
                    },
                    qualityColor(quote.quality),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                when {
                    quote.askTomanPerGram != null && quote.bidTomanPerGram != null -> {
                        PriceCell("قیمت خرید", quote.askTomanPerGram, Modifier.weight(1f), Coral400)
                        PriceCell("قیمت فروش", quote.bidTomanPerGram, Modifier.weight(1f), Mint400)
                    }
                    quote.referenceTomanPerGram != null -> {
                        PriceCell("قیمت مرجع", quote.referenceTomanPerGram, Modifier.weight(1f), Gold400)
                    }
                }
            }
            if (quote.askTomanPerGram == null && quote.bidTomanPerGram == null && quote.referenceTomanPerGram == null) {
                Text("قیمت معتبر این منبع فعلاً دریافت نشده؛ ردیف حذف نشده است.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            quote.sourceTimestamp?.let {
                Text("زمان منبع: $it", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun PriceCell(label: String, value: Double, modifier: Modifier, color: Color) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(15.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(formatToman(value), style = MaterialTheme.typography.titleMedium, color = color, maxLines = 1)
        Text("تومان / گرم", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun OpportunityScreen(state: GoldArbUiState, padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { ScreenHeader("فرصت‌ها", "مقایسه قیمت خرید و فروش") }
        item {
            NoticeCard(
                Icons.Rounded.Security,
                "آستانه پویا ${toPersianDigits((state.policy.minimumNetProfitRate * 100).toInt())}٪",
                "حداقل سود از درصد قیمت کمتر دو سمت و حجم معامله محاسبه می‌شود.",
                Gold400,
            )
        }
        if (state.opportunities.isEmpty()) {
            item {
                EmptyOpportunityCard()
            }
        } else {
            items(state.opportunities) { OpportunityCard(it) }
        }
        item { SectionTitle("فرصت‌های ثبت‌شده", "فقط نمایش؛ بدون ارسال سفارش") }
        if (!state.serverConnected) {
            item {
                NoticeCard(
                    Icons.Rounded.CloudOff,
                    "وضعیت تازه دریافت نشد",
                    "آخرین قیمت‌ها همچنان نمایش داده می‌شوند و دوباره تلاش می‌کنیم.",
                    Coral400,
                )
            }
        } else if (state.serverRuns.isEmpty()) {
            item { NoticeCard(Icons.Rounded.Storage, "هنوز رویدادی ثبت نشده", "در حال حاضر فرصت فعالی برای نمایش وجود ندارد.", Gold400) }
        } else {
            items(state.serverRuns.take(8), key = { "${it.routeKey}:${it.startedAt}" }) { ServerRunCard(it) }
            item {
                Text(
                    state.serverUpdatedAt?.let { "آخرین بررسی: ${formatInstant(it)}" } ?: "وضعیت به‌روز است",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
        }
        item { SectionTitle("روش استفاده", "مقایسه در دو حساب جدا") }
        item {
            StrategyStep("۱", "انتخاب دو پلتفرم", "قیمت خرید یک پلتفرم با قیمت فروش پلتفرم دیگر مقایسه می‌شود.")
            StrategyStep("۲", "بررسی موجودی", "پیش از اقدام، موجودی ریالی و طلایی هر دو حساب را بررسی کنید.")
            StrategyStep("۳", "بررسی قیمت نهایی", "قیمت نهایی را در خود پلتفرم‌ها دوباره ببینید.")
            StrategyStep("۴", "تأیید شما", "زرگرد سفارشی ارسال نمی‌کند و تصمیم نهایی با شماست.")
        }
    }
}

@Composable
private fun ServerRunCard(run: ServerOpportunityRun) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(20.dp), border = CardDefaults.outlinedCardBorder()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("${venueName(run.buyVenueId)} ← ${venueName(run.sellVenueId)}", style = MaterialTheme.typography.titleMedium)
                StatusPill(if (run.status == "active") "فعال" else "بسته", if (run.status == "active") Mint400 else Gold400)
            }
            Text("مدت ${formatDuration(run.durationMs)} · ${toPersianDigits(run.sampleCount)} بار بررسی", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("سود برآوردی ${formatToman(run.latestNetProfitToman)} · بیشترین ${formatToman(run.peakNetProfitToman)}", color = Gold400, fontWeight = FontWeight.Bold)
            Text("فقط ثبت شده است؛ سفارشی ارسال نشده.", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun formatDuration(durationMs: Long?): String {
    if (durationMs == null) return "در حال اندازه‌گیری"
    val minutes = durationMs / 60_000.0
    return if (minutes < 1) {
        "${toPersianDigits((durationMs / 1_000).toInt())} ثانیه"
    } else {
        String.format(Locale.US, "%.1f دقیقه", minutes).replace('.', '٫')
    }
}

private fun venueName(id: String): String = mapOf(
    "milli" to "میلی",
    "ecogold" to "اکوگلد",
    "baazar" to "بازار",
    "zarafza" to "زرافزا",
    "talapp" to "طلاپ",
    "geramino" to "گرامینو",
    "goldika" to "گلدیکا",
    "daric" to "داریک",
)[id] ?: id

@Composable
private fun EmptyOpportunityCard() {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(24.dp), border = CardDefaults.outlinedCardBorder()) {
        Column(modifier = Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Rounded.Autorenew, contentDescription = null, tint = Gold400, modifier = Modifier.size(34.dp))
            Text("هنوز مقایسه‌ای برای نمایش آماده نیست", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
            Text("قیمت‌ها در حال به‌روزرسانی‌اند. چند لحظه بعد دوباره بررسی کنید.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun OpportunityCard(opportunity: Opportunity) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(20.dp), border = CardDefaults.outlinedCardBorder()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${opportunity.buyVenue.venueName} ← ${opportunity.sellVenue.venueName}", style = MaterialTheme.typography.titleMedium)
                Text(formatToman(opportunity.netProfitToman), color = if (opportunity.crossesSafetyThreshold) Mint400 else Coral400, fontWeight = FontWeight.Bold)
            }
            Text("اختلاف نهایی پس از هزینه‌های شناخته‌شده", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                if (opportunity.inventoryReady) "موجودی دو سمت آماده است" else "نیازمند تومان در سکوی خرید و طلا در سکوی فروش",
                style = MaterialTheme.typography.labelMedium,
                color = if (opportunity.inventoryReady) Mint400 else Gold400,
            )
            Text("آستانه این جفت: ${formatToman(opportunity.minimumRequiredProfitToman)}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PortfolioScreen(state: GoldArbUiState, onConvert: (String) -> Unit, onReset: () -> Unit, padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { ScreenHeader("دارایی پلتفرم‌ها", "همگام با حساب آزمایشی سرور") }
        item {
            NoticeCard(Icons.Rounded.AccountBalanceWallet, "شروع با ۵۰ میلیون تومان برای هر پلتفرم", "پس از ثبت خرید، همان پلتفرم دارنده طلا محسوب می‌شود و فقط در سمت فروش قابل استفاده است.", Mint400)
        }
        items(state.quotes, key = { "position-${it.venueId}" }) { quote ->
            PositionCard(quote, state.positions[quote.venueId], onConvert)
        }
        item {
            Surface(
                shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth().clickable(onClick = onReset),
            ) { Text("بازنشانی حساب آزمایشی روی سرور", modifier = Modifier.padding(15.dp), textAlign = TextAlign.Center, color = Coral400) }
        }
    }
}

@Composable
private fun PositionCard(quote: MarketQuote, position: VenuePosition?, onConvert: (String) -> Unit) {
    if (position == null) return
    val holdingGold = position.goldBalanceGram > 0
    val canConvert = if (holdingGold) quote.bidTomanPerGram != null else quote.askTomanPerGram != null
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(18.dp), border = CardDefaults.outlinedCardBorder()) {
        Column(modifier = Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(quote.venueName, style = MaterialTheme.typography.titleMedium)
                StatusPill(if (holdingGold) "دارنده طلا" else "دارنده تومان", if (holdingGold) Gold400 else Mint400)
            }
            Text("تومان: ${formatToman(position.tomanBalance)} · طلا: ${String.format(Locale.US, "%.4f", position.goldBalanceGram).replace('.', '٫')} گرم", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (canConvert) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth().clickable(enabled = canConvert) { onConvert(quote.venueId) },
            ) {
                Text(if (holdingGold) "ثبت فروش کل طلا" else "ثبت خرید با کل موجودی", modifier = Modifier.padding(12.dp), textAlign = TextAlign.Center, color = if (canConvert) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun CoverageScreen(padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { ScreenHeader("منابع", "وضعیت دریافت قیمت") }
        item { CoverageBar() }
        item { CoverageBucket("۵۳", "منابع بررسی‌شده", "فهرست پلتفرم‌هایی که تاکنون بررسی شده‌اند", Mint400, Icons.Rounded.CheckCircle) }
        item { CoverageBucket("۴", "آماده مقایسه", "قیمت خرید و فروشِ قابل مقایسه دارند", Gold400, Icons.Rounded.WarningAmber) }
        item { CoverageBucket("۳۳", "فقط نمایش", "قیمت آن‌ها نمایش داده می‌شود اما در مقایسه نهایی نیست", Color(0xFF8EB8E7), Icons.Rounded.Analytics) }
        item { CoverageBucket("۱۶", "در حال بررسی", "برای دریافت قیمت پایدار دوباره بررسی می‌شوند", Coral400, Icons.Rounded.CloudOff) }
    }
}

@Composable
private fun CoverageBar() {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(24.dp), border = CardDefaults.outlinedCardBorder()) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("پوشش فعلی", style = MaterialTheme.typography.titleMedium)
                Text("وضعیت منابع", color = Mint400, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape)) {
                Box(Modifier.weight(4f).fillMaxSize().background(Mint400))
                Box(Modifier.weight(13f).fillMaxSize().background(Gold400))
                Box(Modifier.weight(20f).fillMaxSize().background(Color(0xFF8EB8E7)))
                Box(Modifier.weight(1f).fillMaxSize().background(MaterialTheme.colorScheme.outline))
                Box(Modifier.weight(18f).fillMaxSize().background(Coral400.copy(alpha = 0.55f)))
            }
            Text("فقط قیمت‌هایی که منبع و زمان مشخص دارند در مقایسه استفاده می‌شوند.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SettingsScreen(
    padding: PaddingValues,
    biometricAvailable: Boolean,
    biometricEnabled: Boolean,
    themeMode: AppThemeMode,
    visualStyle: AppVisualStyle,
    onBiometricChanged: (Boolean) -> Unit,
    onThemeModeChanged: (AppThemeMode) -> Unit,
    onVisualStyleChanged: (AppVisualStyle) -> Unit,
    minimumProfitRate: Double,
    onMinimumProfitPercentChanged: (Double) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { ScreenHeader("تنظیمات زرگَرد", "امنیت و ظاهر") }
        item {
            ToggleSettingRow(
                icon = if (biometricEnabled) Icons.Rounded.Fingerprint else Icons.Rounded.LockOpen,
                title = "قفل اثر انگشت",
                value = if (biometricAvailable) "قفل اپ پس از خروج" else "در این دستگاه در دسترس نیست",
                checked = biometricEnabled,
                enabled = biometricAvailable,
                onCheckedChange = onBiometricChanged,
            )
        }
        item { SectionTitle("استراتژی سرور", "درصد از قیمت کمتر دو سمت؛ مشترک با PWA") }
        item {
            ChoiceSettingRow(
                icon = Icons.Rounded.Analytics,
                title = "حداقل سود پویا",
                choices = listOf("۲٪" to 2.0, "۵٪" to 5.0, "۱۰٪" to 10.0),
                selected = minimumProfitRate * 100,
                onSelected = onMinimumProfitPercentChanged,
            )
        }
        item { SectionTitle("ظاهر برنامه", "انتخاب روشنایی و رنگ‌بندی") }
        item {
            ChoiceSettingRow(
                icon = Icons.Rounded.DarkMode,
                title = "روشنایی",
                choices = listOf("تیره" to AppThemeMode.DARK, "خودکار" to AppThemeMode.SYSTEM, "روشن" to AppThemeMode.LIGHT),
                selected = themeMode,
                onSelected = onThemeModeChanged,
            )
        }
        item {
            ChoiceSettingRow(
                icon = Icons.Rounded.Palette,
                title = "رنگ‌بندی",
                choices = listOf("مشکی و طلایی" to AppVisualStyle.OBSIDIAN_CHAMPAGNE, "سرمه‌ای و طلایی" to AppVisualStyle.MIDNIGHT_NAVY_WARM_GOLD),
                selected = visualStyle,
                onSelected = onVisualStyleChanged,
            )
        }
        item { SectionTitle("درباره برنامه", "نسخه اندروید ۰٫۱۱٫۰") }
        item {
            NoticeCard(
                Icons.Rounded.Security,
                "شیوه کار",
                "زرگرد قیمت‌ها را نمایش و مقایسه می‌کند، اما سفارشی ثبت نمی‌کند. پیش از هر اقدامی قیمت نهایی و موجودی حساب‌ها را بررسی کنید.",
                Mint400,
            )
        }
    }
}

@Composable
fun LockScreen(biometricAvailable: Boolean, onUnlock: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(28.dp),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier.fillMaxWidth().clickable(enabled = biometricAvailable, onClick = onUnlock),
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Icon(Icons.Rounded.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(42.dp))
                Text("زرگَرد قفل است", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                Text(
                    if (biometricAvailable) "برای باز کردن، لمس کنید و هویت خود را تأیید کنید." else "قفل زیستی دستگاه در دسترس نیست.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                if (biometricAvailable) Icon(Icons.Rounded.Fingerprint, contentDescription = "باز کردن", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
            }
        }
    }
}

@Composable
private fun ToggleSettingRow(
    icon: ImageVector,
    title: String,
    value: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp)).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(38.dp).clip(CircleShape).background(if (checked) Mint400.copy(alpha = 0.18f) else Gold400.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = if (checked) "$title روشن" else "$title خاموش", tint = if (checked) Mint400 else Gold400, modifier = Modifier.size(23.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary),
        )
    }
}

@Composable
private fun <T> ChoiceSettingRow(
    icon: ImageVector,
    title: String,
    choices: List<Pair<String, T>>,
    selected: T,
    onSelected: (T) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.titleMedium)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            choices.forEach { (label, value) ->
                val active = value == selected
                Text(
                    text = label,
                    color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                        .background(if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onSelected(value) }.padding(horizontal = 6.dp, vertical = 10.dp),
                )
            }
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, note: String, modifier: Modifier, valueColor: Color = Gold400) {
    Column(modifier = modifier.clip(RoundedCornerShape(17.dp)).background(MaterialTheme.colorScheme.surface).border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(17.dp)).padding(12.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        Text(value, style = MaterialTheme.typography.titleLarge, color = valueColor)
        Text(note, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(subtitle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun StatusPill(label: String, color: Color) {
    Text(
        text = label,
        color = color,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.clip(CircleShape).background(color.copy(alpha = 0.11f)).border(1.dp, color.copy(alpha = 0.3f), CircleShape).padding(horizontal = 10.dp, vertical = 5.dp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun NoticeCard(icon: ImageVector, title: String, body: String, accent: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(MaterialTheme.colorScheme.surface).border(1.dp, accent.copy(alpha = 0.26f), RoundedCornerShape(18.dp)).padding(15.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(accent.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun StrategyStep(number: String, title: String, body: String) {
    Row(modifier = Modifier.padding(vertical = 7.dp), verticalAlignment = Alignment.Top) {
        Box(Modifier.size(30.dp).clip(CircleShape).background(Gold400.copy(alpha = 0.13f)), contentAlignment = Alignment.Center) {
            Text(toPersianDigits(number.toInt()), color = Gold400, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CoverageBucket(value: String, title: String, body: String, accent: Color, icon: ImageVector) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(19.dp), border = CardDefaults.outlinedCardBorder()) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clip(RoundedCornerShape(15.dp)).background(accent.copy(alpha = 0.13f)), contentAlignment = Alignment.Center) {
                Text(value, color = accent, style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(icon, contentDescription = null, tint = accent.copy(alpha = 0.8f), modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
private fun ResearchLane(code: String, title: String, body: String, tag: String) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(MaterialTheme.colorScheme.surface).border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp)).padding(15.dp), verticalAlignment = Alignment.Top) {
        Text(code, color = Gold400, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(tag, style = MaterialTheme.typography.labelMedium, color = Gold400)
            }
            Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SettingRow(icon: ImageVector, title: String, value: String, accent: Color) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(MaterialTheme.colorScheme.surface).border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = accent)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun qualityColor(quality: QuoteQuality): Color = when (quality) {
    QuoteQuality.COMPARABLE -> Mint400
    QuoteQuality.QUARANTINED -> Gold400
    QuoteQuality.REFERENCE_ONLY -> Color(0xFF8EB8E7)
    QuoteQuality.UNAVAILABLE -> Coral400
}

private fun formatToman(value: Double): String = "${NumberFormat.getNumberInstance(Locale("fa")).format(value.toLong())} تومان"

private fun toPersianDigits(value: Int): String = value.toString().map { c ->
    if (c.isDigit()) "۰۱۲۳۴۵۶۷۸۹"[c.digitToInt()] else c
}.joinToString("")

private fun formatInstant(value: String): String = runCatching {
    DateTimeFormatter.ofPattern("HH:mm:ss", Locale("fa"))
        .withZone(ZoneId.systemDefault())
        .format(Instant.parse(value))
}.getOrDefault(value)
