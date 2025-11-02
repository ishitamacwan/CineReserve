package com.example.cinereserve.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.example.cinereserve.R
import com.example.cinereserve.data.network.model.Movie
import com.example.cinereserve.databinding.ItemMovieBinding
import com.example.cinereserve.utils.CommonClass.isOnline

class MoviesAdapter(
    var context: Activity,
    private var movies: List<Movie>,
    private val onClick: (Movie) -> Unit
) : RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {
    private var fullList = ArrayList(movies)

    class MovieViewHolder(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.binding.apply {
            tvTitle.text = movie.title
            textRating.text = String.format("%.1f", movie.vote_average)
            tvReleaseDate.text = movie.release_date

            poster.load("https://image.tmdb.org/t/p/w500${movie.poster_path}") {
                crossfade(true)
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
                placeholder(R.drawable.ic_placeholder)
            }
            poster.load("https://image.tmdb.org/t/p/w500${movie.poster_path}") {
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)

                if (context.isOnline()) {
                    crossfade(true)
                } else {
                    listener(
                        onError = { _, _ -> poster.setImageResource(R.drawable.ic_placeholder) },
                        onSuccess = { _, _ -> }
                    )
                }
            }

            root.setOnClickListener { onClick(movie) }
        }
    }

    override fun getItemCount(): Int = movies.size

    fun updateMovies(newMovies: List<Movie>) {
        val diffCallback = MoviesDiffCallback(movies, newMovies)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        movies = newMovies
        fullList = ArrayList(newMovies)
        diffResult.dispatchUpdatesTo(this)
    }

    fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            fullList
        } else {
            fullList.filter { it.title.contains(query, ignoreCase = true) }
        }
        val diffCallback = MoviesDiffCallback(movies, filteredList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        movies = filteredList
        diffResult.dispatchUpdatesTo(this)
    }
}

class MoviesDiffCallback(
    private val oldList: List<Movie>,
    private val newList: List<Movie>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]
}
