import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JLabel;
import javax.swing.JComboBox;

public class ValutaConvGUI {

	private JFrame frame;
	private JTextField txtFieldEur;
	private JTextField txtFieldOutputValuta;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ValutaConvGUI window = new ValutaConvGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ValutaConvGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		List<Valuta> valute = new ArrayList<Valuta>();
		valute.add(new Valuta("$",0.96f));
		valute.add(new Valuta("¥",0.0064f));
		valute.add(new Valuta("₽",0.011f));
		frame = new JFrame();
		frame.setBounds(100, 100, 282, 148);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		txtFieldEur = new JTextField();
		txtFieldEur.setBounds(47, 80, 86, 20);
		
		frame.getContentPane().add(txtFieldEur);
		txtFieldEur.setColumns(10);
		
		txtFieldOutputValuta = new JTextField();
		txtFieldOutputValuta.setEditable(false);
		txtFieldOutputValuta.setBounds(153, 80, 86, 20);
		frame.getContentPane().add(txtFieldOutputValuta);
		txtFieldOutputValuta.setColumns(10);
		
		JLabel lblValutaSimbolo = new JLabel("$");
		lblValutaSimbolo.setBounds(143, 83, 14, 14);
		frame.getContentPane().add(lblValutaSimbolo);
		
		JLabel lblEuroSimbolo = new JLabel("€");
		lblEuroSimbolo.setBounds(10, 83, 27, 14);
		frame.getContentPane().add(lblEuroSimbolo);
		
		JComboBox<String> cmbBoxValute = new JComboBox<String>();
		cmbBoxValute.setBounds(10, 11, 92, 22);
		cmbBoxValute.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				lblValutaSimbolo.setText(cmbBoxValute.getSelectedItem().toString());
				try {
					float value = Float.parseFloat(txtFieldEur.getText());
					txtFieldEur.setText("");
					txtFieldEur.setText(""+value);
				}
				catch (NumberFormatException e1) {
					
				}
				
			}
			 
		});
		for(Valuta val : valute) {
			cmbBoxValute.addItem(val.getSymbol());
		}
		frame.getContentPane().add(cmbBoxValute);
		
		txtFieldEur.getDocument().addDocumentListener(new DocumentListener() {
			private float previousValue = 0;

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					String text = txtFieldEur.getText();
					Valuta valuta = null;
					for(Valuta val : valute) {
						String valSelected = cmbBoxValute.getSelectedItem().toString();
						if (valSelected == val.getSymbol()) valuta = val; 
					}
					float valueValuta = valuta.getValue();
					float inputValue = Float.parseFloat(txtFieldEur.getText());
					float convertedValue = inputValue/valueValuta;
					txtFieldOutputValuta.setText(""+convertedValue);
					
				} catch (NumberFormatException e1) {
					SwingUtilities.invokeLater(() -> {
						String text = txtFieldEur.getText();
						if (!text.isEmpty()) {
							txtFieldEur.setText(text.substring(0, text.length() - 1));
						}
					});

				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				try {
					String text = txtFieldEur.getText();
					Valuta valuta = null;
					for(Valuta val : valute) {
						String valSelected = cmbBoxValute.getSelectedItem().toString();
						if (valSelected == val.getSymbol()) valuta = val; 
					}
					float valueValuta = valuta.getValue();
					float inputValue = Float.parseFloat(txtFieldEur.getText());
					txtFieldOutputValuta.setText(""+valueValuta*inputValue);

				} catch (NumberFormatException e1) {
					txtFieldOutputValuta.setText("");
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// Non serve
				return;
			}
		});
		
	}
}
