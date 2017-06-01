package br.com.legasist.controlevendas.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import org.parceler.Parcels;

import br.com.legasist.controlevendas.ControleVendasApplication;
import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.domain.Produto;
import br.com.legasist.controlevendas.domain.ProdutoDB;
import br.com.legasist.controlevendas.fragments.dialog.DeletarProdutoDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProdutoFragment extends BaseFragment {
    private Produto produto;

    EditText edtNome, edtEstAtual, edtEstMinimo, edtPrecoCusto, edtPrecoVenda, edtCodBarras;
    ImageButton btnSalvar, btnCodBarras;


    public ProdutoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_produto, container, false);
        produto = Parcels.unwrap(getArguments().getParcelable("produto"));
        setHasOptionsMenu(true); // precisamos informar ao Android q este fragment tem menu

        edtNome = (EditText) view.findViewById(R.id.textNomeProd);
        edtCodBarras = (EditText) view.findViewById(R.id.textCodBarras);
        edtEstAtual = (EditText) view.findViewById(R.id.textEstAtual);
        edtEstMinimo = (EditText) view.findViewById(R.id.textEstMinimo);
        edtPrecoCusto = (EditText) view.findViewById(R.id.textPrecoCusto);
        edtPrecoVenda = (EditText) view.findViewById(R.id.textPrecoVenda);

        if(produto != null && produto.id != 0){
            edtNome.setText(produto.nome);
            edtCodBarras.setText(produto.codigoBarras);
            edtEstAtual.setText(Double.toString(produto.estoqueAtual));
            edtEstMinimo.setText(Double.toString(produto.estoqueMin));
            edtPrecoCusto.setText(Double.toString(produto.precoCusto));
            edtPrecoVenda.setText(Double.toString(produto.precoVenda));
        }

        btnSalvar = (ImageButton) view.findViewById(R.id.btnSalvarProd);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProdutoDB db = new ProdutoDB(getContext());
                if(produto == null || produto.id == 0){
                    Produto p = new Produto();
                    try{
                        p.nome = String.valueOf(edtNome.getText());
                        p.codigoBarras = String.valueOf(edtCodBarras.getText());
                        p.estoqueAtual = Double.parseDouble(String.valueOf(edtEstAtual.getText()));
                        p.estoqueMin = Double.parseDouble(String.valueOf(edtEstMinimo.getText()));
                        p.precoCusto = Double.parseDouble(String.valueOf(edtPrecoCusto.getText()));
                        p.precoVenda = Double.parseDouble(String.valueOf(edtPrecoVenda.getText()));

                        db.save(p);
                    }finally {
                        db.close();
                    }
                }else{
                    try{
                        produto.nome = String.valueOf(edtNome.getText());
                        produto.codigoBarras = String.valueOf(edtCodBarras.getText());
                        produto.estoqueAtual = Double.parseDouble(String.valueOf(edtEstAtual.getText()));
                        produto.estoqueMin = Double.parseDouble(String.valueOf(edtEstMinimo.getText()));
                        produto.precoCusto = Double.parseDouble(String.valueOf(edtPrecoCusto.getText()));
                        produto.precoVenda = Double.parseDouble(String.valueOf(edtPrecoVenda.getText()));
                        db.save(produto);
                    }finally {
                        db.close();
                    }

                }

                getActivity().finish();
                //Envia o evento para o Bus
                ControleVendasApplication.getInstance().getBus().post("refresh");
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle saveInstanceState) {
        super.onViewCreated(view, saveInstanceState);
        //Atualiza a view do fragment com os dados do produto
        setTextString(R.id.tDesc, produto.nome);

        //controlevendas
        /*final ImageView imgView = (ImageView) view.findViewById(R.id.img);
        Picasso.with(getContext()).load(carro.urlFoto).fit().into(imgView);*/
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_frag_produto, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete){
            //toast("Deletar: " + carro.nome);
            DeletarProdutoDialog.show(getFragmentManager(), new DeletarProdutoDialog.Callback() {
                @Override
                public void onClickYes() {
                    toast("Produto [" + produto.nome + "] deletado");
                    //Deleta o produto
                    ProdutoDB db = new ProdutoDB(getActivity());
                    db.delete(produto);
                    //fecha a Activity
                    getActivity().finish();
                    //Envia o evento para o Bus
                    ControleVendasApplication.getInstance().getBus().post("refresh");
                }
            });
            return true;
        } else if (item.getItemId() == R.id.action_share){
            toast("Compartilhar");
        }

        return super.onOptionsItemSelected(item);

    }

}
