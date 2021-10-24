package com.example.android.newsfeed_project;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class SportsNewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<News>>  {

    private static final int LOADER_ID = 1;
    //    private static final String queryUrl = "https://content.guardianapis.com/search?order-by=newest&show-tags=contributor&show-fields=all&api-key=test";
    private static final String queryUrl = "https://content.guardianapis.com/search";
    private static List<News> newsList = new ArrayList<News>();
    private static int currentPage = 1;
    //    private static final String queryUrl = "https://content.guardianapis.com/search?order-by=newest&page=2&show-tags=contributor&show-fields=all&api-key=test";
    private NewsAdapter mAdapter;

    private View loadingIndicator;

    private TextView emptyListText;

    public SportsNewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        newsList.clear();

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);

        ListView listView = rootView.findViewById(R.id.list);
        //TODO: add view pager functionality to change the page
//        LayoutInflater inflater = getLayoutInflater();
//        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.header, listView, false);
//        listView.addFooterView(header, null, false);

        loadingIndicator = rootView.findViewById(R.id.loading_indicator);
        emptyListText = rootView.findViewById(R.id.empty_list_text);
        listView.setEmptyView(emptyListText);

        mAdapter = new NewsAdapter(getActivity(), newsList);
        listView.setAdapter(mAdapter);

        //TODO: SetOnItemClickListener to open web url of the News when clicked

        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            if (getLoaderManager().getLoader(LOADER_ID) == null) {
                getLoaderManager().initLoader(LOADER_ID, null, this);
            }
            else {
                getLoaderManager().restartLoader(LOADER_ID, null, this);
            }
        } else {
            emptyListText.setText(getString(R.string.no_internet_connection));
        }

        return rootView;
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        newsList.clear();
        mAdapter.notifyDataSetChanged();

        loadingIndicator.setVisibility(View.VISIBLE);

        Uri uri = Uri.parse(queryUrl);
        Uri.Builder uriBuilder = uri.buildUpon();
        uriBuilder.appendQueryParameter("order-by", "newest");
        uriBuilder.appendQueryParameter("section", "sport");
        uriBuilder.appendQueryParameter("page-size", "40");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("page", String.valueOf(currentPage));
        uriBuilder.appendQueryParameter("show-fields", "all");
        uriBuilder.appendQueryParameter("api-key", "test");

        return new NewsLoader(getActivity(), uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        loadingIndicator.setVisibility(View.GONE);
        if (data != null) {
            newsList.clear();
            newsList.addAll(data);
            mAdapter.notifyDataSetChanged();

        } else if (!QueryUtils.isNetworkConnected()) {
            newsList.clear();
            mAdapter.notifyDataSetChanged();
            emptyListText.setText(getString(R.string.no_internet_connection));
        }
        else {
            newsList.clear();
            mAdapter.notifyDataSetChanged();
            emptyListText.setText(getString(R.string.no_news_found));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }

}
