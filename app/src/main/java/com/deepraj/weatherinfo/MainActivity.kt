package com.deepraj.weatherinfo

import android.content.Context
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.deepraj.weatherinfo.databinding.ActivityMainBinding
import com.deepraj.weatherinfo.databinding.ForecastItemBinding
import com.deepraj.weatherinfo.utils.BindableAdapter
import com.deepraj.weatherinfo.viewModel.ForecastItemViewModel
import com.deepraj.weatherinfo.viewModel.WeatherViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        binding.apply {
            lifecycleOwner = this@MainActivity
            viewModel = weatherViewModel
        }

        binding.forecastList.apply {
            adapter = createRecyclerViewAdapter()
        }

        weatherViewModel.forecastList.observe(this) {
            if (it.isNotEmpty()) {
                val animation = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_up)
                binding.forecastList.startAnimation(animation)
            }
        }
        weatherViewModel.isFailure.observe(this){
            if (it == true){
                showSnackBarWithAction(this)
            }
        }
    }
    private fun showSnackBarWithAction(context : Context) {
        val snackBarColor = context.getColor( R.color.background_error)
        Snackbar.make(binding.container, R.string.error_message, Snackbar.LENGTH_LONG)
            .setAction(R.string.retry) {
                //Reset isFailure LiveData before making the API call since liveData does not emit for every state change.
                weatherViewModel.resetFailure()
                weatherViewModel.fetchCurrentTempAndForecast()
            }
            .setBackgroundTint(snackBarColor)
            .show()
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