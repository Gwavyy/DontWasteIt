package com.example.dontwasteit.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.dontwasteit.databinding.ActivityOnboardingBinding
import com.example.dontwasteit.ui.main.MainActivity
import androidx.core.content.edit

//Pop up que se muestra solo la primera vez que se abre la aplicación.
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lista de pantallas del onboarding, cada una con titulo y descripcion
        val onboardingItems = listOf(
            OnboardingItem(
                title = "Bienvenido a Don't Waste It",
                description = "Una app para ayudarte a reducir el desperdicio de alimentos en casa.",

            ),
            OnboardingItem(
                title = "Organiza tus productos",
                description = "Escanea, clasifica y guarda los alimentos para llevar un mejor control.",

            ),
            OnboardingItem(
                title = "Evita caducidades",
                description = "Recibe notificaciones antes de que los productos caduquen.",

            )
        )
        // Adaptador personalizado que muestra los items de onboarding en un ViewPager
        val adapter = OnboardingAdapter(onboardingItems)
        binding.viewPager.adapter = adapter

        // Boton para finalizar el onboarding y abrir la app
        binding.buttonEmpezar.setOnClickListener {
            getSharedPreferences("onboarding", MODE_PRIVATE).edit() {
                putBoolean("first_time", false)
            }
            // Lo manda al MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Habilita el boton de empezar solo en la ultima pantalla
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.buttonEmpezar.isEnabled = position == onboardingItems.lastIndex
            }
        })
    }
}
//Data class que representa el contenido de una pantalla del onboarding.
data class OnboardingItem(
    val title: String,
    val description: String
)
