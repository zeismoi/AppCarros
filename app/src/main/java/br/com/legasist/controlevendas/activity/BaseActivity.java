package br.com.legasist.controlevendas.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.activity.Prefs.ConfiguracoesActivity;
import br.com.legasist.controlevendas.fragments.SiteLivroFragment;

/**
 * Created by ovs on 03/05/2017.
 */

public class BaseActivity extends livroandroid.lib.activity.BaseActivity {
    protected DrawerLayout drawerLayout;

    //configura a ToolBar
    protected void setUpToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(br.com.legasist.controlevendas.R.id.toolbar);
        if(toolbar != null){
            setSupportActionBar(toolbar);
        }
    }

    //configura o NavDrawer
    protected void setUpNavDrawer(){
        //DrawerLatout
        final ActionBar actionBar = getSupportActionBar();
        //ícone do menu do NavDrawer
        actionBar.setHomeAsUpIndicator(br.com.legasist.controlevendas.R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(br.com.legasist.controlevendas.R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(br.com.legasist.controlevendas.R.id.nav_view);
        if (navigationView != null && drawerLayout != null){
            //Atualiza as imagens e os textos do header
            setNavViewValues(navigationView, br.com.legasist.controlevendas.R.string.nav_drawer_username, br.com.legasist.controlevendas.R.string.nav_drawer_email, br.com.legasist.controlevendas.R.mipmap.ic_launcher);
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
        TextView tNome = (TextView) headerView.findViewById(br.com.legasist.controlevendas.R.id.tNome);
        TextView tEmail = (TextView) headerView.findViewById(br.com.legasist.controlevendas.R.id.tEmail);
        ImageView imgView = (ImageView) headerView.findViewById(br.com.legasist.controlevendas.R.id.img);

        tNome.setText(nome);
        tEmail.setText(email);
        imgView.setImageResource(img);

    }

    //Trata o evento do menu lateral
    private void onNavDrawerItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.nav_item_produtos_todos:
                Intent intent_produtos = new Intent(getContext(), ProdutosActivity.class);
                //intent_produtos.putExtra("tipo", br.com.legasist.controlevendas.R.string.classicos);
                startActivity(intent_produtos);
                break;
            case R.id.nav_item_categorias:
                //replaceFragment(CarrosFragment.newInstance(R.string.classicos));
                Intent intent_categorias = new Intent(getContext(), CategoriasActivity.class);
               // intent.putExtra("tipo", br.com.legasist.controlevendas.R.string.classicos);
                startActivity(intent_categorias);
                break;
            case R.id.nav_item_clientes:
                //replaceFragment(CarrosFragment.newInstance(R.string.esportivos));
                Intent intent_clientes = new Intent(getContext(), ClientesActivity.class);
                startActivity(intent_clientes);
                break;
            case R.id.nav_item_fornecedores:
                //replaceFragment(CarrosFragment.newInstance(R.string.luxo));
                Intent intent_fornec = new Intent(getContext(), FornecedoresActivity.class);
                startActivity(intent_fornec);
                break;
            case br.com.legasist.controlevendas.R.id.nav_item_site_livro:
                replaceFragment(new SiteLivroFragment());
                break;
            case br.com.legasist.controlevendas.R.id.nav_item_settings:
                Intent intent_config = new Intent(this, ConfiguracoesActivity.class);
                startActivity(intent_config);
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
