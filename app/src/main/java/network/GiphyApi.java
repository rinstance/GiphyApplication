package network;

import io.reactivex.Observable;
import models.ResponseGif;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GiphyApi {
    @GET("random")
    Observable<ResponseGif> getGifs(
            @Query("api_key") String api_key
    );
}
