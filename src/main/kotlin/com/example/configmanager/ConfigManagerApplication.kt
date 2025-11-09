package com.example.configmanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
//testing
//import org.springframework.boot.CommandLineRunner
//import org.springframework.context.annotation.Bean
//import com.example.configmanager.model.FeatureFlag
//import com.example.configmanager.model.EmergencySwitch
//import com.example.configmanager.model.DynamicConfig
//import com.example.configmanager.repository.FeatureFlagRepository
//import com.example.configmanager.repository.DynamicConfigRepository
//import com.example.configmanager.repository.EmergencySwitchRepository
//import com.example.configmanager.service.FeatureFlagService

@SpringBootApplication
class ConfigManagerApplication
{}
fun main(args: Array<String>)
{
    runApplication<ConfigManagerApplication>(*args)

    //println("DynamicConfig class: ${DynamicConfig::class.simpleName}")
    //testFeatureLogic()
    //testEmergencySwitch()

}
//testing
//    @Bean
//    fun testService(service: FeatureFlagService) = CommandLineRunner {
//        println("\n" + "=".repeat(60))
//        println("TESTING FEATURE FLAG SERVICE")
//        println("=".repeat(60))
//
//        // Test 1: Create a feature flag
//        println("\nTEST 1: Creating feature flag...")
//        val flag = service.createFeatureFlag(
//            featureKey = "new_checkout",
//            name = "New Checkout Flow",
//            description = "Redesigned checkout experience",
//            enabled = true,
//            rolloutPercentage = 50
//        )
//        println("Created: ${flag.name} (ID: ${flag.id})")
//
//        // Test 2: Get all feature flags
//        println("\nTEST 2: Getting all feature flags...")
//        val all = service.getAllFeatureFlags()
//        println("Total flags: ${all.size}")
//
//        // Test 3: Get specific flag
//        println("\nTEST 3: Getting specific flag...")
//        val retrieved = service.getFeatureFlag("new_checkout")
//        println("Retrieved: ${retrieved.name}")
//
//        // Test 4: Check if enabled for users
//        println("\nTEST 4: Checking feature for different users...")
//        val users = listOf("alice", "bob", "charlie", "david", "eve")
//        users.forEach { userId ->
//            val (enabled, reason) = service.isFeatureEnabledForUser("new_checkout", userId)
//            val status = if (enabled) "ENABLED" else "DISABLED"
//            println("$status User '$userId': $enabled - $reason")
//        }
//
//        // Test 5: Update rollout
//        println("\nTEST 5: Incrementing rollout...")
//        service.incrementRollout("new_checkout", 25)
//        val updated = service.getFeatureFlag("new_checkout")
//        println("New rollout: ${updated.rolloutPercentage}%")
//
//        // Test 6: Enable for everyone
//        println("\nTEST 6: Enabling for everyone...")
//        service.enableForEveryone("new_checkout")
//        val fullRollout = service.getFeatureFlag("new_checkout")
//        println("Rollout now: ${fullRollout.rolloutPercentage}%")
//
//        // Test 7: Check again (should all be enabled now)
//        println("\nTEST 7: Checking users again (should all be enabled)...")
//        users.take(3).forEach { userId ->
//            val (enabled, reason) = service.isFeatureEnabledForUser("new_checkout", userId)
//            val status = if (enabled) "ENABLED" else "DISABLED"
//            println("$status User '$userId': $enabled - $reason")
//        }
//
//        // Test 8: Disable feature
//        println("\nTEST 8: Disabling feature...")
//        service.disableFeature("new_checkout")
//        val disabled = service.getFeatureFlag("new_checkout")
//        println("Enabled: ${disabled.enabled}")
//
//        // Test 9: Delete feature
//        println("\nTEST 9: Deleting feature flag...")
//        service.deleteFeatureFlag("new_checkout")
//        println("Deleted successfully")
//
//        // Test 10: Verify deleted
//        println("\nTEST 10: Verifying deletion...")
//        val exists = service.featureFlagExists("new_checkout")
//        println("Exists: $exists (should be false)")
//
//        println("\n" + "=".repeat(60))
//        println("ALL SERVICE TESTS COMPLETE")
//        println("=".repeat(60) + "\n")
//    }

//    @Bean
//    fun testRepositories(
//        featureFlagRepo: FeatureFlagRepository,
//        emergencySwitchRepo: EmergencySwitchRepository,
//        dynamicConfigRepo: DynamicConfigRepository
//    ) = CommandLineRunner {
//        println("\n" + "=".repeat(50))
//        println("TESTING REPOSITORIES")
//        println("=".repeat(50))
//
//        //test FeatureFlagRepository
//        println("\nFeature Flags:")
//        println("Total count: ${featureFlagRepo.count()}")
//
//        val checkout = featureFlagRepo.findByFeatureKey("new_checkout")
//        if (checkout.isPresent)
//        {
//            val flag = checkout.get()
//            println("  Found: ${flag.name}")
//            println("  Enabled: ${flag.enabled}")
//            println("  Rollout: ${flag.rolloutPercentage}%")
//        } else
//        {
//            println("  'new_checkout' not found")
//        }
//
//        val enabledFlags = featureFlagRepo.findByEnabledTrue()
//        println("Enabled flags: ${enabledFlags.size}")
//
//        val exists = featureFlagRepo.existsByFeatureKey("new_checkout")
//        println("Exists check: $exists")
//
//        //test EmergencySwitchRepository
//        println("\nEmergency Switches:")
//        println("Total count: ${emergencySwitchRepo.count()}")
//
//        val checkoutSwitch = emergencySwitchRepo.findBySwitchKey("checkout")
//        if (checkoutSwitch.isPresent)
//        {
//            val switch = checkoutSwitch.get()
//            println("  Found: ${switch.name}")
//            println("  Enabled: ${switch.enabled}")
//        } else
//        {
//            println("  'checkout' not found")
//        }
//
//        val disabledSwitches = emergencySwitchRepo.findByEnabledFalse()
//        println("Disabled switches: ${disabledSwitches.size}")
//
//        //test DynamicConfigRepository
//        println("\nDynamic Configs:")
//        println("Total count: ${dynamicConfigRepo.count()}")
//
//        val maxItems = dynamicConfigRepo.findByConfigKey("max_cart_items")
//        if (maxItems.isPresent)
//        {
//            val config = maxItems.get()
//            println("  Found: ${config.name}")
//            println("  Value: ${config.value}")
//            println("  Type: ${config.valueType}")
//            println("  As Integer: ${config.getIntValue()}")
//        } else {
//            println("'max_cart_items' not found")
//        }
//
//        val enabledConfigs = dynamicConfigRepo.findByEnabledTrue()
//        println("Enabled configs: ${enabledConfigs.size}")
//
//        println("\n" + "=".repeat(50))
//        println("ALL REPOSITORY TESTS COMPLETE")
//        println("=".repeat(50) + "\n")
//    }





//testing
//fun testFeatureLogic()
//{
//    //temporary test
//    println("\n=== Testing FeatureFlag Logic ===")
//
//    val flag = FeatureFlag(
//        featureKey = "new_checkout",
//        name = "New Checkout",
//        enabled = true,
//        rolloutPercentage = 50
//    )
//
//    //test with different users
//    println("User 'alice': ${flag.isEnabledForUser("alice")}")
//    println("User 'bob': ${flag.isEnabledForUser("bob")}")
//    println("User 'charlie': ${flag.isEnabledForUser("charlie")}")
//
//    //test consistency with same user twice
//    println("User 'alice' again: ${flag.isEnabledForUser("alice")}")
//
//    //segments test
//    val premiumFlag = FeatureFlag(
//        featureKey = "premium_search",
//        name = "Premium Search",
//        enabled = true,
//        rolloutPercentage = 0,
//        targetSegments = "premium"
//    )
//
//    println("\nPremium feature for regular user: ${premiumFlag.isEnabledForUser("alice", emptySet())}")
//    println("Premium feature for premium user: ${premiumFlag.isEnabledForUser("alice", setOf("premium"))}")
//
//}
//
//fun testEmergencySwitch()
//{
//
//    println("\n=== Testing EmergencySwitch ===")
//
//    val checkoutSwitch = EmergencySwitch(
//        switchKey = "checkout",
//        name = "Checkout System",
//        description = "Controls checkout availability",
//        enabled = true,
//        disabledMessage = "Checkout is temporarily unavailable. Please try again later."
//    )
//
//    println("Switch: ${checkoutSwitch.name}")
//    println("Enabled: ${checkoutSwitch.enabled}")
//    println("Message: ${checkoutSwitch.disabledMessage}")
//
//    //simulate emergency: disable checkoutswitch
//    checkoutSwitch.enabled = false
//    println("\nEmergency! Disabled checkout")
//    println("Enabled: ${checkoutSwitch.enabled}")
//
//    //simulate recovery: enablecheckoutswitch
//    checkoutSwitch.enabled = true
//    println("\nCrisis resolved! Re-enabled checkout")
//    println("Enabled: ${checkoutSwitch.enabled}")
//}