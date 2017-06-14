package br.com.legasist.controlevendas.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;

import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.activity.BaseActivity;
import br.com.legasist.controlevendas.domain.Cliente;
import br.com.legasist.controlevendas.domain.Produto;
import br.com.legasist.controlevendas.fragments.ClienteFragment;
import br.com.legasist.controlevendas.fragments.ProdutoFragment;

public class ProdutoActivity extends BaseActivity {

    public static byte[] imagemBytes = new byte[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto);
        //Configura a toolBar como a actionBar
        setUpToolbar();

        //título da tollBar e botão up navigation
        Produto p = Parcels.unwrap(getIntent().getParcelableExtra("produto"));
        getSupportActionBar().setTitle(p.nome);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Imagem de header na actionBar
        //controlevendas
        /*ImageView appBarImg = (ImageView) findViewById(br.com.legasist.controlevendas.R.id.appBarImg);
        Picasso.with(getContext()).load(c.urlFoto).into(appBarImg);*/
        if(savedInstanceState == null){
            //cria o fragment com o mesmo Bundle (args) da intent
            ProdutoFragment frag = new ProdutoFragment();
            frag.setArguments(getIntent().getExtras());
            //adiciona o fragment ao Layoult
            getSupportFragmentManager().beginTransaction().add(R.id.produtoFragment,frag).commit();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 2 && resultCode == RESULT_OK){
            ImageView fotoProd = (ImageView) findViewById(R.id.imgProd);
            Bundle extras = data.getExtras();
            Bitmap imagemBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imagemBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            imagemBytes = stream.toByteArray();


            fotoProd.setImageBitmap(imagemBitmap);
        }

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        //super.onActivityResult(requestCode, resultCode, data);
        if(result!= null){
            EditText edtCodBarras = (EditText) findViewById(R.id.textCodBarras);
            String barCode = result.getContents();
            if(barCode != null && !"".equals(barCode)){
                edtCodBarras.setText(barCode);
            }
        }

    }

    public void setTitle(String s){
        //o título deve ser setado na CollapsingToolbarLayou
        CollapsingToolbarLayout c = (CollapsingToolbarLayout) findViewById(br.com.legasist.controlevendas.R.id.collaping_tollbar);
        c.setTitle(s);
    }
}
