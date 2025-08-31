package com.nutrisport.shared.platform

import androidx.compose.runtime.Composable

data class PhotoUri(val path: String)

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class PhotoPicker {
    fun open()
    @Composable
    fun InitializePhotoPicker(onImageSelect: (PhotoUri?) -> Unit)
}



/*
package com.nutrisport.shared.platform

import androidx.compose.runtime.Composable
import dev.gitlive.firebase.storage.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class PhotoPicker {
    fun open()
    @Composable
    fun InitializePhotoPicker(onImageSelect: (Uri?) -> Unit)
}*/
