package and09.multiweatherapp.weatherapi

import and09.multiweatherapp.HttpRequest
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder

interface WeatherAPI {
    @get:Throws(JSONException::class)
    val temperature: Int

    @get:Throws(JSONException::class)
    val description: String

    @get:Throws(JSONException::class)
    val iconUrl: String

    @get:Throws(JSONException::class)
    val location: String

    val providerUrl: String
}

class WeatherStackAPI private constructor(queryString: String) {
    private val weatherdata: JSONObject
    companion object {
        private const val API_KEY = "a45b0ad71e10476c6ec06a3d815bd720"
        private const val BASE_URL = "http://api.weatherstack.com/current?access_key=$API_KEY&"

        @Throws(IOException::class, JSONException::class)
        fun fromLocationName(locationName: String?): WeatherStackAPI {
            return WeatherStackAPI("query=" + URLEncoder.encode(locationName, "UTF-8"))
        }
        @Throws(IOException::class, JSONException::class)
        fun fromLatLon(lat: Double, lon: Double): WeatherStackAPI {
            return WeatherStackAPI("query=$lat,$lon")
        }
    }
    init {
        val result = HttpRequest.request(BASE_URL + queryString)
        weatherdata = JSONObject(result)
        println(weatherdata.toString())
    }
}