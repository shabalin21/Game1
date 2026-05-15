package com.example.myapplication.core

/**
 * Interface for all engine subsystems that require lifecycle management.
 */
interface KernelSystem {
    /**
     * Called when the WorldKernel is booting up.
     * Systems should initialize their resources and start any background processes here.
     */
    fun onBoot()

    /**
     * Called when the WorldKernel is shutting down.
     * Systems should clean up resources and stop background processes here.
     */
    fun onShutdown()
}
