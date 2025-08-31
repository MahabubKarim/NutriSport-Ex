package com.nutrisport.adminpanel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmk.nutrisport.util.RequestState
import com.nutrisport.data.GoogleDriveUploader
import com.nutrisport.domain.repository.AdminRepository
import com.nutrisport.shared.util.PreferenceUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AdminPanelViewModel(
    private val adminRepository: AdminRepository,
) : ViewModel(), KoinComponent {

    val preferenceUtils by inject<PreferenceUtils>()
    private val products = adminRepository.readLastTenProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RequestState.Loading
        )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _thumbnailBytesMap = MutableStateFlow<Map<String, ByteArray>>(emptyMap())
    val thumbnailBytesMap: StateFlow<Map<String, ByteArray>> = _thumbnailBytesMap

    fun fetchThumbnail(productId: String, fileId: String) {
        viewModelScope.launch {
            val token = preferenceUtils.getGoogleToken()
            try {
                val bytes = GoogleDriveUploader().downloadImage(token, fileId)
                _thumbnailBytesMap.value = _thumbnailBytesMap.value + (productId to bytes)
            } catch (e: Exception) {
                println("Error downloading thumbnail for $productId: $e")
            }
        }
    }

    fun updateSearchQuery(value: String) {
        _searchQuery.value = value
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val filteredProducts =
        searchQuery
            .debounce(500)
            .flatMapLatest { query ->
                if (query.isBlank()) products
                else adminRepository.searchProductsByTitle(query)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = RequestState.Loading
            )

}