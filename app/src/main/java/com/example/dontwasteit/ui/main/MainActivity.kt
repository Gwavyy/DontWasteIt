package com.example.dontwasteit.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dontwasteit.databinding.ActivityMainBinding
import com.example.dontwasteit.notificaciones.NotisCaducidad
import com.example.dontwasteit.ui.addproduct.AddProductActivity
import com.example.dontwasteit.ui.statistics.StatisticsActivity
import com.example.dontwasteit.viewmodel.ProductViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import androidx.core.content.edit
import com.example.dontwasteit.ui.welcome.OnboardingActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ProductViewModel by viewModels {
        ProductViewModel.Factory
    }

    private var mostrandoConsumidos = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarRecyclerView(mostrandoConsumidos)

        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val mesGuardado = prefs.getString("ultimoMes", null)
        val mesActual = LocalDate.now().toString().substring(0, 7)
        val prefs2 = getSharedPreferences("onboarding", MODE_PRIVATE)
        val firstTime = prefs2.getBoolean("first_time", true)

        if (firstTime) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
        }


        if (mesGuardado != mesActual) {
            lifecycleScope.launch {
                viewModel.resetearMes()
                prefs.edit() { putString("ultimoMes", mesActual) }

                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Nuevo mes detectado. Datos y estadísticas reiniciados.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // Escuchar cambios en el switch
        binding.switchMostrarConsumidos.setOnCheckedChangeListener { _, isChecked ->
            mostrandoConsumidos = isChecked
            configurarRecyclerView(mostrandoConsumidos)
        }

        // Botón flotante para añadir productos
        binding.fabAddProducto.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }
        binding.fabStats.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }



        //Comprueba la caducidad una vez al dia
        val workRequest = PeriodicWorkRequestBuilder<NotisCaducidad>(1, TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "caducidad_check",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
        }
        //boton de prueba para notificaciones
        binding.buttonTestNotificacion.setOnClickListener {
            val testWork = OneTimeWorkRequestBuilder<NotisCaducidad>().build()
            WorkManager.getInstance(this).enqueue(testWork)
        }

    }

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
