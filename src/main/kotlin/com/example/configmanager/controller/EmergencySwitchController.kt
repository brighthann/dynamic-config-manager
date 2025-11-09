package com.example.configmanager.controller

import com.example.configmanager.dto.*
import com.example.configmanager.model.EmergencySwitch
import com.example.configmanager.service.EmergencySwitchService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

//REST Controller for Emergency Switch management
//Base path: /api/emergency-switches
@RestController
@RequestMapping("/api/emergency-switches")
class EmergencySwitchController(
    private val service: EmergencySwitchService
)
{

    //POST /api/emergency-switches
    @PostMapping
    fun createEmergencySwitch(
        @RequestBody request: CreateEmergencySwitchRequest
    ): ResponseEntity<EmergencySwitch>
    {
        return try {
            val emergencySwitch = service.createEmergencySwitch(
                switchKey = request.switchKey,
                name = request.name,
                description = request.description,
                enabled = request.enabled,
                disabledMessage = request.disabledMessage
            )
            ResponseEntity.status(HttpStatus.CREATED).body(emergencySwitch)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    //GET /api/emergency-switches
    @GetMapping
    fun getAllEmergencySwitches(): ResponseEntity<List<EmergencySwitch>>
    {
        val switches = service.getAllEmergencySwitches()
        return ResponseEntity.ok(switches)
    }

    //GET /api/emergency-switches/{key}
    @GetMapping("/{key}")
    fun getEmergencySwitch(@PathVariable key: String): ResponseEntity<EmergencySwitch>
    {
        return try {
            val emergencySwitch = service.getEmergencySwitch(key)
            ResponseEntity.ok(emergencySwitch)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    //GET /api/emergency-switches/disabled
    @GetMapping("/disabled")
    fun getDisabledSwitches(): ResponseEntity<List<EmergencySwitch>>
    {
        val switches = service.getDisabledSwitches()
        return ResponseEntity.ok(switches)
    }

    //PUT /api/emergency-switches/{key}
    @PutMapping("/{key}")
    fun updateEmergencySwitch(
        @PathVariable key: String,
        @RequestBody request: UpdateEmergencySwitchRequest
    ): ResponseEntity<EmergencySwitch>
    {
        return try {
            val emergencySwitch = service.updateEmergencySwitch(
                switchKey = key,
                name = request.name,
                description = request.description,
                enabled = request.enabled,
                disabledMessage = request.disabledMessage
            )
            ResponseEntity.ok(emergencySwitch)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    //DELETE /api/emergency-switches/{key}
    @DeleteMapping("/{key}")
    fun deleteEmergencySwitch(@PathVariable key: String): ResponseEntity<Void>
    {
        return try {
            service.deleteEmergencySwitch(key)
            ResponseEntity.noContent().build()
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    //GET /api/emergency-switches/{key}/status
    @GetMapping("/{key}/status")
    fun getSwitchStatus(@PathVariable key: String): ResponseEntity<SwitchStatusResponse>
    {
        val enabled = service.isSwitchEnabled(key)
        val message = if (!enabled) {
            service.getDisabledMessage(key)
        } else {
            null
        }

        val response = SwitchStatusResponse(enabled = enabled, message = message)
        return ResponseEntity.ok(response)
    }

    //POST /api/emergency-switches/{key}/toggle
    @PostMapping("/{key}/toggle")
    fun toggleSwitch(
        @PathVariable key: String,
        @RequestBody(required = false) request: ToggleSwitchRequest?
    ): ResponseEntity<EmergencySwitch>
    {
        return try {
            val emergencySwitch = service.toggleSwitch(key, request?.updatedBy)
            ResponseEntity.ok(emergencySwitch)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    //POST /api/emergency-switches/{key}/disable
    @PostMapping("/{key}/disable")
    fun disableSwitch(
        @PathVariable key: String,
        @RequestBody(required = false) request: ToggleSwitchRequest?
    ): ResponseEntity<EmergencySwitch>
    {
        return try {
            val emergencySwitch = service.disableSwitch(key, request?.updatedBy)
            ResponseEntity.ok(emergencySwitch)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    //POST /api/emergency-switches/{key}/enable
    @PostMapping("/{key}/enable")
    fun enableSwitch(
        @PathVariable key: String,
        @RequestBody(required = false) request: ToggleSwitchRequest?
    ): ResponseEntity<EmergencySwitch>
    {
        return try {
            val emergencySwitch = service.enableSwitch(key, request?.updatedBy)
            ResponseEntity.ok(emergencySwitch)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }
}