package com.nutrisport.shared.platform

import androidx.compose.runtime.*
import platform.Foundation.NSURL
import platform.UIKit.*
import platform.darwin.NSObject

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PhotoPicker {
    private var openPhotoPicker = mutableStateOf(false)

    actual fun open() {
        openPhotoPicker.value = true
    }

    @Composable
    actual fun InitializePhotoPicker(
        onImageSelect: (PhotoUri?) -> Unit
    ) {
        val openPhotoPickerState by remember { openPhotoPicker }

        LaunchedEffect(openPhotoPickerState) {
            if (openPhotoPickerState) {
                val viewController = getCurrentViewController()
                val picker = UIImagePickerController().apply {
                    sourceType =
                        UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
                    mediaTypes = listOf("public.image", "public.heif")
                    delegate = PickerDelegate { uri ->
                        onImageSelect(uri)
                        openPhotoPicker.value = false
                    }
                }
                viewController?.presentViewController(picker, animated = true, completion = null)
            }
        }
    }

    private fun getCurrentViewController(): UIViewController? {
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        return findTopViewController(rootViewController)
    }

    private fun findTopViewController(viewController: UIViewController?): UIViewController? {
        return when (viewController) {
            is UINavigationController -> findTopViewController(viewController.visibleViewController)
            is UITabBarController -> findTopViewController(viewController.selectedViewController)
            is UIViewController -> viewController.presentedViewController?.let { findTopViewController(it) } ?: viewController
            else -> viewController
        }
    }

    private class PickerDelegate(private val callback: (PhotoUri?) -> Unit) : NSObject(),
        UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

        override fun imagePickerController(
            picker: UIImagePickerController,
            didFinishPickingMediaWithInfo: Map<Any?, *>
        ) {
            val url = didFinishPickingMediaWithInfo[UIImagePickerControllerImageURL] as? NSURL
            callback(url?.absoluteString?.let { PhotoUri(it) })
            picker.dismissViewControllerAnimated(true, completion = null)
        }

        override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
            callback(null)
            picker.dismissViewControllerAnimated(true, completion = null)
        }
    }
}
