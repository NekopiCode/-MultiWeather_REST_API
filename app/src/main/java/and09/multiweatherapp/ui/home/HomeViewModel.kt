package and09.multiweatherapp.ui.home



import and09.multiweatherapp.MainActivity
import and09.multiweatherapp.R
import and09.multiweatherapp.weatherapi.*
import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationRequest
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
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


    private lateinit var man: LocationManager
    private var loc: LocationListener? = null
    private var output: String = ""




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
                val ipInput = prefs.getString("pref_Key_IP_Input", "")?.trim()
                val useGPS_Status = prefs.getBoolean("use_gps", false)
                if (useGPS_Status == true){
                    if (ActivityCompat.checkSelfPermission(
                            app,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            app,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return@withContext
                    }

                }



                //Einsendeaufgabe Nummer 4
                val test_lat = 53.4708393
                val test_lon = 7.4848308
                val apiStringBuilder = when("$providerClassName"){
                    "WeatherStackAPI" -> "$test_lat,$test_lon"
                    "OpenWeatherMapAPI" -> "&lat=$test_lat&lon=$test_lon"
                    else -> {
                        ""
                    }
                }

                val locationName_or_gps_chooser = when(useGPS_Status){
                    true -> apiStringBuilder
                    false -> locationName

                }
                //Log - Just for testing
                Log.d("Log API Stringbuilder:", apiStringBuilder)
                Log.d("Log Provider:", "$providerClassName")
                Log.d("Log use_gps Status:", "$useGPS_Status")

                try {
                    val cls = Class.forName("${WeatherAPI::class.java.`package`?.name}.$providerClassName").kotlin
                    val func = cls.companionObject?.declaredFunctions?.find { it.hasAnnotation<FromLocationName>() }
                    weather = func?.call(cls.companionObjectInstance, locationName_or_gps_chooser ) as WeatherAPI

                    Log.d(javaClass.simpleName, "Temp: ${weather?.temperature}")
                    Log.d(javaClass.simpleName, "Temp: ${weather?.temperature}")
                    Log.d(javaClass.simpleName, "Description:${weather?.description}")
                    Log.d(javaClass.simpleName, "Icon-URL: ${weather?.iconUrl}")
                    Log.d(javaClass.simpleName, "Provider: ${weather?.providerUrl}")
                    Log.d("LogLocation", "Location: ${weather?.location}")
                    Log.d("LogIPHomeView", "$ipInput")

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

