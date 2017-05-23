package br.com.livroandroid.carros.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.livroandroid.carros.CarrosApplication;
import br.com.livroandroid.carros.R;
import br.com.livroandroid.carros.activity.CarroActivity;
import br.com.livroandroid.carros.adapter.CarroAdapter;
import br.com.livroandroid.carros.domain.Carro;
import br.com.livroandroid.carros.domain.CarroDB;
import br.com.livroandroid.carros.domain.CarroService;
import livroandroid.lib.utils.AndroidUtils;
import livroandroid.lib.utils.IOUtils;
import livroandroid.lib.utils.SDCardUtils;

public class CarrosFragment extends BaseFragment {
    private int tipo;
    protected RecyclerView recyclerView;
    private List<Carro> carros;
    private SwipeRefreshLayout swipeLayout;
    private ActionMode actionMode;
    //private Intent shareIntent;

    //Método para instanciar esse fragment pelo tipo
    public static CarrosFragment newInstance(int tipo){
        Bundle args = new Bundle();
        args.putInt("tipo", tipo);
        CarrosFragment f = new CarrosFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            //Lê os tipos dos argumentos
            this.tipo = getArguments().getInt("tipo");
        }
        //Registra a classe para receber eventos
        CarrosApplication.getInstance().getBus().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carros, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        //Swipe to Refresh
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefresh);
        swipeLayout.setOnRefreshListener(OnRefreshListener());
        swipeLayout.setColorSchemeResources(R.color.refresh_progress_1, R.color.refresh_progress_2, R.color.refresh_progress_3);

        //FAB
        /*view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                snack(recyclerView, "Exemplo de FAB button");
            }
        });*/

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Cancela o recebimento de enventos
        CarrosApplication.getInstance().getBus().unregister(this);
    }

    @Subscribe
    public void onBusAtualizarListaCarros(String refresh){
        //Recebeu o evento, atualiza a lista
        taskCarros(false);
    }

    private SwipeRefreshLayout.OnRefreshListener OnRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                //Valida se existe conexão ao fazer o gesto Pull to Refresh
                if (AndroidUtils.isNetworkAvailable(getContext())){
                    //atualiza ao fazer o gesto Pull to Refresh
                    taskCarros(true);
                }else{
                    swipeLayout.setRefreshing(false);
                    snack(recyclerView, R.string.error_conexao_indisponivel);
                }
            }
        };
    }

    @Override
    public void onActivityCreated(@Nullable Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);
        taskCarros(false);
    }

    private void taskCarros(boolean pullToRefresh){
        //busca os carros pelo tipo
        /*try {
            this.carros = CarroService.getCarros(getContext(), tipo);
            //atualiza a lista
            recyclerView.setAdapter(new CarroAdapter(getContext(), carros, onClickCarro()));
        } catch (IOException e) {
            Log.e("livro", e.getMessage(), e);
        }*/
        //Busca os carros: Dispara a Task
        //new GetCarrosTask().execute();
        startTask("carros", new GetCarrosTask(pullToRefresh), pullToRefresh? R.id.swipeToRefresh : R.id.progress);
    }



    private CarroAdapter.CarroOnClickListener onClickCarro(){
        return new CarroAdapter.CarroOnClickListener(){
            @Override
            public void onClickCarro(View view, int idx){
                Carro c = carros.get(idx);
                if (actionMode == null){
                    Intent intent = new Intent(getContext(), CarroActivity.class);
                    intent.putExtra("carro", Parcels.wrap(c));//converte o objeto para Parcelable
                    startActivity(intent);
                }else{//Se a CAB está ativada
                    //seleciona o carro
                    c.selected = !c.selected;
                    //Atualiza o título com a quantidade de carros selecionados
                    updateActionModeTilte();
                    //redesenha a lista
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onLongClickCarro(View view, int idx) {
                if (actionMode != null) {
                    return;
                }
                //liga a action bar de contexto CAB
                actionMode = getAppCompatActivity().startSupportActionMode(getActionModeCallBack());
                Carro c = carros.get(idx);
                c.selected = true; //seleciona o carro
                //solicita ao Android para desenhar a lista novamente
                recyclerView.getAdapter().notifyDataSetChanged();
                //atualiza o título para mostrar a quantidade de carros selecionados
                updateActionModeTilte();
            }
        };
    }

    private ActionMode.Callback getActionModeCallBack() {
        return new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                //infla o menu específico da action bar de contexto CAB
                MenuInflater inflater = getActivity().getMenuInflater();
                inflater.inflate(R.menu.menu_frag_carros_context, menu);
                /*MenuItem shareItem = menu.findItem(R.id.action_share);
                ShareActionProvider share = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
                shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text*//*");
                share.setShareIntent(shareIntent);*/
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                List<Carro> selectedCarros = getSelectedCarros();
                if (item.getItemId() == R.id.action_remove){
                    CarroDB db = new CarroDB(getContext());
                    try{
                        for (Carro c : selectedCarros){
                            db.delete(c);//Deleta o carro do banco
                            carros.remove(c);//Remove da lista
                        }
                    }finally {
                        db.close();
                    }
                    snack(recyclerView, "Carros excluídos com sucesso");

                }else if (item.getItemId() == R.id.action_share){
                    //Dispara a tarefa para fazer download das fotos
                    startTask("compartilhar", new CompartilharTask(selectedCarros));
                    toast("compartilhar " + selectedCarros);
                }
                //encerra o action mode
                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                //limpa o estado
                actionMode = null;
                //configura todos os carros para não selecionados
                for (Carro c : carros){
                    c.selected = false;
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        };
    }

    //Atualiza o título da action bar CAB
    private void updateActionModeTilte(){
        if (actionMode != null){
            actionMode.setTitle("Selecione os carros.");
            actionMode.setSubtitle(null);
            List<Carro> selectedCarros = getSelectedCarros();
            if (selectedCarros.size() == 1){
                actionMode.setSubtitle("1 carro selecionado.");
            }else if (selectedCarros.size() > 1){
                actionMode.setSubtitle(selectedCarros.size() + " carros selecionados.");
            }
            //updateShareIntent(selectedCarros);
        }
    }

    //atualiza a share intent com os carros selecionados
    /*private void updateShareIntent(List<Carro> selectedCarros) {
        if (shareIntent != null){
            //Texto com os carros
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Carros: " + selectedCarros);
        }
    }*/

    //Retorna a lista de carros selecionados
    private List<Carro> getSelectedCarros() {
        List<Carro> list = new ArrayList<Carro>();
        for (Carro c : carros){
            if (c.selected){
                list.add(c);
            }
        }
        return list;
    }

    //Task para buscar os carros
    private class GetCarrosTask implements TaskListener<List<Carro>>{
        private boolean refresh;
        public GetCarrosTask(boolean refresh){
            this.refresh = refresh;
        }
        @Override
        public List<Carro> execute() throws Exception {
            //Thread.sleep(800);
            //busca os carros em backgroun
            return CarroService.getCarros(getContext(), tipo, refresh);
        }

        @Override
        public void updateView(List<Carro> carros) {
            if (carros != null){
                //salva a lista de carros no atributo da classe
                CarrosFragment.this.carros = carros;
                //Atualiza a view na UI Thread
                recyclerView.setAdapter(new CarroAdapter(getContext(), carros, onClickCarro()));
            }
        }

        @Override
        public void onError(Exception exception) {
            //qualquer exceção lançada no método execute vai cair aqui
            alert("Ocorreu algum erro ao buscar os dados");
        }

        @Override
        public void onCancelled(String s) {

        }
    }

    //Task para fazer o download
    //Faça import da classe android.net.URI
    private class CompartilharTask implements TaskListener {
        // Lista de arquivos para compartilhar
        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        private final List<Carro> selectedCarros;

        public CompartilharTask(List<Carro> selectedCarros) {
            this.selectedCarros = selectedCarros;
        }

        @Override
        public Object execute() throws Exception {
            if (selectedCarros != null){
                for (Carro c :selectedCarros){
                    //Faz o download da foto do carro para arquivo
                    String url = c.urlFoto;
                    String fileName = url.substring(url.lastIndexOf("/"));
                    //Cria o arquivo no SD card
                    File file = SDCardUtils.getPrivateFile(getContext(), "carros", fileName);
                    IOUtils.downloadToFile(c.urlFoto, file);
                    //Salva a Uri para compartilhar a foto
                    imageUris.add(Uri.fromFile(file));
                }
            }
            return null;
        }

        @Override
        public void updateView(Object o) {
            //Cria a intent com a foto dos carros
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            shareIntent.setType("image/*");
            //Cria o intent chooser com as opções
            startActivity(Intent.createChooser(shareIntent, "Enviar carros"));
        }

        @Override
        public void onError(Exception exception) {

        }

        @Override
        public void onCancelled(String cod) {

        }
    }

       /* @Override
        protected List<Carro> doInBackground(Void... params) {
            try{
                //Busca os carros em background (Thread)
                return CarroService.getCarros(getContext(),tipo);
            }catch (IOException e){
                Log.e("livroandroid", e.getMessage(), e);
                return null;
            }
        }

        //Atualiza a interface
        @Override
        protected void onPostExecute(List<Carro> carros) {
            if (carros != null){
                CarrosFragment.this.carros = carros;
                //Atualiza a view na UI Thread
                recyclerView.setAdapter(new CarroAdapter(getContext(), carros, onClickCarro()));
            }
        }
    }*/
}
