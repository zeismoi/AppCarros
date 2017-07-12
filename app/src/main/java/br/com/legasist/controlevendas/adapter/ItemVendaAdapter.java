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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.domain.Cliente;
import br.com.legasist.controlevendas.domain.ItensVenda;
import br.com.legasist.controlevendas.domain.OperacoesDB;
import br.com.legasist.controlevendas.domain.Produto;
import br.com.legasist.controlevendas.domain.Venda;

/**
 * Created by ovs on 12/07/2017.
 */

public class ItemVendaAdapter extends RecyclerView.Adapter<ItemVendaAdapter.ItensVendaViewHolder>{
    protected static final String TAG = "controlevendas";
    private final List<ItensVenda> itensVenda;
    private final Context context;
    private ItensVendaOnClickListener itensVendaOnClickListener;

    public ItemVendaAdapter(Context context, List<ItensVenda> itensVenda, ItensVendaOnClickListener itensVendaOnClickListener){
        this.context = context;
        this.itensVenda = itensVenda;
        this.itensVendaOnClickListener= itensVendaOnClickListener;
    }

    @Override
    public int getItemCount(){
        return this.itensVenda !=null ? this.itensVenda.size() : 0;
    }

    @Override
    public ItensVendaViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.adaper_itens_venda, viewGroup, false);
        ItensVendaViewHolder holder = new ItensVendaViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ItensVendaViewHolder holder, final int position){
        //Atualiza a view
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        NumberFormat value = NumberFormat.getInstance(new Locale("pt", "BR"));
        OperacoesDB db = new OperacoesDB(this.context);
        ItensVenda v = itensVenda.get(position);
        Produto p = db.findProdutoById(v.produto);
        holder.tItemProduto.setText(p.nome);
        holder.tItemPreco.setText(currency.format(p.precoVenda));
        holder.tItemQuantidade.setText(value.format(v.quantidade));

        //pinta o fundo de azul se a linha estiver selecionada
        int corFundo = context.getResources().getColor(v.selected ? R.color.primary : R.color.white);
        holder.cardView.setCardBackgroundColor(corFundo);

        //a cor do texto é branca ou azul, depende da cor do fundo
        int corFonte = context.getResources().getColor((v.selected ? R.color.white : R.color.primary));
        holder.tItemProduto.setTextColor(corFonte);
        holder.tItemPreco.setTextColor(corFonte);
        holder.tItemQuantidade.setTextColor(corFonte);

        //click normal
        if (itensVendaOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //a variável position é final
                    itensVendaOnClickListener.onClickItensVenda(holder.itemView, position);
                }
            });
        }

        //click longo
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                itensVendaOnClickListener.onLongClickItensVenda(holder.itemView, position);
                return false;
            }
        });
    }


    public interface ItensVendaOnClickListener {
        public void onClickItensVenda(View view, int idx);
        public void onLongClickItensVenda(View view, int idx);
    }

    //viewHolder com as views
    public static class ItensVendaViewHolder extends RecyclerView.ViewHolder{
        public TextView tItemProduto;
        public TextView tItemPreco;
        public TextView tItemQuantidade;
        ImageView img;
        ProgressBar progress;
        CardView cardView;

        public ItensVendaViewHolder (View view){
            super(view);
            //Cria as views para salvar no viewHolder
            tItemProduto = (TextView) view.findViewById(R.id.textItemProduto);
            tItemPreco = (TextView) view.findViewById(R.id.textItemPreco);
            tItemQuantidade = (TextView) view.findViewById(R.id.textItemQuantidade);

            cardView = (CardView) view.findViewById(R.id.card_view);
        }
    }
}
