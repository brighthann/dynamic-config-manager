package com.example.configmanager.service

import com.example.configmanager.model.EmergencySwitch
import com.example.configmanager.repository.EmergencySwitchRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


//service for emergency switches mana
@Service
class EmergencySwitchService(
    private val repository: EmergencySwitchRepository
)
{


    // create a new emergency switch
    @Transactional
    fun createEmergencySwitch(
        switchKey: String,
        name: String,
        description: String? = null,
        enabled: Boolean = true,
        disabledMessage: String? = null
    ): EmergencySwitch
    {
        if (repository.existsBySwitchKey(switchKey))
        {
            throw IllegalArgumentException("Emergency switch with key '$switchKey' already exists")
        }

        if (!switchKey.matches(Regex("^[a-z_]+$")))
        {
            throw IllegalArgumentException("Switch key must contain only lowercase letters and underscores")
        }

        val emergencySwitch = EmergencySwitch(
            switchKey = switchKey,
            name = name,
            description = description,
            enabled = enabled,
            disabledMessage = disabledMessage
        )

        return repository.save(emergencySwitch)
    }

    fun getAllEmergencySwitches(): List<EmergencySwitch> {
        return repository.findAll()
    }

    fun getEmergencySwitch(switchKey: String): EmergencySwitch
    {
        return repository.findBySwitchKey(switchKey)
            .orElseThrow {
                NoSuchElementException("Emergency switch '$switchKey' not found")
            }
    }

    fun getDisabledSwitches(): List<EmergencySwitch>
    {
        return repository.findByEnabledFalse()
    }

    fun emergencySwitchExists(switchKey: String): Boolean
    {
        return repository.existsBySwitchKey(switchKey)
    }

    @Transactional
    fun updateEmergencySwitch(
        switchKey: String,
        name: String? = null,
        description: String? = null,
        enabled: Boolean? = null,
        disabledMessage: String? = null
    ): EmergencySwitch
    {
        //get emergency switch
        val emergencySwitch = getEmergencySwitch(switchKey)

        //update fields
        name?.let { emergencySwitch.name = it }
        description?.let { emergencySwitch.description = it }
        enabled?.let { emergencySwitch.enabled = it }
        disabledMessage?.let { emergencySwitch.disabledMessage = it }

        emergencySwitch.updatedAt = LocalDateTime.now()

        return repository.save(emergencySwitch)
    }

    @Transactional
    fun deleteEmergencySwitch(switchKey: String)
    {
        val emergencySwitch = getEmergencySwitch(switchKey)
        repository.delete(emergencySwitch)
    }

    fun isSwitchEnabled(switchKey: String): Boolean
    {
        val switchOptional = repository.findBySwitchKey(switchKey)

        //default to enabled if switch not exist
        if (switchOptional.isEmpty) {
            return true
        }

        return switchOptional.get().enabled
    }

    fun getDisabledMessage(switchKey: String): String?
    {
        val switchOptional = repository.findBySwitchKey(switchKey)

        if (switchOptional.isEmpty) {
            return null
        }

        val emergencySwitch = switchOptional.get()
        return if (!emergencySwitch.enabled)
        {
            emergencySwitch.disabledMessage ?: "This feature is temporarily unavailable"
        } else {
            null
        }
    }

    //toggle switch: for quick response in case of incident
    @Transactional
    fun toggleSwitch(switchKey: String, updatedBy: String? = null): EmergencySwitch
    {
        val emergencySwitch = getEmergencySwitch(switchKey)

        //flip switch
        emergencySwitch.enabled = !emergencySwitch.enabled

        emergencySwitch.updatedAt = LocalDateTime.now()

        return repository.save(emergencySwitch)
    }

    @Transactional
    fun disableSwitch(switchKey: String, updatedBy: String? = null): EmergencySwitch
    {
        return updateEmergencySwitch(
            switchKey = switchKey,
            enabled = false
        ).also {
            it.updatedAt = LocalDateTime.now()
            repository.save(it)
        }
    }

    @Transactional
    fun enableSwitch(switchKey: String, updatedBy: String? = null): EmergencySwitch
    {
        return updateEmergencySwitch(
            switchKey = switchKey,
            enabled = true
        ).also {
            it.updatedAt = LocalDateTime.now()
            repository.save(it)
        }
    }
}