package br.com.livroandroid.carros.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.com.livroandroid.carros.R;
import br.com.livroandroid.carros.activity.BaseActivity;
import br.com.livroandroid.carros.fragments.CarrosFragment;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbar();
        setUpNavDrawer();

        //inicializa o layout principal com o fragments dos carros
        replaceFragment(CarrosFragment.newInstance(R.string.carros));
    }
}
