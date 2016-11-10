package com.software.ssp.erkc.di.modules

import com.google.gson.Gson
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.data.rest.AuthProvider
import com.software.ssp.erkc.data.rest.ErkcInterceptor
import com.software.ssp.erkc.data.rest.datasource.*
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun providesDefaultHTTPClient(erkcInterceptor: ErkcInterceptor): OkHttpClient {
        val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(erkcInterceptor)
                .followRedirects(true)
                .build()

        return client
    }

    @Provides
    @Singleton
    fun providesErkcInterceptor(gson: Gson): ErkcInterceptor {
        return ErkcInterceptor(gson)
    }

    @Provides
    @Singleton
    fun providesAuthProvider(): AuthProvider {
        return AuthProvider()
    }

    @Provides
    @Singleton
    fun providesDefaultRetrofitAdapter(client: OkHttpClient, gson: Gson): Retrofit {
        val adapter = Retrofit.Builder()
                .baseUrl(Constants.API_MAIN_ENDPOINT_URL)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        return adapter
    }

    @Provides
    @Singleton
    fun providesAuthDataSource(retrofit: Retrofit): AuthDataSource {
        return retrofit.create(AuthDataSource::class.java)
    }

    @Provides
    @Singleton
    fun providesDictionarySource(retrofit: Retrofit): DictionaryDataSource {
        return retrofit.create(DictionaryDataSource::class.java)
    }

    @Provides
    @Singleton
    fun providesAccountDataSource(retrofit: Retrofit): AccountDataSource {
        return retrofit.create(AccountDataSource::class.java)
    }

    @Provides
    @Singleton
    fun providesCardsDataSource(retrofit: Retrofit): CardsDataSource {
        return retrofit.create(CardsDataSource::class.java)
    }

    @Provides
    @Singleton
    fun providesReceiptsDataSource(retrofit: Retrofit): ReceiptsDataSource {
        return retrofit.create(ReceiptsDataSource::class.java)
    }

    @Provides
    @Singleton
    fun provideFaqDataSource(retrofit: Retrofit): FaqDataSource {
        return retrofit.create(FaqDataSource::class.java)
    }
}