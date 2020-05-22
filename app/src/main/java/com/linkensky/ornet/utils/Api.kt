package com.linkensky.ornet.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.rx2.rxSingle
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

suspend fun <T> api(block: suspend () -> T): T {
    return try {
        block()
    } catch (e: Exception) {
        throw e
    }
}

fun <T : Any> CoroutineScope.rxApi(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> T
) = rxSingle(context) {
    api { block() }
}