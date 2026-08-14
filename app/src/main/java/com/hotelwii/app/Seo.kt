package com.hotelwii.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.hotelwii.core.kicss.WiIcons

/**
 * 🏷️ MetaTab — Estructura de datos para sub-pestañas con título e ícono opcional.
 */
data class MetaTab(
    val titulo: String,
    val icono: ImageVector? = null
)

/**
 * 📜 Seo.kt — Single Source of Truth para metadatos y navegación en HotelWii.
 */
data class MetaRuta(
    val orden: Int? = null,                // Orden numérico para la barra de navegación (null = fuera de nav principal)
    val key: String,                       // Identificador de la ruta ("hola", "auth", "habitaciones", etc.)
    val nombre: String? = null,            // Nombre limpio para botones de navegación
    val titulo: String,                    // Título en la barra superior
    val subtitulo: String,                 // Subtítulo explicativo
    val icono: ImageVector,                // Ícono vectorial oficial
    val tabs: List<MetaTab> = emptyList(), // Pestañas internas
    val mostrarNavBottom: Boolean = true,  // Muestra barra de navegación inferior
    val mostrarHeader: Boolean = true,     // Muestra cabecera superior
    val mostrarTabs: Boolean = true,       // Muestra barra de pestañas
    val mostrarMenuLateral: Boolean = true, // Muestra menú lateral desplegable
    val requiereLayout: Boolean = true     // Requiere marco global de la app
) {
    val esNavPrincipal: Boolean get() = orden != null
}

object Seo {
    /**
     * 🏠 PANTALLA_INICIAL: Constante Single Source of Truth para la ruta por defecto.
     */
    const val PANTALLA_INICIAL = "habitaciones"

    /**
     * 📌 METADATOS: Single Source of Truth ordenado numéricamente mediante `orden`.
     */
    val METADATOS = mapOf(
        "hola" to MetaRuta(
            orden = 1,
            key = "hola",
            nombre = "Hola",
            titulo = "¡Hola HotelWii! 🏨✨",
            subtitulo = "Super App de Gestión Hotelera - Huacachina, Ica",
            icono = WiIcons.Building,
            tabs = listOf(
                MetaTab("General", Icons.Rounded.Home),
                MetaTab("RENIEC", Icons.Rounded.Person),
                MetaTab("SUNAT", Icons.Rounded.Info)
            ),
            mostrarNavBottom = true,
            mostrarHeader = true,
            mostrarTabs = true,
            mostrarMenuLateral = true,
            requiereLayout = true
        ),
        "auth" to MetaRuta(
            orden = null,
            key = "auth",
            nombre = "Autenticación",
            titulo = "Ingreso a HotelWii",
            subtitulo = "Acceso seguro para recepcionistas y administradores",
            icono = WiIcons.Lock,
            mostrarNavBottom = false,
            mostrarHeader = false,
            mostrarTabs = false,
            mostrarMenuLateral = false,
            requiereLayout = false
        ),
        "habitaciones" to MetaRuta(
            orden = 2,
            key = "habitaciones",
            nombre = "Recepción",
            titulo = "Gestión de Recepción",
            subtitulo = "Centro de control de cuartos, reservas y check-in/out",
            icono = WiIcons.Building,
            tabs = listOf(
                MetaTab("Reservas", Icons.Rounded.DateRange),
                MetaTab("Habitaciones", Icons.Rounded.Home),
                MetaTab("Precios", Icons.Rounded.Settings)
            ),
            mostrarNavBottom = true,
            mostrarHeader = true,
            mostrarTabs = true,
            mostrarMenuLateral = true,
            requiereLayout = true
        ),
        "cuenta" to MetaRuta(
            orden = null,
            key = "cuenta",
            nombre = "Cuenta",
            titulo = "Perfil y Configuración",
            subtitulo = "Ajustes de usuario, perfil y preferencias del hotel",
            icono = WiIcons.Person,
            tabs = listOf(
                MetaTab("General", Icons.Rounded.Home),
                MetaTab("Perfil", Icons.Rounded.Person),
                MetaTab("Seguridad", Icons.Rounded.Lock),
                MetaTab("Ajustes", Icons.Rounded.Settings)
            ),
            mostrarNavBottom = true,
            mostrarHeader = true,
            mostrarTabs = true,
            mostrarMenuLateral = true,
            requiereLayout = true
        ),
        "empresas" to MetaRuta(
            orden = 3,
            key = "empresas",
            nombre = "Hoteles",
            titulo = "Gestión de Hoteles y Empresas",
            subtitulo = "Administra tus propiedades hotelera y parámetros de facturación",
            icono = WiIcons.Building,
            tabs = listOf(
                MetaTab("Mis Hoteles", Icons.Rounded.Home),
                MetaTab("Nuevo Hotel", Icons.Rounded.Person),
                MetaTab("Ajustes", Icons.Rounded.Settings)
            ),
            mostrarNavBottom = true,
            mostrarHeader = true,
            mostrarTabs = true,
            mostrarMenuLateral = true,
            requiereLayout = true
        )
    )

    val DEFAULT: MetaRuta get() = METADATOS[PANTALLA_INICIAL] ?: METADATOS["empresas"]!!
}
