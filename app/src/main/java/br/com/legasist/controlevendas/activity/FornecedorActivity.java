package br.com.legasist.controlevendas.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;

import org.parceler.Parcels;

import br.com.legasist.controlevendas.domain.Cliente;
import br.com.legasist.controlevendas.domain.Fornecedor;
import br.com.legasist.controlevendas.fragments.ClienteFragment;
import br.com.legasist.controlevendas.fragments.FornecedorFragment;

public class FornecedorActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(br.com.legasist.controlevendas.R.layout.activity_fornecedor);
        //Configura a toolBar como a actionBar
        setUpToolbar();

        //título da tollBar e botão up navigation
        Fornecedor f = Parcels.unwrap(getIntent().getParcelableExtra("fornecedor"));
        getSupportActionBar().setTitle(f.nome);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Imagem de header na actionBar
        //controlevendas
        /*ImageView appBarImg = (ImageView) findViewById(br.com.legasist.controlevendas.R.id.appBarImg);
        Picasso.with(getContext()).load(c.urlFoto).into(appBarImg);*/
        if(savedInstanceState == null){
            //cria o fragment com o mesmo Bundle (args) da intent
            FornecedorFragment frag = new FornecedorFragment();
            frag.setArguments(getIntent().getExtras());
            //adiciona o fragment ao Layoult
            getSupportFragmentManager().beginTransaction().add(br.com.legasist.controlevendas.R.id.fornecedorFragment,frag).commit();
        }
    }

    public void setTitle(String s){
        //o título deve ser setado na CollapsingToolbarLayou
        CollapsingToolbarLayout c = (CollapsingToolbarLayout) findViewById(br.com.legasist.controlevendas.R.id.collaping_tollbar);
        c.setTitle(s);
    }
}
