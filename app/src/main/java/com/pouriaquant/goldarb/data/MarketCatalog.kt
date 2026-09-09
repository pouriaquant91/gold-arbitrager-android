package com.pouriaquant.goldarb.data

data class MarketCatalogEntry(val id: String, val name: String, val monogram: String, val accent: Long)

object MarketCatalog {
    val entries = listOf(
        "hamrahgold" to "همراه‌گلد", "talanex" to "طلانکس", "goldika" to "گلدیکا",
        "talasea" to "طلاسی", "milli" to "میلی", "zaryaal" to "زریال گلد",
        "taline" to "طلاین", "technogold" to "تکنوگلد", "ecogold" to "اکوگلد",
        "baazar" to "بازار", "goldis" to "گلدیس", "zarafza" to "زرافزا",
        "talapp" to "طلاپ", "zarminex" to "زرمینکس", "geramino" to "گرامینو",
        "melligold" to "ملی‌گلد", "zarpay" to "زرپی", "talaavan" to "طلاوان",
        "abantether" to "آبان‌تتر GoldBox", "invi" to "اینوی", "wallgold" to "وال‌گلد",
        "talaeesho" to "طلایی‌شو", "zarniv" to "زرنیو", "zaargari" to "زرگری",
        "motiha" to "موتی‌ها", "tetalla" to "تتلا", "goldenfa" to "گلدنفا",
        "myshemsh" to "مای‌شمش", "sellonia" to "سلونیا", "dornika" to "درنیکا",
        "tgju-gold" to "TGJU Gold", "daric" to "داریک", "bazaretala" to "بازارطلا",
        "digikala-wealth" to "طلای دیجیتال دیجی‌کالا", "talaland" to "طلالند", "torob-gold" to "ترب‌گلد",
    ).mapIndexed { index, (id, name) ->
        val accents = longArrayOf(0xFF8EB8E7, 0xFF76C9A2, 0xFFF4C862, 0xFFC59670, 0xFFD6B46D)
        MarketCatalogEntry(id, name, name.take(1), accents[index % accents.size])
    }

    fun unavailable(entry: MarketCatalogEntry) = MarketQuote(
        venueId = entry.id,
        venueName = entry.name,
        monogram = entry.monogram,
        quality = QuoteQuality.UNAVAILABLE,
        qualityLabel = "قیمت زنده در دسترس نیست",
        feeLabel = "این منبع در فهرست باقی می‌ماند تا وضعیت آن شفاف باشد",
        sourceLabel = "در صف اتصال",
        accent = entry.accent,
    )
}
