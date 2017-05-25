package br.com.legasist.controlevendas.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.fragments.CarrosFragment;
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

        //FAB
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                snack(v, "Exemplo de FAB button Produtos");
            }
        });

        //Adiciona o Fragment com o mesmo Bundle (args) da intent
        if (savedInstanceState == null){
            ProdutosFragment frag = new ProdutosFragment();
            frag.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(br.com.legasist.controlevendas.R.id.container, frag).commit();
        }
    }
}
