package helpers;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import io.reactivex.subjects.PublishSubject;

public class ImageHelper {
    private PublishSubject<Boolean> progressBarSubject;

    public ImageHelper() {
        progressBarSubject = PublishSubject.create();
    }

    public void downloadGif(ImageView gif, String url) {
        Glide.with(gif.getContext())
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) { return false; }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBarSubject.onNext(true);
                        return false;
                    }
                })
                .into(gif);
    }

    public PublishSubject<Boolean> getProgressBarSubject() {
        return progressBarSubject;
    }
}
