package com.example.notes.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.notes.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Define ActionBar object
        // Define ActionBar object
        val actionBar: ActionBar? = supportActionBar

        // Define ColorDrawable object and parse color
        // using parseColor method
        // with color hash code as its parameter

        // Define ColorDrawable object and parse color
        // using parseColor method
        // with color hash code as its parameter
        val colorDrawable = ColorDrawable(R.color.black)

        // Set BackgroundDrawable

        // Set BackgroundDrawable
        actionBar?.setBackgroundDrawable(colorDrawable)

    }
}