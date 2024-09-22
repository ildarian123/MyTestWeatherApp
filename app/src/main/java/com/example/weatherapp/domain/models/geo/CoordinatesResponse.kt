package com.example.weatherapp.domain.models.geo

data class CoordinatesResponse(
    val results: List<Result>? = null,
    val status: String? = ""
)