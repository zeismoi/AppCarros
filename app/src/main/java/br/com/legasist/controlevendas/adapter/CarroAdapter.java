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

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.legasist.controlevendas.domain.Carro;

/**
 * Created by ovs on 09/05/2017.
 */

public class CarroAdapter extends RecyclerView.Adapter<CarroAdapter.CarrosViewHolder>{
    protected static final String TAG = "livroandroid";
    private final List<Carro> carros;
    private final Context context;
    private CarroOnClickListener carroOnClickListener;

    public CarroAdapter(Context context, List<Carro> carros, CarroOnClickListener carroOnClickListener){
        this.context = context;
        this.carros = carros;
        this.carroOnClickListener = carroOnClickListener;
    }

    @Override
    public int getItemCount(){
        return this.carros !=null ? this.carros.size() : 0;
    }

    @Override
    public CarrosViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(context).inflate(br.com.legasist.controlevendas.R.layout.adaper_carro, viewGroup, false);
        CarrosViewHolder holder = new CarrosViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final CarrosViewHolder holder, final int position){
        //Atualiza a view
        Carro c = carros.get(position);
        holder.tNome.setText(c.nome);
        holder.progress.setVisibility(View.VISIBLE);
        //faz o download da foto e mostra o progressBar
        Picasso.with(context).load(c.urlFoto).fit().into(holder.img, new com.squareup.picasso.Callback(){
            @Override
            public void onSuccess(){
                holder.progress.setVisibility(View.GONE); //download ok
            }

            @Override
            public void onError(){
                holder.progress.setVisibility(View.GONE);
            }
        });

        //pinta o fundo de azul se a linha estiver selecionada
        int corFundo = context.getResources().getColor(c.selected ? br.com.legasist.controlevendas.R.color.primary : br.com.legasist.controlevendas.R.color.white);
        holder.cardView.setCardBackgroundColor(corFundo);

        //a cor do texto é branca ou azul, depende da cor do fundo
        int corFonte = context.getResources().getColor((c.selected ? br.com.legasist.controlevendas.R.color.white : br.com.legasist.controlevendas.R.color.primary));
        holder.tNome.setTextColor(corFonte);

        //click normal
        if (carroOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //a variável position é final
                    carroOnClickListener.onClickCarro(holder.itemView, position);
                }
            });
        }

        //click longo
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                carroOnClickListener.onLongClickCarro(holder.itemView, position);
                return false;
            }
        });
    }


    public interface CarroOnClickListener {
        public void onClickCarro(View view, int idx);
        public void onLongClickCarro(View view, int idx);
    }

    //viewHolder com as views
    public static class CarrosViewHolder extends RecyclerView.ViewHolder{
        public TextView tNome;
        ImageView img;
        ProgressBar progress;
        CardView cardView;

        public CarrosViewHolder (View view){
            super(view);
            //Cria as views para salvar no viewHolder
            tNome = (TextView) view.findViewById(br.com.legasist.controlevendas.R.id.texto);
            img = (ImageView) view.findViewById(br.com.legasist.controlevendas.R.id.img);
            progress = (ProgressBar) view.findViewById(br.com.legasist.controlevendas.R.id.progressImg);
            cardView = (CardView) view.findViewById(br.com.legasist.controlevendas.R.id.card_view);
        }
    }
}
