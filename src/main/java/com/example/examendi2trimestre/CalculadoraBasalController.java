package com.example.examendi2trimestre;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * Controlador para la interfaz de usuario de la Calculadora Basal. Gestiona la interacción del usuario
 * con la interfaz gráfica y realiza los cálculos de la tasa metabólica basal y el gasto energético total
 * basándose en los datos introducidos por el usuario.
 * <p>
 * Además, permite generar y descargar un informe con los resultados del cálculo utilizando JasperReports.
 * </p>
 *
 * @author José Miguel Ruiz Guevara
 * @version 1.0
 */
public class CalculadoraBasalController implements Initializable {

    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfSexo;
    @FXML
    private TextField tfPeso;
    @FXML
    private TextField tfEdad;
    @FXML
    private TextField tfTalla;
    @FXML
    private TextArea tfObservaciones;
    @FXML
    private Button btnGuardar;
    @FXML
    private Label lbMensaje;
    @FXML
    private ChoiceBox<String> cbActividad;
    @FXML
    private Button btnDescargar;

    /**
     * Inicializa el controlador. Este método se llama automáticamente después de que los elementos de la
     * interfaz gráfica han sido cargados y están listos para ser usados.
     * <p>
     * Configura los elementos de la interfaz gráfica, como poblar el {@code ChoiceBox} para la selección
     * de actividad física.
     * </p>
     *
     * @param location  La ubicación utilizada para resolver rutas relativas para el objeto raíz, o
     *                  {@code null} si la ubicación no es conocida.
     * @param resources Los recursos utilizados para localizar el objeto raíz, o {@code null} si
     *                  el objeto raíz no fue localizado.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbActividad.setItems(FXCollections.observableArrayList(
                "Sedentario", "Moderado", "Activo", "Muy activo"
        ));
    }

    /**
     * Maneja la acción de calcular la tasa metabólica basal (GER) y el gasto energético total (GET) del usuario.
     * Realiza la validación de los datos introducidos y muestra el resultado o un mensaje de error en la interfaz.
     *
     * @param actionEvent El evento que desencadenó la llamada de este método.
     */
    @FXML
    public void calcular(ActionEvent actionEvent) {
        String nombre = tfNombre.getText();
        String sexo = tfSexo.getText();
        String pesoStr = tfPeso.getText();
        String edadStr = tfEdad.getText();
        String tallaStr = tfTalla.getText();
        String seleccionActividad = cbActividad.getValue();

        // Validar que todos los campos están llenos
        if (nombre.isEmpty() || sexo.isEmpty() || pesoStr.isEmpty() || edadStr.isEmpty() || tallaStr.isEmpty() || seleccionActividad == null) {
            lbMensaje.setText("Faltan datos. Por favor, complete todos los campos.");
            return;
        }

        try {
            double peso = Double.parseDouble(pesoStr);
            int edad = Integer.parseInt(edadStr);
            double talla = Double.parseDouble(tallaStr);
            double actividad = obtenerFactorActividad(sexo, seleccionActividad);
            double ger;
            double get;

            // Cálculo de GER usando la fórmula de Harris-Benedict revisada
            if (sexo.equalsIgnoreCase("Hombre")) {
                ger = (88.362 + (13.397 * peso) + (4.799 * talla) - (5.677 * edad));
            } else if (sexo.equalsIgnoreCase("Mujer")) {
                ger = (447.593 + (9.247 * peso) + (3.098 * talla) - (4.330 * edad));
            } else {
                lbMensaje.setText("El sexo introducido no es válido. Por favor, introduzca 'Hombre' o 'Mujer'.");
                return;
            }

            // Cálculo de GET multiplicando GER por el factor de actividad
            get = ger * actividad;

            // Mostrar resultados
            lbMensaje.setText(String.format("El cliente %s tiene un GER de %.0f y un GET de %.0f", nombre, ger, get));

        } catch (NumberFormatException e) {
            lbMensaje.setText("Por favor, introduzca valores numéricos en peso, edad, talla.");
        }
    }

    /**
     * Calcula el factor de actividad física basado en el sexo y la selección de actividad del usuario.
     * Este factor se utiliza en el cálculo del gasto energético total (GET).
     *
     * @param sexo      El sexo del usuario, utilizado para ajustar el factor de actividad.
     * @param actividad La actividad física seleccionada por el usuario.
     * @return El factor de actividad correspondiente a la selección del usuario.
     */
    private double obtenerFactorActividad(String sexo, String actividad) {
        switch (actividad) {
            case "Sedentario":
                return 1.3;
            case "Moderado":
                return sexo.equalsIgnoreCase("Hombre") ? 1.6 : 1.5;
            case "Activo":
                return sexo.equalsIgnoreCase("Hombre") ? 1.7 : 1.6;
            case "Muy activo":
                return sexo.equalsIgnoreCase("Hombre") ? 2.1 : 1.9;
            default:
                return 1; // Valor por defecto en caso de error
        }
    }

    /**
     * Genera y descarga un informe con los resultados del cálculo de la tasa metabólica basal y el
     * gasto energético total utilizando JasperReports.
     *
     * @param actionEvent El evento que desencadenó la llamada de este método.
     */
    @FXML
    public void generarInforme(ActionEvent actionEvent) {
        Connection conexion = null;
        try {
            // Parámetros de la conexión a la base de datos
            String url = "jdbc:mysql://servidorxemi.mysql.database.azure.com:3306/calculadoraBasal";
            Properties info = new Properties();
            info.put("user", "xemita");
            info.put("password", "Posnose90");
            info.put("useSSL", "true");
            info.put("verifyServerCertificate", "false");

            // Cargar el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establecer la conexión con la base de datos
            conexion = DriverManager.getConnection(url, info);

            // Ruta al archivo .jasper compilado
            String pathInforme = "reporteBasal.jasper";

            // Definir la ruta y el nombre del archivo PDF de salida
            String pathPDF = "reporteBasal.pdf";

            // Llenar el informe utilizando la conexión a la base de datos
            JasperPrint jasperPrint = JasperFillManager.fillReport(pathInforme, new HashMap<>(), conexion);

            // Exportar a PDF
            JasperExportManager.exportReportToPdfFile(jasperPrint, pathPDF);

            // Opcional: Mostrar un mensaje o realizar una acción después de generar el PDF
            System.out.println("Informe generado: " + pathPDF);
        } catch (ClassNotFoundException | SQLException | JRException e) {
            e.printStackTrace();
            // Manejar adecuadamente la excepción, por ejemplo, mostrando un mensaje de error.
        } finally {
            if (conexion != null) {
                try {
                    conexion.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}