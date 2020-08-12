package ru.maxim.barybians.utils

import android.content.Context
import android.widget.Toast
import java.lang.ref.WeakReference

fun <T> weak(obj: T) = WeakReference(obj)

fun <T> WeakReference<T>.isNull() = this.get() == null
fun <T> WeakReference<T>.isNotNull() = !this.isNull()

fun Any?.isNull() = this == null
fun Any?.isNotNull() = !this.isNull()

fun Context.toast(text: String) {Toast.makeText(this, text, Toast.LENGTH_SHORT).show()}
fun Context.toast(resource: Int) {Toast.makeText(this, getString(resource), Toast.LENGTH_SHORT).show()}
fun Context.longToast(text: String) {Toast.makeText(this, text, Toast.LENGTH_LONG).show()}
fun Context.longToast(resource: Int) {Toast.makeText(this, getString(resource), Toast.LENGTH_LONG).show()}
