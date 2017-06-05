package br.com.legasist.controlevendas.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.domain.Cliente;
import br.com.legasist.controlevendas.domain.Fornecedor;

import static br.com.legasist.controlevendas.R.string.clientes;

/**
 * Created by ovs on 05/06/2017.
 */

public class FornecedorAdapter extends RecyclerView.Adapter<FornecedorAdapter.FornecedoresViewHolder>{
    protected static final String TAG = "controlevendas";
    private final List<Fornecedor> fornecedores;
    private final Context context;
    private FornecedorOnClickListener fornecedorOnClickListener;

    public FornecedorAdapter(Context context, List<Fornecedor> fornecedores, FornecedorOnClickListener fornecedorOnClickListener){
        this.context = context;
        this.fornecedores = fornecedores;
        this.fornecedorOnClickListener= fornecedorOnClickListener;
    }

    @Override
    public int getItemCount(){
        return this.fornecedores !=null ? this.fornecedores.size() : 0;
    }

    @Override
    public FornecedoresViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.adaper_fornecedor, viewGroup, false);
        FornecedoresViewHolder holder = new FornecedoresViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final FornecedorAdapter.FornecedoresViewHolder holder, final int position){
        //Atualiza a view
        Fornecedor f = fornecedores.get(position);
        holder.tNome.setText(f.nome);
        holder.tEndereco.setText(f.endereco);

        //holder.progress.setVisibility(View.VISIBLE);
        //controlevendas
        //faz o download da foto e mostra o progressBar
        /*Picasso.with(context).load(c.urlFoto).fit().into(holder.img, new com.squareup.picasso.Callback(){
            @Override
            public void onSuccess(){
                holder.progress.setVisibility(View.GONE); //download ok
            }

            @Override
            public void onError(){
                holder.progress.setVisibility(View.GONE);
            }
        });*/

        //pinta o fundo de azul se a linha estiver selecionada
        int corFundo = context.getResources().getColor(f.selected ? R.color.primary : R.color.white);
        holder.cardView.setCardBackgroundColor(corFundo);

        //a cor do texto é branca ou azul, depende da cor do fundo
        int corFonte = context.getResources().getColor((f.selected ? R.color.white : R.color.primary));
        holder.tNome.setTextColor(corFonte);

        //click normal
        if (fornecedorOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //a variável position é final
                    fornecedorOnClickListener.onClickFornecedor(holder.itemView, position);
                }
            });
        }

        //click longo
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                fornecedorOnClickListener.onLongClickFornecedor(holder.itemView, position);
                return false;
            }
        });
    }


    public interface FornecedorOnClickListener {
        public void onClickFornecedor(View view, int idx);
        public void onLongClickFornecedor(View view, int idx);
    }

    //viewHolder com as views
    public static class FornecedoresViewHolder extends RecyclerView.ViewHolder{
        public TextView tNome;
        public TextView tEndereco;
        ImageView img;
        ProgressBar progress;
        CardView cardView;

        public FornecedoresViewHolder (View view){
            super(view);
            //Cria as views para salvar no viewHolder
            tNome = (TextView) view.findViewById(R.id.textFornecedor);
            tEndereco = (TextView) view.findViewById(R.id.textEnderecoFornec);
            progress = (ProgressBar) view.findViewById(R.id.progressImg);
            cardView = (CardView) view.findViewById(R.id.card_view);
        }
    }
}
