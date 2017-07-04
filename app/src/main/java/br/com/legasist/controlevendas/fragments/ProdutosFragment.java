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
import android.widget.EditText;
import android.widget.ImageButton;

import com.squareup.otto.Subscribe;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import br.com.legasist.controlevendas.ControleVendasApplication;
import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.activity.ProdutoActivity;
import br.com.legasist.controlevendas.adapter.ProdutoAdapter;
import br.com.legasist.controlevendas.domain.Produto;
import br.com.legasist.controlevendas.domain.OperacoesDB;
import br.com.legasist.controlevendas.domain.ProdutoService;
import livroandroid.lib.utils.AndroidUtils;

public class ProdutosFragment extends BaseFragment {
    private int tipo;
    protected RecyclerView recyclerView;
    private List<Produto> produtos;
    private SwipeRefreshLayout swipeLayout;
    private ActionMode actionMode;

    EditText edtPesqProduto;
    //private Intent shareIntent;

    //Método para instanciar esse //fragment
    public static ProdutosFragment newInstance(){
        /*Bundle args = new Bundle();
        args.putInt("tipo", tipo);*/
        ProdutosFragment f = new ProdutosFragment();
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
        View view = inflater.inflate(R.layout.fragment_produtos, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewProd);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        //Swipe to Refresh
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefresh);
        swipeLayout.setOnRefreshListener(OnRefreshListener());
        swipeLayout.setColorSchemeResources(R.color.refresh_progress_1, R.color.refresh_progress_2, R.color.refresh_progress_3);

        edtPesqProduto = (EditText) view.findViewById(R.id.textPesqProduto);

        //FAB
        /*view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                snack(recyclerView, "Exemplo de FAB button");
            }
        });*/

        //FAB
        /*view.findViewById(R.id.fabprod).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //snack(v, "Exemplo de FAB button - Produtos");
                //abre a tela para cadastro de um novo produto
                Produto p = new Produto();
                Intent intent = new Intent(getContext(), ProdutoActivity.class);
                intent.putExtra("produto", Parcels.wrap(p));//converte o objeto para Parcelable
                startActivity(intent);
            }
        });*/

        ImageButton btnPesqProduto = (ImageButton) view.findViewById(R.id.btnPesqProduto);
        btnPesqProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTask("produtos", new GetProdutosByNomeTask(false, String.valueOf(edtPesqProduto.getText())), false? R.id.swipeToRefresh : R.id.progress);
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
    public void onBusAtualizarListaProdutos(String refresh){
        //Recebeu o evento, atualiza a lista
        taskProdutos(false);
    }

    private SwipeRefreshLayout.OnRefreshListener OnRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                //Valida se existe conexão ao fazer o gesto Pull to Refresh
                if (AndroidUtils.isNetworkAvailable(getContext())){
                    //atualiza ao fazer o gesto Pull to Refresh
                    taskProdutos(true);
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
        taskProdutos(false);
    }

    private void taskProdutos(boolean pullToRefresh){
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
        startTask("produtos", new GetProdutosTask(pullToRefresh), pullToRefresh? R.id.swipeToRefresh : R.id.progress);
    }



    private ProdutoAdapter.ProdutoOnClickListener onClickProduto(){
        return new ProdutoAdapter.ProdutoOnClickListener(){
            @Override
            public void onClickProduto(View view, int idx){
                Produto p = produtos.get(idx);
                if (actionMode == null){
                    Intent intent = new Intent(getContext(), ProdutoActivity.class);
                    intent.putExtra("produto", Parcels.wrap(p));//converte o objeto para Parcelable
                    startActivity(intent);
                }else{//Se a CAB está ativada
                    //seleciona o produto
                    p.selected = !p.selected;
                    //Atualiza o título com a quantidade de produtos selecionados
                    updateActionModeTilte();
                    //redesenha a lista
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onLongClickProduto(View view, int idx) {
                if (actionMode != null) {
                    return;
                }
                //liga a action bar de contexto CAB
                actionMode = getAppCompatActivity().startSupportActionMode(getActionModeCallBack());
                Produto p = produtos.get(idx);
                p.selected = true; //seleciona o produto
                //solicita ao Android para desenhar a lista novamente
                recyclerView.getAdapter().notifyDataSetChanged();
                //atualiza o título para mostrar a quantidade de produtos selecionados
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
                    inflater.inflate(R.menu.menu_frag_produtos_context, menu);
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
                List<Produto> selectedProdutos = getSelectedProdutos();
                if (item.getItemId() == R.id.action_remove){
                    OperacoesDB db = new OperacoesDB(getContext());
                    try{
                        for (Produto p : selectedProdutos){
                            db.delete("produto", p.id);//Deleta o produto do banco
                            produtos.remove(p);//Remove da lista
                        }
                    }finally {
                        db.close();
                    }
                    snack(recyclerView, "Produtos excluídos com sucesso");

                }else if (item.getItemId() == R.id.action_share){
                    //Dispara a tarefa para fazer download das fotos
                    startTask("compartilhar", new CompartilharTask(selectedProdutos));
                    toast("compartilhar " + selectedProdutos);
                }
                //encerra o action mode
                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                //limpa o estado
                actionMode = null;
                //configura todos os produtos para não selecionados
                for (Produto p : produtos){
                    p.selected = false;
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        };
    }

    //Atualiza o título da action bar CAB
    private void updateActionModeTilte(){
        if (actionMode != null){
            actionMode.setTitle("Selecione os produtos.");
            actionMode.setSubtitle(null);
            List<Produto> selectedProdutos = getSelectedProdutos();
            if (selectedProdutos.size() == 1){
                actionMode.setSubtitle("1 produto selecionado.");
            }else if (selectedProdutos.size() > 1){
                actionMode.setSubtitle(selectedProdutos.size() + " produtos selecionados.");
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

    //Retorna a lista de produtos selecionados
    private List<Produto> getSelectedProdutos() {
        List<Produto> list = new ArrayList<Produto>();
        for (Produto p : produtos){
            if (p.selected){
                list.add(p);
            }
        }
        return list;
    }

    //Task para buscar os produtos
    private class GetProdutosTask implements TaskListener<List<Produto>>{
        private boolean refresh;
        public GetProdutosTask(boolean refresh){
            this.refresh = refresh;
        }
        @Override
        public List<Produto> execute() throws Exception {
            //Thread.sleep(800);
            //busca os carros em background
            return ProdutoService.getProdutos(getContext(), tipo, refresh);
        }

        @Override
        public void updateView(List<Produto> produtos) {
            if (produtos != null){
                //salva a lista de produtos no atributo da classe
                ProdutosFragment.this.produtos = produtos;
                //Atualiza a view na UI Thread
                recyclerView.setAdapter(new ProdutoAdapter(getContext(), produtos, onClickProduto()));
            }
        }

        @Override
        public void onError(Exception exception) {
            //qualquer exceção lançada no método execute vai cair aqui
            alert("Ocorreu algum erro ao buscar os dados de produtos");
        }

        @Override
        public void onCancelled(String s) {

        }
    }


    //Task para buscar os produtos por nome
    private class GetProdutosByNomeTask implements TaskListener<List<Produto>>{
        private boolean refresh;
        private String nome;
        public GetProdutosByNomeTask(boolean refresh, String nome){
            this.refresh = refresh;
            this.nome = nome;
        }
        @Override
        public List<Produto> execute() throws Exception {
            //Thread.sleep(800);
            //busca os carros em background
            return ProdutoService.getProdutosByNome(getContext(), tipo, refresh, nome);
        }

        @Override
        public void updateView(List<Produto> produtos) {
            if (produtos != null){
                //salva a lista de produtos no atributo da classe
                ProdutosFragment.this.produtos = produtos;
                //Atualiza a view na UI Thread
                recyclerView.setAdapter(new ProdutoAdapter(getContext(), produtos, onClickProduto()));
            }
        }

        @Override
        public void onError(Exception exception) {
            //qualquer exceção lançada no método execute vai cair aqui
            alert("Ocorreu algum erro ao buscar os dados de produtos");
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
        private final List<Produto> selectedProdutos;

        public CompartilharTask(List<Produto> selectedProdutos) {
            this.selectedProdutos = selectedProdutos;
        }

        @Override
        public Object execute() throws Exception {
            if (selectedProdutos != null){
                for (Produto p : selectedProdutos){
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
            //Cria a intent com a foto dos produtos
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            shareIntent.setType("image/*");
            //Cria o intent chooser com as opções
            startActivity(Intent.createChooser(shareIntent, "Enviar produtos"));
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