package com.donglab.crash.internal.extensions

import android.content.res.Resources

internal fun Int.dpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}