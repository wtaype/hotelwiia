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

    /** Ícono de Edificio / Empresa (Building) */
    val Building: ImageVector by lazy {
        buildWiIcon(
            name     = "Building",
            pathData = "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-2 10h-2v-2h2v2zm0-4h-2V7h2v2zm-4 4h-2v-2h2v2zm0-4h-2V7h2v2zm-4 4H7v-2h2v2zm0-4H7V7h2v2zm6 8H7v-2h8v2z"
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
}
