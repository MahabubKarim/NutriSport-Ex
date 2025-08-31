package com.nutrisport.manageproduct

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmk.kmpauth.google.GoogleUser
import com.mmk.nutrisport.util.RequestState
import com.nutrisport.data.GoogleDriveUploader
import com.nutrisport.domain.repository.AdminRepository
import com.nutrisport.domain.model.Product
import com.nutrisport.shared.platform.PhotoPicker
import com.nutrisport.shared.ui.ProductCategoryUi
import com.nutrisport.shared.util.PreferenceUtils
import dev.gitlive.firebase.storage.File
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
data class ManageProductState(
    val id: String = Uuid.random().toHexString(),
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val title: String = "",
    val description: String = "",
    val thumbnail: String = "thumbnail image",
    val thumbnailBytes: ByteArray? = null,
    val category: ProductCategoryUi = ProductCategoryUi.Protein,
    val flavors: String = "",
    val weight: Int? = null,
    val price: Double = 0.0,
    val isNew: Boolean = false,
    val isPopular: Boolean = false,
    val isDiscounted: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ManageProductState

        if (createdAt != other.createdAt) return false
        if (weight != other.weight) return false
        if (price != other.price) return false
        if (isNew != other.isNew) return false
        if (isPopular != other.isPopular) return false
        if (isDiscounted != other.isDiscounted) return false
        if (id != other.id) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (thumbnail != other.thumbnail) return false
        if (!thumbnailBytes.contentEquals(other.thumbnailBytes)) return false
        if (category != other.category) return false
        if (flavors != other.flavors) return false

        return true
    }

    override fun hashCode(): Int {
        var result = createdAt.hashCode()
        result = 31 * result + (weight ?: 0)
        result = 31 * result + price.hashCode()
        result = 31 * result + isNew.hashCode()
        result = 31 * result + isPopular.hashCode()
        result = 31 * result + isDiscounted.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + thumbnail.hashCode()
        result = 31 * result + (thumbnailBytes?.contentHashCode() ?: 0)
        result = 31 * result + category.hashCode()
        result = 31 * result + flavors.hashCode()
        return result
    }
}

class ManageProductViewModel(
    private val adminRepository: AdminRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel(), KoinComponent {
    private val productId = savedStateHandle.get<String>("id") ?: ""

    val preferenceUtils by inject<PreferenceUtils>()

    var screenState by mutableStateOf(ManageProductState())
        private set

    var thumbnailUploaderState: RequestState<Unit> by mutableStateOf(RequestState.Idle)
        private set

    val isFormValid: Boolean
        get() = screenState.title.isNotEmpty() &&
                screenState.description.isNotEmpty() &&
                screenState.thumbnail.isNotEmpty() &&
                screenState.price != 0.0

    init {
        productId.takeIf { it.isNotEmpty() }?.let { id ->
            viewModelScope.launch {
                val selectedProduct = adminRepository.readProductById(id)
                if (selectedProduct.isSuccess()) {
                    val product = selectedProduct.getSuccessData()

                    updateId(product.id)
                    updateCreatedAt(product.createdAt)
                    updateTitle(product.title)
                    updateDescription(product.description)
                    updateThumbnail(product.thumbnail)
                    updateThumbnailUploaderState(RequestState.Success(Unit))
                    updateCategory(ProductCategoryUi.valueOf(product.category))
                    updateFlavors(product.flavors?.joinToString(",") ?: "")
                    updateWeight(product.weight)
                    updatePrice(product.price)
                    updateNew(product.isNew)
                    updatePopular(product.isPopular)
                    updateDiscounted(product.isDiscounted)
                }
            }
        }
    }

    fun updateId(value: String) {
        screenState = screenState.copy(id = value)
    }

    fun updateCreatedAt(value: Long) {
        screenState = screenState.copy(createdAt = value)
    }

    fun updateTitle(value: String) {
        screenState = screenState.copy(title = value)
    }

    fun updateDescription(value: String) {
        screenState = screenState.copy(description = value)
    }

    fun updateThumbnail(value: String) {
        screenState = screenState.copy(thumbnail = value)
    }

    fun updateThumbnailBytes(bytes: ByteArray?) {
        screenState = screenState.copy(thumbnailBytes = bytes)
    }

    fun updateThumbnailUploaderState(value: RequestState<Unit>) {
        thumbnailUploaderState = value
    }

    fun updateCategory(value: ProductCategoryUi) {
        screenState = screenState.copy(category = value)
    }

    fun updateFlavors(value: String) {
        screenState = screenState.copy(flavors = value)
    }

    fun updateWeight(value: Int?) {
        screenState = screenState.copy(weight = value)
    }

    fun updatePrice(value: Double) {
        screenState = screenState.copy(price = value)
    }

    fun updateNew(value: Boolean) {
        screenState = screenState.copy(isNew = value)
    }

    fun updatePopular(value: Boolean) {
        screenState = screenState.copy(isPopular = value)
    }

    fun updateDiscounted(value: Boolean) {
        screenState = screenState.copy(isDiscounted = value)
    }

    fun createNewProduct(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            adminRepository.createNewProduct(
                product = Product(
                    id = screenState.id,
                    title = screenState.title,
                    description = screenState.description,
                    thumbnail = screenState.thumbnail,
                    category = screenState.category.name,
                    flavors = screenState.flavors.split(","),
                    weight = screenState.weight,
                    price = screenState.price,
                    isNew = screenState.isNew,
                    isPopular = screenState.isPopular,
                    isDiscounted = screenState.isDiscounted
                ),
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }

    fun fetchThumbnailFromDrive(fileId: String) {
        viewModelScope.launch {
            val token = preferenceUtils.getGoogleToken()
            try {
                updateThumbnailUploaderState(RequestState.Loading)
                val bytes = GoogleDriveUploader().downloadImage(token, fileId)
                updateThumbnailBytes(bytes)
                updateThumbnailUploaderState(RequestState.Success(Unit))
            } catch (e: Exception) {
                updateThumbnailUploaderState(RequestState.Error("Failed to download thumbnail: $e"))
            }
        }
    }

    fun uploadThumbnailToStorage(
        imageBytes: ByteArray?,
        fileName: String,
        onSuccess: () -> Unit,
    ) {
        if (imageBytes == null) {
            updateThumbnailUploaderState(RequestState.Error("File is null. Error while selecting an image."))
            return
        }

        updateThumbnailUploaderState(RequestState.Loading)

        viewModelScope.launch {
            try {
                val downloadUrl = adminRepository.uploadImageToDrive(
                    token = preferenceUtils.getGoogleToken(),
                    imageBytes = imageBytes,
                    fileName = fileName
                )

                val fileId = downloadUrl
                    ?: throw Exception("No file ID returned from Drive")
                fetchThumbnailFromDrive(fileId)

                if (downloadUrl.isEmpty()) {
                    throw Exception("Failed to retrieve a download URL after the upload.")
                }

                productId.takeIf { it.isNotEmpty() }?.let { id ->
                    adminRepository.updateProductThumbnail(
                        productId = id,
                        driveFieldId = fileId,
                        onSuccess = {
                            onSuccess()
                            updateThumbnailUploaderState(RequestState.Success(Unit))
                            updateThumbnail(downloadUrl)
                        },
                        onError = { message ->
                            updateThumbnailUploaderState(RequestState.Error(message))
                        }
                    )
                } ?: run {
                    onSuccess()
                    updateThumbnailUploaderState(RequestState.Success(Unit))
                    updateThumbnail(downloadUrl)
                }
            } catch (e: Exception) {
                updateThumbnailUploaderState(RequestState.Error("Error while uploading: $e"))
            }
        }
    }

    fun updateProduct(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        if (isFormValid) {
            viewModelScope.launch {
                adminRepository.updateProduct(
                    product = Product(
                        id = screenState.id,
                        createdAt = screenState.createdAt,
                        title = screenState.title,
                        description = screenState.description,
                        thumbnail = screenState.thumbnail,
                        category = screenState.category.name,
                        flavors = screenState.flavors.split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() },
                        weight = screenState.weight,
                        price = screenState.price,
                        isNew = screenState.isNew,
                        isPopular = screenState.isPopular,
                        isDiscounted = screenState.isDiscounted
                    ),
                    onSuccess = onSuccess,
                    onError = onError
                )
            }
        } else {
            onError("Please fill in the information.")
        }
    }

    fun deleteThumbnailFromStorage(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            adminRepository.deleteImageFromDrive(
                token = preferenceUtils.getGoogleToken(),
                fileId = screenState.thumbnail,
                onSuccess = {
                    productId.takeIf { it.isNotEmpty() }?.let { id ->
                        viewModelScope.launch {
                            adminRepository.updateProductThumbnail(
                                productId = id,
                                driveFieldId = null,
                                onSuccess = {
                                    updateThumbnail(value = "")
                                    updateThumbnailUploaderState(RequestState.Idle)
                                    onSuccess()
                                },
                                onError = { message -> onError(message) }
                            )
                        }
                    } ?: run {
                        updateThumbnail(value = "")
                        updateThumbnailUploaderState(RequestState.Idle)
                        onSuccess()
                    }
                },
                onError = onError
            )
        }
    }

    fun deleteProduct(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        productId.takeIf { it.isNotEmpty() }?.let { id ->
            viewModelScope.launch {
                adminRepository.deleteProduct(
                    productId = id,
                    onSuccess = {
                        deleteThumbnailFromStorage(
                            onSuccess = {},
                            onError = {}
                        )
                        onSuccess()
                    },
                    onError = { message -> onError(message) }
                )
            }
        }
    }
}