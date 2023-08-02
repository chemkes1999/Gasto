import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ControlGastosGUI extends JFrame {
    private final ArrayList<Gasto> listaGastos;
    private final DefaultListModel<Gasto> gastosListModel;
    private final JLabel totalLabel;
    Color originalButtonColor = new Color(195, 195, 195);

    public ControlGastosGUI() {
        listaGastos = new ArrayList<>();
        gastosListModel = new DefaultListModel<>();

// Configuración de la ventana principal
        setTitle("Control de Gastos por Carlos Hemkes Mañueco - julio 2023");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(229, 45, 45)); // Set background color to light gray

        // Panel para el formulario de agregar gasto
        JPanel formularioPanel = new JPanel();
        formularioPanel.setLayout(new GridBagLayout());
        formularioPanel.setBackground(new Color(255, 239, 206)); // Set background color to white
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
        gbc.gridwidth = 1; // Reducir el ancho para que cada botón ocupe una columna
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton agregarButton = new JButton("Agregar");
        formularioPanel.add(agregarButton, gbc);

        gbc.gridx = 1; // Mover el siguiente botón a la columna 1
        gbc.gridy = 2;
        JButton eliminarButton = new JButton("Eliminar");
        formularioPanel.add(eliminarButton, gbc);

        gbc.gridx = 2; // Mover el último botón a la columna 2
        gbc.gridy = 2;
        JButton exportarCSVButton = new JButton("Exportar a CSV");
        formularioPanel.add(exportarCSVButton, gbc);


        // List to display expenses
        JList<Gasto> gastosList = new JList<>(gastosListModel);
        gastosList.setBackground(new Color(200, 200, 255)); // Set background color to light blue
        gastosList.setForeground(Color.BLACK); // Set text color to white

        JScrollPane scrollPane = new JScrollPane(gastosList);
        scrollPane.setBackground(new Color(200, 200, 255)); // Set background color to light blue
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
        agregarButton.setBackground(originalButtonColor); // Set background color to green
        agregarButton.setForeground(Color.BLACK); // Set text color to white

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
        eliminarButton.setBackground(originalButtonColor); // Set background color to red
        eliminarButton.setForeground(Color.BLACK); // Set text color to white
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
        exportarCSVButton.setBackground(originalButtonColor); // Set background color to blue
        exportarCSVButton.setForeground(Color.BLACK); // Set text color to black

        // Configuración del contenido principal
        setLayout(new BorderLayout());
        add(formularioPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(totalPanel, BorderLayout.SOUTH);


        // Animación para resaltar los campos de entrada
        Color originalColor = new Color(255, 239, 206);
        Color highlightColor = new Color(255, 255, 153); // Amarillo claro

        descripcionField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                formularioPanel.setBackground(highlightColor);
            }

            @Override
            public void focusLost(FocusEvent e) {
                formularioPanel.setBackground(originalColor);
            }
        });

        montoField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                formularioPanel.setBackground(highlightColor);
            }

            @Override
            public void focusLost(FocusEvent e) {
                formularioPanel.setBackground(originalColor);
            }
        });

        // Animación para resaltar los botones
        Color originalButtonColor = new Color(195, 195, 195);
        Color addButtonColor = new Color(0, 200, 0); // Verde más claro
        Color deleteButtonColor = new Color(200, 60, 0); // Verde más claro
        Color exportButtonColor = new Color(0, 178, 255); // Verde más claro

        agregarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                agregarButton.setBackground(addButtonColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                agregarButton.setBackground(originalButtonColor);
            }
        });

        eliminarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                eliminarButton.setBackground(deleteButtonColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                eliminarButton.setBackground(originalButtonColor);
            }
        });

        exportarCSVButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                exportarCSVButton.setBackground(exportButtonColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                exportarCSVButton.setBackground(originalButtonColor);
            }
        });
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

            double total = 0;
            // Escribir los datos de los gastos
            for (Gasto gasto : listaGastos) {
                total += gasto.getMonto();
                writer.write(gasto.getDescripcion() + "," + gasto.getMonto() + "\n");
            }
            writer.write("");
            writer.write("TOTAL: " + "," + total);
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
