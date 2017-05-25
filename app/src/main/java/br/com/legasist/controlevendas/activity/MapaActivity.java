package br.com.legasist.controlevendas.activity;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.view.MenuItem;

import org.parceler.Parcels;

import br.com.legasist.controlevendas.domain.Carro;
import br.com.legasist.controlevendas.fragments.MapaFragment;

public class MapaActivity extends BaseActivity {

    private Carro carro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(br.com.legasist.controlevendas.R.layout.activity_mapa);
        //configura a toolBar como a actionBar
        setUpToolbar();
        carro = Parcels.unwrap(getIntent().getParcelableExtra("carro"));
        getSupportActionBar().setTitle(carro.nome);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null){
            //adiciona o fragment no layout da activity
            MapaFragment mapaFragment = new MapaFragment();
            mapaFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().replace(br.com.legasist.controlevendas.R.id.fragLayout, mapaFragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //volta para a activity CarroActivity
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(getActivity());
                intent.putExtra("carro", Parcels.wrap(carro));
                NavUtils.navigateUpTo(getActivity(), intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
