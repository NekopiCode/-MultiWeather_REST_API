package and09.multiweatherapp

import and09.multiweatherapp.weatherapi.OpenWeatherMapAPI
import and09.multiweatherapp.weatherapi.SpringWeatherAPI
import and09.multiweatherapp.weatherapi.WeatherStackAPI
import org.json.JSONException
import org.junit.Test

import org.junit.Assert.*
import java.io.IOException
import kotlin.jvm.Throws

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {


    //Open Weather
    @Test
    @Throws(IOException::class, JSONException::class)
    fun openWeatherMap_getResponseFromName() {
        val api = OpenWeatherMapAPI.fromLocationName("Augsburg")
        println("Temp: ${api.temperature}")
        println("Description: ${api.description}")
        println("Icon: ${api.iconUrl}")
        println("Location: ${api.location}")
    }


    @Test
    @Throws(IOException::class, JSONException::class)
    fun openWeatherMap_getResponseFromLatLon() {
        val api = OpenWeatherMapAPI.fromLatLon(48.366512, 10.894446)
        println("Temp: ${api.temperature}")
        println("Description: ${api.description}")
        println("Icon: ${api.iconUrl}")
        println("Location: ${api.location}")
    }

    //Weather Stack
    @Test
    @Throws(IOException::class, JSONException::class)
    fun weatherStack_getResponseFromName() {
        val api = WeatherStackAPI.fromLocationName("Augsburg")
    }
    @Test
    @Throws(IOException::class, JSONException::class)
    fun weatherStack_getResponseFromLatLon() {
        val api = WeatherStackAPI.fromLatLon(48.366512, 10.894446)
    }


    //Spring Weather Api
    @Test
    @Throws(IOException::class, JSONException::class)
    fun springWeatherAPI_getResponseFromName() {
        val api = SpringWeatherAPI.fromLocationName("San Francisco")
                println("Temp: ${api.temperature}")
                println("Description: ${api.description}")
                println("Icon: ${api.iconUrl}")
                println("Location: ${api.location}")
    }
    @Test
    @Throws(IOException::class, JSONException::class)
    fun springWeatherAPI_getResponseFromLatLon() {
        val api = SpringWeatherAPI.fromLatLon(37.77, -122.42)
        println("Temp: ${api.temperature}")
        println("Description: ${api.description}")
        println("Icon: ${api.iconUrl}")
        println("Location: ${api.location}")
    }

}