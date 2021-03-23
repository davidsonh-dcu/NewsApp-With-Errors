/*
 * Copyright (C) 2021 Hugh Davidson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.hughsnewsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    public static final String LOG_TAG = NewsActivity.class.getName();

    /** URL for Brexit news data from the Guardian news site  dataset */
    private static final String GUARDIAN_REQUEST_URL ="https://content.guardianapis.com/search?page-size=10&q=brexit%20AND%20debate&api-key=test";
    // https://content.guardianapis.com/search?page-size=10&q=brexit%20AND%20debate&api-key=test
    // https://content.guardianapis.com/search?q=
    // https://content.guardianapis.com/search?api-key=test
    //https://content.guardianapis.com/search?order-by=newest&q=brexit%2520AND%2520debate&api-key=test

    /** Constant value for the earthquake loader ID. */
    private static final int NEWS_LOADER_ID = 1;

    /** Adapter for the list of news articles */
    private NewsAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.news_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = findViewById(R.id.list);

        // View when no data is available
        mEmptyStateTextView = findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of news articles as input
        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        // Set the adapter on the {@link ListView} so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected news article.
        newsListView.setOnItemClickListener((adapterView, view, position, l) -> {
            // Find the current news article that was clicked on
            News currentNews = mAdapter.getItem(position);

            // Convert the String URL into a URI object (to pass into the Intent constructor)
            Uri newsUri = Uri.parse(currentNews.getUrl());

            // Create a new intent to view the news URI
            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

            // Send the intent to launch a new activity
            startActivity(websiteIntent);
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }
/*        // Start the AsyncTask to fetch the news data
        NewsAsyncTask task = new NewsAsyncTask();
        task.execute(GUARDIAN_REQUEST_URL);
    }
    *//**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the list of news articles in the response.
     *
     * AsyncTask has three generic parameters: the input type, a type used for progress updates, and
     * an output type. This task will take a String URL, and return an News Article. Progress updates,
     * is just Void for now
     *
     * The methods of AsyncTask: doInBackground() and onPostExecute() are overridden
     *
     *//*
    private class NewsAsyncTask extends AsyncTask<String, Void, List<News>> {
        *//**
         * This method runs on a background thread and performs the network request.
         * It returns a list of {@link News} articles as the result.
         *//*
        @Override
        protected List<News> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            List<News> result = QueryUtils.fetchNewsData(urls[0]);
            return result;
        }

        *//**
         * This method runs on the main UI thread after the background work has been
         * completed. This method receives as input, the return value from the doInBackground()
         * method. First the adapter is cleared to get rid of News data from a previous
         * query to the Guardian news site. Second the adapter is updated with the new list of News Articles.
         * which will trigger the ListView to re-populate its list items.
         *//*
        @Override
        protected void onPostExecute(List<News> data) {
            // Clear the adapter of previous news data
            mAdapter.clear();

            // If there is a valid list of {@link News} articles, they will get added to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }
        }
    }*/

    // onCreateLoader instantiates and returns a new Loader for the given ID
    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle){

        // Return the completed uri 'https://content.guardianapis.com/search?order-by=newest&q=brexit%2520AND%2520debate&api-key=test'
        //return new NewsLoader(this, uriBuilder.toString());
        return new NewsLoader(this, GUARDIAN_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsArticlesList) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No news articles found."
        mEmptyStateTextView.setText(R.string.no_news);

        // Clear the adapter of previous news data
//        mAdapter.clear();

        // If there is a valid list of {@link News} articles, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (newsArticlesList != null && !newsArticlesList.isEmpty()) {
            mAdapter.addAll(newsArticlesList);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

}