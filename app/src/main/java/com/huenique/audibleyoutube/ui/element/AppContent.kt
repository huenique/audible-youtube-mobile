package com.huenique.audibleyoutube.ui.element


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.huenique.audibleyoutube.repository.Repository
import com.huenique.audibleyoutube.state.RepositoryState


@Composable
fun MainAppContent(repository: Repository, repositoryState: RepositoryState) {
    when (repositoryState) {
        RepositoryState.CHANGED -> { ResultAppContent(repository) }
        RepositoryState.DISPLAYED -> { DefaultAppContent() }
    }
}


@Composable
fun DefaultAppContent() {
    Text("Hello, World!")
}


@Composable
fun ResultAppContent(repository: Repository) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row() {
            Text(text = repository.findAll().toString())
        }
    }
}
