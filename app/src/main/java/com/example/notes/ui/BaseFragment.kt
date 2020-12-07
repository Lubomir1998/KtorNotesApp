package com.example.notes.ui

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import com.example.notes.R
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment(layoutId: Int): Fragment(layoutId) {

    fun showSnackBar(message: String) {
        Snackbar.make(requireActivity().findViewById(R.id.rootLayout), message, Snackbar.LENGTH_LONG).show()
    }

}