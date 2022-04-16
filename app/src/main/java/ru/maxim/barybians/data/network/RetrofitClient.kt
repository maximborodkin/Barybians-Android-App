package ru.maxim.barybians.data.network

import com.google.gson.GsonBuilder
import dagger.Reusable
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.network.service.*
import java.util.*
import javax.inject.Inject

@Reusable
class RetrofitClient @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    private val connectionSpec: List<ConnectionSpec> =
        Collections.singletonList(
            ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                .tlsVersions(
                    TlsVersion.TLS_1_0,
                    TlsVersion.TLS_1_1,
                    TlsVersion.TLS_1_2,
                    TlsVersion.TLS_1_3
                )
                .cipherSuites(
                    CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA,
                    CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA,
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
                    CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA,
                    CipherSuite.TLS_EMPTY_RENEGOTIATION_INFO_SCSV
                )
                .build()
        )

    private val authorizationInterceptor = Interceptor {
        val request = it.request().newBuilder()
            .addHeader("Authorization", "Bearer ${preferencesManager.token}")
            .build()
        return@Interceptor it.proceed(request)
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { authorizationInterceptor.intercept(it) }
        .addInterceptor(HttpLoggingInterceptor().apply { level = BODY })
        .connectionSpecs(connectionSpec)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .client(okHttpClient)
        .build()

    val authService: AuthService = retrofit.create(AuthService::class.java)
    val userService: UserService = retrofit.create(UserService::class.java)
    val chatService: ChatService = retrofit.create(ChatService::class.java)
    val postService: PostService = retrofit.create(PostService::class.java)
    val stickerPackService: StickerPackService = retrofit.create(StickerPackService::class.java)
    val commentService: CommentService = retrofit.create(CommentService::class.java)

    companion object {
        const val BASE_URL = "https://api.barybians.ru"
        const val WS_URL = "wss://barybians.ru:3000"
        const val STICKERS_BASE_URL = "https://content.barybians.ru/stickers"
        const val AVATARS_BASE_URL = "https://content.barybians.ru/avatars"
        const val DEFAULT_AVATAR_URL = "https://content.barybians.ru/avatars/j.png"
    }
}