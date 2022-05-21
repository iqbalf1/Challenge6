package com.binar.chapter5.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.binar.challenge6.databinding.FragmentRegisterBinding
import com.binar.chapter5.database.createDB.UserDatabase
import com.binar.chapter5.database.modelDB.User

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class RegisterFragment : Fragment() {


    private var user_db: UserDatabase? = null
    private val REQUEST_CODE_PERMISSION = 100
    lateinit var binding: FragmentRegisterBinding
    lateinit var resultImage: String;
    private val sharedPref = "sharedpreferences"
    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            Glide.with(requireActivity())
                .load(result)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.ivImage)
            resultImage = result.toString();
            print(resultImage)
            Log.d("Check", resultImage)
        }

    private val cameraResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                handleCameraImage(result.data)
                Log.d("Result Camera", "in If Result Camera" + result)
            } else {
                Log.d("Result Camera", "Failed result Camera" + result)
            }
            Log.d("Result Camera", "Result Camera" + result)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user_db = UserDatabase.getInstance(requireContext())

        binding.apply {
            binding.addImage.setOnClickListener {
                checkingPermissions()
            }
            binding.btnRegister.setOnClickListener {
                checkRegister()
            }
        }


    }

    private fun checkRegister() {
        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences(sharedPref, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        var username = ""
        var email = ""
        var images = ""
        var password = ""
        var repassword = ""

        username = binding.etUsername.text.toString()
        email = binding.etEmail.text.toString()
        password = binding.etPassword.text.toString()
        repassword = binding.etConfirmPassword.text.toString()
        images = resultImage


        val userList = User(
            null,
            username,
            email,
            images,
            password,
            repassword
        )

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || repassword.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Silahkan isi kolom terlebih dahulu",
                Toast.LENGTH_LONG
            ).show()
        } else if (password != repassword) {
            binding.etConfirmPassword.setError("Password is not same!")
        } else {
            Thread {
                val result = user_db?.UserDao()?.insertUser(userList)
                activity?.runOnUiThread {
                    if (result != 0.toLong()) {
                        Toast.makeText(
                            requireContext(),
                            "Sukses Menambahkan ${userList.username}",
                            Toast.LENGTH_LONG
                        ).show()
                        editor.putString("images", images)
                        editor.apply()
                        findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Gagal menambahkan ${userList.username}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }.start()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun checkingPermissions() {
        if (isGranted(
                requireActivity(), android.Manifest.permission.CAMERA, arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE_PERMISSION
            )
        ) {
            chooseImageDialog()

        }
    }

    private fun isGranted(
        activity: Activity,
        permission: String,
        permissions: Array<String>,
        request: Int, ): Boolean {
        val permissionCheck = ActivityCompat.checkSelfPermission(activity, permission)
        return if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(activity, permissions, request)
            }
            false
        } else {
            true
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireActivity())
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, please allow permissions from App Settings.")
            .setPositiveButton("App Settings") { _, _ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", activity?.packageName, null)
                intent.data = uri
                startActivity(intent)
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }.show()
    }

    private fun chooseImageDialog() {
        AlertDialog.Builder(requireActivity())
            .setMessage("Pilih Gambar")
            .setPositiveButton("Gallery") { _, _ ->
                openGallery()
            }.setNegativeButton("Camera"){
                _,_-> openCamera()
            }.show()
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        galleryResult.launch("image/*")
    }

    private fun handleCameraImage(intent: Intent?) {
        val bitmap = intent?.extras?.get("data") as Bitmap
        val drawable: Drawable = BitmapDrawable(resources, bitmap)
        saveImageToInternalStorage(bitmap)
        resultImage = saveImageToInternalStorage(bitmap).toString()
        Log.d("Handle Camera Image", "Result Camera" + resultImage)
        Glide.with(requireActivity())
            .load(drawable)
            .apply(RequestOptions.centerCropTransform())
            .into(binding.ivImage)
        Log.d("Save Image", "Result Camera" +saveImageToInternalStorage(bitmap))
//        binding.ivImage.setImageBitmap(bitmap)
    }


    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraResult.launch(cameraIntent)
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(requireContext())
        var file = wrapper.getDir("RegisterUserImage", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }

}