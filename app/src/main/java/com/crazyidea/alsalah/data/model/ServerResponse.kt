package com.crazyidea.alsalah.data.model

data class ServerResponse<T>(val code: Int, val data: T,val message:String, val status: String)
