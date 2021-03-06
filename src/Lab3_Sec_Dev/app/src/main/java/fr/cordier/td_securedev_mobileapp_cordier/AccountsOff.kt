package fr.cordier.td_securedev_mobileapp_cordier

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.drawable.GradientDrawable
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec


class AccountsOff : AppCompatActivity() {

    var linear:LinearLayout?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts_off)

        //Init add account button
        val add: Button =findViewById(R.id.AddAccount)
        add.setOnClickListener{
            val intent = Intent(this, AddAccount::class.java)
            startActivity(intent)
            finish()
        }

        //Init back account button
        val menu: Button =findViewById(R.id.menu)
        menu.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        linear=findViewById(R.id.linearAccountOff)
    }

    override fun onStart(){
        super.onStart()
        val db= DatabaseManager(this)
        val accounts=db.selectAllAccount()
        for(i in 0 until accounts.size step 5){

            val child=LinearLayout(this)
            val txt=TextView(this)
            val border = GradientDrawable()
            border.setStroke(2, Color.parseColor("#1471F0"))
            child.setBackground(border)
            txt.text=accounts.get(i+1)+"\n"+accounts.get(i+2)+" "+accounts.get(i+4)+"\nIBAN : "+accounts.get(i+3)
            txt.setBackgroundColor(Color.parseColor("#F6FCFF"))
            txt.setPadding(10,10,5,10)
            txt.textSize=20f
            var txtParams : LinearLayout.LayoutParams = LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            txtParams.setMargins(30,30,30,30)
            txt.layoutParams=txtParams
            child.addView(txt)
            linear?.addView(child)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    //Close the app when leaving it
    override fun onStop() {
        super.onStop()
        finish()
    }
}
