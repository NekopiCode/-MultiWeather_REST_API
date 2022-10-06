package and09.multiweatherapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    private val _location: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    private val _iconBitmap: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val text: LiveData<String> = _text

    /*
    fun doAction() {
        CoroutineScope(Dispatchers.Main).launch() {
            while(true) {
                _text.value = java.util.Date().toString()
                delay(1000L)
            }
        }
    }
     */

}