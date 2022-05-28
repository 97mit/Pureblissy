package com.stimednp.roommvvm.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast

/**
 * Created by cree on Oct/19/2020.
 * Find me on my lol Github :D -> https://github.com/cree
 */

object UtilExtensions {
    fun <T> Context.openActivity(it: Class<T>, extras: Bundle.() -> Unit = {}) {
        val intent = Intent(this, it)
        intent.putExtras(Bundle().apply(extras))
        startActivity(intent)
    }

    fun View.isVisible(isVisible: Boolean) {
        visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    fun Context.myToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun logD(message: String) {
        Log.d("++++log tag::",message)
    }

    fun EditText.setTextEditable(text: String) {
        this.text = Editable.Factory.getInstance().newEditable(text)
    }
}