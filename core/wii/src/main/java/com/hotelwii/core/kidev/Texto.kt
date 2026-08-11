package com.hotelwii.core.kidev

import com.hotelwii.core.kicss.*


import java.text.Normalizer

@JvmName("CapiStandalone")
fun Capi(text: String = ""): String = text.Capi()

@JvmName("CapitStandalone")
fun Capit(text: String = ""): String = text.Capit()

@JvmName("MayuStandalone")
fun Mayu(text: String = ""): String = text.Mayu()

@JvmName("mis10Standalone")
fun mis10(text: String = "", max: Int = 10): String = text.mis10(max)

@JvmName("wiSlugStandalone")
fun wiSlug(text: String = ""): String = text.wiSlug()

fun String.Capi(): String = replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
fun String.Capit(): String = trim().lowercase().split(Regex("\\s+")).joinToString(" ") { it.Capi() }
fun String.Mayu(): String = uppercase()
fun String.mis10(max: Int = 10): String = if (this.length <= max) this else this.take(max).trimEnd() + "..."
fun String.wiSlug(): String {
    val clean = Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
        .lowercase()
    return clean.replace(Regex("[^a-z0-9]+"), "-").trim('-')
}

