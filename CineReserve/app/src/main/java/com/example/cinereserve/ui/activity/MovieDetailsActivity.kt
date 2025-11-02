package com.example.cinereserve.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.PointerIcon.load
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.Coil
import coil.load
import com.example.cinereserve.R
import com.example.cinereserve.data.network.model.CartItem
import com.example.cinereserve.data.repository.CartRepository
import com.example.cinereserve.databinding.ActivityMovieDetailsBinding
import com.example.cinereserve.ui.viewmodels.MovieViewModel
import com.example.cinereserve.utils.CommonClass.isOnline
import com.example.cinereserve.utils.Urls
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class MovieDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovieDetailsBinding
    private val viewModel: MovieViewModel by viewModels()

    @Inject
    lateinit var cartRepository: CartRepository
    private var movieId: Int = 0
    private val token = "Bearer ${Urls.TOKEN}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.coordinatorLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        movieId = intent.getIntExtra("movie_id", 0)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.ivCart.setOnClickListener {
            val intent = Intent(this, BookTicketActivity::class.java)
            startActivity(intent)
        }
        viewModel.fetchMovieDetails(movieId, token)
        observeMovieDetails()
        binding.buttonSelectSession.setOnClickListener {
            addMovieToCart()
        }
    }

    private fun observeMovieDetails() {
        viewModel.selectedMovie.observe(this) { movie ->
            movie?.let {
                binding.toolbar.title = it.title ?: "Movie Details"

                val imageUrl = "https://image.tmdb.org/t/p/w500${it.posterPath}"
                binding.imageMoviePoster.load(imageUrl) {
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)

                    if (isOnline()) {
                        crossfade(true)
                    } else {
                        listener(
                            onError = { _, _ -> binding.imageMoviePoster.setImageResource(R.drawable.ic_placeholder) },
                            onSuccess = { _, _ -> }
                        )
                    }
                }
                binding.tvRate.text = String.format("%.1f", it.voteAverage ?: 0.0)

                binding.apply {
                    textDescription.text = it.overview ?: "No description available"
                    valueCertificate.text = if (it.adult == true) "18+" else "PG-13"
                    valueRuntime.text = if (it.runtime != null) "${it.runtime} min" else "N/A"
                    valueRelease.text = it.releaseDate ?: "N/A"
                    valueGenre.text = it.genres?.joinToString { g -> g.name ?: "" } ?: "N/A"
                    valueDirector.text = it.productionCompanies?.firstOrNull()?.name ?: "N/A"
                    valueCast.text = it.spokenLanguages?.joinToString { lang -> lang.englishName ?: "" } ?: "N/A"
                }

                updateButtonState(it.id ?: 0)
            }
        }
    }

    private fun addMovieToCart() {
        val movie = viewModel.selectedMovie.value ?: return
        val today = Calendar.getInstance()
        val formattedDate = String.format(
            "%02d/%02d/%04d",
            today.get(Calendar.DAY_OF_MONTH),
            today.get(Calendar.MONTH) + 1,
            today.get(Calendar.YEAR)
        )

        val cartItem = CartItem(
            movieId = movie.id ?: 0,
            title = movie.title ?: "",
            posterUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
            genre = movie.genres?.joinToString { it.name ?: "" } ?: "",
            rating = movie.voteAverage ?: 0.0,
            duration = movie.runtime?.let { "${it / 60}h ${it % 60}m" } ?: "N/A",
            cinema = "",
            date = formattedDate,
            time = "",
            personCount = 1
        )

        lifecycleScope.launch(Dispatchers.IO) {
            cartRepository.addToCart(cartItem)
        }

        val intent = Intent(this, BookTicketActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun updateButtonState(movieId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val cartItems = cartRepository.getCartItems()
            val exists = cartItems.any { it.movieId == movieId }

            launch(Dispatchers.Main) {
                if (exists) {
                    binding.buttonSelectSession.text = "View in Cart"
                    binding.buttonSelectSession.setOnClickListener {
                        val intent = Intent(this@MovieDetailsActivity, BookTicketActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    binding.buttonSelectSession.text = "Book Ticket"
                    binding.buttonSelectSession.setOnClickListener {
                        addMovieToCart()
                    }
                }
            }
        }
    }

}