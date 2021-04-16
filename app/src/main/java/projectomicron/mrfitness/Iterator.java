package projectomicron.mrfitness;

/**
 * This is an interface that provides classes with container objects an iterator that allows a class
 * to access elements in the container without worrying about the container's internal structural or
 * implementation details. Classes that use this interface must implement its methods.
 * Created by Emanuel Guerrero on 11/29/2015.
 */
public interface Iterator<E> {
    boolean hasNext();
    E next();
}
