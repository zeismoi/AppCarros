package br.com.legasist.controlevendas.activity;

import android.os.Bundle;

import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.fragments.PesqProdutosFragment;
import br.com.legasist.controlevendas.fragments.ProdutosFragment;

public class PesqProdutosActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesq_produtos);
        setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Título
        getSupportActionBar().setTitle(R.string.produtos);
        //Adiciona o Fragment com o mesmo Bundle (args) da intent
        if (savedInstanceState == null){
            PesqProdutosFragment frag = new PesqProdutosFragment();
            frag.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.containerPesqProdutos, frag).commit();
        }


    }
}
