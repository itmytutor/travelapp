package com.example.ui.navigation

sealed class Screen {
    data object Home : Screen()
    data object TravelList : Screen()
    data object ProfileSettings : Screen()
    data class Detail(val destinationId: Long) : Screen()
    data class AddEdit(val destinationId: Long? = null) : Screen()
}
