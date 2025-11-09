package com.example.configmanager.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "feature_flags")
data class FeatureFlag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    val featureKey: String,

    @Column(nullable = false)
    var name: String,

    @Column(length = 1000)
    var description: String? = null,

    @Column(nullable = false)
    var enabled: Boolean = false,

    @Column(nullable = false)
    var rolloutPercentage: Int = 0,

    @Column(length = 500)
    var targetSegments: String? = null,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
{
    /**
     * Determine if feature should be enabled for a specific user
     * @param userId special id for the user
     * @param userSegments set of segments user belongs to (e.g., "premium", "beta")
     * @return true if user should see feature
     **/
    fun isEnabledForUser(userId: String, userSegments: Set<String> = emptySet()): Boolean
    {

        if (!enabled) return false

        //check segment targeting
        //check if user is in one segment if we target specific segments
        if (!targetSegments.isNullOrBlank())
        {
            val segments = targetSegments!!.split(",").map { it.trim() }
            if (segments.any { it in userSegments })
            {
                return true
            }
        }

        //rollout percentage with consistent hashing
        //ensure the same user always gets same result
        if (rolloutPercentage >= 100) return true
        if (rolloutPercentage <= 0) return false

        //hash of featureKey + userId to know if user is in rollout
        val hash = (featureKey + userId).hashCode()
        val bucket = Math.abs(hash % 100)
        return bucket < rolloutPercentage
    }
}