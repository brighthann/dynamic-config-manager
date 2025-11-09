package com.example.configmanager.repository

import com.example.configmanager.model.EmergencySwitch
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface EmergencySwitchRepository : JpaRepository<EmergencySwitch, Long> {

    fun findBySwitchKey(switchKey: String): Optional<EmergencySwitch>

    fun findByEnabledFalse(): List<EmergencySwitch>

    fun existsBySwitchKey(switchKey: String): Boolean
}