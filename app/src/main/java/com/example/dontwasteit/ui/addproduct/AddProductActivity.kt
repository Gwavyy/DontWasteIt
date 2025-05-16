package com.example.dontwasteit.ui.addproduct

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dontwasteit.data.database.entities.Categoria
import com.example.dontwasteit.data.database.entities.Product
import com.example.dontwasteit.data.database.provider.DatabaseProvider
import com.example.dontwasteit.data.remote.api.RetrofitInstance
import com.example.dontwasteit.data.repository.ProductRepository
import com.example.dontwasteit.databinding.ActivityAddProductBinding
import com.example.dontwasteit.ui.scanner.BarcodeScannerActivity
import com.example.dontwasteit.viewmodel.ProductViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private val viewModel: ProductViewModel by viewModels {
        ProductViewModel.Factory
    }
    // Variables para datos recuperados desde la API
    private var imagenUrlRecibida: String? = null
    private var cantidadRecibida: String? = null
    private var marcaRecibida: String? = null
    private var nutriscoreRecibido: String? = null

    // Launcher para abrir la cámara y escanear codigo de barra
    private val barcodeLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val barcode = result.data?.getStringExtra("barcode")
            binding.editTextCodigoBarras.setText(barcode)
        }
    }

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = DatabaseProvider.provideDatabase(this).productDao()
        val categoriaDao = DatabaseProvider.provideDatabase(this).categoriaDao()
        val repository = ProductRepository(dao)

        // Calendario para fecha de caducidad
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

        // Boton de escaneo con cámara
        binding.buttonScanBarcode.setOnClickListener {
            val intent = Intent(this, BarcodeScannerActivity::class.java)
            barcodeLauncher.launch(intent)
        }

        // Spinner de categorias
        CoroutineScope(Dispatchers.IO).launch {
            var categorias = categoriaDao.getAll()
            if (categorias.isEmpty()) {
                val defaultCategorias = listOf(
                    Categoria(nombre = "Lácteos"),
                    Categoria(nombre = "Carnes"),
                    Categoria(nombre = "Frutas"),
                    Categoria(nombre = "Verduras"),
                    Categoria(nombre = "Bebidas"),
                    Categoria(nombre = "Panadería"),
                    Categoria(nombre = "Otros")
                )
                categoriaDao.insertAll(defaultCategorias)
                categorias = categoriaDao.getAll()
            }
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

        // Boton Buscar en API
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
                                    binding.imageViewProducto.visibility = View.GONE // al iniciar
                                    Glide.with(this@AddProductActivity)
                                        .load(imagenUrlRecibida)
                                        .into(binding.imageViewProducto)
                                    binding.imageViewProducto.visibility = View.VISIBLE

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

        // Boton Guardar
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
                        categoria = categoriaSeleccionada,
                        nutriscore = nutriscoreRecibido,
                        imagenUrl = imagenUrlRecibida,
                        cantidad = cantidadRecibida,
                        usuarioId = 1
                    )
                    // Insertamos el producto y actualizamos las estadísticas del mes
                    viewModel.insert(producto)
                    viewModel.actualizarEstadisticaMensual()
                    finish()
                }
            } else {
                Toast.makeText(this, "Rellena los campos obligatorios", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Devuelve la fecha actual formateada como yyyy-MM-dd
    private fun getTodayDateString(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date())
    }
}
