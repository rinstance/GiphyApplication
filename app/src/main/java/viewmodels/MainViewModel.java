package viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

import database.CRUDDatabase;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import models.Gif;
import repositories.GiphyApiRepository;
import storage.Constants;

public class MainViewModel extends ViewModel {
    private MutableLiveData<List<Gif>> mutableGifs;
    private GiphyApiRepository repository;
    private Disposable loadingGifDisposable;
    private List<Gif> gifList;

    public MainViewModel() {
        repository = new GiphyApiRepository();
    }

    public LiveData<List<Gif>> getGifs() {
        if (mutableGifs == null) {
            mutableGifs = new MutableLiveData();
            loadGifs();
        }
        return mutableGifs;
    }

    private void loadGifs() {
        gifList = new ArrayList<>();
        loadingGifDisposable = repository.getGif()
                .repeat(Constants.RANDOM_COUNT_GIF)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        responseGif -> {
                            gifList.add(responseGif.getData());
                            if (gifList.size() == Constants.RANDOM_COUNT_GIF) {
                                mutableGifs.postValue(gifList);
                            }
                        }
                );
    }

    public void getRefreshingGifs() {
        loadGifs();
    }

    public boolean saveGif(Gif gif) {
        if (CRUDDatabase.isSaved(gif))
            return false;
        CRUDDatabase.write(gif);
        return true;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        loadingGifDisposable.dispose();
        CRUDDatabase.close();
    }
}






