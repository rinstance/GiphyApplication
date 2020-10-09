package viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

import database.CRUDDatabase;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import models.Gif;

public class SavedGifsViewModel extends ViewModel {
    private MutableLiveData<List<Gif>> mutableSavedGifs;
    private Disposable readDisposable;
    private List<Gif> savedGifs;

    public LiveData<List<Gif>> getSavedGifs() {
        if (mutableSavedGifs == null) {
            mutableSavedGifs = new MutableLiveData<>();
            readGifs();
        }
        return mutableSavedGifs;
    }

    private void readGifs() {
        savedGifs = new ArrayList<>();
        readDisposable = Observable.fromCallable(CRUDDatabase::read)
                .subscribeOn(Schedulers.io())
                .subscribe(hashMap -> {
                    List<String> keys = new ArrayList(hashMap.keySet());
                    for(int i = 0; i < keys.size(); i++) {
                        String id = keys.get(i);
                        String url = hashMap.get(id);
                        Gif gif = new Gif();
                        gif.setId(id);
                        gif.setUrl(url);
                        savedGifs.add(gif);
                    }
                    mutableSavedGifs.postValue(savedGifs);
                });
    }

    public void deleteGif(Gif gif) {
        CRUDDatabase.delete(gif);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        readDisposable.dispose();
    }
}
