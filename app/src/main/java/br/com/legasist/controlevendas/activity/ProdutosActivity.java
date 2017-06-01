package br.com.legasist.controlevendas.activity;

import android.os.Bundle;

import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.fragments.ClientesFragment;
import br.com.legasist.controlevendas.fragments.ProdutosFragment;

public class ProdutosActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos);
        setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Título
        getSupportActionBar().setTitle(R.string.produtos);
        //Adiciona o Fragment com o mesmo Bundle (args) da intent
        if (savedInstanceState == null){
            ProdutosFragment frag = new ProdutosFragment();
            frag.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.containerProdutos, frag).commit();
        }


    }
}
