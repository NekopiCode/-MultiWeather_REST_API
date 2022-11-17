package and09.multiweatherapp.weatherapi

import and09.multiweatherapp.HttpRequest
import and09.multiweatherapp.R
import and09.multiweatherapp.ui.dashboard.DashboardFragment
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.preference.PreferenceManager
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder


class SpringWeatherAPI private constructor(queryString: String): WeatherAPI, Application() {

    private val weatherdata: JSONObject

    init {
        //Einsendeaufgabe Nummer 3 teil 1/2
        setServerAddress()
        val result = HttpRequest.request(BASE_URL + queryString)
        weatherdata = JSONObject(result)
        println(weatherdata.toString())

    }

    companion object {

        private lateinit var context: Context
        fun appContext(myContext: Context){
            context = myContext
        }

        private var BASE_URL = ""

        fun setServerAddress() {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val getIP = prefs.getString("pref_Key_IP_Input", "")?.trim()
            BASE_URL = "http://$getIP:8080/weather?"

            Log.d("Spring_Log", "WebAPI is: $getIP")
        }

        @FromLocationName
        @Throws(IOException::class, JSONException::class)
        fun fromLocationName(locationName: String?): WeatherAPI {
            return SpringWeatherAPI("location=" +
                   URLEncoder.encode(locationName, "UTF-8"))
        }

        @Throws(IOException::class, JSONException::class)
        fun fromLatLon(lat: Double, lon: Double): WeatherAPI {
            return SpringWeatherAPI("query=$lat,$lon")
        }
    }

    @get:Throws(JSONException::class)
    override val temperature: Int
        get() {
            return weatherdata.getInt("temperature")
        }

    @get:Throws(JSONException::class)
    override val description: String
        get() {
            return weatherdata.getString("description")
        }
    @get:Throws(JSONException::class)
    override val location: String
        get() {
            return weatherdata.getString("locationName")
        }
    @get:Throws(JSONException::class)
    override val iconUrl: String
        get() {
            return weatherdata.getString("iconUrl")
        }
    @get:Throws(JSONException::class)
    override val providerUrl: String
        get() {
            return weatherdata.getString("providerUrl")
        }
}