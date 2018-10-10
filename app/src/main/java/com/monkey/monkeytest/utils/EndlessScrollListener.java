package com.monkey.monkeytest.utils;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.AbsListView;

public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {

    /**
     * The total number of items in the dataset after the last load
     */
    private int mPreviousTotal = 0;
    /**
     * True if we are still waiting for the last set of data to load.
     */
    private boolean mLoading = true;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = recyclerView.getLayoutManager().getItemCount();

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

            if (mLoading) {
                if (totalItemCount > mPreviousTotal) {
                    mLoading = false;
                    mPreviousTotal = totalItemCount;
                }
            }
            int visibleThreshold = 5;
            if (!mLoading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + visibleThreshold)) {
                // End has been reached

                onLoadMore();

                mLoading = true;
            }
        } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {

            final int[] firstVisibleItem = new int[1];

            final StaggeredGridLayoutManager linearLayoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    int[] lastVisibleItemPositions = linearLayoutManager.findFirstVisibleItemPositions(null);
                    firstVisibleItem[0] = lastVisibleItemPositions[0];

                }
            });

            if (mLoading) {
                if (totalItemCount > mPreviousTotal) {
                    mLoading = false;
                    mPreviousTotal = totalItemCount;
                }
            }
            int visibleThreshold = 5;
            if (!mLoading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem[0] + visibleThreshold)) {
                // End has been reached

                onLoadMore();

                mLoading = true;
            }
        }else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {

            int firstVisibleItem = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

            if (mLoading) {
                if (totalItemCount > mPreviousTotal) {
                    mLoading = false;
                    mPreviousTotal = totalItemCount;
                }
            }
            int visibleThreshold = 5;
            if (!mLoading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + visibleThreshold)) {
                // End has been reached

                onLoadMore();

                mLoading = true;
            }
        }
    }

    public abstract void onLoadMore();
}
