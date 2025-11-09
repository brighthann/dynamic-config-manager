package com.example.configmanager.controller

import com.example.configmanager.dto.*
import com.example.configmanager.model.FeatureFlag
import com.example.configmanager.service.FeatureFlagService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

//REST Controller for Feature Flag management
//Base path: /api/feature-flags
@RestController
@RequestMapping("/api/feature-flags")
class FeatureFlagController(
    private val service: FeatureFlagService
)
{
    //endpoints

    //Create a new feature flag(CREATE Endpoint)
    //POST /api/feature-flags
    //Body: CreateFeatureFlagRequest
    //return: 201 Created with feature flag
    @PostMapping
    fun createFeatureFlag(
        @RequestBody request: CreateFeatureFlagRequest
    ): ResponseEntity<FeatureFlag>
    {
        return try
        {
            val featureFlag = service.createFeatureFlag(
                featureKey = request.featureKey,
                name = request.name,
                description = request.description,
                enabled = request.enabled,
                rolloutPercentage = request.rolloutPercentage,
                targetSegments = request.targetSegments
            )
            ResponseEntity.status(HttpStatus.CREATED).body(featureFlag)
        } catch (e: IllegalArgumentException)
        {
            ResponseEntity.badRequest().build()
        }
    }

    //READ Endpoint
    //GET /api/feature-flags
    //return: 200 OK with list of feature flags
    @GetMapping
    fun getAllFeatureFlags(): ResponseEntity<List<FeatureFlag>>
    {
        val flags = service.getAllFeatureFlags()
        return ResponseEntity.ok(flags)
    }

    //GET /api/feature-flags/{key}
    //Returns: 200 OK with feature flag, or 404 Not Found
    @GetMapping("/{key}")
    fun getFeatureFlag(@PathVariable key: String): ResponseEntity<FeatureFlag>
    {
        return try {
            val featureFlag = service.getFeatureFlag(key)
            ResponseEntity.ok(featureFlag)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    //GET /api/feature-flags?enabled=true
    //return: 200 OK with list of enabled feature flags
    @GetMapping("/enabled")
    fun getEnabledFeatureFlags(): ResponseEntity<List<FeatureFlag>>
    {
        val flags = service.getEnabledFeatureFlags()
        return ResponseEntity.ok(flags)
    }

    //UPDATE Endpoint
    //PUT /api/feature-flags/{key}
    //body: UpdateFeatureFlagRequest
    //return: 200 OK with updated feature flag, or 404 Not Found
    @PutMapping("/{key}")
    fun updateFeatureFlag(
        @PathVariable key: String,
        @RequestBody request: UpdateFeatureFlagRequest
    ): ResponseEntity<FeatureFlag>
    {
        return try {
            val featureFlag = service.updateFeatureFlag(
                featureKey = key,
                name = request.name,
                description = request.description,
                enabled = request.enabled,
                rolloutPercentage = request.rolloutPercentage,
                targetSegments = request.targetSegments
            )
            ResponseEntity.ok(featureFlag)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    //DELETE Endpoint
    //DELETE /api/feature-flags/{key}
    //return: 204 No Content, or 404 Not Found
    @DeleteMapping("/{key}")
    fun deleteFeatureFlag(@PathVariable key: String): ResponseEntity<Void>
    {
        return try {
            service.deleteFeatureFlag(key)
            ResponseEntity.noContent().build()
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    //SpecOps
    //POST /api/feature-flags/{key}/check
    //body: CheckFeatureRequest
    //return: 200 OK with CheckFeatureResponse
    @PostMapping("/{key}/check")
    fun checkFeatureForUser(
        @PathVariable key: String,
        @RequestBody request: CheckFeatureRequest
    ): ResponseEntity<CheckFeatureResponse>
    {
        val (enabled, reason) = service.isFeatureEnabledForUser(
            featureKey = key,
            userId = request.userId,
            userSegments = request.userSegments
        )

        val response = CheckFeatureResponse(enabled = enabled, reason = reason)
        return ResponseEntity.ok(response)
    }

    //Increment rollout percentage
    //POST /api/feature-flags/{key}/rollout
    //body: IncrementRolloutRequest
    //return: 200 OK with updated feature flag, or 404 Not Found
    @PostMapping("/{key}/rollout")
    fun incrementRollout(
        @PathVariable key: String,
        @RequestBody(required = false) request: IncrementRolloutRequest?
    ): ResponseEntity<FeatureFlag>
    {
        return try {
            val incrementBy = request?.incrementBy ?: 10
            val featureFlag = service.incrementRollout(key, incrementBy)
            ResponseEntity.ok(featureFlag)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    //rollout 100%
    //POST /api/feature-flags/{key}/enable-all
    //Returns: 200 OK with updated feature flag, or 404 Not Found
    @PostMapping("/{key}/enable-all")
    fun enableForEveryone(@PathVariable key: String): ResponseEntity<FeatureFlag>
    {
        return try {
            val featureFlag = service.enableForEveryone(key)
            ResponseEntity.ok(featureFlag)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    //POST /api/feature-flags/{key}/disable
    //Returns: 200 OK with updated feature flag, or 404 Not Found
    @PostMapping("/{key}/disable")
    fun disableFeature(@PathVariable key: String): ResponseEntity<FeatureFlag>
    {
        return try {
            val featureFlag = service.disableFeature(key)
            ResponseEntity.ok(featureFlag)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

}