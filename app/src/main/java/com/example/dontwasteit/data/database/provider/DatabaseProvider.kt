package com.example.dontwasteit.data.database.provider

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.dontwasteit.data.database.ProductDatabase
import com.example.dontwasteit.data.database.entities.Usuario
import com.example.dontwasteit.data.database.entities.Categoria
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseProvider {

    fun provideDatabase(context: Context): ProductDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ProductDatabase::class.java,
            "dontwasteit_db"
        )
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    CoroutineScope(Dispatchers.IO).launch {
                        val database = provideDatabase(context)
                        val usuarioDao = database.usuarioDao()
                        val categoriaDao = database.categoriaDao()

                        // Insertar usuario por defecto
                        usuarioDao.insert(
                            Usuario(nombre = "Usuario", apellido = "Demo", email = "demo@demo.com")
                        )

                        // Insertar categorías por defecto
                        val categorias = listOf(
                            Categoria(nombre = "Lácteos"),
                            Categoria(nombre = "Carnes"),
                            Categoria(nombre = "Frutas"),
                            Categoria(nombre = "Verduras"),
                            Categoria(nombre = "Bebidas")
                        )
                        categoriaDao.insertAll(categorias)
                    }
                }
            })
            .build()
    }
}
