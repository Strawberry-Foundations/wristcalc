package org.strawberryfoundations.wear.calculator.presentation.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import kotlinx.coroutines.delay

data class PagerPageMeta(
    val icon: ImageVector,
    val label: String,
)

@Composable
fun PagerChangePopup(
    currentPage: Int,
    pageMetaByIndex: List<PagerPageMeta>,
    modifier: Modifier = Modifier,
) {
    var visible by remember { mutableStateOf(false) }
    var lastPage by remember { mutableIntStateOf(currentPage) }
    var pageChangeToken by remember { mutableIntStateOf(0) }
    var currentMeta by remember { mutableStateOf(pageMetaByIndex.getOrNull(currentPage)) }

    LaunchedEffect(currentPage, pageMetaByIndex) {
        if (currentPage == lastPage) return@LaunchedEffect

        lastPage = currentPage
        currentMeta = pageMetaByIndex.getOrNull(currentPage)
        pageChangeToken++
    }

    LaunchedEffect(pageChangeToken) {
        if (currentMeta == null) {
            visible = false
            return@LaunchedEffect
        }

        visible = true
        delay(850)
        visible = false
    }

    AnimatedVisibility(
        visible = visible && currentMeta != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier,
    ) {
        val meta = currentMeta ?: return@AnimatedVisibility

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.94f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = meta.icon,
                    contentDescription = meta.label,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    text = meta.label,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}