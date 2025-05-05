package com.example.dontwasteit.data.database.provider

import android.content.Context
import androidx.room.Room
import com.example.dontwasteit.data.database.ProductDatabase

object DatabaseProvider {
    fun provideDatabase(context: Context): ProductDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ProductDatabase::class.java,
            "dontwasteit_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}
