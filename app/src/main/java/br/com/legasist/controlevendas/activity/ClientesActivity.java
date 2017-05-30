package br.com.legasist.controlevendas.activity;

import android.os.Bundle;
import android.view.View;

import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.fragments.ClientesFragment;

public class ClientesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes);
        setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Título
        getSupportActionBar().setTitle(R.string.clientes);
        //Adiciona o Fragment com o mesmo Bundle (args) da intent
        if (savedInstanceState == null){
            ClientesFragment frag = new ClientesFragment();
            frag.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.containerClientes, frag).commit();
        }


    }
}
