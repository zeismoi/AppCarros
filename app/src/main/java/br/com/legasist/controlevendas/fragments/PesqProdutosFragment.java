package br.com.legasist.controlevendas.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import br.com.legasist.controlevendas.adapter.ProdutoAdapter;
import br.com.legasist.controlevendas.domain.Produto;
import br.com.legasist.controlevendas.domain.ProdutoService;
import livroandroid.lib.utils.AndroidUtils;

public class PesqProdutosFragment extends BaseFragment {
    private int tipo;
    protected RecyclerView recyclerView;
    private List<Produto> produtos;
    private SwipeRefreshLayout swipeLayout;

    EditText edtPesqProduto;
    //private Intent shareIntent;

    //Método para instanciar esse //fragment
    public static PesqProdutosFragment newInstance(){
        /*Bundle args = new Bundle();
        args.putInt("tipo", tipo);*/
        PesqProdutosFragment f = new PesqProdutosFragment();
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
        startTask("produtos", new GetProdutosTask(pullToRefresh), pullToRefresh? R.id.swipeToRefresh : R.id.progress);
    }



    private ProdutoAdapter.ProdutoOnClickListener onClickProduto(){
        return new ProdutoAdapter.ProdutoOnClickListener(){
            @Override
            public void onClickProduto(View view, int idx){
                /*Produto p = produtos.get(idx);
                Bundle args = new Bundle();
                //passa o objeto produto por parâmetro
                args.putParcelable("produto", Parcels.wrap(p));

                Intent returnIntent = new Intent();
                returnIntent.putExtra("produto", Parcels.wrap(p));*/


            }

            @Override
            public void onLongClickProduto(View view, int idx) {

            }
        };
    }


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
                PesqProdutosFragment.this.produtos = produtos;
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
                PesqProdutosFragment.this.produtos = produtos;
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

}