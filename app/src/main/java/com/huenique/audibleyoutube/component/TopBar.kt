package com.huenique.audibleyoutube.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.huenique.audibleyoutube.state.SearchWidgetState

@Composable
fun TopBar(
    searchWidgetState: SearchWidgetState,
    searchTextState: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
    onSearchTriggered: () -> Unit,
    title: String
) {
  when (searchWidgetState) {
    SearchWidgetState.CLOSED -> {
      DefaultTopAppBar(title = title, onSearchClicked = onSearchTriggered)
    }
    SearchWidgetState.OPENED -> {
      SearchTopAppBar(
          text = searchTextState,
          onTextChange = onTextChange,
          onCloseClicked = onCloseClicked,
          onSearchClicked = onSearchClicked)
    }
  }
}

@Composable
fun DefaultTopAppBar(title: String, onSearchClicked: () -> Unit) {
  TopAppBar(
      title = { Text(text = title) },
      actions = {
        IconButton(onClick = { onSearchClicked() }) {
          Icon(
              imageVector = Icons.Filled.Search,
              contentDescription = "Search Icon",
              tint = Color.White)
        }
      })
}

@Composable
fun SearchTopAppBar(
    text: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
) {
  val focusRequester = remember { FocusRequester() }

  Surface(
      modifier = Modifier.fillMaxWidth().height(56.dp),
      elevation = AppBarDefaults.TopAppBarElevation,
      color =
          if (isSystemInDarkTheme()) MaterialTheme.colors.background
          else MaterialTheme.colors.primary) {
    TextField(
        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
        value = text,
        onValueChange = { onTextChange(it) },
        placeholder = {
          Text(
              modifier = Modifier.alpha(ContentAlpha.medium),
              text = "Search Video",
              color = Color.White)
        },
        textStyle = TextStyle(fontSize = MaterialTheme.typography.subtitle1.fontSize),
        singleLine = true,
        leadingIcon = {
          IconButton(modifier = Modifier.alpha(ContentAlpha.medium), onClick = {}) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = Color.White)
          }
        },
        trailingIcon = {
          IconButton(
              onClick = {
                if (text.isNotEmpty()) {
                  onTextChange("")
                } else {
                  onCloseClicked()
                }
              }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Icon",
                tint = Color.White)
          }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearchClicked(text) }),
        colors =
            TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                cursorColor = Color.White.copy(alpha = ContentAlpha.medium)))
  }

  LaunchedEffect(Unit) { focusRequester.requestFocus() }
}

@Composable
@Preview
fun DefaultAppBarPreview() {
  DefaultTopAppBar("preview", onSearchClicked = {})
}

@Composable
@Preview
fun SearchAppBarPreview() {
  SearchTopAppBar(
      text = "Some random text", onTextChange = {}, onCloseClicked = {}, onSearchClicked = {})
}
