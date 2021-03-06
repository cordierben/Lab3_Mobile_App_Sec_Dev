package fr.cordier.td_securedev_mobileapp_cordier

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.InputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.spec.AlgorithmParameterSpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.net.ssl.HttpsURLConnection


class AddAccount : AppCompatActivity() {

    private val db=DatabaseManager(this)
    private val crypto= Crypto()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_account)


    }

    override fun onStart(){
        super.onStart()
        val editName:EditText=findViewById(R.id.editName)
        val editAmount:EditText=findViewById(R.id.editAmount)
        val editIBAN:EditText=findViewById(R.id.editIBAN)
        val editCurrency:EditText=findViewById(R.id.editCurrency)

        val create: Button =findViewById(R.id.CreateAccount)
        create.setOnClickListener {
            if(isOnline(this)){
                val currency=editCurrency.text.toString()
                if(validCurrency(currency)){

                    val name:String=editName.text.toString()
                    val amount:String=editAmount.text.toString()
                    val iban=editIBAN.text.toString()

                    //Send the data to MockAPI
                    sendData(currency,name,amount,iban)

                    //Then update accounts
                    accountsData()

                    //Go back to Accounts page
                    val intent = Intent(this, AccountsOff::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this,"The currency is not valid",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"You are not connected to Internet",Toast.LENGTH_SHORT).show()
            }
        }

        val cancel: Button =findViewById(R.id.CancelAccount)
        cancel.setOnClickListener {
            val intent = Intent(this, AccountsOff::class.java)
            startActivity(intent)
        }
    }

    //Check is the currency is a valid one
    private fun validCurrency(currency:String):Boolean{
        val cur=Currency.getAvailableCurrencies()
        for(currencies in cur){
            if(currency.equals(currencies.symbol)) return true
        }
        return false
    }

    //Method to retrieve data from the accounts
    private fun accountsData(){
        val url=URL(crypto.accounts)
        val inputAsString:String
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
    private fun updateOff(dataStr:String){
        val data= JSONArray(dataStr)
        for(i in 0 until data.length()){
            val elem=data.getJSONObject(i)
            val id=elem.getInt("id")
            val exist:String=db.selectAccount(id)
            if(exist.isEmpty()){
                val name:String=elem.getString("account_name")
                val amount:Float=elem.getDouble("amount").toFloat()
                val iban=elem.getString("IBAN")
                val currency=elem.getString("currency")
                db.insertAccount(id,name,amount,iban,currency)
            }
        }
    }

    //Method to send data to MockAPI
    private fun sendData(currency:String,name:String,amount:String,iban:String){
        val url = URL(crypto.accounts+"/")
        val con = url.openConnection()
        val https = con as HttpsURLConnection
        https.requestMethod = "POST" // PUT is another valid option
        https.doOutput = true
        val out = "{\"account_name\":\"$name\",\"amount\":\"$amount\",\"IBAN\":\"$iban\",\"currency\":\"$currency\"}".toByteArray(StandardCharsets.UTF_8)
        val length = out.size
        https.setFixedLengthStreamingMode(length)
        https.setRequestProperty("Content-Type", "application/json")
        https.connect()
        https.outputStream.use{ os -> os.write(out) }
        https.inputStream
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

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, AccountsOff::class.java)
        startActivity(intent)
    }

    //Close the app when leaving it
    override fun onStop() {
        super.onStop()
        finish()
    }
}
