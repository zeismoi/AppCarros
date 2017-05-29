package br.com.legasist.controlevendas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import org.parceler.Parcels;

import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.domain.Carro;
import br.com.legasist.controlevendas.domain.Cliente;
import br.com.legasist.controlevendas.fragments.MapaClienteFragment;
import br.com.legasist.controlevendas.fragments.MapaFragment;

public class MapaClienteActivity extends BaseActivity {

    private Cliente cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_cliente);
        //configura a toolBar como a actionBar
        setUpToolbar();
        cliente = Parcels.unwrap(getIntent().getParcelableExtra("cliente"));
        getSupportActionBar().setTitle(cliente.nome);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null){
            //adiciona o fragment no layout da activity
            MapaClienteFragment mapaFragment = new MapaClienteFragment();
            mapaFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().replace(br.com.legasist.controlevendas.R.id.fragLayout, mapaFragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //volta para a activity ClienteActivity
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(getActivity());
                intent.putExtra("cliente", Parcels.wrap(cliente));
                NavUtils.navigateUpTo(getActivity(), intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
