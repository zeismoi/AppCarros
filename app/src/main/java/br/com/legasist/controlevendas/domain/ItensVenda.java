package br.com.legasist.controlevendas.domain;

@org.parceler.Parcel
public class ItensVenda {

    private static final long serialVersionUID = 7601006766832473959L;
    public long id;
    public double quantidade;
    public long venda;
    public long produto;
    public boolean selected; //Flag para indicar que o item venda está selecionado

  /*  @Override
    public String toString() {
        return "Produto{" + "nome='" + nome + '\'' + '}';
    }
*/

}
