import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

public class Main {

    private JFrame frame;
    private JTextField txtFieldCognome;
    private JTextField txtFieldNome;
    private JTextField txtFieldTelefono;
    private JTextField txtFieldEmail;
    private Clip clip; 
    private DefaultListModel<Contatto> model;
    private boolean chiamataInCorso = false;


    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main window = new Main();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Main() {
        initialize();
        avviaChiamataSpammer();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 495, 357);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        model = new DefaultListModel<>();
        JList<Contatto> jListContatti = new JList<>(model);
        jListContatti.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { 
                    int index = jListContatti.locationToIndex(evt.getPoint());
                    if (index != -1) {
                        Contatto contatto = model.getElementAt(index);
                        openModifyFrame(contatto, index);
                    }
                }
            }
        });
        JScrollPane scrollPaneContatti = new JScrollPane(jListContatti);
        scrollPaneContatti.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneContatti.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPaneContatti.setBounds(10, 25, 459, 240);
        frame.getContentPane().add(scrollPaneContatti);

        JButton btnInserisci = new JButton("Ins");
        btnInserisci.setBounds(215, 265, 60, 23);

        btnInserisci.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createFrame();
            }
        });
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem eliminaItem = new JMenuItem("Elimina");
        JMenuItem chiamamiItem = new JMenuItem("Chiamami più tardi");
        
        eliminaItem.addActionListener(e -> {
            int[] selectedIndices = jListContatti.getSelectedIndices();
            if (selectedIndices.length == 0) {
                JOptionPane.showMessageDialog(frame, "Seleziona almeno un contatto da eliminare.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
            for (int i = selectedIndices.length - 1; i >= 0; i--) {
                model.remove(selectedIndices[i]);
            }
        });
        

        chiamamiItem.addActionListener(e -> {
            int selectedIndex = jListContatti.getSelectedIndex();
            if (selectedIndex != -1) {
            	String programmazione;
            	if (chiamataInCorso) {
            		programmazione = "Chiamata già programmata!";
            	}
            	else programmazione = "Chiamata programmata!";
            	
                Contatto contatto = model.getElementAt(selectedIndex);

                JLabel lblNotifica = new JLabel(programmazione);
                lblNotifica.setBounds(200, 50, 200, 30); 
                lblNotifica.setForeground(Color.BLUE); 
                frame.getContentPane().add(lblNotifica);

                javax.swing.Timer timer = new javax.swing.Timer(20, new ActionListener() {
                    int posX = 200;
                    int posY = 50;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (posY > 0 && posX < frame.getWidth() - 200) {
                            posX += 2; 
                            posY -= 1; 
                            lblNotifica.setBounds(posX, posY, 200, 30); 
                        } else {
                            ((javax.swing.Timer) e.getSource()).stop();
                        }
                    }
                });
                timer.start();

                javax.swing.Timer removeTimer = new javax.swing.Timer(2000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        frame.getContentPane().remove(lblNotifica); 
                        frame.repaint(); 
                    }
                });
                removeTimer.setRepeats(false);
                removeTimer.start();
                if (chiamataInCorso) return;
                avviaNotificaChiamata(contatto);
            } else {
                JOptionPane.showMessageDialog(frame, "Seleziona almeno un contatto da avvisare.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
        });
        
        popupMenu.add(eliminaItem);
        popupMenu.add(chiamamiItem);
        jListContatti.setComponentPopupMenu(popupMenu);
        jListContatti.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        frame.getContentPane().add(btnInserisci);
        
        JLabel lblTitolo = new JLabel("Rubrica Contatti");
        lblTitolo.setBounds(177, 2, 98, 13);
        frame.getContentPane().add(lblTitolo);

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        JMenu mnNewMenu = new JMenu("File");
        menuBar.add(mnNewMenu);
        JMenuItem mntmSalva = new JMenuItem("Salva");
        JMenuItem mntmImporta = new JMenuItem("Importa");
        JMenuItem mntmEsci = new JMenuItem("Esci");
        mntmEsci.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
	        	System.exit(0);
			}
        });
        mnNewMenu.add(mntmSalva);
        mnNewMenu.add(mntmImporta);
        mnNewMenu.add(mntmEsci);
        
        mntmSalva.addActionListener(e -> salvaContatti());
        mntmImporta.addActionListener(e -> importaContatti());
    }
    
    private void avviaChiamataSpammer() {
        int delay = 10000 + new Random().nextInt(15000); 

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (chiamataInCorso) {
                    while (chiamataInCorso) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                chiamataInCorso = true;
                Contatto spammer = new Contatto("Assistenza Microsoft", null, null, null);
                riproduciSuoneria();
                SwingUtilities.invokeLater(() -> {
                    mostraFinestraChiamata(spammer, "Images/spammer.jpg");
                });

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        avviaChiamataSpammer(); 
                    }
                }, 5000);
            }
        }, delay);
    }
    
    private void avviaNotificaChiamata(Contatto contatto) {
    	chiamataInCorso = true;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                riproduciSuoneria();
                SwingUtilities.invokeLater(() -> mostraFinestraChiamata(contatto,"Images/immagineContatto.jpg"));
            }
        }, 10000);
    }
    

	private void riproduciSuoneria() {
	    try {
	        //File fileAudio = new File("Sounds/callNotification.wav"); 
	        //AudioInputStream audioStream = AudioSystem.getAudioInputStream(fileAudio);
	        //clip = AudioSystem.getClip();
	        //clip.open(audioStream);
	        //clip.start();
	        //clip.loop(Clip.LOOP_CONTINUOUSLY); 
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	private void stopSuoneria() {
	    if (clip != null && clip.isRunning()) {
	        clip.stop();
	        clip.close();
	    }
	}

	private void mostraFinestraChiamata(Contatto contatto, String imagePath) {
		
	    JDialog chiamataFrame = new JDialog(frame, "Chiamata in arrivo", true);
	    chiamataFrame.setSize(300, 250);
	    chiamataFrame.setLayout(new BorderLayout());
	    chiamataFrame.setResizable(false);
	    
	    ImageIcon icon = new ImageIcon(imagePath); 
	    ImageIcon originalIcon = new ImageIcon(imagePath);

		 Image scaledImage = originalIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
		 ImageIcon scaledIcon = new ImageIcon(scaledImage);
	
		 JLabel imageLabel = new JLabel(scaledIcon, SwingConstants.CENTER);

	    JLabel label = new JLabel("Chiamata da: " + contatto.getNome(), SwingConstants.CENTER);
	    label.setFont(new Font("Arial", Font.BOLD, 16));
	    JButton accettaButton = new JButton("Rifiuta");
	    JButton rifiutaButton = new JButton("Rifiuta");

	    accettaButton.addActionListener(e -> {
	    	chiamataInCorso = false;
	        stopSuoneria();
	        chiamataFrame.dispose();
	    });

	    rifiutaButton.addActionListener(e -> {
	    	chiamataInCorso = false;
	        stopSuoneria();
	        chiamataFrame.dispose();
	    });

	    JPanel buttonPanel = new JPanel();
	    buttonPanel.add(accettaButton);
	    buttonPanel.add(rifiutaButton);

	    chiamataFrame.add(imageLabel, BorderLayout.NORTH);
	    chiamataFrame.add(label, BorderLayout.CENTER);
	    chiamataFrame.add(buttonPanel, BorderLayout.SOUTH);

	    chiamataFrame.setLocationRelativeTo(frame);
	    chiamataFrame.setVisible(true);
	}

    
    private void openModifyFrame(Contatto contatto, int index) {
        JFrame modifyFrame = new JFrame("Modifica Contatto");
        modifyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2, 10, 10));

        JLabel nomeLabel = new JLabel("Nome:");
        JLabel cognomeLabel = new JLabel("Cognome:");
        JLabel emailLabel = new JLabel("Email:");
        JLabel telefonoLabel = new JLabel("Telefono:");

        JTextField nomeField = new JTextField(contatto.getNome());

        JTextField cognomeField = new JTextField(contatto.getCognome());

        JTextField emailField = new JTextField(contatto.getEmail());
        JTextField telefonoField = new JTextField(contatto.getTelefono());

        JButton confermaButton = new JButton("Conferma");
        JButton annullaButton = new JButton("Annulla");

        panel.add(nomeLabel);
        panel.add(nomeField);
        panel.add(cognomeLabel);
        panel.add(cognomeField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(telefonoLabel);
        panel.add(telefonoField);
        panel.add(confermaButton);
        panel.add(annullaButton);

        modifyFrame.getContentPane().add(BorderLayout.CENTER, panel);
        modifyFrame.pack();
        modifyFrame.setLocationByPlatform(true);
        modifyFrame.setVisible(true);
        modifyFrame.setResizable(false);

        confermaButton.addActionListener(e -> {
        	String nome = nomeField.getText().trim();
            String cognome = cognomeField.getText().trim();
            String email = emailField.getText().trim();
            String telefono = telefonoField.getText().trim();
            if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Tutti i campi devono essere compilati.", "Errore", JOptionPane.ERROR_MESSAGE);
            } else {
                contatto.setEmail(email);
                contatto.setTelefono(telefono);
                model.set(index, contatto); 
                modifyFrame.dispose();
            }
        });

        annullaButton.addActionListener(e -> modifyFrame.dispose()); 
    }

    
    public void createFrame() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                JFrame frame = new JFrame("Inserimento Dati");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(6, 2, 10, 10));

                JLabel nomeLabel = new JLabel("Nome:");
                JLabel cognomeLabel = new JLabel("Cognome:");
                JLabel emailLabel = new JLabel("Email:");
                JLabel telefonoLabel = new JLabel("Telefono:");

                JTextField nomeField = new JTextField();
                JTextField cognomeField = new JTextField();
                JTextField emailField = new JTextField();
                JTextField telefonoField = new JTextField();

                JButton confermaButton = new JButton("Conferma");
                JButton annullaButton = new JButton("Annulla");

                panel.add(nomeLabel);
                panel.add(nomeField);
                panel.add(cognomeLabel);
                panel.add(cognomeField);
                panel.add(emailLabel);
                panel.add(emailField);
                panel.add(telefonoLabel);
                panel.add(telefonoField);
                panel.add(confermaButton);
                panel.add(annullaButton);

                frame.getContentPane().add(BorderLayout.CENTER, panel);
                frame.pack();
                frame.setLocationByPlatform(true);
                frame.setVisible(true);
                frame.setResizable(false);

                confermaButton.addActionListener(e -> {
                    String nome = nomeField.getText().trim();
                    String cognome = cognomeField.getText().trim();
                    String email = emailField.getText().trim();
                    String telefono = telefonoField.getText().trim();

                    if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Tutti i campi devono essere compilati.", "Errore", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    boolean numeroEsiste = false;
                    for (int i = 0; i < model.getSize(); i++) {
                        if (model.getElementAt(i).getTelefono().equals(telefono)) {
                            numeroEsiste = true;
                            break;
                        }
                    }

                    if (numeroEsiste) {
                        JOptionPane.showMessageDialog(frame, "Il numero di telefono esiste già.", "Errore", JOptionPane.ERROR_MESSAGE);
                    } else {
                        Contatto contatto = new Contatto(nome, cognome, email, telefono);
                        model.addElement(contatto);
                        frame.dispose();
                    }
                });

                annullaButton.addActionListener(e -> {
                    frame.dispose();
                });
            }
        });
    }
    
    private void salvaContatti() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Scegli dove salvare il file");
        int userSelection = fileChooser.showSaveDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                List<Contatto> contatti = new ArrayList<>();
                for (int i = 0; i < model.size(); i++) {
                    contatti.add(model.getElementAt(i));
                }
                out.writeObject(contatti);
                JOptionPane.showMessageDialog(frame, "Contatti salvati con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Errore nel salvataggio!", "Errore", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void importaContatti() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleziona il file da importare");
        int userSelection = fileChooser.showOpenDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                List<Contatto> contatti = (List<Contatto>) in.readObject();
                model.clear();
                for (Contatto c : contatti) {
                    model.addElement(c);
                }
                JOptionPane.showMessageDialog(frame, "Contatti importati con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(frame, "Errore durante l'importazione!", "Errore", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}

