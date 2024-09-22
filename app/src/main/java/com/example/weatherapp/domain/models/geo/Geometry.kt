package com.example.weatherapp.domain.models.geo

data class Geometry(
    val location: Location,
    val location_type: String,
    val viewport: Viewport
)