package br.com.legasist.controlevendas.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.parceler.Parcels;

import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.domain.Cliente;
import br.com.legasist.controlevendas.domain.Fornecedor;

public class MapaFornecedorFragment extends BaseFragment implements OnMapReadyCallback {
    //objeto que controla o GoogleMaps
    GoogleMap map;
    private Fornecedor fornecedor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mapa_fornecedor, container, false);
        setHasOptionsMenu(true);
        //Recupera o fragment q está no layout
        //utiliza o getChildFragmentManager pois é im fragment dentro do outro
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFornecFragment);
        //inicia o Google Maps dentro do fragment
        mapFragment.getMapAsync(this);
        this.fornecedor = Parcels.unwrap(getArguments().getParcelable("fornecedor"));
        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        //o método onMapReady(map) é chamdo quando a inicialização do mapa estiver ok
        this.map = map;
        if (fornecedor != null) {
            //ativa o botão para mostrar minha localização
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            map.setMyLocationEnabled(true);
            //cria o objeto LatLng com a coordenada do endereço do fornecedor
            LatLng location = new LatLng(fornecedor.latitude, fornecedor.longitude);
            //posiciona o mapa na coordenada do endereço (zoom = 13)
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 15);
            map.moveCamera(update);
            //marcador no local do endereço
            map.addMarker(new MarkerOptions()
                                .title(fornecedor.nome)
                                .snippet(fornecedor.telefone)
                                .position(location));


            //evento ao clicar no marcador
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    LatLng latLng = marker.getPosition();
                    Toast.makeText(getContext(), "Marcador: " + marker.getTitle() + " > " + latLng, Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

            //evento ao clicar na janela do marcador
            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    LatLng latLng = marker.getPosition();
                    Toast.makeText(getContext(), "Clicou no: " + marker.getTitle() + " > " + latLng, Toast.LENGTH_SHORT).show();
                }
            });

            //tipo do mapa
            //(normal, satélite, terreno ou híbrido
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_frag_mapa_fornecedor, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (map != null && fornecedor != null){
            if (item.getItemId() == R.id.action_location_fornecedor){
                //posiciona mapa na localização do endereço
                LatLng location = new LatLng(fornecedor.latitude, fornecedor.longitude);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(location,13));
            }else if (item.getItemId() == R.id.action_location_directions){
                //posiciona mapa no usuário
                toast("Mostrar mapa direções até o fornecedor");
            }else if (item.getItemId() == R.id.action_zoom_in){
                toast("Zoom +");
                map.animateCamera(CameraUpdateFactory.zoomIn());
            }else if (item.getItemId() == R.id.action_zoom_out){
                toast("Zoom -");
                map.animateCamera(CameraUpdateFactory.zoomOut());
            }else if (item.getItemId() == R.id.action_mapa_normal){
                //Modo Normal
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }else if (item.getItemId() == R.id.action_mapa_satelite){
                //Modo Satélite
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }else if (item.getItemId() == R.id.action_mapa_terreno){
                //Modo Terreno
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }else if (item.getItemId() == R.id.action_mapa_hibrido){
                //Modo Híbrido
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        }
        return super.onOptionsItemSelected(item);
    }


}
