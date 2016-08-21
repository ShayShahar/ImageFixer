package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;

public class MainWindowController implements ITransforms, Initializable{

	private final FileChooser m_chooser = new FileChooser();
	private Image m_original = null;
	private WritableImage m_fixed = null;
	private RangeSlider m_slider;
	@FXML private ImageView m_originalView;
	@FXML private ImageView m_fixedView;
	@FXML private ImageView m_rightImage;
	@FXML private ImageView m_leftImage;
	@FXML private Pane m_emptyPane;
	@FXML private Label m_lowValue;
	@FXML private AreaChart<String, Integer> m_histogramChart;
	@FXML private ListView<Image> m_listView = new ListView<Image>();
	@FXML private TextField m_mat00, m_mat01, m_mat02, m_mat10, m_mat11, m_mat12, m_mat20, m_mat21, m_mat22;
	@FXML private TextField m_mat500, m_mat501, m_mat502, m_mat503, m_mat504, m_mat510, m_mat511, m_mat512, m_mat513, m_mat514,
	m_mat520, m_mat521, m_mat522, m_mat523, m_mat524, m_mat530, m_mat531, m_mat532, m_mat533, m_mat534, m_mat540, m_mat541, m_mat542, m_mat543, m_mat544;
	@FXML private GridPane m_threeGrid;
	@FXML private GridPane m_fiveGrid;
	@FXML private Button m_switchButton, m_negButton, m_cngButton, m_maskButton, m_clearButton, m_subButton, m_addButton;
	private ArrayList<Image> m_dataObservable = new ArrayList<Image>();
	private int m_lastMin;
	private int m_lastMax;
	private int m_kernelSize;
	@FXML private StackPane m_leftPane, m_rightPane;
	private boolean m_allowLoadFromImage = true;
	@FXML private AnchorPane m_mainPane;
	@FXML private MenuBar m_menuBar;
	@FXML private Label m_lable22, m_lable221;

	private static double xOffset = 0;
	private static double yOffset = 0;
	
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
		
		if (m_allowLoadFromImage){
			InputStream inputStream = null;

			configureFileChooser(m_chooser);
			m_chooser.setTitle("Save Image");
			
			File file = m_chooser.showOpenDialog(null);
				
	    	 if (file != null) {

	    		 try {
	    			 inputStream = new FileInputStream(file.getPath());  // get the file path
	    			 m_original = new Image(inputStream);
	    			 m_originalView.setImage(m_original);
	    			 m_fixedView.setImage(greyScaleImage(m_original));
	    			 m_negButton.setDisable(false);
	    			 m_cngButton.setDisable(false);
	    			 m_maskButton.setDisable(false);

	    	    	 m_allowLoadFromImage = false;

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
	    	 }
	    	 
		}

			 	
	}
	
	public void onMinimizeButtonClick(ActionEvent p_event){
		Stage stage = (Stage) m_threeGrid.getScene().getWindow();
		stage.setIconified(true);
	}
	
	 
	public void onClearButtonMenu(ActionEvent p_event){
		m_dataObservable.clear();
		updateListView(m_dataObservable);
		m_histogramChart.getData().clear();
		m_fixedView.setImage(null);
		URL url = MainWindowController.class.getResource("/img/imagehere.png");
		m_originalView.setImage(new Image(url.toString()));
		m_allowLoadFromImage = true;
		m_negButton.setDisable(true);
		m_cngButton.setDisable(true);
		m_maskButton.setDisable(true);
		m_leftImage.setImage(null);
		m_rightImage.setImage(null);
	}
	
	public void onBasicTransformationLink(ActionEvent p_event){
		 try {
			java.awt.Desktop.getDesktop().browse(new URI("https://en.wikipedia.org/wiki/Kernel_(image_processing)"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			
			e.printStackTrace();
		}

	
	}
		
	public void onLoadImageMenuClick(ActionEvent p_event){
		
		InputStream inputStream = null;

		configureFileChooser(m_chooser);
		m_chooser.setTitle("Save Image");
		
		File file = m_chooser.showOpenDialog(null);
			
    	 if (file != null) {

    		 try {
    			 inputStream = new FileInputStream(file.getPath());  // get the file path
    			 m_original = new Image(inputStream);
    			 m_originalView.setImage(m_original);
    			 m_fixedView.setImage(greyScaleImage(m_original));
    			 m_negButton.setDisable(false);
    			 m_cngButton.setDisable(false);
    			 m_maskButton.setDisable(false);

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

	@Override
	public void saveImage(File p_file) {
		
        try {
			ImageIO.write(SwingFXUtils.fromFXImage(m_fixed, null), "png", p_file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void onSaveImageButtonClick(ActionEvent p_event){
		FileChooser fileChooser = new FileChooser();
		
		//Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files (*.png)", "*.png");
		fileChooser.getExtensionFilters().add(extFilter);
		
		//Show save file dialog
		File file = fileChooser.showSaveDialog(new Stage());
		
		if(file != null){
			saveImage(file);
		}
		
	}
	
	 private  void configureFileChooser( final FileChooser p_chooser){   
		  
		 p_chooser.setTitle("Load Image");
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
	public double[] createHistogram(Image p_image) {
			double[] histogram = new double[256];
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
	        
	        //normalize histogram
	        for (int i = 0 ; i < 256 ; i++){
	        	histogram[i] = histogram[i]/(height * width);
	        	
	        }

			return histogram;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void updateBarChartHistogram(double[] p_histogram){
		
		Series<String, Integer> s1 = new XYChart.Series<>();

		for (int i = 0; i<256; i++){
			Double value = p_histogram[i];
			String index = Integer.toString(i);
			s1.getData().add(new XYChart.Data(index, value));
		}
											
		m_histogramChart.getData().clear();
		m_histogramChart.getData().add(s1);
		
		updateSlider(p_histogram);
	}
	
	private void updateSlider(double[] p_histogram){
		
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
    	m_lable221.setText(Integer.toString((int)m_slider.getHighValue()));
    	m_lable22.setText(Integer.toString((int)m_slider.getLowValue()));

	}
	
	public void onEnableMaskButtonClick(ActionEvent event){
		double[][] kernelMatrix;
		
		if (m_kernelSize == 3){
			kernelMatrix  = new double[3][3];
			
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
				
				m_fixedView.setImage(enableMaskFilter(m_fixed, kernelMatrix));
				
			}catch(Exception e){
				displayErrorMessage("Invalid Input","Some fields are missing or not include a number in a floating point format");
				return;
			}
		}
		
		else{
			kernelMatrix  = new double[5][5];

			try{
				kernelMatrix[0][0] = Double.parseDouble(m_mat500.getText());	
				kernelMatrix[0][1] = Double.parseDouble(m_mat501.getText());
				kernelMatrix[0][2] = Double.parseDouble(m_mat502.getText());
				kernelMatrix[0][3] = Double.parseDouble(m_mat503.getText());
				kernelMatrix[0][4] = Double.parseDouble(m_mat504.getText());
				
				kernelMatrix[1][0] = Double.parseDouble(m_mat510.getText());
				kernelMatrix[1][1] = Double.parseDouble(m_mat511.getText());
				kernelMatrix[1][2] = Double.parseDouble(m_mat512.getText());
				kernelMatrix[1][3] = Double.parseDouble(m_mat513.getText());
				kernelMatrix[1][4] = Double.parseDouble(m_mat514.getText());
				
				kernelMatrix[2][0] = Double.parseDouble(m_mat520.getText());
				kernelMatrix[2][1] = Double.parseDouble(m_mat521.getText());
				kernelMatrix[2][2] = Double.parseDouble(m_mat522.getText());
				kernelMatrix[2][3] = Double.parseDouble(m_mat523.getText());
				kernelMatrix[2][4] = Double.parseDouble(m_mat524.getText());
				
				kernelMatrix[3][0] = Double.parseDouble(m_mat530.getText());
				kernelMatrix[3][1] = Double.parseDouble(m_mat531.getText());
				kernelMatrix[3][2] = Double.parseDouble(m_mat532.getText());
				kernelMatrix[3][3] = Double.parseDouble(m_mat533.getText());
				kernelMatrix[3][4] = Double.parseDouble(m_mat534.getText());
				
				kernelMatrix[4][0] = Double.parseDouble(m_mat540.getText());
				kernelMatrix[4][1] = Double.parseDouble(m_mat541.getText());
				kernelMatrix[4][2] = Double.parseDouble(m_mat542.getText());
				kernelMatrix[4][3] = Double.parseDouble(m_mat543.getText());
				kernelMatrix[4][4] = Double.parseDouble(m_mat544.getText());
				
				m_fixedView.setImage(enableMaskFilter(m_fixed, kernelMatrix));
				
			}catch(Exception e){
				displayErrorMessage("Invalid Input","Some fields are missing or not include a number in a floating point format");
				return;
			}
			 
		}
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
		
		if (m_kernelSize == 3){
			for (int i = 0; i< width; i++){
				for (int j = 0; j< height; j++){
					if (i == 0 || j == 0 || i == width - 1 || j == height - 1){
						writer.setColor(i, j, Color.BLACK);
					}
					else{
						double sum = 0;
						for (int k = 0, z= i-1; k < m_kernelSize; k++, z++){
							for  (int t = 0, w= j-1; t < m_kernelSize; t++, w++){
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
				
		}
		else{
			
			for (int i = 0; i< width; i++){
				for (int j = 0; j< height; j++){
					if (i == 0 || i == 1 || j == 1 || j == 0 || i == width - 2  || i == width - 1 || j == height - 2 || j == height - 1){
						writer.setColor(i, j, Color.BLACK);
					}
					else{
						double sum = 0;
						for (int k = 0, z= i-2; k < m_kernelSize; k++, z++){
							for  (int t = 0, w= j-2; t < m_kernelSize; t++, w++){
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
		m_slider.setMinWidth(535);
		
		
		m_slider.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	m_lable22.setText(Integer.toString((int)m_slider.getLowValue()));
            	m_lable221.setText(Integer.toString((int)m_slider.getHighValue()));

            }
        });
		
		m_emptyPane.getChildren().add(m_slider);
		m_kernelSize = 3;
		
		
		m_negButton.setDisable(true);
		m_cngButton.setDisable(true);
		m_maskButton.setDisable(true);
		
		m_mat00.setStyle("-fx-alignment: CENTER;");
		m_mat01.setStyle("-fx-alignment: CENTER;");
		m_mat02.setStyle("-fx-alignment: CENTER;");
		m_mat10.setStyle("-fx-alignment: CENTER;");
		m_mat11.setStyle("-fx-alignment: CENTER;");
		m_mat12.setStyle("-fx-alignment: CENTER;");
		m_mat20.setStyle("-fx-alignment: CENTER;");
		m_mat21.setStyle("-fx-alignment: CENTER;");
		m_mat22.setStyle("-fx-alignment: CENTER;");
		m_mat500.setStyle("-fx-alignment: CENTER;");
		m_mat501.setStyle("-fx-alignment: CENTER;");
		m_mat502.setStyle("-fx-alignment: CENTER;");
		m_mat503.setStyle("-fx-alignment: CENTER;");
		m_mat504.setStyle("-fx-alignment: CENTER;");
		m_mat510.setStyle("-fx-alignment: CENTER;");
		m_mat511.setStyle("-fx-alignment: CENTER;");
		m_mat512.setStyle("-fx-alignment: CENTER;");
		m_mat513.setStyle("-fx-alignment: CENTER;");
		m_mat514.setStyle("-fx-alignment: CENTER;");
		m_mat520.setStyle("-fx-alignment: CENTER;");
		m_mat521.setStyle("-fx-alignment: CENTER;");
		m_mat522.setStyle("-fx-alignment: CENTER;");
		m_mat523.setStyle("-fx-alignment: CENTER;");
		m_mat524.setStyle("-fx-alignment: CENTER;");
		m_mat530.setStyle("-fx-alignment: CENTER;");
		m_mat531.setStyle("-fx-alignment: CENTER;");
		m_mat532.setStyle("-fx-alignment: CENTER;");
		m_mat533.setStyle("-fx-alignment: CENTER;");
		m_mat534.setStyle("-fx-alignment: CENTER;");
		m_mat540.setStyle("-fx-alignment: CENTER;");
		m_mat541.setStyle("-fx-alignment: CENTER;");	
		m_mat542.setStyle("-fx-alignment: CENTER;");	
		m_mat543.setStyle("-fx-alignment: CENTER;");
		m_mat544.setStyle("-fx-alignment: CENTER;");
		 
		m_clearButton.setStyle("-fx-text-fill: #ffffff");
		
		m_menuBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
        		Stage stage = (Stage) m_threeGrid.getScene().getWindow();
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        });
		
		m_menuBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
        		Stage stage = (Stage) m_threeGrid.getScene().getWindow();

        		stage.setX(event.getScreenX() + xOffset);
        		stage.setY(event.getScreenY() + yOffset);
            }
        });

		
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
		
		
		if (m_lastMin > m_slider.getLowValue() || m_lastMax < m_slider.getHighValue()){
			
			for (int i = 0; i< width; i++)
				for (int j = 0; j< height; j++){
					Color color = reader.getColor(i, j);
					int range = (int)m_slider.getHighValue() - (int)m_slider.getLowValue();
					double irc = color.getRed() * 255.0 - m_lastMin;
					int delta = m_lastMax - m_lastMin;
					double value = irc * range / delta + (int)m_slider.getLowValue();
					
					value = value / 255.0;
					
					if (value > 1)
						value = 1.0;
					
					if (value < 0)
						value = 0;
							
					writer.setColor(i, j, Color.color(value, value, value));
					
				}
			 
		}
		else if (m_lastMin == m_slider.getLowValue() && m_lastMax == m_slider.getHighValue()){
			
			for (int i = 0; i< width; i++)
				for (int j = 0; j< height; j++){
					Color color = reader.getColor(i, j);
					writer.setColor(i, j, color);
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
	
	public void onCleartResultButtonClick(ActionEvent p_event){
		m_dataObservable.clear();
		updateListView(m_dataObservable);
	}
	    
	public void onAddingButtonClick(ActionEvent p_event){
		
		if (m_leftImage.getImage() == null || m_rightImage.getImage() == null){
			displayErrorMessage("Missing Images","Please load images to the stack before try to perform a binary operation.");
			return;
		}
		m_fixedView.setImage(imageAdder(m_leftImage.getImage(), m_rightImage.getImage()));
	}
	
	public void onSubButtonClick(ActionEvent p_event){
		
		if (m_leftImage.getImage() == null || m_rightImage.getImage() == null){
			displayErrorMessage("Missing Images","Please load images to the stack before try to perform a binary operation.");
			return;
		}
		m_fixedView.setImage(imageSubstructor(m_leftImage.getImage(), m_rightImage.getImage()));
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
	
	private void displayErrorMessage(String title, String information) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				URL url = MainWindowController.class.getResource("/img/error.png");
				Dialog<Pair<String, String>> dialog = new Dialog<>();
				dialog.setTitle("Error Message");
				dialog.setHeaderText(title);
				dialog.setContentText(information);
				dialog.setGraphic(new ImageView(url.toString()));
				dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
				dialog.showAndWait();
			}
		});			
	}
	
	public void onSwitchKernelButtonClick(ActionEvent p_event){
		
		if (m_threeGrid.isVisible()){
			m_threeGrid.setVisible(false);
			m_fiveGrid.setVisible(true);
			m_kernelSize = 5;
			m_switchButton.setText("Switch to 3x3 kernel");
		}
		else{
			m_threeGrid.setVisible(true);
			m_fiveGrid.setVisible(false);
			m_kernelSize = 3;
			m_switchButton.setText("Switch to 5x5 kernel");
		}
	}
	
	public void onExitButtonClick(ActionEvent p_event){
		System.exit(1);
	}
}
