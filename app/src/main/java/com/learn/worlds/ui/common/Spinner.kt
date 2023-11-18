package com.learn.worlds.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.learn.worlds.ui.preferences.PreferenceValue


@Composable
fun PreferenceSpinner(
    modifier: Modifier = Modifier,
    items: List<PreferenceValue>,
    selectedItem: PreferenceValue,
    onItemSelected: (PreferenceValue) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.padding(4.dp)) {
        Row(Modifier.clickable {
            expanded = !expanded
        }) {
            Text(text = stringResource(selectedItem.stringRes!!))
            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }) {
            Card(
                modifier = Modifier.padding(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                items.forEach {
                    Text(modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clickable {
                            expanded = !expanded
                            onItemSelected.invoke(it)
                        }, text =  stringResource(it.stringRes)
                    )
                }
            }

        }
    }
}