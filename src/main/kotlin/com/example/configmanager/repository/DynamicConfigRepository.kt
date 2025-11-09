package com.example.configmanager.repository

import com.example.configmanager.model.DynamicConfig
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface DynamicConfigRepository : JpaRepository<DynamicConfig, Long> {

    fun findByConfigKey(configKey: String): Optional<DynamicConfig>

    fun findByEnabledTrue(): List<DynamicConfig>

    fun existsByConfigKey(configKey: String): Boolean
}