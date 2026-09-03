@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.pouriaquant.goldarb.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.NotificationsActive
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
    MARKET("بازار", Icons.Rounded.Dashboard),
    OPPORTUNITIES("فرصت‌ها", Icons.Rounded.SwapVert),
    COVERAGE("پوشش", Icons.Rounded.Storage),
    SETTINGS("تنظیمات", Icons.Rounded.Settings),
}

@Composable
fun GoldArbApp(viewModel: GoldArbViewModel = viewModel()) {
    var sectionIndex by remember { mutableIntStateOf(0) }
    val state = viewModel.state

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            containerColor = Pine950,
            contentWindowInsets = WindowInsets.statusBars,
            bottomBar = {
                NavigationBar(
                    containerColor = Pine900,
                    windowInsets = WindowInsets.navigationBars,
                ) {
                    AppSection.entries.forEachIndexed { index, section ->
                        NavigationBarItem(
                            selected = index == sectionIndex,
                            onClick = { sectionIndex = index },
                            icon = { Icon(section.icon, contentDescription = section.label) },
                            label = { Text(section.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Pine950,
                                selectedTextColor = Gold400,
                                indicatorColor = Gold400,
                                unselectedIconColor = Ink400,
                                unselectedTextColor = Ink400,
                            ),
                        )
                    }
                }
            },
        ) { padding ->
            when (AppSection.entries[sectionIndex]) {
                AppSection.MARKET -> MarketScreen(state, viewModel::refresh, padding)
                AppSection.OPPORTUNITIES -> OpportunityScreen(state, padding)
                AppSection.COVERAGE -> CoverageScreen(padding)
                AppSection.SETTINGS -> SettingsScreen(state, padding)
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
            Surface(shape = CircleShape, color = Pine850, border = CardDefaults.outlinedCardBorder()) {
                IconButton(onClick = onRefresh, enabled = !isLoading) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(19.dp), strokeWidth = 2.dp, color = Gold400)
                    } else {
                        Icon(Icons.Rounded.Refresh, contentDescription = "به‌روزرسانی", tint = Gold400)
                    }
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
                    colors = listOf(Pine800.copy(alpha = 0.65f), Pine950),
                    radius = 900f,
                ),
            )
            .padding(padding),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            ScreenHeader("دیدبان طلا", "GOLDARB / نسخه Android", state.isLoading, onRefresh)
        }
        item { SafetyHero(state.opportunities.firstOrNull(), state.quotes.size, state.failedVenueNames.size) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.fillMaxWidth()) {
                MetricCard("کل سکوها", "۵۲", "کاتالوگ", Modifier.weight(1f))
                MetricCard("feed زنده", "۱۵", "Node معتبر", Modifier.weight(1f))
                MetricCard("فاقد feed", "۳۰", "نیازمند کشف", Modifier.weight(1f), Coral400)
            }
        }
        item {
            SectionTitle("نرخ‌های مستقیم روی موبایل", "bid/ask جعلی تولید نمی‌شود")
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
                text = state.receivedAt?.let { "آخرین دریافت دستگاه: ${formatInstant(it)}" } ?: "در حال دریافت نخستین snapshot…",
                style = MaterialTheme.typography.labelMedium,
                color = Ink400,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun SafetyHero(best: Opportunity?, receivedCount: Int, failedCount: Int) {
    val safe = best?.crossesSafetyThreshold == true
    Card(
        colors = CardDefaults.cardColors(containerColor = Pine900),
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
                StatusPill(if (safe) "عبور از گیت ایمنی" else "حالت پایش — معامله خاموش", if (safe) Mint400 else Gold400)
                Text(
                    if (safe) formatToman(best!!.netProfitToman) else "سیگنال قابل‌اجرا نداریم",
                    style = MaterialTheme.typography.displaySmall,
                    color = if (safe) Mint400 else MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    if (best == null) {
                        "فقط quote دوطرفه، هم‌زمان و دارای fee تأییدشده مقایسه می‌شود. تک‌نرخ میلی به bid/ask ساختگی تبدیل نشده است."
                    } else {
                        "خرید ${best.buyVenue.venueName} ← فروش ${best.sellVenue.venueName} برای ${best.quantityGram} گرم، پس از همه ذخیره‌های هزینه."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink200,
                )
                HorizontalDivider(color = Outline)
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("دریافت موفق دستگاه: ${toPersianDigits(receivedCount)}", style = MaterialTheme.typography.labelMedium, color = Ink400)
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
        colors = CardDefaults.cardColors(containerColor = Pine900),
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
                    Text(quote.sourceLabel, style = MaterialTheme.typography.labelMedium, color = Ink400)
                }
                StatusPill(quote.qualityLabel, qualityColor(quote.quality))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                when {
                    quote.askTomanPerGram != null && quote.bidTomanPerGram != null -> {
                        PriceCell("خرید شما / ask", quote.askTomanPerGram, Modifier.weight(1f), Coral400)
                        PriceCell("فروش شما / bid", quote.bidTomanPerGram, Modifier.weight(1f), Mint400)
                    }
                    quote.referenceTomanPerGram != null -> {
                        PriceCell("نرخ مرجع — غیرقابل اجرا", quote.referenceTomanPerGram, Modifier.weight(1f), Gold400)
                    }
                }
            }
            Text(quote.feeLabel, style = MaterialTheme.typography.bodyMedium, color = Ink200)
            quote.sourceTimestamp?.let {
                Text("زمان منبع: $it", style = MaterialTheme.typography.labelMedium, color = Ink400, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun PriceCell(label: String, value: Double, modifier: Modifier, color: Color) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(15.dp)).background(Pine850).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Ink400)
        Text(formatToman(value), style = MaterialTheme.typography.titleMedium, color = color, maxLines = 1)
        Text("تومان / گرم", style = MaterialTheme.typography.labelMedium, color = Ink400)
    }
}

@Composable
private fun OpportunityScreen(state: GoldArbUiState, padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { ScreenHeader("فرصت‌ها", "NET AFTER ALL COSTS") }
        item {
            NoticeCard(
                Icons.Rounded.Security,
                "گیت بدبینانه ${formatToman(state.policy.minimumNetProfitToman)}",
                "VAT فقط روی کارمزد، لغزش دو سمت، هزینه بازتوازن و تسویه از سود کم می‌شوند.",
                Gold400,
            )
        }
        if (state.opportunities.isEmpty()) {
            item {
                EmptyOpportunityCard()
            }
        } else {
            items(state.opportunities.take(8)) { OpportunityCard(it) }
        }
        item { SectionTitle("استراتژی تکرارپذیر", "inventory-neutral / بدون انتقال لحظه‌ای طلا") }
        item {
            StrategyStep("۱", "سرمایه دوطرفه", "در هر سکوی منتخب هم ریال و هم طلا نگهداری می‌شود.")
            StrategyStep("۲", "اجرای هم‌زمان", "خرید در ask پایین و فروش موجودی در bid بالا؛ انتقال طلا داخل سیکل صفر است.")
            StrategyStep("۳", "انتظار برای برگشت جهت", "سیکلی ارزشمند است که جهت اختلاف بین همان دو سکو در طول روز برگردد.")
            StrategyStep("۴", "بازتوازن محدود", "فقط پس از چند سیکل یا عبور موجودی از حد امن، ریال/طلا جابه‌جا می‌شود.")
        }
    }
}

@Composable
private fun EmptyOpportunityCard() {
    Card(colors = CardDefaults.cardColors(containerColor = Pine900), shape = RoundedCornerShape(24.dp), border = CardDefaults.outlinedCardBorder()) {
        Column(modifier = Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Rounded.Autorenew, contentDescription = null, tint = Gold400, modifier = Modifier.size(34.dp))
            Text("هنوز جفت دوطرفهٔ معتبر نداریم", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
            Text("این نتیجه بهتر از سود ظاهری غلط است: quoteهای قرنطینه و تک‌نرخ وارد موتور نشده‌اند. مانیتور باید چند روز داده جمع کند تا برگشت جهت و مدت فرصت اندازه‌گیری شود.", style = MaterialTheme.typography.bodyMedium, color = Ink200, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun OpportunityCard(opportunity: Opportunity) {
    Card(colors = CardDefaults.cardColors(containerColor = Pine900), shape = RoundedCornerShape(20.dp), border = CardDefaults.outlinedCardBorder()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${opportunity.buyVenue.venueName} ← ${opportunity.sellVenue.venueName}", style = MaterialTheme.typography.titleMedium)
                Text(formatToman(opportunity.netProfitToman), color = if (opportunity.crossesSafetyThreshold) Mint400 else Coral400, fontWeight = FontWeight.Bold)
            }
            Text("اسپرد خام ${formatToman(opportunity.grossSpreadToman)} · لغزش ${formatToman(opportunity.slippageReserveToman)} · بازتوازن ${formatToman(opportunity.rebalanceReserveToman)} · تسویه ${formatToman(opportunity.settlementReserveToman)}", style = MaterialTheme.typography.bodyMedium, color = Ink200)
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
        item { ScreenHeader("پوشش داده", "۵۲ سکوی شناسایی‌شده") }
        item { CoverageBar() }
        item { CoverageBucket("۱۵", "feed JSON قابل دریافت", "گلدیس نیز کشف شد؛ هر quote هنوز گیت کیفیت مستقل دارد", Mint400, Icons.Rounded.CheckCircle) }
        item { CoverageBucket("۱", "ناسازگاری runtime", "ملی‌گلد در probe سیستم پاسخ می‌دهد اما redirect کلاینت Node باید رفع شود", Gold400, Icons.Rounded.WarningAmber) }
        item { CoverageBucket("۶", "HTML / WebSocket / snapshot", "پاسخ داریم، اما هنوز collector زندهٔ استاندارد نیست", Color(0xFF8EB8E7), Icons.Rounded.Analytics) }
        item { CoverageBucket("۳۰", "بدون feed معتبر", "ابتدا discovery عمومی و تست اپ؛ سپس API رسمی/partner برای موارد بسته", Coral400, Icons.Rounded.CloudOff) }
        item { SectionTitle("برنامه ۳۰ سکوی باقیمانده", "از ارزان‌ترین مسیر اثبات شروع می‌کنیم") }
        item { ResearchLane("A", "کشف عمومی", "بررسی bundle وب، XHR، GraphQL، Socket.IO و endpointهای preview؛ بدون دورزدن احراز هویت.", "اولویت بالا") }
        item { ResearchLane("B", "اپ موبایل", "تحلیل ترافیک مجاز روی دستگاه خودمان، deep-linkها و پاسخ‌های pre-order برای bid/ask واقعی.", "پس از A") }
        item { ResearchLane("C", "تأمین‌کننده مشترک", "تشخیص white-labelها؛ یک feed معتبر ممکن است چند برند را پوشش دهد، ولی venue مستقل فرض نمی‌شود.", "صرفه‌جویی بالا") }
        item { ResearchLane("D", "Partner API", "برای سکوهای بسته: درخواست read-only API، sandbox، rate limit، timestamp و مجوز بازنشر.", "مسیر قراردادی") }
    }
}

@Composable
private fun CoverageBar() {
    Card(colors = CardDefaults.cardColors(containerColor = Pine900), shape = RoundedCornerShape(24.dp), border = CardDefaults.outlinedCardBorder()) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("پوشش فعلی", style = MaterialTheme.typography.titleMedium)
                Text("۲۷٪ مستقیم", color = Mint400, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape)) {
                Box(Modifier.weight(15f).fillMaxSize().background(Mint400))
                Box(Modifier.weight(1f).fillMaxSize().background(Gold400))
                Box(Modifier.weight(6f).fillMaxSize().background(Color(0xFF8EB8E7)))
                Box(Modifier.weight(30f).fillMaxSize().background(Coral400.copy(alpha = 0.55f)))
            }
            Text("feed داشتن با قابل معامله بودن یکی نیست؛ فقط quote هم‌زمان، دوطرفه و هزینه‌کامل وارد سیگنال می‌شود.", style = MaterialTheme.typography.bodyMedium, color = Ink200)
        }
    }
}

@Composable
private fun SettingsScreen(state: GoldArbUiState, padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { ScreenHeader("کنترل ریسک", "READ-ONLY MONITOR") }
        item { SettingRow(Icons.Rounded.Security, "حداقل سود خالص", formatToman(state.policy.minimumNetProfitToman), Gold400) }
        item { SettingRow(Icons.Rounded.Analytics, "لغزش محافظه‌کارانه", "۰٫۱٪ در هر سمت", Color(0xFF8EB8E7)) }
        item { SettingRow(Icons.Rounded.Autorenew, "ذخیره بازتوازن", "۰٫۰۳٪ ارزش میانی", Mint400) }
        item { SettingRow(Icons.Rounded.NotificationsActive, "هشدار تلگرام", "در backend؛ کلید داخل اپ ذخیره نمی‌شود", Gold400) }
        item { SettingRow(Icons.Rounded.Security, "اجرای خودکار", "خاموش تا تأیید preview و مجوز API", Coral400) }
        item {
            NoticeCard(
                Icons.Rounded.Security,
                "اصل امنیتی",
                "اپ فقط endpointهای عمومی read-only را می‌خواند. توکن معامله، موجودی و کلید تلگرام باید در backend امن بماند؛ هیچ secretی داخل APK قرار نمی‌گیرد.",
                Mint400,
            )
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, note: String, modifier: Modifier, valueColor: Color = Gold400) {
    Column(modifier = modifier.clip(RoundedCornerShape(17.dp)).background(Pine900).border(1.dp, Outline, RoundedCornerShape(17.dp)).padding(12.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Ink400, maxLines = 1)
        Text(value, style = MaterialTheme.typography.titleLarge, color = valueColor)
        Text(note, style = MaterialTheme.typography.labelMedium, color = Ink400, maxLines = 1)
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(subtitle, style = MaterialTheme.typography.labelMedium, color = Ink400)
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
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Pine900).border(1.dp, accent.copy(alpha = 0.26f), RoundedCornerShape(18.dp)).padding(15.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(accent.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(body, style = MaterialTheme.typography.bodyMedium, color = Ink200)
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
            Text(body, style = MaterialTheme.typography.bodyMedium, color = Ink200)
        }
    }
}

@Composable
private fun CoverageBucket(value: String, title: String, body: String, accent: Color, icon: ImageVector) {
    Card(colors = CardDefaults.cardColors(containerColor = Pine900), shape = RoundedCornerShape(19.dp), border = CardDefaults.outlinedCardBorder()) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clip(RoundedCornerShape(15.dp)).background(accent.copy(alpha = 0.13f)), contentAlignment = Alignment.Center) {
                Text(value, color = accent, style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(body, style = MaterialTheme.typography.bodyMedium, color = Ink200)
            }
            Icon(icon, contentDescription = null, tint = accent.copy(alpha = 0.8f), modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
private fun ResearchLane(code: String, title: String, body: String, tag: String) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Pine900).border(1.dp, Outline, RoundedCornerShape(18.dp)).padding(15.dp), verticalAlignment = Alignment.Top) {
        Text(code, color = Gold400, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(tag, style = MaterialTheme.typography.labelMedium, color = Gold400)
            }
            Text(body, style = MaterialTheme.typography.bodyMedium, color = Ink200)
        }
    }
}

@Composable
private fun SettingRow(icon: ImageVector, title: String, value: String, accent: Color) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Pine900).border(1.dp, Outline, RoundedCornerShape(18.dp)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = accent)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(value, style = MaterialTheme.typography.bodyMedium, color = Ink200)
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
