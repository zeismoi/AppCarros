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
import android.widget.TextView;

import org.parceler.Parcels;

import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.domain.Carro;
import br.com.legasist.controlevendas.domain.Categoria;

/**
 * Created by ovs on 22/05/2017.
 */

public class CategoriaDialog extends DialogFragment {
    private Callback callback;
    private Categoria categoria;
    private TextView tNome;
    //Interface para retornar o resultado
    public interface Callback{
        void onCategoriaUpdate(Categoria categoria);
    }

    //Método utilitário para criar o Dialog
    public static void show(FragmentManager fm, Categoria categoria, Callback callback){
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("editar_categoria");
        if (prev != null){
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        CategoriaDialog frag = new CategoriaDialog();
        frag.callback = callback;
        Bundle args = new Bundle();
        //passa o objeto categoria por parâmetro
        args.putParcelable("categoria", Parcels.wrap(categoria));
        frag.setArguments(args);
        frag.show(ft, "editar_categoria");
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getDialog() == null){
            return;
        }
        //Atualiza o tamanho do Dialog
        int width = getResources().getDimensionPixelSize(br.com.legasist.controlevendas.R.dimen.popup_width);
        int height = getResources().getDimensionPixelSize(br.com.legasist.controlevendas.R.dimen.popup_height);
        getDialog().getWindow().setLayout(width, height);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_editar_categoria, container, false);
        view.findViewById(R.id.btAtualizar).setOnClickListener(onClickAtualizar());
        view.findViewById(R.id.btFechar).setOnClickListener(onClickFechar());
        tNome = (TextView) view.findViewById(R.id.tNomeCateg);
        this.categoria = Parcels.unwrap(getArguments().getParcelable("categoria"));
        if (categoria != null){
            tNome.setText(categoria.categoria);
        }else{
            categoria = new Categoria();
        }
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
                categoria.categoria = novoNome;
                if(callback != null){
                    callback.onCategoriaUpdate(categoria);
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
