package com.example.finalmockproject.viewmodel

import androidx.lifecycle.ViewModel
import com.example.finalmockserver.IMyAidlInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatAppViewModel @Inject constructor() : ViewModel(){
    private var aidlService: IMyAidlInterface? = null

    fun setUserService(service: IMyAidlInterface) {
        aidlService = service
    }
}
