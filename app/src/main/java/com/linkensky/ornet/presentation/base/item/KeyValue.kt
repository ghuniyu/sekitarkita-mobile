package com.linkensky.ornet.presentation.base.item

/**
 * Key Value
 *
 * Provide key based hash code for object without hash code like onClickListener
 */

class KeyValue<X>(private var value: X, private var key: String = "") {
    override fun hashCode() = if (value == null) -1 else key.hashCode()

    fun setValue(key: String = this.key, value: X) {
        this.key = key
        this.value = value
    }

    fun setValue(value: X) {
        this.value = value
    }

    fun getValue() = value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KeyValue<*>

        if (key != other.key) return false

        return true
    }

    operator fun invoke() = getValue()
}

typealias UnitListener = KeyValue<(() -> Unit)?>

fun <X> keyValue(value: X) = KeyValue(value, "")
fun <X> keyValue(key: String = "", value: X) = KeyValue(value, key)