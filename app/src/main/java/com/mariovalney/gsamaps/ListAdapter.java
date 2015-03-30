package com.mariovalney.gsamaps;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mariovalney.gsamaps.data.DataContract.AmbassadorEntry;

/**
 * Created by neo on 20/11/14.
 */
public class ListAdapter extends CursorAdapter {

    public ListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {
        public final ImageView flagView;
        public final TextView nomeView;
        public final TextView instituicaoView;

        public ViewHolder(View view) {
            flagView = (ImageView) view.findViewById(R.id.list_item_icon_flag);
            nomeView = (TextView) view.findViewById(R.id.list_item_textview_nome);
            instituicaoView = (TextView) view.findViewById(R.id.list_item_textview_instituicao);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_ambassadors, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Lendo o Cursor
        String nome =
                cursor.getString(cursor.getColumnIndex(AmbassadorEntry.COLUMN_NAME_NOME));
        String instituicao =
                cursor.getString(cursor.getColumnIndex(AmbassadorEntry.COLUMN_NAME_INSTITUICAO));
        String pais =
                cursor.getString(cursor.getColumnIndex(AmbassadorEntry.COLUMN_NAME_PAIS));

        // Populando as Views
        viewHolder.nomeView.setText(nome);
        viewHolder.instituicaoView.setText(instituicao);

        if (pais.equals("BR")) {
            viewHolder.flagView.setImageResource(R.drawable.ic_flag_br);
        } else if (pais.equals("MX")) {
            viewHolder.flagView.setImageResource(R.drawable.ic_flag_mx);
        } else {
            viewHolder.flagView.setImageResource(R.drawable.ic_flag_gsa);
        }
    }
}
