import com.google.gson.Gson
import com.linkensky.ornet.BuildConfig
import com.linkensky.ornet.data.remote.Service
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Client {
    val service: Service
    private val gson: Gson = Gson()

    init {

        val gsonConverter = GsonConverterFactory.create(gson)

        val loggingInterceptor: HttpLoggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor {
                val requestBuilder = it.request().newBuilder()

                requestBuilder.addHeader("Accept", "application/json")

                it.proceed(requestBuilder.build())
            }
            .build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(gsonConverter)
            .baseUrl(BuildConfig.APP_BASE_URL)
            .client(client)
            .build()

        service = retrofit.create(Service::class.java)
    }
}