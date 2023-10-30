package com.learn.worlds.utils

import androidx.annotation.StringRes
import com.learn.worlds.R

val uniqueSyncronizationUniqueWorkName = "syncronization_work"
enum class FirebaseDatabaseChild(val path: String){
    LEARNING_ITEMS("learning_items"),
    LEARNING_ITEMS_LAST_SYNC_DATETIME("learning_items_last_sync_datetime")
}

sealed class FirebaseError{
    data class UserDisabled(val errorType: ErrorType = ErrorType.ERROR_USER_DISABLED): FirebaseError()
    data class InvalidLoginCredentials(val errorType: ErrorType = ErrorType.INVALID_LOGIN_CREDENTIALS): FirebaseError()
    data class SomethingError(val errorType: ErrorType = ErrorType.SOMETHING_ERROR): FirebaseError()
}

enum class ErrorType(@StringRes val resID: Int) {
    NOT_AUTHENTICATED(R.string.error_not_auth),
    SOMETHING_ERROR(R.string.error_someshing_went_wrong),
    DATABASE_LIMITS(R.string.error_limits_adding_words),
    INVALID_LOGIN_CREDENTIALS(R.string.error_invalid_login),
    ERROR_USER_DISABLED(R.string.error_auth_disabled_account)
}