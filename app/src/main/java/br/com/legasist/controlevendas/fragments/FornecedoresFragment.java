package br.com.legasist.controlevendas.fragments;

import android.content.Intent;
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
import br.com.legasist.controlevendas.activity.FornecedorActivity;
import br.com.legasist.controlevendas.adapter.FornecedorAdapter;
import br.com.legasist.controlevendas.domain.Fornecedor;
import br.com.legasist.controlevendas.domain.FornecedorService;
import br.com.legasist.controlevendas.domain.OperacoesDB;
import livroandroid.lib.utils.AndroidUtils;

public class FornecedoresFragment extends BaseFragment {
    private int tipo;
    protected RecyclerView recyclerView;
    private List<Fornecedor> fornecedores;
    private SwipeRefreshLayout swipeLayout;
    private ActionMode actionMode;
    //private Intent shareIntent;

    //Método para instanciar esse //fragment pelo tipo
    public static FornecedoresFragment newInstance(){
        /*Bundle args = new Bundle();
        args.putInt("tipo", tipo);*/
        FornecedoresFragment f = new FornecedoresFragment();
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
        View view = inflater.inflate(R.layout.fragment_fornecedores, container, false);
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
        view.findViewById(R.id.fabfornec).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //snack(v, "Exemplo de FAB button - Fornecedoes");
                //abre a tela para cadastro de um novo fornecedor
                Fornecedor f = new Fornecedor();
                Intent intent = new Intent(getContext(), FornecedorActivity.class);
                intent.putExtra("fornecedor", Parcels.wrap(f));//converte o objeto para Parcelable
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
    public void onBusAtualizarListaFornecedores(String refresh){
        //Recebeu o evento, atualiza a lista
        taskFornecedores(false);
    }

    private SwipeRefreshLayout.OnRefreshListener OnRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                //Valida se existe conexão ao fazer o gesto Pull to Refresh
                if (AndroidUtils.isNetworkAvailable(getContext())){
                    //atualiza ao fazer o gesto Pull to Refresh
                    taskFornecedores(true);
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
        taskFornecedores(false);
    }

    private void taskFornecedores(boolean pullToRefresh){
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
        startTask("fornecedores", new GetFornecedoesTask(pullToRefresh), pullToRefresh? R.id.swipeToRefresh : R.id.progress);
    }



    private FornecedorAdapter.FornecedorOnClickListener onClickFornecedor(){
        return new FornecedorAdapter.FornecedorOnClickListener(){
            @Override
            public void onClickFornecedor(View view, int idx){
                Fornecedor f = fornecedores.get(idx);
                if (actionMode == null){
                    Intent intent = new Intent(getContext(), FornecedorActivity.class);
                    intent.putExtra("fornecedor", Parcels.wrap(f));//converte o objeto para Parcelable
                    startActivity(intent);
                }else{//Se a CAB está ativada
                    //seleciona o fornecedor
                    f.selected = !f.selected;
                    //Atualiza o título com a quantidade de fornecedoes selecionados
                    updateActionModeTilte();
                    //redesenha a lista
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onLongClickFornecedor(View view, int idx) {
                if (actionMode != null) {
                    return;
                }
                //liga a action bar de contexto CAB
                actionMode = getAppCompatActivity().startSupportActionMode(getActionModeCallBack());
                Fornecedor f = fornecedores.get(idx);
                f.selected = true; //seleciona o fornecedor
                //solicita ao Android para desenhar a lista novamente
                recyclerView.getAdapter().notifyDataSetChanged();
                //atualiza o título para mostrar a quantidade de fornecedores selecionados
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
                    inflater.inflate(R.menu.menu_frag_fornecedores_context, menu);
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
                List<Fornecedor> selectedFornecedores = getSelectedFornecedores();
                if (item.getItemId() == R.id.action_remove){
                    OperacoesDB db = new OperacoesDB(getContext());
                    try{
                        for (Fornecedor f : selectedFornecedores){
                            db.delete("fornecedor", f.id_fornecedor);//Deleta o fornecedor do banco
                            fornecedores.remove(f);//Remove da lista
                        }
                    }finally {
                        db.close();
                    }
                    snack(recyclerView, "Fornecedoes excluídos com sucesso");

                }
                //controlevendas
                /*else if (item.getItemId() == R.id.action_share){
                    //Dispara a tarefa para fazer download das fotos
                    startTask("compartilhar", new CompartilharTask(selectedClientes));
                    toast("compartilhar " + selectedClientes);
                }*/
                //encerra o action mode
                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                //limpa o estado
                actionMode = null;
                //configura todos os fornecedores para não selecionados
                for (Fornecedor f : fornecedores){
                    f.selected = false;
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        };
    }

    //Atualiza o título da action bar CAB
    private void updateActionModeTilte(){
        if (actionMode != null){
            actionMode.setTitle("Selecione os fornecedores.");
            actionMode.setSubtitle(null);
            List<Fornecedor> selectedFornecedores = getSelectedFornecedores();
            if (selectedFornecedores.size() == 1){
                actionMode.setSubtitle("1 fornecedor selecionado.");
            }else if (selectedFornecedores.size() > 1){
                actionMode.setSubtitle(selectedFornecedores.size() + " fornecedores selecionados.");
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

    //Retorna a lista de fornecedores selecionados
    private List<Fornecedor> getSelectedFornecedores() {
        List<Fornecedor> list = new ArrayList<Fornecedor>();
        for (Fornecedor f : fornecedores){
            if (f.selected){
                list.add(f);
            }
        }
        return list;
    }

    //Task para buscar os fornecedores
    private class GetFornecedoesTask implements TaskListener<List<Fornecedor>>{
        private boolean refresh;
        public GetFornecedoesTask(boolean refresh){
            this.refresh = refresh;
        }
        @Override
        public List<Fornecedor> execute() throws Exception {
            //Thread.sleep(800);
            //busca os clientes em background
            return FornecedorService.getFornecedores(getContext(), tipo, refresh);
        }

        @Override
        public void updateView(List<Fornecedor> fornecedores) {
            if (fornecedores != null){
                //salva a lista de fornecedores no atributo da classe
                FornecedoresFragment.this.fornecedores = fornecedores;
                //Atualiza a view na UI Thread
                recyclerView.setAdapter(new FornecedorAdapter(getContext(), fornecedores, onClickFornecedor()));
            }
        }

        @Override
        public void onError(Exception exception) {
            //qualquer exceção lançada no método execute vai cair aqui
            alert("Ocorreu algum erro ao buscar os dados de fornecedores");
        }

        @Override
        public void onCancelled(String s) {

        }
    }

    //controlevendas
    //Task para fazer o download
    //Faça import da classe android.net.URI
    /*private class CompartilharTask implements TaskListener {
        // Lista de arquivos para compartilhar
        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        private final List<Cliente> selectedClientes;

        public CompartilharTask(List<Cliente> selectedClientes) {
            this.selectedClientes = selectedClientes;
        }

        @Override
        public Object execute() throws Exception {
            if (selectedClientes != null){
                for (Cliente c :selectedClientes){
                    //controlevendas
                 *//*   //Faz o download da foto do carro para arquivo
                    String url = c.urlFoto;
                    String fileName = url.substring(url.lastIndexOf("/"));
                    //Cria o arquivo no SD card
                    File file = SDCardUtils.getPrivateFile(getContext(), "carros", fileName);
                    IOUtils.downloadToFile(c.urlFoto, file);
                    //Salva a Uri para compartilhar a foto
                    imageUris.add(Uri.fromFile(file));*//*
                }
            }
            return null;
        }

        @Override
        public void updateView(Object o) {
            //Cria a intent com a foto dos clientes
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            shareIntent.setType("image*//*");
            //Cria o intent chooser com as opções
            startActivity(Intent.createChooser(shareIntent, "Enviar clientes"));
        }

        @Override
        public void onError(Exception exception) {

        }

        @Override
        public void onCancelled(String cod) {

        }
    }*/

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