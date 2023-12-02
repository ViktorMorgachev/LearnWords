package com.learn.worlds.ui.profile

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.learn.worlds.R
import com.learn.worlds.data.model.base.AccountType
import com.learn.worlds.data.model.base.Balance
import com.learn.worlds.data.model.base.BalanceType
import com.learn.worlds.data.model.base.Profile
import com.learn.worlds.defaultNewUserBalance
import com.learn.worlds.ui.common.BaseButton
import com.learn.worlds.ui.common.InformationDialog
import com.learn.worlds.ui.common.PreferenceSpinner
import com.learn.worlds.ui.preferences.PreferenceData
import com.learn.worlds.ui.preferences.PreferenceValue
import com.learn.worlds.ui.preferences.Preferences
import com.learn.worlds.ui.theme.LearnWordsTheme
import com.learn.worlds.utils.ShimmerSpacer
import com.learn.worlds.utils.capitalize
import java.util.Locale

val defaultProfile = Profile(
    firstName = "Alina",
    secondName = "Andersen",
    email = "alina.anderses@gmail.com",
    accountType = AccountType.Base,
    balance = Balance(
        value = defaultNewUserBalance,
        balanceType = BalanceType.CatCoin
    )
)

private val profileState = ProfileState(
    profilePrefs = listOf(
        Preferences.ProfilePreference(
            preferenceData = PreferenceData.DefaultProfileGender,
            selectedVariant = PreferenceValue.GenderProfileHide,
            variants = listOf(
                PreferenceValue.GenderProfileOther,
                PreferenceValue.GenderProfileFemale,
                PreferenceValue.GenderProfileMale,
                PreferenceValue.GenderProfileHide
            ),
            icon = R.drawable.gender
        )
    ),
    profile = defaultProfile,
    loadingState = false
)


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileScreenPreviewDark(modifier: Modifier = Modifier) {
    ProfileScreenBase(modifier = modifier)
}

@Composable
fun ProfileScreenBase(modifier: Modifier = Modifier) {
    LearnWordsTheme {
        ProfileScreen(
            modifier = modifier,
            profileState = profileState,
            onProfileChanged = {},
            navigateToFillProfile = {},
            navigatetoBack = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun ProfileScreenPreviewLight(modifier: Modifier = Modifier) {
    ProfileScreenBase(modifier = modifier)
}

@Composable
fun ProfileScreenUI(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    uiState: ProfileState = viewModel.uiState.collectAsStateWithLifecycle().value,
    navigateToFillProfile: () -> Unit,
    navigateToBack: () -> Unit,
) {
    LearnWordsTheme {
        ProfileScreen(
            modifier = modifier,
            profileState = uiState,
            onProfileChanged = {
                viewModel.handleEvent(ProfileEvent.onUpdateProfilePrefence(it))
            },
            navigateToFillProfile = {
                navigateToFillProfile.invoke()
            },
            navigatetoBack = navigateToBack,
        )
    }
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    profileState: ProfileState,
    onProfileChanged: (Preferences.ProfilePreference) -> Unit,
    navigateToFillProfile: () -> Unit,
    navigatetoBack: () -> Unit
) {

    var profileShimmerSize = remember { mutableStateOf(IntSize.Zero) }

    var showInformationDialog by remember { mutableStateOf(false) }

    if (showInformationDialog) {
        InformationDialog(
            message = stringResource(R.string.information_fill_profile),
            onDismiss = {
                showInformationDialog = false
                navigatetoBack.invoke()
            },
            onNextButtonText = stringResource(R.string.next),
            onNextAction = {
                showInformationDialog = false
                navigateToFillProfile.invoke()
            })

    }
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (topBackground, bottomBackground, profileCard, toolbarText, profileSections) = createRefs()
        Box(modifier = Modifier
            .constrainAs(topBackground) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(profileCard.bottom, margin = 40.dp)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            .background(MaterialTheme.colorScheme.primary))
        Box(modifier = Modifier
            .constrainAs(bottomBackground) {
                top.linkTo(topBackground.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            .background(MaterialTheme.colorScheme.inverseOnSurface))
        Box(modifier = modifier
            .padding(16.dp)
            .constrainAs(profileCard) {
                start.linkTo(parent.start, margin = 16.dp)
                end.linkTo(parent.end, margin = 16.dp)
                top.linkTo(toolbarText.bottom, margin = 32.dp)
                width = Dimension.fillToConstraints
            }
            .onSizeChanged {
                profileShimmerSize.value = it
            }) {
            AnimatedVisibility(
                visible = profileState.loadingState || profileState.profile != null,
                enter = fadeIn(tween(durationMillis = 200, easing = LinearEasing)),
                exit = fadeOut(tween(durationMillis = 200, easing = LinearEasing)),
            ) {
                ProfileCard(
                    modifier = Modifier,
                    firstName = profileState.profile?.firstName ?: "",
                    secondName = profileState.profile?.secondName ?: "",
                    email = profileState.profile?.email ?: "",
                    accountType = profileState.profile?.accountType ?: AccountType.Base,
                    loadingState = profileState.profile == null
                )
            }

        }
        Text(
            modifier = modifier.constrainAs(toolbarText) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top, margin = 30.dp)
                width = Dimension.preferredWrapContent
                height = Dimension.preferredWrapContent
            }, text = stringResource(R.string.profile),
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium
        )
        Crossfade(
            modifier = modifier
                .padding(horizontal = 30.dp)
                .constrainAs(profileSections) {
                    start.linkTo(profileCard.start)
                    end.linkTo(profileCard.end)
                    top.linkTo(profileCard.bottom, margin = 30.dp)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.preferredWrapContent
                    height = Dimension.preferredWrapContent
                },
            targetState = profileState.loadingState || profileState.profile != null,
            animationSpec = tween(durationMillis = 200, easing = LinearEasing),
            label = "crossfade_profile_additionals"
        ) { state ->
            if (state) {
                ProfileAdditionalInfo(
                    modifier = modifier,
                    balance = profileState.profile?.balance,
                    profilePreference = profileState.profilePrefs,
                    onProfileChanged = { onProfileChanged.invoke(it)} ,
                    loadingState = profileState.loadingState)
            } else {
                Column {
                    EmptyProfileCard(modifier = modifier, onFillProfileAction = {
                        showInformationDialog = true
                    })
                }


            }

        }


    }

}

@Composable
fun EmptyProfileCard(modifier: Modifier = Modifier, onFillProfileAction: () -> Unit) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            modifier = Modifier.size(300.dp),
            imageVector = ImageVector.vectorResource(R.drawable.profile),
            contentDescription = "Android"
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Упс!!!\n Не полностью заполненный профиль",
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.weight(1f))
        BaseButton(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
            text = stringResource(R.string.fill),
            onClickAction = onFillProfileAction
        )

    }
}

@Composable
fun ProfileAdditionalInfo(
    modifier: Modifier = Modifier,
    loadingState: Boolean,
    balance: Balance?,
    profilePreference: List<Preferences.ProfilePreference>,
    onProfileChanged: (Preferences.ProfilePreference) -> Unit,
) {
    Column(modifier = modifier) {

        profilePreference.forEach { actualPrefs ->
            if (loadingState) {
                ShimmerSpacer(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .height(12.dp), shape = CardDefaults.shape
                )
            } else {
                SelecteableProfilePreference(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    preference = actualPrefs,
                    onSelectedVariant = {
                        onProfileChanged.invoke(
                            actualPrefs.copy(
                                selectedVariant = it
                            )
                        )
                    })
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (loadingState) {
            ShimmerSpacer(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .height(12.dp), shape = CardDefaults.shape
            )
        } else {
            balance?.let {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Icon(
                            tint = Color.Unspecified,
                            imageVector = ImageVector.vectorResource(balance.balanceType.img),
                            contentDescription = "profile_balance"
                        )
                        Text(
                            modifier = Modifier.padding(start = 16.dp),
                            text = stringResource(R.string.balance),
                            color = MaterialTheme.colorScheme.inverseSurface
                        )
                    }

                    Text(
                        text = "${profileState.profile?.balance?.value}\t≽ܫ≼",
                        color = MaterialTheme.colorScheme.inverseSurface
                    )
                }
            }

        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun ProfileCard(
    modifier: Modifier = Modifier,
    firstName: String,
    secondName: String,
    accountType: AccountType,
    email: String,
    loadingState: Boolean
) {

    var size by remember { mutableStateOf(IntSize.Zero) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged {
                size = it
            }
    ) {
        Column(
            modifier = modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AvatarIcon(firstName = firstName, lastName = secondName, loadingState = loadingState)
            Spacer(modifier = Modifier.height(8.dp))
            if (loadingState) {
                ShimmerSpacer(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(12.dp), shape = CardDefaults.shape
                )
            } else {
                Text(
                    text = "${firstName.capitalize()} ${secondName.capitalize()}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            if (loadingState) {
                ShimmerSpacer(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(12.dp), shape = CardDefaults.shape
                )
            } else {
                Text(text = email)
            }

        }

    }
}


@Composable
fun getInitials(firstName: String, lastName: String): String {
    return "${firstName.first().uppercase()}${lastName.first().uppercase()}"
}

@Composable
fun AvatarIcon(firstName: String, lastName: String, loadingState: Boolean) {


    if (loadingState) {
        ShimmerSpacer(
            modifier = Modifier
                .clip(CircleShape)
                .size(64.dp),
            shape = CircleShape,
        )

    } else {
        val initials = getInitials(firstName, lastName)
        Box(
            modifier = Modifier
                .size(64.dp)
                .drawBehind {
                    drawRoundRect(
                        cornerRadius = CornerRadius(90f, 90f), color = Color.Black, style = Stroke(
                            width = 4f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    )
                }
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp)
        ) {
            Text(
                text = initials,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }

}

@Composable
fun SelecteableProfilePreference(
    modifier: Modifier = Modifier,
    preference: Preferences.ProfilePreference,
    onSelectedVariant: (PreferenceValue) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Icon(
                tint = Color.Unspecified,
                imageVector = ImageVector.vectorResource(preference.icon),
                contentDescription = "profile_prefs_${preference.preferenceData.key}"
            )
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = stringResource(preference.preferenceData.prefName),
                color = MaterialTheme.colorScheme.inverseSurface
            )
        }
        PreferenceSpinner(
            textColor = MaterialTheme.colorScheme.onBackground,
            containerColor = MaterialTheme.colorScheme.background,
            modifier = modifier,
            items = preference.variants,
            selectedItem = preference.selectedVariant
        ) { selectedVariant ->
            onSelectedVariant.invoke(selectedVariant)
        }
    }
}
