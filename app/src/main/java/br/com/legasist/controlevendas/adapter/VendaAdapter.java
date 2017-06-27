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

import java.text.SimpleDateFormat;
import java.util.List;

import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.domain.Venda;

/**
 * Created by ovs on 19/06/2017.
 */

public class VendaAdapter extends RecyclerView.Adapter<VendaAdapter.VendasViewHolder>{
    protected static final String TAG = "controlevendas";
    private final List<Venda> vendas;
    private final Context context;
    private VendaOnClickListener vendaOnClickListener;

    public VendaAdapter(Context context, List<Venda> vendas, VendaOnClickListener vendaOnClickListener){
        this.context = context;
        this.vendas = vendas;
        this.vendaOnClickListener= vendaOnClickListener;
    }

    @Override
    public int getItemCount(){
        return this.vendas !=null ? this.vendas.size() : 0;
    }

    @Override
    public VendasViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.adaper_venda, viewGroup, false);
        VendasViewHolder holder = new VendasViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final VendasViewHolder holder, final int position){
        //Atualiza a view
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Venda v = vendas.get(position);
        holder.tDataVenda.setText(dateFormat.format(v.data));
        if(v.cliente != 0) {
            holder.tCliente.setText((int) v.cliente);
        }

        //pinta o fundo de azul se a linha estiver selecionada
        int corFundo = context.getResources().getColor(v.selected ? R.color.primary : R.color.white);
        holder.cardView.setCardBackgroundColor(corFundo);

        //a cor do texto é branca ou azul, depende da cor do fundo
        int corFonte = context.getResources().getColor((v.selected ? R.color.white : R.color.primary));

        if(v.cliente != 0) {
            holder.tCliente.setTextColor(corFonte);
        }

        //click normal
        if (vendaOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //a variável position é final
                    vendaOnClickListener.onClickVenda(holder.itemView, position);
                }
            });
        }

        //click longo
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                vendaOnClickListener.onLongClickVenda(holder.itemView, position);
                return false;
            }
        });
    }


    public interface VendaOnClickListener {
        public void onClickVenda(View view, int idx);
        public void onLongClickVenda(View view, int idx);
    }

    //viewHolder com as views
    public static class VendasViewHolder extends RecyclerView.ViewHolder{
        public TextView tDataVenda;
        public TextView tCliente;
        ImageView img;
        ProgressBar progress;
        CardView cardView;

        public VendasViewHolder (View view){
            super(view);
            //Cria as views para salvar no viewHolder
            tDataVenda = (TextView) view.findViewById(R.id.textDataVenda);
            tCliente = (TextView) view.findViewById(R.id.textClienteVenda);

            cardView = (CardView) view.findViewById(R.id.card_view);
        }
    }
}
