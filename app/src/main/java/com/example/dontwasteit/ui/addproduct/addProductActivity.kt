package com.example.dontwasteit.ui.addproduct

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dontwasteit.data.database.provider.DatabaseProvider
import com.example.dontwasteit.data.database.entities.Product
import com.example.dontwasteit.databinding.ActivityAddProductBinding
import com.example.dontwasteit.data.remote.api.RetrofitInstance
import com.example.dontwasteit.data.repository.ProductRepository
import com.example.dontwasteit.viewmodel.ProductViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private val viewModel: ProductViewModel by viewModels{
        ProductViewModel.Factory
    }

    private var imagenUrlRecibida: String? = null
    private var cantidadRecibida: String? = null
    private var marcaRecibida: String? = null
    private var nutriscoreRecibido: String? = null

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = DatabaseProvider.provideDatabase(this).productDao()
        val categoriaDao = DatabaseProvider.provideDatabase(this).categoriaDao()
        val repository = ProductRepository(dao)

        // Configurar calendario para fecha
        binding.editTextFechaCaducidad.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val fecha = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    binding.editTextFechaCaducidad.setText(fecha)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.minDate = System.currentTimeMillis()
                show()
            }
        }

        // Cargar categorías en el spinner
        CoroutineScope(Dispatchers.IO).launch {
            val categorias = categoriaDao.getAll()
            runOnUiThread {
                val adapter = ArrayAdapter(
                    this@AddProductActivity,
                    android.R.layout.simple_spinner_item,
                    categorias.map { it.nombre }
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerCategoria.adapter = adapter
            }
        }

        // Buscar producto por código de barras
        binding.buttonBuscarEnApi.setOnClickListener {
            val barcode = binding.editTextCodigoBarras.text.toString()
            if (barcode.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = RetrofitInstance.api.getProductByBarcode(barcode)
                        val product = response.product
                        if (response.status == 1 && product?.product_name != null) {
                            imagenUrlRecibida = product.image_front_url
                            cantidadRecibida = product.quantity
                            marcaRecibida = product.brands
                            nutriscoreRecibido = product.nutriscore_grade

                            runOnUiThread {
                                binding.editTextNombre.setText(product.product_name)
                                if (!imagenUrlRecibida.isNullOrEmpty()) {
                                    Glide.with(this@AddProductActivity)
                                        .load(imagenUrlRecibida)
                                        .into(binding.imageViewProducto)
                                }
                                Toast.makeText(this@AddProductActivity, "Producto encontrado", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(this@AddProductActivity, "Producto no encontrado", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("API_ERROR", "Error de red: ${e.message}", e)
                        runOnUiThread {
                            Toast.makeText(this@AddProductActivity, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Introduce un código de barras", Toast.LENGTH_SHORT).show()
            }
        }

        // Guardar producto
        binding.buttonGuardar.setOnClickListener {
            val nombre = binding.editTextNombre.text.toString().trim()
            val fechaCaducidad = binding.editTextFechaCaducidad.text.toString().trim()
            val codigoBarras = binding.editTextCodigoBarras.text.toString().trim()
            val categoriaSeleccionada = binding.spinnerCategoria.selectedItem?.toString()

            if (nombre.isNotEmpty() && fechaCaducidad.isNotEmpty() && categoriaSeleccionada != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    val categorias = categoriaDao.getAll()
                    val categoriaId = categorias.firstOrNull { it.nombre == categoriaSeleccionada }?.id

                    val producto = Product(
                        nombre = nombre,
                        fechaCaducidad = fechaCaducidad,
                        fechaRegistro = getTodayDateString(),
                        escaneado = codigoBarras.isNotEmpty(),
                        consumido = false,
                        barcode = if (codigoBarras.isNotEmpty()) codigoBarras else null,
                        marca = marcaRecibida,
                        categoriaId = categoriaId,
                        nutriscore = nutriscoreRecibido,
                        imagenUrl = imagenUrlRecibida,
                        cantidad = cantidadRecibida,
                        usuarioId = 1 // único usuario por ahora
                    )

                    viewModel.insert(producto)
                    finish()
                }
            } else {
                Toast.makeText(this, "Rellena los campos obligatorios", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getTodayDateString(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date())
    }
}
