package org.strawberryfoundations.wear.calculator.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.HorizontalPageIndicator
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import org.strawberryfoundations.wear.calculator.presentation.core.CurrencyIconOption
import org.strawberryfoundations.wear.calculator.presentation.core.loadCurrencyIcon
import org.strawberryfoundations.wear.calculator.presentation.core.saveCurrencyIcon
import org.strawberryfoundations.wear.calculator.presentation.theme.WearCalculatorTheme
import org.strawberryfoundations.wear.calculator.presentation.views.BillView
import org.strawberryfoundations.wear.calculator.presentation.views.CalculatorMainView
import org.strawberryfoundations.wear.calculator.presentation.views.ChangelogView
import org.strawberryfoundations.wear.calculator.presentation.views.SettingsView


// Class: MainActivity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            MainView()
        }
    }
}

@Composable
fun MainView() {
    WearCalculatorTheme {
        AppScaffold(
            timeText = { TimeText() }
        ) {
            val context = LocalContext.current
            val navController = rememberSwipeDismissableNavController()

            val initialPage = 0
            val pagerState = rememberPagerState(initialPage = initialPage) { 3 }

            val displayTextState = remember { mutableStateOf("") }
            val currentExpressionState = remember { mutableStateOf("") }
            val billTipPercentState = remember { mutableIntStateOf(5) }
            val billPeopleCountState = remember { mutableIntStateOf(1) }
            var selectedCurrencyIcon by remember { mutableStateOf(CurrencyIconOption.default) }
            var hasLoadedCurrencyIcon by remember { mutableStateOf(false) }

            LaunchedEffect(context) {
                selectedCurrencyIcon = loadCurrencyIcon(context)
                hasLoadedCurrencyIcon = true
            }

            LaunchedEffect(selectedCurrencyIcon, hasLoadedCurrencyIcon, context) {
                if (hasLoadedCurrencyIcon) {
                    saveCurrencyIcon(context, selectedCurrencyIcon)
                }
            }
            
            SwipeDismissableNavHost(
                navController = navController,
                startDestination = "main",
            ) {
                composable(route = "main") {
                    ScreenScaffold { _ ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background),
                            contentAlignment = Alignment.Center,
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                key = { it },
                                modifier = Modifier.fillMaxSize(),
                            ) { page ->
                                when (page) {
                                    0 -> CalculatorMainView(
                                        displayTextState = displayTextState,
                                        currentExpressionState = currentExpressionState,
                                    )
                                    1 -> BillView(
                                        displayText = displayTextState.value,
                                        currentExpression = currentExpressionState.value,
                                        isPageActive = pagerState.currentPage == 1,
                                        currencyIcon = selectedCurrencyIcon,
                                        tipPercentState = billTipPercentState,
                                        peopleCountState = billPeopleCountState,
                                    )
                                    2 -> SettingsView(
                                        selectedCurrencyIcon = selectedCurrencyIcon,
                                        onCurrencyIconSelected = { selectedCurrencyIcon = it },
                                        onNavigateToChangelog = {
                                            navController.navigate("changelog")
                                        },
                                    )
                                }
                            }

                            HorizontalPageIndicator(
                                pagerState = pagerState,
                                modifier = Modifier.align(Alignment.BottomCenter),
                                selectedColor = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }

                composable(route = "changelog") {
                    ChangelogView()
                }
            }
        }
    }
}

