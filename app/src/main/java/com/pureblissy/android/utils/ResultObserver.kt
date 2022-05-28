package com.stimednp.roommvvm.utils

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.stimednp.roommvvm.utils.UtilExtensions.logD

class ResultObserver(private val registry: ActivityResultRegistry) : DefaultLifecycleObserver {
    lateinit var getContent: ActivityResultLauncher<String>
    override fun onCreate(owner: LifecycleOwner) {
        getContent = registry.register("key", owner, ActivityResultContracts.GetContent()) { uri ->
            // Handle the returned Uri
            logD("fdsfsd"+uri)
        }
    }
    fun selectImage() {
        getContent.launch("image/*")
    }
}