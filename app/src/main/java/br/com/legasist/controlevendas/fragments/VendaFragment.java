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
import android.widget.EditText;
import android.widget.ImageButton;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import br.com.legasist.controlevendas.ControleVendasApplication;
import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.domain.OperacoesDB;
import br.com.legasist.controlevendas.domain.Venda;
import br.com.legasist.controlevendas.fragments.dialog.DeletarVendaDialog;
import livroandroid.lib.utils.IntentUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class VendaFragment extends BaseFragment {
    private Venda venda;

    EditText edtData, edtCliente, edtValor, edtDesconto, edtTotal;
    ImageButton btnSalvar, btnBuscaCli, btnAdicionaProd;


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

        edtData = (EditText) view.findViewById(R.id.textDataVenda);
        edtCliente = (EditText) view.findViewById(R.id.textClienteVenda);
        edtValor = (EditText) view.findViewById(R.id.textValorVenda);
        edtDesconto = (EditText) view.findViewById(R.id.textDescontoVenda);
        edtTotal = (EditText) view.findViewById(R.id.textTotalVenda);

        if(venda != null && venda.id != 0){
            edtData.setText((CharSequence) venda.data);
            edtCliente.setText(Long.toString(venda.cliente));
            edtValor.setText(Double.toString(venda.valor));
            edtDesconto.setText(Double.toString(venda.desconto));
            edtTotal.setText(Double.toString(venda.total));
        }

        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        btnSalvar = (ImageButton) view.findViewById(R.id.btnSalvarVenda);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OperacoesDB db = new OperacoesDB(getContext());
                if(venda == null || venda.id == 0) {
                    Venda v = new Venda();
                    try {
                        v.data = dateFormat.parse(String.valueOf(edtData.getText()));
                        v.cliente = Long.parseLong(String.valueOf(edtCliente.getText()));
                        v.valor = Double.parseDouble(String.valueOf(edtValor.getText()));
                        v.desconto = Double.parseDouble(String.valueOf(edtDesconto.getText()));
                        v.total = Double.parseDouble(String.valueOf(edtTotal.getText()));
                        db.saveVenda(v);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }finally {
                        db.close();
                    }
                }else{
                    try{
                        venda.data = dateFormat.parse(String.valueOf(edtData.getText()));
                        venda.cliente = Long.parseLong(String.valueOf(edtCliente.getText()));
                        venda.valor = Double.parseDouble(String.valueOf(edtValor.getText()));
                        venda.desconto = Double.parseDouble(String.valueOf(edtDesconto.getText()));
                        venda.total = Double.parseDouble(String.valueOf(edtTotal.getText()));
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


        return view;
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
