package com.example.localnewstracker

import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val id: String,
    val title: String,
    val icon: ImageVector? = null,
)


