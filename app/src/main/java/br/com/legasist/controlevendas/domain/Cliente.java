package br.com.legasist.controlevendas.domain;

@org.parceler.Parcel
public class Cliente {

    private static final long serialVersionUID = 7601006766832473959L;
    public long id;
    public String nome;
    public String endereco;
    public String cidade;
    public String uf;
    public String celular;
    public String email;
    public double latitude;
    public double longitude;
    public boolean selected; //Flag para indicar que o cliente está selecionado

    @Override
    public String toString() {
        return "Cliente{" + "nome='" + nome + '\'' + '}';
    }
}
