package com.example.nearby

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.nearby.data.model.Market
import com.example.nearby.ui.screens.home.HomeScreen
import com.example.nearby.ui.screens.home.HomeViewModel
import com.example.nearby.ui.screens.market_details.MarketDetailsScreen
import com.example.nearby.ui.screens.market_details.MarketDetailsUiEvent
import com.example.nearby.ui.screens.market_details.MarketDetailsViewModel
import com.example.nearby.ui.screens.qrcode_scanner.QRCodeScannerScreen
import com.example.nearby.ui.screens.route.Home
import com.example.nearby.ui.screens.route.QRCodeScanner
import com.example.nearby.ui.screens.route.Splash
import com.example.nearby.ui.screens.route.Welcome
import com.example.nearby.ui.screens.splash.SplashScreen
import com.example.nearby.ui.screens.welcome.WelcomeScreen
import com.example.nearby.ui.theme.NearbyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NearbyTheme {
                val navController = rememberNavController()

                val homeViewModel by viewModels<HomeViewModel>()
                val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()

                val marketDetailsViewModel by viewModels<MarketDetailsViewModel>()
                val marketDetailsUiState by marketDetailsViewModel.uiState.collectAsStateWithLifecycle()

                NavHost(
                    navController = navController,
                    startDestination = Splash
                ) {
                    composable<Splash> {
                        SplashScreen(
                            onNavigateToWelcome = {
                                navController.navigate(Welcome)
                            }
                        )
                    }

                    composable<Welcome> {
                        WelcomeScreen(
                            onNavigateToHome = {
                                navController.navigate(Home)
                            }
                        )
                    }

                    composable<Home> {
                        HomeScreen(
                            uiState = homeUiState,
                            onEvent = homeViewModel::onEvent,
                            onNavigateToMarketDetails = { selectedMarket ->
                                navController.navigate(selectedMarket)
                            }
                        )
                    }

                    composable<Market> {
                        val selectedMarket = it.toRoute<Market>()

                        MarketDetailsScreen(
                            market = selectedMarket,
                            uiState = marketDetailsUiState,
                            onEvent = marketDetailsViewModel::onEvent,
                            onNavigateToQRCodeScanner = {
                                navController.navigate(QRCodeScanner)
                            },
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable<QRCodeScanner> {
                        QRCodeScannerScreen(
                            onCompletedScan = { qrCodeContent ->
                                if (qrCodeContent.isNotEmpty())
                                    marketDetailsViewModel.onEvent(
                                        MarketDetailsUiEvent.OnFetchCoupon(
                                            qrCodeContent = qrCodeContent
                                        )
                                    )
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
