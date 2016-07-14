package application;

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
}
