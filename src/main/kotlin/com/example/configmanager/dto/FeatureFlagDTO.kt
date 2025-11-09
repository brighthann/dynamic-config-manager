package com.example.configmanager.dto

//DTO request for creating new feature flag
data class CreateFeatureFlagRequest(
    val featureKey: String,
    val name: String,
    val description: String? = null,
    val enabled: Boolean = false,
    val rolloutPercentage: Int = 0,
    val targetSegments: String? = null
)

//for updating feature flag
data class UpdateFeatureFlagRequest(
    val name: String? = null,
    val description: String? = null,
    val enabled: Boolean? = null,
    val rolloutPercentage: Int? = null,
    val targetSegments: String? = null
)

//check if a feature is enabled
data class CheckFeatureRequest(
    val userId: String,
    val userSegments: Set<String> = emptySet()
)

//response DTO for feature check
data class CheckFeatureResponse(
    val enabled: Boolean,
    val reason: String
)

//request DTO for incrementing rollout
data class IncrementRolloutRequest(
    val incrementBy: Int = 10
)