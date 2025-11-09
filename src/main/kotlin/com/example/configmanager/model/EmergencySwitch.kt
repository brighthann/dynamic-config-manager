package com.example.configmanager.model

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 *emergency switch to quickly disable features without deployment during incidents
 *like disable checkout during payment gateway outage(example)
 **/
@Entity
@Table(name = "emergency_switches")
data class EmergencySwitch(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    //special identifier for switch (e.g., "checkout", "payment_processing")
    @Column(unique = true, nullable = false)
    val switchKey: String,

    @Column(nullable = false)
    var name: String,

    //description of what switch controls
    @Column(length = 1000)
    var description: String? = null,

    //false = feature is disabled (emergency mode)
    @Column(nullable = false)
    var enabled: Boolean = true,

    //message for when feature is disabled
    @Column(length = 500)
    var disabledMessage: String? = null,

    //switch date of creation
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    //recent switch updae
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)