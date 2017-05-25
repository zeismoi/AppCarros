package br.com.legasist.controlevendas.activity;

import android.app.backup.BackupManager;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.adapter.TabsAdapter;
import br.com.legasist.controlevendas.fragments.dialog.AboutDialog;
import livroandroid.lib.utils.Prefs;

public class MainActivity extends BaseActivity {
    private BackupManager backupManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(br.com.legasist.controlevendas.R.layout.activity_main);
        setUpToolbar();
        setUpNavDrawer();
        setupViewPagerTabs();
        //inicializa o layout principal com o fragments dos carros
        //replaceFragment(new CarrosTabFragment());

        //FAB
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                snack(v, "Exemplo de FAB button");
            }
        });

        //Gerenciador de Backup
        backupManager = new BackupManager(getContext());

    }

    //configura as tabs + viewPager
    private void setupViewPagerTabs(){
        //ViewPager
        final ViewPager viewPager = (ViewPager) findViewById(br.com.legasist.controlevendas.R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new TabsAdapter(getContext(), getSupportFragmentManager()));
        //Tabs
        TabLayout tabLayout = (TabLayout) findViewById(br.com.legasist.controlevendas.R.id.tabLayout);
        //cria as tabs com o mesmo adapter utilizado pelo ViewPager
        tabLayout.setupWithViewPager(viewPager);
        int cor = ContextCompat.getColor(getContext(), br.com.legasist.controlevendas.R.color.white);
        //cor branca no texto (o fundo azul foi definido no layoult)
        tabLayout.setTabTextColors(cor,cor);
        //Lê o índice da última tab utilizada no aplicativo
        int tabIdx = Prefs.getInteger(getContext(), "tabIdx");
        viewPager.setCurrentItem(tabIdx);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //salva o índice da p[agina/tab selecionada
                Prefs.setInteger(getContext(), "tabIdx", viewPager.getCurrentItem());

                //Faz o backup
                backupManager.dataChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(br.com.legasist.controlevendas.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == br.com.legasist.controlevendas.R.id.action_about){
            AboutDialog.showAbout(getSupportFragmentManager());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
