package br.com.legasist.controlevendas.fragments;


import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import br.com.legasist.controlevendas.ControleVendasApplication;
import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.activity.MapaFornecedorActivity;
import br.com.legasist.controlevendas.domain.Fornecedor;
import br.com.legasist.controlevendas.domain.OperacoesDB;
import br.com.legasist.controlevendas.fragments.dialog.DeletarFornecedorDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class FornecedorFragment extends BaseFragment {
    private Fornecedor fornecedor;

    EditText edtNome, edtEndereco, edtCidade, edtTelefone, edtEmail;
    ImageButton btnSalvar, btnLocalizar;
    double lat,lng;


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

        final Spinner combo = (Spinner) view.findViewById(R.id.comboEstados);
        ArrayAdapter<CharSequence> adaptador = ArrayAdapter.createFromResource(getContext(), R.array.estados, android.R.layout.simple_spinner_item);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        combo.setAdapter(adaptador);

        edtNome = (EditText) view.findViewById(R.id.textNomeFornec);
        edtEndereco = (EditText) view.findViewById(R.id.textEnderecoFornec);
        edtCidade = (EditText) view.findViewById(R.id.textCidadeFornec);
        //edtUf = (EditText) view.findViewById(R.id.textUfFornec);
        edtTelefone = (EditText) view.findViewById(R.id.textTelefoneFornec);
        edtEmail = (EditText) view.findViewById(R.id.textEmailFornec);

        if(fornecedor != null && fornecedor.id_fornecedor != 0){
            edtNome.setText(fornecedor.nome);
            edtEndereco.setText(fornecedor.endereco);
            edtCidade.setText(fornecedor.cidade);
           // edtUf.setText(fornecedor.uf);
            edtTelefone.setText(fornecedor.telefone);
            edtEmail.setText(fornecedor.email);
            combo.setSelection(adaptador.getPosition(fornecedor.uf));
        }

        btnSalvar = (ImageButton) view.findViewById(R.id.btnSalvarFornec);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OperacoesDB db = new OperacoesDB(getContext());
                if(fornecedor == null || fornecedor.id_fornecedor == 0){
                    Fornecedor f = new Fornecedor();
                    try{
                        f.nome = String.valueOf(edtNome.getText());
                        f.endereco = String.valueOf(edtEndereco.getText());
                        f.cidade = String.valueOf(edtCidade.getText());
                        f.uf = combo.getSelectedItem().toString();//String.valueOf(edtUf.getText());
                        f.telefone = String.valueOf(edtTelefone.getText());
                        f.email = String.valueOf(edtEmail.getText());
                        f.latitude = lat;
                        f.longitude = lng;
                        db.saveFornecedor(f);
                    }finally {
                        db.close();
                    }
                }else{
                    try{
                        fornecedor.nome = String.valueOf(edtNome.getText());
                        fornecedor.endereco = String.valueOf(edtEndereco.getText());
                        fornecedor.cidade = String.valueOf(edtCidade.getText());
                        fornecedor.uf = combo.getSelectedItem().toString();  //String.valueOf(edtUf.getText());
                        fornecedor.telefone = String.valueOf(edtTelefone.getText());
                        fornecedor.email = String.valueOf(edtEmail.getText());
                        fornecedor.latitude = lat;
                        fornecedor.longitude = lng;
                        db.saveFornecedor(fornecedor);
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
                String endereco = edtEndereco.getText() + " " + edtCidade.getText() + " " + combo.getSelectedItem().toString();
                Geocoder gc = new Geocoder(getContext(), new Locale("pt", "BR"));
                List<Address> list = null;
                try {
                    list = gc.getFromLocationName(endereco, 10);
                    lat = list.get(0).getLatitude();
                    lng = list.get(0).getLongitude();

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
                //Toast.makeText(getContext(), "Retorno: " + list.get(0).getLatitude() + " - " + list.get(0).getLongitude() , Toast.LENGTH_LONG).show();
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
                    OperacoesDB db = new OperacoesDB(getActivity());
                    db.delete("fornecedor", fornecedor.id_fornecedor);
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
