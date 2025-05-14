package com.example.dontwasteit.ui.statistics

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dontwasteit.databinding.ActivityStatisticsHistoryBinding
import com.example.dontwasteit.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

class StatisticsHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticsHistoryBinding
    private val viewModel: ProductViewModel by viewModels { ProductViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            viewModel.obtenerEstadisticas().collect { list ->
                binding.recyclerView.adapter = StatisticsAdapter(list)
            }
        }
    }
}
