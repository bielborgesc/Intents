package br.edu.ifsp.ads.pdm.intents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.edu.ifsp.ads.pdm.intents.Constants.URL
import br.edu.ifsp.scl.ads.pdm.intents.databinding.ActivityUrlBinding

class UrlActivity : AppCompatActivity() {

    private lateinit var aub: ActivityUrlBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        aub = ActivityUrlBinding.inflate(layoutInflater)
        setContentView(aub.root)
        supportActionBar?.subtitle = "UrlActivity"

        val urlAnterior = intent.getStringExtra(URL) ?: ""
        if (urlAnterior.isNotEmpty()){
            aub.urlEt.setText(urlAnterior)
        }

        aub.entrarUrlBt.setOnClickListener {
            val retornoIntent = Intent()
            retornoIntent.putExtra(URL, aub.urlEt.text.toString())
            setResult(RESULT_OK, retornoIntent)
            finish()
        }
    }
}