package com.example.giziku.util

import kotlinx.serialization.Serializable

@Serializable
data class Nutrisi(
    val kalori: Int,
    val protein: Int,
    val karbohidrat: Int,
    val lemak: Int,
    val serat: Int,
    val gula: Int,
    val natrium: Int,
    val kolesterol: Int,
    val vitamin: Int
)