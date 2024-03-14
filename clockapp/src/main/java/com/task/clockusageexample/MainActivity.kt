package com.task.clockusageexample

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        val button: Button = findViewById(R.id.button)
        button.setOnClickListener {
            if (navController.currentDestination?.id != R.id.secondFragment) {
                navController.navigate(R.id.action_firstFragment_to_secondFragment3)
                button.text = getString(R.string.back)
            }
            else {
                navController.navigateUp()
                button.text = getString(R.string.go_to_store)
            }
        }
    }
}