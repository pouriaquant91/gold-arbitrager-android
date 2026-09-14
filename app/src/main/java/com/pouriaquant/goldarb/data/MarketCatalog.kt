package com.pouriaquant.goldarb.data

data class MarketCatalogEntry(val id: String, val name: String, val monogram: String, val accent: Long)

object MarketCatalog {
    val entries = listOf(
        "hamrahgold" to "همراه‌گلد", "zaryaal" to "زریال گلد", "milli" to "میلی",
        "technogold" to "تکنوگلد", "goldika" to "گلدیکا", "talasea" to "طلاسی",
        "taline" to "طلاین", "ecogold" to "اکوگلد", "igolden" to "آی‌گلدن",
        "zarafza" to "زرافزا", "baazar" to "بازار", "talanex" to "طلانکس",
        "talaavan" to "طلاوان", "goldis" to "گلدیس", "goldenfa" to "گلدنفا",
        "geramino" to "گرامینو", "motiha" to "موتی‌ها", "zarpaad" to "زرپاد",
        "tokeniko" to "توکنیکو", "zarniv" to "زرنیو", "invi" to "اینوی",
        "melligold" to "ملی‌گلد", "daric" to "داریک", "talapp" to "طلاپ",
        "wallgold" to "وال‌گلد", "azkisarmayeh" to "ازکی سرمایه",
        "digikala-wealth" to "طلای دیجیتال دیجی‌کالا", "bazaretala" to "بازارطلا",
        "myshemsh" to "مای‌شمش", "zarpay" to "زرپی", "zarminex" to "زرمینکس",
        "goldbaan" to "گلدبان", "nikcoin" to "نیک‌کوین", "abantether" to "آبان‌تتر GoldBox",
        "zcoinn" to "زکوین", "digizargar" to "دیجی‌زرگر", "zariran" to "زرایران",
        "talaland" to "طلالند", "atigold" to "آتی‌گلد", "zarbun" to "زربُن",
        "zaargari" to "زرگری", "talaeesho" to "طلایی‌شو", "geram" to "گرم",
        "zarbama" to "زرباما", "talajet" to "طلاجت", "tetalla" to "تتلا",
        "torob-gold" to "ترب‌گلد", "farazgold" to "فرازگلد", "hivagold" to "هیواگلد",
        "zarinayar" to "زرین‌عیار", "sellonia" to "سلونیا", "dornika" to "درنیکا",
        "tgju-gold" to "TGJU Gold",
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
