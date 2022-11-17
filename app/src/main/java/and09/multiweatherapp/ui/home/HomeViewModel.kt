package and09.multiweatherapp.ui.home



import and09.multiweatherapp.R
import and09.multiweatherapp.weatherapi.*
import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationRequest
import android.util.Log
import android.widget.Toast
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.PERMISSION_DENIED
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.content.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import kotlinx.coroutines.*
import org.json.JSONException
import java.io.FileNotFoundException
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.ConnectException
import java.net.URL
import java.net.UnknownHostException
import kotlin.jvm.Throws
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

    private var lat: String = ""
    private var lon: String = ""

    @Throws(SecurityException::class)
    fun retrieveWeatherData() {
        CoroutineScope(Dispatchers.Main).launch() {
            var errorMessage: String = ""
            var weather: WeatherAPI? = null
            var bitmap: Bitmap? = null
            withContext(Dispatchers.IO) {
                val app = getApplication() as Application
                val prefs = PreferenceManager.getDefaultSharedPreferences(app)
                val locationName = prefs.getString(app.getString(R.string.location_name), "Aurich")?.trim()
                val providerClassName = prefs.getString("weather_provider", "$provider")?.trim()
                val useGPS_Status = prefs.getBoolean("use_gps", false)


                if (checkSelfPermission(
                        app,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PermissionChecker.PERMISSION_GRANTED  ||
                    checkSelfPermission(
                        app,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PermissionChecker.PERMISSION_GRANTED
                ) else {
                    val locationManager_retrieveWeatherData = app.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val lastLocation = locationManager_retrieveWeatherData.getLastKnownLocation(GPS_PROVIDER)
                    lat = lastLocation?.latitude.toString()
                    lon = lastLocation?.longitude.toString()
                }

                Log.d("Home Coord", "$lat, $lon")
                val apiStringBuilder = when("$providerClassName"){
                    "WeatherStackAPI" -> "$lat,$lon"
                    "OpenWeatherMapAPI" -> "&lat=$lat&lon=$lon"
                    else -> {
                        ""
                    }
                }

                val locationName_or_gps_chooser = when(useGPS_Status){
                    true -> apiStringBuilder
                    false -> locationName
                }

                //Log - Just for testing
                Log.d("Log API Stringbuilder", apiStringBuilder)
                Log.d("Log Provider", "$providerClassName")
                Log.d("HOME GPS TEST", "$lat,$lon")

                try {
                    val cls = Class.forName("${WeatherAPI::class.java.`package`?.name}.$providerClassName").kotlin
                    val func = cls.companionObject?.declaredFunctions?.find { it.hasAnnotation<FromLocationName>() }
                    weather = func?.call(cls.companionObjectInstance, locationName_or_gps_chooser ) as WeatherAPI

                    Log.d(javaClass.simpleName, "Temp: ${weather?.temperature}")
                    Log.d(javaClass.simpleName, "Description:${weather?.description}")
                    Log.d(javaClass.simpleName, "Icon-URL: ${weather?.iconUrl}")
                    Log.d(javaClass.simpleName, "Provider: ${weather?.providerUrl}")
                    Log.d(javaClass.simpleName, "Location: ${weather?.location}")

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
                        else -> Log.d("Bug", "Unbekannte Fehler").toString()
                    }
                }
            }
            if (weather != null)
                updateValues(weather, bitmap)
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

