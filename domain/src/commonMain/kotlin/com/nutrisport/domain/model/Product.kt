package com.nutrisport.domain.model

import androidx.compose.ui.graphics.Color
import com.nutrisport.shared.CategoryBlue
import com.nutrisport.shared.CategoryGreen
import com.nutrisport.shared.CategoryPurple
import com.nutrisport.shared.CategoryRed
import com.nutrisport.shared.CategoryYellow
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
data class Product(
    val id: String,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val title: String,
    val description: String,
    val thumbnail: String,
    val category: String,
    val flavors: List<String>? = null,
    val weight: Int? = null,
    val price: Double,
    val isPopular: Boolean = false,
    val isDiscounted: Boolean = false,
    val isNew: Boolean = false
)