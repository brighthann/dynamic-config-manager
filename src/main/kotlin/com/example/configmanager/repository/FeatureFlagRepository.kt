package com.example.configmanager.repository

import com.example.configmanager.model.FeatureFlag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface FeatureFlagRepository : JpaRepository<FeatureFlag, Long> {

    //find feature flag by its key
    fun findByFeatureKey(featureKey: String): Optional<FeatureFlag>

    //find all enabled feature flags
    fun findByEnabledTrue(): List<FeatureFlag>

    //check if feature flag exists with given key
    fun existsByFeatureKey(featureKey: String): Boolean

    /**
     *methods for feature flags:
     *findAll(): List<FeatureFlag>
     *findById(id: Long): Optional<FeatureFlag>
     *save(entity: FeatureFlag): FeatureFlag
     *deleteById(id: Long)
     *count(): Long
     *existsById(id: Long): Boolean
     **/
}