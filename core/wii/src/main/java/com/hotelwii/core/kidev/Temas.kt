package com.hotelwii.core.kidev

import android.content.Context
import android.content.res.Configuration
import com.hotelwii.core.kicss.HotelWiTemas
import com.hotelwii.core.kicss.WiTemaColors
import com.hotelwii.core.kicss.temaPorDefecto

/**
 * 🎨 WiTemas — Gestor centralizado de Temas y Persistencia de HotelWii
 * Inspirado en la arquitectura de tema.js (PancitaWii)
 */
object WiTemas {
    private const val KEY_TEMA = "wiTema"

    /**
     * Busca un tema por su nombre oficial (Luz, Cielo, Dulce, Paz, Futuro)
     */
    fun findByName(nombre: String): WiTemaColors {
        return HotelWiTemas.find { it.name.equals(nombre, ignoreCase = true) } ?: temaPorDefecto()
    }

    /**
     * Obtiene el tema inicial al arrancar la app:
     * 1. Lee la preferencia guardada en WiStore ("wiTema").
     * 2. Si no hay tema guardado, detecta el modo oscuro del sistema (Futuro para dark, Luz para light).
     */
    fun getTemaInicial(context: Context): WiTemaColors {
        val store = wiStore(context)
        val cachedTema = store.get(KEY_TEMA, "")

        if (cachedTema.isNotEmpty()) {
            return findByName(cachedTema)
        }

        // Detección automática según el modo del sistema Android
        val isSystemDark = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val defaultTema = if (isSystemDark) findByName("Futuro") else temaPorDefecto()
        
        // Guardar la inicialización por defecto
        store.save(KEY_TEMA, defaultTema.name)
        return defaultTema
    }

    /**
     * Guarda la preferencia del tema activo en WiStore y retorna el objeto WiTemaColors
     */
    fun saveTema(context: Context, temaNombre: String): WiTemaColors {
        val tema = findByName(temaNombre)
        wiStore(context).save(KEY_TEMA, tema.name)
        return tema
    }

    /**
     * Guarda directamente por objeto WiTemaColors
     */
    fun saveTema(context: Context, tema: WiTemaColors): WiTemaColors {
        wiStore(context).save(KEY_TEMA, tema.name)
        return tema
    }
}
