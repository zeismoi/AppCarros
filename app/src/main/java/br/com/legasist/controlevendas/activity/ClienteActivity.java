package br.com.legasist.controlevendas.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import br.com.legasist.controlevendas.domain.Carro;
import br.com.legasist.controlevendas.domain.Cliente;
import br.com.legasist.controlevendas.fragments.CarroFragment;
import br.com.legasist.controlevendas.fragments.ClienteFragment;

public class ClienteActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(br.com.legasist.controlevendas.R.layout.activity_cliente);
        //Configura a toolBar como a actionBar
        setUpToolbar();

        //título da tollBar e botão up navigation
        Cliente c = Parcels.unwrap(getIntent().getParcelableExtra("cliente"));

        if(c.nome != null) {
            getSupportActionBar().setTitle(c.nome);
        }else{
            getSupportActionBar().setTitle("Novo cliente");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Imagem de header na actionBar
        //controlevendas
        /*ImageView appBarImg = (ImageView) findViewById(br.com.legasist.controlevendas.R.id.appBarImg);
        Picasso.with(getContext()).load(c.urlFoto).into(appBarImg);*/
        if(savedInstanceState == null){
            //cria o fragment com o mesmo Bundle (args) da intent
            ClienteFragment frag = new ClienteFragment();
            frag.setArguments(getIntent().getExtras());
            //adiciona o fragment ao Layoult
            getSupportFragmentManager().beginTransaction().add(br.com.legasist.controlevendas.R.id.clienteFragment,frag).commit();
        }
    }

    public void setTitle(String s){
        //o título deve ser setado na CollapsingToolbarLayou
        CollapsingToolbarLayout c = (CollapsingToolbarLayout) findViewById(br.com.legasist.controlevendas.R.id.collaping_tollbar);
        c.setTitle(s);
    }
}
