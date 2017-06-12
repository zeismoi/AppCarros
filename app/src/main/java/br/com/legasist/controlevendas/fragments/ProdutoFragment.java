package br.com.legasist.controlevendas.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import org.parceler.Parcels;

import java.util.HashMap;
import java.util.List;

import br.com.legasist.controlevendas.ControleVendasApplication;
import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.domain.Categoria;
import br.com.legasist.controlevendas.domain.Fornecedor;
import br.com.legasist.controlevendas.domain.OperacoesDB;
import br.com.legasist.controlevendas.domain.Produto;
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

        final Spinner combo = (Spinner) view.findViewById(R.id.comboCategorias);
        final Spinner comboFornec = (Spinner) view.findViewById(R.id.comboFornecedores);

        final ArrayAdapter<String> adaptador;
        adaptador = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        combo.setAdapter(adaptador);
        OperacoesDB db = new OperacoesDB(getContext());
        List<Categoria> listaCateg = db.findAllCategorias();
        String[] spinnerArray = new String[listaCateg.size()];
        final HashMap<String,String> spinnerMap = new HashMap<String, String>();
        for (Categoria cat:listaCateg) {
            adaptador.add(cat.categoria);
            spinnerMap.put(cat.categoria, String.valueOf(cat.id));
        }

        final ArrayAdapter<String> adaptadorFornec;
        adaptadorFornec = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item);
        adaptadorFornec.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comboFornec.setAdapter(adaptadorFornec);
        List<Fornecedor> listaFornec = db.findAllFornecedores();
        String[] spinnerArrayFornec = new String[listaFornec.size()];
        final HashMap<String,String> spinnerMapFornec = new HashMap<String, String>();
        for (Fornecedor f:listaFornec) {
            adaptadorFornec.add(f.nome);
            spinnerMapFornec.put(f.nome, String.valueOf(f.id_fornecedor));
        }

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

            if(produto.categ != 0) {
                Categoria cat = db.findCategoriaById((produto.categ));
                combo.setSelection(adaptador.getPosition(cat.categoria));
            }

            if(produto.fornecedor != 0) {
                Fornecedor f = db.findFornecedorById((produto.fornecedor));
                comboFornec.setSelection(adaptadorFornec.getPosition(f.nome));
            }
        }

        btnSalvar = (ImageButton) view.findViewById(R.id.btnSalvarProd);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OperacoesDB db = new OperacoesDB(getContext());
                if(produto == null || produto.id == 0){
                    Produto p = new Produto();
                    try{
                        p.nome = String.valueOf(edtNome.getText());
                        p.codigoBarras = String.valueOf(edtCodBarras.getText());
                        p.estoqueAtual = Double.parseDouble(String.valueOf(edtEstAtual.getText()));
                        p.estoqueMin = Double.parseDouble(String.valueOf(edtEstMinimo.getText()));
                        p.precoCusto = Double.parseDouble(String.valueOf(edtPrecoCusto.getText()));
                        p.precoVenda = Double.parseDouble(String.valueOf(edtPrecoVenda.getText()));
                        //p.categoria = String.valueOf(combo.getSelectedItem());
                        long id = Long.parseLong(spinnerMap.get(String.valueOf(combo.getSelectedItem())));
                        //p.categ = new Categoria();
                        p.categ = id;

                        long idFornec = Long.parseLong(spinnerMapFornec.get(String.valueOf(comboFornec.getSelectedItem())));
                        p.fornecedor = idFornec;

                        //p.categ.id = id;
                        db.saveProduto(p);
                    }finally {
                        db.close();
                    }
                }else{
                    try{
                        produto.nome = String.valueOf(edtNome.getText());
                        produto.codigoBarras = String.valueOf(combo.getSelectedItem()); //String.valueOf(edtCodBarras.getText());
                        produto.estoqueAtual = Double.parseDouble(String.valueOf(edtEstAtual.getText()));
                        produto.estoqueMin = Double.parseDouble(String.valueOf(edtEstMinimo.getText()));
                        produto.precoCusto = Double.parseDouble(String.valueOf(edtPrecoCusto.getText()));
                        produto.precoVenda = Double.parseDouble(String.valueOf(edtPrecoVenda.getText()));
                        //produto.categoria = String.valueOf(combo.getSelectedItem());
                        long id = Long.parseLong(spinnerMap.get(String.valueOf(combo.getSelectedItem())));
                        produto.categ = id;

                        long idFornec = Long.parseLong(spinnerMapFornec.get(String.valueOf(comboFornec.getSelectedItem())));
                        produto.fornecedor = idFornec;

                        db.saveProduto(produto);
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
                    OperacoesDB db = new OperacoesDB(getActivity());
                    db.delete("produto", produto.id);
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
