package br.com.livroandroid.carros.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import br.com.livroandroid.carros.R;
import br.com.livroandroid.carros.activity.Prefs.ConfiguracoesActivity;
import br.com.livroandroid.carros.fragments.CarrosFragment;
import br.com.livroandroid.carros.fragments.CarrosTabFragment;
import br.com.livroandroid.carros.fragments.SiteLivroFragment;

/**
 * Created by ovs on 03/05/2017.
 */

public class BaseActivity extends livroandroid.lib.activity.BaseActivity {
    protected DrawerLayout drawerLayout;

    //configura a ToolBar
    protected void setUpToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null){
            setSupportActionBar(toolbar);
        }
    }

    //configura o NavDrawer
    protected void setUpNavDrawer(){
        //DrawerLatout
        final ActionBar actionBar = getSupportActionBar();
        //ícone do menu do NavDrawer
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null && drawerLayout != null){
            //Atualiza as imagens e os textos do header
            setNavViewValues(navigationView, R.string.nav_drawer_username, R.string.nav_drawer_email, R.mipmap.ic_launcher);
            //Trata o evento de clique no menu
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    //seleciona a linha
                    menuItem.setChecked(true);
                    //fecha o menu
                    drawerLayout.closeDrawers();
                    //Trata o evento do menu
                    onNavDrawerItemSelected(menuItem);
                    return true;
                }
            });
        }
    }

    //Atualiza os dados do header do Navigation Viewpublic
    static void setNavViewValues(NavigationView navView, int nome, int email, int img){
        View headerView = navView.getHeaderView(0);
        TextView tNome = (TextView) headerView.findViewById(R.id.tNome);
        TextView tEmail = (TextView) headerView.findViewById(R.id.tEmail);
        ImageView imgView = (ImageView) headerView.findViewById(R.id.img);

        tNome.setText(nome);
        tEmail.setText(email);
        imgView.setImageResource(img);

    }

    //Trata o evento do menu lateral
    private void onNavDrawerItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.nav_item_carros_todos:
                //Mostrar as 3 tabs(Clássicos, esportivos, luxo)
                //replaceFragment(new CarrosTabFragment());
                //nada aqui, pois somente a MainActivity tem menu lateral
                break;
            case R.id.nav_item_carros_classicos:
                //replaceFragment(CarrosFragment.newInstance(R.string.classicos));
                Intent intent = new Intent(getContext(), CarrosActivity.class);
                intent.putExtra("tipo", R.string.classicos);
                startActivity(intent);
                break;
            case R.id.nav_item_carros_esportivos:
                //replaceFragment(CarrosFragment.newInstance(R.string.esportivos));
                intent = new Intent(getContext(), CarrosActivity.class);
                intent.putExtra("tipo", R.string.esportivos);
                startActivity(intent);

                break;
            case R.id.nav_item_carros_luxo:
                //replaceFragment(CarrosFragment.newInstance(R.string.luxo));
                intent = new Intent(getContext(), CarrosActivity.class);
                intent.putExtra("tipo", R.string.luxo);
                startActivity(intent);

                break;
            case R.id.nav_item_site_livro:
                replaceFragment(new SiteLivroFragment());
                break;
            case R.id.nav_item_settings:
                intent = new Intent(this, ConfiguracoesActivity.class);
                startActivity(intent);
                break;
        }
    }

    //adiciona o Fragment ao centro da tela
    protected void replaceFragment(Fragment frag){
       // getSupportFragmentManager().beginTransaction().replace(R.id.container, frag, "TAG").commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case android.R.id.home:
                //Trata o clique no botão q abre o menu
                if(drawerLayout != null){
                    openDrawer();
                    return true;
                }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    //abre o menu lateral
    protected void openDrawer(){
        if (drawerLayout != null){
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    //fecha o menu lateral
    protected void closeDrawer(){
        if (drawerLayout != null){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void toast(String msg){
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
