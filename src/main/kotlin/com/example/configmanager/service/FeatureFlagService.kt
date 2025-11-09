package com.example.configmanager.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.example.configmanager.model.FeatureFlag
import com.example.configmanager.repository.FeatureFlagRepository
import java.time.LocalDateTime


//service for feature flag management
@Service
class FeatureFlagService(
    private val repository: FeatureFlagRepository
)
{
    //methods

    //create a new feature flag method
    //throw IllegalArgumentException if feature key exists or validation fail
    @Transactional
    fun createFeatureFlag(
        featureKey: String,
        name: String,
        description: String? = null,
        enabled: Boolean = false,
        rolloutPercentage: Int = 0,
        targetSegments: String? = null
    ): FeatureFlag
    {
        //check if key exists
        if (repository.existsByFeatureKey(featureKey))
        {
            throw IllegalArgumentException("Feature flag with key '$featureKey' already exists")
        }

        //rollout percentage
        if (rolloutPercentage < 0 || rolloutPercentage > 100)
        {
            throw IllegalArgumentException("Rollout percentage must be between 0 and 100")
        }

        //validation: feature key format
        if (!featureKey.matches(Regex("^[a-z_]+$")))
        {
            throw IllegalArgumentException("Feature key must contain only lowercase letters and underscores")
        }

        //create feature flag
        val featureFlag = FeatureFlag(
            featureKey = featureKey,
            name = name,
            description = description,
            enabled = enabled,
            rolloutPercentage = rolloutPercentage,
            targetSegments = targetSegments
        )

        return repository.save(featureFlag)
    }

    //read methods
    //all featureflags
    fun getAllFeatureFlags(): List<FeatureFlag>
    {
        return repository.findAll()
    }

    //get feature flag by key
    fun getFeatureFlag(featureKey: String): FeatureFlag
    {
        return repository.findByFeatureKey(featureKey)
            .orElseThrow {
                NoSuchElementException("Feature flag '$featureKey' not found")
            }
    }

    //get enabled feature flags
    fun getEnabledFeatureFlags(): List<FeatureFlag>
    {
        return repository.findByEnabledTrue()
    }

    //check if feature flag exists
    fun featureFlagExists(featureKey: String): Boolean
    {
        return repository.existsByFeatureKey(featureKey)
    }

    //update-method
    //only update non-null fields
    @Transactional
    fun updateFeatureFlag(
        featureKey: String,
        name: String? = null,
        description: String? = null,
        enabled: Boolean? = null,
        rolloutPercentage: Int? = null,
        targetSegments: String? = null
    ): FeatureFlag
    {
        //get existing feature flag
        val featureFlag = getFeatureFlag(featureKey)

        //update only provided fields
        name?.let { featureFlag.name = it }
        description?.let { featureFlag.description = it }
        enabled?.let { featureFlag.enabled = it }

        //check rollout percentage
        rolloutPercentage?.let {
            if (it < 0 || it > 100) {
                throw IllegalArgumentException("Rollout percentage must be between 0 and 100")
            }
            featureFlag.rolloutPercentage = it
        }

        targetSegments?.let { featureFlag.targetSegments = it }

        //update timestamp
        featureFlag.updatedAt = LocalDateTime.now()

        return repository.save(featureFlag)
    }


    //delete-method
    @Transactional
    fun deleteFeatureFlag(featureKey: String)
    {
        //check if exists
        val featureFlag = getFeatureFlag(featureKey)

        repository.delete(featureFlag)
    }

    //eval-method
    //check if a feature is enabled for a specific user
    //consistent hashing to ensure same user always gets same result
    //@param featureKey: feature to check
    //@param userId
    //@param userSegments: segment set of user(e.g., "premium", "beta")
    //@return pair(enabled: Boolean, reason: String)
    fun isFeatureEnabledForUser(
        featureKey: String,
        userId: String,
        userSegments: Set<String> = emptySet()
    ): Pair<Boolean, String>
    {
        //get feature flag
        val featureFlagOptional = repository.findByFeatureKey(featureKey)
        //default to disabled if feature flag false
        if (featureFlagOptional.isEmpty)
        {
            return Pair(false, "Feature flag not found")
        }

        val featureFlag = featureFlagOptional.get()
        //check if globally disabled
        if (!featureFlag.enabled)
        {
            return Pair(false, "Feature is globally disabled")
        }

        //check if enabled for user by usiig entity's logic
        val enabled = featureFlag.isEnabledForUser(userId, userSegments)
        //know the reason
        val reason = when {
            !enabled && !featureFlag.targetSegments.isNullOrBlank() -> {
                "User not in target segments: ${featureFlag.targetSegments}"
            }
            !enabled && featureFlag.rolloutPercentage < 100 -> {
                "User not in ${featureFlag.rolloutPercentage}% rollout"
            }
            enabled && !featureFlag.targetSegments.isNullOrBlank() -> {
                "User in target segment"
            }
            enabled && featureFlag.rolloutPercentage >= 100 -> {
                "Feature at 100% rollout"
            }
            enabled -> {
                "User in ${featureFlag.rolloutPercentage}% rollout"
            }
            else -> "Reason unknown"
        }

        return Pair(enabled, reason)
    }


    //check if feature is enabled for user(without reason)
    fun isFeatureEnabled(featureKey: String, userId: String): Boolean
    {
        val (enabled, _) = isFeatureEnabledForUser(featureKey, userId)
        return enabled
    }


    //gradual rollout(increment rollout percentage by given amount)
    @Transactional
    fun incrementRollout(featureKey: String, incrementBy: Int = 10): FeatureFlag
    {
        val featureFlag = getFeatureFlag(featureKey)

        val newPercentage = (featureFlag.rolloutPercentage + incrementBy).coerceIn(0, 100)
        featureFlag.rolloutPercentage = newPercentage
        featureFlag.updatedAt = LocalDateTime.now()

        return repository.save(featureFlag)
    }

    //100% rollout(enable feature for all users)
    @Transactional
    fun enableForEveryone(featureKey: String): FeatureFlag
    {
        return updateFeatureFlag(
            featureKey = featureKey,
            enabled = true,
            rolloutPercentage = 100
        )
    }


    //disable for all users
    @Transactional
    fun disableFeature(featureKey: String): FeatureFlag
    {
        return updateFeatureFlag(
            featureKey = featureKey,
            enabled = false
        )
    }


}


