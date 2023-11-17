package com.learn.worlds.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.learn.worlds.NavigationDrawerMediator
import com.learn.worlds.R
import com.learn.worlds.utils.stringRes

@Preview
@Composable
private fun ActualTopBarPrewiew() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxWidth()) {
            ActualTopBar(
                title = R.string.learn,
                actions = listOf(
                    ActionTopBar(
                        imageVector = Icons.Default.FilterList,
                        contentDesc = R.string.desc_action_filter_list,
                        action = {})
                )
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActualTopBar(
    iconLeftAppBar: IconLeftAppBar? = null,
    @StringRes title: Int?,
    actions: List<ActionTopBar>
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            if (iconLeftAppBar is IconLeftAppBar.NavMenuIcon) {
                Icon(
                    modifier = Modifier.clickable { NavigationDrawerMediator.open() },
                    imageVector = iconLeftAppBar.imageVector, contentDescription = null
                )
            }
            if (iconLeftAppBar is IconLeftAppBar.NavBackIcon) {
                Icon(
                    modifier = Modifier.clickable { iconLeftAppBar.action.invoke() },
                    imageVector = iconLeftAppBar.imageVector, contentDescription = null
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            title?.let {
                Text(
                    text = stringResource(title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        actions = {
            actions.forEach {
                IconButton(onClick = {
                    it.action.invoke()
                }) {
                    Column {
                        it.dropDownContent?.invoke()
                    }
                    Icon(
                        imageVector = it.imageVector,
                        contentDescription = stringRes(it.contentDesc)
                    )
                }
            }


        }
    )
}