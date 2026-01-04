package io.mhdkhubbi.filo.ui.theme.screens

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object HomeScreen : NavKey

@Serializable
data class WelcomeScreen(val name: String) : NavKey


@Serializable
data object ProfileScreen : NavKey