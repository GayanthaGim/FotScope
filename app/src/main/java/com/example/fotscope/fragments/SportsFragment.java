package com.example.fotscope.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fotscope.ArticleDetailActivity;
import com.example.fotscope.R;
import com.example.fotscope.adapters.ArticleAdapter;
import com.example.fotscope.models.Article;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SportsFragment extends Fragment implements ArticleAdapter.OnReadMoreClickListener {

    private RecyclerView recyclerView;
    private ArticleAdapter adapter;
    private List<Article> articleList;

    private static final String TAG = "SportsFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sports, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewSports);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the article list and adapter
        articleList = new ArrayList<>();
        adapter = new ArticleAdapter(articleList, this);
        recyclerView.setAdapter(adapter);

        // Fetch articles from Firebase
        fetchArticlesFromFirebase();

        return view;
    }

    private void fetchArticlesFromFirebase() {
        DatabaseReference articlesRef = FirebaseDatabase.getInstance()
                .getReference("articles/sports");

        articlesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                articleList.clear();
                for (DataSnapshot articleSnapshot : snapshot.getChildren()) {
                    Article article = articleSnapshot.getValue(Article.class);
                    if (article != null) {
                        articleList.add(article);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch articles", error.toException());
            }
        });
    }

    @Override
    public void onReadMoreClick(Article article) {
        // Create an Intent to navigate to ArticleDetailActivity
        Intent intent = new Intent(getContext(), ArticleDetailActivity.class);

        // Passing the article data to ArticleDetailActivity
        intent.putExtra("title", article.getTitle());
        intent.putExtra("date", article.getDate());
        intent.putExtra("fullSummary", article.getFullSummary()); // Full summary instead of short summary
        intent.putExtra("imageUrl", article.getImageUrl());

        // Start ArticleDetailActivity
        startActivity(intent);
    }
}
