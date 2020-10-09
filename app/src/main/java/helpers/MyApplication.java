package helpers;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import network.GiphyApi;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import storage.Constants;

public class MyApplication extends Application {
    public static MyApplication instance;
    private Retrofit retrofit;
    private GifsDatabaseHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        setRetrofit();
        setDatabaseHelper();
    }

    private void setDatabaseHelper() {
        dbHelper = new GifsDatabaseHelper(getApplicationContext());
    }

    public SQLiteDatabase getDatabase() {
        return dbHelper.getWritableDatabase();
    }

    private void setRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_RANDOM_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public GiphyApi getApi() {
        return retrofit.create(GiphyApi.class);
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
