package br.com.legasist.controlevendas.fragments;


import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.Toast;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import br.com.legasist.controlevendas.ControleVendasApplication;
import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.activity.MapaClienteActivity;
import br.com.legasist.controlevendas.activity.MapaFornecedorActivity;
import br.com.legasist.controlevendas.domain.Cliente;
import br.com.legasist.controlevendas.domain.ClienteDB;
import br.com.legasist.controlevendas.domain.Fornecedor;
import br.com.legasist.controlevendas.domain.FornecedorDB;
import br.com.legasist.controlevendas.fragments.dialog.DeletarClienteDialog;
import br.com.legasist.controlevendas.fragments.dialog.DeletarFornecedorDialog;
import livroandroid.lib.utils.IntentUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class FornecedorFragment extends BaseFragment {
    private Fornecedor fornecedor;

    EditText edtNome, edtEndereco, edtCidade, edtUf, edtTelefone, edtEmail;
    ImageButton btnSalvar, btnLocalizar;


    public FornecedorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fornecedor, container, false);
        fornecedor = Parcels.unwrap(getArguments().getParcelable("fornecedor"));
        setHasOptionsMenu(true); // precisamos informar ao Android q este fragment tem menu

        edtNome = (EditText) view.findViewById(R.id.textNomeCli);
        edtEndereco = (EditText) view.findViewById(R.id.textEnderecoFornec);
        edtCidade = (EditText) view.findViewById(R.id.textCidadeFornec);
        edtUf = (EditText) view.findViewById(R.id.textUfFornec);
        edtTelefone = (EditText) view.findViewById(R.id.textTelefoneFornec);
        edtEmail = (EditText) view.findViewById(R.id.textEmailFornec);

        if(fornecedor != null && fornecedor.id_fornecedor != 0){
            edtNome.setText(fornecedor.nome);
            edtEndereco.setText(fornecedor.endereco);
            edtCidade.setText(fornecedor.cidade);
            edtUf.setText(fornecedor.uf);
            edtTelefone.setText(fornecedor.telefone);
            edtEmail.setText(fornecedor.email);
        }

        btnSalvar = (ImageButton) view.findViewById(R.id.btnSalvarFornec);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FornecedorDB db = new FornecedorDB(getContext());
                if(fornecedor == null || fornecedor.id_fornecedor == 0){
                    Fornecedor f = new Fornecedor();
                    try{
                        f.nome = String.valueOf(edtNome.getText());
                        f.endereco = String.valueOf(edtEndereco.getText());
                        f.cidade = String.valueOf(edtCidade.getText());
                        f.uf = String.valueOf(edtUf.getText());
                        f.telefone = String.valueOf(edtTelefone.getText());
                        f.email = String.valueOf(edtEmail.getText());
                        db.save(f);
                    }finally {
                        db.close();
                    }
                }else{
                    try{
                        fornecedor.nome = String.valueOf(edtNome.getText());
                        fornecedor.endereco = String.valueOf(edtEndereco.getText());
                        fornecedor.cidade = String.valueOf(edtCidade.getText());
                        fornecedor.uf = String.valueOf(edtUf.getText());
                        fornecedor.telefone = String.valueOf(edtTelefone.getText());
                        fornecedor.email = String.valueOf(edtEmail.getText());
                        db.save(fornecedor);
                    }finally {
                        db.close();
                    }

                }
                getActivity().finish();
                //Envia o evento para o Bus
                ControleVendasApplication.getInstance().getBus().post("refresh");
            }
        });

        btnLocalizar = (ImageButton) view.findViewById(R.id.btnLocalizacaoFornec);
        btnLocalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //para descobrir a latitude e a longitude do endereço
                String endereco = edtEndereco.getText() + " " + edtCidade.getText() + " " + edtUf.getText();
                Geocoder gc = new Geocoder(getContext(), new Locale("pt", "BR"));
                List<Address> list = null;
                try {
                    list = gc.getFromLocationName(endereco, 10);
                    double lat = list.get(0).getLatitude();
                    double lng = list.get(0).getLongitude();

                    fornecedor.latitude = lat;
                    fornecedor.longitude = lng;

                    //configura a Lat/Lng
                    setTextString(R.id.tLatLng, String.format("Lat/Lng: %s/%s", fornecedor.latitude, fornecedor.longitude));
                    //Adiciona o Fragment do mapa

                    MapaFornecedorFragment mapaFornecFragment = new MapaFornecedorFragment();
                    mapaFornecFragment.setArguments(getArguments());
                    getChildFragmentManager().beginTransaction().replace(R.id.mapFornecFragment, mapaFornecFragment).commit();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getContext(), "Retorno: " + list.get(0).getLatitude() + " - " + list.get(0).getLongitude() , Toast.LENGTH_LONG).show();
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle saveInstanceState) {
        super.onViewCreated(view, saveInstanceState);
        //Atualiza a view do fragment com os dados do fornecedor
        setTextString(R.id.tDesc, fornecedor.nome);

        //controlevendas
        /*final ImageView imgView = (ImageView) view.findViewById(R.id.img);
        Picasso.with(getContext()).load(carro.urlFoto).fit().into(imgView);*/

        if(fornecedor.latitude != 0 && fornecedor.longitude != 0) {
            //configura a Lat/Lng
            setTextString(R.id.tLatLng, String.format("Lat/Lng: %s/%s", fornecedor.latitude, fornecedor.longitude));
            //Adiciona o Fragment do mapa
            MapaFornecedorFragment mapaFornecFragment = new MapaFornecedorFragment();
            mapaFornecFragment.setArguments(getArguments());
            getChildFragmentManager().beginTransaction().replace(R.id.mapFornecFragment, mapaFornecFragment).commit();
        }
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_frag_fornecedor, menu);
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
            DeletarFornecedorDialog.show(getFragmentManager(), new DeletarFornecedorDialog.Callback() {
                @Override
                public void onClickYes() {
                    toast("Fornecedor [" + fornecedor.nome + "] deletado");
                    //Deleta o fornecedor
                    FornecedorDB db = new FornecedorDB(getActivity());
                    db.delete(fornecedor);
                    //fecha a Activity
                    getActivity().finish();
                    //Envia o evento para o Bus
                    ControleVendasApplication.getInstance().getBus().post("refresh");
                }
            });
            return true;
        } else if (item.getItemId() == R.id.action_share){
            toast("Compartilhar");
        }else if (item.getItemId() == R.id.action_mapa) {
            //abre outra activity para mostrar o mapa
            Intent intent = new Intent(getContext(), MapaFornecedorActivity.class);
            intent.putExtra("fornecedor", Parcels.wrap(fornecedor));
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }

}
