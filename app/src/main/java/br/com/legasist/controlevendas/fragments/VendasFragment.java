package br.com.legasist.controlevendas.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import br.com.legasist.controlevendas.ControleVendasApplication;
import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.activity.VendaActivity;
import br.com.legasist.controlevendas.adapter.VendaAdapter;
import br.com.legasist.controlevendas.domain.Cliente;
import br.com.legasist.controlevendas.domain.OperacoesDB;
import br.com.legasist.controlevendas.domain.Venda;
import livroandroid.lib.utils.AndroidUtils;

public class VendasFragment extends BaseFragment {
    private int tipo;
    protected RecyclerView recyclerView;
    private List<Venda> vendas;
    private SwipeRefreshLayout swipeLayout;
    private ActionMode actionMode;
    //private Intent shareIntent;

    //Método para instanciar esse //fragment pelo tipo
    public static VendasFragment newInstance(){
        /*Bundle args = new Bundle();
        args.putInt("tipo", tipo);*/
        VendasFragment f = new VendasFragment();
        //f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if(getArguments() != null){
            //Lê os tipos dos argumentos
            this.tipo = getArguments().getInt("tipo");
        }*/
        //Registra a classe para receber eventos
        ControleVendasApplication.getInstance().getBus().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clientes, container, false);
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

        //FAB
        view.findViewById(R.id.fabvendas).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //abre a tela para cadastro de uma nova venda
                Venda vend = new Venda();
                Intent intent = new Intent(getContext(), VendaActivity.class);
                intent.putExtra("venda", Parcels.wrap(vend));//converte o objeto para Parcelable
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Cancela o recebimento de enventos
        ControleVendasApplication.getInstance().getBus().unregister(this);
    }

    @Subscribe
    public void onBusAtualizarListaClientes(String refresh){
        //Recebeu o evento, atualiza a lista
        taskVendas(false);
    }

    private SwipeRefreshLayout.OnRefreshListener OnRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                //Valida se existe conexão ao fazer o gesto Pull to Refresh
                if (AndroidUtils.isNetworkAvailable(getContext())){
                    //atualiza ao fazer o gesto Pull to Refresh
                    taskVendas(true);
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
        taskVendas(false);
    }

    private void taskVendas(boolean pullToRefresh){
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
        startTask("vendas", new GetVendasTask(pullToRefresh), pullToRefresh? R.id.swipeToRefresh : R.id.progress);
    }



    private VendaAdapter.VendaOnClickListener onClickVenda(){
        return new VendaAdapter.VendaOnClickListener(){
            @Override
            public void onClickVenda(View view, int idx){
                Venda v = vendas.get(idx);
                if (actionMode == null){
                    Intent intent = new Intent(getContext(), VendaActivity.class);
                    intent.putExtra("venda", Parcels.wrap(v));//converte o objeto para Parcelable
                    startActivity(intent);
                }else{//Se a CAB está ativada
                    //seleciona a venda
                    v.selected = !v.selected;
                    //Atualiza o título com a quantidade de vendas selecionados
                    updateActionModeTilte();
                    //redesenha a lista
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onLongClickVenda(View view, int idx) {
                if (actionMode != null) {
                    return;
                }
                //liga a action bar de contexto CAB
                actionMode = getAppCompatActivity().startSupportActionMode(getActionModeCallBack());
                Venda v = vendas.get(idx);
                v.selected = true; //seleciona a venda
                //solicita ao Android para desenhar a lista novamente
                recyclerView.getAdapter().notifyDataSetChanged();
                //atualiza o título para mostrar a quantidade de vendas selecionados
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
                    inflater.inflate(R.menu.menu_frag_vendas_context, menu);
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
                List<Venda> selectedVendas = getSelectedVendas();
                if (item.getItemId() == R.id.action_remove){
                    OperacoesDB db = new OperacoesDB(getContext());
                    try{
                        for (Venda v : selectedVendas){
                            db.delete("venda", v.id);//Deleta a venda do banco
                            vendas.remove(v);//Remove da lista
                        }
                    }finally {
                        db.close();
                    }
                    snack(recyclerView, "Vendas excluídas com sucesso");

                }else if (item.getItemId() == R.id.action_share){
                    //Dispara a tarefa para fazer download das fotos
                    startTask("compartilhar", new CompartilharTask(selectedVendas));
                    toast("compartilhar " + selectedVendas);
                }
                //encerra o action mode
                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                //limpa o estado
                actionMode = null;
                //configura todos as vendas para não selecionadas
                for (Venda v : vendas){
                    v.selected = false;
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        };
    }

    //Atualiza o título da action bar CAB
    private void updateActionModeTilte(){
        if (actionMode != null){
            actionMode.setTitle("Selecione as vendas.");
            actionMode.setSubtitle(null);
            List<Venda> selectedVendas = getSelectedVendas();
            if (selectedVendas.size() == 1){
                actionMode.setSubtitle("1 venda selecionada.");
            }else if (selectedVendas.size() > 1){
                actionMode.setSubtitle(selectedVendas.size() + " vendas selecionadas.");
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

    //Retorna a lista de vendas selecionadas
    private List<Venda> getSelectedVendas() {
        List<Venda> list = new ArrayList<Venda>();
        for (Venda v : vendas){
            if (v.selected){
                list.add(v);
            }
        }
        return list;
    }

    //Task para buscar as vendas
    private class GetVendasTask implements TaskListener<List<Venda>>{
        private boolean refresh;
        public GetVendasTask(boolean refresh){
            this.refresh = refresh;
        }
        @Override
        public List<Venda> execute() throws Exception {
            //Thread.sleep(800);
            //busca as vendas em background
            return VendaService.getVendas(getContext(), tipo, refresh);
        }

        @Override
        public void updateView(List<Venda> vendas) {
            if (vendas != null){
                //salva a lista de vendas no atributo da classe
                VendasFragment.this.vendas = vendas;
                //Atualiza a view na UI Thread
                recyclerView.setAdapter(new VendaAdapter(getContext(), vendas, onClickVenda()));
            }
        }

        @Override
        public void onError(Exception exception) {
            //qualquer exceção lançada no método execute vai cair aqui
            alert("Ocorreu algum erro ao buscar os dados de vendas");
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
        private final List<Venda> selectedVendas;

        public CompartilharTask(List<Venda> selectedVendas) {
            this.selectedVendas = selectedVendas;
        }

        @Override
        public Object execute() throws Exception {
            if (selectedVendas != null){
                for (Venda v : selectedVendas){
                    //controlevendas
                 /*   //Faz o download da foto do carro para arquivo
                    String url = c.urlFoto;
                    String fileName = url.substring(url.lastIndexOf("/"));
                    //Cria o arquivo no SD card
                    File file = SDCardUtils.getPrivateFile(getContext(), "carros", fileName);
                    IOUtils.downloadToFile(c.urlFoto, file);
                    //Salva a Uri para compartilhar a foto
                    imageUris.add(Uri.fromFile(file));*/
                }
            }
            return null;
        }

        @Override
        public void updateView(Object o) {
            //Cria a intent com a foto dos vendas
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            shareIntent.setType("image/*");
            //Cria o intent chooser com as opções
            startActivity(Intent.createChooser(shareIntent, "Enviar vendas"));
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