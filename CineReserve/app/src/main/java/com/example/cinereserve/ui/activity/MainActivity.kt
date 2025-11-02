package com.example.cinereserve.ui.activity

import android.app.Activity
import android.content.Context
import com.example.cinereserve.ui.adapter.MoviesAdapter
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.cinereserve.R
import com.example.cinereserve.databinding.ActivityMainBinding
import com.example.cinereserve.ui.viewmodels.MovieViewModel
import com.example.cinereserve.utils.CommonClass.isOnline
import com.example.cinereserve.utils.Urls
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MoviesAdapter

    private val viewModel: MovieViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.sortIcon.setOnClickListener {
            showSortBottomSheet()
        }
        binding.toolbarMain.ivCart.setOnClickListener {
            val intent = Intent(this, BookTicketActivity::class.java)
            startActivity(intent)
        }
        binding.toolbarMain.ivBook.setOnClickListener {
            val intent = Intent(this, BookedMovieActivity::class.java)
            startActivity(intent)
        }

        binding.searchEditText.addTextChangedListener { text ->
            val query = text.toString()
            adapter.filter(query)
            binding.moviesRecyclerView.scrollToPosition(0)
        }

        setupRecyclerView()
        val token = "Bearer ${Urls.TOKEN}"
        viewModel.fetchMovies(token)
        viewModel.movies.observe(this) { movies ->
            Log.d("TAG", "onCreate list size: ${movies.size}")
            adapter.updateMovies(movies)
        }
        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

    }
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = android.graphics.Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
    private fun setupRecyclerView() {
        adapter = MoviesAdapter(this,emptyList()) { movie ->
            if (isOnline()) {
                val intent = Intent(this, MovieDetailsActivity::class.java)
                intent.putExtra("movie_id", movie.id)
                startActivity(intent)
            } else {
                Toast.makeText(this, "You are offline. Cannot open movie details.", Toast.LENGTH_SHORT).show()
            }
        }


        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.moviesRecyclerView.layoutManager = layoutManager
        binding.moviesRecyclerView.adapter = adapter

        binding.moviesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) { // only when scrolling down
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItems = layoutManager.findFirstVisibleItemPositions(null)
                    val pastVisibleItems = firstVisibleItems.minOrNull() ?: 0

                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        viewModel.fetchMovies("Bearer ${Urls.TOKEN}")
                    }
                }
            }
        })
    }

    private fun showSortBottomSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottomsheet_sort, null)
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)

        val radioGroup = bottomSheetView.findViewById<RadioGroup>(R.id.sortRadioGroup)

        when(viewModel.currentSort.value) {
            "default" -> radioGroup.check(R.id.rbSortDefault)
            "rating" -> radioGroup.check(R.id.rbSortByRating)
            "date" -> radioGroup.check(R.id.rbSortByDate)
            "title" -> radioGroup.check(R.id.rbSortByTitle)
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbSortDefault -> viewModel.sortMoviesBy("default")
                R.id.rbSortByRating -> viewModel.sortMoviesBy("rating")
                R.id.rbSortByDate -> viewModel.sortMoviesBy("date")
                R.id.rbSortByTitle -> viewModel.sortMoviesBy("title")
            }
            binding.moviesRecyclerView.scrollToPosition(0)
            dialog.dismiss()
        }

        dialog.show()
    }



}