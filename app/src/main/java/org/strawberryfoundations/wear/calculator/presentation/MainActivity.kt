package org.strawberryfoundations.wear.calculator.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.HorizontalPageIndicator
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.TimeText
import org.strawberryfoundations.wear.calculator.presentation.theme.WearCalculatorTheme
import org.strawberryfoundations.wear.calculator.presentation.views.CalculatorMainView
import org.strawberryfoundations.wear.calculator.presentation.views.BillView


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
            val initialPage = 1
            val pagerState = rememberPagerState(initialPage = initialPage) { 2 }
            val displayTextState = remember { mutableStateOf("") }
            val currentExpressionState = remember { mutableStateOf("") }
            
            ScreenScaffold { _ ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    HorizontalPager(
                        state = pagerState,
                        key = { it },
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        when (page) {
                            0 -> CalculatorMainView(
                                displayTextState = displayTextState,
                                currentExpressionState = currentExpressionState
                            )
                            1 -> BillView(
                                displayText = displayTextState.value,
                                currentExpression = currentExpressionState.value,
                                isPageActive = pagerState.currentPage == 1
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
    }
}

