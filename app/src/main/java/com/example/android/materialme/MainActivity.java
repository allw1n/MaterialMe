package com.example.android.materialme;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Sport> mSportsData;
    private SportsAdapter mAdapter;
    private boolean RESET = false;

    private ExtendedFloatingActionButton fabRestore;

    private Snackbar snackbar;

    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));

        fabRestore = findViewById(R.id.fab_restore);

        mRecyclerView = findViewById(R.id.recyclerView);

        snackbar = Snackbar.make(findViewById(R.id.parent_layout),
                R.string.layout_unchanged, Snackbar.LENGTH_SHORT);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSportsData = new ArrayList<>();

        mAdapter = new SportsAdapter(this, mSportsData);
        mRecyclerView.setAdapter(mAdapter);

        initializeData();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper
                .SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT |
                ItemTouchHelper.DOWN | ItemTouchHelper.UP,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                int from = viewHolder.getBindingAdapterPosition();
                int to = target.getBindingAdapterPosition();
                Collections.swap(mSportsData, from, to);
                mAdapter.notifyItemMoved(from, to);
                RESET = true;
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder,
                                 int direction) {
                mSportsData.remove(viewHolder.getBindingAdapterPosition());
                mAdapter.notifyItemRemoved(viewHolder.getBindingAdapterPosition());
                RESET = true;
            }
        });
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        fabRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restoreCards();
            }
        });
    }

    private void initializeData() {
        String[] sportsList = getResources()
                .getStringArray(R.array.sports_titles);
        String[] sportsInfo = getResources()
                .getStringArray(R.array.sports_info);
        TypedArray sportImage = getResources().
                obtainTypedArray(R.array.sports_images);

        mSportsData.clear();

        for(int i=0; i<sportsList.length; i++){
            mSportsData.add(new Sport(sportsList[i], sportsInfo[i], sportImage.getResourceId(i, 0)));
        }

        sportImage.recycle();

        mAdapter.notifyItemRangeInserted(0, sportsList.length);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_restore) {
            restoreCards();
        }

        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void restoreCards() {
        if (RESET) {
            if (snackbar.isShown()) {
                snackbar.dismiss();
            }
            mAdapter.notifyDataSetChanged();
            initializeData();
            RESET = false;
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            if (linearLayoutManager != null) {
                linearLayoutManager.scrollToPositionWithOffset(0,0);
            }
        } else {
            fabRestore.shrink();
            snackbar.setAction(R.string.dismiss, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            }).setBackgroundTint(getColor(R.color.black)).setTextColor(getColor(R.color.white))
                    .setActionTextColor(getColor(R.color.white));
            snackbar.show();
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    fabRestore.extend();
                    super.onDismissed(transientBottomBar, event);
                }
            });
        }
    }
}
