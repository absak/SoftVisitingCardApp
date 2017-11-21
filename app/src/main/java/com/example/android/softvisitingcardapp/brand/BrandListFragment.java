package com.example.android.softvisitingcardapp.brand;



import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.softvisitingcardapp.R;
import com.example.android.softvisitingcardapp.brand.BrandActivity;
import com.example.android.softvisitingcardapp.brand.BrandAdapter;
import com.example.android.softvisitingcardapp.api.APIService;
import com.example.android.softvisitingcardapp.api.APIUrl;
import com.example.android.softvisitingcardapp.models.Brands;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class BrandListFragment extends Fragment {

    private RecyclerView recyclerViewUsers;
    private RecyclerView.Adapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public static String userEmail;
    public static int cardId;

    public BrandListFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("All Brands");

        recyclerViewUsers = (RecyclerView) view.findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_home_swipe_refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });


        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.show();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(getActivity(), BrandActivity.class);
                startActivity(intent);
            }
        });

        recyclerViewUsers.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0 ||dy<0 && fab.isShown())
                    fab.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    fab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        updateList();
    }

    private void updateList(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService service = retrofit.create(APIService.class);

        Call<Brands> call = service.getBrands();

        call.enqueue(new Callback<Brands>() {
            @Override
            public void onResponse(Call<Brands> call, Response<Brands> response) {
                adapter = new BrandAdapter(response.body().getBrands(), getActivity());
                recyclerViewUsers.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<Brands> call, Throwable t) {
                Toast.makeText(getActivity(), "List items not loaded yet...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshContent() {
        updateList();
        mSwipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getActivity(), "List is refreshed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_search_list_item, menu);

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) ((BrandAdapter) adapter).getFilter().filter(newText);
                return true;
            }
        });
    }
}
