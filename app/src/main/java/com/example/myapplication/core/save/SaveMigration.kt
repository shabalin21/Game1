package com.example.myapplication.core.save

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveMigrator @Inject constructor() {

    private val currentVersion = 2

    fun migrate(json: String, fromVersion: Int): String {
        var result = json
        var version = fromVersion

        while (version < currentVersion) {
            result = applyMigration(result, version)
            version++
            Timber.i("SaveMigration: Migrated to version $version")
        }

        return result
    }

    private fun applyMigration(json: String, version: Int): String {
        return when(version) {
            1 -> migrateV1ToV2(json)
            else -> json
        }
    }

    private fun migrateV1ToV2(json: String): String {
        // Example: Add a new mandatory field
        Timber.d("SaveMigration: V1 -> V2 Logic Executed")
        return json.replace("}", ", \"version\": 2}")
    }
}
