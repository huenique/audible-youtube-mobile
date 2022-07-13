package com.huenique.audibleyoutube.screen

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Divider
import androidx.compose.material.Icon
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
import com.huenique.audibleyoutube.R

@Composable
fun LibraryScreen() {
  LibrarySelection()
}

@Composable
fun LibrarySelection() {
  Column(modifier = Modifier.padding(start = 18.dp, end = 18.dp)) {

    // TopBar / Content separator
    Box(modifier = Modifier.height(40.dp)) {}

    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(painter = painterResource(id = R.drawable.ic_library_music), contentDescription = null)

      Column {
        ClickableText(
            text = AnnotatedString("All Songs"),
            modifier = Modifier.padding(start = 14.dp, top = 10.dp, bottom = 10.dp),
            style =
                TextStyle(
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                    fontSize = 20.sp),
            onClick = {})
        Divider(
            Modifier.padding(start = 14.dp),
            color = Color.Gray.copy(alpha = 0.6f),
            thickness = 1.dp)
      }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(painter = painterResource(id = R.drawable.ic_playlist), contentDescription = null)

      Column {
        ClickableText(
            text = AnnotatedString("Playlists"),
            modifier = Modifier.padding(start = 14.dp, top = 10.dp, bottom = 10.dp),
            style =
                TextStyle(
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                    fontSize = 20.sp),
            onClick = {})
        Divider(
            Modifier.padding(start = 14.dp),
            color = Color.Gray.copy(alpha = 0.6f),
            thickness = 1.dp)
      }
    }
  }
}

@Preview
@Composable
fun LibrarySelectionPreview() {
  LibrarySelection()
}
