package com.binar.chapter5.fragment

import android.content.Context
import android.content.SharedPreferences

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.binar.challenge6.R
import com.binar.challenge6.databinding.FragmentMainBinding

import com.binar.chapter5.adapter.MainAdapter
import com.binar.chapter5.database.createDB.UserDatabase

import com.binar.chapter5.view_model.SecondViewModel

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class MainFragment : Fragment() {

    private lateinit var binding : FragmentMainBinding
    private val sharedPref = "sharedpreferences"
    private val movieViewModel : SecondViewModel by viewModels()
    private var user_db : UserDatabase? = null
    lateinit var userManager : com.binar.chapter5.data_store.UserManager
    lateinit var images : String
    lateinit var username : String
    var isLoggedIn : Boolean = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences : SharedPreferences = requireActivity().getSharedPreferences(sharedPref, Context.MODE_PRIVATE)
        user_db = UserDatabase.getInstance(requireContext())
        userManager = com.binar.chapter5.data_store.UserManager(requireContext())
        images = sharedPreferences.getString("images","null").toString()
        getPhoto()


        binding.apply {
            binding.ivProfile.setOnClickListener {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToProfileFragment())
            }


        }

        movieViewModel.getMovies().observe(requireActivity()){
            if(it==null){
                binding.progressBar.visibility = View.VISIBLE
            }else{
                binding.progressBar.visibility = View.INVISIBLE
            }
            Log.d("Tag","Fragment activity : datanya -> $it")
            val adapter = MainAdapter(it)
            val layoutManager = GridLayoutManager(requireContext(),2)
            binding.rvMain.layoutManager = layoutManager
            binding.rvMain.adapter = adapter
        }
    }

    private fun observeData() {


    }

    private fun getPhoto() {
        userManager.userNameFlow.asLiveData().observe(requireActivity(),{
            username = it
            binding.tvUsername.setText("Welcome, $username")

            Thread{
                val result = user_db?.UserDao()?.getPhotoProfile(it)
                activity?.runOnUiThread {
                    if(result!=null) {
                        Glide.with(requireActivity())
                            .load(result)
                            .apply(RequestOptions.centerCropTransform())
                            .error(R.drawable.ic_baseline_profile_24)
                            .into(binding.ivProfile)
                        images = result
                    }
                    else{
                        Toast.makeText(
                            requireContext(),
                            "Failed to load image",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }.start()
        })
        userManager.isLoggedInFlow.asLiveData().observe(requireActivity(),{
            isLoggedIn = it
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater,container,false)
        return binding.root }

}