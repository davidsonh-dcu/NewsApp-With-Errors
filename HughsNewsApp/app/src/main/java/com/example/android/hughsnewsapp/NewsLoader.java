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

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

/** Loads a list of news articles by using an AsyncTask to perform the network request to the given URL.*/
public class NewsLoader extends AsyncTaskLoader<List<News>> {
    /** Tag for log messages */
    private static final String LOG_TAG = NewsLoader.class.getName();

    /** Query URL */
    private final String mUrl;

    /**
     * Constructor for the new NewsLoader
     * @param context of the activity
     * @param url to load data from
     * */
    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /** This is on a background thread. */
    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<News> newsArticlesList = QueryUtils.fetchNewsData(mUrl);
        return newsArticlesList;
    }
}
