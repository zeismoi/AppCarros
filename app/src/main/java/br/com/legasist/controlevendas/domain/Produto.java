package br.com.legasist.controlevendas.domain;

@org.parceler.Parcel
public class Produto {

    private static final long serialVersionUID = 7601006766832473959L;
    public long id;
    public String nome;
    public double estoque_atual;
    public double estoque_min;
    public double preco_custo;
    public double preco_venda;
    public Fornecedor fornecedor;
    public boolean selected; //Flag para indicar que o produto está selecionado

    @Override
    public String toString() {
        return "Produto{" + "nome='" + nome + '\'' + '}';
    }
}
