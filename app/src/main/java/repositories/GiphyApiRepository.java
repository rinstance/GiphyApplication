package repositories;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import helpers.MyApplication;
import io.reactivex.Observable;
import models.Gif;
import models.ResponseGif;
import network.GiphyApi;
import storage.Constants;

public class GiphyApiRepository {
    private GiphyApi giphyApi;
    private MutableLiveData<List<Gif>> newGifData;

    public GiphyApiRepository() {
        giphyApi = MyApplication.getInstance().getApi();
        newGifData = new MutableLiveData<>();
    }

    public Observable<ResponseGif> getGif() {
        return giphyApi.getGifs(Constants.API_KEY);
    }
}
