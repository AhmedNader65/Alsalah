package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.data.model.ServerResponse
import com.crazyidea.alsalah.data.model.User

interface UserRepository : BaseRepository{
    suspend fun login(uid:String, name:String):ServerResponse<User>
}