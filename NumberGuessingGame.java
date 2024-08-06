import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

public class NumberGuessingGame extends JFrame {
    private JTextField guessField;
    private JLabel messageLabel;
    private JLabel scoreLabel;
    private JLabel highScoreLabel;
    private JButton guessButton;
    private JButton restartButton;
    private JButton startButton;
    private JComboBox<String> rangeComboBox;
    private JComboBox<String> timerComboBox;
    private JEditorPane historyArea;
    private JProgressBar timerProgressBar;
    private int randomNumber;
    private int attemptsLeft;
    private Timer timer;
    private int timeLeft;
    private int range;
    private int timerLimit;
    private int score;
    private int highScore;
    private ArrayList<String> history;
    private boolean lastGameWon;

    public NumberGuessingGame() {
        setTitle("Number Guessing Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel setupPanel = new JPanel();
        setupPanel.setLayout(new GridLayout(4, 1, 10, 10));
        messageLabel = new JLabel("Select difficulty and start the game.", SwingConstants.CENTER);
        guessField = new JTextField();
        timerProgressBar = new JProgressBar(0, 100);
        guessButton = new JButton("Guess");
        restartButton = new JButton("Restart");
        startButton = new JButton("Start Game");
        rangeComboBox = new JComboBox<>(new String[]{"1-100", "1-1000"});
        timerComboBox = new JComboBox<>(new String[]{"30 seconds", "60 seconds", "120 seconds"});
        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        highScoreLabel = new JLabel("High Score: 0", SwingConstants.CENTER);
        historyArea = new JEditorPane();
        historyArea.setContentType("text/html");
        historyArea.setEditable(false);
        historyArea.setPreferredSize(new Dimension(780, 100));
        JScrollPane historyScrollPane = new JScrollPane(historyArea);

        // Configuration
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        guessField.setFont(new Font("Arial", Font.PLAIN, 16));
        timerProgressBar.setFont(new Font("Arial", Font.BOLD, 16));
        guessButton.setFont(new Font("Arial", Font.BOLD, 16));
        restartButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        rangeComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        timerComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        highScoreLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // timer bar style
        timerProgressBar.setForeground(Color.GREEN); // color for the progress bar
        timerProgressBar.setBackground(Color.LIGHT_GRAY); // Background color
        timerProgressBar.setFont(new Font("Arial", Font.BOLD, 30)); // Font for the progress bar timer
        timerProgressBar.setPreferredSize(new Dimension(780, 30)); // Adjust size

        // layout for components
        setupPanel.add(new JLabel("Select Number Range:", SwingConstants.CENTER));
        setupPanel.add(rangeComboBox);
        setupPanel.add(new JLabel("Select Timer Limit:", SwingConstants.CENTER));
        setupPanel.add(timerComboBox);
        setupPanel.add(startButton);
        setupPanel.add(scoreLabel);
        setupPanel.add(highScoreLabel);

        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(4, 1, 10, 10));
        gamePanel.add(messageLabel);
        gamePanel.add(guessField);
        gamePanel.add(guessButton);
        gamePanel.add(timerProgressBar);
        gamePanel.add(restartButton);

        // Add panels to frame
        add(setupPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);
        add(historyScrollPane, BorderLayout.SOUTH);

        // state of components
        guessField.setEnabled(false);
        guessButton.setEnabled(false);
        restartButton.setEnabled(false);

        // history
        history = new ArrayList<>();
        lastGameWon = false;

        // Action listener for Start button
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedRange = (String) rangeComboBox.getSelectedItem();
                if (selectedRange != null) {
                    range = selectedRange.equals("1-100") ? 100 : 1000;
                    attemptsLeft = range == 100 ? 10 : 15;
                }

                String selectedTimer = (String) timerComboBox.getSelectedItem();
                if (selectedTimer != null) {
                    switch (selectedTimer) {
                        case "30 seconds":
                            timerLimit = 30;
                            break;
                        case "60 seconds":
                            timerLimit = 60;
                            break;
                        case "120 seconds":
                            timerLimit = 120;
                            break;
                    }
                }

                startGame();
            }
        });

        // Action listener for Guess button
        guessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleGuess();
            }
        });

        // Action listener for Restart button
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        setVisible(true);
    }

    private void startGame() {
        Random rand = new Random();
        randomNumber = rand.nextInt(range) + 1;
        attemptsLeft = range == 100 ? 10 : 15;
        timeLeft = timerLimit;

        messageLabel.setText("Guess a number between 1 and " + range);
        timerProgressBar.setValue(100);
        timerProgressBar.setStringPainted(true);
        guessField.setEnabled(true);
        guessField.setText("");
        guessButton.setEnabled(true);
        restartButton.setEnabled(false);

        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                int progress = (int) ((double) timeLeft / timerLimit * 100);
                timerProgressBar.setValue(progress);
                timerProgressBar.setString(timeLeft + "s");
                if (timeLeft <= 0) {
                    timer.stop();
                    endGame(false);
                }
            }
        });
        timer.start();
    }

    private void handleGuess() {
        try {
            int guess = Integer.parseInt(guessField.getText());
            attemptsLeft--;
            if (guess == randomNumber) {
                endGame(true);
            } else if (attemptsLeft <= 0) {
                endGame(false);
            } else {
                if (guess < randomNumber) {
                    messageLabel.setText("Too Low! Attempts left: " + attemptsLeft);
                } else {
                    messageLabel.setText("Too High! Attempts left: " + attemptsLeft);
                }
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Please enter a valid number.");
        }
    }
    private void endGame(boolean won) {
        timer.stop();
        if (won) {
            if (lastGameWon) {
                score += attemptsLeft; 
            } else {
                score = attemptsLeft; 
            }
            lastGameWon = true;
            messageLabel.setText("Congratulations! You've guessed the number!");
        } else {
            score = 0; 
            lastGameWon = false;
            messageLabel.setText("Game over! The number was " + randomNumber);
        }
        guessField.setEnabled(false);
        guessButton.setEnabled(false);
        restartButton.setEnabled(true);
        updateHistory(won);
        updateScores();
    }

    private void updateHistory(boolean won) {
        String resultColor = won ? "green" : "red";
        String resultText = won ? "Won" : "Lost";
        String emoji = won ? "🎉" : "💣";
        history.add(String.format("<span style='color:%s;'>%s: The number was %d with %d attempts left. %s</span>",
                resultColor, resultText, randomNumber, attemptsLeft, emoji));
        StringBuilder historyText = new StringBuilder("<html>");
        for (String entry : history) {
            historyText.append(entry).append("<br>");
        }
        historyText.append("</html>");
        historyArea.setText(historyText.toString());
    }

    private void updateScores() {
        if (score > highScore) {
            highScore = score;
            highScoreLabel.setText("High Score: " + highScore);
        }
        scoreLabel.setText("Score: " + score);
    }

    public static void main(String[] args) {
        new NumberGuessingGame();
    }
}
