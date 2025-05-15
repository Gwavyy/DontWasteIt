package com.example.dontwasteit.ui.welcome

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dontwasteit.databinding.ItemOnboardingBinding

class OnboardingAdapter(private val items: List<OnboardingItem>) : RecyclerView.Adapter<OnboardingAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemOnboardingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OnboardingItem) {
            binding.textTitle.text = item.title
            binding.textDescription.text = item.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOnboardingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
}


