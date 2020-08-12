package net.boomber.wasm;

/**
 * BoxedPointer
 */
public class BoxedPointer {

	public Integer pointer;
	public Integer length;

	public BoxedPointer(Integer pointer, Integer length) {
		this.pointer = pointer;
		this.length = length;
	}

	public String toString() {
		return "{ ptr: " + pointer + ", len: " + length + "}";
	}

	public static BoxedPointer single(Integer pointer) {
		return new BoxedPointer(pointer, 1);
	}
}