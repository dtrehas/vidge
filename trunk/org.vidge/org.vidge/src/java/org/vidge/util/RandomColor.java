package org.vidge.util;

import java.awt.Color;
import java.util.Random;

import org.eclipse.swt.widgets.Display;

public class RandomColor {

	private Random rand;
	private Color[] colorArray = new Color[] {
			Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.PINK, Color.MAGENTA
	};

	/**
	 * Constructor for objects of class RandomColor initializes the random number generator
	 */
	public RandomColor() {
		rand = new Random();
		for (int a = 0; a < 7; a++) {
			colorArray[a] = randomColor1(colorArray[a].toString());
		}
	}

	/**
	 * randomColor returns a pseudorandom Color
	 * 
	 * @return a pseudorandom Color
	 */
	public Color randomColor() {
		return (new Color(rand.nextInt(56) + 200, rand.nextInt(56) + 200, rand.nextInt(56) + 100));
	}

	public Color randomColor1() {
		final float hue = rand.nextFloat();
		// Saturation between 0.1 and 0.3
		final float saturation = (rand.nextInt(2000) + 1000) / 10000f;
		final float luminance = 0.9f;
		return Color.getHSBColor(hue, saturation, luminance);
		// return (new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
	}

	public Color randomColor1(String name) {
		final float hue = 120;
		// Saturation between 0.1 and 0.3
		final float saturation = (Math.abs(name.hashCode()) + 1000) / 10000f;
		final float luminance = 1.6f;
		return Color.getHSBColor(hue, saturation, luminance);
		// return (new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
	}

	/**
	 * randomGray returns a pseudorandom gray Color
	 * 
	 * @return a pseudorandom Color
	 */
	public Color randomGray() {
		int intensity = rand.nextInt(256);
		return (new Color(intensity, intensity, intensity));
	}

	public Color randomColor(String name) {
		return colorArray[Math.abs(name.hashCode() % 6)];
	}

	public org.eclipse.swt.graphics.Color randomSWTColor(String name) {
		Color randomColor = randomColor(name);
		return new org.eclipse.swt.graphics.Color(Display.getCurrent(), randomColor.getRed(), randomColor.getGreen(), randomColor.getBlue());
	}
}