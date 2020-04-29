package com.volio.coloringbook2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.volio.coloringbook2.model.ColorBookItem

class ColorBookViewModel : ViewModel() {
    val url = MutableLiveData<String>().apply { postValue("hix") }
}