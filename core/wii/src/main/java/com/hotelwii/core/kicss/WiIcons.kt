package com.hotelwii.core.kicss

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

private fun buildWiIcon(name: String, pathData: String): ImageVector =
    ImageVector.Builder(
        name           = name,
        defaultWidth   = 24.dp,
        defaultHeight  = 24.dp,
        viewportWidth  = 24f,
        viewportHeight = 24f,
    ).addPath(
        pathData = PathParser().parsePathString(pathData).toNodes(),
        fill     = SolidColor(Color.Black),
    ).build()

object WiIcons {
    val Star get() = Icons.Rounded.Star
    val Lock get() = Icons.Rounded.Lock
    val Menu get() = Icons.Rounded.Menu
    val Person get() = Icons.Rounded.Person
    val Restaurant get() = Icons.Rounded.Place
    val PointOfSale get() = Icons.Rounded.ShoppingCart
    val BarChart get() = Icons.Rounded.DateRange
    val Inventory get() = Icons.Rounded.Place
    val Refresh get() = Icons.Rounded.Refresh

    /** Ícono de Check / Aprobado */
    val Check: ImageVector by lazy {
        buildWiIcon(
            name = "Check",
            pathData = "M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"
        )
    }

    /** Ícono de Nube / Subida (CloudUpload) */
    val CloudUpload: ImageVector by lazy {
        buildWiIcon(
            name = "CloudUpload",
            pathData = "M19.35 10.04C18.67 6.59 15.64 4 12 4 9.11 4 6.6 5.64 5.35 8.04 2.34 8.36 0 10.91 0 14c0 3.31 2.69 6 6 6h13c2.76 0 5-2.24 5-5 0-2.64-2.05-4.78-4.65-4.96zM14 13v4h-4v-4H7l5-5 5 5h-3z"
        )
    }

    /** Ícono de Reloj / Timer */
    val Timer: ImageVector by lazy {
        buildWiIcon(
            name = "Timer",
            pathData = "M12 2C6.5 2 2 6.5 2 12s4.5 10 10 10 10-4.5 10-10S17.5 2 12 2zm4.2 14.2L11 13V7h1.5v5.2l4.5 2.7-.8 1.3z"
        )
    }

    /** Ícono de Edificio / Empresa (Building) */
    val Building: ImageVector by lazy {
        buildWiIcon(
            name     = "Building",
            pathData = "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-2 10h-2v-2h2v2zm0-4h-2V7h2v2zm-4 4h-2v-2h2v2zm0-4h-2V7h2v2zm-4 4H7v-2h2v2zm0-4H7V7h2v2zm6 8H7v-2h8v2z"
        )
    }

    val CleaningServices: ImageVector by lazy {
        buildWiIcon(
            name = "CleaningServices",
            pathData = "M15 11V5c0-1.1-.9-2-2-2h-2c-1.1 0-2 .9-2 2v6H4c-1.1 0-2 .9-2 2v6c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2v-6c0-1.1-.9-2-2-2h-5zm-4-6h2v6h-2V5zm9 14H4v-4h16v4z"
        )
    }

    val Build: ImageVector by lazy {
        buildWiIcon(
            name = "Build",
            pathData = "M22.7 19l-9.1-9.1c.9-2.3.4-5-1.5-6.9-2-2-5-2.4-7.4-1.3L9 6 6 9 1.6 4.7C.4 7.1.9 10.1 2.9 12.1c1.9 1.9 4.6 2.4 6.9 1.5l9.1 9.1c.4.4 1 .4 1.4 0l2.3-2.3c.5-.4.5-1.1.1-1.4z"
        )
    }

    val GridView: ImageVector by lazy {
        buildWiIcon(
            name = "GridView",
            pathData = "M3 3v8h8V3H3zm6 6H5V5h4v4zm-6 4v8h8v-8H3zm6 6H5v-4h4v4zm4-16v8h8V3h-8zm6 6h-4V5h4v4zm-6 4v8h8v-8h-8zm6 6h-4v-4h4v4z"
        )
    }

    val ViewList: ImageVector by lazy {
        buildWiIcon(
            name = "ViewList",
            pathData = "M3 14h4v-4H3v4zm0 5h4v-4H3v4zM3 9h4V5H3v4zm5 5h13v-4H8v4zm0 5h13v-4H8v4zM8 5v4h13V5H8z"
        )
    }

    val Link: ImageVector by lazy {
        buildWiIcon(
            name = "Link",
            pathData = "M3.9 12c0-1.71 1.39-3.1 3.1-3.1h4V7H7c-2.76 0-5 2.24-5 5s2.24 5 5 5h4v-1.9H7c-1.71 0-3.1-1.39-3.1-3.1zM8 13h8v-2H8v2zm9-6h-4v1.9h4c1.71 0 3.1 1.39 3.1 3.1s-1.39 3.1-3.1 3.1h-4V17h4c2.76 0 5-2.24 5-5s-2.24-5-5-5z"
        )
    }

    val Wifi: ImageVector by lazy {
        buildWiIcon(
            name = "Wifi",
            pathData = "M12 3C7.95 3 4.21 4.64 1.42 7.32l1.41 1.41C5.22 6.36 8.44 5 12 5s6.78 1.36 9.17 3.73l1.41-1.41C19.79 4.64 16.05 3 12 3zm0 4c-2.97 0-5.66 1.19-7.63 3.12l1.41 1.41C7.3 9.99 9.53 9 12 9s4.7 1 6.22 2.53l1.41-1.41C17.66 8.19 14.97 7 12 7zm0 4c-1.85 0-3.52.75-4.75 1.96l1.41 1.41C9.48 13.59 10.67 13 12 13s2.52.59 3.34 1.37l1.41-1.41C15.52 11.75 13.85 11 12 11zm0 4c-.83 0-1.5.67-1.5 1.5s.67 1.5 1.5 1.5 1.5-.67 1.5-1.5-.67-1.5-1.5-1.5z"
        )
    }

    val Bathtub: ImageVector by lazy {
        buildWiIcon(
            name = "Bathtub",
            pathData = "M20 13V4.83C20 3.27 18.73 2 17.17 2c-.75 0-1.47.3-2 .83l-1.25 1.25c-.16-.05-.33-.08-.5-.08-1.1 0-2 .9-2 2v.17l-3.59 3.59c-.43.43-.68 1.02-.68 1.63V13h12.85zM7 20c0 1.1.9 2 2 2h6c1.1 0 2-.9 2-2v-1H7v1zm14-5H3c-.55 0-1 .45-1 1v2c0 .55.45 1 1 1h18c.55 0 1-.45 1-1v-2c0-.55-.45-1-1-1z"
        )
    }

    val Tv: ImageVector by lazy {
        buildWiIcon(
            name = "Tv",
            pathData = "M21 3H3c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h5v2h8v-2h5c1.1 0 1.99-.9 1.99-2L23 5c0-1.1-.9-2-2-2zm0 14H3V5h18v12z"
        )
    }

    val Coffee: ImageVector by lazy {
        buildWiIcon(
            name = "Coffee",
            pathData = "M18.5 3H6c-1.1 0-2 .9-2 2v8c0 2.21 1.79 4 4 4h6c2.21 0 4-1.79 4-4v-3h.5c1.38 0 2.5-1.12 2.5-2.5S19.88 5 18.5 5h-.5V5c0-1.1-.9-2-2-2zM18.5 8h-.5V5h.5c.28 0 .5.22.5.5s-.22.5-.5.5zM4 19h16v2H4v-2z"
        )
    }

    val PhotoCamera: ImageVector by lazy {
        buildWiIcon(
            name = "PhotoCamera",
            pathData = "M12 12c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3zm9-8h-3.17L16 2H8L6.17 4H3c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h18c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 14H3V6h4.05l1.83-2h6.24l1.83 2H21v12z"
        )
    }

    val Visibility: ImageVector by lazy {
        buildWiIcon(
            name     = "Visibility",
            pathData = "M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5" +
                       "c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5" +
                       "-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"
        )
    }

    val VisibilityOff: ImageVector by lazy {
        buildWiIcon(
            name     = "VisibilityOff",
            pathData = "M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92" +
                       "c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7" +
                       "l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46" +
                       "C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84" +
                       "l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55" +
                       "c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55" +
                       "c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2z" +
                       "M11.84 9.02l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z"
        )
    }
    val Phone: ImageVector by lazy {
        buildWiIcon(
            name = "Phone",
            pathData = "M6.62 10.79c1.44 2.83 3.76 5.14 6.59 6.59l2.2-2.2c.27-.27.67-.36 1.02-.24 1.12.37 2.33.57 3.57.57.55 0 1 .45 1 1V20c0 .55-.45 1-1 1-9.39 0-17-7.61-17-17 0-.55.45-1 1-1h3.5c.55 0 1 .45 1 1 0 1.25.2 2.45.57 3.57.11.35.03.74-.25 1.02l-2.2 2.2z"
        )
    }

    val Security: ImageVector by lazy {
        buildWiIcon(
            name = "Security",
            pathData = "M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm0 10.99h7c-.53 4.12-3.28 7.79-7 8.94V12H5V6.3l7-3.11v8.8z"
        )
    }

    val Settings: ImageVector by lazy {
        buildWiIcon(
            name = "Settings",
            pathData = "M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6 1.62 3.6 3.6-1.62 3.6-3.6 3.6z"
        )
    }

    val SystemUpdate: ImageVector by lazy {
        buildWiIcon(
            name = "SystemUpdate",
            pathData = "M17 1.01L7 1c-1.1 0-2 .9-2 2v18c0 1.1.9 2 2 2h10c1.1 0 2-.9 2-2V3c0-1.1-.9-1.99-2-1.99zM17 19H7V5h10v14zm-1-6h-3V8h-2v5H8l4 4 4-4z"
        )
    }

    val Notifications: ImageVector by lazy {
        buildWiIcon(
            name = "Notifications",
            pathData = "M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z"
        )
    }

    val Info: ImageVector by lazy {
        buildWiIcon(
            name = "Info",
            pathData = "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z"
        )
    }

    val Bolt: ImageVector by lazy {
        buildWiIcon(
            name = "Bolt",
            pathData = "M11 21h-1l1-7H7.5c-.88 0-.33-.75-.31-.78C8.48 10.94 10.42 7.54 13.01 3h1l-1 7h3.51c.4 0 .62.19.4.66C13.06 17.06 11 21 11 21z"
        )
    }

    val Print: ImageVector by lazy {
        buildWiIcon(
            name = "Print",
            pathData = "M19 8H5c-1.66 0-3 1.34-3 3v6h4v4h12v-4h4v-6c0-1.66-1.34-3-3-3zm-3 11H8v-5h8v5zm3-7c-.55 0-1-.45-1-1s.45-1 1-1 1 .45 1 1-.45 1-1 1zm-1-9H6v4h12V3z"
        )
    }

    val Receipt: ImageVector by lazy {
        buildWiIcon(
            name = "Receipt",
            pathData = "M18 17H6v-2h12v2zm0-4H6v-2h12v2zm0-4H6V7h12v2zM3 22l1.5-1.5L6 22l1.5-1.5L9 22l1.5-1.5L12 22l1.5-1.5L15 22l1.5-1.5L18 22l1.5-1.5L21 22V2l-1.5 1.5L18 2l-1.5 1.5L15 2l-1.5 1.5L12 2l-1.5 1.5L9 2 7.5 3.5 6 2 4.5 3.5 3 2v20z"
        )
    }
}
