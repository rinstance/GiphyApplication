package com.example.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import adapters.GifAdapter;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import models.Gif;
import viewmodels.SavedGifsViewModel;

public class SavedGifsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SavedGifsViewModel model;
    private List<Gif> savedGifs;
    private GifAdapter savedGifAdapter;
    private ConstraintLayout constraintLayout;
    private TextView noSavedText;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_gifs);
        init();
        setAdapter();
        updateAdapter();
    }

    public void onRandomGifsClick(View view) {
        finish();
    }

    private void updateAdapter() {
        LiveData<List<Gif>> savedGifsLiveData = model.getSavedGifs();
        savedGifsLiveData.observe(this, gifs -> {
            setNoSavedText(gifs.isEmpty());
            savedGifs.clear();
            savedGifs.addAll(gifs);
            savedGifAdapter.notifyDataSetChanged();
        });
    }

    private void setAdapter() {
        savedGifs = new ArrayList<>();
        savedGifAdapter = new GifAdapter(savedGifs);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(savedGifAdapter);
        savedGifAdapter.setOnLongClickListener(this::deleteGifDialog);
    }

    private void setNoSavedText(boolean flag) {
        if (flag) {
            noSavedText.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);
        }
    }

    private void deleteGifDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete this GIF?");
        builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("OK", (dialog, which) -> deleteFromSaved(savedGifs.get(position)));
        builder.show();
    }

    private void deleteFromSaved(Gif gif) {
        Completable.fromAction(() -> {
                    model.deleteGif(gif);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Snackbar.make(constraintLayout, "Deleted", Snackbar.LENGTH_SHORT).show();
                    savedGifs.remove(gif);
                    savedGifAdapter.notifyDataSetChanged();
                    setNoSavedText(savedGifs.isEmpty());
                }).isDisposed();
    }

    private void init() {
        recyclerView = findViewById(R.id.recyclerview_saved_gif);
        constraintLayout = findViewById(R.id.constraint_layout);
        noSavedText = findViewById(R.id.no_saved_text);
        backButton = findViewById(R.id.back_button);
        model = ViewModelProviders.of(this).get(SavedGifsViewModel.class);
    }
}
