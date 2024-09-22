package com.example.weatherapp.presentation.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentMainBinding
import com.example.weatherapp.domain.models.weather.WeatherResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainFragment : Fragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentMainBinding
    private lateinit var navController: NavController
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val lastCityKey = "lastCity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        navController = findNavController()
        binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchFieldEditText.setText(
            sharedPreferences.getString(lastCityKey, ""),
            TextView.BufferType.EDITABLE
        )
        setObservers()
        setListeners()

        if (!locationPermissionGranted()) {
            requestLocationPermission()
        }

        if (!binding.searchFieldEditText.text.isNullOrEmpty()) {
            binding.searchButton.callOnClick()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getWeatherDataByCoordinates() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    if (isInternetConnected()) {
                        mainViewModel.getWeatherData(
                            location.latitude.toString(),
                            location.longitude.toString()
                        )
                    }
                    else
                        Toast.makeText(context, getString(R.string.check_your_internet_connection), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun requestLocationPermission() {
        val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    mainViewModel.locationPermissionGranted.value = true
                }
            }
        //We need to handel all scenarios with location permission here
        //but don't have enough time for it
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun locationPermissionGranted(): Boolean {
        var permissionGranted = false
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            permissionGranted = isGranted
        }
        return permissionGranted
    }

    @SuppressLint("MissingPermission")
    private fun setListeners() {
        binding.searchButton.setOnClickListener {
            sharedPreferences.edit().putString(lastCityKey, binding.searchFieldEditText.text.toString()).apply()
            (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(requireView().windowToken, 0)
            if (isInternetConnected())
                //First requirement says "Allow customers to enter a US city"
                //It is unclear should we allow customers to enter any other city or random text
                //Probably we need to add text watcher and validate user input
                //But we need to get those validation rules first.
                mainViewModel.getGeoData(binding.searchFieldEditText.text.toString())
            else
                Toast.makeText(context, getString(R.string.check_your_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }


    private fun setObservers() {
        mainViewModel.locationPermissionGranted.observe(viewLifecycleOwner) {
            if (mainViewModel.locationPermissionGranted.value == true) {
                getWeatherDataByCoordinates()
            }
        }

        mainViewModel.weatherData.observe(viewLifecycleOwner) {
            setWeatherDataToUI(it)
        }

        mainViewModel.geoData.observe(viewLifecycleOwner) {
            if (!it.results.isNullOrEmpty()) {
                //Response may have several results. We take first one.
                //Need to get from PM requirements on how to choose the correct one.
                if (isInternetConnected()) {
                    mainViewModel.getWeatherData(
                        it.results[0].geometry.location.lat.toString(),
                        it.results[0].geometry.location.lng.toString()
                    )
                } else
                    Toast.makeText(context, getString(R.string.check_your_internet_connection), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, getString(R.string.city_not_found_please_try_again), Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Weather response contains a lot of weather data.
    //We show only some basic info, that might be interesting for the user.
    private fun setWeatherDataToUI(data: WeatherResponse?) {
        if (data?.name == null || data.weather.isNullOrEmpty()) {
            Toast.makeText(context, getString(R.string.city_not_found_please_try_again), Toast.LENGTH_SHORT).show()
        } else {
            binding.cityValueTextView.text = data.name
            binding.windSpeedValueTextView.text = data.wind?.speed.toString()
            //Weather API returns temperature in hundreds, need to check with documentation later.
            binding.temperatureValueTextView.text = data.main?.temp.toString()
            //Weather API returns weather as an array, so we get first.
            //Need to clarify with API documentation can there be more then 1 element
            binding.weatherDescriptionTextView.text = data.weather[0].description.toString()
            setWeatherImage(data.weather[0].id)
        }
    }

    private fun setWeatherImage(id: Int?) {
        var iconCode = ""
        if (id != null) {
            when (id) {
                in 200..232 -> {iconCode = "11d"}
                in 300..321 -> {iconCode = "09d"}
                in 500..531 -> {iconCode = "10d"}
                in 600..622 -> {iconCode = "13d"}
                in 701..781 -> {iconCode = "50d"}
                800 -> {iconCode = "01d"}
                801 -> {iconCode = "02d"}
                802 -> {iconCode = "03d"}
                803 -> {iconCode = "04d"}
                804 -> {iconCode = "04d"}
            }
        }
        //Picasso has automatic memory and disk caching according to https://square.github.io/picasso/
        Picasso.get().load("https://openweathermap.org/img/wn/${iconCode}@2x.png")
            .into(binding.weatherImageView)
    }

    private fun isInternetConnected(): Boolean {
        var result = false
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
        return result
    }

}