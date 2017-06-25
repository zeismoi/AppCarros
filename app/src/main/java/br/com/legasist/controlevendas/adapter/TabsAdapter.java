package br.com.legasist.controlevendas.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;

import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.fragments.CarrosFragment;
import br.com.legasist.controlevendas.fragments.ClientesFragment;
import br.com.legasist.controlevendas.fragments.FornecedoresFragment;
import br.com.legasist.controlevendas.fragments.ProdutosFragment;
import br.com.legasist.controlevendas.fragments.VendasFragment;

/**
 * Created by ovs on 10/05/2017.
 */

public class TabsAdapter extends FragmentPagerAdapter{
    private Context context;
    Drawable myDrawable;

    public TabsAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        SpannableStringBuilder sb;
        ImageSpan span;
        if(position == 0){
            myDrawable = context.getResources().getDrawable(R.drawable.icone_branco_cliente);
            sb = new SpannableStringBuilder("[icon]"); // space added before text for convenience
            //myDrawable.setBounds(0, 0, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight());
            myDrawable.setBounds(0, 0, 78, 78);
            span = new ImageSpan(myDrawable, ImageSpan.ALIGN_BASELINE);
            sb.setSpan(span, 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
           // return context.getString(R.string.clientes);
        }else if(position == 1){
            myDrawable = context.getResources().getDrawable(R.drawable.icone_branco_produto);
            sb = new SpannableStringBuilder("[icon]"); // space added before text for convenience
            //myDrawable.setBounds(0, 9, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight());
            myDrawable.setBounds(0, 0, 78, 78);
            span = new ImageSpan(myDrawable, ImageSpan.ALIGN_BASELINE);
            sb.setSpan(span, 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
            //return context.getString(R.string.produtos);
        }else if(position == 2){
            myDrawable = context.getResources().getDrawable(R.drawable.icone_branco_fornecedor);
            sb = new SpannableStringBuilder("[icon]"); // space added before text for convenience
            //myDrawable.setBounds(0, 0, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight());
            myDrawable.setBounds(0, 0, 78, 78);
            span = new ImageSpan(myDrawable, ImageSpan.ALIGN_BASELINE);
            sb.setSpan(span, 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
            //return context.getString(R.string.fornecedores);
        }
        myDrawable = context.getResources().getDrawable(R.drawable.icone_dinheiro);
        sb = new SpannableStringBuilder("[icon]"); // space added before text for convenience
        //myDrawable.setBounds(0, 0, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight());
        myDrawable.setBounds(0, 0, 78, 78);
        span = new ImageSpan(myDrawable, ImageSpan.ALIGN_BASELINE);
        sb.setSpan(span, 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
        //return context.getString(R.string.areceber);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;
        if(position == 0){
            f = ClientesFragment.newInstance();
        }else if(position == 1){
            f = ProdutosFragment.newInstance();
        }else if (position == 2){
            f = FornecedoresFragment.newInstance();
        }else if (position == 3){
            f = VendasFragment.newInstance();
        }
        return f;
    }
}
