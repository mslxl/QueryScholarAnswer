package com.mslxl.fubuki_tsuhatsuha.ui.answer

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.mslxl.fubuki_tsuhatsuha.data.AnswerRepository

class AnswerViewModelFactory(
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle?
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(AnswerViewModel::class.java)) {
            return AnswerViewModel(
                savedStateHandle = handle,
                repository = AnswerRepository()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}