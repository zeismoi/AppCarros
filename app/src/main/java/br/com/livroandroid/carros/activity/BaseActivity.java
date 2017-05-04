package br.com.livroandroid.carros.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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

/**
 * Created by ovs on 03/05/2017.
 */

public class BaseActivity extends AppCompatActivity {
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
                toast("Clicou em carros");
                break;
            case R.id.nav_item_carros_classicos:
                toast("Clicou em classicos");
                break;
            case R.id.nav_item_carros_esportivos:
                toast("Clicou em esportivos");
                break;
            case R.id.nav_item_carros_luxo:
                toast("Clicou em luxo");
                break;
            case R.id.nav_item_site_livro:
                toast("Clicou em site");
                break;
            case R.id.nav_item_settings:
                toast("Clicou em settings");
                break;
        }
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
