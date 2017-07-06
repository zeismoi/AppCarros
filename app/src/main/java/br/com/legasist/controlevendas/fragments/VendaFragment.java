package br.com.legasist.controlevendas.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import br.com.legasist.controlevendas.ControleVendasApplication;
import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.domain.Cliente;
import br.com.legasist.controlevendas.domain.OperacoesDB;
import br.com.legasist.controlevendas.domain.Produto;
import br.com.legasist.controlevendas.domain.Venda;
import br.com.legasist.controlevendas.fragments.dialog.DeletarVendaDialog;
import br.com.legasist.controlevendas.fragments.dialog.PesqProdutoDialog;
import livroandroid.lib.utils.IntentUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class VendaFragment extends BaseFragment {
    private Venda venda;

    EditText edtData, edtCliente, edtValor, edtDesconto, edtTotal;
    ImageButton btnSalvar, btnAdicionaProd;
    Button btnEscolherProduto;


    public VendaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_venda, container, false);
        //cliente = Parcels.unwrap(getArguments().getParcelable("cliente"));
        venda = Parcels.unwrap(getArguments().getParcelable("venda"));
        setHasOptionsMenu(true); // precisamos informar ao Android q este fragment tem menu

        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        final AutoCompleteTextView clientes = (AutoCompleteTextView) view.findViewById(R.id.auto_complete_clientes);

        edtData = (EditText) view.findViewById(R.id.textDataVenda);
        edtCliente = (EditText) view.findViewById(R.id.auto_complete_clientes);
        edtValor = (EditText) view.findViewById(R.id.textValorVenda);
        edtDesconto = (EditText) view.findViewById(R.id.textDescontoVenda);
        edtTotal = (EditText) view.findViewById(R.id.textTotalVenda);

        OperacoesDB db = new OperacoesDB(getContext());
        List<Cliente> listaCli = db.findAllClientes();
        final ArrayAdapter<String> adaptador;
        String[] autoComplArray = new String[listaCli.size()];
        final HashMap<String,String> autoComplMap = new HashMap<String, String>();
        adaptador = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        // ArrayAdapter para preencher com os clientes
        clientes.setAdapter(adaptador);

        for (Cliente cli:listaCli) {
            adaptador.add(cli.nome);
            autoComplMap.put(cli.nome, String.valueOf(cli.id));
        }
        if(listaCli.size() == 0){
            adaptador.add(getResources().getString(R.string.nao_ha_cliente));
            autoComplMap.put(getResources().getString(R.string.nao_ha_cliente), String.valueOf("-1"));
            clientes.setThreshold(adaptador.getPosition(getResources().getString(R.string.nao_ha_cliente)));
        }else {
            adaptador.add(getResources().getString(R.string.digite_o_cliente));
            autoComplMap.put(getResources().getString(R.string.digite_o_cliente), String.valueOf("-1"));
            clientes.setThreshold(adaptador.getPosition(getResources().getString(R.string.digite_o_cliente)));
        }


        if(venda != null && venda.id != 0){
            edtData.setText(dateFormat.format(venda.data));
            edtCliente.setText(Long.toString(venda.cliente));
            edtValor.setText(Double.toString(venda.valor));
            edtDesconto.setText(Double.toString(venda.desconto));
            edtTotal.setText(Double.toString(venda.total));
            if(venda.cliente != 0) {
                Cliente cli = db.findClienteById((venda.cliente));
                clientes.setText(cli.nome);
            }
        }

        btnSalvar = (ImageButton) view.findViewById(R.id.btnSalvarVenda);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OperacoesDB db = new OperacoesDB(getContext());
                if(venda == null || venda.id == 0) {
                    Venda v = new Venda();
                    try {
                        v.data = dateFormat.parse(String.valueOf(edtData.getText()));
                        /*if((String.valueOf(edtCliente.getText()) != null) && (!(String.valueOf(edtCliente.getText()).equals("")))) {
                            v.cliente = Long.parseLong(String.valueOf(edtCliente.getText()));
                        }*/
                        v.valor = Double.parseDouble(String.valueOf(edtValor.getText()));
                        v.desconto = Double.parseDouble(String.valueOf(edtDesconto.getText()));
                        v.total = Double.parseDouble(String.valueOf(edtTotal.getText()));

                        //SE NÃO EXISTIR NENHUM ITEM SELECIONADO NA COMBO, NÃO GRAVA, POIS O ID ESTAVA VINDO COMO -1
                        if(clientes.getText() != null && autoComplMap.get(String.valueOf(clientes.getText())) != String.valueOf(-1)) {
                            long id = Long.parseLong(autoComplMap.get(String.valueOf(clientes.getText())));
                            v.cliente = id;
                        }

                        db.saveVenda(v);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }finally {
                        db.close();
                    }
                }else{
                    try{
                        venda.data = dateFormat.parse(String.valueOf(edtData.getText()));
                        /*if(edtCliente.getText()!= null && !edtCliente.getText().equals("")) {
                            venda.cliente = Long.parseLong(String.valueOf(edtCliente.getText()));
                        }*/
                        venda.valor = Double.parseDouble(String.valueOf(edtValor.getText()));
                        venda.desconto = Double.parseDouble(String.valueOf(edtDesconto.getText()));
                        venda.total = Double.parseDouble(String.valueOf(edtTotal.getText()));

                        //SE NÃO EXISTIR NENHUM ITEM SELECIONADO NA COMBO, NÃO GRAVA, POIS O ID ESTAVA VINDO COMO -1
                        if(clientes.getText() != null && autoComplMap.get(String.valueOf(clientes.getText())) != String.valueOf(-1)) {
                            long id = Long.parseLong(autoComplMap.get(String.valueOf(clientes.getText())));
                            venda.cliente = id;
                        }

                        db.saveVenda(venda);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } finally {
                        db.close();
                    }

                }
                getActivity().finish();
                //Envia o evento para o Bus
                ControleVendasApplication.getInstance().getBus().post("refresh");
            }
        });

        btnEscolherProduto = (Button) view.findViewById(R.id.btnEscolherProduto);
        btnEscolherProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(getContext(), PesqProdutosActivity.class);
                startActivityForResult(intent,4);*/
                PesqProdutoDialog.show(getFragmentManager(), new PesqProdutoDialog.Callback() {
                    @Override
                    public void onProdutoUpdate(Produto produto) {

                        ControleVendasApplication.getInstance().getBus().post("refresh");
                    }
                });
            }
        });

        /*btnPesqCliente = (ImageButton) view.findViewById(R.id.btnBuscaCliente);
        btnPesqCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PesqClienteDialog.show(getFragmentManager(), new PesqClienteDialog.Callback() {
                    @Override
                    public void onClienteUpdate(Cliente cliente) {
                        toast("Cliente [" + cliente.nome + "] selecionado");
                        //Salva a catgoria depois de fechar o Dialog
                       // OperacoesDB db = new OperacoesDB(getContext());
                       // db.saveCategoria(categoria);
                        //controlevendas
                        //Atualiza o título com o novo nome
                        //CategoriaActivity a = (CarroActivity) getActivity();
                        //a.setTitle(carro.nome);
                        //Envia o evento para o Bus
                        ControleVendasApplication.getInstance().getBus().post("refresh");
                    }
                });
            }
        });*/


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onViewCreated(View view, Bundle saveInstanceState) {
        super.onViewCreated(view, saveInstanceState);
        //Atualiza a view do fragment com os dados da venda
        setTextString(R.id.tDesc, String.valueOf(venda.cliente));

    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_frag_venda, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            //toast("Editar: " + carro.nome);
            //controlevendas
            /*EditarCarroDialog.show(getFragmentManager(), carro, new EditarCarroDialog.Callback() {
                @Override
                public void onCarroUpdate(Carro carro) {
                    toast("Carro [" + carro.nome + "] atualizado");
                    //Salva o carro depois de fechar o Dialog
                    CarroDB db = new CarroDB(getContext());
                    db.save(carro);
                    //Atualiza o título com o novo nome
                    CarroActivity a = (CarroActivity) getActivity();
                    a.setTitle(carro.nome);
                    //Envia o evento para o Bus
                    ControleVendasApplication.getInstance().getBus().post("refresh");
                }
            });*/
            return true;
        } else if (item.getItemId() == R.id.action_delete){
            //toast("Deletar: " + carro.nome);
            DeletarVendaDialog.show(getFragmentManager(), new DeletarVendaDialog.Callback() {
                @Override
                public void onClickYes() {
                    toast("Venda [" + venda.data + " - " + venda.cliente + "] deletada");
                    //Deleta a venda
                    OperacoesDB db = new OperacoesDB(getActivity());
                    db.delete("venda", venda.id);
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

    //Cria o popupMenu posicionado na âncora
    private void showVideo(final String url, View ancoraView) {
        if (url != null && ancoraView != null){
            //cria o popupMenu posicionado na âncora
            PopupMenu popupMenu = new PopupMenu(getActionBar().getThemedContext(), ancoraView);
            popupMenu.inflate(R.menu.menu_popup_video);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_video_browser){
                        //abre o vídeo no Browser
                        IntentUtils.openBrowser(getContext(), url);
                    }else if (item.getItemId() == R.id.action_video_player){
                        //abre o vídeo no player de vídeo nativo
                        IntentUtils.showVideo(getContext(), url);
                    }else if (item.getItemId() == R.id.action_video_view){
                        //abre outra activity com VideoView
                    }
                    return true;
                }
            });
            popupMenu.show();
        }
    }

}
