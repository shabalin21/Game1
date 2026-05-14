package com.example.myapplication.data.local

import com.example.myapplication.domain.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

fun PetEntity.toDomain(json: Json): PetModel {
    return try {
        PetModel(
            id = id,
            name = name,
            stats = json.decodeFromString(statsJson),
            birthTimestamp = birthTimestamp,
            lastUpdateTimestamp = lastUpdateTimestamp,
            level = level,
            xp = xp,
            isSleeping = isSleeping,
            psychology = try {
                json.decodeFromString(psychologyJson)
            } catch (e: Exception) {
                PsychologyState()
            },
            emotionState = json.decodeFromString(emotionStateJson),
            evolutionStage = try {
                EvolutionStage.valueOf(evolutionStage)
            } catch (e: Exception) {
                EvolutionStage.BABY
            },
            lifetimeStats = try {
                json.decodeFromString(lifetimeStatsJson)
            } catch (e: Exception) {
                LifetimeStats()
            },
            employment = try {
                json.decodeFromString(employmentJson)
            } catch (e: Exception) {
                EmploymentState()
            },
            equippedItems = try {
                json.decodeFromString(equippedItemsJson)
            } catch (e: Exception) {
                emptyMap()
            },
            savedOutfits = try {
                json.decodeFromString(savedOutfitsJson)
            } catch (e: Exception) {
                emptyMap()
            },
            ownedPermanentIds = try {
                json.decodeFromString(ownedPermanentIdsJson)
            } catch (e: Exception) {
                emptySet()
            },
            activeModifiers = try {
                json.decodeFromString(activeModifiersJson)
            } catch (e: Exception) {
                emptyList()
            },
            appearance = try {
                json.decodeFromString(appearanceJson)
            } catch (e: Exception) {
                BuddyAppearance()
            },
            social = try {
                json.decodeFromString(socialJson)
            } catch (e: Exception) {
                SocialState()
            },
            casinoSession = try {
                json.decodeFromString(casinoSessionJson)
            } catch (e: Exception) {
                CasinoSession()
            },
            ownedAssets = try {
                json.decodeFromString(ownedAssetsJson)
            } catch (e: Exception) {
                emptyMap()
            },
            assetCostBasis = try {
                json.decodeFromString(assetCostBasisJson)
            } catch (e: Exception) {
                emptyMap()
            },
            missions = try {
                json.decodeFromString(missionsJson)
            } catch (e: Exception) {
                MissionState()
            },
            collectionLog = try {
                json.decodeFromString(collectionLogJson)
            } catch (e: Exception) {
                emptySet()
            }
        )
    } catch (e: Exception) {
        Timber.e(e, "Failed to decode PetEntity, returning default PetModel")
        PetModel(id = id, name = name)
    }
}

fun PetModel.toEntity(json: Json): PetEntity {
    return PetEntity(
        id = id,
        name = name,
        statsJson = json.encodeToString(stats),
        birthTimestamp = birthTimestamp,
        lastUpdateTimestamp = lastUpdateTimestamp,
        level = level,
        xp = xp,
        isSleeping = isSleeping,
        psychologyJson = json.encodeToString(psychology),
        emotionStateJson = json.encodeToString(emotionState),
        evolutionStage = evolutionStage.name,
        lifetimeStatsJson = json.encodeToString(lifetimeStats),
        employmentJson = json.encodeToString(employment),
        equippedItemsJson = json.encodeToString(equippedItems),
        savedOutfitsJson = json.encodeToString(savedOutfits),
        ownedPermanentIdsJson = json.encodeToString(ownedPermanentIds),
        activeModifiersJson = json.encodeToString(activeModifiers),
        appearanceJson = json.encodeToString(appearance),
        socialJson = json.encodeToString(social),
        casinoSessionJson = json.encodeToString(casinoSession),
        ownedAssetsJson = json.encodeToString(ownedAssets),
        assetCostBasisJson = json.encodeToString(assetCostBasis),
        missionsJson = json.encodeToString(missions),
        collectionLogJson = json.encodeToString(collectionLog)
    )
}

fun SettingsEntity.toDomain(json: Json): SettingsModel {
    return try {
        json.decodeFromString(settingsJson)
    } catch (e: Exception) {
        Timber.e(e, "Failed to decode SettingsEntity, returning default SettingsModel")
        SettingsModel()
    }
}

fun SettingsModel.toEntity(json: Json): SettingsEntity {
    return SettingsEntity(
        settingsJson = json.encodeToString(this)
    )
}

fun UpgradeModel.toEntity(): UpgradeEntity {
    return UpgradeEntity(
        id = id,
        level = currentLevel
    )
}
