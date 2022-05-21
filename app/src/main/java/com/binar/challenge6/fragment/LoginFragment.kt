package com.binar.chapter5.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.UserManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.binar.challenge6.databinding.FragmentLoginBinding
import com.binar.chapter5.database.createDB.UserDatabase


import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    lateinit var userManager : com.binar.chapter5.data_store.UserManager
    private var user_db : UserDatabase?= null
    private lateinit var binding : FragmentLoginBinding
    private val sharedPref = "sharedpreferences"
    lateinit var username : String
    var isLoggedIn : Boolean = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userManager = com.binar.chapter5.data_store.UserManager(requireContext())
        user_db = UserDatabase.getInstance(requireContext())
        binding.apply {
            binding.btnLogin.setOnClickListener {
                checkLogin()
             }
            binding.btnRegister.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
            }
        }
    }

    private fun saveUsername() {
         username = binding.etUsername.text.toString()
         isLoggedIn = true

        GlobalScope.launch {
            userManager.storePrefs(username,isLoggedIn)
        }
    }

    private fun observeData(){

        userManager.userNameFlow.asLiveData().observe(requireActivity(),{
            username = it
        })
        userManager.isLoggedInFlow.asLiveData().observe(requireActivity(),{
            isLoggedIn = it
        })
     }

    private fun checkLogin() {

        var username = "";
        var password = "";

        username = binding.etUsername.text.toString()
        password = binding.etPassword.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Silahkan isi username dan password anda",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Thread {
                val result = user_db?.UserDao()?.login(username, password)
                activity?.runOnUiThread {
                    if (result != null) {
                        saveUsername()
                        observeData()
                        Toast.makeText(
                            requireContext(),
                            "Login berhasil",
                            Toast.LENGTH_LONG
                        ).show()
                        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMainFragment())
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Gagal Login ",
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
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root }

}