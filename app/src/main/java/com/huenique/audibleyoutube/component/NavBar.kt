package com.huenique.audibleyoutube.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huenique.audibleyoutube.R

@Composable
fun NavBar(onHomeClick: () -> Unit, onSearchClick: () -> Unit, onLibraryClick: () -> Unit) {
  val isSysInDark = isSystemInDarkTheme()
  val navIconTint = if (isSystemInDarkTheme()) MaterialTheme.colors.onBackground else Color.White

  Surface(
      modifier = Modifier.fillMaxWidth().height(56.dp),
      elevation = AppBarDefaults.BottomAppBarElevation,
      color = if (isSysInDark) MaterialTheme.colors.background else MaterialTheme.colors.primary) {
    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
      NavIcon(
          iconName = "Home",
          tint = navIconTint,
          iconImage = Icons.Filled.Home,
          onClick = onHomeClick)
      NavIcon(
          iconName = "Search",
          tint = navIconTint,
          iconImage = Icons.Filled.Search,
          onClick = onSearchClick)
      NavIcon(
          iconName = "Library",
          tint = navIconTint,
          iconImage = R.drawable.ic_library_music,
          onClick = onLibraryClick)
    }
  }
}

@Composable
fun NavIcon(iconName: String, tint: Color, iconImage: Any, onClick: () -> Unit) {
  IconButton(onClick = onClick) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
      when (iconImage) {
        is Int -> {
          Icon(
              painter = painterResource(id = iconImage), contentDescription = iconName, tint = tint)
        }
        is ImageVector -> {
          Icon(imageVector = iconImage, contentDescription = iconName, tint = tint)
        }
        else -> {}
      }
      Text(text = iconName, color = tint, fontSize = 10.sp)
    }
  }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NavBarPreview() {
  NavBar({}, {}, {})
}
