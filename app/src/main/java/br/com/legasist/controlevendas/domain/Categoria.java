package br.com.legasist.controlevendas.domain;

@org.parceler.Parcel
public class Categoria {

    private static final long serialVersionUID = 7601006766832473959L;
    public long id;
    public String categoria;
    public boolean selected; //Flag para indicar que o cliente está selecionado

    @Override
    public String toString() {
        return "Categoria{" + "nome='" + categoria + '\'' + '}';
    }
}
