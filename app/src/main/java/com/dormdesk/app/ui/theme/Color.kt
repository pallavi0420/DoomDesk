package com.dormdesk.app.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// Modern, High-Contrast & Accessible Palette
// ==========================================

// Primary Brand Accents
val BrandPrimaryLight = Color(0xFF1E3A8A)       // Deep Royal Navy - authoritative & clear
val BrandPrimaryDark = Color(0xFF60A5FA)        // Vibrant Sky Blue - crisp on dark surfaces
val BrandSecondaryLight = Color(0xFFC2410C)     // Terracotta Warm Accent - high contrast
val BrandSecondaryDark = Color(0xFFFB923C)      // Warm Amber/Orange accent for dark mode

// Light Theme Neutrals (Crisp & High Contrast)
val BackgroundLight = Color(0xFFF8FAFC)         // Clean off-white canvas
val SurfaceLight = Color(0xFFFFFFFF)            // Pure white card surfaces
val SurfaceVariantLight = Color(0xFFF1F5F9)     // Subtle slate tint for containers
val TextPrimaryLight = Color(0xFF0F172A)        // Deep Slate 900 - 100% sharp readability
val TextSecondaryLight = Color(0xFF475569)      // Slate 600 - WCAG AA compliant (>4.5:1 ratio)
val BorderLightContrast = Color(0xFFE2E8F0)     // Clean defined border

// Dark Theme Neutrals (Refined OLED/Deep Slate)
val BackgroundDark = Color(0xFF0B0F19)          // Deep slate navy canvas
val SurfaceDark = Color(0xFF161F33)             // Elevated sleek card surface
val SurfaceVariantDark = Color(0xFF1E293B)      // Secondary dark surface
val TextPrimaryDark = Color(0xFFF8FAFC)         // Crisp pure white/slate
val TextSecondaryDark = Color(0xFFCBD5E1)       // Soft readable platinum
val BorderDarkContrast = Color(0xFF2E3D59)      // Subtle dark mode border

// Status & Semantic Colors (High Visibility)
val StatusPending = Color(0xFFD97706)           // Amber 600
val StatusAssigned = Color(0xFF0284C7)          // Sky 600
val StatusInProgress = Color(0xFF4F46E5)        // Indigo 600
val StatusResolved = Color(0xFF16A34A)          // Green 600
val StatusClosed = Color(0xFF64748B)            // Slate 500
val StatusUrgent = Color(0xFFDC2626)            // Red 600
val StatusSuccess = Color(0xFF16A34A)

// ==========================================
// Backward-Compatibility Aliases
// (Ensures legacy references compile and look great)
// ==========================================
val WarmIvory = BackgroundLight
val EspressoBrown = TextPrimaryLight
val MutedTerracotta = BrandSecondaryLight
val WarmTaupe = TextSecondaryLight
val SoftWhite = SurfaceLight
val DeepCharcoal = BackgroundDark
val SageGreen = StatusResolved

val NeutralLightSurface = SurfaceVariantLight
val NeutralDarkSurface = SurfaceVariantDark
val NeutralDarkCard = SurfaceDark
val BorderLight = BorderLightContrast
val BorderDark = BorderDarkContrast
val TextMuted = TextSecondaryLight
val TextMutedLight = TextSecondaryDark
