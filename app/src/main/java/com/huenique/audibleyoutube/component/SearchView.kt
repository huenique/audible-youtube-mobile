package com.huenique.audibleyoutube.component

import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.huenique.audibleyoutube.repository.HttpResponseRepository
import com.huenique.audibleyoutube.state.ActionRepositoryState
import com.huenique.audibleyoutube.state.HttpResponseRepositoryState
import com.huenique.audibleyoutube.state.PlaylistState
import com.huenique.audibleyoutube.ui.theme.AudibleYoutubeTheme
import org.json.JSONException
import org.json.JSONObject
import java.io.File

@Composable
fun SearchView(
    actionRepoState: ActionRepositoryState,
    moreActionState: SnapshotStateMap<String, String>,
    searchResultRepoState: HttpResponseRepositoryState,
    playlistState: PlaylistState,
    successResponseState: Boolean,
    searchResultRepo: HttpResponseRepository,
    isLoading: Boolean,
    onContentLoad: (Boolean) -> Unit,
    onClickMoreAction: () -> Unit,
    onAddToPlaylist: (String, File, File) -> Unit,
    onCreatePlaylist: (File, String, MutableState<String>) -> Unit,
    onCloseDialogue: () -> Unit,
    onPlaylistShow: () -> Unit,
    onDownloadVideo: (String, File) -> Unit
) {
  if (isLoading) PreLoader()

  when (searchResultRepoState) {
    HttpResponseRepositoryState.CHANGED -> {
      onContentLoad(false)
      VariableContent(searchResultRepo, moreActionState, onClickMoreAction)
      MainDialogue(
          actionRepoState = actionRepoState,
          moreActionState = moreActionState,
          playlistState = playlistState,
          successResponseState = successResponseState,
          onAddToPlaylist = onAddToPlaylist,
          onCreatePlaylist = onCreatePlaylist,
          onCloseDialogue = onCloseDialogue,
          onPlaylistShow = onPlaylistShow,
          onDownloadVideo = onDownloadVideo)
    }
    HttpResponseRepositoryState.DISPLAYED -> {
      if (!isLoading) DefaultContent()
    }
    else -> {}
  }
}

@Composable
fun DefaultContent() {
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
fun VariableContent(
    searchResultRepo: HttpResponseRepository,
    moreActionState: SnapshotStateMap<String, String>,
    onClickMoreAction: () -> Unit
) {
  Column(
      modifier = Modifier.verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally) {
    val result = searchResultRepo.getContent()
    val json =
        try {
          JSONObject(result)
        } catch (err: JSONException) {
          JSONObject()
        }

    if (json.has("playlist")) {
      ResultContent(
          json,
          moreActionState,
          onClickMoreAction,
      )
    } else {
      ErrorContent(result)
    }
  }
}

@Composable
fun ErrorContent(message: String) {
  Column(horizontalAlignment = Alignment.Start) {
    Text(text = "Oops! Please try again.", fontWeight = FontWeight.Bold)
    Text(
        text = if (message == "{}") "\nReason: Service Unavailable" else message,
        color = MaterialTheme.colors.error)
    Divider(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))
    Text(text = "Other things you can do:", textDecoration = TextDecoration.Underline)
    Text(
        text =
            "- Check your internet connection and try again.\n" +
                "- Try restarting the app.\n" +
                "- Try again later.")
  }
}

@Composable
fun ResultContent(
    json: JSONObject,
    moreActionState: SnapshotStateMap<String, String>,
    onClickMoreAction: () -> Unit
) {
  // For the response structure, see:
  // https://audible-youtube.herokuapp.com/docs#/videos/Search_search_get
  val playlist = json.getJSONArray("playlist")
  val results = playlist.getJSONObject(0).getJSONArray("result")

  // TODO: Remove this temp fix later.
  // https://github.com/huenique/audible-youtube/issues/8
  if (results.length() == 0) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Box(modifier = Modifier.height(56.dp)) {}
      Text(text = "No matching result!")
    }
  } else {
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
            AsyncImage(
                model = thumbnail,
                contentDescription = "Search result thumbnail",
                contentScale = ContentScale.Fit)
          }

          Column(modifier = Modifier.padding(start = 6.dp).weight(1f).fillMaxHeight()) {
            Text(
                text = videoTitle,
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
                moreActionState["thumbnail"] = thumbnail
                onClickMoreAction()
              }) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                modifier = Modifier.size(24.dp),
                contentDescription = "More Action")
          }
        }
      }
    }
  }

  Box(modifier = Modifier.height(120.dp)) {}
}

@Composable
fun MainDialogue(
    actionRepoState: ActionRepositoryState,
    moreActionState: SnapshotStateMap<String, String>,
    playlistState: PlaylistState,
    successResponseState: Boolean,
    onAddToPlaylist: (String, File, File) -> Unit,
    onCreatePlaylist: (File, String, MutableState<String>) -> Unit,
    onCloseDialogue: () -> Unit,
    onPlaylistShow: () -> Unit,
    onDownloadVideo: (String, File) -> Unit
) {
  when (actionRepoState) {
    ActionRepositoryState.OPENED ->
        ResultDialogue(
            moreActionState = moreActionState,
            playlistState = playlistState,
            successResponseState = successResponseState,
            onAddToPlaylist = onAddToPlaylist,
            onCreatePlaylist = onCreatePlaylist,
            onCloseDialogue = onCloseDialogue,
            onPlaylistShow = onPlaylistShow,
            onDownloadVideo = onDownloadVideo)
    ActionRepositoryState.CLOSED -> {}
  }
}

@Composable
fun ResultDialogue(
    moreActionState: SnapshotStateMap<String, String>,
    playlistState: PlaylistState,
    successResponseState: Boolean,
    onAddToPlaylist: (String, File, File) -> Unit,
    onCreatePlaylist: (File, String, MutableState<String>) -> Unit,
    onCloseDialogue: () -> Unit,
    onPlaylistShow: () -> Unit,
    onDownloadVideo: (String, File) -> Unit
) {
  val context = LocalContext.current
  val fileDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
  val fileName = "${moreActionState["videoTitle"].toString().replace("/", "")}.mp3"
  val file = File(fileDir, fileName)

  val createPlaylistDxState = remember { mutableStateOf(value = false) }
  val playlistCreationState = remember { mutableStateOf(value = true) }
  val playlistCreation by playlistCreationState

  PlaylistSelection(
      playlistState = playlistState,
      playlistCreation = playlistCreation,
      onPlaylistCreation = { playlistCreationState.value = it },
      onCreatePlaylist = { createPlaylistDxState.value = it },
      onSelectPlaylist = { playlist: File, playlistName: String ->
        onAddToPlaylist(moreActionState["videoLink"].toString(), file, playlist)
        if (successResponseState) {
          Toast.makeText(context, "1 song added to $playlistName", Toast.LENGTH_SHORT).show()
        }
      },
      onDeletePlaylist = { playlist: File -> playlist.delete() })
  CreatePlaylistDialogue(
      onCreatePlaylist = onCreatePlaylist,
      onPlaylistCreation = { playlistCreationState.value = it },
      onCreateDxClose = { createPlaylistDxState.value = it },
      createPlaylistState = createPlaylistDxState.value)

  when (playlistState) {
    PlaylistState.PENDING -> {
      Box(
          modifier =
              Modifier.fillMaxSize()
                  .background(MaterialTheme.colors.background.copy(alpha = 0.6f))
                  .clickable { onCloseDialogue() },
          contentAlignment = Alignment.Center) {
        Box(
            modifier =
                Modifier.background(Color.DarkGray)
                    .width(LocalConfiguration.current.screenWidthDp.dp / 2)) {
          Column(modifier = Modifier.padding(start = 14.dp)) {
            MoreActionOption(text = "Add to Playlist", onClick = onPlaylistShow)
            MoreActionOption(
                text = "Download",
                onClick = { onDownloadVideo(moreActionState["videoLink"].toString(), file) })
          }
        }
      }
    }
    else -> {}
  }
}

@Composable
fun MoreActionOption(text: String, onClick: () -> Unit) {
  ClickableText(
      text = AnnotatedString(text),
      modifier = Modifier.padding(top = 10.dp, bottom = 10.dp).fillMaxWidth(),
      style = TextStyle(color = Color.White),
      onClick = { onClick() })
}

@Preview
@Composable
fun PreLoaderPreview() {
  AudibleYoutubeTheme { PreLoader() }
}

@Preview
@Composable
fun DefaultAppContentPreview() {
  AudibleYoutubeTheme { DefaultContent() }
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
  AudibleYoutubeTheme { ResultContent(JSONObject(json), moreActionState) {} }
}
