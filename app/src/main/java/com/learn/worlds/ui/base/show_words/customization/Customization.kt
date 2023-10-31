package com.learn.worlds.ui.base.show_words.customization

import androidx.compose.ui.graphics.Color
import com.codelab.basiclayouts.ui.theme.md_theme_dark_cardbg_switch_off_learned
import com.codelab.basiclayouts.ui.theme.md_theme_dark_cardbg_switch_off_learning
import com.codelab.basiclayouts.ui.theme.md_theme_dark_cardbg_switch_on_learned
import com.codelab.basiclayouts.ui.theme.md_theme_dark_cardbg_switch_on_learning
import com.codelab.basiclayouts.ui.theme.md_theme_dark_textColor_switch_off_learned
import com.codelab.basiclayouts.ui.theme.md_theme_dark_textColor_switch_off_learning
import com.codelab.basiclayouts.ui.theme.md_theme_dark_textColor_switch_on_learned
import com.codelab.basiclayouts.ui.theme.md_theme_dark_textColor_switch_on_learning
import com.codelab.basiclayouts.ui.theme.md_theme_light_cardbg_switch_off_learned
import com.codelab.basiclayouts.ui.theme.md_theme_light_cardbg_switch_off_learning
import com.codelab.basiclayouts.ui.theme.md_theme_light_cardbg_switch_on_learned
import com.codelab.basiclayouts.ui.theme.md_theme_light_cardbg_switch_on_learning
import com.codelab.basiclayouts.ui.theme.md_theme_light_textColor_switch_off_learned
import com.codelab.basiclayouts.ui.theme.md_theme_light_textColor_switch_off_learning
import com.codelab.basiclayouts.ui.theme.md_theme_light_textColor_switch_on_learned
import com.codelab.basiclayouts.ui.theme.md_theme_light_textColor_switch_on_learning
import com.learn.worlds.data.model.base.LearningStatus

fun getCardBackground(isSystemDarkTheme: Boolean, foreignCard: Boolean, learningStatus: String): Color{
    return  if (!isSystemDarkTheme) {
        if (foreignCard) {
            if (learningStatus == LearningStatus.LEARNING.name) {
                md_theme_light_cardbg_switch_on_learning
            } else {
                md_theme_light_cardbg_switch_on_learned
            }
        } else {
            if (learningStatus == LearningStatus.LEARNING.name) {
                md_theme_light_cardbg_switch_off_learning
            } else {
                md_theme_light_cardbg_switch_off_learned
            }
        }
    } else {
        if (foreignCard) {
            if (learningStatus == LearningStatus.LEARNING.name) {
                md_theme_dark_cardbg_switch_on_learning
            } else {
                md_theme_dark_cardbg_switch_on_learned
            }
        } else {
            if (learningStatus == LearningStatus.LEARNING.name) {
                md_theme_dark_cardbg_switch_off_learning
            } else {
                md_theme_dark_cardbg_switch_off_learned
            }
        }
    }
}

fun getCardTextColor(isSystemDarkTheme: Boolean, foreignCard: Boolean, learningStatus: String): Color{
    return   if (!isSystemDarkTheme) {
        if (foreignCard) {
            if (learningStatus == LearningStatus.LEARNING.name) {
                md_theme_light_textColor_switch_on_learning
            } else {
                md_theme_light_textColor_switch_on_learned
            }
        } else {
            if (learningStatus == LearningStatus.LEARNING.name) {
                md_theme_light_textColor_switch_off_learning
            } else {
                md_theme_light_textColor_switch_off_learned
            }
        }
    } else {
        if (foreignCard) {
            if (learningStatus == LearningStatus.LEARNING.name) {
                md_theme_dark_textColor_switch_on_learning
            } else {
                md_theme_dark_textColor_switch_on_learned
            }
        } else {
            if (learningStatus == LearningStatus.LEARNING.name) {
                md_theme_dark_textColor_switch_off_learning
            } else {
                md_theme_dark_textColor_switch_off_learned
            }
        }

    }
}