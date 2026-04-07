package org.strawberryfoundations.wear.calculator.presentation.views

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.pager.VerticalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.foundation.pager.PagerDefaults
import androidx.wear.compose.foundation.requestFocusOnHierarchyActive
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import org.strawberryfoundations.wear.calculator.R
import org.strawberryfoundations.wear.calculator.presentation.composable.PagerChangePopup
import org.strawberryfoundations.wear.calculator.presentation.composable.PagerPageMeta
import kotlin.math.abs
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun ExperimentalTestUiView() {
    val pagerState = rememberPagerState(pageCount = { 2 })

    val statePage1 = rememberScalingLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            flingBehavior = PagerDefaults.snapFlingBehavior(state = pagerState),
            rotaryScrollableBehavior = null,
            userScrollEnabled = true,
        ) { pageIndex ->
            if (pageIndex == 0) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Seite 0\nSwipe hoch für History",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            } else {
                TestPageContent(listState = statePage1, pageIndex = pageIndex)
            }
        }

        UnifiedPagerScrollIndicator(
            pagerCurrentPage = pagerState.currentPage,
            scrollState = statePage1,
            modifier = Modifier.align(Alignment.CenterEnd),
        )

        PagerChangePopup(
            currentPage = pagerState.currentPage,
            pageMetaByIndex = listOf(
                PagerPageMeta(Icons.Rounded.Payments, stringResource(R.string.pager_page_number, 1)),
                PagerPageMeta(Icons.Rounded.History, stringResource(R.string.pager_page_number, 2)),
            ),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp),
        )
    }
}

@Composable
private fun TestPageContent(listState: ScalingLazyListState, pageIndex: Int) {
    val focusRequester = remember { FocusRequester() }

    ScalingLazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .requestFocusOnHierarchyActive()
            .rotaryScrollable(
                behavior = RotaryScrollableDefaults.behavior(listState),
                focusRequester = focusRequester,
            ),
        autoCentering = null,
    ) {
        item { Text("Seite $pageIndex (scrollbar + morph test)") }
        items(24) { i ->
            Text("Item $i")
        }
    }
}

@Composable
private fun UnifiedPagerScrollIndicator(
    pagerCurrentPage: Int,
    scrollState: ScalingLazyListState,
    modifier: Modifier = Modifier,
) {
    val activeColor = MaterialTheme.colorScheme.onSurface
    val inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
    val railColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)

    val layoutInfo by remember(scrollState) { derivedStateOf { scrollState.layoutInfo } }
    val visibleCount = layoutInfo.visibleItemsInfo.size.coerceAtLeast(1)
    val totalCount = layoutInfo.totalItemsCount
    val maxFirstIndex = (totalCount - visibleCount).coerceAtLeast(0)
    val firstVisibleIndex = layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0

    val rawScrollFraction = if (maxFirstIndex == 0) {
        0f
    } else {
        (firstVisibleIndex.toFloat() / maxFirstIndex.toFloat()).coerceIn(0f, 1f)
    }

    val canScrollPage1 = totalCount > visibleCount
    val shouldMorph = pagerCurrentPage == 1 && canScrollPage1

    val morphProgress by animateFloatAsState(
        targetValue = if (shouldMorph) 1f else 0f,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "IndicatorMorphProgress",
    )

    val animatedScrollFraction by animateFloatAsState(
        targetValue = rawScrollFraction,
        animationSpec = tween(durationMillis = 140),
        label = "IndicatorScrollFraction",
    )

    val lowerBaseY = (-6).dp
    val lowerHeight = lerp(7.dp, 30.dp, morphProgress)
    val lowerWidth = lerp(7.dp, 5.dp, morphProgress)
    val thumbHeight = lerp(7.dp, 12.dp, morphProgress)
    val thumbWidth = lerp(7.dp, 6.dp, morphProgress)
    val thumbTravel = lerp(0.dp, 18.dp, morphProgress)

    val midCurve = sin(animatedScrollFraction * PI).toFloat().coerceIn(0f, 1f)
    val railCurveX = (-1).dp * morphProgress
    val thumbCurveX = (-2).dp * (midCurve * morphProgress)

    Box(
        modifier = modifier
            .size(width = 24.dp, height = 78.dp)
            .offset(x = (-2).dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 4.dp)
                .clip(CircleShape)
                .size(7.dp)
                .background(if (pagerCurrentPage == 0) activeColor else inactiveColor),
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(x = railCurveX, y = lowerBaseY)
                .clip(RoundedCornerShape(50))
                .size(width = lowerWidth, height = lowerHeight)
                .background(
                    if (morphProgress > 0.02f) railColor else if (pagerCurrentPage == 1) activeColor else inactiveColor,
                ),
        )

        if (morphProgress > 0.01f) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(
                        x = thumbCurveX,
                        y = lowerBaseY + (thumbTravel * animatedScrollFraction),
                    )
                    .clip(RoundedCornerShape(50))
                    .size(width = thumbWidth, height = thumbHeight)
                    .background(activeColor),
            )
        }
    }
}
