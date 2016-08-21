package application;

import java.io.File;

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
	public void saveImage(File p_file);
	
	/**
	 * Creates a histogram and displays it on the graph
	 * @param p_image
	 * @return
	 */
	public double[] createHistogram(Image p_image);
	public WritableImage enableMaskFilter(Image p_image, double[][] kernel);
	public WritableImage negativeFilter(Image p_image);
	public WritableImage changeContrast(Image p_image);
	public WritableImage imageAdder(Image p_left, Image p_right);
	public WritableImage imageSubstructor(Image p_left, Image p_right);
	
}
