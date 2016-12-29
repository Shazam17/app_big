package com.software.ssp.erkc.di.modules

import android.content.Context
import com.google.gson.Gson
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.AuthProvider
import com.software.ssp.erkc.data.rest.ErkcInterceptor
import com.software.ssp.erkc.data.rest.datasource.*
import com.software.ssp.erkc.data.rest.repositories.*
import dagger.Module
import dagger.Provides
import io.realm.Realm
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
    fun providesErkcInterceptor(gson: Gson, activeSession: ActiveSession, context: Context): ErkcInterceptor {
        return ErkcInterceptor(gson, activeSession, context)
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
    fun providesIpuDataSource(retrofit: Retrofit): IpuDataSource {
        return retrofit.create(IpuDataSource::class.java)
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

    @Provides
    @Singleton
    fun providePaymentDataSource(retrofit: Retrofit): PaymentDataSource {
        return retrofit.create(PaymentDataSource::class.java)
    }

    @Provides
    @Singleton
    fun provideSettingsDataSource(retrofit: Retrofit): SettingsDataSource {
        return retrofit.create(SettingsDataSource::class.java)
    }

    @Provides
    @Singleton
    fun provideMessagesDataSource(retrofit: Retrofit): MessagesDataSource {
        return retrofit.create(MessagesDataSource::class.java)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(settingsDataSource: SettingsDataSource): SettingsRepository {
        return SettingsRepository(settingsDataSource)
    }

    @Provides
    fun provideRealmRepository(realm: Realm): RealmRepository {
        return RealmRepository(realm)
    }

    @Provides
    @Singleton
    fun provideCardsRepository(cardsDataSource: CardsDataSource): CardsRepository {
        return CardsRepository(cardsDataSource)
    }

    @Provides
    @Singleton
    fun provideMessagesRepository(messagesDataSource: MessagesDataSource): MessagesRepository {
        return MessagesRepository(messagesDataSource)
    }

    @Provides
    @Singleton
    fun providePaymentRepository(paymentDataSource: PaymentDataSource): PaymentRepository {
        return PaymentRepository(paymentDataSource)
    }
}
