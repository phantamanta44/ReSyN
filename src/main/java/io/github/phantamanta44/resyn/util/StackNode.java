package io.github.phantamanta44.resyn.util;

public class StackNode<T> {

    private T value;
    private final StackNode<T> parent;

    public StackNode(T value, StackNode<T> parent) {
        this.value = value;
        this.parent = parent;
    }
    
    public StackNode(T value) {
        this(value, null);
    }
    
    public StackNode() {
        this(null);
    }
    
    public StackNode<T> getParent() {
        return parent;
    }
    
    public boolean hasParent() {
        return this.parent != null;
    }
    
    public T getValue() {
        return value;
    }
    
    public void setValue(T value) {
        this.value = value;
    }
    
    public StackNode<T> extend(T value) {
        return new StackNode<>(value, this);
    }
    
    public StackNode<T> extend() {
        return extend(null);
    }

    @Override
    public String toString() {
        if (parent == null) return String.format("[ %s ]", value);
        return String.format("%s -> [ %s ]", parent, value);
    }

}
