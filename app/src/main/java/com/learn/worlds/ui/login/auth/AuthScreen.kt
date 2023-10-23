package com.learn.worlds.ui.login.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.learn.worlds.R
import com.learn.worlds.ui.common.SomethingWentWrongDialog
import com.learn.worlds.ui.common.SuccessDialog
import com.learn.worlds.ui.theme.LearnWordsTheme


@Preview(showSystemUi = true, device = "id:pixel_3a")
@Composable
fun AuthScreenPreview() {

    LearnWordsTheme{
        AuthScreen(
            authenticationState = AuthenticationState(
                authenticationMode = AuthenticationMode.SIGN_UP,
                dialogAuthSuccess = AuthSuccessEvent.SIGN_UP
            ),
            onAuthSuccessAction = {})
    }

}

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    authenticationState: AuthenticationState,
    onAuthSuccessAction: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            if (authenticationState.isLoading) {
                CircularProgressIndicator()
            } else {
                AuthenticationForm(
                    modifier = Modifier.fillMaxSize(),
                    authenticationMode = authenticationState.authenticationMode,
                    email = authenticationState.email,
                    password = authenticationState.password,
                    handleEvent = { viewModel.handleEvent(it) },
                    passwordRequirements = authenticationState.passwordRequirements,
                    enableAuthentication = authenticationState.isFormValid()
                )
            }

            authenticationState.dialogAuthSuccess?.let {
                when (it) {
                    AuthSuccessEvent.SIGN_IN -> {
                        SuccessDialog(
                            message = stringResource(R.string.account_was_sig_in)
                        ) {
                            onAuthSuccessAction.invoke()
                            viewModel.handleEvent(AuthenticationEvent.DialogDismiss)
                        }
                    }

                    AuthSuccessEvent.SIGN_UP -> {
                        SuccessDialog(
                            message = stringResource(R.string.account_was_sign_up)
                        ) {
                            onAuthSuccessAction.invoke()
                            viewModel.handleEvent(AuthenticationEvent.DialogDismiss)
                        }
                    }
                }
            }
            authenticationState.dialogError?.let { error ->
                SomethingWentWrongDialog(
                    message = error.error,
                    onDismiss = { viewModel.handleEvent(AuthenticationEvent.DialogDismiss) })
            }
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AuthenticationForm(
    modifier: Modifier = Modifier,
    authenticationMode: AuthenticationMode,
    email: String?,
    password: String?,
    handleEvent: (event: AuthenticationEvent) -> Unit,
    passwordRequirements: List<PasswordRequirement>,
    enableAuthentication: Boolean
) {
    val actualFocusRequster = FocusRequester()
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(32.dp))
        AuthenticationTitle(authenticationMode = authenticationMode)
        Spacer(modifier = Modifier.height(20.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 32.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EmailInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(actualFocusRequster),
                    email = email ?: "",
                    onEmailChanged = { handleEvent.invoke(AuthenticationEvent.EmailChanged(it)) },
                    onNextClicked = {}
                )
                Spacer(modifier = Modifier.height(16.dp))
                PasswordInput(
                    modifier = Modifier.fillMaxWidth(),
                    password = password ?: "",
                    onPasswordChanged = { handleEvent.invoke(AuthenticationEvent.PasswordChanged(it)) },
                    onDoneClicked = { handleEvent.invoke(AuthenticationEvent.Authenticate) }
                )
                Spacer(modifier = Modifier.height(12.dp))

                AnimatedVisibility(
                    visible = authenticationMode == AuthenticationMode.SIGN_UP,
                    enter = slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }) + fadeIn(),
                    exit = slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }) + fadeOut(),
                ) {
                    RequirementForm(
                        visible = true,
                        passwordRequirements = passwordRequirements
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                AuthenticationButton(
                    modifier = Modifier.fillMaxWidth(),
                    authenticationMode = authenticationMode,
                    onAuthAction = { handleEvent.invoke(AuthenticationEvent.Authenticate) },
                    enableAuthentication = enableAuthentication
                )
                Spacer(modifier = Modifier.weight(1f))
                ToggleAuthenticationMode(
                    modifier = Modifier.fillMaxWidth(),
                    authenticationMode = authenticationMode,
                    onToggleAuthMode = { handleEvent.invoke(AuthenticationEvent.ToggleAuthenticationMode) }
                )
            }
        }
    }
}

@Composable
fun AuthenticationButton(
    modifier: Modifier = Modifier,
    authenticationMode: AuthenticationMode,
    enableAuthentication: Boolean,
    onAuthAction: () -> Unit
) {
    OutlinedButton(
        modifier = modifier,
        onClick = { onAuthAction.invoke() },
        enabled = enableAuthentication
    ) {
        Text(
            text = stringResource(
                if (authenticationMode ==
                    AuthenticationMode.SIGN_IN
                ) {
                    R.string.action_sign_in
                } else {
                    R.string.action_sign_up
                }
            ),

            )
    }
}


@Composable
fun ToggleAuthenticationMode(
    modifier: Modifier = Modifier,
    authenticationMode: AuthenticationMode,
    onToggleAuthMode: () -> Unit
) {
    Surface(
        modifier = modifier.padding(top = 8.dp),
        shadowElevation = 4.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        TextButton(modifier = Modifier
            .background(MaterialTheme.colorScheme.surface),
            onClick = {
                onToggleAuthMode.invoke()
            }
        ) {
            Text(
                text = stringResource(
                    if (authenticationMode == AuthenticationMode.SIGN_IN) {
                        R.string.action_need_account
                    } else {
                        R.string.action_already_have_account
                    }
                )
            )
        }
    }
}

@Composable
fun RequirementForm(visible: Boolean, passwordRequirements: List<PasswordRequirement>) {
    if (visible) {
        PasswordRequirements(satisfiedRequirements = passwordRequirements)
    }
}


@Composable
fun PasswordInput(
    modifier: Modifier = Modifier,
    password: String,
    onPasswordChanged: (email: String) -> Unit,
    onDoneClicked: () -> Unit,
) {

    var isPasswordHidden by rememberSaveable { mutableStateOf(true) }


    TextField(
        modifier = modifier,
        value = password,
        onValueChange = {
            onPasswordChanged(it)
        },
        singleLine = true,
        label = {
            Text(
                text = stringResource(id = R.string.label_password)
            )
        },
        trailingIcon = {
            Icon(
                modifier = Modifier.clickable {
                    isPasswordHidden = !isPasswordHidden
                },
                imageVector = if (isPasswordHidden) {
                    Icons.Default.Visibility
                } else Icons.Default.VisibilityOff,
                contentDescription = null
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null
            )
        },
        visualTransformation = if (!isPasswordHidden) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onDoneClicked()
            }
        )
    )
}

@Composable
fun EmailInput(
    modifier: Modifier = Modifier,
    email: String?,
    onEmailChanged: (email: String) -> Unit,
    onNextClicked: () -> Unit,
) {
    TextField(modifier = modifier,
        value = email ?: "",
        onValueChange = {
            onEmailChanged(it)
        },
        label = {
            Text(text = stringResource(id = R.string.label_email))
        },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null
            )
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Email

        ),
        keyboardActions = KeyboardActions(
            onNext = {
                onNextClicked()
            }
        )
    )
}

@Composable
fun AuthenticationTitle(
    modifier: Modifier = Modifier,
    authenticationMode: AuthenticationMode
) {
    Text(
        text = stringResource(
            if (authenticationMode == AuthenticationMode.SIGN_IN) {
                R.string.label_sign_in_to_account
            } else {
                R.string.label_sign_up_for_account
            }
        ),
        fontSize = 18.sp,
        fontWeight = FontWeight.Black
    )
}

@Composable
fun PasswordRequirements(
    modifier: Modifier = Modifier,
    satisfiedRequirements: List<PasswordRequirement>
) {
    Column(modifier = modifier) {
        PasswordRequirement.entries.forEach { requement ->
            Requirement(
                message = stringResource(
                    id = requement.label
                ),
                satisfied = satisfiedRequirements.contains(
                    requement
                )
            )
        }
    }
}

@Composable
fun Requirement(
    modifier: Modifier = Modifier,
    message: String,
    satisfied: Boolean
) {
    val requirementStatus = if (satisfied) {
        stringResource(id = R.string.password_requirement_satisfied, message)
    } else {
        stringResource(id = R.string.password_requirement_needed, message)
    }
    val tint = if (satisfied) {
        MaterialTheme.colorScheme.onSurface
    } else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)

    Row(
        modifier = modifier
            .padding(6.dp)
            .semantics(mergeDescendants = true) {
                text = AnnotatedString(requirementStatus)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(12.dp),
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = tint
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier.clearAndSetSemantics { },
            text = message,
            color = tint
        )
    }
}