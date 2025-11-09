package com.example.configmanager.controller

import com.example.configmanager.dto.*
import com.example.configmanager.model.ConfigValueType
import com.example.configmanager.model.DynamicConfig
import com.example.configmanager.service.DynamicConfigService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

//REST Controller for Dynamic Configuration management
//base path: /api/configs
@RestController
@RequestMapping("/api/configs")
class DynamicConfigController(
    private val service: DynamicConfigService
)
{

    //POST /api/configs
    @PostMapping
    fun createDynamicConfig(
        @RequestBody request: CreateDynamicConfigRequest
    ): ResponseEntity<DynamicConfig>
    {
        return try {
            val dynamicConfig = service.createDynamicConfig(
                configKey = request.configKey,
                name = request.name,
                value = request.value,
                valueType = request.valueType,
                description = request.description,
                defaultValue = request.defaultValue,
                enabled = request.enabled
            )
            ResponseEntity.status(HttpStatus.CREATED).body(dynamicConfig)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    //GET /api/configs
    @GetMapping
    fun getAllDynamicConfigs(): ResponseEntity<List<DynamicConfig>> {
        val configs = service.getAllDynamicConfigs()
        return ResponseEntity.ok(configs)
    }

    //GET /api/configs/{key}
    @GetMapping("/{key}")
    fun getDynamicConfig(@PathVariable key: String): ResponseEntity<DynamicConfig> {
        return try {
            val dynamicConfig = service.getDynamicConfig(key)
            ResponseEntity.ok(dynamicConfig)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    //GET /api/configs/enabled
    @GetMapping("/enabled")
    fun getEnabledDynamicConfigs(): ResponseEntity<List<DynamicConfig>> {
        val configs = service.getEnabledDynamicConfigs()
        return ResponseEntity.ok(configs)
    }

    //PUT /api/configs/{key}
    @PutMapping("/{key}")
    fun updateDynamicConfig(
        @PathVariable key: String,
        @RequestBody request: UpdateDynamicConfigRequest
    ): ResponseEntity<DynamicConfig>
    {
        return try {
            val dynamicConfig = service.updateDynamicConfig(
                configKey = key,
                name = request.name,
                description = request.description,
                value = request.value,
                valueType = request.valueType,
                defaultValue = request.defaultValue,
                enabled = request.enabled
            )
            ResponseEntity.ok(dynamicConfig)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    //DELETE /api/configs/{key}
    @DeleteMapping("/{key}")
    fun deleteDynamicConfig(@PathVariable key: String): ResponseEntity<Void> {
        return try {
            service.deleteDynamicConfig(key)
            ResponseEntity.noContent().build()
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    //GET /api/configs/{key}/value
    @GetMapping("/{key}/value")
    fun getTypedValue(@PathVariable key: String): ResponseEntity<TypedValueResponse>
    {
        return try {
            val config = service.getDynamicConfig(key)

            val typedValue: Any? = when (config.valueType) {
                ConfigValueType.STRING -> service.getStringValue(key)
                ConfigValueType.INTEGER -> service.getIntValue(key)
                ConfigValueType.DECIMAL -> service.getDoubleValue(key)
                ConfigValueType.BOOLEAN -> service.getBooleanValue(key)
                ConfigValueType.JSON -> service.getJsonValue(key)
            }

            val response = TypedValueResponse(
                key = config.configKey,
                value = typedValue,
                valueType = config.valueType,
                enabled = config.enabled
            )

            ResponseEntity.ok(response)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    //GET /api/configs/{key}/value/string?default=fallback
    @GetMapping("/{key}/value/string")
    fun getStringValue(
        @PathVariable key: String,
        @RequestParam(required = false) default: String?
    ): ResponseEntity<Map<String, String?>>
    {
        val value = if (default != null) {
            service.getStringValue(key, default)
        } else {
            service.getStringValue(key)
        }
        return ResponseEntity.ok(mapOf("value" to value))
    }

    //GET /api/configs/{key}/value/int?default=100
    @GetMapping("/{key}/value/int")
    fun getIntValue(
        @PathVariable key: String,
        @RequestParam(required = false) default: Int?
    ): ResponseEntity<Map<String, Int?>>
    {
        val value = if (default != null) {
            service.getIntValue(key, default)
        } else {
            service.getIntValue(key)
        }
        return ResponseEntity.ok(mapOf("value" to value))
    }

    //GET /api/configs/{key}/value/double?default=10.5
    @GetMapping("/{key}/value/double")
    fun getDoubleValue(
        @PathVariable key: String,
        @RequestParam(required = false) default: Double?
    ): ResponseEntity<Map<String, Double?>>
    {
        val value = if (default != null) {
            service.getDoubleValue(key, default)
        } else {
            service.getDoubleValue(key)
        }
        return ResponseEntity.ok(mapOf("value" to value))
    }

    //GET /api/configs/{key}/value/bool?default=true
    @GetMapping("/{key}/value/bool")
    fun getBooleanValue(
        @PathVariable key: String,
        @RequestParam(required = false) default: Boolean?
    ): ResponseEntity<Map<String, Boolean?>>
    {
        val value = if (default != null) {
            service.getBooleanValue(key, default)
        } else {
            service.getBooleanValue(key)
        }
        return ResponseEntity.ok(mapOf("value" to value))
    }

    //PATCH /api/configs/{key}/value
    @PatchMapping("/{key}/value")
    fun updateValue(
        @PathVariable key: String,
        @RequestBody request: UpdateValueRequest
    ): ResponseEntity<DynamicConfig>
    {
        return try {
            val dynamicConfig = service.updateValue(key, request.value)
            ResponseEntity.ok(dynamicConfig)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    //POST /api/configs/{key}/enable
    @PostMapping("/{key}/enable")
    fun enableConfig(@PathVariable key: String): ResponseEntity<DynamicConfig>
    {
        return try {
            val dynamicConfig = service.enableConfig(key)
            ResponseEntity.ok(dynamicConfig)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    //POST /api/configs/{key}/disable
    @PostMapping("/{key}/disable")
    fun disableConfig(@PathVariable key: String): ResponseEntity<DynamicConfig> {
        return try {
            val dynamicConfig = service.disableConfig(key)
            ResponseEntity.ok(dynamicConfig)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }
}