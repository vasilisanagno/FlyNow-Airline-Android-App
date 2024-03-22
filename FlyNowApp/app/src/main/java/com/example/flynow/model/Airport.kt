package com.example.flynow.model

//data class for each airport and what is the matching criterion
data class Airport(
    val name: String,
    val city: String
) {
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            name,
            city,
            "${city.first()} (${name.first()})",
        )

        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}