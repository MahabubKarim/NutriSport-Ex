package com.nutrisport.domain.repository

import com.mmk.kmpauth.google.GoogleUser
import com.mmk.nutrisport.util.RequestState
import com.nutrisport.domain.model.Product
import dev.gitlive.firebase.storage.File
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface AdminRepository {
    fun getCurrentUserId(): String?
    suspend fun createNewProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    )

    fun readLastTenProducts(): Flow<RequestState<List<Product>>>
    suspend fun readProductById(id: String): RequestState<Product>
    suspend fun updateProductThumbnail(
        productId: String,
        driveFieldId: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    )

    suspend fun updateProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    )

    suspend fun deleteProduct(
        productId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    )

    fun searchProductsByTitle(
        searchQuery: String,
    ): Flow<RequestState<List<Product>>>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun uploadImageToDrive(
        token: String?,
        imageBytes: ByteArray,
        fileName: String = "${Uuid.random().toHexString()}.jpg",
        folderId: String? = null
    ): String?

    suspend fun deleteImageFromDrive(
        token: String?,
        fileId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
}