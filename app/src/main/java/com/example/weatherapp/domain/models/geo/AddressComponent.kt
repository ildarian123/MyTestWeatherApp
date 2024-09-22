package com.example.weatherapp.domain.models.geo

data class AddressComponent(
    val long_name: String,
    val short_name: String,
    val types: List<String>
)