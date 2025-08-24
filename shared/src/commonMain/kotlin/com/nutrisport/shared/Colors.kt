package com.nutrisport.shared

// Surface  app backgrounds, sheets, or panels.

// Primary, Secondary, Tertiary  emphasize foreground elements.

// Container  backgrounds of interactive components.

// On  the contrasting color (e.g., white or black depending on theme)

// Variant  variants are designed to provide visual hierarchy while staying consistent with the palette.

// https://m3.material.io/styles/color/roles

import androidx.compose.ui.graphics.Color

val GrayLighter = Color(0xFFFAFAFA)
val Gray = Color(0xFFF1F1F1)
val GrayDarker = Color(0xFFEBEBEB)

val Yellowish = Color(0xffb4c104)
val Orange = Color(0xFFF24C00)
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val Red = Color(0xFFDD0000)

val CategoryYellow = Color(0xFFFFC738)
val CategoryBlue = Color(0xFF38B3FF)
val CategoryGreen = Color(0xFF19D109)
val CategoryPurple = Color(0xFF8E5EFF)
val CategoryRed = Color(0xFFFF5E60)

val Surface = White
val SurfaceLighter = GrayLighter
val SurfaceDarker = Gray
val SurfaceBrand = Yellowish
val SurfaceError = Red
val SurfaceSecondary = Orange

val BorderIdle = GrayDarker
val BorderError = Red
val BorderSecondary = Orange

val TextPrimary = Black
val TextSecondary = Orange
val TextWhite = White
val TextBrand = Yellowish

val ButtonPrimary = Yellowish
val ButtonSecondary = GrayDarker
val ButtonDisabled = GrayDarker

val IconPrimary = Black
val IconSecondary = Orange
val IconWhite = White