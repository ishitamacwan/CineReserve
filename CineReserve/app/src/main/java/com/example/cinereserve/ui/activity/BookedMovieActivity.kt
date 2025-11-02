package com.example.cinereserve.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.cinereserve.R
import com.example.cinereserve.databinding.ActivityBookedMovieBinding
import com.example.cinereserve.ui.adapter.BookedMovieAdapter
import com.example.cinereserve.ui.viewmodels.BookedMovieViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
@AndroidEntryPoint

class BookedMovieActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookedMovieBinding
    private lateinit var adapter: BookedMovieAdapter
    private val viewModel: BookedMovieViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBookedMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = BookedMovieAdapter(emptyList())
        binding.rvBookList.adapter = adapter

        lifecycleScope.launch {
            viewModel.bookedMovies.collectLatest { movies ->
                adapter.updateList(movies)

                if (movies.isEmpty()) {
                    binding.rvBookList.visibility = View.GONE
                    binding.noDataText.visibility = View.VISIBLE
                } else {
                    binding.rvBookList.visibility = View.VISIBLE
                    binding.noDataText.visibility = View.GONE
                }
            }
        }

        viewModel.loadBookedMovies()

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}