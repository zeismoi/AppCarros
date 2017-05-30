package br.com.legasist.controlevendas.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.adapter.TabsAdapter;

public class ClientesTabFragment extends BaseFragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clientes_tab, container, false);

        //ViewPager
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPagerCli);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new TabsAdapter(getContext(), getChildFragmentManager()));
        //Tabs
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        //cria as tabs com o mesmo adapter utilizado pelo VirePager
        tabLayout.setupWithViewPager(viewPager);
        int cor = ContextCompat.getColor(getContext(), br.com.legasist.controlevendas.R.color.white);
        //cor branca no texto (o fundo azul foi definido no layoult)
        tabLayout.setTabTextColors(cor,cor);
        return view;
    }


}
