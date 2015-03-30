package com.mariovalney.gsamaps;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Mário Valney on 18/11/14.
 * mail: mariovalney@gmail.com
 */
public class AmbassadorFragmentDetails extends Fragment {

    public static View mView;

    public static TextView AMBASSADOR_DETAIL_NOME_VIEW;
    public static TextView AMBASSADOR_DETAIL_INSTITUICAO_VIEW;
    public static TextView AMBASSADOR_DETAIL_END_UM_VIEW;
    public static TextView AMBASSADOR_DETAIL_END_DOIS_VIEW;

    public AmbassadorFragmentDetails() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Cria a View
        mView = inflater.inflate(R.layout.ambassador_fragment, container, false);

        AMBASSADOR_DETAIL_NOME_VIEW = (TextView) mView.findViewById(
                R.id.ambassador_textview_nome);
        AMBASSADOR_DETAIL_INSTITUICAO_VIEW = (TextView) mView.findViewById(
                R.id.ambassador_textview_instituicao);
        AMBASSADOR_DETAIL_END_UM_VIEW = (TextView) mView.findViewById(
                R.id.ambassador_textview_instituicao_endereco_linha1);
        AMBASSADOR_DETAIL_END_DOIS_VIEW = (TextView) mView.findViewById(
                R.id.ambassador_textview_instituicao_endereco_linha2);

        return mView;
    }
}
