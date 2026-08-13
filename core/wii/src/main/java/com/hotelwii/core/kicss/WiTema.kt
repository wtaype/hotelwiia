package com.hotelwii.core.kicss

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * WiTemaColors — Transcripción 1:1 de las variables CSS por cada tema de witema.css
 */
@Immutable
data class WiTemaColors(
    val name: String,
    val bg: Color,
    val wb: Color,
    val tx: Color,
    val tx1: Color,
    val tx2: Color,
    val tx3: Color,
    val tx4: Color = Color(0xFFFFFFFF),
    val tx5: Color = Color(0xFF0F172A), // Texto de alto contraste para botones/acciones de contenedor claro
    val txa: Color,
    val txe: Color,
    val hv: Color,
    val hva: Color,
    val mco: Color,
    val mbg: Color,
    val brd: Color,
    val inp: Color,
    val bg1: Color,
    val bg2: Color,
    val bg3: Color,
    val bg4: Color,
    val bg5: Color,
    val bg6: Color,
    val bg7: Color,
    val bg8: Color,
    val bt: Color,
    val glassBg: Color,
    val glassBrd: Color,
    val glassShd: Color,
    val glassBlur: String = "20px",
    val isDark: Boolean = false
)

/**
 * WiTemaGlobal — Variables globales del :root de witema.css
 */
object WiTemaGlobal {
    // Semáforo global (:root)
    val success  = Color(0xFF3CD741)
    val error    = Color(0xFFFF3849)
    val warning  = Color(0xFFA726)
    val info     = Color(0xFF00A8E6)
    val white    = Color(0xFFFFFFFF)
    val offline  = Color(0xFFDDDDDD)

    // Colores por nombre oficial de marca (:root)
    val cielo    = Color(0xFF0EBEFF)
    val dulce    = Color(0xFFFF5C69)
    val paz      = Color(0xFF29C72E)
    val futuro   = Color(0xFF21273B)
    val luz      = Color(0xFF18C5C5)
    val oro      = Color(0xFFFFDA34)
    val mora     = Color(0xFF7000FF)
    val formal   = Color(0xFF1D4ED8)
}

/**
 * HotelWiTemas — Los 5 Temas Oficiales completos convertidos 1:1 de witema.css:
 * 1. Luz
 * 2. Cielo
 * 3. Dulce
 * 4. Paz
 * 5. Futuro
 */
val HotelWiTemas = listOf(
    // 🩵 1. LUZ (witema.css: :root[data-theme="luz"])
    WiTemaColors(
        name      = "Luz",
        bg        = Color(0xFFEEF2F6),
        wb        = Color(0xFFFFFFFF),
        tx        = Color(0xFF000000),
        tx1       = Color(0xFF1E293B),
        tx2       = Color(0xFF0D0D0D),
        tx3       = Color(0xFF64748B),
        tx4       = Color(0xFF000000),
        tx5       = Color(0xFF0F172A), // Slate oscuro nítido
        txa       = Color(0xFFFFFFFF),
        txe       = Color(0xFF00897B),
        hv        = Color(0xFF14B8A6),
        hva       = Color(0xFF0D9488),
        mco       = Color(0xFF18C5C5),
        mbg       = Color(0xFF18C5C5),
        brd       = Color(0xFFE2E8F0),
        inp       = Color(0xFFF8FAFC),
        bg1       = Color(0x1A18C5C5),
        bg2       = Color(0xFF18C5C5),
        bg3       = Color(0xFFFFFFFF),
        bg4       = Color(0x2418C5C5),
        bg5       = Color(0x5918C5C5),
        bg6       = Color(0xCCEEF2F6),
        bg7       = Color(0xFF18C5C5),
        bg8       = Color(0xFFFFFFFF),
        bt        = Color(0xFF18C5C5),
        glassBg   = Color(0xE0FFFFFF),
        glassBrd  = Color(0xFFD2E5E5),
        glassShd  = Color(0x40A6B4C8),
        glassBlur = "25px",
        isDark    = false
    ),

    // 🌊 2. CIELO (witema.css: :root[data-theme="cielo"])
    WiTemaColors(
        name      = "Cielo",
        bg        = Color(0xFFCCEFFF),
        wb        = Color(0xFFE5F7FF),
        tx        = Color(0xFF000000),
        tx1       = Color(0xFF1A1A1A),
        tx2       = Color(0xFF333333),
        tx3       = Color(0xFF666666),
        tx4       = Color(0xFF000000),
        tx5       = Color(0xFF0C4A6E), // Azul marino profundo nítido
        txa       = Color(0xFFFFFFFF),
        txe       = Color(0xFF000000),
        hv        = Color(0xFF00A8E6),
        hva       = Color(0xFF1873CD),
        mco       = Color(0xFF1978D7),
        mbg       = Color(0xFF1978D7),
        brd       = Color(0xFFB8D9EB),
        inp       = Color(0xFFF0F9FF),
        bg1       = Color(0x26FFFFFF),
        bg2       = Color(0xFF1978D7),
        bg3       = Color(0xFFE5F7FF),
        bg4       = Color(0x191978D7),
        bg5       = Color(0x331978D7),
        bg6       = Color(0x80CCEFFF),
        bg7       = Color(0xFFFFFFFF),
        bg8       = Color(0xFFFFFFFF),
        bt        = Color(0xFF1978D7),
        glassBg   = Color(0x85E5F7FF),
        glassBrd  = Color(0xB8FFFFFF),
        glassShd  = Color(0x141978D7),
        glassBlur = "20px",
        isDark    = false
    ),

    // 🍓 3. DULCE (witema.css: :root[data-theme="dulce"])
    WiTemaColors(
        name      = "Dulce",
        bg        = Color(0xFFFFCCD1),
        wb        = Color(0xFFFFEBED),
        tx        = Color(0xFF000000),
        tx1       = Color(0xFF1A0000),
        tx2       = Color(0xFF330000),
        tx3       = Color(0xFF660000),
        tx4       = Color(0xFF000000),
        tx5       = Color(0xFF881337), // Rojo borgoña profundo nítido
        txa       = Color(0xFFFFFFFF),
        txe       = Color(0xFF000000),
        hv        = Color(0xFFFF7A85),
        hva       = Color(0xFFFF3849),
        mco       = Color(0xFFFF3849),
        mbg       = Color(0xFFFF3849),
        brd       = Color(0xFFFFB3BA),
        inp       = Color(0xFFFFF5F6),
        bg1       = Color(0x61FFFFFF),
        bg2       = Color(0xFFFF3849),
        bg3       = Color(0xFFFFEBED),
        bg4       = Color(0x19FF3849),
        bg5       = Color(0x33FF3849),
        bg6       = Color(0x80FFCCD1),
        bg7       = Color(0xFFFFFFFF),
        bg8       = Color(0xFFFFFFFF),
        bt        = Color(0xFFFF3849),
        glassBg   = Color(0x85FFEBED),
        glassBrd  = Color(0xB8FFFFFF),
        glassShd  = Color(0x14FF3849),
        glassBlur = "20px",
        isDark    = false
    ),

    // 🌿 4. PAZ (witema.css: :root[data-theme="paz"])
    WiTemaColors(
        name      = "Paz",
        bg        = Color(0xFFCCFFCE),
        wb        = Color(0xFFEBFFEB),
        tx        = Color(0xFF000000),
        tx1       = Color(0xFF001A00),
        tx2       = Color(0xFF003300),
        tx3       = Color(0xFF006600),
        tx4       = Color(0xFF000000),
        tx5       = Color(0xFF14532D), // Verde bosque profundo nítido
        txa       = Color(0xFFFFFFFF),
        txe       = Color(0xFF000000),
        hv        = Color(0xFF3CD741),
        hva       = Color(0xFF25B62A),
        mco       = Color(0xFF25B62A),
        mbg       = Color(0xFF25B62A),
        brd       = Color(0xFFA8E6AB),
        inp       = Color(0xFFF0FFF1),
        bg1       = Color(0x42FFFFFF),
        bg2       = Color(0xFF25B62A),
        bg3       = Color(0xFFEBFFEB),
        bg4       = Color(0x1925B62A),
        bg5       = Color(0x3325B62A),
        bg6       = Color(0x80CCFFCE),
        bg7       = Color(0xFFFFFFFF),
        bg8       = Color(0xFFFFFFFF),
        bt        = Color(0xFF25B62A),
        glassBg   = Color(0x85EBFFEB),
        glassBrd  = Color(0xB8FFFFFF),
        glassShd  = Color(0x1425B62A),
        glassBlur = "20px",
        isDark    = false
    ),

    // 🌌 5. FUTURO (witema.css: :root[data-theme="futuro"])
    WiTemaColors(
        name      = "Futuro",
        bg        = Color(0xFF0A0E1A),
        wb        = Color(0xFF151B2E),
        tx        = Color(0xFFFFFFFF),
        tx1       = Color(0xFFF1F5F9),
        tx2       = Color(0xFFCBD5E1),
        tx3       = Color(0xFF94A3B8),
        tx4       = Color(0xFFFFFFFF),
        tx5       = Color(0xFF00F3FF), // Cyan Neón brillante nítido
        txa       = Color(0xFF0A0E1A),
        txe       = Color(0xFF00F3FF),
        hv        = Color(0xFF00D4FF),
        hva       = Color(0xFF00F3FF),
        mco       = Color(0xFF00F3FF),
        mbg       = Color(0xFF151B2E),
        brd       = Color(0xFF2D3A52),
        inp       = Color(0xFF121829),
        bg1       = Color(0x1400F3FF),
        bg2       = Color(0xFF00F3FF),
        bg3       = Color(0xFF1A2235),
        bg4       = Color(0x1F00F3FF),
        bg5       = Color(0x4000F3FF),
        bg6       = Color(0xB31A2235),
        bg7       = Color(0xFF00F3FF),
        bg8       = Color(0xFF1A2235),
        bt        = Color(0xFF00F3FF),
        glassBg   = Color(0x99151B2E),
        glassBrd  = Color(0x2E00F3FF),
        glassShd  = Color(0x59000000),
        glassBlur = "24px",
        isDark    = true
    )
)

fun temaPorDefecto(): WiTemaColors = HotelWiTemas[0]

fun obtenerTemaColors(nombre: String): WiTemaColors {
    return HotelWiTemas.find { it.name.equals(nombre, ignoreCase = true) } ?: temaPorDefecto()
}
