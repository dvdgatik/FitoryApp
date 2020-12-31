package com.dimakers.fitoryapp.api;

import com.dimakers.fitoryapp.Variables;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {
//    public static final String BASE_URL = "http://192.168.1.8:8000/";
//    public static final String BASE_URL = "http://192.168.43.145:8000/";
    public static final String BASE_URL = Variables.HOST;
    private static Retrofit retrofit = null;
    public static Retrofit getApi() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60,TimeUnit.SECONDS)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request newRequest = chain.request().newBuilder()
                                    .addHeader("Authorization", "Token "+Variables.TOKEN_API)
                                    .build();
                            return chain.proceed(newRequest);
                        }
                    }).addInterceptor(logging).
                            build();

            retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
