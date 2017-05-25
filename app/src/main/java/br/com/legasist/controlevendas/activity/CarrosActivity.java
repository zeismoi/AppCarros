package br.com.legasist.controlevendas.activity;

import android.os.Bundle;

import br.com.legasist.controlevendas.fragments.CarrosFragment;

public class CarrosActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(br.com.legasist.controlevendas.R.layout.activity_carros);
        setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Título
        getSupportActionBar().setTitle(getString(getIntent().getIntExtra("tipo",0)));
        //Adiciona o Fragment com o mesmo Bundle (args) da intent
        if (savedInstanceState == null){
            CarrosFragment frag = new CarrosFragment();
            frag.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(br.com.legasist.controlevendas.R.id.container, frag).commit();
        }
    }
}
