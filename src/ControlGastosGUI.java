import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
class Gasto {
    private String descripcion;
    private double monto;

    public Gasto(String descripcion, double monto) {
        this.descripcion = descripcion;
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getMonto() {
        return monto;
    }

    @Override
    public String toString() {
        return "- Descripción: " + descripcion + ", Monto: $" + monto;
    }
}

public class ControlGastosGUI extends JFrame {
    private ArrayList<Gasto> listaGastos;
    private DefaultListModel<Gasto> gastosListModel;
    private JLabel totalLabel;

    public ControlGastosGUI() {
        listaGastos = new ArrayList<>();
        gastosListModel = new DefaultListModel<>();

// Configuración de la ventana principal
        setTitle("Control de Gastos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        setLocationRelativeTo(null);

        // Panel para el formulario de agregar gasto
        JPanel formularioPanel = new JPanel();
        formularioPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formularioPanel.add(new JLabel("Descripción:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField descripcionField = new JTextField();
        formularioPanel.add(descripcionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        formularioPanel.add(new JLabel("Monto:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0; // Ajusta el campo de entrada del monto para que se expanda horizontalmente
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField montoField = new JTextField();
        formularioPanel.add(montoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton agregarButton = new JButton("Agregar");
        formularioPanel.add(agregarButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton eliminarButton = new JButton("Eliminar");
        formularioPanel.add(eliminarButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton exportarCSVButton = new JButton("Exportar a CSV");
        formularioPanel.add(exportarCSVButton, gbc);

        // Lista para mostrar los gastos
        JList<Gasto> gastosList = new JList<>(gastosListModel);
        JScrollPane scrollPane = new JScrollPane(gastosList);

        // Panel para mostrar el total de gastos
        JPanel totalPanel = new JPanel();
        totalLabel = new JLabel("Total de gastos: $0.00");
        totalPanel.add(totalLabel);

        // Acción para el botón "Agregar"
        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String descripcion = descripcionField.getText().trim();
                String montoStr = montoField.getText().trim();

                if (descripcion.isEmpty() || montoStr.isEmpty()) {
                    JOptionPane.showMessageDialog(ControlGastosGUI.this, "Debe completar todos los campos.",
                            "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    double monto = Double.parseDouble(montoStr);

                    Gasto nuevoGasto = new Gasto(descripcion, monto);
                    listaGastos.add(nuevoGasto);
                    gastosListModel.addElement(nuevoGasto);

                    calcularTotalGastos();

                    descripcionField.setText("");
                    montoField.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ControlGastosGUI.this, "El monto debe ser un número válido.",
                            "Formato inválido", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Acción para el botón "Eliminar"
        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int indiceSeleccionado = gastosList.getSelectedIndex();
                if (indiceSeleccionado != -1) {
                    listaGastos.remove(indiceSeleccionado);
                    gastosListModel.removeElementAt(indiceSeleccionado);
                    calcularTotalGastos();
                } else {
                    JOptionPane.showMessageDialog(ControlGastosGUI.this, "Seleccione un gasto de la lista para eliminar.",
                            "Elemento no seleccionado", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        exportarCSVButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Guardar archivo CSV");
                int userSelection = fileChooser.showSaveDialog(ControlGastosGUI.this);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    if (!filePath.toLowerCase().endsWith(".csv")) {
                        filePath += ".csv"; // Agregar extensión .csv si no se ha especificado
                    }
                    exportarCSV(filePath);
                }
            }
        });
        // Configuración del contenido principal
        setLayout(new BorderLayout());
        add(formularioPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(totalPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
    private void exportarCSV(String rutaArchivo) {
        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            // Escribir la fecha como un título
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String fechaTitulo = "Fecha (" + dateFormat.format(new Date()) + ")";
            writer.write(fechaTitulo + "\n");
            // Escribir el encabezado
            writer.write("DESCRIPCION,MONTO\n");

            double total=0;
            // Escribir los datos de los gastos
            for (Gasto gasto : listaGastos) {
                total += gasto.getMonto();
                writer.write(gasto.getDescripcion() + "," + gasto.getMonto() + "\n");
            }
            writer.write("");
            writer.write("TOTAL: "+total);
            JOptionPane.showMessageDialog(ControlGastosGUI.this,
                    "Datos exportados correctamente a " + rutaArchivo, "Exportación exitosa", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(ControlGastosGUI.this,
                    "Error al exportar los datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calcularTotalGastos() {
        double total = 0;
        for (Gasto gasto : listaGastos) {
            total += gasto.getMonto();
        }
        totalLabel.setText("Total de gastos: $" + String.format("%.2f", total));
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ControlGastosGUI();
            }
        });
    }
}
