package gg.wil.imposter.data;

public class Pair<L,R> {

    private final L left;

    public L getLeft() {
        return left;
    }

    private final R right;

    public R getRight() {
        return right;
    }

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }
}
