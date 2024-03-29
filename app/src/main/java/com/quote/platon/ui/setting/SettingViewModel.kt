package com.quote.platon.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }

    val text: LiveData<String> = _text

     val swipeEffect = MutableLiveData<Boolean>()

     val musicState = MutableLiveData<Boolean>()

    val landscapeOrientation = MutableLiveData<Boolean>()


}