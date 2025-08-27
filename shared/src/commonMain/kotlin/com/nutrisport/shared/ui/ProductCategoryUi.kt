package com.nutrisport.shared.ui

import androidx.compose.ui.graphics.Color
import com.nutrisport.shared.ui.theme.CategoryBlue
import com.nutrisport.shared.ui.theme.CategoryGreen
import com.nutrisport.shared.ui.theme.CategoryPurple
import com.nutrisport.shared.ui.theme.CategoryRed
import com.nutrisport.shared.ui.theme.CategoryYellow

enum class ProductCategoryUi(
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