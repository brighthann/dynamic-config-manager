package com.example.configmanager.dto

import com.example.configmanager.model.ConfigValueType


data class CreateDynamicConfigRequest(
    val configKey: String,
    val name: String,
    val value: String,
    val valueType: ConfigValueType,
    val description: String? = null,
    val defaultValue: String? = null,
    val enabled: Boolean = true
)

data class UpdateDynamicConfigRequest(
    val name: String? = null,
    val description: String? = null,
    val value: String? = null,
    val valueType: ConfigValueType? = null,
    val defaultValue: String? = null,
    val enabled: Boolean? = null
)

data class UpdateValueRequest(
    val value: String
)

data class TypedValueResponse(
    val key: String,
    val value: Any?,
    val valueType: ConfigValueType,
    val enabled: Boolean
)