package com.weather.weatherapp
//
//import android.content.Context
//import android.content.Intent
//import android.location.LocationManager
//import android.os.Bundle
//import android.provider.Settings
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.karumi.dexter.Dexter
//import com.karumi.dexter.MultiplePermissionsReport
//import com.karumi.dexter.PermissionToken
//import com.karumi.dexter.listener.PermissionRequest
//import com.karumi.dexter.listener.multi.MultiplePermissionsListener
//import android.Manifest
//import android.annotation.SuppressLint
//import android.app.AlertDialog
//import android.app.Dialog
//import android.content.ActivityNotFoundException
//import android.location.Location
//import android.net.Uri
//import android.os.Looper
//import android.util.Log
//import android.view.Menu
//import android.view.MenuItem
//import androidx.core.content.ContextCompat.startActivity
//import com.example.weatherapp.R
//import com.weather.Network.WeatherService
//import com.example.weatherapp.databinding.ActivityMainBinding
//import com.google.android.gms.location.*
//import com.weatherapp.models.WeatherResponse
//import retrofit.*
//import java.text.SimpleDateFormat
//import java.util.*
//
//
//
//class MainActivity : AppCompatActivity() {
//
//
//    // A fused location client variable which is further used to get the user's current location
//    private lateinit var mFusedLocationClient: FusedLocationProviderClient
//    private var mProgressDialog: Dialog? = null
//    private var binding: ActivityMainBinding? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding?.root)
//
//        // Initialize the Fused location variable
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//
//        if (!isLocationEnabled()) {
//            Toast.makeText(
//                this,
//                "Your location provider is turned off. Please turn it on.",
//                Toast.LENGTH_SHORT
//            ).show()
//
//            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//            startActivity(intent)
//        } else {
//            Dexter.withActivity(this)
//                .withPermissions(
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                )
//                .withListener(object : MultiplePermissionsListener {
//                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
//                        if (report!!.areAllPermissionsGranted()) {
//
//                            // START
//                            requestLocationData()
//                            // END
//                        }
//
//                        if (report.isAnyPermissionPermanentlyDenied) {
//                            Toast.makeText(
//                                this@MainActivity,
//                                "You have denied location permission. Please allow it is mandatory.",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//
//                    override fun onPermissionRationaleShouldBeShown(
//                        permissions: MutableList<PermissionRequest>?,
//                        token: PermissionToken?
//                    ) {
//                        showRationalDialogForPermissions()
//                    }
//                }).onSameThread()
//
//                .check()
//        }
//
//    }
//
//
//    /**
//     * A location callback object of fused location provider client where we will get the current location details.
//     */
//    private val mLocationCallback = object : LocationCallback() {
//        override fun onLocationResult(locationResult: LocationResult) {
//            val mLastLocation: Location = locationResult.lastLocation
//            val latitude = mLastLocation.latitude
//            Log.i("Current Latitude", "$latitude")
//            val longitude = mLastLocation.longitude
//            Log.i("Current Longitude", "$longitude")
//            getLocationWeatherDetails(latitude, longitude)
//        }
//
//    }
//
//    /**
//     * A function used to show the alert dialog when the permissions are denied and need to allow it from settings app info.
//     */
//    private fun showRationalDialogForPermissions() {
//        AlertDialog.Builder(this)
//            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
//            .setPositiveButton(
//                "GO TO SETTINGS"
//            ) { _, _ ->
//                try {
//                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                    val uri = Uri.fromParts("package", packageName, null)
//                    intent.data = uri
//                    startActivity(intent)
//                } catch (e: ActivityNotFoundException) {
//                    e.printStackTrace()
//                }
//            }
//            .setNegativeButton("Cancel") {
//                    dialog,
//                    _
//                ->
//                dialog.dismiss()
//            }.show()
//    }
//
//    /**
//     * A function which is used to verify that the location or GPS is enabled or not.
//     */
//    private fun isLocationEnabled(): Boolean {
//
//        // This provides access to the system location services.
//        val locationManager: LocationManager =
//            getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
//            LocationManager.NETWORK_PROVIDER
//        )
//    }
//
//
//    /**
//     * A function to request the current location. Using the fused location provider client.
//     */
//    @SuppressLint("MissingPermission")
//    private fun requestLocationData() {
//
//        val mLocationRequest = LocationRequest()
//        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//
//        mFusedLocationClient.requestLocationUpdates(
//            mLocationRequest, mLocationCallback,
//            Looper.myLooper()
//        )
//    }
//
//
////    private fun getLocationWeatherDetails(latitude: Double, longitude: Double) {
////        if (Constants.isNetworkAvailable(this)) {
////
//////            val retrofit : Retrofit = Retrofit.Builder().baseUrl(Constants.BASE_URL)
//////                .addConverterFactory(GsonConverterFactory.create()).build()
//////
//////
//////            val service : WeatherService = retrofit.create(WeatherService::class.java)
//////
//////            val listCall : Call<WeatherResponse> = service.getweather(
//////                latitude, longitude, Constants.METRIC_UNIT, Constants.APP_ID
//////            )
//////
//////            listCall.enqueue(object :Callback<WeatherResponse>,
//////                retrofit2.Callback<WeatherResponse> {
//////                override fun onResponse(
//////                    call: Call<WeatherResponse>,
//////                    response: retrofit2.Response<WeatherResponse>,
//////                ) {
//////                    if(response!!.isSuccess){
//////                        val weatherList : WeatherResponse = response.body()
//////                        Log.i("Response Reuslt", "$weatherList")
//////                    }else{
//////                        val rc = response.code()
//////                        when(rc){
//////                            400 -> {
//////                                Log.e("Error 400", "Bad Connection")
//////
//////                            }
//////                            404 -> {
//////
//////                            }
//////                        }
//////                    }
//////                }
//////
//////                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
//////                    TODO("Not yet implemented")
//////                }
//////
//////                override fun onResponse(response: Response<WeatherResponse>?, retrofit: Retrofit?) {
//////                    TODO("Not yet implemented")
//////                }
//////
//////                override fun onFailure(t: Throwable?) {
//////                    TODO("Not yet implemented")
//////                }
//////
//////
//////            })
////
////
////            /**
////             * Add the built-in converter factory first. This prevents overriding its
////             * behavior but also ensures correct behavior when using converters that consume all types.
////             */
////            val retrofit: Retrofit = Retrofit.Builder()
////                // API base URL.
////                .baseUrl(Constants.BASE_URL)
////                /** Add converter factory for serialization and deserialization of objects. */
////                /**
////                 * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
////                 * decoding from JSON (when no charset is specified by a header) will use UTF-8.
////                 */
////                .addConverterFactory(GsonConverterFactory.create())
////                /** Create the Retrofit instances. */
////                .build()
////            // END
////
////            // TODO (STEP 5: Further step for API call)
////            // START
////            /**
////             * Here we map the service interface in which we declares the end point and the API type
////             *i.e GET, POST and so on along with the request parameter which are required.
////             */
////            val service: WeatherService =
////                retrofit.create<WeatherService>(WeatherService::class.java)
////
////            /** An invocation of a Retrofit method that sends a request to a web-server and returns a response.
////             * Here we pass the required param in the service
////             */
////            val listCall: Call<WeatherResponse> = service.getweather(
////                latitude, longitude, Constants.METRIC_UNIT, Constants.APP_ID
////            )
////
////            showCustomProgressDialog()
////
////            // Callback methods are executed using the Retrofit callback executor.
////            listCall.enqueue(object : Callback<WeatherResponse> {
////                override fun onResponse(
////                    response: Response<WeatherResponse>?,
////                    retrofit: Retrofit
////                ) {
////
////                    // Check weather the response is success or not.
////                    if (response!!.isSuccess) {
////
////                        hideProgressDialog()
////                        /** The de-serialized response body of a successful response. */
////                        val weatherList: WeatherResponse = response.body()
////                        setupUI(weatherList)
////                        Log.i("Response Result", "$weatherList")
////
////                    } else {
////                        // If the response is not success then we check the response code.
////                        val sc = response.code()
////                        when (sc) {
////                            400 -> {
////                                Log.e("Error 400", "Bad Request")
////                            }
////                            404 -> {
////                                Log.e("Error 404", "Not Found")
////                            }
////                            else -> {
////                                Log.e("Error", "Generic Error")
////                            }
////                        }
////                    }
////                }
////
////                override fun onFailure(t: Throwable) {
////                    Log.e("Errorrrrr", t.message.toString())
////                    hideProgressDialog()
////                }
////
////
////            })
////
////
////        } else {
////            Toast.makeText(
////                this@MainActivity,
////                "no internet connection",
////                Toast.LENGTH_SHORT
////            ).show()
////        }
////    }
//
//
//
//    private fun getLocationWeatherDetails(latitude: Double, longitude: Double) {
//
//        if (Constants.isNetworkAvailable(this@MainActivity)) {
//
//            /**
//             * Add the built-in converter factory first. This prevents overriding its
//             * behavior but also ensures correct behavior when using converters that consume all types.
//             */
//            val retrofit: Retrofit = Retrofit.Builder()
//                // API base URL.
//                .baseUrl(Constants.BASE_URL)
//                /** Add converter factory for serialization and deserialization of objects. */
//                /**
//                 * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
//                 * decoding from JSON (when no charset is specified by a header) will use UTF-8.
//                 */
//                .addConverterFactory(GsonConverterFactory.create())
//                /** Create the Retrofit instances. */
//                .build()
//
//            /**
//             * Here we map the service interface in which we declares the end point and the API type
//             *i.e GET, POST and so on along with the request parameter which are required.
//             */
//            val service: WeatherService =
//                retrofit.create<WeatherService>(WeatherService::class.java)
//
//            /** An invocation of a Retrofit method that sends a request to a web-server and returns a response.
//             * Here we pass the required param in the service
//             */
//            val listCall: Call<WeatherResponse> = service.getweather(
//                latitude, longitude, Constants.METRIC_UNIT, Constants.APP_ID
//            )
//
//            showCustomProgressDialog() // Used to show the progress dialog
//
//            // Callback methods are executed using the Retrofit callback executor.
//            listCall.enqueue(object : Callback<WeatherResponse> {
//                @SuppressLint("SetTextI18n")
//                override fun onResponse(
//                    response: Response<WeatherResponse>,
//                    retrofit: Retrofit
//                ) {
//
//                    // Check weather the response is success or not.
//                    if (response.isSuccess) {
//
//                        hideProgressDialog() // Hides the progress dialog
//
//                        /** The de-serialized response body of a successful response. */
//                        val weatherList: WeatherResponse = response.body()
//                        Log.i("Response Result", "$weatherList")
//
//                        setupUI(weatherList)
//                    } else {
//                        // If the response is not success then we check the response code.
//                        val sc = response.code()
//                        when (sc) {
//                            400 -> {
//                                Log.e("Error 400", "Bad Request")
//                            }
//                            404 -> {
//                                Log.e("Error 404", "Not Found")
//                            }
//                            else -> {
//                                Log.e("Error", "Generic Error")
//                            }
//                        }
//                    }
//                }
//
//                override fun onFailure(t: Throwable) {
//                    hideProgressDialog() // Hides the progress dialog
//                    Log.e("Errorrrrr", t.message.toString())
//                }
//            })
//        } else {
//            Toast.makeText(
//                this@MainActivity,
//                "No internet connection available.",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//    }
//
//    /**
//     * Method is used to show the Custom Progress Dialog.
//     */
//    private fun showCustomProgressDialog() {
//        mProgressDialog = Dialog(this)
//
//        /*Set the screen content from a layout resource.
//        The resource will be inflated, adding all top-level views to the screen.*/
//        mProgressDialog!!.setContentView(R.layout.dialog_custom_progress)
//
//        //Start the dialog and display it on screen.
//        mProgressDialog!!.show()
//    }
//
//
//    /**
//     * This function is used to dismiss the progress dialog if it is visible to user.
//     */
//    private fun hideProgressDialog() {
//        if (mProgressDialog != null) {
//            mProgressDialog!!.dismiss()
//        }
//    }
//
//
//    /**
//     * Function is used to set the result in the UI elements.
//     */
//    private fun setupUI(weatherList: WeatherResponse) {
//
//        // For loop to get the required data. And all are populated in the UI.
//        for (z in weatherList.weather.indices) {
//            Log.i("NAMEEEEEEEE", weatherList.weather[z].main)
//
//            binding?.tvMain?.text = weatherList.weather[z].main
//            binding?.tvMainDescription?.text = weatherList.weather[z].description
//            binding?.tvTemp?.text =
//                weatherList.main.temp.toString() + getUnit(application.resources.configuration.locales.toString())
//            binding?.tvHumidity?.text = weatherList.main.humidity.toString() + " per cent"
//            binding?.tvMin?.text = weatherList.main.temp_min.toString() + " min"
//            binding?.tvMax?.text = weatherList.main.temp_max.toString() + " max"
//            binding?.tvSpeed?.text = weatherList.wind.speed.toString()
//            binding?.tvName?.text = weatherList.name
//            binding?.tvCountry?.text = weatherList.sys.country
//            binding?.tvSunriseTime?.text = unixTime(weatherList.sys.sunrise.toLong())
//            binding?.tvSunsetTime?.text = unixTime(weatherList.sys.sunset.toLong())
//
//            // Here we update the main icon
//            when (weatherList.weather[z].icon) {
//                "01d" -> binding?.ivMain?.setImageResource(R.drawable.sunny)
//                "02d" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
//                "03d" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
//                "04d" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
//                "04n" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
//                "10d" -> binding?.ivMain?.setImageResource(R.drawable.rain)
//                "11d" -> binding?.ivMain?.setImageResource(R.drawable.storm)
//                "13d" -> binding?.ivMain?.setImageResource(R.drawable.snowflake)
//                "01n" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
//                "02n" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
//                "03n" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
//                "10n" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
//                "11n" -> binding?.ivMain?.setImageResource(R.drawable.rain)
//                "13n" -> binding?.ivMain?.setImageResource(R.drawable.snowflake)
//            }
//        }
//    }
//
//    /**
//     * Function is used to get the temperature unit value.
//     */
//    private fun getUnit(value: String): String? {
//        Log.i("unitt", value)
//        var value = "°C"
//        if ("US" == value || "LR" == value || "MM" == value) {
//            value = "°F"
//        }
//        return value
//    }
//
//    /**
//     * The function is used to get the formatted time based on the Format and the LOCALE we pass to it.
//     */
//    private fun unixTime(timex: Long): String? {
//        val date = Date(timex * 1000L)
//        @SuppressLint("SimpleDateFormat") val sdf =
//            SimpleDateFormat("HH:mm:ss")
//        sdf.timeZone = TimeZone.getDefault()
//        return sdf.format(date)
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.action_refresh -> {
//                requestLocationData()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//}
//
//



import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.weather.Network.WeatherService
import com.weatherapp.models.WeatherResponse
import retrofit.*
import com.example.weatherapp.R
import java.text.SimpleDateFormat
import java.util.*

// OpenWeather Link : https://openweathermap.org/api
/**
 * The useful link or some more explanation for this app you can checkout this link :
 * https://medium.com/@sasude9/basic-android-weather-app-6a7c0855caf4
 */
class MainActivity : AppCompatActivity() {

    // A fused location client variable which is further user to get the user's current location
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var binding: ActivityMainBinding? = null

    // A global variable for Progress Dialog
    private var mProgressDialog: Dialog? = null

    // TODO (STEP 5: Make the latitude and longitude variables global to use it in the menu item selection to refresh the data.)
    // START
    // A global variable for Current Latitude
    private var mLatitude: Double = 0.0
    // A global variable for Current Longitude
    private var mLongitude: Double = 0.0
    // END

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        // Initialize the Fused location variable
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        Toast.makeText(this@MainActivity,"Please Turn On the Light Mode. I'll create this application for dark mode also. But till now enjoy that.",Toast.LENGTH_LONG).show()

        if (!isLocationEnabled()) {
            Toast.makeText(
                this,
                "Your location provider is turned off. Please turn it on.",
                Toast.LENGTH_SHORT
            ).show()

            // This will redirect you to settings from where you need to turn on the location provider.
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } else {
            Dexter.withActivity(this)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            requestLocationData()
                        }

                        if (report.isAnyPermissionPermanentlyDenied) {
                            Toast.makeText(
                                this@MainActivity,
                                "You have denied location permission. Please allow it is mandatory.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread()
                .check()
        }
    }

    // TODO (STEP 4: Now add the override methods to load the menu file and perform the selection on item click.)
    // START
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main , menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // TODO (STEP 7: Now finally, make an api call on item selection.)
            // START
            R.id.action_refresh -> {
                getLocationWeatherDetails()
                true
            }
            else -> super.onOptionsItemSelected(item)
            // END
        }
    }
    // END

    /**
     * A function which is used to verify that the location or GPS is enable or not of the user's device.
     */
    private fun isLocationEnabled(): Boolean {

        // This provides access to the system location services.
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    /**
     * A function used to show the alert dialog when the permissions are denied and need to allow it from settings app info.
     */
    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog,
                                           _ ->
                dialog.dismiss()
            }.show()
    }

    /**
     * A function to request the current location. Using the fused location provider client.
     */
    @SuppressLint("MissingPermission")
    private fun requestLocationData() {

        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    /**
     * A location callback object of fused location provider client where we will get the current location details.
     */
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {

            val mLastLocation: Location = locationResult.lastLocation
            // TODO (STEP 6: Assign the values to the global variables here
            //  to use that for api calling. And remove the latitude and
            //  longitude from the parameter as we can directly use it while
            //  API calling.)
            // START
            mLatitude = mLastLocation.latitude
            Log.e("Current Latitude", "$mLatitude")
            mLongitude = mLastLocation.longitude
            Log.e("Current Longitude", "$mLongitude")
            // END

            getLocationWeatherDetails()
        }
    }

    /**
     * Function is used to get the weather details of the current location based on the latitude longitude
     */
    private fun getLocationWeatherDetails() {

        if (Constants.isNetworkAvailable(this@MainActivity)) {

            /**
             * Add the built-in converter factory first. This prevents overriding its
             * behavior but also ensures correct behavior when using converters that consume all types.
             */
            val retrofit: Retrofit = Retrofit.Builder()
                // API base URL.
                .baseUrl(Constants.BASE_URL)
                /** Add converter factory for serialization and deserialization of objects. */
                /**
                 * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
                 * decoding from JSON (when no charset is specified by a header) will use UTF-8.
                 */
                .addConverterFactory(GsonConverterFactory.create())
                /** Create the Retrofit instances. */
                .build()

            /**
             * Here we map the service interface in which we declares the end point and the API type
             *i.e GET, POST and so on along with the request parameter which are required.
             */
            val service: WeatherService =
                retrofit.create<WeatherService>(WeatherService::class.java)

            /** An invocation of a Retrofit method that sends a request to a web-server and returns a response.
             * Here we pass the required param in the service
             */
            val listCall: Call<WeatherResponse> = service.getweather (
                mLatitude, mLongitude, Constants.METRIC_UNIT, Constants.APP_ID
            )

            showCustomProgressDialog() // Used to show the progress dialog

            // Callback methods are executed using the Retrofit callback executor.
            listCall.enqueue(object : Callback<WeatherResponse> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    response: Response<WeatherResponse>,
                    retrofit: Retrofit
                ) {

                    // Check weather the response is success or not.
                    if (response.isSuccess) {

                        hideProgressDialog() // Hides the progress dialog

                        /** The de-serialized response body of a successful response. */
                        val weatherList: WeatherResponse = response.body()
                        Log.i("Response Result", "$weatherList")

                        setupUI(weatherList)
                    } else {
                        // If the response is not success then we check the response code.
                        val sc = response.code()
                        when (sc) {
                            400 -> {
                                Log.e("Error 400", "Bad Request")
                            }
                            404 -> {
                                Log.e("Error 404", "Not Found")
                            }
                            else -> {
                                Log.e("Error", "Generic Error")
                            }
                        }
                    }
                }

                override fun onFailure(t: Throwable) {
                    hideProgressDialog() // Hides the progress dialog
                    Log.e("Errorrrrr", t.message.toString())
                }
            })
        } else {
            Toast.makeText(
                this@MainActivity,
                "No internet connection available.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Method is used to show the Custom Progress Dialog.
     */
    private fun showCustomProgressDialog() {
        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog!!.setContentView(R.layout.dialog_custom_progress)

        //Start the dialog and display it on screen.
        mProgressDialog!!.show()
    }

    /**
     * This function is used to dismiss the progress dialog if it is visible to user.
     */
    private fun hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
    }

    /**
     * Function is used to set the result in the UI elements.
     */
    private fun setupUI(weatherList: WeatherResponse) {

        // For loop to get the required data. And all are populated in the UI.
        for (z in weatherList.weather.indices) {
            Log.i("NAMEEEEEEEE", weatherList.weather[z].main)

            binding?.tvMain?.text = weatherList.weather[z].main
            binding?.tvMainDescription?.text = weatherList.weather[z].description
            binding?.tvTemp?.text =
                weatherList.main.temp.toString() + getUnit(application.resources.configuration.locales.toString())
            binding?.tvHumidity?.text = weatherList.main.humidity.toString() + " per cent"
            binding?.tvMin?.text = weatherList.main.temp_min.toString() + " min"
            binding?.tvMax?.text = weatherList.main.temp_max.toString() + " max"
            binding?.tvSpeed?.text = weatherList.wind.speed.toString()
            binding?.tvName?.text = weatherList.name
            binding?.tvCountry?.text = weatherList.sys.country
            binding?.tvSunriseTime?.text = unixTime(weatherList.sys.sunrise.toLong())
            binding?.tvSunsetTime?.text = unixTime(weatherList.sys.sunset.toLong())

            // Here we update the main icon
            when (weatherList.weather[z].icon) {
                "01d" -> binding?.ivMain?.setImageResource(R.drawable.sunny)
                "02d" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                "03d" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                "04d" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                "04n" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                "10d" -> binding?.ivMain?.setImageResource(R.drawable.rain)
                "11d" -> binding?.ivMain?.setImageResource(R.drawable.storm)
                "13d" -> binding?.ivMain?.setImageResource(R.drawable.snowflake)
                "01n" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                "02n" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                "03n" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                "10n" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                "11n" -> binding?.ivMain?.setImageResource(R.drawable.rain)
                "13n" -> binding?.ivMain?.setImageResource(R.drawable.snowflake)
            }
        }
    }

    /**
     * Function is used to get the temperature unit value.
     */
    private fun getUnit(value: String): String? {
        Log.i("unitttttt", value)
        var value = "°C"
        if ("US" == value || "LR" == value || "MM" == value) {
            value = "°F"
        }
        return value
    }

    /**
     * The function is used to get the formatted time based on the Format and the LOCALE we pass to it.
     */
    private fun unixTime(timex: Long): String? {
        val date = Date(timex * 1000L)
        @SuppressLint("SimpleDateFormat") val sdf =
            SimpleDateFormat("HH:mm:ss")
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }
}