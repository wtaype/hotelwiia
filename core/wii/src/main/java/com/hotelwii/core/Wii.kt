// ANDROID - INFORMACIÓN Y MANDO CENTRAL HOTELWII
package com.hotelwii.core

import android.content.Context

object Wii {
    const val id = "HotelWii"
    const val app = "HotelWii"
    const val icon = "hotel"
    const val titulo = "HotelWii | Super App de Gestión Hotelera"
    const val keywii = "HotelWii, pos, hotel, recepcion, reniec, sunat, reservas"
    const val descri = "Super App de gestión hotelera y recepción ultra-veloz para hoteles con Decolecta API e impresión ESC/POS."
    const val lanzamiento = 2026
    const val by = "@wilder.taype"
    const val linkweb = "https://hotelwii.web.app"
    const val linkme = "https://wtaype.github.io/"
    const val packageName = "com.hotelwii.app"

    // 🚀 CONTROL CENTRAL DE VERSIONES HOTELWII (SEMVER ENTERPRISE)
    const val versionCode = 10001             // SemVer Ponderado: 1*10000 + 0*100 + 1
    const val versionName = "1.0.1"           // Versión semántica legible
    const val versionFile = "v$versionName"   // ⚡ Automático: Nombre de APKs y carpetas (v1.0.1)
    const val versionNotas = "Mejoras de rendimiento en Recepción y Centro Inteligente de Actualización."
    const val isMandatory = false             // Actualización obligatoria sí/no
    const val version = "v1"                  // Tag simplificado para Git / GitHub (ej. v1, v2)
    const val dtema = "paz"

    /**
     * Obtiene el versionName real instalado en el dispositivo en tiempo de ejecución
     */
    fun getInstalledVersionName(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: versionName
        } catch (e: Exception) {
            versionName
        }
    }
}

/** ACTUALIZAR AL TAG POR SEGURIDAD [TAG NUEVO] (1)
git tag v1 -m "Version v1" ; git push origin v1 

ACTUALIZACIÓN AL MAIN PRINCIPAL DEL PROYECTO [MAIN] (2)
git add . ; git commit -m "Actualizacion Principal v1.0.1" ; git push origin main

// REEMPLAZAR TAG DE SEGURIDAD EXISTENTE [TAG REMPLAZO] (3)
git tag -d v1 ; git tag v1 -m "Version v1 actualizada" ; git push origin v1 --force

// Actualizar versiones de seguridad [COMPILAR, INSTALAR Y EJECUTAR] (4)
.\gradlew.bat assembleDebug ; adb install -r app/build/outputs/apk/debug/app-debug.apk ; adb shell am start -n com.hotelwii.app/.MainActivity;
*/
