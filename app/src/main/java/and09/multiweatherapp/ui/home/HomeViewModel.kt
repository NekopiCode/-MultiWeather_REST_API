package and09.multiweatherapp.ui.home

import and09.multiweatherapp.R
import and09.multiweatherapp.weatherapi.*
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import kotlinx.coroutines.*
import org.json.JSONException
import java.io.FileNotFoundException
import java.net.ConnectException
import java.net.URL
import java.net.UnknownHostException
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.hasAnnotation

class HomeViewModel(application: Application) : AndroidViewModel(application) {


    private val _location: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    private val _iconBitmap: MutableLiveData<Bitmap> by lazy {
        MutableLiveData<Bitmap>()
    }

    private val _temperature: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    private val _description: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    private val _provider: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }


    val location: LiveData<String> = _location
    val temperature: LiveData<String> = _temperature
    val description: LiveData<String> = _description
    val provider: LiveData<String> = _provider
    val iconBitmap: LiveData<Bitmap> = _iconBitmap


    fun retrieveWeatherData() {
        CoroutineScope(Dispatchers.Main).launch() {
            var errorMessage: String = ""
            var weather: WeatherAPI? = null
            var bitmap: Bitmap? = null
            withContext(Dispatchers.IO) {
                val app = getApplication() as Application
                val prefs = PreferenceManager.getDefaultSharedPreferences(app)
                val locationName = prefs.getString(app.getString(R.string.location_name), "Augsburg")?.trim()
                val providerClassName = prefs.getString(app.getString(R.string.weather_provider), "${OpenWeatherMapAPI}")
                //val providerClassName = Class.forName("${WeatherAPI::providerUrl}")
                try {
                    val cls = Class.forName("${WeatherAPI::class.java.`package`?.name}.$providerClassName").kotlin
                    val func = cls.companionObject?.declaredFunctions?.find { it.hasAnnotation<FromLocationName>() }
                    weather = func?.call(cls.companionObjectInstance, locationName) as WeatherAPI

                    Log.d(javaClass.simpleName, "Temp: ${weather?.temperature}")
                    Log.d(javaClass.simpleName, "Temp: ${weather?.temperature}")
                    Log.d(javaClass.simpleName, "Description:${weather?.description}")
                    Log.d(javaClass.simpleName, "Icon-URL: ${weather?.iconUrl}")
                    Log.d(javaClass.simpleName, "Provider:${weather?.providerUrl}")

                    val iconUrl = URL(weather?.iconUrl) // java.net!
                    val inputStream = iconUrl.openStream()
                    bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream.close()
                } catch (ex: Exception) {
                    Log.e(javaClass.simpleName, ex.cause.toString())
                    errorMessage = when (ex.cause) {
                        is UnknownHostException -> "Keine Internetverbindung. Bitte überprüfen Sie Ihre Netzwerkeinstellungen"
                        is ConnectException -> "Netzwerkdienst antwortet nicht. Bitte schalten Sie auf einen anderen Dienst um"
                        is FileNotFoundException -> "Es wurden keine Daten zum gewählten Standort zurückgeliefert"
                        else -> "Unbekannter Fehler"
                    }
                }

            }
            if (weather != null) updateValues(weather, bitmap)
            else Toast.makeText(getApplication(), errorMessage, Toast.LENGTH_LONG).show()
        }

    }

    fun updateValues(weather: WeatherAPI?, bitmap: Bitmap?) {
        try {
            _location.value = weather?.location
            _temperature.value = "${weather?.temperature} °C"
            _description.value = weather?.description
            _provider.value = weather?.providerUrl
            _iconBitmap.value = bitmap
        } catch (ex: JSONException) {
            Log.e(javaClass.simpleName, ex.toString())
        }
    }
}
