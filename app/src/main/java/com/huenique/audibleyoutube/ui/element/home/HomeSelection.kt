package com.huenique.audibleyoutube.ui.element

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.huenique.audibleyoutube.R
import com.huenique.audibleyoutube.ui.theme.AudibleYoutubeTheme

@Composable
fun HomeSelection() {
  Column {
    RecentlyPlayed()
    RecentlyAdded()
  }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RecentlyPlayed() {
  Text(
      text = "Recently Played",
      modifier = Modifier.padding(start = 14.dp, top = 24.dp),
      color = if (isSystemInDarkTheme()) Color.White else Color.Black)
  HorizontalPager(count = 5) {
    Image(painter = painterResource(id = R.drawable.placeholder_image), contentDescription = null)
  }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RecentlyAdded() {
  Text(
      text = "Recently Added",
      modifier = Modifier.padding(start = 14.dp, top = 24.dp),
      color = if (isSystemInDarkTheme()) Color.White else Color.Black)
  HorizontalPager(count = 5) {
    Image(painter = painterResource(id = R.drawable.placeholder_image), contentDescription = null)
  }
}

@Preview
@Composable
fun MusicLibraryPreview() {
  AudibleYoutubeTheme { HomeSelection() }
}
