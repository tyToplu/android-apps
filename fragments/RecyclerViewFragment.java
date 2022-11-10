package com.tahayunus.assignmenttravelbook.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.tahayunus.assignmenttravelbook.MainActivity;
import com.tahayunus.assignmenttravelbook.R;
import com.tahayunus.assignmenttravelbook.adapter.TravelAdapter;
import com.tahayunus.assignmenttravelbook.databinding.RecyclerRowBinding;
import com.tahayunus.assignmenttravelbook.model.Post;

import java.util.ArrayList;

public class RecyclerViewFragment extends Fragment{
    private ArrayList<Post> posts;
    private TravelAdapter travelAdapter;
    public RecyclerViewFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        posts = new ArrayList<>();
        getData();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        travelAdapter = new TravelAdapter(posts);
        RecyclerView recyclerView =view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(travelAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.add_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_art) {/*
            NavDirections directions = UploadFragmentDirections.actionUploadFragmentToRecyclerViewFragment();
            Navigation.findNavController(getView()).navigate(directions);

            --------------
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView,UploadFragment.class,null)
                    .commit();
           */

            RecyclerViewFragmentDirections.ActionRecyclerViewFragmentToUploadFragment
                    directions =
                    RecyclerViewFragmentDirections.actionRecyclerViewFragmentToUploadFragment();
            directions.setNewOrOld("new");
            Navigation.findNavController(getView()).navigate(directions);

        }
        return super.onOptionsItemSelected(item);
    }

    private void getData(){
        try {
            SQLiteDatabase sqLiteDatabase = getActivity()
                    .openOrCreateDatabase("Arts", MainActivity.MODE_PRIVATE,null);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM arts",null);
            int nameIx = cursor.getColumnIndex("artName");
            int idIx = cursor.getColumnIndex("id");


            while(cursor.moveToNext()){
                String name = cursor.getString(nameIx);
                int id = cursor.getInt(idIx);
                Post post = new Post(name,id);
                posts.add(post);
            }
            travelAdapter.notifyDataSetChanged();

            //cursor daha sonra kapatilmali
            cursor.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}