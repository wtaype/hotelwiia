package com.hotelwii.core.kidev

import com.hotelwii.core.kicss.*


fun wiRateLimit(store: WiStore, key: String, max: Int = 5, windowMs: Long = 60_000L): Boolean {
    val now = System.currentTimeMillis()
    val startKey = "rate_${key}_start"
    val countKey = "rate_${key}_count"
    val start = store.getLong(startKey, 0L)
    val count = store.getLong(countKey, 0L)
    val reset = start <= 0L || now - start > windowMs
    val nextCount = if (reset) 1L else count + 1L
    store.saveLong(startKey, if (reset) now else start)
    store.saveLong(countKey, nextCount)
    return nextCount <= max
}

