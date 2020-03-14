package com.mslxl.fubuki_tsuhatsuha.data.util

import java.math.BigInteger
import java.security.MessageDigest

fun String.md5():String{
    val secretBytes = MessageDigest.getInstance("md5").digest(toByteArray(Charsets.UTF_8))
    val md5code = BigInteger(1,secretBytes).toString(16)
    return buildString {
        repeat(32-md5code.length){
            append("0")
        }
        append(md5code)
    }
}
