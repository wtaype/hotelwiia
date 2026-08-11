package com.hotelwii.core.kidev

import com.hotelwii.core.kicss.*


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider

enum class WiTooltipPosition { Top, Bottom }

class TooltipPositionProvider(
    private val position: WiTooltipPosition,
    private val marginPx: Int = 16
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val x = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2
        val y = when (position) {
            WiTooltipPosition.Top -> {
                anchorBounds.top - popupContentSize.height - marginPx
            }
            WiTooltipPosition.Bottom -> {
                anchorBounds.bottom + marginPx
            }
        }
        return IntOffset(x, y)
    }
}

@Composable
fun WiTooltip(
    expanded: Boolean,
    text: String,
    position: WiTooltipPosition = WiTooltipPosition.Top,
    type: WiMsgType = WiMsgType.Info,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {}
) {
    if (expanded) {
        Popup(
            popupPositionProvider = TooltipPositionProvider(position),
            onDismissRequest = onDismissRequest
        ) {
            Box(
                modifier = modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(type.wiColor())
                    .padding(horizontal = dpSmart(10f, 1.1f, 14f), vertical = dpSmart(6f, 0.7f, 9f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = WiCss.white,
                    style = WiText.tiny.copy(
                        fontFamily = fPoppins,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

