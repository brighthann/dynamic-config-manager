package com.example.configmanager.dto

//request DTO for creating new emergency switch
data class CreateEmergencySwitchRequest(
    val switchKey: String,
    val name: String,
    val description: String? = null,
    val enabled: Boolean = true,
    val disabledMessage: String? = null
)

//request DTO for updating existing emergency switch
class UpdateEmergencySwitchRequest(
    val name: String? = null,
    val description: String? = null,
    val enabled: Boolean? = null,
    val disabledMessage: String? = null
)

//toggling a switch
data class ToggleSwitchRequest(
    val updatedBy: String? = null
)

//response DTO for switch status check
data class SwitchStatusResponse(
    val enabled: Boolean,
    val message: String?
)