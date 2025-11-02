package com.example.cinereserve.data.local

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "cinema_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MovieDatabase {
        return Room.databaseBuilder(
            context,
            MovieDatabase::class.java,
            "movies_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideBookedDatabase(@ApplicationContext context: Context): BookedMovieDB {
        return Room.databaseBuilder(
            context,
            BookedMovieDB::class.java,
            "booked_movies_db"
        ).build()
    }

    @Provides
    fun provideMovieDao(db: MovieDatabase): MovieDao = db.movieDao()

    @Provides
    fun provideBookedMovieDao(db: BookedMovieDB): BookedMovieDao = db.bookedMovieDao()

    @Provides
    @Singleton
    fun provideCartDao(db: AppDatabase): CartDao {
        return db.cartDao()
    }
}
