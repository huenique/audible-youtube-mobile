package com.huenique.audibleyoutube.ui.element

import android.content.res.Configuration
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.huenique.audibleyoutube.repository.SearchResultRepository
import com.huenique.audibleyoutube.state.ActionRepositoryState
import com.huenique.audibleyoutube.state.SearchRepositoryState
import com.huenique.audibleyoutube.ui.theme.AudibleYoutubeTheme
import java.io.File
import org.json.JSONObject

@Composable
fun MainAppContent(
    actionRepoState: ActionRepositoryState,
    moreActionState: SnapshotStateMap<String, String>,
    searchResultRepoState: SearchRepositoryState,
    searchResultRepo: SearchResultRepository,
    isLoading: Boolean,
    onContentLoad: (Boolean) -> Unit,
    onMoreActionClicked: () -> Unit,
    onAddToPlaylist: (String, File) -> Unit,
    onCloseDialogue: () -> Unit
) {
  if (isLoading) PreLoader()

  when (searchResultRepoState) {
    SearchRepositoryState.CHANGED -> {
      onContentLoad(false)
      VariableAppContent(searchResultRepo, moreActionState, onMoreActionClicked)
      MainDialogue(actionRepoState, moreActionState, onAddToPlaylist, onCloseDialogue)
    }
    SearchRepositoryState.DISPLAYED -> {
      if (!isLoading) DefaultAppContent()
    }
  }
}

@Composable
fun DefaultAppContent() {
  Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier.align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally) {
      Text(text = "It feels empty here...")
    }
  }
}

@Composable
fun PreLoader() {
  Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier.align(Alignment.TopCenter),
        horizontalAlignment = Alignment.CenterHorizontally) {
      CircularProgressIndicator(modifier = Modifier.padding(top = 10.dp))
    }
  }
}

@Composable
fun VariableAppContent(
    searchResultRepo: SearchResultRepository,
    moreActionState: SnapshotStateMap<String, String>,
    onMoreActionClicked: () -> Unit
) {

  Column(
      modifier = Modifier.verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally) {
    val json = JSONObject(searchResultRepo.getAll())
    when (true) {
      json.has("playlist") ->
          ResultAppContent(
              json,
              moreActionState,
              onMoreActionClicked,
          )
      else -> {
        Text(
            "Something went wrong!\n" +
                "- Check your internet connection and try again.\n" +
                "- Try restarting the app.")
      }
    }
  }
}

@Composable
fun ResultAppContent(
    json: JSONObject,
    moreActionState: SnapshotStateMap<String, String>,
    onMoreActionClicked: () -> Unit
) {

  // For the response structure, see
  // https://audible-youtube.herokuapp.com/docs#/videos/Search_search_get
  val playlist = json.getJSONArray("playlist")
  val results = playlist.getJSONObject(0).getJSONArray("result")

  for (resultIndex in 0 until results.length()) {
    val result = results.getJSONObject(resultIndex)

    val videoTitle = result.getString("title")
    val videoLink = result.getString("link")
    val viewCount = result.getJSONObject("viewCount").getString("short")
    val channelName = result.getJSONObject("channel").getString("name")
    val thumbnail = result.getJSONArray("thumbnails").getJSONObject(0).getString("url")

    Card(modifier = Modifier.fillMaxWidth().heightIn(0.dp, 100.dp).padding(top = 10.dp)) {
      Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.width(130.dp)) {
          AsyncImage(model = thumbnail, contentDescription = null, contentScale = ContentScale.Crop)
        }

        Column(modifier = Modifier.padding(start = 6.dp).width(230.dp).fillMaxHeight()) {
          Text(
              videoTitle,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              overflow = TextOverflow.Ellipsis,
              maxLines = 2)

          Text(channelName, fontSize = 12.sp)
          Text(viewCount, fontSize = 12.sp)
        }

        IconButton(
            onClick = {
              moreActionState["videoLink"] = videoLink
              moreActionState["videoTitle"] = videoTitle
              onMoreActionClicked()
            }) {
          Icon(
              imageVector = Icons.Rounded.MoreVert,
              modifier = Modifier.size(128.dp),
              contentDescription = "More Action")
        }
      }
    }
  }
}

@Composable
fun MainDialogue(
    actionRepoState: ActionRepositoryState,
    moreActionState: SnapshotStateMap<String, String>,
    onAddToPlaylist: (String, File) -> Unit,
    onCloseDialogue: () -> Unit,
) {
  when (actionRepoState) {
    ActionRepositoryState.OPENED ->
        ResultDialogue(moreActionState, onAddToPlaylist, onCloseDialogue)
    ActionRepositoryState.CLOSED -> println("Action Repo is CLOSED")
  }
}

@Composable
fun ResultDialogue(
    moreActionState: SnapshotStateMap<String, String>,
    onAddToPlaylist: (String, File) -> Unit,
    onCloseDialogue: () -> Unit,
) {
  val config = LocalConfiguration.current
  val context = LocalContext.current
  val fileName = "${moreActionState["videoTitle"].toString()}.mp3"

  Box(
      modifier =
          Modifier.fillMaxSize()
              .background(MaterialTheme.colors.background.copy(alpha = 0.6f))
              .clickable { onCloseDialogue() },
      contentAlignment = Alignment.Center) {
    Box(
        modifier = Modifier.background(Color.DarkGray).width(config.screenWidthDp.dp / 2),
        contentAlignment = Alignment.Center) {
      ClickableText(
          text = AnnotatedString("Add to Playlist"),
          modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
          style = TextStyle(color = Color.White),
          onClick = {
            onAddToPlaylist(
                moreActionState["videoLink"].toString(),
                File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName))
          })
    }
  }
}

@Preview
@Composable
fun PreLoaderPreview() {
  AudibleYoutubeTheme { PreLoader() }
}

@Preview
@Composable
fun DefaultAppContentPreview() {
  AudibleYoutubeTheme { DefaultAppContent() }
}

@Preview
@Composable
fun ResultAppContentPreview() {
  val json =
"""
{
    "playlist":[
        {
            "result":[
                {
                    "title":"Rick - Never Gonna Give You Up (Official Music Video)",
                    "viewCount":{
                        "text":"1,239,118,202 views",
                        "short":"1.2B views"
                    },
                    "thumbnails":[
                        {
                            "url":"https://i.picsum.photos/id/618/360/202.jpg?hmac=aFSMGUerVxIar_fMqW8ZEhFA7jlhCUZ4wB2CjgFIOLM",
                            "width":360,
                            "height":202
                        }
                    ],
                    "channel":{
                        "name":"Rick"
                    }
                }
            ]
        }
    ]
}
"""
  val moreActionState = remember { mutableStateMapOf<String, String>() }
  AudibleYoutubeTheme { ResultAppContent(JSONObject(json), moreActionState) {} }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ResultDialoguePreview() {
  val moreActionState = remember { mutableStateMapOf<String, String>() }
  AudibleYoutubeTheme { ResultDialogue(moreActionState, { a, b -> a + b }, {}) }
}
