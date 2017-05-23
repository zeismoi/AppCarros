package br.com.livroandroid.carros.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.ErrorDialogFragment;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import br.com.livroandroid.carros.CarrosApplication;
import br.com.livroandroid.carros.R;
import br.com.livroandroid.carros.activity.CarroActivity;
import br.com.livroandroid.carros.domain.Carro;
import br.com.livroandroid.carros.domain.CarroDB;
import br.com.livroandroid.carros.fragments.dialog.DeletarCarroDialog;
import br.com.livroandroid.carros.fragments.dialog.EditarCarroDialog;
import livroandroid.lib.utils.IntentUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarroFragment extends BaseFragment {
    private Carro carro;


    public CarroFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_carro, container, false);
        carro = Parcels.unwrap(getArguments().getParcelable("carro"));
        setHasOptionsMenu(true); // precisamos informar ao Android q este fragment tem menu
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle saveInstanceState) {
        super.onViewCreated(view, saveInstanceState);
        //Atualiza a view do fragment com os dados do carro
        setTextString(R.id.tDesc, carro.desc);
        final ImageView imgView = (ImageView) view.findViewById(R.id.img);
        Picasso.with(getContext()).load(carro.urlFoto).fit().into(imgView);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_frag_carro, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            //toast("Editar: " + carro.nome);
            EditarCarroDialog.show(getFragmentManager(), carro, new EditarCarroDialog.Callback() {
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
                    CarrosApplication.getInstance().getBus().post("refresh");
                }
            });
            return true;
        } else if (item.getItemId() == R.id.action_delete){
            //toast("Deletar: " + carro.nome);
            DeletarCarroDialog.show(getFragmentManager(), new DeletarCarroDialog.Callback() {
                @Override
                public void onClickYes() {
                    toast("Carro [" + carro.nome + "] deletado");
                    //Deleta o carro
                    CarroDB db = new CarroDB(getActivity());
                    db.delete(carro);
                    //fecha a Activity
                    getActivity().finish();
                    //Envia o evento para o Bus
                    CarrosApplication.getInstance().getBus().post("refresh");
                }
            });
            return true;
        } else if (item.getItemId() == R.id.action_share){
            toast("Compartilhar");
        }else if (item.getItemId() == R.id.action_maps) {
            toast("Mapas");
        }else if (item.getItemId() == R.id.action_video) {
            //URL do vídeo
            final String url = carro.urlVideo;
            //Lê a view que é a âncora do popup (é a view do botão da action bar)
            View menuItemView = getActivity().findViewById(item.getItemId());
            if (menuItemView != null && url != null){
                //Mostra o alerta com as opções do vídeo
                showVideo(url, menuItemView);
            }
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
