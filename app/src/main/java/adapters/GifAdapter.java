package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.activities.R;

import java.util.List;

import helpers.ImageHelper;
import models.Gif;

public class GifAdapter extends RecyclerView.Adapter<GifAdapter.GifHolder> {
    private List<Gif> gifs;
    private ImageHelper imageHelper;
    private onLongClickListener onLongClickListener;

    public interface onLongClickListener {
        void onLongItemClick(int position);
    }

    public void setOnLongClickListener(onLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public GifAdapter(List<Gif> gifs) {
        this.gifs = gifs;
    }

    @NonNull
    @Override
    public GifHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.gif_item, parent, false);
        return new GifHolder(view, onLongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull GifHolder holder, int position) {
        String url;
        if (gifs.get(position).getImages() == null) {
            url = gifs.get(position).getUrl();
        } else {
            url = gifs.get(position).getImages().getDownsized_large().getUrl();
            gifs.get(position).setUrl(url);
        }
        holder.loadingProgressBar.setVisibility(View.VISIBLE);
        imageHelper = new ImageHelper();
        imageHelper.getProgressBarSubject()
                .subscribe(aBoolean -> {
                    if (aBoolean)
                        holder.loadingProgressBar.setVisibility(View.GONE);
                }).isDisposed();
        imageHelper.downloadGif(holder.gif, url);
    }

    @Override
    public int getItemCount() {
        return gifs.size();
    }

    class GifHolder extends RecyclerView.ViewHolder {
        private ImageView gif;
        private ProgressBar loadingProgressBar;

        GifHolder(@NonNull View itemView, onLongClickListener onLongClickListener) {
            super(itemView);
            init(itemView);
            setOnLongItemClick(itemView, onLongClickListener);
        }

        private void setOnLongItemClick(View itemView, onLongClickListener onLongClickListener) {
            itemView.setOnLongClickListener(v -> {
                if (onLongClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onLongClickListener.onLongItemClick(position);
                        return true;
                    }
                }
                return false;
            });
        }

        private void init(View itemView) {
            gif = itemView.findViewById(R.id.gif);
            loadingProgressBar = itemView.findViewById(R.id.progress_loading_gif);
        }
    }
}
