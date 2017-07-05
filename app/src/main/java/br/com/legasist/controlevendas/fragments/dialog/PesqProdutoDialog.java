package br.com.legasist.controlevendas.fragments.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.parceler.Parcels;

import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.domain.Cliente;
import br.com.legasist.controlevendas.domain.Produto;
import br.com.legasist.controlevendas.domain.Venda;
import br.com.legasist.controlevendas.fragments.ClientesFragment;
import br.com.legasist.controlevendas.fragments.PesqProdutosFragment;

/**
 * Created by ovs on 22/05/2017.
 */

public class PesqProdutoDialog extends DialogFragment {
    private Callback callback;
    private Produto produto;
    private TextView tNome;
    private FrameLayout frameLayout;
    //Interface para retornar o resultado
    public interface Callback{
        void onProdutoUpdate(Produto produto);
    }

    //Método utilitário para criar o Dialog
    public static void show(FragmentManager fm, Callback callback){
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("pesquisar_produto");
        if (prev != null){
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        PesqProdutoDialog frag = new PesqProdutoDialog();
        frag.callback = callback;
        Bundle args = new Bundle();
        //passa o objeto produto por parâmetro
        //args.putParcelable("venda", Parcels.wrap(v));
        frag.setArguments(args);
        frag.show(ft, "pesquisar_produto");
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getDialog() == null){
            return;
        }
        //Atualiza o tamanho do Dialog
        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
        getDialog().getWindow().setLayout(width, height);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_pesquisar_produto, container, false);
        //view.findViewById(R.id.btAtualizar).setOnClickListener(onClickAtualizar());
        //view.findViewById(R.id.btFechar).setOnClickListener(onClickFechar());
        //tNome = (TextView) view.findViewById(R.id.tNomeCateg);
        frameLayout = (FrameLayout) view.findViewById(R.id.pesqProdutoFragment);
        this.produto = Parcels.unwrap(getArguments().getParcelable("produto"));

        PesqProdutosFragment frag = new PesqProdutosFragment();
        frag.setArguments(getArguments());
        //adiciona o fragment ao Layoult


        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAtualizar();
            }
        });


       // setArguments(getArguments());
        getChildFragmentManager().beginTransaction().replace(R.id.pesqProdutoFragment, frag).commit();



        /*if (produto != null){
            tNome.setText(categoria.categoria);
        }else{
            categoria = new Categoria();
        }*/
        return view;
    }

    private View.OnClickListener onClickAtualizar() {
        return new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String novoNome = tNome.getText().toString();
                if (novoNome == null || novoNome.trim().length() == 0){
                    tNome.setError("Informe a categoria");
                    return;
                }
                Context context = view.getContext();
                //Atualiza o banco de dados
               // categoria.categoria = novoNome;
                if(callback != null){
                    callback.onProdutoUpdate(produto);
                }
                //Fecha o DialogFragment
                dismiss();
            }
        };
    }

    private View.OnClickListener onClickFechar() {
        return new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Fecha o DialogFragment
                dismiss();
            }
        };
    }


}
