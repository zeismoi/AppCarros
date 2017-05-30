package br.com.legasist.controlevendas.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.fragments.CarrosFragment;
import br.com.legasist.controlevendas.fragments.ClientesFragment;
import br.com.legasist.controlevendas.fragments.ProdutosFragment;

/**
 * Created by ovs on 10/05/2017.
 */

public class TabsAdapter extends FragmentPagerAdapter{
    private Context context;

    public TabsAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0){
            return context.getString(R.string.clientes);
        }else if(position == 1){
            return context.getString(R.string.vendas);
        }
        return context.getString(R.string.areceber);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;
        if(position == 0){
            f = ClientesFragment.newInstance();
        }else if(position == 1){
            f = CarrosFragment.newInstance(br.com.legasist.controlevendas.R.string.esportivos);
        }else{
            f = CarrosFragment.newInstance(br.com.legasist.controlevendas.R.string.luxo);
        }
        return f;
    }
}
