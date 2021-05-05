package quiroga.humberto.misnotas

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.io.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private var listaNotas = ArrayList<Nota>();
    private var adapter: AdapterNote?=null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        leerNotas();

        var listaView:ListView = this.findViewById(R.id.listView_notas) as ListView;
        var boton_AgregarNota:com.google.android.material.floatingactionbutton.FloatingActionButton =  this.findViewById(R.id.boton_addNote) as com.google.android.material.floatingactionbutton.FloatingActionButton;

        this.adapter = AdapterNote(this, listaNotas);
        listaView.adapter = adapter;

        boton_AgregarNota.setOnClickListener {
            var intent:Intent = Intent(this, AgregarNotaActivity::class.java);
            startActivityForResult(intent, 123);
        }

    }



    fun leerNotas(){
        listaNotas.clear();
        var carpeta = File(ubicacion().absolutePath);

        if(carpeta.exists()){
            var archivos = carpeta.listFiles();
            if(archivos != null){
                for (archivo in archivos){
                    leerArchivo(archivo);
                }
            }
        }
    }

    private fun leerArchivo(archivo: File){
        val fis = FileInputStream(archivo);
        val di = DataInputStream(fis);
        val br = BufferedReader(InputStreamReader(di));
        var strLine:String? = br.readLine();
        var myData = "";

        while (strLine != null){
            myData = myData +" \n"+ strLine;
            strLine = br.readLine();
        }
        br.close();
        di.close();
        fis.close();

        var nombre = archivo.name.substring(0, archivo.name.length-4);
        var nota = Nota(nombre, myData);
        listaNotas.add(nota);
    }

    private fun ubicacion():File{
        val carpeta = File(getExternalFilesDir(null), "notas");
        if(!carpeta.exists()){carpeta.mkdir();}
        return carpeta;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 123){
            leerNotas();
            adapter?.notifyDataSetChanged();
        }
    }

    private class AdapterNote:BaseAdapter{
        var context: Context
        var listaNotas = ArrayList<Nota>();

        constructor(context: Context, listaNotas: ArrayList<Nota>){
            this.context = context;
            this.listaNotas = listaNotas;
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var inflator = LayoutInflater.from(context);
            var vista = inflator.inflate(R.layout.nota_layout, null);
            var nota = listaNotas[position];

            var titulo:TextView = vista.findViewById(R.id.textView_tituloNota) as TextView;
            var contenido:TextView = vista.findViewById(R.id.textView_contenidoNota) as TextView;

            var boton_borrar:ImageView = vista.findViewById(R.id.imgaeView_borrarNota) as ImageView;

            boton_borrar.setOnClickListener {
                eliminar(nota.titulo);
                listaNotas.remove(nota);
                this.notifyDataSetChanged();
            }

            titulo.setText(nota.titulo);
            contenido.setText(nota.contenido);

            return vista;
        }

        private fun eliminar(titulo:String){
            if(titulo == ""){Toast.makeText(context, "Error: Titulo vacio!", Toast.LENGTH_SHORT).show();}
            else{
                try {
                    var archivo = File(ubicacion(), titulo + ".txt");
                    archivo.delete();
                    Toast.makeText(context, "Se elimino correctamente le archivo!", Toast.LENGTH_SHORT).show();
                }catch (e:Exception){Toast.makeText(context, "Error al eliminar el archivo!", Toast.LENGTH_SHORT).show()}
            }
        }

        private fun ubicacion(): String{
            val carpeta = File(context?.getExternalFilesDir(null), "notas");
            if(!carpeta.exists()){
                carpeta.mkdir();
            }
            return carpeta.absolutePath;
        }

        override fun getItem(position: Int): Any {
            return  listaNotas.get(position);
        }

        override fun getItemId(position: Int): Long {
            return position.toLong();
        }

        override fun getCount(): Int {
            return listaNotas.size;
        }

    }
}