package com.example.configmanager.service

import com.example.configmanager.model.ConfigValueType
import com.example.configmanager.model.DynamicConfig
import com.example.configmanager.repository.DynamicConfigRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


@Service
class DynamicConfigService(
    private val repository: DynamicConfigRepository
)
{
    @Transactional
    fun createDynamicConfig(
        configKey: String,
        name: String,
        value: String,
        valueType: ConfigValueType,
        description: String? = null,
        defaultValue: String? = null,
        enabled: Boolean = true
    ): DynamicConfig
    {
        if (repository.existsByConfigKey(configKey))
        {
            throw IllegalArgumentException("Dynamic config with key '$configKey' already exists")
        }

        if (!configKey.matches(Regex("^[a-z_]+$")))
        {
            throw IllegalArgumentException("Config key must contain only lowercase letters and underscores")
        }

        validateValueForType(value, valueType)

        //check if default value matches type
        defaultValue?.let { validateValueForType(it, valueType) }

        //create dynamic config
        val dynamicConfig = DynamicConfig(
            configKey = configKey,
            name = name,
            description = description,
            value = value,
            valueType = valueType,
            defaultValue = defaultValue,
            enabled = enabled
        )

        return repository.save(dynamicConfig)
    }

    private fun validateValueForType(value: String, valueType: ConfigValueType)
    {
        try {
            when (valueType) {
                ConfigValueType.STRING -> {
                    // Any string is valid
                }
                ConfigValueType.INTEGER -> {
                    value.toInt()
                }
                ConfigValueType.DECIMAL -> {
                    value.toDouble()
                }
                ConfigValueType.BOOLEAN -> {
                    val normalized = value.lowercase()
                    if (normalized !in listOf("true", "false", "1", "0", "yes", "no")) {
                        throw IllegalArgumentException("Boolean value must be true/false/1/0/yes/no")
                    }
                }
                ConfigValueType.JSON -> {
                    // Basic JSON validation - check if it starts with { or [
                    val trimmed = value.trim()
                    if (!trimmed.startsWith("{") && !trimmed.startsWith("[")) {
                        throw IllegalArgumentException("JSON value must start with { or [")
                    }
                }
            }
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Value '$value' is not a valid $valueType", e)
        }
    }

    fun getAllDynamicConfigs(): List<DynamicConfig>
    {
        return repository.findAll()
    }

    fun getDynamicConfig(configKey: String): DynamicConfig
    {
        return repository.findByConfigKey(configKey)
            .orElseThrow {
                NoSuchElementException("Dynamic config '$configKey' not found")
            }
    }

    fun getEnabledDynamicConfigs(): List<DynamicConfig>
    {
        return repository.findByEnabledTrue()
    }

    fun dynamicConfigExists(configKey: String): Boolean
    {
        return repository.existsByConfigKey(configKey)
    }

    @Transactional
    fun updateDynamicConfig(
        configKey: String,
        name: String? = null,
        description: String? = null,
        value: String? = null,
        valueType: ConfigValueType? = null,
        defaultValue: String? = null,
        enabled: Boolean? = null
    ): DynamicConfig
    {
        //get config
        val dynamicConfig = getDynamicConfig(configKey)

        //If updating value, validate against current (or new) type
        val typeToUse = valueType ?: dynamicConfig.valueType
        value?.let {
            validateValueForType(it, typeToUse)
            dynamicConfig.value = it
        }

        //If updating type, validate current value against new type
        valueType?.let {
            validateValueForType(dynamicConfig.value, it)
            dynamicConfig.valueType = it
        }

        //update fields
        name?.let { dynamicConfig.name = it }
        description?.let { dynamicConfig.description = it }
        enabled?.let { dynamicConfig.enabled = it }

        //check default value
        defaultValue?.let {
            validateValueForType(it, typeToUse)
            dynamicConfig.defaultValue = it
        }

        dynamicConfig.updatedAt = LocalDateTime.now()

        return repository.save(dynamicConfig)
    }

    @Transactional
    fun deleteDynamicConfig(configKey: String)
    {
        val dynamicConfig = getDynamicConfig(configKey)
        repository.delete(dynamicConfig)
    }

    //get config value as String
    fun getStringValue(configKey: String): String?
    {
        val configOptional = repository.findByConfigKey(configKey)
        if (configOptional.isEmpty) {
            return null
        }
        return configOptional.get().getEffectiveValue()
    }

    //get config value as String with default fallback
    fun getStringValue(configKey: String, defaultFallback: String): String {
        return getStringValue(configKey) ?: defaultFallback
    }

    //et config value as Int
    fun getIntValue(configKey: String): Int?
    {
        val configOptional = repository.findByConfigKey(configKey)
        if (configOptional.isEmpty) {
            return null
        }

        val config = configOptional.get()
        val effectiveValue = config.getEffectiveValue()

        return try {
            effectiveValue.toInt()
        } catch (e: NumberFormatException) {
            null
        }
    }

    //get config value as Int with default fallback
    fun getIntValue(configKey: String, defaultFallback: Int): Int
    {
        return getIntValue(configKey) ?: defaultFallback
    }

    //get config value as Double
    fun getDoubleValue(configKey: String): Double?
    {
        val configOptional = repository.findByConfigKey(configKey)
        if (configOptional.isEmpty)
        {
            return null
        }

        val config = configOptional.get()
        val effectiveValue = config.getEffectiveValue()

        return try {
            effectiveValue.toDouble()
        } catch (e: NumberFormatException) {
            null
        }
    }

    //config value as Double with default fallback
    fun getDoubleValue(configKey: String, defaultFallback: Double): Double
    {
        return getDoubleValue(configKey) ?: defaultFallback
    }


    //get config value as Bool
    fun getBooleanValue(configKey: String): Boolean?
    {
        val configOptional = repository.findByConfigKey(configKey)
        if (configOptional.isEmpty)
        {
            return null
        }

        val config = configOptional.get()
        val effectiveValue = config.getEffectiveValue()

        return when (effectiveValue.lowercase())
        {
            "true", "1", "yes" -> true
            "false", "0", "no" -> false
            else -> null
        }
    }

    //get config value as Bool with default fallback
    fun getBooleanValue(configKey: String, defaultFallback: Boolean): Boolean
    {
        return getBooleanValue(configKey) ?: defaultFallback
    }

    //get config value as JSON string
    fun getJsonValue(configKey: String): String?
    {
        val configOptional = repository.findByConfigKey(configKey)
        if (configOptional.isEmpty)
        {
            return null
        }

        val config = configOptional.get()
        return if (config.valueType == ConfigValueType.JSON)
        {
            config.getEffectiveValue()
        } else {
            null
        }
    }

    @Transactional
    fun enableConfig(configKey: String): DynamicConfig
    {
        return updateDynamicConfig(configKey = configKey, enabled = true)
    }

    @Transactional
    fun disableConfig(configKey: String): DynamicConfig
    {
        return updateDynamicConfig(configKey = configKey, enabled = false)
    }

    @Transactional
    fun updateValue(configKey: String, newValue: String): DynamicConfig
    {
        return updateDynamicConfig(configKey = configKey, value = newValue)
    }
}