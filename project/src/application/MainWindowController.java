package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import org.controlsfx.control.RangeSlider;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Callback;

public class MainWindowController implements ITransforms, Initializable{

	private final FileChooser m_chooser = new FileChooser();
	private Image m_original = null;
	private WritableImage m_fixed = null;
	private RangeSlider m_slider;
	private File file = new File("image.png");
	@FXML private ImageView m_originalView;
	@FXML private ImageView m_fixedView;
	@FXML private ImageView m_rightImage;
	@FXML private ImageView m_leftImage;
	@FXML private Pane m_emptyPane;
	@FXML private Label m_lowValue;
	@FXML private BarChart<String, Integer> m_histogramChart;
	@FXML private ListView<Image> m_listView = new ListView<Image>();
	@FXML private TextField m_mat00, m_mat01, m_mat02, m_mat10, m_mat11, m_mat12, m_mat20, m_mat21, m_mat22;
	@FXML private TextField m_operationField;
	private ArrayList<Image> m_dataObservable = new ArrayList<Image>();
	private int m_lastMin;
	private int m_lastMax;
 
	
	public ObservableList<Image> getImages(ArrayList<Image> p_list){
		
		ObservableList<Image> data = FXCollections.observableArrayList();
		
		for (int i=0 ; i < p_list.size(); i++){
			data.add(p_list.get(i));
		}
		
		return data;
	}
	
	public void updateListView(ArrayList<Image> p_list){
		
		try{
			m_listView.setItems(getImages(p_list));
		}catch(Exception e){
			
		}
	}
	
	public void onLoadImageClick(MouseEvent event){
		
		InputStream inputStream = null;

		configureFileChooser(m_chooser);
		m_chooser.setTitle("Save Image");
		
		File file = m_chooser.showOpenDialog(null);
			
    	 if (file != null) {

    		 try {
    			 inputStream = new FileInputStream(file.getPath());  // get the file path
    			 m_original = new Image(inputStream);
    			 m_originalView.setImage(m_original);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    	 }
			 	
	}
		
	 public void onGreyscaleButtonClick(ActionEvent p_event){
		 m_fixedView.setImage(greyScaleImage(m_original));
	 }
	 
	 public void onNegativeButtonClick(ActionEvent p_event){
		 m_fixedView.setImage(negativeFilter(m_fixed)); 
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
		m_dataObservable.add(m_fixed);
		updateListView(m_dataObservable);
		updateBarChartHistogram(createHistogram(m_fixed));
        return image;
	}
	
	public void onSaveImageButtonClick(ActionEvent p_event){
		saveImage();
	}
	@Override
	public void saveImage() {
		
        try {
			ImageIO.write(SwingFXUtils.fromFXImage(m_fixed, null), "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
			int[] histogram = new int[256];
			int height = (int)p_image.getHeight();
			int width = (int)p_image.getWidth();
			PixelReader reader = p_image.getPixelReader();
			
	        for(int i = 0; i < width; i++){
	            
	            for(int j = 0; j < height; j++){
	            	Color color = reader.getColor(i, j);
	            	int a = (int)(color.getRed()*255);
	            	histogram[a]++;
	            }
	         }

			return histogram;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void updateBarChartHistogram(int[] p_histogram){
		
		Series<String, Integer> s1 = new XYChart.Series<>();

		for (int i = 0; i<256; i++){
			Integer value = p_histogram[i];
			String index = Integer.toString(i);
			s1.getData().add(new XYChart.Data(index, value));
		}
											

		m_histogramChart.getData().clear();
		m_histogramChart.getData().add(s1);	
		
		updateSlider(p_histogram);
	}
	
	private void updateSlider(int[] p_histogram){
		
		//update lower value
		for (int i = 0 ; i < p_histogram.length; i++){
			if (p_histogram[i] != 0){
				m_slider.setLowValue(i);
				m_lastMin = i;
				break;
			}
		}
		
		//update higher value
		for (int i = p_histogram.length - 1 ; i >= 0 ; i--){
			if (p_histogram[i] != 0){
				m_slider.setHighValue(i);
				m_lastMax = i;
				break;
			}
		}
		
	}
	
	public void onEnableMaskButtonClick(ActionEvent event){
		double[][] kernelMatrix = new double[3][3];
		try{
			kernelMatrix[0][0] = Double.parseDouble(m_mat00.getText());	
			kernelMatrix[0][1] = Double.parseDouble(m_mat01.getText());
			kernelMatrix[0][2] = Double.parseDouble(m_mat02.getText());
			kernelMatrix[1][0] = Double.parseDouble(m_mat10.getText());
			kernelMatrix[1][1] = Double.parseDouble(m_mat11.getText());
			kernelMatrix[1][2] = Double.parseDouble(m_mat12.getText());
			kernelMatrix[2][0] = Double.parseDouble(m_mat20.getText());
			kernelMatrix[2][1] = Double.parseDouble(m_mat21.getText());
			kernelMatrix[2][2] = Double.parseDouble(m_mat22.getText());
			
		}catch(Exception e){
			System.out.println("Missing fields");
			return;
		}
		 m_fixedView.setImage(enableMaskFilter(m_fixed, kernelMatrix));
		 
	}
	
	public void onChangeContrastButtonClick(ActionEvent event){
		 m_fixedView.setImage(changeContrast(m_fixed)); 
	}

	@Override
	public WritableImage enableMaskFilter(Image p_image, double[][] kernel) {
				
		int height = (int)p_image.getHeight();
		int width = (int)p_image.getWidth();
		
		PixelReader reader = p_image.getPixelReader();
		WritableImage image = new WritableImage(width,height);
		PixelWriter writer = image.getPixelWriter();
		
		for (int i = 0; i< width; i++){
			for (int j = 0; j< height; j++){
				if (i == 0 || j == 0 || i == width - 1 || j == height - 1){
					writer.setColor(i, j, Color.BLACK);
				}
				else{
					double sum = 0;
					for (int k = 0, z= i-1; k < 3; k++, z++){
						for  (int t = 0, w= j-1; t < 3; t++, w++){
							Color color = reader.getColor(z, w);
							sum += (double)(color.getRed()*kernel[k][t]);	
						}
					}
					if (sum < 0)
						writer.setColor(i, j, Color.BLACK);
					else if (sum > 1)
						writer.setColor(i, j, Color.WHITE);	
					else{
						writer.setColor(i, j, Color.color(sum, sum, sum));
					}
				}
			}
		}
		m_fixed = image;
		m_dataObservable.add(m_fixed);
		updateListView(m_dataObservable);
		updateBarChartHistogram(createHistogram(m_fixed));
		return image;
	}


	public void initialize(URL location, ResourceBundle resources) {
		m_slider = new RangeSlider(0,255,0,255);
		m_slider.setShowTickMarks(true);
		m_slider.setShowTickLabels(true);
		m_slider.setBlockIncrement(10);
		m_slider.setMinWidth(555);
		
		m_emptyPane.getChildren().add(m_slider);
		
		m_mat00.setStyle("-fx-alignment: CENTER;");
		m_mat01.setStyle("-fx-alignment: CENTER;");
		m_mat02.setStyle("-fx-alignment: CENTER;");
		m_mat10.setStyle("-fx-alignment: CENTER;");
		m_mat11.setStyle("-fx-alignment: CENTER;");
		m_mat12.setStyle("-fx-alignment: CENTER;");
		m_mat20.setStyle("-fx-alignment: CENTER;");
		m_mat21.setStyle("-fx-alignment: CENTER;");
		m_mat22.setStyle("-fx-alignment: CENTER;");
		m_operationField.setStyle("-fx-alignment: CENTER;");
		
		m_listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		m_listView.setCellFactory(new Callback<ListView<Image>, 
	              ListCell<Image>>() {
	                  @Override 
	                  public ListCell<Image> call(ListView<Image> list) {
	                	  
	                      return new ImageSetter();
	                  }
	              }
	     );
		
		m_listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Image>(){

			@Override
			public void changed(ObservableValue<? extends Image> observable, Image oldValue, Image newValue) {
				
				try{
					
					Platform.runLater(new Runnable() {
					    @Override public void run() {
					    	try{
								m_fixed = new WritableImage(newValue.getPixelReader(),(int)newValue.getWidth(),(int)newValue.getHeight());
								m_fixedView.setImage(m_fixed);
								
								m_listView.getSelectionModel().clearSelection();
					    	}catch(Exception e){
					    		
					    	}
					    	
							
					    }});
					
				}catch(Exception e){
					
				}
				

			}
		});
	}
	
	  static class ImageSetter extends ListCell<Image> {
		  
		  
          private final ImageView imageView = new ImageView();
          {
              imageView.setFitHeight(100);
              imageView.setFitWidth(160);
              imageView.setPreserveRatio(true);
          }
		  
	        @Override
	        public void updateItem(Image item, boolean empty) {
	            super.updateItem(item, empty);
	            
	            if (empty){
	            	
	                setText(null);
	                setGraphic(null);
	            }
	            else{
	            	
		            if (item != null) {
		            	
		                imageView.setImage(item);
		                setGraphic(imageView);
		                
		            }
	            }

	        }
	    }

	@Override
	public WritableImage negativeFilter(Image p_image) {
		
		int height = (int)p_image.getHeight();
		int width = (int)p_image.getWidth();
		
		PixelReader reader = p_image.getPixelReader();
		WritableImage image = new WritableImage(width,height);
		PixelWriter writer = image.getPixelWriter();
		
		for (int i = 0; i< width; i++)
			for (int j = 0; j< height; j++){
				Color color = reader.getColor(i, j);
				double value = (double)color.getRed();
				writer.setColor(i, j, Color.color(1.0-value, 1.0-value, 1.0-value));
				
			}
		
		m_dataObservable.add(image);
		updateListView(m_dataObservable);
		m_fixed = image;
		updateBarChartHistogram(createHistogram(m_fixed));
		return image;
		
	}

	@Override
	public WritableImage changeContrast(Image p_image) {
		
		int height = (int)p_image.getHeight();
		int width = (int)p_image.getWidth();
		
		PixelReader reader = p_image.getPixelReader();
		WritableImage image = new WritableImage(width,height);
		PixelWriter writer = image.getPixelWriter();
		
		
		if (m_lastMin > m_slider.getLowValue() || m_lastMax < m_slider.getHighValue() )
		{
			
			for (int i = 0; i< width; i++)
				for (int j = 0; j< height; j++){
					Color color = reader.getColor(i, j);
					double value = (((int)(color.getRed()*255) - m_slider.getLowValue())*255)/(m_slider.getHighValue() - m_slider.getLowValue())/255;
					writer.setColor(i, j, Color.color(value, value, value));
					
				}
			
		}
		else{
			for (int i = 0; i< width; i++)
				for (int j = 0; j< height; j++){
					Color color = reader.getColor(i, j);
					double value = (m_slider.getLowValue() + ((color.getRed()))*(m_slider.getHighValue() - m_slider.getLowValue()))/256; 
					writer.setColor(i, j, Color.color(value, value, value));
					
				}
		}

		
		m_dataObservable.add(image);
		updateListView(m_dataObservable);
		m_fixed = image;
		updateBarChartHistogram(createHistogram(m_fixed));
		return image;
	}
	
	public void onLeftButtonClick(ActionEvent p_event){
		m_leftImage.setImage(m_fixedView.getImage());
	}
	
	public void onRightButtonClick(ActionEvent p_event){
		m_rightImage.setImage(m_fixedView.getImage());
	}
	    
	public void onPerformBinaryButtonClick(ActionEvent p_event){
		
		switch (m_operationField.getText()){
			case "-":{
				m_fixedView.setImage(imageSubstructor(m_leftImage.getImage(), m_rightImage.getImage()));
				break;
			}
			
			case "+":{
				m_fixedView.setImage(imageAdder(m_leftImage.getImage(), m_rightImage.getImage()));
				break;
			}
			
			default: {
				System.out.println("Error");
				break;
			}
		}
	}
	
	public WritableImage imageAdder(Image p_left, Image p_right){
		
		int height = (int)p_left.getHeight();
		int width = (int)p_left.getWidth();
		
		PixelReader readerLeft = p_left.getPixelReader();
		PixelReader readerRight = p_right.getPixelReader();
		WritableImage image = new WritableImage(width,height);
		PixelWriter writer = image.getPixelWriter();
		
		for (int i = 0; i< width; i++)
			for (int j = 0; j< height; j++){
				Color left = readerLeft.getColor(i, j);
				Color right = readerRight.getColor(i, j);
				double value = (left.getRed() + right.getRed())/2; 
				writer.setColor(i, j, Color.color(value, value, value));
			}
	
		m_dataObservable.add(image);
		updateListView(m_dataObservable);
		m_fixed = image;
		updateBarChartHistogram(createHistogram(m_fixed));
		return image;
	}
	
	public WritableImage imageSubstructor(Image p_left, Image p_right){
		
		int height = (int)p_left.getHeight();
		int width = (int)p_left.getWidth();
		
		PixelReader readerLeft = p_left.getPixelReader();
		PixelReader readerRight = p_right.getPixelReader();
		WritableImage image = new WritableImage(width,height);
		PixelWriter writer = image.getPixelWriter();
		
		for (int i = 0; i< width; i++)
			for (int j = 0; j< height; j++){
				Color left = readerLeft.getColor(i, j);
				Color right = readerRight.getColor(i, j);
				double value = (left.getRed() - right.getRed());
				if (value < 0){
					value = 0;
				}
				writer.setColor(i, j, Color.color(value, value, value));
			}
	
		m_dataObservable.add(image);
		updateListView(m_dataObservable);
		m_fixed = image;
		updateBarChartHistogram(createHistogram(m_fixed));
		return image;
	}
}
