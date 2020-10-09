package com.example.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import adapters.GifAdapter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import models.Gif;
import viewmodels.MainViewModel;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MainViewModel model;
    private GifAdapter gifAdapter;
    private List<Gif> gifs;
    private boolean isFirstLoading = true;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar progressLoadingGifs;
    private Button refreshButton, savedGifsButton;
    private Disposable saveDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setAdapter();
        updateAdapter();
        setRefreshListener();
    }

    public void onSavedGifsClick(View view) {
        startActivity(new Intent(this, SavedGifsActivity.class));
    }

    private void setRefreshListener() {
        refreshLayout.setOnRefreshListener(this::refresh);
        refreshButton.setOnClickListener(v -> refresh());
    }

    private void refresh() {
        refreshLayout.setRefreshing(true);
        model.getRefreshingGifs();
    }

    private void updateAdapter() {
        LiveData<List<Gif>> gifsLiveData = model.getGifs();
        gifsLiveData.observe(this, gifs -> {
            if (isFirstLoading)
                showFirstLoading();
            else
                Snackbar.make(refreshLayout, "Updated", Snackbar.LENGTH_SHORT).show();
            if (refreshLayout.isRefreshing())
                refreshLayout.setRefreshing(false);
            this.gifs.clear();
            this.gifs.addAll(gifs);
            gifAdapter.notifyDataSetChanged();
        });
    }

    private void showFirstLoading() {
        progressLoadingGifs.setVisibility(View.GONE);
        refreshButton.setVisibility(View.VISIBLE);
        savedGifsButton.setVisibility(View.VISIBLE);
        isFirstLoading = false;
    }

    private void setAdapter() {
        gifs = new ArrayList<>();
        gifAdapter = new GifAdapter(gifs);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(gifAdapter);
        gifAdapter.setOnLongClickListener(this::saveGifDialog);
    }

    private void saveGifDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save this GIF?");
        builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("OK", (dialog, which) -> saveGifToDatabase(gifs.get(position)));
        builder.show();
    }

    private void saveGifToDatabase(Gif gif) {
        saveDisposable = Observable.fromCallable(() -> model.saveGif(gif))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean)
                        Snackbar.make(refreshLayout, "Saved", Snackbar.LENGTH_SHORT).show();
                    else
                        Snackbar.make(refreshLayout, "Already saved", Snackbar.LENGTH_SHORT).show();
                });
    }

    private void init() {
        recyclerView = findViewById(R.id.recyclerview_random_gif);
        refreshLayout = findViewById(R.id.refresh_layout);
        progressLoadingGifs = findViewById(R.id.progress_loading_gifs);
        refreshButton = findViewById(R.id.refresh_button);
        savedGifsButton = findViewById(R.id.saved_gifs_button);
        model = ViewModelProviders.of(this).get(MainViewModel.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveDisposable.dispose();
    }
}
