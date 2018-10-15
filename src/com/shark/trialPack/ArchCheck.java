package com.shark.trialPack;

public class ArchCheck {

	public static void rolling() {
		System.out.println("starting rolling...");
		int a, b, c, d, e;
		for (a = 0; a < 100; a++) {
			b = a;
			c = b;
			d = c;
			e = d;
			System.out.println("a " + a + " b " + b + " c " + c + " d " + d + " e " + e);
		}
		System.out.println("END of rolling.");
	}

}
