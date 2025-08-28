package com.nutrisport.shared.di

import com.nutrisport.shared.platform.PhotoPicker
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * A Koin module that provides platform-specific dependencies for iOS.
 */
actual val platformModule: Module = module {
    single { PhotoPicker() }
}
