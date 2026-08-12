// ANDROID - INFORMACIÓN Y MANDO CENTRAL HOTELWII
package com.hotelwii.core

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
    const val version = "v2.0.0"
    const val dtema = "paz"
}

/** ACTUALIZAR AL TAG POR SEGURIDAD [TAG NUEVO] (1)
git tag v2 -m "Version v2" ; git push origin v2 

ACTUALIZACIÓN AL MAIN PRINCIPAL DEL PROYECTO [MAIN] (2)
git add . ; git commit -m "Actualizacion Principal v2.0.0" ; git push origin main

// REEMPLAZAR TAG DE SEGURIDAD EXISTENTE [TAG REMPLAZO] (3)
git tag -d v2 ; git tag v2 -m "Version v2 actualizada" ; git push origin v2 --force

// Actualizar versiones de seguridad [COMPILAR, INSTALAR Y EJECUTAR] (4)
.\gradlew.bat assembleDebug ; adb install -r app/build/outputs/apk/debug/app-debug.apk ; adb shell am start -n com.hotelwii.app/.MainActivity;
*/
