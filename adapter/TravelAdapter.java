package com.tahayunus.assignmenttravelbook.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.tahayunus.assignmenttravelbook.MainActivity;
import com.tahayunus.assignmenttravelbook.databinding.RecyclerRowBinding;
import com.tahayunus.assignmenttravelbook.fragments.RecyclerViewFragmentDirections;
import com.tahayunus.assignmenttravelbook.model.Post;
import java.util.ArrayList;

public class TravelAdapter extends RecyclerView.Adapter<TravelAdapter.TravelHolder> {
    private ArrayList<Post> posts;
    public TravelAdapter(ArrayList<Post> posts){
        this.posts = posts;
    }

    @NonNull
    @Override
    public TravelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TravelHolder(RecyclerRowBinding.inflate(
                LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TravelHolder holder, int position) {
        holder.binding.recyclerViewTextView.setText(posts.get(position).artName);
        holder.itemView.setOnClickListener(v -> {
            RecyclerViewFragmentDirections.ActionRecyclerViewFragmentToUploadFragment
                    directions =
                    RecyclerViewFragmentDirections.actionRecyclerViewFragmentToUploadFragment();
            directions.setNewOrOld("old");
            directions.setId(position);
            Navigation.findNavController(v).navigate(directions);

        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class TravelHolder extends RecyclerView.ViewHolder {
        RecyclerRowBinding binding;
        public TravelHolder(@NonNull RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
