package br.com.legasist.controlevendas.activity;

import android.os.Bundle;

import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.fragments.CategoriasFragment;

public class CategoriasActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);
        setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Título
        getSupportActionBar().setTitle(R.string.categorias);
        //Adiciona o Fragment com o mesmo Bundle (args) da intent
        if (savedInstanceState == null){
            CategoriasFragment frag = new CategoriasFragment();
            frag.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.containerCategorias, frag).commit();
        }


    }
}
