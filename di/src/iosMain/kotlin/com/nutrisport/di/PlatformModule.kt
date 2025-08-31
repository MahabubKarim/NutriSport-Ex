package com.nutrisport.di

import com.nutrisport.shared.platform.PhotoPicker
import com.nutrisport.shared.util.IOSPreferenceUtils
import com.nutrisport.shared.util.PreferenceUtils
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { PhotoPicker() }
    single<PreferenceUtils> { IOSPreferenceUtils() }
}