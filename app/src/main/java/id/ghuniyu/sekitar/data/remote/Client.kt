import com.google.gson.Gson
import id.ghuniyu.sekitar.data.remote.Service
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Client {
    val service: Service
    private val gson: Gson = Gson()

    init {

        val gsonConverter = GsonConverterFactory.create(gson)

        val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

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
            .baseUrl("")
            .client(client)
            .build()

        service = retrofit.create(Service::class.java)
    }
}