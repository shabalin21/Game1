package com.example.myapplication.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.ui.theme.*
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.automirrored.filled.List
import com.example.myapplication.ui.screen.home.HomeScreen
import com.example.myapplication.ui.screen.work.WorkScreen
import com.example.myapplication.ui.screen.outside.CityHubScreen
import com.example.myapplication.ui.screen.shop.ShopScreen
import com.example.myapplication.ui.screen.gym.GymScreen
import com.example.myapplication.ui.screen.inventory.InventoryHubScreen
import com.example.myapplication.ui.screen.ownership.PropertyHubScreen
import com.example.myapplication.ui.screen.social.SocialHubScreen
import com.example.myapplication.ui.screen.stats.ProfileHubScreen
import com.example.myapplication.ui.screen.system.CheatMenu
import com.example.myapplication.ui.screen.buddy.BuddyScreen
import com.example.myapplication.ui.screen.casino.CasinoScreen
import com.example.myapplication.ui.screen.minigames.*
import com.example.myapplication.ui.screen.cargo.CargoScreen
import com.example.myapplication.ui.screen.assets.AssetsScreen
import com.example.myapplication.ui.screen.upgrades.UpgradesScreen
import com.example.myapplication.ui.screen.ownership.OwnershipGalleryScreen
import com.example.myapplication.ui.screen.settings.SettingsScreen
import com.example.myapplication.ui.screen.social.SocialFeedScreen
import com.example.myapplication.ui.screen.shop.SpecialShopsScreen
import com.example.myapplication.ui.screen.shop.BlackMarketScreen
import com.example.myapplication.ui.screen.mission.MissionsScreen
import com.example.myapplication.ui.screen.progression.PrestigeScreen
import com.example.myapplication.ui.screen.debug.DebugMenu
import com.example.myapplication.ui.screen.stats.StatsScreen
import com.example.myapplication.ui.animation.JuiceViewModel
import com.example.myapplication.domain.admin.CheatManager
import com.example.myapplication.ui.component.JuiceOverlay
import androidx.compose.foundation.layout.Box

@Composable
fun PetNavHost() {
    val juiceViewModel: JuiceViewModel = hiltViewModel()
    val cheatViewModel: com.example.myapplication.ui.screen.system.CheatViewModel = hiltViewModel()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val rootScreens = listOf(
        Screen.Home, 
        Screen.District, 
        Screen.Inventory, 
        Screen.Social, 
        Screen.Profile
    )
    
    val isRootScreen = currentDestination?.hierarchy?.any { dest ->
        rootScreens.any { dest.hasRoute(it::class) }
    } == true

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        bottomBar = {
            if (isRootScreen) {
                NavigationBar(
                    containerColor = SurfaceDark,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .background(SurfaceDark)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(Shapes.cardRadius))
                        .border(1.dp, GlassBorder, RoundedCornerShape(Shapes.cardRadius))
                ) {
                    val items = listOf(
                        Triple("Home", Screen.Home, Icons.Default.Home),
                        Triple("District", Screen.District, Icons.Default.Place),
                        Triple("Social", Screen.Social, Icons.Default.Share),
                        Triple("Inventory", Screen.Inventory, Icons.Default.ShoppingCart),
                        Triple("Profile", Screen.Profile, Icons.Default.Person)
                    )
                    items.forEach { (name, screen, icon) ->
                        val selected = currentDestination?.hierarchy?.any { it.hasRoute(screen::class) } == true
                        
                        val indicatorColor by animateColorAsState(
                            targetValue = if (selected) PremiumPurple.copy(alpha = 0.1f) else Color.Transparent,
                            label = "NavIndicatorColor"
                        )

                        NavigationBarItem(
                            icon = { 
                                Icon(
                                    icon, 
                                    contentDescription = name, 
                                    tint = if (selected) PremiumPurple else Color.White.copy(alpha = 0.4f)
                                ) 
                            },
                            label = { 
                                Text(
                                    name, 
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (selected) Color.White else Color.White.copy(alpha = 0.4f),
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1,
                                    fontSize = 9.sp
                                ) 
                            },
                            selected = selected,
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = indicatorColor,
                                selectedIconColor = PremiumPurple,
                                unselectedIconColor = Color.White.copy(alpha = 0.4f)
                            ),
                            onClick = {
                                navController.navigate(screen) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        val minigameViewModel: MinigameViewModel = hiltViewModel()
        
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home,
                modifier = Modifier.padding(if (isRootScreen) innerPadding else PaddingValues()),
                enterTransition = { fadeIn(tween(400)) },
                exitTransition = { fadeOut(tween(300)) }
            ) {
                composable<Screen.Home> { 
                    HomeScreen(
                        onNavigateToUpgrades = { navController.navigate(Screen.Upgrades) },
                        onNavigateToSettings = { navController.navigate(Screen.Settings) }
                    ) 
                }
                
                composable<Screen.District> {
                    CityHubScreen(
                        onNavigateToWork = { navController.navigate(Screen.Work) },
                        onNavigateToCasino = { navController.navigate(Screen.Casino) },
                        onNavigateToBlackMarket = { navController.navigate(Screen.SpecialtyExchange) },
                        onNavigateToInvestments = { navController.navigate(Screen.Assets) },
                        onNavigateToMinigames = { navController.navigate(Screen.ActivityHub) },
                        onNavigateToStore = { navController.navigate(Screen.Store) },
                        onNavigateToProperty = { navController.navigate(Screen.Property) }
                    )
                }

                composable<Screen.Inventory> {
                    InventoryHubScreen(
                        onNavigateToCargo = { navController.navigate(Screen.Storage) },
                        onNavigateToAppearance = { navController.navigate(Screen.Buddy) }
                    )
                }

                composable<Screen.Property> {
                    PropertyHubScreen(
                        onNavigateToOwnership = { navController.navigate(Screen.Ownership) },
                        onNavigateToUpgrades = { navController.navigate(Screen.Upgrades) }
                    )
                }

                composable<Screen.Social> {
                    SocialHubScreen(
                        onNavigateToFeed = { navController.navigate(Screen.SocialFeed) },
                        onNavigateToMissions = { navController.navigate(Screen.Missions) }
                    )
                }

                composable<Screen.Profile> {
                    ProfileHubScreen(
                        onNavigateToStats = { navController.navigate(Screen.Statistics) },
                        onNavigateToPrestige = { navController.navigate(Screen.Prestige) },
                        onNavigateToDebug = { navController.navigate(Screen.Debug) },
                        onNavigateToSettings = { navController.navigate(Screen.Settings) }
                    )
                }

                composable<Screen.Settings> {
                    SettingsScreen(
                        onBack = { navController.popBackStack() },
                        onNavigateToDebug = { navController.navigate(Screen.Debug) }
                    )
                }

                // Sub-pages
                composable<Screen.Work> { WorkScreen(onBack = { navController.popBackStack() }) }
                composable<Screen.Casino> { CasinoScreen(onBack = { navController.popBackStack() }) }
                composable<Screen.Buddy> { BuddyScreen(onBack = { navController.popBackStack() }) }
                composable<Screen.Storage> { CargoScreen(onBack = { navController.popBackStack() }) }
                composable<Screen.Assets> { 
                    AssetsScreen(
                        onBack = { navController.popBackStack() }
                    ) 
                }
                composable<Screen.Store> { ShopScreen(onBack = { navController.popBackStack() }) }
                composable<Screen.Ownership> { OwnershipGalleryScreen(onBack = { navController.popBackStack() }) }
                composable<Screen.SocialFeed> { SocialFeedScreen(onBack = { navController.popBackStack() }) }
                composable<Screen.SpecialShops> { SpecialShopsScreen(onBack = { navController.popBackStack() }) }
                composable<Screen.SpecialtyExchange> { BlackMarketScreen(onBack = { navController.popBackStack() }) }
                composable<Screen.Missions> { MissionsScreen(onBack = { navController.popBackStack() }) }
                composable<Screen.Prestige> { PrestigeScreen(onBack = { navController.popBackStack() }) }
                composable<Screen.Upgrades> { UpgradesScreen(onBack = { navController.popBackStack() }) }
                composable<Screen.Statistics> { StatsScreen(onBack = { navController.popBackStack() }) }
                
                composable<Screen.Debug> {
                    CheatMenu(
                        viewModel = cheatViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable<Screen.ActivityHub> {
                    MinigameHub(
                        onPlayTapRush = { navController.navigate(Screen.TapRush) },
                        onPlayMemoryMatch = { navController.navigate(Screen.MemoryMatch) },
                        onPlayReactionTap = { navController.navigate(Screen.ReactionTap) },
                        onPlayNeonDodge = { navController.navigate(Screen.FocusDodge) },
                        onPlayNeonHack = { navController.navigate(Screen.SystemSync) },
                        onPlayVoidRunner = { navController.navigate(Screen.KineticPath) },
                        onBack = { navController.popBackStack() }
                    )
                }

                // Simulation Activities (Minigames)
                composable<Screen.TapRush> {
                    TapRushGame(
                        onGameEnd = { score -> minigameViewModel.rewardTapRush(score) },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable<Screen.MemoryMatch> {
                    MemoryMatchGame(
                        onGameEnd = { score -> minigameViewModel.rewardMemoryMatch(score) },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable<Screen.ReactionTap> {
                    ReactionTapGame(
                        onGameEnd = { score -> minigameViewModel.rewardReactionTap(score) },
                        onExit = { navController.popBackStack() }
                    )
                }
                composable<Screen.FocusDodge> {
                    NeonDodgeGame(
                        onGameEnd = { score -> minigameViewModel.rewardNeonDodge(score) },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable<Screen.SystemSync> {
                    NeonHackGame(
                        onGameEnd = { score -> minigameViewModel.rewardNeonHack(score) },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable<Screen.KineticPath> {
                    VoidRunnerGame(
                        onGameEnd = { score -> minigameViewModel.rewardVoidRunner(score) },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
            
            JuiceOverlay(viewModel = juiceViewModel)
        }
    }
}
