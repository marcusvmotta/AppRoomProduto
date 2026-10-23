package com.example.myapplication.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplication.dao.ProductDao
import com.example.myapplication.model.Product

// A8: version mudou de 1 para 2 por causa do campo quantity
@Database(entities = [Product::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}
