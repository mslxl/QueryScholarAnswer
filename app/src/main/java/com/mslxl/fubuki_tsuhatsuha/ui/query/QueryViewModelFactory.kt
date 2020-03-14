package com.mslxl.fubuki_tsuhatsuha.ui.query

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.mslxl.fubuki_tsuhatsuha.data.LocalDataSource
import com.mslxl.fubuki_tsuhatsuha.data.QueryRepository

class QueryViewModelFactory(
    private val applicationContext: Context,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle?
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    @Suppress("UNCHECKED_CAST")

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(QueryViewModel::class.java)) {
            return QueryViewModel(
                savedStateHandle = handle,
                repository = QueryRepository(
                    localDataSource = LocalDataSource(applicationContext.getSharedPreferences("data",Context.MODE_PRIVATE))
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}