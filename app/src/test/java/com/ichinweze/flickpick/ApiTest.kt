package com.ichinweze.flickpick

import com.ichinweze.flickpick.data.ApiData.MovieGenreResults
import com.ichinweze.flickpick.data.ApiData.MoviePageResult
import com.ichinweze.flickpick.interfaces.MovieApiService
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiTest {

    val baseUrl = "https://api.themoviedb.org/3/"

    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl) // Can be any valid base URL
        .addConverterFactory(GsonConverterFactory.create()) // Or your preferred converter
        .build()

    val movieApiService = retrofit.create(MovieApiService::class.java)

    //val testUrl = "discover/movie?include_adult=false&include_video=false&language=en-US&page=1&sort_by=popularity.desc&vote_average.gte=7&vote_average.lte=8"
    val testUrl = "discover/movie?include_adult=false&include_video=false&language=en-US&sort_by=popularity.desc&with_genres=28&with_origin_country=US&release_date.gte=2000-01-01&vote_average.gte=6&vote_average.lte=7&with_runtime.gte=100"
    val authToken: String = "Bearer ${BuildConfig.TMDB_AUTH_TOKEN}"

    @Test
    fun getMovieResponseFromApi() {
        var page = 0
        var totalPages = 0
        var totalResults = 0

        var results = listOf<MoviePageResult>()

        val movieApiResponse = movieApiService
            .getMovieApiResponse(authToken = authToken, urlWithSearchParams = testUrl)
            .execute()

        val responseBody = movieApiResponse.body()
        val status = movieApiResponse.code()

        println("getMovieResponseFromApi: status = $status, success = ${movieApiResponse.isSuccessful}")

        if (responseBody != null) {
            page = responseBody.page
            totalPages = responseBody.totalPages
            totalResults = responseBody.totalResults

            results = responseBody.results

            println("getMovieResponseFromApi: page = $page")
            println("getMovieResponseFromApi: total pages = $totalPages")
            println("getMovieResponseFromApi: total results = $totalResults")
            println("getMovieResponseFromApi: results: $results")
        }

        assert(results.isNotEmpty()) { "No results returned" }
        assert(page == 1) { "Unexpected page returned: $page" }
        assert(movieApiResponse.isSuccessful) { "API Request unsuccessful" }
        assert(responseBody != null) { "Empty response" }
    }

    @Test
    fun getGenreIdsFromApi() {
        var genres = listOf<MovieGenreResults>()

        val unexpectedGenreIds = genres.filter { genre -> genre.id < 0 }
        val unexpectedGenreValues = genres.filter { genre -> genre.name == "" }

        val movieApiResponse = movieApiService
            .getMovieGenreIds(authToken = authToken)
            .execute()

        val responseBody = movieApiResponse.body()
        val status = movieApiResponse.code()

        println("getGenreIdsFromApi: status = $status, success = ${movieApiResponse.isSuccessful}")

        if (responseBody != null) {
            genres = responseBody.genres

            println("getGenreIdsFromApi: number of genres = ${genres.size}")
        }

        assert(genres.isNotEmpty()) { "No results returned" }
        assert(unexpectedGenreIds.isEmpty()) { "Unexpected genre ID returned: $unexpectedGenreIds" }
        assert(unexpectedGenreValues.isEmpty()) {
            "Unexpected genre values: count = ${unexpectedGenreValues.size}"
        }
        assert(movieApiResponse.isSuccessful) { "API Request unsuccessful" }
        assert(responseBody != null) { "Empty response" }
    }
}