package quiroga.humberto.misnotas

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class AgregarNotaActivity : AppCompatActivity() {

    private var editText_titulo:EditText?=null;
    private var editText_contenido:EditText?=null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_nota)

        var boton_guardar:Button = this.findViewById(R.id.boton_guardarNota) as Button;
        editText_titulo = this.findViewById(R.id.editText_titulo) as EditText;
        editText_contenido = this.findViewById(R.id.editText_contenidoNota) as EditText;

        boton_guardar.setOnClickListener {
            guardar_nota();
        }
    }

    public fun guardar(){
        var titulo = editText_titulo?.text.toString();
        var contenido = editText_contenido?.text.toString();

        if(titulo == "" || contenido == ""){Toast.makeText(this, "No campos vacios", Toast.LENGTH_SHORT).show()}
        else{
            try{
                val archivo = File(ubicacion(), titulo + ".txt")
                val fos = FileOutputStream(archivo);
                fos.write(contenido.toByteArray());
                fos.close();
                Toast.makeText(this, "Se guardo correctamente el archivo!", Toast.LENGTH_SHORT).show()
            }catch (e:Exception){Toast.makeText(this, "Error: No se pudo guardar", Toast.LENGTH_SHORT).show()}
        }
        finish();
    }

    private fun ubicacion():String{
        val carpeta = File(getExternalFilesDir(null), "notas");
        if(!carpeta.exists()){carpeta.mkdir();}
        return carpeta.absolutePath;
    }

    fun guardar_nota(){
         if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
             ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 235);
         }else{
           this.guardar()
         }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            235 -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    this.guardar()
                }
                else{Toast.makeText(this, "Error: Permisos denegados!", Toast.LENGTH_SHORT).show()}
            }
        }
    }
}