package com.javainiai.chefskiss.ui.app

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javainiai.chefskiss.data.database.config.Config
import com.javainiai.chefskiss.data.database.services.configservice.ConfigService
import com.javainiai.chefskiss.data.enums.Language
import com.javainiai.chefskiss.data.utils.LocaleUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChefsKissAppViewModel(private val context: Context, private val config: ConfigService) :
    ViewModel() {
    var languageState = MutableStateFlow(Language.English)
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val languageCode = config.getValue("language")
            var language = Language.English
            if (languageCode != null) {
                for (lang in Language.entries) {
                    if (lang.code == languageCode) {
                        language = lang
                        break
                    }
                }
            }
            updateLanguage(language)
        }
    }

    fun updateLanguage(language: Language) {
        languageState.update { language }

        viewModelScope.launch(Dispatchers.IO) {
            config.insert(Config("language", language.code))
        }

        LocaleUtils.setLocale(context, language.code)
    }
}