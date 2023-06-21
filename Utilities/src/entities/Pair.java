package entities;

import java.io.Serializable;
import java.util.Objects;
/**
 * Created A pair Class For Easier Implementation.
 * @author razi
 *
 * @param <T> first parameter
 * @param <K> second parameter
 */
public class Pair<T,K> implements Serializable {
	private static final long serialVersionUID = 55L;
	private T first;
	private K second;
	/**
     * Constructs a Pair object with the specified first and second values.
     *
     * @param first the first value
     * @param second the second value
     */
	public Pair(T first, K second) {
		this.first = first;
		this.second = second;
	}
	/**
     * Returns the first value of the pair.
     *
     * @return the first value
     */
	public T getFirst() {
		return first;
	}
	/**
     * Sets the first value of the pair.
     *
     * @param first the first value
     */
	public void setFirst(T first) {
		this.first = first;
	}
	/**
     * Returns the second value of the pair.
     *
     * @return the second value
     */
	public K getSecond() {
		return second;
	}
	/**
     * Sets the second value of the pair.
     *
     * @param second the second value
     */
	public void setSecond(K second) {
		this.second = second;
	}

	@Override
	public int hashCode() {
		return Objects.hash(first.hashCode(), second.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		try {
			Pair<T,K> other = (Pair<T,K>) obj;
			return Objects.equals(first, other.first) && Objects.equals(second, other.second);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
