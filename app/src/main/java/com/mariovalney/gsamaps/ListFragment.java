package com.mariovalney.gsamaps;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mariovalney.gsamaps.data.DataContract.AmbassadorEntry;

/**
 * Created by neo on 18/11/14.
 */
public class ListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static ListAdapter listAdapter;
    private static ListView listView;
    private int mPositionCursor = ListView.INVALID_POSITION;
    public static final String SELECTED_KEY = "selected_ambassador";

    private static final String[] LIST_COLUMNS = {
            AmbassadorEntry.TABLE_NAME + "." + AmbassadorEntry._ID,
            AmbassadorEntry.COLUMN_NAME_NOME,
            AmbassadorEntry.COLUMN_NAME_PAIS,
            AmbassadorEntry.COLUMN_NAME_INSTITUICAO
    };

    private static final int LIST_LOADER = 0;

    public ListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        listAdapter = new ListAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.list_fragment, container, false);

        listView = (ListView) rootView.findViewById(R.id.listview_lista);
        listView.setAdapter(listAdapter);

        // Implementando o Clique no item da listagem de Embaixadores
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long l) {

                Cursor cursor = listAdapter.getCursor();

                if (cursor != null && cursor.moveToPosition(position)) {
                    long id = cursor.getLong(cursor.getColumnIndex(AmbassadorEntry._ID));
                    ((Callback) getActivity()).onItemSelected(id);
                }

                mPositionCursor = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPositionCursor = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LIST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPositionCursor != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPositionCursor);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LIST_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

        Uri ambassadoList = AmbassadorEntry.buildAmbassadorListUri();

        return new CursorLoader(
                getActivity(),
                ambassadoList,
                LIST_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        listAdapter.swapCursor(cursor);

        if (mPositionCursor != ListView.INVALID_POSITION) {
            listView.smoothScrollToPosition(mPositionCursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        listAdapter.swapCursor(null);
    }

    public interface Callback {
        public void onItemSelected(long id);
    }
}
