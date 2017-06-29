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
import br.com.legasist.controlevendas.domain.Categoria;
import br.com.legasist.controlevendas.domain.Cliente;
import br.com.legasist.controlevendas.fragments.ClienteFragment;
import br.com.legasist.controlevendas.fragments.ClientesFragment;

import static br.com.legasist.controlevendas.R.string.categoria;

/**
 * Created by ovs on 22/05/2017.
 */

public class PesqClienteDialog extends DialogFragment {
    private Callback callback;
    private Cliente cliente;
    private TextView tNome;
    //Interface para retornar o resultado
    public interface Callback{
        void onClienteUpdate(Cliente cliente);
    }

    //Método utilitário para criar o Dialog
    public static void show(FragmentManager fm, Callback callback){
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("pesquisar_cliente");
        if (prev != null){
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        PesqClienteDialog frag = new PesqClienteDialog();
        frag.callback = callback;
        Bundle args = new Bundle();
        //passa o objeto categoria por parâmetro
        //args.putParcelable("cliente", Parcels.wrap(cliente));
        frag.setArguments(args);
        frag.show(ft, "pesquisar_cliente");
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
        View view = inflater.inflate(R.layout.dialog_pesquisar_cliente, container, false);
        //view.findViewById(R.id.btAtualizar).setOnClickListener(onClickAtualizar());
        //view.findViewById(R.id.btFechar).setOnClickListener(onClickFechar());
        //tNome = (TextView) view.findViewById(R.id.tNomeCateg);
        this.cliente = Parcels.unwrap(getArguments().getParcelable("cliente"));

        ClientesFragment frag = new ClientesFragment();
       // frag.setArguments(getExtras());
        //adiciona o fragment ao Layoult

       // setArguments(getArguments());
        getChildFragmentManager().beginTransaction().replace(R.id.pesqClienteFragment, frag).commit();

        /*if (cliente != null){
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
                    callback.onClienteUpdate(cliente);
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
