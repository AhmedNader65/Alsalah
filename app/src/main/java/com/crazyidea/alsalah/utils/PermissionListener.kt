package com.crazyidea.alsalah.utils

interface PermissionListener {
    fun   shouldShowRationaleInfo()
    fun   isPermissionGranted(isGranted : Boolean)
}