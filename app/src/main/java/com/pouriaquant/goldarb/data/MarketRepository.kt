package com.pouriaquant.goldarb.data

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant

interface MarketRepository {
    fun refresh(): MarketSnapshot
}

class PublicFeedMarketRepository : MarketRepository {
    override fun refresh(): MarketSnapshot {
        val quotes = mutableListOf<MarketQuote>()
        val failed = mutableListOf<String>()

        load("بازار", ::fetchBaazar)?.let(quotes::add) ?: failed.add("بازار")
        load("گلدیس", ::fetchGoldis)?.let(quotes::add) ?: failed.add("گلدیس")
        load("اکوگلد", ::fetchEcogold)?.let(quotes::add) ?: failed.add("اکوگلد")
        load("میلی", ::fetchMilli)?.let(quotes::add) ?: failed.add("میلی")
        load("زرافزا", ::fetchZarafza)?.let(quotes::add) ?: failed.add("زرافزا")
        load("مهربان‌گلد", ::fetchMehrban)?.let(quotes::add) ?: failed.add("مهربان‌گلد")
        load("گرامینو", ::fetchGeramino)?.let(quotes::add) ?: failed.add("گرامینو")
        load("داریک", ::fetchDaric)?.let(quotes::add) ?: failed.add("داریک")

        val tokenizedGoldResult = runCatching(::fetchTokenizedGoldComparison)

        return MarketSnapshot(
            quotes = quotes.sortedWith(compareBy({ it.quality.ordinal }, { it.venueName })),
            receivedAt = Instant.now().toString(),
            failedVenueNames = failed,
            tokenizedGold = tokenizedGoldResult.getOrNull(),
            tokenizedGoldError = tokenizedGoldResult.exceptionOrNull()?.let { "دریافت XAUT از والکس/تبدیل ناموفق بود" },
        )
    }

    private fun load(name: String, block: () -> MarketQuote): MarketQuote? =
        try {
            block()
        } catch (_: Exception) {
            null
        }

    private fun getJson(url: String): JSONObject {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 8_000
            readTimeout = 8_000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "ZarArb-Android/0.4")
            instanceFollowRedirects = true
        }
        return try {
            require(connection.responseCode in 200..299) { "upstream-${connection.responseCode}" }
            connection.inputStream.bufferedReader().use { JSONObject(it.readText()) }
        } finally {
            connection.disconnect()
        }
    }

    private fun fetchBaazar(): MarketQuote {
        val data = getJson("https://api.baazar.ir/landing/v1/price").getJSONObject("data")
        return MarketQuote(
            venueId = "baazar",
            venueName = "بازار",
            monogram = "ب",
            askTomanPerGram = data.getDouble("buyPrice") / 10,
            bidTomanPerGram = data.getDouble("sellPrice") / 10,
            quality = QuoteQuality.COMPARABLE,
            qualityLabel = "دوطرفه زنده",
            feeLabel = "قیمت نهایی endpoint؛ بدون افزودن fee ساختگی",
            sourceLabel = "REST رسمی",
            sourceTimestamp = data.optLong("currentTime").takeIf { it > 0 }?.let(Instant::ofEpochMilli)?.toString(),
            accent = 0xFF8EB8E7,
        )
    }

    private fun fetchEcogold(): MarketQuote {
        val rows = getJson("https://backend.ecogold.ir/api/prices/otc").getJSONArray("data")
        val row = (0 until rows.length())
            .map { rows.getJSONObject(it) }
            .single { it.getString("symbol") == "GOLD18-IRT" }
        return MarketQuote(
            venueId = "ecogold",
            venueName = "اکوگلد",
            monogram = "ا",
            askTomanPerGram = row.getDouble("buy_price"),
            bidTomanPerGram = row.getDouble("sell_price"),
            quality = QuoteQuality.QUARANTINED,
            qualityLabel = "دوطرفه؛ fee در انتظار تأیید",
            feeLabel = "کارمزد خرید ۰٫۵٪ اعلام شده؛ شمول در نرخ و fee فروش باید با preview تأیید شود",
            sourceLabel = "REST رسمی",
            sourceTimestamp = row.optString("created_at").takeIf(String::isNotBlank),
            accent = 0xFF76C9A2,
        )
    }

    private fun fetchGoldis(): MarketQuote {
        val data = getJson("https://goldis.ir/price/api/v1/price/assets/gold18k/final-prices")
            .getJSONObject("data")
        val receivedAt = Instant.now()
        val sourceTimestamp = IranDateTime.jalaliToIso(
            data.optString("last_update_date"),
            data.optString("last_update_time"),
        )
        val isFresh = IranDateTime.isFresh(sourceTimestamp, receivedAt)
        return MarketQuote(
            venueId = "goldis",
            venueName = "گلدیس",
            monogram = "گ",
            askTomanPerGram = data.getDouble("final_buy_price") / 10,
            bidTomanPerGram = data.getDouble("final_sell_price") / 10,
            quality = if (isFresh) QuoteQuality.COMPARABLE else QuoteQuality.QUARANTINED,
            qualityLabel = if (isFresh) "دوطرفهٔ نهایی و تازه" else "دوطرفه؛ timestamp نامعتبر یا کهنه",
            feeLabel = "طبق FAQ رسمی کارمزد معامله صفر است؛ اختلاف در قیمت‌های نهایی دیده می‌شود",
            sourceLabel = "REST عمومی کشف‌شده",
            sourceTimestamp = sourceTimestamp,
            accent = 0xFF7DC8A5,
        )
    }

    private fun fetchMilli(): MarketQuote {
        val data = getJson("https://milli.gold/api/v1/public/milli-price/external").getJSONObject("data")
        return MarketQuote(
            venueId = "milli",
            venueName = "میلی",
            monogram = "م",
            referenceTomanPerGram = data.getDouble("price18") * 100,
            quality = QuoteQuality.REFERENCE_ONLY,
            qualityLabel = "تک‌نرخ رسمی",
            feeLabel = "بیش از ۲۰۰mg: کارمزد ۰٫۵٪ هر سمت؛ bid/ask فقط پس از preview معتبر است",
            sourceLabel = "REST رسمی",
            sourceTimestamp = data.optString("date").takeIf(String::isNotBlank),
            accent = 0xFFF4C862,
            buyCommissionRate = 0.005,
            sellCommissionRate = 0.005,
            pricesIncludeCommission = false,
        )
    }

    private fun fetchZarafza(): MarketQuote {
        val gold = getJson("https://api.zarafza.com/v2/prices")
            .getJSONObject("data").getJSONObject("G18")
        return MarketQuote(
            venueId = "zarafza",
            venueName = "زرافزا",
            monogram = "ز",
            askTomanPerGram = gold.getJSONObject("sell").getDouble("price"),
            bidTomanPerGram = gold.getJSONObject("buy").getDouble("price"),
            quality = QuoteQuality.QUARANTINED,
            qualityLabel = "دوطرفه؛ بدون زمان منبع",
            feeLabel = "قیمت نهایی اعلامی؛ timestamp منبع منتشر نشده",
            sourceLabel = "REST رسمی",
            accent = 0xFFC59670,
        )
    }

    private fun fetchMehrban(): MarketQuote {
        val payload = getJson("https://mehrban.gold/api/config/goldprice?isSite=true")
        require(payload.optBoolean("isSuccess"))
        val data = payload.getJSONObject("data")
        return MarketQuote(
            venueId = "mehrban-gold",
            venueName = "مهربان‌گلد",
            monogram = "م",
            askTomanPerGram = data.getDouble("buy"),
            bidTomanPerGram = data.getDouble("sell"),
            quality = QuoteQuality.QUARANTINED,
            qualityLabel = "دوطرفه؛ بدون زمان منبع",
            feeLabel = "شمول fee و timestamp نیازمند تأیید مستقیم",
            sourceLabel = "REST رسمی",
            accent = 0xFFD7A173,
        )
    }

    private fun fetchGeramino(): MarketQuote {
        val data = getJson("https://api.geramino.com/gold").getJSONObject("gold_data")
        return MarketQuote(
            venueId = "geramino",
            venueName = "گرامینو",
            monogram = "گ",
            askTomanPerGram = data.getDouble("buy_price"),
            bidTomanPerGram = data.getDouble("sell_price"),
            quality = QuoteQuality.QUARANTINED,
            qualityLabel = "دوطرفه؛ بدون زمان منبع",
            feeLabel = "قیمت نهایی endpoint؛ timestamp منبع منتشر نشده",
            sourceLabel = "REST رسمی",
            accent = 0xFF91B9DC,
        )
    }

    private fun fetchDaric(): MarketQuote {
        val data = getJson("https://apie.daric.gold/public/general/topprice/GOLD18TMN")
        return MarketQuote(
            venueId = "daric",
            venueName = "داریک",
            monogram = "د",
            askTomanPerGram = data.getJSONObject("bestSell").getDouble("price"),
            bidTomanPerGram = data.getJSONObject("bestBuy").getDouble("price"),
            quality = QuoteQuality.QUARANTINED,
            qualityLabel = "دفتر سفارش؛ fee/time ناقص",
            feeLabel = "bestSell/bestBuy عمومی است؛ کارمزد و timestamp هنوز تأیید نشده",
            sourceLabel = "REST عمومی",
            accent = 0xFFE0B968,
        )
    }

    private data class BookLevel(val price: Double, val quantity: Double)
    private data class XautBook(val name: String, val asks: List<BookLevel>, val bids: List<BookLevel>)
    private data class Fill(val total: Double, val complete: Boolean)

    private fun fetchTokenizedGoldComparison(): TokenizedGoldComparison {
        val wallexRaw = getJson("https://api.wallex.ir/v1/depth?symbol=XAUTTMN").getJSONObject("result")
        val tabdealRaw = getJson("https://api1.tabdeal.org/r/api/v1/depth?symbol=XAUTIRT&limit=20")
        val wallex = XautBook(
            name = "والکس",
            asks = objectLevels(wallexRaw.getJSONArray("ask"), ascending = true),
            bids = objectLevels(wallexRaw.getJSONArray("bid"), ascending = false),
        )
        val tabdeal = XautBook(
            name = "تبدیل",
            asks = arrayLevels(tabdealRaw.getJSONArray("asks"), ascending = true),
            bids = arrayLevels(tabdealRaw.getJSONArray("bids"), ascending = false),
        )
        val quantityXaut = 0.01
        return listOf(
            evaluateXautRoute(wallex, tabdeal, quantityXaut),
            evaluateXautRoute(tabdeal, wallex, quantityXaut),
        ).maxBy { it.netProfitToman }
    }

    private fun objectLevels(array: org.json.JSONArray, ascending: Boolean): List<BookLevel> =
        (0 until array.length()).mapNotNull { index ->
            val row = array.optJSONObject(index) ?: return@mapNotNull null
            val price = row.optDouble("price")
            val quantity = row.optDouble("quantity")
            if (price > 0 && quantity > 0) BookLevel(price, quantity) else null
        }.sortedWith(if (ascending) compareBy(BookLevel::price) else compareByDescending(BookLevel::price))

    private fun arrayLevels(array: org.json.JSONArray, ascending: Boolean): List<BookLevel> =
        (0 until array.length()).mapNotNull { index ->
            val row = array.optJSONArray(index) ?: return@mapNotNull null
            val price = row.optString(0).toDoubleOrNull() ?: return@mapNotNull null
            val quantity = row.optString(1).toDoubleOrNull() ?: return@mapNotNull null
            if (price > 0 && quantity > 0) BookLevel(price, quantity) else null
        }.sortedWith(if (ascending) compareBy(BookLevel::price) else compareByDescending(BookLevel::price))

    private fun fill(levels: List<BookLevel>, requested: Double): Fill {
        var remaining = requested
        var total = 0.0
        for (level in levels) {
            val quantity = minOf(remaining, level.quantity)
            total += quantity * level.price
            remaining -= quantity
            if (remaining <= 1e-12) return Fill(total, true)
        }
        return Fill(total, false)
    }

    private fun evaluateXautRoute(buy: XautBook, sell: XautBook, quantityXaut: Double): TokenizedGoldComparison {
        val buyFill = fill(buy.asks, quantityXaut)
        val sellFill = fill(sell.bids, quantityXaut)
        val tradingFee = (buyFill.total + sellFill.total) * 0.0035
        val feeVat = tradingFee * 0.10
        val rebalanceReserve = ((buyFill.total + sellFill.total) / 2) * 0.0005
        val net = sellFill.total - buyFill.total - tradingFee - feeVat - rebalanceReserve
        val equivalent18kGram = quantityXaut * 31.1034768 / 0.75
        val netPer18kGram = net / equivalent18kGram
        return TokenizedGoldComparison(
            quantityXaut = quantityXaut,
            equivalent18kGram = equivalent18kGram,
            buyVenueName = buy.name,
            sellVenueName = sell.name,
            netProfitToman = net,
            netProfitTomanPer18kGram = netPer18kGram,
            profitable = buyFill.complete && sellFill.complete && net.isFinite() && net >= 100_000,
            receivedAt = Instant.now().toString(),
        )
    }
}
