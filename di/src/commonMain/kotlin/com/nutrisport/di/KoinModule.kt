package com.nutrisport.di

import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.nutrisport.adminpanel.AdminPanelViewModel
import com.nutrisport.auth.AuthViewModel
import com.nutrisport.data.repository.AdminRepositoryImpl
import com.nutrisport.data.repository.CustomerRepositoryImpl
import com.nutrisport.data.GoogleDriveUploader
import com.nutrisport.domain.repository.AdminRepository
import com.nutrisport.domain.repository.CustomerRepository
import com.nutrisport.home.HomeGraphViewModel
import com.nutrisport.manageproduct.ManageProductViewModel
import com.nutrisport.profile.ProfileViewModel
import com.nutrisport.shared.util.Constants
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val sharedModule = module {
    // Single HttpClient instance for the app
    // single { createHttpClient() }

    single {
        GoogleAuthProvider.create(
            credentials = GoogleAuthCredentials(serverId = Constants.WEB_CLIENT_ID)
        )
    }

    // Drive uploader (uses platform HttpClient)
    single { GoogleDriveUploader() }

    // Repositories
    single<CustomerRepository> { CustomerRepositoryImpl() }

    // AdminRepository requires driveUploader
    single<AdminRepository> {
        AdminRepositoryImpl(
            driveUploader = get()
        )
    }

    viewModelOf(::AuthViewModel)
    viewModelOf(::HomeGraphViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::AdminPanelViewModel)
    viewModelOf(::ManageProductViewModel)
}

fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null
) {
    startKoin {
        config?.invoke(this)
        modules(sharedModule, platformModule)
    }
}
