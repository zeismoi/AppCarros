package br.com.legasist.controlevendas.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.parceler.Parcels;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.legasist.controlevendas.ControleVendasApplication;
import br.com.legasist.controlevendas.R;
import br.com.legasist.controlevendas.domain.Cliente;
import br.com.legasist.controlevendas.domain.OperacoesDB;
import br.com.legasist.controlevendas.domain.Produto;
import br.com.legasist.controlevendas.domain.Venda;
import br.com.legasist.controlevendas.fragments.ClienteFragment;
import br.com.legasist.controlevendas.fragments.VendaFragment;
import br.com.legasist.controlevendas.fragments.dialog.PesqProdutoDialog;

public class VendaActivity extends BaseActivity {

    private Venda venda;
    private Produto prodSelec;

    EditText edtData, edtCliente, edtValor, edtDesconto, edtTotal;
    ImageButton btnSalvar, btnAdicionaProd;
    Button btnEscolherProduto;
    TextView txtProdEscolhido, txtPrecoProdEscolhido;
    NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venda);
        //Configura a toolBar como a actionBar
        setUpToolbar();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");


        //título da tollBar e botão up navigation
        Venda v = Parcels.unwrap(getIntent().getParcelableExtra("venda"));

        if(v.data != null) {
            getSupportActionBar().setTitle(dateFormat.format(v.data));
        }else{
            getSupportActionBar().setTitle("Nova venda");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //venda = Parcels.unwrap(getArguments().getParcelable("venda"));
        //setHasOptionsMenu(true); // precisamos informar ao Android q este fragment tem menu

        //final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        final AutoCompleteTextView clientes = (AutoCompleteTextView) findViewById(R.id.auto_complete_clientes);

        txtProdEscolhido = (TextView) findViewById(R.id.txtProdEscolhido);
        txtPrecoProdEscolhido= (TextView) findViewById(R.id.txtPrecoProdEscolhido);

        edtData = (EditText) findViewById(R.id.textDataVenda);
        edtCliente = (EditText) findViewById(R.id.auto_complete_clientes);
        edtValor = (EditText) findViewById(R.id.textValorVenda);
        edtDesconto = (EditText) findViewById(R.id.textDescontoVenda);
        edtTotal = (EditText) findViewById(R.id.textTotalVenda);

        OperacoesDB db = new OperacoesDB(getContext());
        List<Cliente> listaCli = db.findAllClientes();
        final ArrayAdapter<String> adaptador;
        String[] autoComplArray = new String[listaCli.size()];
        final HashMap<String,String> autoComplMap = new HashMap<String, String>();
        adaptador = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        // ArrayAdapter para preencher com os clientes
        clientes.setAdapter(adaptador);

        for (Cliente cli:listaCli) {
            adaptador.add(cli.nome);
            autoComplMap.put(cli.nome, String.valueOf(cli.id));
        }
        if(listaCli.size() == 0){
            adaptador.add(getResources().getString(R.string.nao_ha_cliente));
            autoComplMap.put(getResources().getString(R.string.nao_ha_cliente), String.valueOf("-1"));
            clientes.setThreshold(adaptador.getPosition(getResources().getString(R.string.nao_ha_cliente)));
        }else {
            adaptador.add(getResources().getString(R.string.digite_o_cliente));
            autoComplMap.put(getResources().getString(R.string.digite_o_cliente), String.valueOf("-1"));
            clientes.setThreshold(adaptador.getPosition(getResources().getString(R.string.digite_o_cliente)));
        }


        if(venda != null && venda.id != 0){
            edtData.setText(dateFormat.format(venda.data));
            edtCliente.setText(Long.toString(venda.cliente));
            edtValor.setText(Double.toString(venda.valor));
            edtDesconto.setText(Double.toString(venda.desconto));
            edtTotal.setText(Double.toString(venda.total));
            if(venda.cliente != 0) {
                Cliente cli = db.findClienteById((venda.cliente));
                clientes.setText(cli.nome);
            }
        }

        btnSalvar = (ImageButton) findViewById(R.id.btnSalvarVenda);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OperacoesDB db = new OperacoesDB(getContext());
                if(venda == null || venda.id == 0) {
                    Venda v = new Venda();
                    try {
                        v.data = dateFormat.parse(String.valueOf(edtData.getText()));
                        /*if((String.valueOf(edtCliente.getText()) != null) && (!(String.valueOf(edtCliente.getText()).equals("")))) {
                            v.cliente = Long.parseLong(String.valueOf(edtCliente.getText()));
                        }*/
                        v.valor = Double.parseDouble(String.valueOf(edtValor.getText()));
                        v.desconto = Double.parseDouble(String.valueOf(edtDesconto.getText()));
                        v.total = Double.parseDouble(String.valueOf(edtTotal.getText()));

                        //SE NÃO EXISTIR NENHUM ITEM SELECIONADO NA COMBO, NÃO GRAVA, POIS O ID ESTAVA VINDO COMO -1
                        if(clientes.getText() != null && autoComplMap.get(String.valueOf(clientes.getText())) != String.valueOf(-1)) {
                            long id = Long.parseLong(autoComplMap.get(String.valueOf(clientes.getText())));
                            v.cliente = id;
                        }

                        db.saveVenda(v);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }finally {
                        db.close();
                    }
                }else{
                    try{
                        venda.data = dateFormat.parse(String.valueOf(edtData.getText()));
                        /*if(edtCliente.getText()!= null && !edtCliente.getText().equals("")) {
                            venda.cliente = Long.parseLong(String.valueOf(edtCliente.getText()));
                        }*/
                        venda.valor = Double.parseDouble(String.valueOf(edtValor.getText()));
                        venda.desconto = Double.parseDouble(String.valueOf(edtDesconto.getText()));
                        venda.total = Double.parseDouble(String.valueOf(edtTotal.getText()));

                        //SE NÃO EXISTIR NENHUM ITEM SELECIONADO NA COMBO, NÃO GRAVA, POIS O ID ESTAVA VINDO COMO -1
                        if(clientes.getText() != null && autoComplMap.get(String.valueOf(clientes.getText())) != String.valueOf(-1)) {
                            long id = Long.parseLong(autoComplMap.get(String.valueOf(clientes.getText())));
                            venda.cliente = id;
                        }

                        db.saveVenda(venda);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } finally {
                        db.close();
                    }

                }
                getActivity().finish();
                //Envia o evento para o Bus
                ControleVendasApplication.getInstance().getBus().post("refresh");
            }
        });

        btnEscolherProduto = (Button) findViewById(R.id.btnEscolherProduto);
        btnEscolherProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(getContext(), PesqProdutosActivity.class);
                startActivityForResult(intent,4);*/
                /*PesqProdutoDialog.show(getFragmentManager(), new PesqProdutoDialog.Callback() {
                    @Override
                    public void onProdutoUpdate(Produto produto) {

                        ControleVendasApplication.getInstance().getBus().post("refresh");
                    }
                });*/

                Intent intent = new Intent(getContext(), PesqProdutosActivity.class);
                //Bundle args = new Bundle();
                //passa o objeto produto por parâmetro
                //args.putParcelable("produto", (Parcelable) new Produto());
                //intent.putExtra("produto", args);
                startActivityForResult(intent, 7);

            }
        });






        //Imagem de header na actionBar
        //controlevendas
        /*ImageView appBarImg = (ImageView) findViewById(br.com.legasist.controlevendas.R.id.appBarImg);
        Picasso.with(getContext()).load(c.urlFoto).into(appBarImg);*/
        /*if(savedInstanceState == null){
            //cria o fragment com o mesmo Bundle (args) da intent
            VendaFragment frag = new VendaFragment();
            frag.setArguments(getIntent().getExtras());
            //adiciona o fragment ao Layoult
            getSupportFragmentManager().beginTransaction().add(R.id.vendaFragment,frag).commit();
        }*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle extras = data.getExtras();
        prodSelec = Parcels.unwrap(extras.getParcelable("produto"));
        txtProdEscolhido.setText(prodSelec.nome);
        txtPrecoProdEscolhido.setText(currency.format(prodSelec.precoVenda));
    }

    public void setTitle(String s){
        //o título deve ser setado na CollapsingToolbarLayou
        CollapsingToolbarLayout c = (CollapsingToolbarLayout) findViewById(br.com.legasist.controlevendas.R.id.collaping_tollbar);
        c.setTitle(s);
    }
}
