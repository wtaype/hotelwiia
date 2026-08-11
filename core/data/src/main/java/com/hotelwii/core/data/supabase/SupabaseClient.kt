package com.hotelwii.core.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object HotelWiiSupabase {
    // Configuración global del cliente Supabase
    private var clientInstance: SupabaseClient? = null

    fun initialize(url: String, anonKey: String): SupabaseClient {
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
        get() = clientInstance ?: throw IllegalStateException("Supabase no ha sido inicializado. Ingrese credenciales en local.properties.")
}
