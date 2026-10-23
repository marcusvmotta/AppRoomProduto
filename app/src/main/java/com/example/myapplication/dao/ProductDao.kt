package com.example.myapplication.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.model.Product

@Dao
interface ProductDao {
    // suspend: o Room não permite consultas na main thread
    @Query("SELECT * FROM product")
    suspend fun getAll(): List<Product>

    @Insert
    suspend fun insertAll(vararg products: Product)

    // A5: buscar por nome
    @Query("SELECT * FROM product WHERE name LIKE :nome")
    suspend fun buscarPorNome(nome: String): List<Product>

    // A6: excluir todos
    @Query("DELETE FROM product")
    suspend fun deleteAll()

    // A7: buscar um produto pelo id e atualizar
    @Query("SELECT * FROM product WHERE id = :id")
    suspend fun getById(id: Int): Product?

    @Update
    suspend fun update(product: Product)
}
