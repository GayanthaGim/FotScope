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

public class EventsFragment extends Fragment implements ArticleAdapter.OnReadMoreClickListener {

    private RecyclerView recyclerView;
    private ArticleAdapter adapter;
    private List<Article> eventList;

    private static final String TAG = "EventsFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the event list and adapter
        eventList = new ArrayList<>();
        adapter = new ArticleAdapter(eventList, this);
        recyclerView.setAdapter(adapter);

        // Fetch event articles from Firebase
        fetchEventsFromFirebase();

        return view;
    }

    private void fetchEventsFromFirebase() {
        // Reference to the "events" section in Firebase
        DatabaseReference eventsRef = FirebaseDatabase.getInstance()
                .getReference("articles/events");

        // Fetch data from Firebase
        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventList.clear();
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    Article event = eventSnapshot.getValue(Article.class);
                    if (event != null) {
                        eventList.add(event); // Add the event article to the list
                    }
                }
                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log error if the fetch fails
                Log.e(TAG, "Failed to fetch events", error.toException());
            }
        });
    }

    @Override
    public void onReadMoreClick(Article article) {
        // Intent to navigate to the article's detail page
        Intent intent = new Intent(getContext(), ArticleDetailActivity.class);

        // Passing the article data to the detail activity
        intent.putExtra("title", article.getTitle());
        intent.putExtra("date", article.getDate());
        intent.putExtra("fullSummary", article.getFullSummary()); // Full summary
        intent.putExtra("imageUrl", article.getImageUrl());

        // Start the article detail activity
        startActivity(intent);
    }
}
