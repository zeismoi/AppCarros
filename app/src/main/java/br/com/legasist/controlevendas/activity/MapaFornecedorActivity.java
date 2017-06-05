package br.com.legasist.controlevendas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import org.parceler.Parcels;

import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.domain.Cliente;
import br.com.legasist.controlevendas.domain.Fornecedor;
import br.com.legasist.controlevendas.fragments.MapaClienteFragment;
import br.com.legasist.controlevendas.fragments.MapaFornecedorFragment;

public class MapaFornecedorActivity extends BaseActivity {

    private Fornecedor fornecedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_fornecedor);
        //configura a toolBar como a actionBar
        setUpToolbar();
        fornecedor = Parcels.unwrap(getIntent().getParcelableExtra("fornecedor"));
        getSupportActionBar().setTitle(fornecedor.nome);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null){
            //adiciona o fragment no layout da activity
            MapaFornecedorFragment mapaFragment = new MapaFornecedorFragment();
            mapaFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().replace(R.id.fragLayout, mapaFragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //volta para a activity ClienteActivity
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(getActivity());
                intent.putExtra("fornecedor", Parcels.wrap(fornecedor));
                NavUtils.navigateUpTo(getActivity(), intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
