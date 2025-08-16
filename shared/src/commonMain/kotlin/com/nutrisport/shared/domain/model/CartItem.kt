package com.nutrisport.shared.domain.model

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
@OptIn(ExperimentalUuidApi::class)
data class CartItem(
    val id: String = Uuid.Companion.random().toHexString(),
    val productId: String,
    val flavor: String? = null,
    val quantity: Int
)