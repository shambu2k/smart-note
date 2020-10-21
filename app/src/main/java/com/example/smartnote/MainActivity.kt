package com.example.smartnote

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.smartnote.databinding.ActivityMainBinding
import com.example.smartnote.db.*
import com.example.smartnote.viewbinding.viewLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewLifecycle(ActivityMainBinding::inflate)

    private lateinit var navController: NavController

    private lateinit var bookViewModel: BookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        val dao = BookDatabase.getInstance(application).bookDao
        val repository = BookRepository(dao)
        val factory =  BookViewModelFactory(repository)
        bookViewModel = ViewModelProvider(this,factory).get(BookViewModel::class.java)
        var subs = listOf<String>("Maths","Chem","Physics")
        var folders = listOf<String>("folder1","folder2","folder3")
        bookViewModel.insert(Book(0,"book1",subs,folders))
        bookViewModel.insert(Book(0,"book2",subs,folders))
        bookViewModel.books.observe(this, Observer {
            Log.i("MyTag",it.toString())
        })

        setupNavigation()

    }

    private fun setupNavigation() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        navController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)
        NavigationUI.setupWithNavController(binding.sideNavigationDrawer, navController)
        binding.sideNavigationDrawer.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.books_item -> {
                    navController.navigate(R.id.booksFragment)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.qrscanner_item -> {
                    navController.navigate(R.id.qrscannerFragment)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            true
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, binding.drawerLayout)
    }
}
