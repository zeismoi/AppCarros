package br.com.legasist.controlevendas.domain;

import java.util.Date;

@org.parceler.Parcel
public class ItensVenda {

    private static final long serialVersionUID = 7601006766832473959L;
    public long id;
    public long venda;  //fk para Venda
    public long produto; //fk para Produto
    public double quantidade;
    public boolean selected; //Flag para indicar que o produto está selecionado

    @Override
    public String toString() {
        return "Itens Venda{" + "quant='" + quantidade + '\'' + '}';
    }
}
