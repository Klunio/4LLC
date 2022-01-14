package com.dartmouth.moonshot.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dartmouth.moonshot.*
import com.dartmouth.moonshot.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private lateinit var buttonSignOut: Button
    private lateinit var buttonUpdateName: Button
    private lateinit var edittextName: EditText
    private lateinit var edittextBio: EditText
    //private lateinit var textviewName: TextView
    //private lateinit var storageReference: StorageReference
    private lateinit var imageUri: Uri
    private lateinit var pImageView: ImageView
    private lateinit var tempImgUri: Uri
    private val tempImgFileName = "uttam_temp_img.jpg"
    lateinit var tempImgFile: File
    var changeImage = 0
    var opDiag = 0


    private lateinit var profileViewModel: ProfileViewModel
    //private lateinit var coinViewModel: CoinViewModel

    private var mFirebaseAuth: FirebaseAuth? = null
    private var firestoreDB = Firebase.firestore

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mFirebaseAuth = FirebaseAuth.getInstance()
        //profileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        profileViewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(ProfileViewModel::class.java)

        /*coinViewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(CoinViewModel::class.java)*/

        Util.checkPermissions(this.activity)
        imageUri = Uri.parse("android.resource://com.dartmouth.moonshot/${R.drawable.default_profile}")

        tempImgFile = File(activity?.getExternalFilesDir(null), tempImgFileName)
        tempImgUri = FileProvider.getUriForFile(requireContext(), "com.dartmouth.moonshot", tempImgFile)

        edittextName = root.findViewById(R.id.edittext_name)
        edittextBio = root.findViewById(R.id.edittext_bio)
        //textviewName = root.findViewById(R.id.text_home)

        pImageView = binding.imgProfile
        pImageView.setOnClickListener{
            opDiag = 1
            onAlertDialogue()
        }

        profileViewModel.getUser().observe(viewLifecycleOwner, Observer { userModel ->
            binding.userModel = userModel

            edittextName.setText(userModel.name.toString())
            edittextBio.setText(userModel.bio.toString())

        })

        /*coinViewModel.getAllCoins().observe(viewLifecycleOwner, Observer {
            println(it.toString())
        })*/




        buttonSignOut = root.findViewById(R.id.button_signout)
        buttonSignOut.setOnClickListener(){
                opDiag = 0
                signOutUser()
            }

        buttonUpdateName = root.findViewById(R.id.button_update_name)
        buttonUpdateName.setOnClickListener(){
            profileViewModel.updateName(edittextName.text.toString())
            profileViewModel.updateBio(edittextBio.text.toString())
            profileViewModel.updateImage(imageUri, changeImage)
            //imageUri = Uri.parse("")
            //profileViewModel.updateSavedCoins(arrayListOf("",""))
            //updateName()
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        if(opDiag != 1) {
            //mFirebaseAuth = FirebaseAuth.getInstance()

            profileViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                    .create(ProfileViewModel::class.java)
            profileViewModel.getUser().observe(viewLifecycleOwner, Observer { userModel ->
                binding.userModel = userModel

                edittextName.setText(userModel.name.toString())
                edittextBio.setText(userModel.bio.toString())

            })
        }
        //mFirebaseAuth = FirebaseAuth.getInstance()

        // Get saved name
        /*!if(mFirebaseAuth != null) {
            if(mFirebaseAuth!!.currentUser != null) {
                val docRef =
                    firestoreDB.collection("users").document(mFirebaseAuth!!.currentUser!!.uid)
                docRef.get().addOnSuccessListener {
                    val newName = it["name"].toString()
                    textviewName.text = newName
                }
            }
        }*/
    }

    // I learned from here https://developer.android.com/guide/topics/ui/dialogs and didn't
    // need the dialog, id
    fun onAlertDialogue(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select profile image")
        builder.setPositiveButton("Camera"){
                _, _ -> onChangePhotoClicked()
        }
        builder.setNegativeButton("Gallery"){
                _, _ -> galleryClicked()
        }
        builder.show()
    }


    fun galleryClicked(){
        val galintent: Intent = Intent(Intent.ACTION_PICK)
        galintent.type = "image/*"
        gactivityResultLauncher.launch(galintent)
    }

    val gactivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
            result: ActivityResult ->
        changeImage = 1
        val bitmap = result.data?.data?.let { Util.getBitmap(requireContext(), it) }
        //Toast.makeText(this.activity, result.data?.data.toString(), Toast.LENGTH_SHORT).show()
        if(result.data?.data != null){
            pImageView.setImageBitmap(bitmap)
            //opDiag = 0
            //need to do data.data bc result.data is an intent
            imageUri = result.data?.data as Uri
        }
    }


    //used the camera demo as a starting point for this
    fun onChangePhotoClicked(){
        val camintent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        camintent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri)
        activityResultLauncher.launch(camintent)
    }

    val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
            result: ActivityResult ->
        changeImage = 1
        val bitmap = Util.getBitmap(requireContext(), tempImgUri)
        pImageView.setImageBitmap(bitmap)
        //opDiag = 0
        imageUri = tempImgUri
    }

    /*fun updateName(){
        val newName = edittextName.text
        // Create hash map with newName
        val doc = firestoreDB.collection("users").document(mFirebaseAuth!!.currentUser!!.uid)
        val updates = hashMapOf<String, Any>(
            "name" to newName.toString()
        )
        doc.set(updates)
        textviewName.text = newName
    }*/

    fun signOutUser(){
        mFirebaseAuth!!.signOut()
        logIn()
    }

    fun logIn(){
        val intent = Intent(requireActivity(), LogInActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}