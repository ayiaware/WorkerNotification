package com.ayia.workernotification.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.ayia.workernotification.R
import com.ayia.workernotification.databinding.ActivityMainBinding
import com.ayia.workernotification.framework.TodoApp

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var mainViewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProvider(
            this, MainViewModelFactory(
                owner = this,
                todoInteractors = TodoApp.instance.getTodoInteractions()
            )
        )[MainViewModel::class.java]


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener {
            navToTodoFormDestination()
        }



        navController.addOnDestinationChangedListener { _, destination, _ ->

            if (destination.id == R.id.TodoFormFragment) {
                binding.fab.hide()
            }
            else
                binding.fab.show()

        }


    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun navToTodoFormDestination() {

        val navController = findNavController(R.id.nav_host_fragment_content_main)

        navController.navigate(
            TodoListFragmentDirections.actionTodoListFragmentToTodoFormFragment(
                0
            )

        )

    }
}