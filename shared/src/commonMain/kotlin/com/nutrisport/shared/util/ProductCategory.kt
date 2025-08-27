package com.nutrisport.shared.util

import androidx.compose.ui.graphics.Color
import com.nutrisport.shared.CategoryBlue
import com.nutrisport.shared.CategoryGreen
import com.nutrisport.shared.CategoryPurple
import com.nutrisport.shared.CategoryRed
import com.nutrisport.shared.CategoryYellow

enum class ProductCategory(
    val title: String,
    val color: Color
) {
    Protein(
        title = "Protein",
        color = CategoryYellow
    ),
    Creatine(
        title = "Creatine",
        color = CategoryBlue
    ),
    PreWorkout(
        title = "Pre-Workout",
        color = CategoryGreen
    ),
    Gainers(
        title = "Gainers",
        color = CategoryPurple
    ),
    Accessories(
        title = "Accessories",
        color = CategoryRed
    )
}