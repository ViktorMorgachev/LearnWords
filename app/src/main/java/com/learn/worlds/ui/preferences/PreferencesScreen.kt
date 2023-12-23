package com.learn.worlds.ui.preferences

import android.graphics.drawable.VectorDrawable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.learn.worlds.R
import com.learn.worlds.ui.common.ActualTopBar
import com.learn.worlds.ui.common.IconLeftAppBar
import com.learn.worlds.ui.common.PreferenceSpinner
import com.learn.worlds.ui.theme.LearnWordsTheme
import com.learn.worlds.utils.stringRes


enum class PreferenceValue(@StringRes val stringRes: Int) {
    Native(stringRes = R.string.prefs_native),
    Foreign(stringRes = R.string.prefs_foreign),
    Random(stringRes = R.string.prefs_random),
    GenderSpeechMale(stringRes = R.string.prefs_speech_gender_male),
    GenderSpeechFemale(stringRes = R.string.prefs_speech_gender_female),
    GenderProfileFemale(stringRes = R.string.prefs_profile_gender_female),
    GenderProfileMale(stringRes = R.string.prefs_profile_gender_male),
    GenderProfileOther(stringRes = R.string.prefs_profile_gender_other),
    GenderProfileHide(stringRes = R.string.prefs_profile_gender_hide),
}
enum class PreferenceData(@StringRes val prefName: Int, val key: String) {
    DefaultLanguageOfList(
        prefName = R.string.prefs_default_language,
        key = "default_language_of_list"
    ),
    DefaultLanguageOfMemorization(
        prefName = R.string.prefs_default_language,
        key = "default_language_of_memorization"
    ),
    DefaultTimerOfMemorization(
        prefName = R.string.prefs_timer_settings,
        key = "default_timer_of_memorization"
    ),
    DefaultSpeechSoundGender(
        prefName = R.string.prefs_speech_gender,
        key = "default_speech_sound_gender"
    ),
    DefaultProfileGender(
        prefName = R.string.gender,
        key = "default_profile_gender"
    )
}

fun PreferenceValue.key(): String {
    return this.name.lowercase()
}

sealed class Preferences(@StringRes open val groupName: Int? = null, open val key: String) {

    data class SelecteablePreference(
        override val groupName: Int? = null,
        val variants: List<PreferenceValue>,
        var selectedVariant: PreferenceValue,
        val preferenceData: PreferenceData,
    ) : Preferences(groupName, preferenceData.key)

    data class ProfilePreference(
        val variants: List<PreferenceValue>,
        var selectedVariant: PreferenceValue,
        val preferenceData: PreferenceData,
        @DrawableRes val icon: Int,
    ) : Preferences(key = preferenceData.key)

    data class CheckeablePreference(
        override val groupName: Int? = null,
        var checked: Boolean,
        val preferenceData: PreferenceData,
    ) : Preferences(groupName, preferenceData.key)

    data class SliderPreference(
        override val groupName: Int? = null,
        val preferenceData: PreferenceData,
        val range: IntRange,
        var actualValue: String
    ) : Preferences(groupName, preferenceData.key)
}


@Preview
@Composable
fun PreferencesScreenPreview() {
    var allPreferences by remember {
        mutableStateOf(
            listOf(
                Preferences.SelecteablePreference(
                    variants = listOf(
                        PreferenceValue.Native,
                        PreferenceValue.Foreign,
                    ),
                    preferenceData = PreferenceData.DefaultLanguageOfList,
                    selectedVariant = PreferenceValue.Native,
                    groupName = R.string.list_of_words,
                ),
                Preferences.SelecteablePreference(
                    variants = listOf(
                        PreferenceValue.Native,
                        PreferenceValue.Foreign,
                        PreferenceValue.Random
                    ),
                    groupName = R.string.prefs_group_learn_screen,
                    selectedVariant = PreferenceValue.Native,
                    preferenceData = PreferenceData.DefaultLanguageOfMemorization

                ),
                Preferences.SliderPreference(
                    preferenceData = PreferenceData.DefaultTimerOfMemorization,
                    range = 60..120,
                    actualValue = "60",
                    groupName = R.string.prefs_group_learn_screen,
                ),
                Preferences.SelecteablePreference(
                    variants = listOf(
                        PreferenceValue.GenderSpeechFemale,
                        PreferenceValue.GenderSpeechMale
                    ),
                    groupName = R.string.prefs_others,
                    selectedVariant = PreferenceValue.GenderSpeechFemale,
                    preferenceData = PreferenceData.DefaultSpeechSoundGender
                )
            )
        )
    }

    LearnWordsTheme {
        PreferencesScreen(
            modifier = Modifier.fillMaxWidth(),
            data = allPreferences,
            onChangedPreferences = { changedPrefs ->
                val oldList: MutableList<Preferences> =
                    mutableListOf<Preferences>().apply { addAll(allPreferences) }
                oldList.firstOrNull { it.key == changedPrefs.key }?.let {
                    val actualIndex = oldList.indexOf(it)
                    oldList.removeAt(actualIndex)
                    oldList.add(actualIndex, changedPrefs)
                    allPreferences = oldList
                }
            },
            appBar = {
                ActualTopBar(
                    iconLeftAppBar = IconLeftAppBar.NavBackIcon(),
                    title = R.string.prefs_title,
                )
            })
    }
}

@Composable
fun PreferencesScreenBase(
    modifier: Modifier = Modifier,
    viewModel: PreferencesViewModel = hiltViewModel(),
    uiState: PreferencesState = viewModel.uiState.collectAsStateWithLifecycle().value
) {

    LearnWordsTheme {
        PreferencesScreen(
            modifier = modifier.fillMaxWidth(),
            data = uiState.actualPreferences,
            onChangedPreferences = { changedPrefs ->
                viewModel.handleEvent(PreferencesEvent.onUpdatePreferences(changedPrefs))
            },
            appBar = {
                ActualTopBar(
                    iconLeftAppBar = IconLeftAppBar.NavBackIcon(),
                    title = R.string.prefs_title,
                )
            })
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreferencesScreen(
    modifier: Modifier = Modifier,
    data: List<Preferences>,
    onChangedPreferences: (Preferences) -> Unit,
    appBar: @Composable (() -> Unit)? = null,
) {

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            appBar?.invoke()
            Card(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        data.filter { it.groupName != null }.groupBy { it.groupName }
                            .forEach { groupName, preferences ->
                                stickyHeader {
                                    groupName?.let {
                                        PreferenceHeader(text = stringRes(groupName))
                                    }
                                }
                                items(preferences) { actualPref ->
                                    if (actualPref is Preferences.SliderPreference) {
                                        SliderPreference(modifier = modifier,
                                            sliderPreference = actualPref,
                                            onSlidedAction = { newValue ->
                                                onChangedPreferences.invoke(
                                                    actualPref.copy(
                                                        actualValue = newValue
                                                    )
                                                )
                                            })
                                    }
                                    if (actualPref is Preferences.SelecteablePreference) {
                                        SelecteablePreference(
                                            modifier = modifier,
                                            preference = actualPref,
                                            onSelectedVariant = { newValue ->
                                                onChangedPreferences.invoke(
                                                    actualPref.copy(
                                                        selectedVariant = newValue
                                                    )
                                                )
                                            })
                                    }
                                }
                            }
                    }
                }
            }
        }

    }
}

@Composable
fun PreferenceHeader(modifier: Modifier = Modifier, text: String) {
    Row(
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SwitchPreference(
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    text: String,
    onSwitchedState: () -> Unit
) {

    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = text)
        Switch(
            checked = checked,
            onCheckedChange = {
                onSwitchedState.invoke()
            },
        )
    }
}

@Composable
fun SliderPreference(
    modifier: Modifier = Modifier,
    sliderPreference: Preferences.SliderPreference,
    onSlidedAction: (String) -> Unit
) {

    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(sliderPreference.preferenceData.prefName),
            modifier = Modifier.weight(1f)
        )
        Slider(
            modifier = Modifier.weight(0.4f),
            valueRange = sliderPreference.range.first.toFloat()..sliderPreference.range.last.toFloat(),
            value = sliderPreference.actualValue.toFloat(),
            steps = sliderPreference.range.last - sliderPreference.range.first,
            onValueChange = {
                onSlidedAction.invoke(it.toString())
            }
        )
    }
}

@Composable
fun SliderPreferenceStatefull(
    modifier: Modifier = Modifier,
    sliderPreference: Preferences.SliderPreference,
    onSlidedAction: (Int) -> Unit
) {

    var currentValue by remember { mutableStateOf(sliderPreference.actualValue) }

    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(sliderPreference.preferenceData.prefName),
            modifier = Modifier.weight(1f)
        )
        Slider(
            modifier = Modifier.weight(0.4f),
            valueRange = sliderPreference.range.first.toFloat()..sliderPreference.range.last.toFloat(),
            value = currentValue.toFloat(),
            steps = sliderPreference.range.last - sliderPreference.range.first,
            onValueChange = {
                currentValue = it.toString()
                onSlidedAction.invoke(it.toInt())
            }
        )
    }
}

@Composable
fun SelecteablePreference(
    modifier: Modifier = Modifier,
    preference: Preferences.SelecteablePreference,
    onSelectedVariant: (PreferenceValue) -> Unit
) {

    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = stringResource(preference.preferenceData.prefName))
        PreferenceSpinner(
            modifier = modifier,
            items = preference.variants,
            selectedItem = preference.selectedVariant
        ) { selectedVariant ->
            onSelectedVariant.invoke(selectedVariant)
        }
    }
}


