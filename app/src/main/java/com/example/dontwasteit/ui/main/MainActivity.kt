package com.example.dontwasteit.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.example.dontwasteit.databinding.ActivityMainBinding
import com.example.dontwasteit.notificaciones.NotisCaducidad
import com.example.dontwasteit.ui.addproduct.AddProductActivity
import com.example.dontwasteit.ui.statistics.StatisticsActivity
import com.example.dontwasteit.ui.welcome.OnboardingActivity
import com.example.dontwasteit.viewmodel.ProductViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // ViewModel que maneja productos y estadisticas
    private val viewModel: ProductViewModel by viewModels {
        ProductViewModel.Factory
    }

    // Estado del switch de productos consumidos
    private var mostrandoConsumidos = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura lista de productos
        configurarRecyclerView(mostrandoConsumidos)

        lifecycleScope.launch {
            viewModel.actualizarEstadisticaMensual()
        }

        // Gestion del pop-up y del mes actual
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val mesGuardado = prefs.getString("ultimoMes", null)
        val mesActual = LocalDate.now().toString().substring(0, 7)

        val prefs2 = getSharedPreferences("onboarding", MODE_PRIVATE)
        val firstTime = prefs2.getBoolean("first_time", true)

        // Lanza onboarding si es la primera vez
        if (firstTime) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
        }

        // Si ha cambiado el mes, se resetean productos y estadisticas
        if (mesGuardado != mesActual) {
            lifecycleScope.launch {
                viewModel.resetearMes()
                prefs.edit { putString("ultimoMes", mesActual) }

                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Nuevo mes detectado. Datos y estadísticas reiniciados.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // Switch para mostrar u ocultar productos consumidos
        binding.switchMostrarConsumidos.setOnCheckedChangeListener { _, isChecked ->
            mostrandoConsumidos = isChecked
            configurarRecyclerView(mostrandoConsumidos)
        }

        // Boton para añadir producto
        binding.fabAddProducto.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        // Boton para ver estadísticas
        binding.fabStats.setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
        }

        // Boton de prueba para lanzar notificacion manualmente

        binding.buttonTestNotificacion.setOnClickListener {
            val testWork = OneTimeWorkRequestBuilder<NotisCaducidad>().build()
            WorkManager.getInstance(this).enqueue(testWork)
        }
        binding.buttonTestNotificacion.apply { visibility = View.GONE }

        // Boton para reiniciar onboarding manualmente
        binding.buttonResetOnboarding.apply {
            visibility = View.GONE // ocultarlo en versión final con GONE
            setOnClickListener {
                getSharedPreferences("onboarding", MODE_PRIVATE)
                    .edit{
                        putBoolean("first_time", true)
                    }
                Toast.makeText(
                    this@MainActivity,
                    "Onboarding reiniciado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        // Crea una tarea periodica que comprueba caducidad cada 24h
        val workRequest = PeriodicWorkRequestBuilder<NotisCaducidad>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "caducidad_check",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        // Pedir permiso de notificaciones en Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
        }
    }


     // Configura el RecyclerView segun si se quieren ver productos consumidos o no.

    private fun configurarRecyclerView(consumidos: Boolean) {
        val adapter = ProductAdapter(mostrarBotones = !consumidos).apply {
            onConsumidoClick = { productoActualizado ->
                viewModel.insert(productoActualizado)
                viewModel.actualizarEstadisticaMensual()
            }
            onEliminarClick = { productoAEliminar ->
                viewModel.delete(productoAEliminar)
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Escuchar productos desde el ViewModel y mostrarlos en la lista
        lifecycleScope.launch {
            if (consumidos) {
                viewModel.productosConsumidos.collectLatest { productos ->
                    adapter.submitList(productos)
                }
            } else {
                viewModel.productosNoConsumidos.collectLatest { productos ->
                    adapter.submitList(productos)
                }
            }
        }
    }
}
