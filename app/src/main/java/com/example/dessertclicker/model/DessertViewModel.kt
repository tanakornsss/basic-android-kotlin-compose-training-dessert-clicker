package com.example.dessertclicker.model

import androidx.lifecycle.ViewModel
import com.example.dessertclicker.data.Datasource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DessertViewModel : ViewModel() {
    private val dessertList: List<Dessert> = Datasource.dessertList
    private val _uiState = MutableStateFlow(DessertUIState())
    val uiState: StateFlow<DessertUIState> = _uiState.asStateFlow()

    fun determineDessertToShow(
        desserts: List<Dessert> = dessertList,
        dessertsSold: Int = _uiState.value.dessertSold
    ): Dessert {
        var dessertToShow = desserts.first()
        for (dessert in desserts) {
            if (dessertsSold >= dessert.startProductionAmount) {
                dessertToShow = dessert
            } else {
                // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
                // you'll start producing more expensive desserts as determined by startProductionAmount
                // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
                // than the amount sold.
                break
            }
        }

        return dessertToShow
    }

    fun priceAndSoldIncrement() {
        _uiState.update { currentState ->

            // Create a temporary value to update the state later on.
            val newDessertSold = currentState.dessertSold + 1
            val newDessert = determineDessertToShow(dessertsSold = newDessertSold)

            currentState.copy(
                revenue = currentState.revenue + newDessert.price,
                dessertSold = newDessertSold,
                currentDessertImageID = newDessert.imageId
            )
        }
    }
}

data class DessertUIState(
    val revenue: Int = 0,
    val dessertSold: Int = 0,
    val currentDessertImageID: Int = Datasource.dessertList.first().imageId,
)