package br.com.livroandroid.carros.activity;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import br.com.livroandroid.carros.R;
import br.com.livroandroid.carros.domain.Carro;
import br.com.livroandroid.carros.fragments.CarroFragment;

public class CarroActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carro);
        //Configura a toolBar como a actionBar
        setUpToolbar();

        //título da tollBar e botão up navigation
        Carro c = Parcels.unwrap(getIntent().getParcelableExtra("carro"));
        getSupportActionBar().setTitle(c.nome);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Imagem de header na actionBar
        ImageView appBarImg = (ImageView) findViewById(R.id.appBarImg);
        Picasso.with(getContext()).load(c.urlFoto).into(appBarImg);
        if(savedInstanceState == null){
            //cria o fragment com o mesmo Bundle (args) da intent
            CarroFragment frag = new CarroFragment();
            frag.setArguments(getIntent().getExtras());
            //adiciona o fragment ao Layoult
            getSupportFragmentManager().beginTransaction().add(R.id.carroFragment,frag).commit();
        }
    }

    public void setTitle(String s){
        //o título deve ser setado na CollapsingToolbarLayou
        CollapsingToolbarLayout c = (CollapsingToolbarLayout) findViewById(R.id.collaping_tollbar);
        c.setTitle(s);
    }
}
