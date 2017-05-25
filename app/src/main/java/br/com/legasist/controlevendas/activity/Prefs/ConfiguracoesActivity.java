package br.com.legasist.controlevendas.activity.Prefs;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import br.com.legasist.controlevendas.activity.BaseActivity;

public class ConfiguracoesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(br.com.legasist.controlevendas.R.layout.activity_configuracoes);
        setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Adiciona o Fragment de configurações
        if (savedInstanceState == null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(br.com.legasist.controlevendas.R.id.container, new PrefsFragment());
            ft.commit();
        }
    }

    //Fragment q carrega o Layout com as configurações
    public static class PrefsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            //carrega as configurações
            addPreferencesFromResource(br.com.legasist.controlevendas.R.xml.preferences);
        }
    }
}
