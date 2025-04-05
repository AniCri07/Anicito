package verificaLab;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;

public class verific {

	private JFrame frame;
	private JTextField txtFieldOre;
	private JTextField txtFieldConvertito;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					verific window = new verific();
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
	public verific() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		ButtonGroup btnGroup = new ButtonGroup();
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		List<Mezzo> mezzi = new ArrayList<Mezzo>();

		mezzi.add(new Mezzo("Barca",5));
		mezzi.add(new Mezzo("Auto",3));
		mezzi.add(new Mezzo("Moto",2));
		mezzi.add(new Mezzo("Bici",1));
		
		JComboBox<String> cmbBoxMezzi = new JComboBox<String>();
		for (Mezzo mezzo : mezzi) {
			cmbBoxMezzi.addItem(mezzo.getNome()+" €"+mezzo.getValue());
		}
		cmbBoxMezzi.setBounds(31, 26, 94, 22);
		frame.getContentPane().add(cmbBoxMezzi);
		
		JLabel lblOre = new JLabel("N. Ore");
		lblOre.setBounds(65, 184, 46, 14);
		frame.getContentPane().add(lblOre);
		
		txtFieldOre = new JTextField();
		txtFieldOre.setBounds(122, 181, 46, 20);
		frame.getContentPane().add(txtFieldOre);
		txtFieldOre.setColumns(10);
		
		JLabel lblSimboloEuro = new JLabel("€");
		lblSimboloEuro.setBounds(178, 184, 22, 14);
		frame.getContentPane().add(lblSimboloEuro);
		
		txtFieldConvertito = new JTextField();
		txtFieldConvertito.setEditable(false);
		txtFieldConvertito.setBounds(199, 181, 74, 20);
		frame.getContentPane().add(txtFieldConvertito);
		txtFieldConvertito.setColumns(10);
		
		JRadioButton rdbtnSconto10 = new JRadioButton("Sconto 10%");
		rdbtnSconto10.setBounds(274, 26, 109, 23);
		frame.getContentPane().add(rdbtnSconto10);
		
		JRadioButton rdbtnNoSconto = new JRadioButton("No Sconto");
		rdbtnNoSconto.setBounds(274, 56, 109, 23);
		frame.getContentPane().add(rdbtnNoSconto);
		rdbtnNoSconto.setSelected(true);
		btnGroup.add(rdbtnNoSconto);
		btnGroup.add(rdbtnSconto10);
		
		JButton btnCalcola = new JButton("Calcola");
		btnCalcola.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int i = cmbBoxMezzi.getSelectedIndex();
					Mezzo mezzoSelezionato = mezzi.get(i);
					int sconto;
					if (rdbtnSconto10.isSelected()) sconto = 10;
					else if (rdbtnNoSconto.isSelected()) sconto = 0;
					else sconto = 0;
					float prezzo = Integer.parseInt(txtFieldOre.getText()) * mezzoSelezionato.getValue();
					double prezzoScontato= prezzo * (1 - sconto / 100.0);
					txtFieldConvertito.setText(""+prezzoScontato);
				} catch(Exception e1) {
					
				}
				
			}
		});
		btnCalcola.setBounds(188, 209, 85, 23);
		frame.getContentPane().add(btnCalcola);
		
		cmbBoxMezzi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnCalcola.doClick();
			}
		});
		
		rdbtnNoSconto.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnCalcola.doClick();
			}
		});
		
		rdbtnSconto10.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnCalcola.doClick();
			}
		});
		
		txtFieldOre.getDocument().addDocumentListener(new DocumentListener() {
			private float previousValue = 0;

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					String text = txtFieldOre.getText();
					int i = cmbBoxMezzi.getSelectedIndex();
					Mezzo mezzoSelezionato = mezzi.get(i);
					int sconto;
					if (rdbtnSconto10.isSelected()) sconto = 10;
					else if (rdbtnNoSconto.isSelected()) sconto = 0;
					else sconto = 0;
					float prezzo = Integer.parseInt(txtFieldOre.getText()) * mezzoSelezionato.getValue();
					double prezzoScontato= prezzo * (1 - sconto / 100.0);
					txtFieldConvertito.setText(""+prezzoScontato);
					
				} catch (NumberFormatException e1) {
					SwingUtilities.invokeLater(() -> {
						String text = txtFieldOre.getText();
						if (!text.isEmpty()) {
							txtFieldOre.setText(text.substring(0, text.length() - 1));
						}
					});

				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				try {
					String text = txtFieldOre.getText();
					int i = cmbBoxMezzi.getSelectedIndex();
					Mezzo mezzoSelezionato = mezzi.get(i);
					int sconto;
					if (rdbtnSconto10.isSelected()) sconto = 10;
					else if (rdbtnNoSconto.isSelected()) sconto = 0;
					else sconto = 0;
					float prezzo = Integer.parseInt(txtFieldOre.getText()) * mezzoSelezionato.getValue();
					double prezzoScontato= prezzo * (1 - sconto / 100.0);
					txtFieldConvertito.setText(""+prezzoScontato);

				} catch (NumberFormatException e1) {
					txtFieldConvertito.setText("");
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// Non serve
				return;
			}
		});
		
		JButton btnEsci = new JButton("Esci");
		btnEsci.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnEsci.setBounds(294, 209, 89, 23);
		frame.getContentPane().add(btnEsci);
	}
}
