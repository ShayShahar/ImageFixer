package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class MainWindowController implements ITransforms{

	private final FileChooser m_chooser = new FileChooser();
	private Image m_original = null;
	@SuppressWarnings("unused")
	private WritableImage m_fixed = null;
	@FXML private ImageView m_originalView;
	@FXML private ImageView m_fixedView;
	

	public void onLoadImageClick(MouseEvent event){
		
		InputStream inputStream = null;

		configureFileChooser(m_chooser);
		m_chooser.setTitle("Save Image");
		
		File file = m_chooser.showOpenDialog(null);
			
    	 if (file != null) {

    		 try {
    			 inputStream = new FileInputStream(file.getPath());  // get the file path
    			 m_original = new Image(inputStream);
    			// m_original = new Image(inputStream,320,320,false,false);  // resize the picture
    			 m_originalView.setImage(m_original);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    	 }
			 	
	}
		
	 public void onGreyscaleButtonClick(ActionEvent p_event){
		 m_fixedView.setImage(greyScaleImage(m_original));
	 }
	
	@Override
	public WritableImage greyScaleImage(Image p_image) {
		
		int height = (int)p_image.getHeight();
		int width = (int)p_image.getWidth();
		
		PixelReader reader = p_image.getPixelReader();
		WritableImage image = new WritableImage(width,height);
		PixelWriter writer = image.getPixelWriter();

		
        for(int i = 0; i < width; i++){
            
            for(int j = 0; j < height; j++){

            	Color color = reader.getColor(i, j);
	            color = color.grayscale();
	            writer.setColor(i, j, color);
            }
         }
		m_fixed = image;
        return image;
	}

	@Override
	public void SaveImage() {
		// TODO Auto-generated method stub
		
	}
	
	
	 private  void configureFileChooser( final FileChooser p_chooser){   
		  
		 p_chooser.setTitle("View Pictures");
		 p_chooser.setInitialDirectory(
             new File(System.getProperty("user.home"))
         );                 
		 p_chooser.getExtensionFilters().addAll(
             new FileChooser.ExtensionFilter("All Images", "*.*"),
             new FileChooser.ExtensionFilter("JPG", "*.jpg"),
             new FileChooser.ExtensionFilter("PNG", "*.png")
         );
      }

	@Override
	public int[] createHistogram(Image p_image) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

}
