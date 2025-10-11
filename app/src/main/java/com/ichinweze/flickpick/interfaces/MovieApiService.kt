package com.ichinweze.flickpick.interfaces

import com.ichinweze.flickpick.data.ApiData.MovieApiResponseData
import com.ichinweze.flickpick.data.ApiData.MovieGenreResponseData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

interface MovieApiService {
    @GET
    fun getMovieApiResponse(
        @Header("Authorization") authToken: String,
        @Header("accept") value: String = "application/json",
        @Url urlWithSearchParams: String
    ): Call<MovieApiResponseData>

    @GET("genre/movie/list?language=en")
    fun getMovieGenreIds(
        @Header("Authorization") authToken: String,
        @Header("accept") value: String = "application/json"
    ): Call<MovieGenreResponseData>
}