package com.huenique.audibleyoutube.component

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huenique.audibleyoutube.R
import com.huenique.audibleyoutube.state.PlayButtonState

@Composable
fun MaximizedPlayer(
    playButtonState: PlayButtonState,
    currentSongPlaying: String,
    onPlayClick: () -> Unit,
    onForwardClick: () -> Unit,
    onBackClick: () -> Unit,
    onArrowDownClick: () -> Unit
) {
  // Emulate marquee text effect
  val scrollState = rememberScrollState()
  var shouldAnimate by remember { mutableStateOf(true) }
  LaunchedEffect(key1 = shouldAnimate) {
    scrollState.animateScrollTo(
        scrollState.maxValue,
        animationSpec = tween(1000, 200, easing = CubicBezierEasing(0f, 0f, 0f, 0f)))
    scrollState.scrollTo(0)
    shouldAnimate = !shouldAnimate
  }

  // Music progress to track playing audio position
  var currentValue by remember { mutableStateOf(0L) }
  var isPlaying by remember { mutableStateOf(false) }

  Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally) {
    IconButton(onClick = { onArrowDownClick() }, modifier = Modifier.align(Alignment.Start)) {
      Icon(
          painter = painterResource(id = R.drawable.ic_baseline_keyboard_arrow_down),
          contentDescription = "Minimize player")
    }

    Spacer(modifier = Modifier.height(64.dp))

    Column(modifier = Modifier.padding(start = 14.dp, end = 14.dp)) {
      Image(
          painter = painterResource(id = R.drawable.placeholder_image),
          contentDescription = "Song cover",
          modifier = Modifier.align(alignment = Alignment.CenterHorizontally))

      MarqueeText(
          text = "Veeeeerrrryyy Loooonnngg - Title of the song",
          fontSize = 24.sp,
          modifier = Modifier.align(Alignment.Start))
    }

    Spacer(modifier = Modifier.weight(1f))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
      IconButton(
          onClick = { onBackClick() },
          modifier = Modifier.size(56.dp).align(Alignment.CenterVertically)) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_skip_previous),
            contentDescription = null,
            modifier = Modifier.fillMaxSize())
      }

      Spacer(modifier = Modifier.width(26.dp))

      IconButton(onClick = { onPlayClick() }, modifier = Modifier.size(68.dp)) {
        val painterId =
            when (playButtonState) {
              PlayButtonState.PLAYING -> R.drawable.ic_baseline_pause_circle_filled
              PlayButtonState.PAUSED -> R.drawable.ic_baseline_play_circle_filled
            }
        Icon(
            painter = painterResource(id = painterId),
            contentDescription = "Maximized player's play or pause button",
            modifier = Modifier.fillMaxSize())
      }

      Spacer(modifier = Modifier.width(26.dp))

      IconButton(
          onClick = { onForwardClick() },
          modifier = Modifier.size(56.dp).align(Alignment.CenterVertically)) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_skip_next),
            contentDescription = null,
            modifier = Modifier.fillMaxSize())
      }
    }

    // NOTE: The size should be equal to the navigation bar for consistency.
    Spacer(modifier = Modifier.height(56.dp))
  }
}

@Composable
fun MinimizedPlayer(
    playButtonState: PlayButtonState,
    currentSongPlaying: String,
    onPlayerClick: () -> Unit = {},
    onPlayClick: () -> Unit = {},
    onForwardClick: () -> Unit
) {
  Surface(
      modifier = Modifier.fillMaxWidth().height(56.dp).clickable(onClick = onPlayerClick),
      elevation = AppBarDefaults.BottomAppBarElevation,
      color = MaterialTheme.colors.primaryVariant) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Image(
          painter = painterResource(id = R.drawable.ic_baseline_image),
          contentDescription = "Song cover",
          modifier = Modifier.height(56.dp).width(56.dp),
          alignment = Alignment.CenterStart,
          colorFilter = ColorFilter.tint(color = MaterialTheme.colors.onPrimary))
      Text(currentSongPlaying)
    }

    Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
      val painterId =
          when (playButtonState) {
            PlayButtonState.PLAYING -> R.drawable.ic_pause
            PlayButtonState.PAUSED -> R.drawable.ic_play
          }

      IconButton(onClick = { onPlayClick() }) {
        Icon(
            painter = painterResource(id = painterId),
            contentDescription = "Minimized player's play button",
            tint = Color.White)
      }
      IconButton(
          onClick = {
            // prepare new song
            onForwardClick()
          }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_skip_next),
            contentDescription = "Minimized player's forward button",
            tint = Color.White)
      }
    }
  }
}

@Preview
@Composable
fun MaximizedPlayerPreview() {
  MaximizedPlayer(
      playButtonState = PlayButtonState.PLAYING,
      currentSongPlaying = "Veeeeerrrryyy Loooonnngg - Title of the song",
      onPlayClick = {},
      onBackClick = {},
      onForwardClick = {},
      onArrowDownClick = {})
}

@Preview
@Composable
fun MinimizedPlayerPreview() {
  MinimizedPlayer(
      PlayButtonState.PLAYING,
      currentSongPlaying = "",
      onForwardClick = {},
      onPlayClick = {},
      onPlayerClick = {})
}
