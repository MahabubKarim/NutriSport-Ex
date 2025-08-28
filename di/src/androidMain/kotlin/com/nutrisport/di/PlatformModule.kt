package com.nutrisport.di

import com.nutrisport.shared.platform.PhotoPicker
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { PhotoPicker() }
}