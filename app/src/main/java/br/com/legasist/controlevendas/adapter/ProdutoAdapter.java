package br.com.legasist.controlevendas.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.domain.Produto;

/**
 * Created by ovs on 01/06/2017.
 */

public class ProdutoAdapter extends RecyclerView.Adapter<ProdutoAdapter.ProdutosViewHolder>{
    protected static final String TAG = "controlevendas";
    private final List<Produto> produtos;
    private final Context context;
    private ProdutoOnClickListener produtoOnClickListener;

    public ProdutoAdapter(Context context, List<Produto> produtos, ProdutoOnClickListener produtoOnClickListener){
        this.context = context;
        this.produtos = produtos;
        this.produtoOnClickListener= produtoOnClickListener;
    }

    @Override
    public int getItemCount(){
        return this.produtos !=null ? this.produtos.size() : 0;
    }

    @Override
    public ProdutosViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.adaper_produto, viewGroup, false);
        ProdutosViewHolder holder = new ProdutosViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ProdutoAdapter.ProdutosViewHolder holder, final int position){
        //Atualiza a view
        Produto p = produtos.get(position);
        holder.tNome.setText(p.nome);
       // holder.tEstAtual.setText(Double.toString(p.estoqueAtual));
        //holder.tPrecoVenda.setText(Double.toString(p.precoVenda));

        NumberFormat value = NumberFormat.getInstance(new Locale("pt", "BR"));
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        holder.tEstAtual.setText(value.format(p.estoqueAtual));
        holder.tPrecoVenda.setText(currency.format(p.precoVenda));

        if(p.foto != null) {
            byte[] foto = p.foto;
            ByteArrayInputStream imageStream = new ByteArrayInputStream(foto);
            Bitmap imageBitmap = BitmapFactory.decodeStream(imageStream);
            holder.img.setImageBitmap(imageBitmap);
        }

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
        int corFundo = context.getResources().getColor(p.selected ? R.color.primary : R.color.white);
        holder.cardView.setCardBackgroundColor(corFundo);

        //a cor do texto é branca ou azul, depende da cor do fundo
        int corFonte = context.getResources().getColor((p.selected ? R.color.white : R.color.primary));
        holder.tNome.setTextColor(corFonte);

        //click normal
        if (produtoOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //a variável position é final
                    produtoOnClickListener.onClickProduto(holder.itemView, position);
                }
            });
        }

        //click longo
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                produtoOnClickListener.onLongClickProduto(holder.itemView, position);
                return false;
            }
        });
    }


    public interface ProdutoOnClickListener {
        public void onClickProduto(View view, int idx);
        public void onLongClickProduto(View view, int idx);
    }

    //viewHolder com as views
    public static class ProdutosViewHolder extends RecyclerView.ViewHolder{
        public TextView tNome;
        public TextView tEstAtual;
        public TextView tPrecoVenda;
        ImageView img;
        ProgressBar progress;
        CardView cardView;

        public ProdutosViewHolder (View view){
            super(view);
            //Cria as views para salvar no viewHolder
            tNome = (TextView) view.findViewById(R.id.textProduto);
            tEstAtual = (TextView) view.findViewById(R.id.textEstoqueAtual);
            tPrecoVenda = (TextView) view.findViewById(R.id.textPrecoVenda);
            img = (ImageView) view.findViewById(R.id.img);
            progress = (ProgressBar) view.findViewById(R.id.progressImg);
            cardView = (CardView) view.findViewById(R.id.card_view);
        }
    }
}
