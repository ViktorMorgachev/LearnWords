package com.learn.worlds.utils

import androidx.annotation.StringRes
import com.learn.worlds.R

val uniqueSyncronizationUniqueWorkName = "syncronization_work"

enum class ErrorType(@StringRes val resID: Int) {
    NOT_AUTHENTICATED(R.string.error_not_auth),
    SOMETHING_ERROR(R.string.error_someshing_went_wrong),
    DATABASE_LIMITS(R.string.error_limits_adding_words),
    INVALID_FIREBASE_LOGIN_CREDENTIALS(R.string.error_invalid_login)
}