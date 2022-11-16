package and09.multiweatherapp.weatherapi

import and09.multiweatherapp.HttpRequest
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder
import kotlin.jvm.Throws


class OpenWeatherMapAPI private constructor(queryString: String) : WeatherAPI{

    private val weatherdata: JSONObject

    companion object {
        private const val API_KEY = "3af8dd38306b05b4d3d04f886736f2ca"
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/weather?lang=de&APPID=$API_KEY&"

        @FromLocationName
        @Throws(IOException::class, JSONException::class)
        fun fromLocationName(locationName: String?): WeatherAPI {
            //return OpenWeatherMapAPI("q=" + URLEncoder.encode( locationName, "UTF-8"))
            return OpenWeatherMapAPI("q=" +  locationName)
        }



        @Throws(IOException::class, JSONException::class)
        fun fromLatLon(lat: Double, lon: Double): WeatherAPI {
            return OpenWeatherMapAPI("lat=$lat&lon=$lon")
        }
    }


    @get:Throws(JSONException::class)
    override val temperature: Int
        get() {
            val main = weatherdata.getJSONObject("main")
            val tempKelvin = main.getDouble("temp")
            return (tempKelvin - 273.15).toInt()
        }

    @get:Throws(JSONException::class)
    override val description: String
        get() {
            val weather = weatherdata.getJSONArray("weather")
            return weather.getJSONObject(0).getString("description")
        }

    @get:Throws(JSONException::class)
    override val iconUrl: String
        get() {
            val weather = weatherdata.getJSONArray("weather")
            return "https://openweathermap.org/img/w/${weather.getJSONObject(0).getString("icon")}.png"
        }

    @get:Throws(JSONException::class)
    override val location: String
        get() {
            return weatherdata.getString("name")
        }


    override val providerUrl: String
        get() = "https://www.openweathermap.org"

    init {
        val result = HttpRequest.request(BASE_URL + queryString)
        Log.d("LogResult", BASE_URL + queryString)
        weatherdata = JSONObject(result)
        //Log.d("Log Weather API:", "${weatherdata}")
        println(weatherdata.toString()) //Debug!
    }

}