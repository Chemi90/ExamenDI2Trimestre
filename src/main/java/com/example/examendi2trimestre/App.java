package com.example.examendi2trimestre;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * La clase {@code App} extiende de {@link javafx.application.Application} y sirve como punto de entrada principal
 * para la aplicación de la Calculadora Basal. Inicializa la interfaz de usuario y configura la escena principal.
 * <p>
 * Esta aplicación utiliza JavaFX para crear una interfaz gráfica que permite a los usuarios calcular su tasa
 * metabólica basal utilizando diversos parámetros de entrada.
 * </p>
 *
 * <h2>Uso:</h2>
 * Para ejecutar la aplicación, llame al método {@code main} de esta clase. La interfaz gráfica se cargará, mostrando
 * la ventana de la Calculadora Basal.
 *
 * <h2>Características:</h2>
 * <ul>
 *   <li>Carga un archivo FXML que describe la interfaz de usuario de la Calculadora Basal.</li>
 *   <li>Configura y muestra la ventana principal de la aplicación.</li>
 * </ul>
 *
 * @author José Miguel Ruiz Guevara
 * @version 1.0
 */
public class App extends Application {

    /**
     * Inicia y muestra la ventana principal de la aplicación.
     * <p>
     * Este método carga la interfaz de usuario desde un archivo FXML, configura la escena y muestra la ventana
     * principal. El título de la ventana se establece en "Calculadora Basal!".
     * </p>
     *
     * @param stage El {@link Stage} principal en el que se mostrará la escena.
     * @throws IOException Si ocurre un error al cargar el archivo FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("calculadorabasal-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 541, 637);
        stage.setTitle("Calculadora Basal!");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Método principal que lanza la aplicación.
     * <p>
     * Este método es el punto de entrada de la aplicación. Invoca el método {@code launch} heredado de
     * {@link javafx.application.Application}, que a su vez llama al método {@code start}.
     * </p>
     *
     * @param args Argumentos de la línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        launch();
    }
}