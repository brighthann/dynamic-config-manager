package com.example.configmanager.model

import jakarta.persistence.*
import java.time.LocalDateTime

//supported value types
enum class ConfigValueType {
    STRING,
    INTEGER,
    DECIMAL,
    BOOLEAN,
    JSON    //JSON objects
}

//configuration values stored with type information for safe retrieval
@Entity
@Table(name = "dynamic_configs")
data class DynamicConfig(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    //configuration key
    @Column(unique = true, nullable = false)
    val configKey: String,

    @Column(nullable = false)
    var name: String,

    @Column(length = 1000)
    var description: String? = null,

    //values stored as string to be converted to proper type based on valueType
    @Column(name = "config_value",nullable = false)
    var value: String,

    //valuetype for safe conversion
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var valueType: ConfigValueType,

    //default value to use if config disabled
    @Column
    var defaultValue: String? = null,


    //config status enabled or not
    @Column(nullable = false)
    var enabled: Boolean = true,

    //config creation time
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),


    //config last update
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

{
    //get value as int or null if conversion fails
    fun getIntValue(): Int? {
        return try {
            value.toInt()
        } catch (e: NumberFormatException) {
            null
        }
    }

    //get value as Double or null if conversion fails
    fun getDoubleValue(): Double? {
        return try {
            value.toDouble()
        } catch (e: NumberFormatException) {
            null
        }
    }

    //get value as Bool
    fun getBooleanValue(): Boolean {
        return when (value.lowercase()) {
            "true", "1", "yes" -> true
            "false", "0", "no" -> false
            else -> false
        }
    }

    //return defaultValue if config not enabled
    fun getEffectiveValue(): String {
        return if (enabled) value else (defaultValue ?: value)
    }
}