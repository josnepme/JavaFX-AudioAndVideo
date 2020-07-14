/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carmelo.javafx;

import javafx.application.Application;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaMarkerEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

/**
 *
 * @author Carmelo Marín Abrego
 */
public class PlayMedia extends Application {

    @Override
    public void start(Stage primaryStage) {
        
//        AudioClip audio = new AudioClip(url /*"file:///c:/developer/temp/audio.mp3"*/);
//        audio.play();
//        audio.setVolume(0.85);
        
        //String url = getClass().getResource("audio.mp3").toExternalForm();  
        //String url = "https://www.youtube.com/embed/qGNWMcfWwPU"
        String url = "file:///D:Trolls2.mp4";
        
        Media media = new Media(url);
        media.setOnError(() -> System.out.println("Media: " + media.getError().getMessage()));

        MediaPlayer player = new MediaPlayer(media);
        player.setOnError(() -> System.out.println("MediaPlayer: " + player.getError().getMessage()));
        player.statusProperty().addListener((prop, oldStatus, newStatus) -> {
            // se ha producido un cambio de estado
            System.out.println("Estatus cambio de " + oldStatus + " a " + newStatus);
        });
        
        // crear marcadores, son indicadores que se disparan al alcanzar el timepo definido 
        ObservableMap<String, Duration> markers = media.getMarkers();
        markers.put("START", Duration.ZERO);
        markers.put("INTERVAL", Duration.minutes(0.8));
        markers.put("END", media.getDuration());

        // manejar marcadores
        player.setOnMarker((MediaMarkerEvent e) -> {
            Pair<String, Duration> marker = e.getMarker();
            String markerText = marker.getKey();
            Duration markerTime = marker.getValue();
            System.out.println("Reached the marker " + markerText + " at " + markerTime);
        });

        MediaView view = new MediaView(player);
        view.setPreserveRatio(false);
        view.setFitHeight(360);
        view.setFitWidth(640);

        //----------------------------------------------------------------------//
        
        Slider slider_time = new Slider();
        Label actual_time = new Label("0.00");
        Label total_time = new Label("0.00");
        
        player.setOnReady(() -> {

            // obtener metadatos, si existen
            media.getMetadata().forEach((k, v) -> System.out.println(k + ", " + v));
            
            total_time.setText(String.format("%.2f", player.getTotalDuration().toMinutes()));     
            slider_time.setMax(player.getTotalDuration().toSeconds());
            
            slider_time.valueProperty().addListener((p, o, value) -> {
                if (slider_time.isPressed()) {
                    player.seek(Duration.seconds(value.doubleValue()));
                }
            });

            player.currentTimeProperty().addListener((p, o, value) -> {
                slider_time.setValue(value.toSeconds());
                actual_time.setText(String.format("%.2f", value.toMinutes()));
            });
        });
        
        HBox.setHgrow(slider_time, Priority.ALWAYS);
        HBox time_bar = new HBox(actual_time, slider_time, total_time);
        time_bar.setSpacing(10.0);

        //----------------------------------------------------------------------// 
        
        Label lbl_volumen = new Label("Volumen");
        
        Slider volumen = new Slider(0, 1, 0.8);
        player.volumeProperty().bind(volumen.valueProperty());

        Label actual_volumen = new Label("80%");
        actual_volumen.textProperty().bind(player.volumeProperty().multiply(100.0).asString("%.2f %%"));
        
        Button play  = new Button("Reproducir");
        Button pause = new Button("Pausar");
        Button stop  = new Button("Detener");

        play.setOnAction(e -> player.play());
        pause.setOnAction(e -> player.pause());
        stop.setOnAction(e -> player.stop());

        Label cur_rate = new Label("1x");
        cur_rate.textProperty().bind(player.rateProperty().asString("%.1fx"));
        
        Button inc_rate  = new Button(">>");
        Button dec_rate  = new Button("<<");
        
        // los valores validos para setRate van de 0 a 8
        inc_rate.setOnAction(e -> player.setRate(player.getRate() + 1));
        dec_rate.setOnAction(e -> player.setRate(player.getRate() - 1));
        
        HBox.setHgrow(volumen, Priority.ALWAYS);
        HBox panel = new HBox(
                play, pause, stop, 
                dec_rate, cur_rate, inc_rate, 
                lbl_volumen, volumen, actual_volumen);
        
        panel.setSpacing(10.0);
        panel.setAlignment(Pos.CENTER);

        //---------------------------------------------------------------------//
        
        VBox root = new VBox(view, time_bar, panel);
        root.setPadding(new Insets(10.0));
        root.setSpacing(10.0);

        Scene scene = new Scene(root);

        primaryStage.setTitle("JavaFX Media API");
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
