package com.deepraj.weatherinfo

import com.deepraj.weatherinfo.utils.BindableAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.deepraj.weatherinfo.databinding.ActivityMainBinding
import com.deepraj.weatherinfo.databinding.ForecastItemBinding
import com.deepraj.weatherinfo.viewModel.ForecastItemViewModel
import com.deepraj.weatherinfo.viewModel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val homeViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        binding.apply {
            lifecycleOwner = this@MainActivity
            viewModel = homeViewModel
        }

        binding.forecastList.apply {
            adapter = createRecyclerViewAdapter()
        }

        homeViewModel.forecastList.observe(this) {
            if (it.isNotEmpty()) {
                val animation = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_up)
                binding.forecastList.startAnimation(animation)
            }
        }
    }

    private fun createRecyclerViewAdapter(): BindableAdapter<ForecastItemViewModel> {
        return BindableAdapter.AdapterBuilder
            .newBuilder<ForecastItemViewModel>()
            .registerView(
                inflateDataBinding = ForecastItemBinding::inflate,
                setViewModel = ForecastItemBinding::setViewModel
            ).build()
    }
}