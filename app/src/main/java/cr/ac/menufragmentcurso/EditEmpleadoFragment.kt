package cr.ac.menufragmentcurso

import android.app.Activity.RESULT_OK
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
import android.widget.*
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
 * Use the [EditEmpleadoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditEmpleadoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var empleado: Empleado? = null
    lateinit var image_avatar : ImageView


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
        // Inflate the layout for this fragment
        var view:View= inflater.inflate(R.layout.fragment_edit_empleado, container, false)

        var nuevoId = view.findViewById<EditText>(R.id.modificarId)
        nuevoId.setText(empleado?.identificacion)

        var nuevoNombre = view.findViewById<EditText>(R.id.modificarNombre)
        nuevoNombre.setText(empleado?.nombre)

        var nuevoPuesto = view.findViewById<EditText>(R.id.modificarPuesto)
        nuevoPuesto.setText(empleado?.puesto)

        var nuevoDepartamento = view.findViewById<EditText>(R.id.modificarDepartamento)
        nuevoDepartamento.setText(empleado?.departamento)

        image_avatar = view.findViewById(R.id.avatar)

        view.findViewById<Button>(R.id.editar).setOnClickListener{
            val builder = AlertDialog.Builder(context)

            builder.setMessage("Desea modificar el usuario?")
                .setCancelable(false)
                .setPositiveButton("Si"){dialog,id->

                    val edit_id = view?.findViewById<EditText>(R.id.modificarId)
                    val edit_nombre = view?.findViewById<EditText>(R.id.modificarNombre)
                    val edit_puesto = view?.findViewById<EditText>(R.id.modificarPuesto)
                    val edit_departamento = view?.findViewById<EditText>(R.id.modificarDepartamento)

                    if (empleado?.avatar != ""){
                        image_avatar.setImageBitmap(empleado?.avatar?.let { it1 -> decodeImage(it1) })
                    }

                    //  Rellena los campos para editar
                    empleado?.identificacion = edit_id?.text.toString()
                    empleado?.nombre = edit_nombre?.text.toString()
                    empleado?.puesto = edit_puesto?.text.toString()
                    empleado?.departamento = edit_departamento?.text.toString()
                    empleado?.avatar = encodeImage(image_avatar.drawable.toBitmap())!!

                    empleado?.let { EmpleadoRepository.instance.edit(it) }

                    val fragment: Fragment = CameraFragment.newInstance(getString(R.string.menu_camera))
                    fragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.home_content, fragment)
                        ?.commit()
                    activity?.setTitle("Lista Usuarios")
                }

                .setNegativeButton("No"){dialog,id->


                }
            val alert = builder.create()
            alert.show()

        }


        view.findViewById<Button>(R.id.eliminar).setOnClickListener {

            val builder = AlertDialog.Builder(context)

            builder.setMessage("Desea eliminar el usuario?")
                .setCancelable(false)
                .setPositiveButton("Si") { dialog, id ->

                    empleado?.let { EmpleadoRepository.instance.borrar(it)}

                    val fragment: androidx.fragment.app.Fragment = CameraFragment.newInstance(getString(R.string.menu_camera))
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

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){

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
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    private fun decodeImage (b64 : String): Bitmap{
        val imageBytes = Base64.decode(b64, 0)
        return  BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param empleado Parameter 1.
         * @return A new instance of fragment EditEmpleadoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(empleado: Empleado) =
            EditEmpleadoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, empleado)
                }
            }
    }
}