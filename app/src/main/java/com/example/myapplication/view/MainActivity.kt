package com.example.myapplication.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.myapplication.dao.ProductDao
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.db.AppDatabase
import com.example.myapplication.model.Product
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDatabase
    private lateinit var productDao: ProductDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        )
            .fallbackToDestructiveMigration() // recria o banco se a estrutura mudar
            .build()

        productDao = db.productDao()

        // Inserir produto
        binding.btnInserirProduto.setOnClickListener {
            val name = binding.editProductName.text.toString()
            val price = binding.editProductPrice.text.toString().toDoubleOrNull()
            val description = binding.editProductDescription.text.toString()
            val quantity = binding.editProductQuantity.text.toString().toIntOrNull() ?: 0

            // A2: validação de preço (vazio, zero ou negativo)
            if (price == null || price <= 0) {
                binding.editProductPrice.error = "Informe um preço válido"
                return@setOnClickListener
            }

            if (name.isEmpty()) {
                Toast.makeText(this, "Preencha o nome!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val product = Product(name = name, price = price, description = description, quantity = quantity)

            lifecycleScope.launch {
                productDao.insertAll(product)
                Log.i("TAG", "Produto inserido: $name - R$$price")
                Toast.makeText(this@MainActivity, "Produto inserido!", Toast.LENGTH_SHORT).show()

                // A1: limpar os campos e voltar o cursor para o Nome
                binding.editProductName.text.clear()
                binding.editProductPrice.text.clear()
                binding.editProductDescription.text.clear()
                binding.editProductQuantity.text.clear()
                binding.editProductName.requestFocus()
            }
        }

        // A3: listar produtos na tela
        binding.btnListarProdutos.setOnClickListener {
            lifecycleScope.launch {
                val products = productDao.getAll()
                mostrarLista(products)
            }
        }

        // A5: buscar por nome
        binding.btnBuscar.setOnClickListener {
            val texto = binding.editBusca.text.toString()
            lifecycleScope.launch {
                val products = productDao.buscarPorNome("%$texto%")
                mostrarLista(products)
            }
        }

        // A6: excluir todos
        binding.btnExcluirTodos.setOnClickListener {
            lifecycleScope.launch {
                productDao.deleteAll()
                binding.txtLista.text = "Todos os produtos foram excluídos."
            }
        }

        // A7: atualizar o preço pelo id
        binding.btnAtualizar.setOnClickListener {
            val id = binding.editIdAtualizar.text.toString().toIntOrNull()
            val novoPreco = binding.editNovoPreco.text.toString().toDoubleOrNull()

            if (id == null || novoPreco == null || novoPreco <= 0) {
                Toast.makeText(this, "Informe um id e um preço válidos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val produto = productDao.getById(id)
                if (produto == null) {
                    Toast.makeText(this@MainActivity, "Produto não encontrado!", Toast.LENGTH_SHORT).show()
                } else {
                    productDao.update(produto.copy(price = novoPreco))
                    Toast.makeText(this@MainActivity, "Preço atualizado!", Toast.LENGTH_SHORT).show()
                    mostrarLista(productDao.getAll())
                }
            }
        }
    }

    // A3 e A4: mostra a lista, a quantidade e a soma dos preços no TextView
    private fun mostrarLista(products: List<Product>) {
        if (products.isEmpty()) {
            binding.txtLista.text = "Nenhum produto encontrado."
            return
        }

        var texto = ""
        products.forEach {
            texto += "${it.id} - ${it.name} | R$ ${it.price} | Qtd: ${it.quantity} | ${it.description}\n"
            Log.i("TAG", "Produto = ${it.name}, Preço = R$${it.price}, Descrição = ${it.description}")
        }

        val total = products.sumOf { it.price }
        texto += "\nQuantidade de produtos: ${products.size}"
        texto += "\nSoma dos preços: R$ $total"

        binding.txtLista.text = texto
    }
}
