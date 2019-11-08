package com.androidstudy.movies.di

import androidx.room.Room
import com.androidstudy.devfest19.BuildConfig
import com.androidstudy.movies.data.Database
import com.androidstudy.movies.data.network.AuthInterceptor
import com.androidstudy.movies.data.repository.CharactersRepo
import com.androidstudy.movies.ui.viewmodel.CharacterViewModel
import com.androidstudy.movies.utils.Utils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun injectFeature() = loadFeature

private val loadFeature by lazy {
    loadKoinModules(
        retrofit,
        authClient,
        authRetrofit,
        movieDatabase,
        movieDao,
        charactersDao,
        characterRepository,
        characterViewModel
    )
}

val retrofit = module(override = true) {
    single {

        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }
        } else {
            interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.NONE }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor).build()

        Retrofit.Builder()
            .baseUrl(Utils.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}

val authClient = module {
    single {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }
        } else {
            interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.NONE }
        }
        OkHttpClient.Builder().addInterceptor(AuthInterceptor()).addInterceptor(interceptor).build()
    }
}

val authRetrofit = module {
    single {
        Retrofit.Builder()
            .baseUrl(Utils.BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}



val movieDao = module {
    single { get<Database>().movieDao() }
}

val charactersDao = module {
    single { get<Database>().charactersDao() }
}

val movieDatabase = module {
    single {
        Room.databaseBuilder(androidContext(), Database::class.java, "devfest_movies")
            .build()
    }
}

val characterRepository = module {
    single { CharactersRepo(get(), get()) }
}

val characterViewModel = module {
    viewModel { CharacterViewModel(get()) }
}

