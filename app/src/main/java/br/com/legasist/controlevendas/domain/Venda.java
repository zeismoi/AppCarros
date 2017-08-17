package br.com.legasist.controlevendas.domain;

import java.util.Date;

@org.parceler.Parcel
public class Venda {

    private static final long serialVersionUID = 7601006766832473959L;
    public long id;
    public Date data;
    public long cliente; //fk para Cliente
    public double valor;
    public double desconto;
    public double total;  //valor - desconto
    public boolean selected; //Flag para indicar que a venda está selecionada

    @Override
    public String toString() {
        return "Venda{" + "data='" + data + '\'' + '}';
    }
}
