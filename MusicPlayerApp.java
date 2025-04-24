import javazoom.jl.player.Player;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class MusicPlayerApp {
    private JFrame frame;
    private JButton addButton, playButton, nextButton;
    private JList<String> songList;
    private DefaultListModel<String> listModel;
    private ArrayList<File> playlist;
    private int currentIndex = 0;
    private Player player;
    private Thread playerThread;

    public MusicPlayerApp() {
        frame = new JFrame("Java Music Player 🎵");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        songList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(songList);

        addButton = new JButton("Add Songs");
        playButton = new JButton("Play");
        nextButton = new JButton("Next");

        JPanel controlPanel = new JPanel();
        controlPanel.add(addButton);
        controlPanel.add(playButton);
        controlPanel.add(nextButton);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        playlist = new ArrayList<>();

        addButton.addActionListener(e -> chooseSongs());
        playButton.addActionListener(e -> playSelected());
        nextButton.addActionListener(e -> playNext());

        frame.setVisible(true);
    }

    private void chooseSongs() {
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = folderChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = folderChooser.getSelectedFile();
            File[] files = selectedFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));

            if (files != null && files.length > 0) {
                for (File file : files) {
                    playlist.add(file);
                    listModel.addElement(file.getName());
                }
            } else {
                JOptionPane.showMessageDialog(frame, "No MP3 files found in selected folder.");
            }
        }
    }

    private void playSelected() {
        int index = songList.getSelectedIndex();
        if (index != -1) {
            currentIndex = index;
            playSong(playlist.get(index));
        }
    }

    private void playNext() {
        stopPlayer();
        currentIndex++;
        if (currentIndex < playlist.size()) {
            songList.setSelectedIndex(currentIndex);
            playSong(playlist.get(currentIndex));
        } else {
            JOptionPane.showMessageDialog(frame, "End of playlist.");
        }
    }

    private void playSong(File file) {
        stopPlayer();
        playerThread = new Thread(() -> {
            try {
                FileInputStream fis = new FileInputStream(file);
                player = new Player(fis);
                player.play();
                // Auto-play next after current song ends
                SwingUtilities.invokeLater(() -> playNext());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Error playing file: " + e.getMessage());
            }
        });
        playerThread.start();
    }

    private void stopPlayer() {
        if (player != null) {
            player.close();
        }
        if (playerThread != null && playerThread.isAlive()) {
            playerThread.interrupt();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MusicPlayerApp::new);
    }
}