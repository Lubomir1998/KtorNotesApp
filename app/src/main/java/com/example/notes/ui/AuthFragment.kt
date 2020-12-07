package com.example.notes.ui

import android.content.SharedPreferences
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.room.Index
import com.example.notes.R
import com.example.notes.data.remote.BasicAuthInterceptor
import com.example.notes.databinding.FragmentAuthBinding
import com.example.notes.other.Status
import com.example.notes.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment: BaseFragment(R.layout.fragment_auth) {

    private lateinit var binding: FragmentAuthBinding
    private val model: AuthViewModel by viewModels()

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    @Inject
    lateinit var baseAuthInterceptor: BasicAuthInterceptor

    private var currentEmail: String? = null
    private var currentPassword: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        requireActivity().requestedOrientation = SCREEN_ORIENTATION_PORTRAIT

        subscribeToObservers()

        binding.btnRegister.setOnClickListener {
            val email = binding.etRegisterEmail.text.toString()
            val password = binding.etRegisterPassword.text.toString()
            val confirmedPassword = binding.etRegisterPasswordConfirm.text.toString()

            model.registerUser(email, password, confirmedPassword)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etLoginEmail.text.toString()
            val password = binding.etLoginPassword.text.toString()

            currentEmail = email
            currentPassword = password

            model.loginUser(email, password)

        }




    }





    private fun redirectLogin() {
        val navOptions = NavOptions.Builder().setPopUpTo(R.id.authFragment, true).build()

        findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToNotesFragment(), navOptions)
    }


    private fun authenticateApi(email: String, password: String) {
        baseAuthInterceptor.email = email
        baseAuthInterceptor.password = password
    }


    private fun subscribeToObservers() {
        model.registerStatus.observe(viewLifecycleOwner, { result ->
            result?.let {
                when(result.status) {
                    Status.LOADING -> {
                        binding.registerProgressBar.visibility = View.VISIBLE
                    }
                    Status.SUCCESS -> {
                        binding.registerProgressBar.visibility = View.GONE
                        showSnackBar(result.data ?: "Successfully created an account")
                    }
                    Status.ERROR -> {
                        binding.registerProgressBar.visibility = View.GONE
                        showSnackBar(result.message ?: "An unknown error occurred")
                    }
                }
            }

        })

        model.loginStatus.observe(viewLifecycleOwner, { result ->
            result?.let {
                when(result.status) {
                    Status.LOADING -> {
                        binding.loginProgressBar.visibility = View.VISIBLE
                    }
                    Status.SUCCESS -> {
                        binding.loginProgressBar.visibility = View.GONE
                        showSnackBar(result.data ?: "Successfully logged in")

                        sharedPrefs.edit()
                                .putString("key_email", currentEmail)
                                .putString("key_pass", currentPassword)
                                .apply()

                        authenticateApi(currentEmail ?: "", currentPassword ?: "")
                        redirectLogin()
                    }
                    Status.ERROR -> {
                        binding.loginProgressBar.visibility = View.GONE
                        showSnackBar(result.message ?: "An unknown error occurred")
                    }
                }
            }

        })
    }


}