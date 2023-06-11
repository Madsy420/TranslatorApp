import com.google.cloud.translate.Translate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class TranslatorAppUI extends JFrame {
    private JButton scanButton;
    private JButton stopButton;
    private JButton translateButton;
    private JComboBox translationLanguageCb;
    private JTextField integerField;
    private JTextArea translateTextField;
    private JScrollPane translateTextFieldScroll;
    private MouseScanListener mouseScanListener;
    private Rectangle selectedRegion;
    private String langToTranslate = "en";
    private GoogleCloudUtilities googleCloudUtilitiesInstance;

    private JFrame overlayFrame;
    private JPanel mainPanel;

    public TranslatorAppUI(String googleVisionCredPath, String googleTranslateCredPath) {
        googleCloudUtilitiesInstance = new GoogleCloudUtilities(googleVisionCredPath, googleTranslateCredPath);
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Swing UI Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel(new GridBagLayout());
        add(mainPanel);

        scanButton = new JButton("SCAN");
        stopButton = new JButton("STOP");
        translateButton = new JButton("TRANSLATE");
        translationLanguageCb = new JComboBox();
        integerField = new JTextField();

        int gridX = 0;
        int gridY = 0;
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.weightx = 1;
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(scanButton, gbc);

        gridX++;
        gbc = new GridBagConstraints();

        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.weightx = 1;
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(stopButton, gbc);

        gridX++;
        gbc = new GridBagConstraints();

        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.weightx = 1;
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(translateButton, gbc);

        gridX++;
        gbc = new GridBagConstraints();

        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.weightx = 1;
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(new JLabel("Translate To:"), gbc);

        gridX++;
        gbc = new GridBagConstraints();

        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.weightx = 1;
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(translationLanguageCb, gbc);


        gridY++;
        gridX=0;
        gbc = new GridBagConstraints();

        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.weightx = 1;
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(new JLabel("Translate Count:"), gbc);

        gridX++;
        gbc = new GridBagConstraints();

        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.weightx = 1;
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(integerField, gbc);

        gridX=0;
        gridY++;
        gbc = new GridBagConstraints();

        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.weightx = 1;
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(new JLabel("Translated Text:"), gbc);

        gridX=0;
        gridY++;
        gbc = new GridBagConstraints();

        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.gridwidth = 6;
        gbc.weightx = 3;
        gbc.weighty = 3;
        gbc.insets = new Insets(20,20,20,20);
        gbc.fill = GridBagConstraints.BOTH;
        setTranslateTextField(gbc);
        setTranslateLangCb();

        addButtonListeners();

        // Set up the content pane
        pack();
        setScreenLocation();
        shiftToScanMode();

    }

    private void setScreenLocation()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        int windowWidth = getWidth();
        int windowHeight = getHeight();
        int x = screenWidth - windowWidth;
        int y = (screenHeight - windowHeight) / 2;
        setPreferredSize(new Dimension(600, 400));
        setResizable(false);
        setLocation(x,y);
    }

    private void setTranslateTextField(GridBagConstraints gbc)
    {
        translateTextField = new JTextArea();
        translateTextFieldScroll = new JScrollPane(translateTextField);
        translateTextField.setLineWrap(true);
        translateTextField.setWrapStyleWord(true);
        translateTextField.setEditable(false);
        mainPanel.add(translateTextFieldScroll, gbc);
    }

    private void setTranslateLangCb()
    {
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        for(String lang : LanguageOptionsManager.getAllSupportedLanguage())
        {
            comboBoxModel.addElement(lang);
        }
        translationLanguageCb.setModel(comboBoxModel);
        translationLanguageCb.setSelectedItem(0);
        translationLanguageCb.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    langToTranslate = LanguageOptionsManager.getAbbreviatedLanguage(((JComboBox) e.getSource()).
                            getSelectedItem().toString());
                }
            }
        });
    }


    private void addButtonListeners()
    {
        // Add action listeners to buttons
        scanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startScanning();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shiftToScanMode();
            }
        });

        translateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                takeScreenShotAndPostTranslate();
            }
        });
    }

    private void showOverlayWindow() {
        overlayFrame = new JFrame();
        overlayFrame.setUndecorated(true);
        //overlayFrame.setBackground(new Color(0, 0, 0, 0));
        overlayFrame.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        overlayFrame.setOpacity(0.5f);
        overlayFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        overlayFrame.setAlwaysOnTop(true);
        overlayFrame.setVisible(true);
        SwingUtilities.invokeLater(() -> overlayFrame.requestFocus());
    }

    private void hideOverlayWindow() {
        if (overlayFrame != null) {
            overlayFrame.dispose();
            overlayFrame = null;
        }
    }

    private void takeScreenShotAndPostTranslate()
    {
        BufferedImage bufferedImage = ScreenSnapshotHelper.getScreenSnap(selectedRegion);
        String detectedText = googleCloudUtilitiesInstance.detectWords(bufferedImage);
        String translatedString = googleCloudUtilitiesInstance.translate(detectedText, langToTranslate);
        translateTextField.setText(translatedString);
    }

    private void startScanning() {
        try {
            mouseScanListener = new MouseScanListener();
            Toolkit.getDefaultToolkit().addAWTEventListener(mouseScanListener, AWTEvent.MOUSE_EVENT_MASK);
            showOverlayWindow();
            scanButton.setEnabled(false);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    private void stopScanning() {
        try {
            Toolkit.getDefaultToolkit().removeAWTEventListener(mouseScanListener);
            hideOverlayWindow();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void cancelScanning()
    {
        stopScanning();
        shiftToScanMode();
    }

    private void shiftToTranslateMode() {
        scanButton.setEnabled(false);
        stopButton.setEnabled(true);
        translateButton.setEnabled(true);
        mouseScanListener = null;
    }

    private void shiftToScanMode() {
        selectedRegion = null;
        translateButton.setEnabled(false);
        stopButton.setEnabled(false);
        scanButton.setEnabled(true);
    }

    public class MouseScanListener implements AWTEventListener {
        private Point startPoint;

        public void eventDispatched(AWTEvent event)
        {
            if(event instanceof MouseEvent)
            {
                MouseEvent mouseEvent = (MouseEvent) event;
                if(mouseEvent.getButton() == MouseEvent.BUTTON1)
                {
                    if(mouseEvent.getID() == MouseEvent.MOUSE_PRESSED)
                    {
                        startPoint = mouseEvent.getPoint();
                    }
                    else if(mouseEvent.getID() == MouseEvent.MOUSE_RELEASED)
                    {
                        Point currentPoint = mouseEvent.getLocationOnScreen();

                        // Calculate the rectangle based on the starting and current points
                        int x = Math.min(startPoint.x, currentPoint.x);
                        int y = Math.min(startPoint.y, currentPoint.y);
                        int width = Math.abs(startPoint.x - currentPoint.x);
                        int height = Math.abs(startPoint.y - currentPoint.y);

                        // Create a rectangle representing the dragged area
                        selectedRegion = new Rectangle(x, y, width, height);
                        stopScanning();
                        shiftToTranslateMode();
                    }
                }
                else if(mouseEvent.getID() == MouseEvent.MOUSE_PRESSED)
                {
                    cancelScanning();
                }
            }
        }

    }
}