package com.openclassrooms.rebonnte.ui.medicine
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.swipeable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.rebonnte.R
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDeleteItem(
    modifier: Modifier = Modifier,
    onDelete: suspend () -> Unit,
    deleteIcon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Supprimer",
            tint = Color.White,
            modifier = Modifier.size(40.dp)
        )
    },
    content: @Composable (Modifier) -> Unit,
    resetState: Boolean = false
) {
    val isUserConnected = FirebaseAuth.getInstance().currentUser!=null
    val density = LocalDensity.current
    val offsetSize = 72.dp
    val offsetSizePx = with(density) { offsetSize.toPx() }
    val context = LocalContext.current

    val swipeableState: SwipeableState<Int> = rememberSwipeableState(initialValue = 0)
    val anchors = mapOf(0f to 0, -offsetSizePx to 1)

    LaunchedEffect(resetState) {
        if (resetState) {
            swipeableState.snapTo(0)
        }
    }

    val swipeOffsetPx = swipeableState.offset.value
    val revealedWidthPx = (-swipeOffsetPx).coerceAtLeast(0f).coerceAtMost(offsetSizePx)
    val revealedWidthDp = with(density) { revealedWidthPx.toDp() }

    val coroutineScope = rememberCoroutineScope()
    val errorString = stringResource(R.string.you_must_be_connected)
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(revealedWidthDp)
                .background(Color.Red)
                .align(Alignment.CenterEnd)
                .clickable {
                    coroutineScope.launch {
                        if(isUserConnected) {
                            onDelete()
                        } else {
                            Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show()
                        }
                        swipeableState.snapTo(0)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (revealedWidthPx > 0f) {
                deleteIcon()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .swipeable(
                    state = swipeableState,
                    anchors = anchors,
                    thresholds = { _, _ -> FractionalThreshold(0.3f) },
                    orientation = Orientation.Horizontal
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            content(Modifier.fillMaxWidth())
        }
    }
}