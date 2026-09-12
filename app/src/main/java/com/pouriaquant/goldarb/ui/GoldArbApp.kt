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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Fingerprint
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pouriaquant.goldarb.data.AssetPosition
import com.pouriaquant.goldarb.data.AssetSignal
import com.pouriaquant.goldarb.data.AssetTrade
import com.pouriaquant.goldarb.security.AppThemeMode
import com.pouriaquant.goldarb.security.AppVisualStyle
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

private val Gold = Color(0xFFE9B949)
private val Silver = Color(0xFFC9D2DC)
private val Copper = Color(0xFFD47A45)
private val Tether = Color(0xFF35C89F)
private val Positive = Color(0xFF48D7A2)
private val Negative = Color(0xFFFF7D77)
private val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("fa-IR"))

private enum class AssetPage(
    val key: String,
    val label: String,
    val symbol: String,
    val description: String,
    val color: Color,
    val venues: List<Pair<String, String>>,
) {
    GOLD("gold", "طلا", "Au", "طلای ۱۸ عیار", Gold, listOf("myshemsh" to "مای‌شمش", "geramino" to "گرامینو", "baazar" to "بازار", "technogold" to "تکنوگلد", "talapp" to "طلاپ", "zarminex" to "زرماینکس")),
    SILVER("silver", "نقره", "Ag", "نقره ۹۹۹", Silver, listOf("tgju-silver" to "شبکه اطلاع‌رسانی طلا و ارز", "iran-silver" to "بازار تخصصی نقره ایران", "silver-store" to "فروشگاه‌های شمش نقره")),
    COPPER("copper", "مس", "Cu", "مس کاتد", Copper, listOf("ime-copper" to "بورس کالای ایران", "lme-copper" to "بورس فلزات لندن", "ahanonline-copper" to "بازار آهن و فلزات")),
    USDT("usdt", "تتر", "₮", "USDT / تومان", Tether, listOf("wallex" to "والکس", "tabdeal" to "تبدیل", "exir" to "اکسیر", "raastin" to "راستین", "ramzinex" to "رمزینکس", "ompfinex" to "اوام‌پی‌فینکس")),
    SETTINGS("settings", "تنظیمات", "⚙", "ظاهر و امنیت دستگاه", Color(0xFF8EB8E7), emptyList()),
}

private data class DisplaySignal(
    val id: String, val asset: String, val buyVenueId: String, val sellVenueId: String,
    val quantity: Double, val unit: String, val buyPrice: Double, val sellPrice: Double,
    val netProfit: Double, val decision: String, val executionStatus: String, val sampledAt: String,
)

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
                        ArbitoMark()
                        Column { Text("آربیتو", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black); Text("Arbito Signal Lab", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp) }
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { scope.launch { drawerState.close() } }) { Icon(Icons.Rounded.Close, "بستن منو") }
                    }
                    HorizontalDivider()
                    AssetPage.entries.forEach { item ->
                        NavigationDrawerItem(
                            label = { Column { Text(item.label, fontWeight = FontWeight.Bold); Text(item.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
                            icon = { ElementBadge(item.symbol, item.color, 42) },
                            selected = page == item,
                            onClick = { page = item; scope.launch { drawerState.close() } },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp),
                        )
                    }
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
                        title = { Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) { ArbitoMark(30); Text("آربیتو", fontWeight = FontWeight.Black) } },
                        navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Rounded.Menu, "باز کردن منو") } },
                        actions = {
                            StatusPill(state.serverConnected)
                            IconButton(onClick = viewModel::refresh) { if (state.isLoading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp) else Icon(Icons.Rounded.Refresh, "به‌روزرسانی") }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background.copy(alpha = .96f)),
                    )
                },
            ) { padding ->
                if (page == AssetPage.SETTINGS) SettingsPage(padding, biometricAvailable, biometricEnabled, themeMode, visualStyle, onBiometricChanged, onThemeModeChanged, onVisualStyleChanged)
                else AssetMarketPage(page, state, padding)
            }
        }
    }
}

@Composable
private fun AssetMarketPage(page: AssetPage, state: GoldArbUiState, padding: PaddingValues) {
    var historyTab by remember(page) { mutableIntStateOf(0) }
    val signals = if (page == AssetPage.GOLD) state.calculationRuns.map {
        DisplaySignal(it.id, "gold", it.buyVenueId, it.sellVenueId, it.quantityGram, "گرم", it.buyPriceToman, it.sellPriceToman, it.netProfitToman, it.decision, it.executionStatus, it.sampledAt)
    } else state.assetSignals.filter { it.asset == page.key }.map(AssetSignal::toDisplay)
    val trades = state.assetTrades.filter { it.asset == page.key }
    val positions = state.assetPositions.filter { it.asset == page.key }
    val best = signals.firstOrNull()
    val executed = signals.firstOrNull { it.executionStatus != "calculated" }
    val venueName: (String) -> String = { id -> page.venues.firstOrNull { it.first == id }?.second ?: id }
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Brush.radialGradient(listOf(page.color.copy(alpha = .16f), MaterialTheme.colorScheme.background), radius = 900f)),
        contentPadding = PaddingValues(top = padding.calculateTopPadding() + 14.dp, bottom = 30.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { HeroCard(page, if (page == AssetPage.GOLD) state.serverUpdatedAt else state.assetHeartbeats.firstOrNull { it.asset == page.key }?.checkedAt) }
        item { SummaryRow(page, signals, if (page == AssetPage.GOLD) state.trades.map { it.id }.distinct().size else trades.map { it.signalId }.distinct().size, state.serverConnected) }
        item { SignalCard(page, best, venueName) }
        item { PaperTradeCard(page, executed, venueName) }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) { Icon(Icons.Rounded.History, null, tint = page.color); Text("گزارش سرور", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
                    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(4.dp)) {
                        listOf("سیگنال‌ها", "معاملات فرضی", "دارایی‌ها").forEachIndexed { index, label -> TabButton(label, historyTab == index, page.color, Modifier.weight(1f)) { historyTab = index } }
                    }
                }
            }
        }
        when (historyTab) {
            0 -> if (signals.isEmpty()) item { EmptyCard("هنوز سیگنالی ثبت نشده است.") } else items(signals.take(30), key = { it.id }) { HistorySignalRow(it, venueName, page.color) }
            1 -> if (trades.isEmpty() && page == AssetPage.GOLD && state.trades.isEmpty()) item { EmptyCard("معامله فرضی ثبت نشده است.") } else if (page == AssetPage.GOLD) items(state.trades.take(30), key = { it.id }) { HistoryTradeRow(it.venueId, it.side, it.quantityGram, "گرم", it.totalToman, it.occurredAt, venueName) } else items(trades.take(30), key = { it.id }) { HistoryTradeRow(it.venueId, it.side, it.quantity, it.unit, it.totalToman, it.occurredAt, venueName) }
            else -> if (page == AssetPage.GOLD) items(state.positions.values.toList().take(60), key = { it.venueId }) { PositionRow(it.venueId, it.tomanBalance, it.goldBalanceGram, "گرم", venueName) } else if (positions.isEmpty()) item { EmptyCard("دفتر دارایی این بازار هنوز ایجاد نشده است.") } else items(positions, key = { it.venueId }) { PositionRow(it.venueId, it.tomanBalance, it.assetBalance, it.unit, venueName) }
        }
        item { VenueCoverage(page, signals, positions) }
    }
}

private fun AssetSignal.toDisplay() = DisplaySignal(id, asset, buyVenueId, sellVenueId, quantity, unit, buyPriceToman, sellPriceToman, netProfitToman, decision, executionStatus, sampledAt)

@Composable private fun HeroCard(page: AssetPage, updatedAt: String?) { Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = page.color.copy(alpha = .12f)), modifier = Modifier.border(1.dp, page.color.copy(alpha=.3f), RoundedCornerShape(28.dp))) { Row(Modifier.fillMaxWidth().padding(24.dp), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) { Text("بازار ${page.label}", color = page.color, fontWeight = FontWeight.Bold); Text(page.description, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black); Text(updatedAt ?: "در انتظار اولین پایش", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp) }; ElementBadge(page.symbol, page.color, 88) } } }

@Composable private fun SummaryRow(page: AssetPage, signals: List<DisplaySignal>, tradeCount: Int, connected: Boolean) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { MiniStat("وضعیت", if (connected) "متصل" else "قطع", Icons.Rounded.Autorenew, page.color, Modifier.weight(1f)); MiniStat("سیگنال", formatter.format(signals.count { it.decision == "accepted" }), Icons.Rounded.Analytics, page.color, Modifier.weight(1f)); MiniStat("معامله", formatter.format(tradeCount), Icons.Rounded.SwapVert, page.color, Modifier.weight(1f)) } }
@Composable private fun MiniStat(label:String,value:String,icon:androidx.compose.ui.graphics.vector.ImageVector,color:Color,modifier:Modifier){ Card(modifier,shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){Column(Modifier.padding(12.dp),verticalArrangement=Arrangement.spacedBy(7.dp)){Icon(icon,null,tint=color,modifier=Modifier.size(19.dp));Text(label,fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant);Text(value,fontWeight=FontWeight.Bold)}} }

@Composable private fun SignalCard(page:AssetPage, signal:DisplaySignal?, venueName:(String)->String){ Card(shape=RoundedCornerShape(24.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("سیگنال آخر",color=page.color,fontSize=12.sp);Text(if(signal==null)"فعلاً سیگنالی نداریم" else "${venueName(signal.buyVenueId)} ← ${venueName(signal.sellVenueId)}",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)};Icon(Icons.Rounded.Analytics,null,tint=page.color)};if(signal==null) EmptyContent(page) else {Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){RouteBox("خرید از",venueName(signal.buyVenueId),signal.buyPrice,page.color,Modifier.weight(1f));Icon(Icons.Rounded.SwapVert,null,tint=page.color,modifier=Modifier.align(Alignment.CenterVertically));RouteBox("فروش در",venueName(signal.sellVenueId),signal.sellPrice,page.color,Modifier.weight(1f))};HorizontalDivider();Row(verticalAlignment=Alignment.CenterVertically){Surface(color=if(signal.decision=="accepted")Positive.copy(alpha=.14f) else Negative.copy(alpha=.14f),shape=RoundedCornerShape(999.dp)){Text(if(signal.decision=="accepted")"بالای آستانه" else "زیر آستانه",color=if(signal.decision=="accepted")Positive else Negative,modifier=Modifier.padding(horizontal=11.dp,vertical=7.dp),fontSize=12.sp)};Spacer(Modifier.weight(1f));Column(horizontalAlignment=Alignment.End){Text("سود خالص",fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant);Text(money(signal.netProfit),fontWeight=FontWeight.Black,color=if(signal.netProfit>=0)Positive else Negative)}}}}} }
@Composable private fun RouteBox(label:String,venue:String,price:Double,color:Color,modifier:Modifier){Column(modifier.clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(14.dp),verticalArrangement=Arrangement.spacedBy(5.dp)){Text(label,fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant);Text(venue,fontWeight=FontWeight.Bold);Text(money(price),fontSize=12.sp,color=color)}}

@Composable private fun PaperTradeCard(page:AssetPage, signal:DisplaySignal?, venueName:(String)->String){Card(shape=RoundedCornerShape(24.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("معامله فرضی سرور",color=page.color,fontSize=12.sp);Text(if(signal==null)"معامله‌ای انجام نشده" else "در دفتر دارایی اعمال شد",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)};Icon(Icons.Rounded.Wallet,null,tint=page.color)};if(signal==null) Text("پس از عبور سود خالص از آستانه و کافی بودن موجودی هر دو طرف، معامله فرضی ثبت می‌شود.",color=MaterialTheme.colorScheme.onSurfaceVariant,lineHeight=24.sp) else {Text("${venueName(signal.buyVenueId)} ↔ ${venueName(signal.sellVenueId)}",fontWeight=FontWeight.Bold);Text("${formatter.format(signal.quantity)} ${signal.unit} خرید و فروش فرضی شد.",color=MaterialTheme.colorScheme.onSurfaceVariant);Text(money(signal.netProfit),color=Positive,fontWeight=FontWeight.Black)}}}}

@Composable private fun TabButton(label:String,selected:Boolean,color:Color,modifier:Modifier,onClick:()->Unit){Surface(modifier=modifier.clickable(onClick=onClick),shape=RoundedCornerShape(11.dp),color=if(selected)color.copy(alpha=.18f) else Color.Transparent){Text(label,modifier=Modifier.padding(vertical=11.dp),textAlign=TextAlign.Center,color=if(selected)color else MaterialTheme.colorScheme.onSurfaceVariant,fontSize=12.sp,fontWeight=if(selected)FontWeight.Bold else FontWeight.Normal)}}
@Composable private fun HistorySignalRow(signal:DisplaySignal,venueName:(String)->String,color:Color){Card(shape=RoundedCornerShape(17.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){Row(Modifier.fillMaxWidth().padding(14.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(11.dp)){Box(Modifier.size(9.dp).clip(CircleShape).background(if(signal.decision=="accepted")Positive else Negative));Column(Modifier.weight(1f)){Text("${venueName(signal.buyVenueId)} ← ${venueName(signal.sellVenueId)}",fontWeight=FontWeight.Bold);Text(signal.sampledAt,fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)};Column(horizontalAlignment=Alignment.End){Text(money(signal.netProfit),color=if(signal.netProfit>=0)Positive else Negative,fontWeight=FontWeight.Bold);Text(if(signal.executionStatus=="buy-and-sell")"اجرا در دفتر" else "فقط محاسبه",fontSize=11.sp,color=color)}}}}
@Composable private fun HistoryTradeRow(venueId:String,side:String,quantity:Double,unit:String,total:Double,time:String,venueName:(String)->String){Card(shape=RoundedCornerShape(17.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){Row(Modifier.fillMaxWidth().padding(14.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(11.dp)){Surface(color=(if(side=="buy")Positive else Negative).copy(alpha=.14f),shape=RoundedCornerShape(9.dp)){Text(if(side=="buy")"خرید" else "فروش",color=if(side=="buy")Positive else Negative,modifier=Modifier.padding(8.dp),fontSize=11.sp)};Column(Modifier.weight(1f)){Text(venueName(venueId),fontWeight=FontWeight.Bold);Text(time,fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)};Column(horizontalAlignment=Alignment.End){Text("${formatter.format(quantity)} $unit",fontWeight=FontWeight.Bold);Text(money(total),fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)}}}}
@Composable private fun PositionRow(venueId:String,toman:Double,asset:Double,unit:String,venueName:(String)->String){Card(shape=RoundedCornerShape(17.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){Row(Modifier.fillMaxWidth().padding(14.dp),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(venueName(venueId),fontWeight=FontWeight.Bold);Text("دارایی سرور",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)};Column(horizontalAlignment=Alignment.End){Text("${formatter.format(asset)} $unit",fontWeight=FontWeight.Bold);Text(money(toman),fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)}}}}

@Composable private fun VenueCoverage(page:AssetPage,signals:List<DisplaySignal>,positions:List<AssetPosition>){Card(shape=RoundedCornerShape(24.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){Text("منابع متناسب با ${page.label}",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold);page.venues.forEach{(id,name)->val connected=positions.any{it.venueId==id}||signals.any{it.buyVenueId==id||it.sellVenueId==id};Row(Modifier.fillMaxWidth().padding(vertical=6.dp),verticalAlignment=Alignment.CenterVertically){ElementBadge(name.take(1),page.color,38);Spacer(Modifier.width(10.dp));Column(Modifier.weight(1f)){Text(name,fontWeight=FontWeight.Bold);Text(if(connected)"متصل به گزارش سرور" else "در انتظار خوراک دوطرفه",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)};Box(Modifier.size(8.dp).clip(CircleShape).background(if(connected)Positive else MaterialTheme.colorScheme.outline))}}}}
}

@Composable private fun EmptyContent(page:AssetPage){Column(Modifier.fillMaxWidth().padding(vertical=34.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(8.dp)){Icon(Icons.Rounded.CloudOff,null,tint=page.color,modifier=Modifier.size(38.dp));Text(if(page==AssetPage.SILVER||page==AssetPage.COPPER)"خوراک دوطرفه معتبر لازم است" else "فرصت قابل‌قبولی ثبت نشده",fontWeight=FontWeight.Bold);Text("تا اتصال منبع معتبر، قیمت یا سود ساختگی نمایش داده نمی‌شود.",textAlign=TextAlign.Center,color=MaterialTheme.colorScheme.onSurfaceVariant,lineHeight=22.sp)}}
@Composable private fun EmptyCard(text:String){Card(shape=RoundedCornerShape(17.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){Text(text,Modifier.fillMaxWidth().padding(25.dp),textAlign=TextAlign.Center,color=MaterialTheme.colorScheme.onSurfaceVariant)}}

@Composable private fun SettingsPage(padding:PaddingValues,biometricAvailable:Boolean,biometricEnabled:Boolean,themeMode:AppThemeMode,visualStyle:AppVisualStyle,onBiometricChanged:(Boolean)->Unit,onThemeModeChanged:(AppThemeMode)->Unit,onVisualStyleChanged:(AppVisualStyle)->Unit){LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(top=padding.calculateTopPadding()+16.dp,bottom=30.dp,start=16.dp,end=16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){item{Text("تنظیمات",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Black)};item{SettingSwitch(Icons.Rounded.Fingerprint,"قفل اثر انگشت",if(biometricAvailable)"قفل محلی برنامه" else "در این دستگاه در دسترس نیست",biometricEnabled,biometricAvailable,onBiometricChanged)};item{Card(shape=RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Row(horizontalArrangement=Arrangement.spacedBy(9.dp)){Icon(Icons.Rounded.DarkMode,null);Text("حالت نمایش",fontWeight=FontWeight.Bold)};AppThemeMode.entries.forEach{mode->Surface(modifier=Modifier.fillMaxWidth().clickable{onThemeModeChanged(mode)},shape=RoundedCornerShape(12.dp),color=if(mode==themeMode)MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant){Text(mode.name,Modifier.padding(13.dp))}}}}};item{Card(shape=RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Row(horizontalArrangement=Arrangement.spacedBy(9.dp)){Icon(Icons.Rounded.Palette,null);Text("رنگ پایه",fontWeight=FontWeight.Bold)};AppVisualStyle.entries.forEach{style->Surface(modifier=Modifier.fillMaxWidth().clickable{onVisualStyleChanged(style)},shape=RoundedCornerShape(12.dp),color=if(style==visualStyle)MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant){Text(style.name,Modifier.padding(13.dp))}}}}}}}
@Composable private fun SettingSwitch(icon:androidx.compose.ui.graphics.vector.ImageVector,title:String,subtitle:String,checked:Boolean,enabled:Boolean,onChecked:(Boolean)->Unit){Card(shape=RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){Row(Modifier.fillMaxWidth().padding(18.dp),verticalAlignment=Alignment.CenterVertically){Icon(icon,null);Spacer(Modifier.width(12.dp));Column(Modifier.weight(1f)){Text(title,fontWeight=FontWeight.Bold);Text(subtitle,fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)};Switch(checked,onCheckedChange=onChecked,enabled=enabled)}}}

@Composable private fun StatusPill(connected:Boolean){Surface(shape=RoundedCornerShape(999.dp),color=(if(connected)Positive else Negative).copy(alpha=.12f)){Row(Modifier.padding(horizontal=10.dp,vertical=6.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(5.dp)){Box(Modifier.size(7.dp).clip(CircleShape).background(if(connected)Positive else Negative));Text(if(connected)"سرور" else "قطع",fontSize=11.sp,color=if(connected)Positive else Negative)}}}
@Composable private fun ArbitoMark(size:Int=42){Box(Modifier.size(size.dp).clip(RoundedCornerShape((size/3).dp)).background(Brush.linearGradient(listOf(Gold,Silver,Copper,Tether))),contentAlignment=Alignment.Center){Text("A",color=Color(0xFF080B12),fontWeight=FontWeight.Black,fontSize=(size/2).sp)}}
@Composable private fun ElementBadge(symbol:String,color:Color,size:Int){Box(Modifier.size(size.dp).clip(RoundedCornerShape((size/3).dp)).background(color.copy(alpha=.9f)),contentAlignment=Alignment.Center){Text(symbol,color=Color(0xFF11141B),fontWeight=FontWeight.Black)}}
private fun money(value:Double)="${formatter.format(value.toLong())} تومان"

@Composable
fun LockScreen(biometricAvailable: Boolean, onUnlock: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Tether.copy(alpha=.22f), Color(0xFF080B12)),radius=900f)).padding(28.dp), contentAlignment=Alignment.Center) {
        Card(shape=RoundedCornerShape(28.dp),colors=CardDefaults.cardColors(containerColor=Color(0xFF111723))) { Column(Modifier.padding(28.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(14.dp)) { ArbitoMark(72); Text("آربیتو قفل است",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Black); Text(if(biometricAvailable)"برای مشاهده گزارش سرور قفل دستگاه را باز کنید." else "قفل امن در این دستگاه در دسترس نیست.",textAlign=TextAlign.Center,color=MaterialTheme.colorScheme.onSurfaceVariant); Surface(modifier=Modifier.fillMaxWidth().clickable(enabled=biometricAvailable,onClick=onUnlock),shape=RoundedCornerShape(16.dp),color=if(biometricAvailable)Tether else Color.Gray){Row(Modifier.padding(15.dp),horizontalArrangement=Arrangement.Center,verticalAlignment=Alignment.CenterVertically){Icon(if(biometricAvailable)Icons.Rounded.Fingerprint else Icons.Rounded.Lock,null,tint=Color(0xFF08130F));Spacer(Modifier.width(8.dp));Text("باز کردن",color=Color(0xFF08130F),fontWeight=FontWeight.Bold)}} } }
    }
}
