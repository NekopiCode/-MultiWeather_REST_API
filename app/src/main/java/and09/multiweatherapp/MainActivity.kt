package and09.multiweatherapp

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import and09.multiweatherapp.databinding.ActivityMainBinding
import and09.multiweatherapp.ui.home.HomeFragment
import and09.multiweatherapp.weatherapi.SpringWeatherAPI
import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode


class MainActivity : AppCompatActivity(), LocationListener {


    val prefsChangedListener = object : SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(prefs: SharedPreferences?, key: String?) {
            val getboolean = prefs?.getBoolean("use_gps", false)
                if (getboolean == true) {
                    getLocation()
                }
            println("prefs changed, key: $key")
        }
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        SpringWeatherAPI.appContext(this)

        //Prefs. Manager Listener
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        prefs.registerOnSharedPreferenceChangeListener(prefsChangedListener)


        val getboolean = prefs.getBoolean("use_gps", false)
        if (getboolean == true) {
            getLocation()
        }

        // Permissions
        requestPermissions(
            arrayOf<String>(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ), 0
        )

        //onCreate End
    }


    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }

    override fun onLocationChanged(location: Location) {
        Log.d("Lat_Long MainActivity", "${location.latitude}, ${location.longitude}")
        //Toast.makeText(this, "${location.latitude}, ${location.longitude}", Toast.LENGTH_SHORT).show()

    }


//MainActivity End
}




