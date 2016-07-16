package application;

import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public interface ITransforms {
	
	/**
	 * Convert image to grey colors
	 * @param p_image Image object instance
	 * @return
	 */
	public WritableImage greyScaleImage(Image p_image);
	
	/**
	 * Save the fixed image to any location.
	 */
	public void SaveImage();
	
	/**
	 * Creates a histogram and displays it on the graph
	 * @param p_image
	 * @return
	 */
	public int[] createHistogram(Image p_image);
	public WritableImage lowPassFilter(Image p_image, int kernel);
	public WritableImage highPassFilter(Image p_image, int kernel);

}
