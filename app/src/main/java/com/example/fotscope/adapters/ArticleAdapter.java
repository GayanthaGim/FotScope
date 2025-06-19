package com.example.fotscope.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fotscope.ArticleDetailActivity;
import com.example.fotscope.R;
import com.example.fotscope.models.Article;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    // Interface to handle "Read More" clicks
    public interface OnReadMoreClickListener {
        void onReadMoreClick(Article article);
    }

    private List<Article> articleList;
    private OnReadMoreClickListener readMoreClickListener;

    // Constructor
    public ArticleAdapter(List<Article> articleList, OnReadMoreClickListener listener) {
        this.articleList = articleList;
        this.readMoreClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_card, parent, false); // Layout for each article
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articleList.get(position);

        // Set data for title, summary, and date
        holder.articleTitle.setText(article.getTitle());
        holder.articleSummary.setText(article.getSummary()); // Short summary
        holder.articleDate.setText(article.getDate());

        // Load the image if the URL is provided
        String imageUrl = article.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Firebase Storage reference (imageUrl is gs://)
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl(imageUrl);  // imageUrl is gs:// format

            // Get the download URL
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Use Glide to load the image with the download URL
                Glide.with(holder.itemView.getContext())
                        .load(uri)  // URI is the downloadable HTTP URL
                        .into(holder.articleImage);
            }).addOnFailureListener(e -> {
                // Handle failure, e.g., set a default image
                Glide.with(holder.itemView.getContext())
                        .load(R.drawable.placeholder_image)  // Default placeholder image
                        .into(holder.articleImage);
            });
        } else {
            holder.articleImage.setImageDrawable(null);  // Clear image if no URL
        }

        // Handle "Read More" click
        holder.readMore.setOnClickListener(v -> {
            // When "Read More" is clicked, pass article details to ArticleDetailActivity
            Intent intent = new Intent(holder.itemView.getContext(), ArticleDetailActivity.class);
            intent.putExtra("title", article.getTitle());
            intent.putExtra("date", article.getDate());
            intent.putExtra("fullSummary", article.getFullSummary()); // Full content to display
            intent.putExtra("imageUrl", article.getImageUrl());

            // Start the activity
            holder.itemView.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return articleList.size();
    }

    // ViewHolder to hold the views for each article
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView articleTitle, articleSummary, articleDate, readMore;
        ImageView articleImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            articleTitle = itemView.findViewById(R.id.articleTitle);
            articleSummary = itemView.findViewById(R.id.articleSummary);
            articleDate = itemView.findViewById(R.id.articleDate);
            articleImage = itemView.findViewById(R.id.articleImage);
            readMore = itemView.findViewById(R.id.readMore); // "Read More" button
        }
    }
}
