package br.com.legasist.controlevendas.fragments;

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

import java.util.ArrayList;
import java.util.List;

import br.com.legasist.controlevendas.ControleVendasApplication;
import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.adapter.CategoriaAdapter;
import br.com.legasist.controlevendas.domain.Categoria;
import br.com.legasist.controlevendas.domain.CategoriaDB;
import br.com.legasist.controlevendas.domain.CategoriaService;
import br.com.legasist.controlevendas.fragments.dialog.CategoriaDialog;
import livroandroid.lib.utils.AndroidUtils;

public class CategoriasFragment extends BaseFragment {
    private int tipo;
    protected RecyclerView recyclerView;
    private List<Categoria> categorias;
    private SwipeRefreshLayout swipeLayout;
    private ActionMode actionMode;
    //private Intent shareIntent;

    //Método para instanciar esse //fragment pelo tipo
    public static CategoriasFragment newInstance(){
        /*Bundle args = new Bundle();
        args.putInt("tipo", tipo);*/
        CategoriasFragment f = new CategoriasFragment();
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
        View view = inflater.inflate(R.layout.fragment_categorias, container, false);
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
        view.findViewById(R.id.fabcateg).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //snack(v, "Exemplo de FAB button - Clientes");
                //abre a tela para cadastro de uma nova categoria

                CategoriaDialog.show(getFragmentManager(), null, new CategoriaDialog.Callback() {
                    @Override
                    public void onCategoriaUpdate(Categoria categoria) {
                        toast("Categoria [" + categoria.categoria + "] atualizada");
                        //Salva a catgoria depois de fechar o Dialog
                        CategoriaDB db = new CategoriaDB(getContext());
                        db.save(categoria);
                        //controlevendas
                        //Atualiza o título com o novo nome
                        //CategoriaActivity a = (CarroActivity) getActivity();
                        //a.setTitle(carro.nome);
                        //Envia o evento para o Bus
                        ControleVendasApplication.getInstance().getBus().post("refresh");
                    }
                });
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
    public void onBusAtualizarListaCategorias(String refresh){
        //Recebeu o evento, atualiza a lista
        taskCategorias(false);
    }

    private SwipeRefreshLayout.OnRefreshListener OnRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                //Valida se existe conexão ao fazer o gesto Pull to Refresh
                if (AndroidUtils.isNetworkAvailable(getContext())){
                    //atualiza ao fazer o gesto Pull to Refresh
                    taskCategorias(true);
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
        taskCategorias(false);
    }

    private void taskCategorias(boolean pullToRefresh){
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
        startTask("categorias", new GetCategoriasTask(pullToRefresh), pullToRefresh? R.id.swipeToRefresh : R.id.progress);
    }



    private CategoriaAdapter.CategoriaOnClickListener onClickCategoria(){
        return new CategoriaAdapter.CategoriaOnClickListener(){
            @Override
            public void onClickCategoria(View view, int idx){
                Categoria c = categorias.get(idx);
                if (actionMode == null){
                    CategoriaDialog.show(getFragmentManager(), c, new CategoriaDialog.Callback() {
                        @Override
                        public void onCategoriaUpdate(Categoria categoria) {
                            toast("Categoria [" + categoria.categoria + "] atualizada");
                            //Salva a catgoria depois de fechar o Dialog
                            CategoriaDB db = new CategoriaDB(getContext());
                            db.save(categoria);
                            //controlevendas
                            //Atualiza o título com o novo nome
                            //CategoriaActivity a = (CarroActivity) getActivity();
                            //a.setTitle(carro.nome);
                            //Envia o evento para o Bus
                            ControleVendasApplication.getInstance().getBus().post("refresh");
                        }
                    });
                }else{//Se a CAB está ativada
                    //seleciona a categoria
                    c.selected = !c.selected;
                    //Atualiza o título com a quantidade de categorias selecionados
                    updateActionModeTilte();
                    //redesenha a lista
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onLongClickCategoria(View view, int idx) {
                if (actionMode != null) {
                    return;
                }
                //liga a action bar de contexto CAB
                actionMode = getAppCompatActivity().startSupportActionMode(getActionModeCallBack());
                Categoria c = categorias.get(idx);
                c.selected = true; //seleciona a categoria
                //solicita ao Android para desenhar a lista novamente
                recyclerView.getAdapter().notifyDataSetChanged();
                //atualiza o título para mostrar a quantidade de clientes selecionados
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
                    inflater.inflate(R.menu.menu_frag_categorias_context, menu);
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
                List<Categoria> selectedCategorias = getSelectedCategorias();
                if (item.getItemId() == R.id.action_remove){
                    CategoriaDB db = new CategoriaDB(getContext());
                    try{
                        for (Categoria c : selectedCategorias){
                            db.delete(c);//Deleta a categoria do banco
                            categorias.remove(c);//Remove da lista
                        }
                    }finally {
                        db.close();
                    }
                    snack(recyclerView, "Categorias excluídas com sucesso");

                }
                //encerra o action mode
                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                //limpa o estado
                actionMode = null;
                //configura todas as categorias para não selecionadas
                for (Categoria c : categorias){
                    c.selected = false;
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        };
    }

    //Atualiza o título da action bar CAB
    private void updateActionModeTilte(){
        if (actionMode != null){
            actionMode.setTitle("Selecione as categorias.");
            actionMode.setSubtitle(null);
            List<Categoria> selectedCategorias = getSelectedCategorias();
            if (selectedCategorias.size() == 1){
                actionMode.setSubtitle("1 categorias selecionada.");
            }else if (selectedCategorias.size() > 1){
                actionMode.setSubtitle(selectedCategorias.size() + " categorias selecionadas.");
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

    //Retorna a lista de categorias selecionadas
    private List<Categoria> getSelectedCategorias() {
        List<Categoria> list = new ArrayList<Categoria>();
        for (Categoria c : categorias){
            if (c.selected){
                list.add(c);
            }
        }
        return list;
    }

    //Task para buscar as categorias
    private class GetCategoriasTask implements TaskListener<List<Categoria>>{
        private boolean refresh;
        public GetCategoriasTask(boolean refresh){
            this.refresh = refresh;
        }
        @Override
        public List<Categoria> execute() throws Exception {
            //Thread.sleep(800);
            //busca as categorias em background
            return CategoriaService.getCategorias(getContext(), tipo, refresh);
        }

        @Override
        public void updateView(List<Categoria> categorias) {
            if (categorias != null){
                //salva a lista de categorias no atributo da classe
                CategoriasFragment.this.categorias = categorias;
                //Atualiza a view na UI Thread
                recyclerView.setAdapter(new CategoriaAdapter(getContext(), categorias, onClickCategoria()));
            }
        }

        @Override
        public void onError(Exception exception) {
            //qualquer exceção lançada no método execute vai cair aqui
            alert("Ocorreu algum erro ao buscar os dados de categorias");
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