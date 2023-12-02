package com.learn.worlds.ui.profile.edit

import javax.annotation.concurrent.Immutable

sealed class ProfileEditEvent {
    object onSaveProfileEvent: ProfileEditEvent()
    object onDismissErrorDialogEvent: ProfileEditEvent()
    data class onChangeFirstNameEvent(val name: String): ProfileEditEvent()
    data class onChangeSecondNameEvent(val name: String): ProfileEditEvent()
}
@Immutable
data class ProfileEditState(
    val firstName:  String = "",
    val secondName: String = "",
    val loadingState: Boolean,
    val somethingWentWrongState: Boolean = false
)