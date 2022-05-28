package com.stimednp.roommvvm.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by cree on March/6/2022.
 */

object Coroutines {
    fun main(work: suspend (() -> Unit)) = CoroutineScope(Dispatchers.Main).launch { work() }
    fun io(work: suspend (() -> Unit)) = CoroutineScope(Dispatchers.IO).launch { work() }
}