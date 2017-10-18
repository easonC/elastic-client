package easonc.elastic.client.lang;

/**
 * Created by zhuqi on 2016/11/30.
 */
public class TowTuple<V1, V2> {
    public static <V1, V2> TowTuple<V1, V2> tuple(V1 v1, V2 v2) {
        return new TowTuple<>(v1, v2);
    }

    private final V1 v1;
    private final V2 v2;

    public TowTuple(V1 v1, V2 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public V1 v1() {
        return v1;
    }

    public V2 v2() {
        return v2;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TowTuple tuple = (TowTuple) o;

        if (v1 != null ? !v1.equals(tuple.v1) : tuple.v1 != null) return false;
        if (v2 != null ? !v2.equals(tuple.v2) : tuple.v2 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = v1 != null ? v1.hashCode() : 0;
        result = 31 * result + (v2 != null ? v2.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ThreeTuple [v1=" + v1 + ", v2=" + v2 + "]";
    }
}
