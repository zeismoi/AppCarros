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

import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.domain.Carro;
import br.com.legasist.controlevendas.domain.Cliente;

/**
 * Created by ovs on 09/05/2017.
 */

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClientesViewHolder>{
    protected static final String TAG = "controlevendas";
    private final List<Cliente> clientes;
    private final Context context;
    private ClienteOnClickListener clienteOnClickListener;

    public ClienteAdapter(Context context, List<Cliente> clientes, ClienteOnClickListener clienteOnClickListener){
        this.context = context;
        this.clientes = clientes;
        this.clienteOnClickListener= clienteOnClickListener;
    }

    @Override
    public int getItemCount(){
        return this.clientes !=null ? this.clientes.size() : 0;
    }

    @Override
    public ClientesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.adaper_cliente, viewGroup, false);
        ClientesViewHolder holder = new ClientesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ClienteAdapter.ClientesViewHolder holder, final int position){
        //Atualiza a view
        Cliente c = clientes.get(position);
        holder.tNome.setText(c.nome);

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
        int corFundo = context.getResources().getColor(c.selected ? R.color.primary : R.color.white);
        holder.cardView.setCardBackgroundColor(corFundo);

        //a cor do texto é branca ou azul, depende da cor do fundo
        int corFonte = context.getResources().getColor((c.selected ? R.color.white : R.color.primary));
        holder.tNome.setTextColor(corFonte);

        //click normal
        if (clienteOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //a variável position é final
                    clienteOnClickListener.onClickCliente(holder.itemView, position);
                }
            });
        }

        //click longo
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                clienteOnClickListener.onLongClickCliente(holder.itemView, position);
                return false;
            }
        });
    }


    public interface ClienteOnClickListener {
        public void onClickCliente(View view, int idx);
        public void onLongClickCliente(View view, int idx);
    }

    //viewHolder com as views
    public static class ClientesViewHolder extends RecyclerView.ViewHolder{
        public TextView tNome;
        ImageView img;
        ProgressBar progress;
        CardView cardView;

        public ClientesViewHolder (View view){
            super(view);
            //Cria as views para salvar no viewHolder
            tNome = (TextView) view.findViewById(br.com.legasist.controlevendas.R.id.texto);
            img = (ImageView) view.findViewById(br.com.legasist.controlevendas.R.id.img);
            progress = (ProgressBar) view.findViewById(br.com.legasist.controlevendas.R.id.progressImg);
            cardView = (CardView) view.findViewById(br.com.legasist.controlevendas.R.id.card_view);
        }
    }
}
