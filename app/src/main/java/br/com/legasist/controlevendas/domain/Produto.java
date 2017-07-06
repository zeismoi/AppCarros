package br.com.legasist.controlevendas.domain;

@org.parceler.Parcel
public class Produto {

    private static final long serialVersionUID = 7601006766832473959L;
    public long id;
    public String nome;
    public double estoqueAtual;
    public double estoqueMin;
    public double precoCusto;
    public double precoVenda;
    public String codigoBarras;
    public long fornecedor;
    public String categoria;
    public long categ;
    public byte[] foto;
    public boolean selected; //Flag para indicar que o produto está selecionado

    @Override
    public String toString() {
        return "Produto{" + "nome='" + nome + '\'' + '}';
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }
}
