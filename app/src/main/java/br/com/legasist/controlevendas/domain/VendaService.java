package br.com.legasist.controlevendas.domain;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import livroandroid.lib.utils.FileUtils;
import livroandroid.lib.utils.HttpHelper;
import livroandroid.lib.utils.IOUtils;

import static br.com.legasist.controlevendas.R.string.clientes;
import static br.com.legasist.controlevendas.R.string.vendas;

/**
 * Created by ovs on 29/05/2017.
 */

public class VendaService {
    private static final boolean LOG_ON = false;
    private static final String TAG = "VendaService";
    private static final String URL = "http://www.livroandroid.com.br/livro/carros/carros_{tipo}.json";

    public static List<Venda> getVendas(Context context, int tipo, boolean refresh) throws IOException {
        //busca as vendas no banco de dados (somente se refresh = false)
        //List<Cliente> clientes = !refresh ? getVendasFromBanco(context) : null;
        //sempre buscar no banco por enquanto
        List<Venda> vendas = !false ? getVendasFromBanco(context) : null;
        if(vendas != null && vendas.size()>0){
            //retorna os clientes encontrados do banco
            return vendas;
        }
        //controlelevendas
        //se não encontrar, busca no WebService
        //clientes = getVendasFromWebService(context, tipo);
        //return clientes;
        return null;

        /*String tipoString = getTipo(tipo);
        String url = URL.replace("{tipo}", tipoString);
        //Faz a requisição HTTP no servidor e retorna a String com o conteúdo
        HttpHelper http = new HttpHelper();
        String json = http.doGet(url);
        List<Carro> carros = parserJSON(context, json);
        salvarArquivoNaMemoriaInterna(context, url, json);*/

    }

    private static List<Venda> getVendasFromWebService(Context context, int tipo) throws IOException {
        String tipoString = getTipo(tipo);
        String url = URL.replace("{tipo}", tipoString);
        Log.d(TAG, "URL: " + url);
        //Faz a requisição HTTP no servidor e retorna a String com o conteúdo
        HttpHelper http = new HttpHelper();
        String json = http.doGet(url);
        List<Venda> vendas = parserJSON(context, json);
        //Depois de buscar, salva as vendas
        salvarVendas(context, vendas);
        return vendas;
    }

    //salva as vendas no banco de dados
    private static void salvarVendas(Context context, List<Venda> vendas) {
        OperacoesDB db = new OperacoesDB(context);
        try{
            //Deleta as vendas pelo tipo para limpar o banco
           // String tipoString = getTipo(tipo);
            db.deleteTodos("venda");
            //salva todas as vendas
            for (Venda v : vendas){
                //c.tipo = tipoString;
                Log.d(TAG, "Salvando a venda: " + v.data);
                db.saveVenda(v);
            }
        }finally {
            db.close();
        }

    }

    private static List<Venda> getVendasFromBanco(Context context) throws IOException {
        OperacoesDB db = new OperacoesDB(context);
        try {
            //String tipoString = getTipo(tipo);
            List<Venda> vendas = null;
            vendas = db.findAllVendas();
            Log.d(TAG, "Retornando " + vendas.size() + " vendas do banco");
            return vendas;
        }finally {
            db.close();
        }
    }

    private static void salvarArquivoNaMemoriaInterna(Context context, String url, String json) {
        String fileName = url.substring(url.lastIndexOf("/")+1);
        File file = FileUtils.getFile(context, fileName);
        IOUtils.writeString(file,json);
        Log.d(TAG, "Arquivo salvo: " + file);
    }

    //Converte a constante para String, para criar a URL do wen service
    private static String getTipo(int tipo) {
        if (tipo == br.com.legasist.controlevendas.R.string.classicos){
            return "classicos";
        }else if (tipo == br.com.legasist.controlevendas.R.string.esportivos){
            return "esportivos";
        }
        return "luxo";
    }

    //FAZ A LEITURA DO ARQUIVO QUE ESTÁ NA PASTA /res/raw
    private static String readFile(Context context, int tipo) throws IOException {
        if(tipo == br.com.legasist.controlevendas.R.string.classicos){
            return FileUtils.readRawFileString(context, br.com.legasist.controlevendas.R.raw.carros_classicos, "UTF-8");
        }else if(tipo == br.com.legasist.controlevendas.R.string.esportivos){
            return FileUtils.readRawFileString(context, br.com.legasist.controlevendas.R.raw.carros_esportivos, "UTF-8");
        }
        return FileUtils.readRawFileString(context, br.com.legasist.controlevendas.R.raw.carros_luxo, "UTF-8");
    }

    //controlevendas
    //Faz o parser do XML e cria a lista de carros
    /*private static List<Carro> parserXML(Context context, String xml){
        List<Carro> carros = new ArrayList<Carro>();
        Element root = XMLUtils.getRoot(xml, "UTF-8");
        //Lê todas as tags<carro>
        List<Node> nodeCarros = XMLUtils.getChildren(root, "carro") ;
        //insere cada carro na lista
        for (Node node :nodeCarros){
            Carro c = new Carro();
            //Lê as informações de cada carro
            c.nome = XMLUtils.getText(node, "nome");
            c.desc = XMLUtils.getText(node, "desc");
            c.urlFoto = XMLUtils.getText(node, "url_foto");
            c.urlInfo = XMLUtils.getText(node, "url_info");
            c.urlVideo = XMLUtils.getText(node, "url_video");
            c.latitude = XMLUtils.getText(node, "latitude");
            c.longitude = XMLUtils.getText(node, "longitude");

            if(LOG_ON){
                Log.d(TAG, "Carro" + c.nome + " > " + c.urlFoto);
            }
            carros.add(c);
        }
        if (LOG_ON){
            Log.d(TAG, carros.size() + " encontrados");
        }
        return carros;
    }*/

    //Faz o parser do JSON e cria a lista de vendas
    private static List<Venda> parserJSON(Context context, String json) throws IOException {
        List<Venda> vendas = new ArrayList<Venda>();
        try{
            JSONObject root = new JSONObject(json);
            JSONObject obj = root.getJSONObject("vendas");
            JSONArray jsonVendas = obj.getJSONArray("venda");
            //insere cada venda na lista
            for (int i=0; i < jsonVendas.length(); i++){
                JSONObject jsonVenda = jsonVendas.getJSONObject(i);
                Venda v = new Venda();
                //Lê as informações de cada venda
                v.data = (Date) jsonVenda.opt("data");
                v.cliente = jsonVenda.optLong("cliente");
                v.valor = jsonVenda.optDouble("valor");
                v.desconto = jsonVenda.optDouble("desconto");
                v.total = jsonVenda.optDouble("total");
                if(LOG_ON){
                    Log.d(TAG, "Venda" + v.cliente + " " + v.total);
                }
                vendas.add(v);
            }
            if (LOG_ON){
                Log.d(TAG, vendas.size() + " encontrados");
            }

        } catch (JSONException e) {
            throw  new IOException(e.getMessage(), e);
        }
        return vendas;
    }
}
