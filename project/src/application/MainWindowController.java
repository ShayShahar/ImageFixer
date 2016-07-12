package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

public class MainWindowController implements ITransforms{

	private final FileChooser fileChooser = new FileChooser();
	@FXML private ImageView originalImage;

	
	
	public void onLoadImageClick(MouseEvent event){
		
		InputStream inputStream = null;
		Image picture = null;
		configureFileChooser(fileChooser);
		fileChooser.setTitle("Save Image");
		
		File file = fileChooser.showOpenDialog(null);
			
    	 if (file != null) {

    		 try {
    			 inputStream = new FileInputStream(file.getPath());  // get the file path
    			 picture = new Image(inputStream,320,320,false,false);  // resize the picture
    			 originalImage.setImage(picture);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    	 }
			 	
	}
	
	 private  void configureFileChooser( final FileChooser fileChooser){   
		  
         fileChooser.setTitle("View Pictures");
         fileChooser.setInitialDirectory(
             new File(System.getProperty("user.home"))
         );                 
         fileChooser.getExtensionFilters().addAll(
             new FileChooser.ExtensionFilter("All Images", "*.*"),
             new FileChooser.ExtensionFilter("JPG", "*.jpg"),
             new FileChooser.ExtensionFilter("PNG", "*.png")
         );
      }
	
	
	
	@Override
	public Image greyScaleImage(Image p_image) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
