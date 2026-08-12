package com.hotelwii.app.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hotelwii.app.Rutas
import com.hotelwii.app.RutasState
import com.hotelwii.app.components.Drawer
import com.hotelwii.app.components.Header
import com.hotelwii.app.components.Tabs
import com.hotelwii.core.kicss.WiCss
import kotlinx.coroutines.launch

/**
 * 🏰 Principal.kt — Contenedor maestro con HorizontalPager sincrónico a 60fps de respuesta instantánea.
 * Pasa pageIndex a content() para que cada página se renderice de forma nativa lado a lado sin delay.
 */
@Composable
fun Principal(
    rutasState: RutasState,
    content: @Composable (pageIndex: Int) -> Unit
) {
    val meta = Rutas.getMeta(rutasState.rutaActual)
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    if (!meta.requiereLayout) {
        // 🌌 Vista limpia a pantalla completa sin cromo de interfaz (Auth / Bienvenida)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(WiCss.bg)
                .statusBarsPadding()
        ) {
            content(0)
        }
    } else {
        // 🏰 ModalNavigationDrawer Flotante Overlay
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = meta.mostrarMenuLateral,
            drawerContent = {
                Drawer(
                    rutaActiva = rutasState.rutaActual,
                    onSeleccionarRuta = { ruta ->
                        rutasState.navegarA(ruta)
                        scope.launch { drawerState.close() }
                    }
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(WiCss.bg)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    val tabCount = meta.tabs.size
                    val pagerState = if (tabCount > 0) {
                        rememberPagerState(
                            initialPage = rutasState.tabActivaIndex.coerceIn(0, tabCount - 1),
                            pageCount = { tabCount }
                        )
                    } else null

                    // Sincronización 1: Gesto Swipe del Usuario en tiempo real -> Actualizar tabActivaIndex mediante targetPage (Cero Lag)
                    if (pagerState != null) {
                        LaunchedEffect(pagerState) {
                            snapshotFlow { pagerState.targetPage }.collect { target ->
                                if (target != rutasState.tabActivaIndex) {
                                    rutasState.seleccionarTab(target)
                                }
                            }
                        }

                        // Sincronización 2: Cambio externo de ruta / tap -> Pager Animado Sincrónico
                        LaunchedEffect(rutasState.tabActivaIndex, rutasState.rutaActual) {
                            val target = rutasState.tabActivaIndex.coerceIn(0, tabCount - 1)
                            if (pagerState.currentPage != target && !pagerState.isScrollInProgress) {
                                pagerState.animateScrollToPage(target)
                            }
                        }
                    }

                    // 1. Bloque Superior 100% Ancho (Header + Sub-pestañas)
                    if (meta.mostrarHeader) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(WiCss.wb)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .statusBarsPadding()
                            ) {
                                Header(
                                    meta = meta,
                                    onToggleSidebar = {
                                        scope.launch {
                                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                        }
                                    },
                                    onClickAvatar = {
                                        rutasState.navegarA("cuenta")
                                    },
                                    onClickTitle = {
                                        if (rutasState.rutaActual != com.hotelwii.app.Seo.PANTALLA_INICIAL) {
                                            rutasState.navegarA(com.hotelwii.app.Seo.PANTALLA_INICIAL)
                                        }
                                    }
                                )
                            }

                            // Sub-pestañas 100% Ancho
                            if (meta.mostrarTabs && meta.tabs.isNotEmpty()) {
                                Tabs(
                                    tabsList = meta.tabs,
                                    tabActivaIndex = rutasState.tabActivaIndex,
                                    onSeleccionarTab = { index ->
                                        rutasState.seleccionarTab(index)
                                        pagerState?.let { ps ->
                                            scope.launch { ps.animateScrollToPage(index) }
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // 2. Contenido Central con HorizontalPager Sincrónico NATIVO por página
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                horizontal = rutasState.paddingHorizontal,
                                vertical = rutasState.paddingVertical
                            ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (meta.mostrarTabs && meta.tabs.isNotEmpty() && pagerState != null) {
                                HorizontalPager(
                                    state = pagerState,
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier.fillMaxSize()
                                ) { page ->
                                    content(page)
                                }
                            } else {
                                content(0)
                            }
                        }
                    }
                }
            }
        }
    }
}
