package com.shifthackz.compose.daynightswitch

import android.animation.ArgbEvaluator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val ThumbDiameter = DayNightSwitchTokens.SelectedHandleWidth
private val UncheckedThumbDiameter = DayNightSwitchTokens.UnselectedHandleWidth
private val SwitchWidth = DayNightSwitchTokens.TrackWidth
private val SwitchHeight = DayNightSwitchTokens.TrackHeight
private val ThumbPadding = (SwitchHeight - ThumbDiameter) / 2
private val ThumbPathLength = (SwitchWidth - ThumbDiameter) - ThumbPadding

@Composable
fun DayNightSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    animationDurationMillis: Int = 100,
) {
    val animationSpec = TweenSpec<Float>(durationMillis = animationDurationMillis)
    val thumbPaddingStart = (SwitchHeight - UncheckedThumbDiameter) / 2
    val minBound = with(LocalDensity.current) { thumbPaddingStart.toPx() }
    val maxBound = with(LocalDensity.current) { ThumbPathLength.toPx() }
    val valueToOffset = remember<(Boolean) -> Float>(minBound, maxBound) {
        { value -> if (value) maxBound else minBound }
    }
    val valueToAlpha = remember<(Boolean) -> Float> {
        { value -> if (value) 1f else 0f }
    }

    val targetValue = valueToOffset(checked)
    val offset = remember { Animatable(targetValue) }

    val targetAlpha = valueToAlpha(checked)
    val alpha = remember { Animatable(targetAlpha) }

    val scope = rememberCoroutineScope()

    SideEffect {
        offset.updateBounds(lowerBound = minBound)
    }

    DisposableEffect(checked) {
        if (offset.targetValue != targetValue) {
            scope.launch {
                offset.animateTo(targetValue, animationSpec)
            }
        }
        if (alpha.targetValue != targetAlpha) {
            scope.launch {
                alpha.animateTo(targetAlpha, animationSpec)
            }
        }
        onDispose { }
    }

    val isPressed by interactionSource.collectIsPressedAsState()
    val thumbValue = offset.asState()
    val alphaValue = alpha.asState()

    val thumbOffset = if (isPressed) {
        with(LocalDensity.current) {
            if (checked) {
                ThumbPathLength - DayNightSwitchTokens.TrackOutlineWidth
            } else {
                DayNightSwitchTokens.TrackOutlineWidth
            }.toPx()
        }
    } else {
        thumbValue.value
    }

    val thumbSizeDp = if (isPressed) {
        DayNightSwitchTokens.PressedHandleWidth
    } else {
        UncheckedThumbDiameter + (ThumbDiameter - UncheckedThumbDiameter) *
                ((thumbValue.value - minBound) / (maxBound - minBound))
    }

    Box(
        modifier = modifier.toggleable(
            value = checked,
            onValueChange = onCheckedChange,
            enabled = enabled,
            role = Role.Switch,
            interactionSource = interactionSource,
            indication = null,
        )
    ) {
        val color = ArgbEvaluator().evaluate(
            alphaValue.value,
            DayNightSwitchTokens.DayBackgroundColor.toArgb(),
            DayNightSwitchTokens.NightBackgroundColor.toArgb(),
        )
        val resolvedColor = Color(color as Int)
        val bgShape = RoundedCornerShape(DayNightSwitchTokens.ShapeRadius)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(DayNightSwitchTokens.TrackWidth)
                .height(DayNightSwitchTokens.TrackHeight)
                .border(DayNightSwitchTokens.BorderSize, resolvedColor, bgShape)
                .background(resolvedColor, bgShape)
                .alpha(if (enabled) 1f else 0.5f)
        ) {
            val endImgOffset = with(LocalDensity.current) {
                -(DayNightSwitchTokens.TrackWidth * 0.45f).toPx().roundToInt()
            }
            val startImgOffset = with(LocalDensity.current) {
                (DayNightSwitchTokens.TrackWidth * 0.27f).toPx().roundToInt()
            }

            Image(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset { IntOffset(endImgOffset + thumbValue.value.roundToInt(), 0) }
                    .padding(start = thumbPaddingStart / 2)
                    .size(19.dp)
                    .alpha(alpha.asState().value),
                painter = painterResource(id = R.drawable.ic_stars),
                contentDescription = "stars",
            )
            Image(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset { IntOffset(startImgOffset + thumbValue.value.roundToInt(), 0) }
                    .size(22.dp)
                    .alpha(1f - alpha.asState().value),
                painter = painterResource(id = R.drawable.ic_clouds),
                contentDescription = "clouds",
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset { IntOffset(thumbOffset.roundToInt(), 0) }
                    .indication(
                        interactionSource = interactionSource,
                        indication = null,
                    )
                    .requiredSize(thumbSizeDp)
                    .background(resolvedColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.size(thumbSizeDp),
                    painter = painterResource(id = R.drawable.ic_sun),
                    contentDescription = "day",
                )
                Image(
                    modifier = Modifier
                        .alpha(alpha.asState().value)
                        .size(thumbSizeDp),
                    painter = painterResource(id = R.drawable.ic_moon),
                    contentDescription = "night",
                )
            }
        }
    }
}
