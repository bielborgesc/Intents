package br.edu.ifsp.ads.pdm.intents

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.ads.pdm.intents.Constants.URL
import br.edu.ifsp.scl.ads.pdm.intents.R
import br.edu.ifsp.scl.ads.pdm.intents.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val amb : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private  lateinit var urlArl: ActivityResultLauncher<Intent>
    private  lateinit var pegarImage: ActivityResultLauncher<Intent>
    private lateinit var persmissionChangeArl: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)
        supportActionBar?.subtitle = "MainActivity"

        urlArl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { resultado: ActivityResult ->
            if(resultado.resultCode == RESULT_OK){
                val urlRetornada = resultado.data?.getStringExtra(URL) ?: ""
                amb.urlTv.text = urlRetornada
            }
        }

        persmissionChangeArl = registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
            object: ActivityResultCallback<Boolean> {
                override fun onActivityResult(concedida: Boolean?) {
                    if( concedida!! ) {
                        chamarNumero(chamar = true)
                    } else {
                        Toast.makeText(this@MainActivity, "Permissão nescessária para a execução!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

            }
        )

        pegarImage = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { resultado: ActivityResult ->
            if(resultado.resultCode == RESULT_OK){
                val imageUri = resultado.data?.data
                imageUri?.let {
                    amb.urlTv.text = it.toString()
                }

//                Abrindo visuzalidor
                val visualizadorImagemIntent = Intent(Intent.ACTION_VIEW, imageUri)
                startActivity(visualizadorImagemIntent)
            }
        }

        amb.entrarUrlBt.setOnClickListener {
            val urlActivityIntent = Intent(this, UrlActivity::class.java)
            urlActivityIntent.putExtra(URL, amb.urlTv.text.toString())
            urlArl.launch(urlActivityIntent)
        }
    }

    //Coloca o menu da ActionBar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    //Trata das escolhas das opções de menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.viewMi -> {
                chamarNumero(chamar = false)
                true
            }
            R.id.callMi -> {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                        chamarNumero(chamar = true)
                        true
                    } else {
                        persmissionChangeArl.launch(Manifest.permission.CALL_PHONE)
                    }
                }
                true
            }
            R.id.pickMi -> {
                val pegarImagemIntent = Intent(Intent.ACTION_PICK)
                val diretorioImagens = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .path
                pegarImagemIntent.setDataAndType(Uri.parse((diretorioImagens)), "image/*")
                pegarImage.launch(pegarImagemIntent)
                true
            }
            R.id.chooserMi -> {
                val escolherAppIntent = Intent(Intent.ACTION_CHOOSER)
                val interfaceIntent = Intent(Intent.ACTION_VIEW, Uri.parse(amb.urlTv.text.toString()))
                escolherAppIntent.putExtra(
                    Intent.EXTRA_INTENT,
                    interfaceIntent
                )
                escolherAppIntent.putExtra(Intent.EXTRA_TITLE, "Escolha seu navegador")
                escolherAppIntent.putExtra(
                    Intent.EXTRA_INTENT,
                    interfaceIntent
                )
                startActivity(escolherAppIntent)
                true
            }
            else -> { false }
        }
    }

    private fun chamarNumero(chamar: Boolean){
        val uri = Uri.parse("tel:${amb.urlTv.text}")
        val intent = Intent(if (chamar) Intent.ACTION_CALL else Intent.ACTION_DIAL)
        intent.data = uri
        startActivity(intent)
    }

}