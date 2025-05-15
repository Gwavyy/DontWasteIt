package com.example.dontwasteit.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.dontwasteit.databinding.ActivityOnboardingBinding
import com.example.dontwasteit.ui.main.MainActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        val adapter = OnboardingAdapter(onboardingItems)
        binding.viewPager.adapter = adapter

        binding.buttonEmpezar.setOnClickListener {
            getSharedPreferences("onboarding", MODE_PRIVATE).edit()
                .putBoolean("first_time", false)
                .apply()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.buttonEmpezar.isEnabled = position == onboardingItems.lastIndex
            }
        })
    }
}
// OnboardingItem.kt
data class OnboardingItem(
    val title: String,
    val description: String
)
