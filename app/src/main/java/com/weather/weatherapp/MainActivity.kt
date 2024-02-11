package com.weather.weatherapp

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
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import com.google.android.material.navigation.NavigationView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.weather.Network.WeatherService
import com.weather.models.WeatherResponse
import retrofit.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    // A fused location client variable which is further user to get the user's current location
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var binding: ActivityMainBinding? = null

    private var mProgressDialog: Dialog? = null
    private lateinit var navigationView: NavigationView
    private lateinit var drawerlayout: DrawerLayout

    private lateinit var toolbar: Toolbar

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

        Toast.makeText(
            this@MainActivity,
            "Please Turn On the Light Mode. I'll create this application for dark mode also. But till now enjoy that.",
            Toast.LENGTH_LONG
        ).show()

        val currentDateTime = getCurrentDateTimeIn12HourFormatWithDay()
        binding?.timeDate?.text = currentDateTime

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


        // For navigation Bar
        drawerlayout = findViewById(R.id.menu_drawer)
        navigationView = findViewById(R.id.navigation_menu)
        toolbar = findViewById(R.id.menuButton)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu)
            actionBar.setTitle(0)
        }

        val toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawerlayout, toolbar, R.string.navigation_open, R.string.navigation_close
        )

        navigationView = findViewById<View>(R.id.navigation_menu) as NavigationView
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.feed -> closeDrawer()
                R.id.aboutUs -> aboutUs()

                R.id.reportBug -> ReportBug()
                R.id.ic_feedback -> feedback()
                R.id.ic_share -> share()
                R.id.ic_privacy -> privacy()
                R.id.ic_open -> Open_souce()
            }
            closeDrawer()
            false
        }
    }


    private fun privacy(){
        val intent = Intent(this@MainActivity, privacy_policy_page::class.java)
        startActivity(intent)
    }

    private fun Open_souce(){
        val intent = Intent(this@MainActivity, open_source_page::class.java)
        startActivity(intent)
    }




    private fun feedback(){
        val recipient = getString(R.string.email)
        val subject = "FeeBack : Weather Application"
        val message = getString(R.string.email_body)

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }
        startActivity(intent)
    }

    private fun share(){
        var body = "Let's check this application - Weather App. " +
                "This gives the best weather in fetching your location"
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, body)
        startActivity(
            Intent.createChooser(
                shareIntent,
                getString(R.string.share_title)
            )
        )
    }


    private fun ReportBug() {
        val recipient = getString(R.string.email)
        val subject = getString(R.string.subject_for_email)
        val message = getString(R.string.email_body)

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }
        startActivity(intent)
    }


    private fun aboutUs() {
        val intent = Intent(this@MainActivity, AboutUs::class.java)
        startActivity(intent)
    }

    // TODO (STEP 4: Now add the override methods to load the menu file and perform the selection on item click.)
    // START
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun closeDrawer() {
        val drawer = findViewById<DrawerLayout>(R.id.menu_drawer)
        drawer.closeDrawer(GravityCompat.START)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                getLocationWeatherDetails()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * A function which is used to verify that the location or GPS is enable or not of the user's device.
     */
    private fun isLocationEnabled(): Boolean {
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

            val mLastLocation: Location = locationResult.lastLocation!!
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

            val service: WeatherService =
                retrofit.create<WeatherService>(WeatherService::class.java)

            /** An invocation of a Retrofit method that sends a request to a web-server and returns a response.
             * Here we pass the required param in the service
             */
            val listCall: Call<WeatherResponse> = service.getweather(
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
                "11n" -> binding?.ivMain?.setImageResource(R.drawable.evening)
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

    private fun getCurrentDateTimeIn12HourFormatWithDay(): String {
        val dateFormat = SimpleDateFormat("EEEE, dd-MM-yyyy\nhh:mm:ss a", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Kolkata") // Set time zone to Indian Standard Time (IST)
        val currentDateAndTime: String = dateFormat.format(Date())
        return currentDateAndTime
    }

    override fun onBackPressed() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Exit App")
        alertDialogBuilder.setMessage("Are you sure you want to exit the app?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            super.onBackPressed()
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}