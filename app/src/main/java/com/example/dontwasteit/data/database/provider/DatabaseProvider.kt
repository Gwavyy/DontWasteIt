package com.example.dontwasteit.data.database.provider

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.dontwasteit.data.database.ProductDatabase
import com.example.dontwasteit.data.database.entities.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseProvider {
    //Devuelve la instancia de la base de datos de Room
    fun provideDatabase(context: Context): ProductDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ProductDatabase::class.java,
            "dontwasteit_db"
        )
            .fallbackToDestructiveMigration() //Si cambia el esquema y no se tiene migracion se destruye la base de datos
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    //Corrutina para insertar usuario por defecto
                    CoroutineScope(Dispatchers.IO).launch {
                        val database = provideDatabase(context)
                        val usuarioDao = database.usuarioDao()

                        // Insertar usuario por defecto
                        usuarioDao.insert(
                            Usuario(nombre = "Usuario", apellido = "Demo", email = "demo@demo.com")
                        )

                    }
                }
            })
            .build()
    }
}
