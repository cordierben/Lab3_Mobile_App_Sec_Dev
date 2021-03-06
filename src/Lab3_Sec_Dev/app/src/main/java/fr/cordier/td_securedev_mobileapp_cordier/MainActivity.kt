package fr.cordier.td_securedev_mobileapp_cordier

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.hardware.biometrics.BiometricPrompt
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {

    private val db: DatabaseManager = DatabaseManager(this)
    private var offline: Button? = null
    private var log: ImageView? = null
    private var logo:ImageView?=null
    private val crypto = Crypto()
    private var id: String = ""
    private var txtw: TextView? = null
    var authenticatebtn: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //We suppose the user logged in before, and that he has the id 1.
        id = "1"

        //Welcome message with name and last name
        txtw = findViewById(R.id.welcometxt)
        logo=findViewById(R.id.logo)

    }

    override fun onStart() {
        super.onStart()

        //Button to refresh accounts locally
        log = findViewById(R.id.log)
        log?.setOnClickListener {
            if (isOnline(this)) {
                val anim: Animation =
                    AnimationUtils.loadAnimation(applicationContext, R.anim.rotate)
                log?.startAnimation(anim)
                accountsData()
            } else {
                Toast.makeText(this, "You are not connected to internet", Toast.LENGTH_SHORT).show()
            }
        }

        //Button to access to the accounts (offline)
        offline = findViewById(R.id.offline)
        offline?.setOnClickListener {
            val db=DatabaseManager(this)
            if(db.selectAllAccount().isNotEmpty()){
                val intent = Intent(this, AccountsOff::class.java)
                startActivity(intent)
                finish()
            } else{
                Toast.makeText(this,"You don't have any accounts offline. Please press the refresh button first.",Toast.LENGTH_LONG).show()
            }
        }

        //Create the prompt for fingerprint auth
        authenticatebtn = findViewById(R.id.fingerprint)
        authenticatebtn?.setOnClickListener {
            //Check if the device can handle biometric
            if (checkBiometricSupport()) {
                val biometricPrompt = BiometricPrompt.Builder(this).setTitle("Authenticate").setSubtitle("Authentication is needed").
                    setNegativeButton("Cancel", this.mainExecutor, DialogInterface.OnClickListener { _, _ -> Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show() }).build()
                biometricPrompt.authenticate(getCancellationSignal(), mainExecutor, authenticationCallback
                )
            }
        }
    }

    //Method to fetch user (considering he has logged in before), to display a welcome message)
    private fun fetchUser() {
        runOnUiThread {
            if(isOnline(this)){
                val policy: StrictMode.ThreadPolicy =
                    StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)
                val url = URL(crypto.config + id)
                val inputAsString: String
                val urlConnection: HttpsURLConnection = url.openConnection() as HttpsURLConnection
                try {
                    val inp: InputStream = BufferedInputStream(urlConnection.inputStream)
                    inputAsString = inp.bufferedReader().use { it.readText() }
                    val user = JSONObject(inputAsString)
                    txtw?.append(user.getString("name") + " " + user.getString("lastname"))
                } finally {
                    urlConnection.disconnect()
                }
            }
        }
    }

    //Method to retrieve data from the accounts
    fun accountsData() {
        val url = URL(crypto.accounts)
        val inputAsString: String
        val urlConnection: HttpsURLConnection = url.openConnection() as HttpsURLConnection
        try {
            val inp: InputStream = BufferedInputStream(urlConnection.inputStream)
            inputAsString = inp.bufferedReader().use { it.readText() }
        } finally {
            urlConnection.disconnect()
        }
        updateOff(inputAsString)
    }

    //Method to update local database with the data from the accounts, to get an offline access
    private fun updateOff(dataStr: String) {
        val data = JSONArray(dataStr)
        for (i in 0 until data.length()) {
            val elem = data.getJSONObject(i)
            val id = elem.getInt("id")
            val exist: String = db.selectAccount(id)
            if (exist.isEmpty()) {
                val name = elem.getString("accountName")
                val amount: Float = elem.getDouble("amount").toFloat()
                val iban = elem.getString("iban")
                val currency = elem.getString("currency")
                db.insertAccount(id, name, amount, iban, currency)
            }
        }
    }


    //Method to check if the user is connected to Internet
    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    //Methods concerning fingerprint authentification
    private var cancellationSignal: CancellationSignal? = null
    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
        get() = @RequiresApi(Build.VERSION_CODES.P) object :
            BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(applicationContext, errString, Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                val txticon: TextView = findViewById(R.id.icontxt)
                txticon.visibility = View.VISIBLE
                offline?.visibility = View.VISIBLE
                txtw?.visibility = View.VISIBLE
                log?.visibility = View.VISIBLE
                val txtf: TextView = findViewById(R.id.textFingerprint)
                txtf.visibility = View.INVISIBLE
                authenticatebtn?.visibility = View.INVISIBLE
                logo?.visibility=View.INVISIBLE
                fetchUser()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.d("******", "Fingerprint not recognised")
            }
        }

    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
        }
        return cancellationSignal as CancellationSignal
    }

    private fun checkBiometricSupport(): Boolean {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL or BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS ->{
                return true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->{
                Toast.makeText(this,"No biometric features available on this device. For security reason, we don't allow other authentification system",Toast.LENGTH_LONG).show()
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->{
                Toast.makeText(this,"Biometric features are currently unavailable.",Toast.LENGTH_SHORT).show()
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Prompts the user to create credentials that your app accepts.
                Toast.makeText(this,"Please enroll at least one fingerprint in Settings.",Toast.LENGTH_SHORT).show()
                return false
            }
        }
        Toast.makeText(this,"An error occurred. For security reason, we don't allow other authentification system",Toast.LENGTH_SHORT).show()
        return false
    }
}



