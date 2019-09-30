package com.example.gmails4

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class PageViewModel : ViewModel(){
    private val _index = MutableLiveData<Int>()//this is not a list,it only can save one data
    private val _Str = MutableLiveData<String>()


    //datatype must same to observer
    val text: LiveData<String> = Transformations.map(_index){
        "Hello world from section: $it"
    }//change data to usable value
    val text2:LiveData<String> = Transformations.map(_Str){
        it
    }

    fun setIndex(index: Int){
        _index.value = index
    }
    fun setStr(str:String){
        _Str.value = str
    }

}