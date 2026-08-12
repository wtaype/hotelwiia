package com.hotelwii.core.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object HotelWiiSupabase {
    private var clientInstance: SupabaseClient? = null

    private const val DEFAULT_URL = "https://jinkvztwldsuyppgdzmd.supabase.co"
    private const val DEFAULT_ANON_KEY = "sb_publishable_P400uHDaaZ67e_ugVs6xWw_Xc5xfA7Z"

    fun initialize(url: String = DEFAULT_URL, anonKey: String = DEFAULT_ANON_KEY): SupabaseClient {
        if (clientInstance == null) {
            clientInstance = createSupabaseClient(
                supabaseUrl = url,
                supabaseKey = anonKey
            ) {
                install(Auth)
                install(Postgrest)
                install(Realtime)
            }
        }
        return clientInstance!!
    }

    val client: SupabaseClient
        get() = clientInstance ?: initialize()

    val instancia: SupabaseClient
        get() = client
}

