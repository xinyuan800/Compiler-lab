public class ArrayType extends Type{
    private Type contained;
    private int num_elements;

    public ArrayType(Type contained,int num_elements){
        this.contained = contained;
        this.num_elements = num_elements;
    }

    public ArrayType() {

    }

    public void setContained(Type contained){
        this.contained= contained;
    }

    public void setNum_elements(int num_elements){
        this.num_elements = num_elements;
    }

    public Type getContained() {
        return contained;
    }

    public int getNum_elements() {
        return num_elements;
    }
}
