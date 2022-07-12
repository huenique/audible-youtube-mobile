package com.huenique.audibleyoutube.ui.component

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
fun NavBar() {
  Surface(
      modifier = Modifier.fillMaxWidth().height(56.dp),
      elevation = AppBarDefaults.BottomAppBarElevation,
      color = MaterialTheme.colors.primary) {
    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
      NavIcon(iconName = "Home", iconImage = Icons.Filled.Home)
      NavIcon(iconName = "Search", iconImage = Icons.Filled.Search)
      NavIcon(iconName = "Library", iconImage = R.drawable.ic_library_music)
    }
  }
}

@Composable
fun NavIcon(iconName: String, iconImage: Any) {
  IconButton(onClick = { println("Search clicked") }) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
      when (iconImage) {
        is Int -> {
          Icon(
              painter = painterResource(id = iconImage),
              contentDescription = iconName,
              tint = Color.White)
        }
        is ImageVector -> {
          Icon(imageVector = iconImage, contentDescription = iconName, tint = Color.White)
        }
        else -> {}
      }
      Text(text = iconName, fontSize = 10.sp)
    }
  }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NavBarPreview() {
  NavBar()
}
