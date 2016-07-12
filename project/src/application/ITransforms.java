package application;

import javafx.scene.image.Image;

public interface ITransforms {
	
	/**
	 * Convert image to grey colors
	 * @param p_image Image object instance
	 * @return
	 */
	public Image greyScaleImage(Image p_image);
}
