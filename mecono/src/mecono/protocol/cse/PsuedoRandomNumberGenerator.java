package mecono.protocol.cse;

import java.util.Random;

/**
 *
 * @author Jakson
 */
public class PsuedoRandomNumberGenerator {

	public PsuedoRandomNumberGenerator(long seed) {
		rand_gen.setSeed(seed);
	}

	private final Random rand_gen = new Random();
}
