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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import br.com.legasist.controlevendas.ControleVendasApplication;
import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.activity.CarroActivity;
import br.com.legasist.controlevendas.activity.MapaActivity;
import br.com.legasist.controlevendas.activity.MapaClienteActivity;
import br.com.legasist.controlevendas.domain.Carro;
import br.com.legasist.controlevendas.domain.CarroDB;
import br.com.legasist.controlevendas.domain.Cliente;
import br.com.legasist.controlevendas.domain.ClienteDB;
import br.com.legasist.controlevendas.fragments.dialog.DeletarCarroDialog;
import br.com.legasist.controlevendas.fragments.dialog.DeletarClienteDialog;
import br.com.legasist.controlevendas.fragments.dialog.EditarCarroDialog;
import livroandroid.lib.utils.IntentUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClienteFragment extends BaseFragment {
    private Cliente cliente;

    EditText edtNome, edtEndereco, edtCidade, edtCelular, edtEmail;
    ImageButton btnSalvar, btnLocalizar;


    public ClienteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cliente, container, false);
        cliente = Parcels.unwrap(getArguments().getParcelable("cliente"));
        setHasOptionsMenu(true); // precisamos informar ao Android q este fragment tem menu

        final Spinner combo = (Spinner) view.findViewById(R.id.comboEstados);
        ArrayAdapter<CharSequence> adaptador = ArrayAdapter.createFromResource(getContext(), R.array.estados, android.R.layout.simple_spinner_item);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        combo.setAdapter(adaptador);

        edtNome = (EditText) view.findViewById(R.id.textNomeCli);
        edtEndereco = (EditText) view.findViewById(R.id.textEnderecoCli);
        edtCidade = (EditText) view.findViewById(R.id.textCidadeCli);
        //edtUf = (EditText) view.findViewById(R.id.textUfCli);
        edtCelular = (EditText) view.findViewById(R.id.textCelularCli);
        edtEmail = (EditText) view.findViewById(R.id.textEmailCli);

        if(cliente != null && cliente.id != 0){
            edtNome.setText(cliente.nome);
            edtEndereco.setText(cliente.endereco);
            edtCidade.setText(cliente.cidade);
            //edtUf.setText(cliente.uf);
            edtCelular.setText(cliente.celular);
            edtEmail.setText(cliente.email);
            combo.setSelection(adaptador.getPosition(cliente.uf));
        }

        btnSalvar = (ImageButton) view.findViewById(R.id.btnSalvarCli);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClienteDB db = new ClienteDB(getContext());
                if(cliente == null || cliente.id == 0){
                    Cliente c = new Cliente();
                    try{
                        c.nome = String.valueOf(edtNome.getText());
                        c.endereco = String.valueOf(edtEndereco.getText());
                        c.cidade = String.valueOf(edtCidade.getText());
                        c.uf = combo.getSelectedItem().toString(); //String.valueOf(edtUf.getText());
                        c.celular = String.valueOf(edtCelular.getText());
                        c.email = String.valueOf(edtEmail.getText());
                        db.save(c);
                    }finally {
                        db.close();
                    }
                }else{
                    try{
                        cliente.nome = String.valueOf(edtNome.getText());
                        cliente.endereco = String.valueOf(edtEndereco.getText());
                        cliente.cidade = String.valueOf(edtCidade.getText());
                        cliente.uf = combo.getSelectedItem().toString(); //String.valueOf(edtUf.getText());
                        cliente.celular = String.valueOf(edtCelular.getText());
                        cliente.email = String.valueOf(edtEmail.getText());
                        db.save(cliente);
                    }finally {
                        db.close();
                    }

                }
                getActivity().finish();
                //Envia o evento para o Bus
                ControleVendasApplication.getInstance().getBus().post("refresh");
            }
        });

        btnLocalizar = (ImageButton) view.findViewById(R.id.btnLocalizacaoCli);
        btnLocalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //para descobrir a latitude e a longitude do endereço
                String endereco = edtEndereco.getText() + " " + edtCidade.getText() + " " + combo.getSelectedItem().toString();;
                Geocoder gc = new Geocoder(getContext(), new Locale("pt", "BR"));
                List<Address> list = null;
                try {
                    list = gc.getFromLocationName(endereco, 10);
                    double lat = list.get(0).getLatitude();
                    double lng = list.get(0).getLongitude();

                    cliente.latitude = lat;
                    cliente.longitude = lng;

                    //configura a Lat/Lng
                    setTextString(R.id.tLatLng, String.format("Lat/Lng: %s/%s", cliente.latitude, cliente.longitude));
                    //Adiciona o Fragment do mapa

                    MapaClienteFragment mapaFragment = new MapaClienteFragment();
                    mapaFragment.setArguments(getArguments());
                    getChildFragmentManager().beginTransaction().replace(R.id.mapCliFragment, mapaFragment).commit();

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
        //Atualiza a view do fragment com os dados do cliente
        setTextString(R.id.tDesc, cliente.nome);

        //controlevendas
        /*final ImageView imgView = (ImageView) view.findViewById(R.id.img);
        Picasso.with(getContext()).load(carro.urlFoto).fit().into(imgView);*/

        if(cliente.latitude != 0 && cliente.longitude != 0) {
            //configura a Lat/Lng
            setTextString(R.id.tLatLng, String.format("Lat/Lng: %s/%s", cliente.latitude, cliente.longitude));
            //Adiciona o Fragment do mapa
            MapaClienteFragment mapaCliFragment = new MapaClienteFragment();
            mapaCliFragment.setArguments(getArguments());
            getChildFragmentManager().beginTransaction().replace(R.id.mapCliFragment, mapaCliFragment).commit();
        }
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_frag_cliente, menu);
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
            DeletarClienteDialog.show(getFragmentManager(), new DeletarClienteDialog.Callback() {
                @Override
                public void onClickYes() {
                    toast("Cliente [" + cliente.nome + "] deletado");
                    //Deleta o cliente
                    ClienteDB db = new ClienteDB(getActivity());
                    db.delete(cliente);
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
            Intent intent = new Intent(getContext(), MapaClienteActivity.class);
            intent.putExtra("cliente", Parcels.wrap(cliente));
            startActivity(intent);
        }else if (item.getItemId() == R.id.action_video) {
            //URL do vídeo
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
