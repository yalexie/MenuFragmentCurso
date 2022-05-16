package cr.ac.menufragmentcurso

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.squareup.picasso.Picasso
import cr.ac.menufragmentcurso.entity.Empleado
import cr.ac.menufragmentcurso.repository.EmpleadoRepository
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val PICK_IMAGE = 100

/**
 * A simple [Fragment] subclass.
 * Use the [AddEmpleadoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddEmpleadoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var empleado: Empleado? = null
    lateinit var image_avatar : ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            empleado = it.get(ARG_PARAM1) as Empleado?

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view:View= inflater.inflate(R.layout.fragment_add_empleado, container, false)


        view.findViewById<Button>(R.id.agregar).setOnClickListener {

            val builder = AlertDialog.Builder(context)

            builder.setMessage("Desea agregar el usuario?")
                .setCancelable(false)
                .setPositiveButton("Si") { dialog, id ->

                    val add_id = view.findViewById<EditText>(R.id.agregarId).text.toString()
                    val add_nombre = view.findViewById<EditText>(R.id.agregarNombre).text.toString()
                    val add_puesto = view.findViewById<EditText>(R.id.agregarPuesto).text.toString()
                    val add_departamento = view.findViewById<EditText>(R.id.agregarDepartamento).text.toString()

                    image_avatar = view.findViewById(R.id.avatar)

                    //if (empleado?.avatar != ""){
                        //image_avatar.setImageBitmap(empleado?.avatar?.let { it1 -> decodeImage(it1) })
                    //}
                    var idEmpleado : Int = EmpleadoRepository.instance.data().size+1

                    //  Se crea la instancia del Empleado con cada uno de los parametros
                    //var empleado : Empleado = Empleado (0, add_id?.text.toString(), add_nombre?.text.toString(), add_puesto?.text.toString(), add_departamento
                     //   ?.text.toString(), "")
                    //  Guarda al empleado
                    val empleado:Empleado= Empleado(idEmpleado,add_id,add_nombre,add_puesto,add_departamento,"" )
                    empleado?.let { EmpleadoRepository.instance.crear(it)}

                    val fragment: Fragment = CameraFragment.newInstance(getString(R.string.menu_camera))
                    fragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.home_content, fragment)
                        ?.commit()
                    activity?.setTitle("Lista Usuarios")
                }
                .setNegativeButton("No") { dialog, id ->

                }

            val alert = builder.create()
            alert.show()
        }

        image_avatar.setOnClickListener{
            var gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, PICK_IMAGE)

        }


        // Inflate the layout for this fragment
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK){

            var imageUri = data?.data

            Picasso.get()
                .load(imageUri)
                .resize(120,120)
                .centerCrop()
                .into(image_avatar)

        }
    }

    private fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT).replace("\n","")
    }

    private fun decodeImage (b64 : String): Bitmap {
        val imageBytes = Base64.decode(b64, 0)
        return  BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddEmpleadoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            AddEmpleadoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)

                }
            }
    }
}