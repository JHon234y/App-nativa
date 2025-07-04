
package com.agritrack.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.agritrack.app.data.database.HarvestDao
import com.agritrack.app.ui.screens.detail.HarvestDetailViewModel
import com.agritrack.app.ui.screens.home.MainViewModel

/**
 * Fábrica para crear instancias de MainViewModel.
 * Es necesaria porque MainViewModel tiene una dependencia (HarvestDao) en su constructor.
 */
class MainViewModelFactory(private val dao: HarvestDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * Fábrica para crear instancias de HarvestDetailViewModel.
 * Es necesaria porque HarvestDetailViewModel tiene dependencias en su constructor.
 */
class HarvestDetailViewModelFactory(
    private val dao: HarvestDao,
    private val harvestId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HarvestDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HarvestDetailViewModel(dao, harvestId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
