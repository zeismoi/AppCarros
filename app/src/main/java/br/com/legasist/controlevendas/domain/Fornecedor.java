package br.com.legasist.controlevendas.domain;

/**
 * Created by ovs on 25/05/2017.
 */

@org.parceler.Parcel
public class Fornecedor {
    private static final long serialVersionUID = 6501006766832473959L;
    public long id_fornecedor;
    public String nome;
    public String endereco;
    public String cidade;
    public String uf;
    public String telefone;
    public String email;
    public double latitude;
    public double longitude;
    public boolean selected; //Flag para indicar que o fornecedor está selecionado

    @Override
    public String toString() {
        return "Fornecedor{" + "nome='" + nome + '\'' + '}';
    }

}
