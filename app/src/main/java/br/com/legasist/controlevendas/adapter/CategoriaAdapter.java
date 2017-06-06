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
import br.com.legasist.controlevendas.domain.Categoria;
import br.com.legasist.controlevendas.domain.Cliente;

/**
 * Created by ovs on 06/06/2017.
 */

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriasViewHolder>{
    protected static final String TAG = "controlevendas";
    private final List<Categoria> categorias;
    private final Context context;
    private CategoriaOnClickListener categoriaOnClickListener;

    public CategoriaAdapter(Context context, List<Categoria> categorias, CategoriaOnClickListener categoriaOnClickListener){
        this.context = context;
        this.categorias = categorias;
        this.categoriaOnClickListener = categoriaOnClickListener;
    }

    @Override
    public int getItemCount(){
        return this.categorias !=null ? this.categorias.size() : 0;
    }

    @Override
    public CategoriasViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.adaper_categoria, viewGroup, false);
        CategoriasViewHolder holder = new CategoriasViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final CategoriaAdapter.CategoriasViewHolder holder, final int position){
        //Atualiza a view
        Categoria c = categorias.get(position);
        holder.tNome.setText(c.categoria);

        //pinta o fundo de azul se a linha estiver selecionada
        int corFundo = context.getResources().getColor(c.selected ? R.color.primary : R.color.white);
        holder.cardView.setCardBackgroundColor(corFundo);

        //a cor do texto é branca ou azul, depende da cor do fundo
        int corFonte = context.getResources().getColor((c.selected ? R.color.white : R.color.primary));
        holder.tNome.setTextColor(corFonte);

        //click normal
        if (categoriaOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //a variável position é final
                    categoriaOnClickListener.onClickCategoria(holder.itemView, position);
                }
            });
        }

        //click longo
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                categoriaOnClickListener.onLongClickCategoria(holder.itemView, position);
                return false;
            }
        });
    }


    public interface CategoriaOnClickListener {
        public void onClickCategoria(View view, int idx);
        public void onLongClickCategoria(View view, int idx);
    }

    //viewHolder com as views
    public static class CategoriasViewHolder extends RecyclerView.ViewHolder{
        public TextView tNome;
        CardView cardView;

        public CategoriasViewHolder (View view){
            super(view);
            //Cria as views para salvar no viewHolder
            tNome = (TextView) view.findViewById(R.id.textCategoria);
            cardView = (CardView) view.findViewById(R.id.card_view);
        }
    }
}
