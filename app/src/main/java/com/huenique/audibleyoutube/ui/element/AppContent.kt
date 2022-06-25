package com.huenique.audibleyoutube.ui.element

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.huenique.audibleyoutube.model.MainViewModel
import com.huenique.audibleyoutube.repository.Repository
import com.huenique.audibleyoutube.state.RepositoryState
import com.huenique.audibleyoutube.ui.theme.AudibleYoutubeTheme
import org.json.JSONObject

@Composable
fun MainAppContent(
    repository: Repository<String>,
    repositoryState: RepositoryState,
    mainViewModel: MainViewModel
) {
    if (mainViewModel.isLoading.value) PreLoader()

    when (repositoryState) {
        RepositoryState.CHANGED -> {
            mainViewModel.updatePreloadState(newValue = false)
            VariableAppContent(repository)
        }
        RepositoryState.DISPLAYED -> {
            if (!mainViewModel.isLoading.value) DefaultAppContent()
        }
    }
}

@Composable
fun DefaultAppContent() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) { Text(text = "It feels empty here...") }
    }
}

@Composable
fun PreLoader() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Searching for videos...")
            CircularProgressIndicator(modifier = Modifier.padding(top = 10.dp))
        }
    }
}

@Composable
fun VariableAppContent(repository: Repository<String>) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val json = JSONObject(repository.getAll())
        when (true) {
            json.has("playlist") -> ResultAppContent(json)
            else -> {
                Text(
                    "Something went wrong!\n" +
                            "- Check your internet connection and try again.\n" +
                            "- Try restarting the app."
                )
            }
        }
    }
}

@Composable
fun ResultAppContent(json: JSONObject) {
    // For the response structure, see
    // https://audible-youtube.herokuapp.com/docs#/videos/Search_search_get
    val playlist = json.getJSONArray("playlist")
    val results = playlist.getJSONObject(0).getJSONArray("result")

    for (resultIndex in 0 until results.length()) {
        val result = results.getJSONObject(resultIndex)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Row {
                Box(
                    modifier = Modifier.width(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model =
                        result
                            .getJSONArray("thumbnails")
                            .getJSONObject(0)
                            .getString("url"),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }

                Column(modifier = Modifier.padding(start = 6.dp)) {
                    Text(
                        result.getString("title"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                    Text(
                        result.getJSONObject("channel").getString("name"),
                        fontSize = 12.sp
                    )
                    Text(
                        result.getJSONObject("viewCount").getString("short"),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreLoaderPreview() {
    AudibleYoutubeTheme { PreLoader() }
}

@Preview(showBackground = true)
@Composable
fun DefaultAppContentPreview() {
    AudibleYoutubeTheme { DefaultAppContent() }
}
