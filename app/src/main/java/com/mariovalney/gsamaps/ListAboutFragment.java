package com.mariovalney.gsamaps;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by neo on 20/11/14.
 */
public class ListAboutFragment extends Fragment {

    public ListAboutFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Opções

        String [] aboutOptions = {
                getString(R.string.settings_know_gsa),
                getString(R.string.settings_gsa_video)
        };

        List<String> listaDeOpcoes = new ArrayList<String>(Arrays.asList(aboutOptions));

        // Criando o adaptador da lista
        final ArrayAdapter<String> listaDeOpcoesAdapter =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.list_item_about,
                        R.id.list_item_textview_title,
                        listaDeOpcoes);

        View rootView = inflater.inflate(R.layout.list_fragment, container, false);

        // Seta o adaptador à listview
        ListView listView = (ListView) rootView.findViewById(R.id.listview_lista);
        listView.setAdapter(listaDeOpcoesAdapter);

        // Implementando o Clique nos itens da Lista
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long l) {
                // Atualiza os dados do aplicativo sincronizando com o Web Service
                if (position == 0) {
                    // Abre o navegador na URL do GSA Program
                    Intent intent = new Intent(Intent.ACTION_VIEW)
                            .setData(Uri.parse(getString(R.string.url_gsa_program)));
                    startActivity(intent);
                }
                else if (position == 1) {
                    // Abre o navegador na URL do GSA Program
                    Intent intent = new Intent(Intent.ACTION_VIEW)
                            .setData(Uri.parse(getString(R.string.url_gsa_video)));
                    startActivity(intent);
                }

            }
        });

        return rootView;
    }
}