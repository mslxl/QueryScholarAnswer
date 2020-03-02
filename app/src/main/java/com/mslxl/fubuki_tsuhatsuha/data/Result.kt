package com.mslxl.fubuki_tsuhatsuha.data

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out T : Any> {

    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val status: Int, val message: String) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[status=$status,message=$message]"
        }
    }

    inline fun onSuccess(block: (data: T) -> Unit): Result<T> {
        if (this is Success) {
            block.invoke(data)
        }
        return this
    }

    inline fun onError(block: (status: Int, message: String) -> Unit): Result<T> {
        if (this is Error) {
            block.invoke(status, message)
        }
        return this
    }
}
