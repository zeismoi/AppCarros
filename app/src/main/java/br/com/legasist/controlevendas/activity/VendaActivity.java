package br.com.legasist.controlevendas.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;

import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.domain.Cliente;
import br.com.legasist.controlevendas.domain.Venda;
import br.com.legasist.controlevendas.fragments.ClienteFragment;
import br.com.legasist.controlevendas.fragments.VendaFragment;

public class VendaActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venda);
        //Configura a toolBar como a actionBar
        setUpToolbar();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");


        //título da tollBar e botão up navigation
        Venda v = Parcels.unwrap(getIntent().getParcelableExtra("venda"));
        getSupportActionBar().setTitle(dateFormat.format(v.data));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Imagem de header na actionBar
        //controlevendas
        /*ImageView appBarImg = (ImageView) findViewById(br.com.legasist.controlevendas.R.id.appBarImg);
        Picasso.with(getContext()).load(c.urlFoto).into(appBarImg);*/
        if(savedInstanceState == null){
            //cria o fragment com o mesmo Bundle (args) da intent
            VendaFragment frag = new VendaFragment();
            frag.setArguments(getIntent().getExtras());
            //adiciona o fragment ao Layoult
            getSupportFragmentManager().beginTransaction().add(R.id.vendaFragment,frag).commit();
        }
    }

    public void setTitle(String s){
        //o título deve ser setado na CollapsingToolbarLayou
        CollapsingToolbarLayout c = (CollapsingToolbarLayout) findViewById(br.com.legasist.controlevendas.R.id.collaping_tollbar);
        c.setTitle(s);
    }
}
