package com.ichinweze.flickpick

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
    val authToken: String = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5MDBmZGZlNGY2OWY3MWY5YzFkNzJiNTAwZDBmMDE3OSIsIm5iZiI6MTc1OTU5OTE0OC4yNjIsInN1YiI6IjY4ZTE1YTJjYTIyNjYyN2FjZjI1YWIzZSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.ijkX6Eg1pTo-lZUChSjigXDTbl7ei33bUNtzqu6mFOA"

    val thing1 = "discover/movie?include_adult=false&include_video=false&language=en-US&sort_by=vote_average.desc&page=1&with_origin_country=GB&release_date.gte=2010-01-01&release_date.lte=2020-12-31&vote_average.gte=7&vote_average.lte=8.5&with_runtime.gte=2010&with_runtime.lte=2020"
    val thing2 = "discover/movie?include_adult=false&include_video=false&language=en-US&sort_by=vote_average.desc&page=2&with_origin_country=GB&release_date.gte=2010-01-01&release_date.lte=2020-12-31&vote_average.gte=7&vote_average.lte=8.5&with_runtime.gte=2010&with_runtime.lte=2020"

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
            page = responseBody!!.page
            totalPages = responseBody!!.totalPages
            totalResults = responseBody!!.totalResults

            results = responseBody!!.results

            println("getMovieResponseFromApi: page = $page")
            println("getMovieResponseFromApi: total pages = $totalPages")
            println("getMovieResponseFromApi: total results = $totalResults")
            println("getMovieResponseFromApi: results: $results")
        } else {
            println("getMovieResponseFromApi: Empty response")
        }

        assert(results.isNotEmpty()) { "No results returned" }
        assert(page == 1) { "Unexpected page returned: $page" }
        assert(movieApiResponse.isSuccessful) { "API Request unsuccessful" }
    }
}