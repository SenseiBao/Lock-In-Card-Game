package com.lockin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class GameModel {
    private Stack<String> deck = new Stack<>();

    // Changed from just int to Lists so we can show them
    private List<String> teamAWords = new ArrayList<>();
    private List<String> teamBWords = new ArrayList<>();

    public GameModel() {
        loadWords();
    }

    private void loadWords() {
        try {
            var stream = getClass().getResourceAsStream("/words.txt");
            if (stream != null) {
                new BufferedReader(new InputStreamReader(stream))
                        .lines()
                        .map(String::trim)        // Good practice: removes accidental spaces
                        .filter(s -> !s.isEmpty()) // Good practice: ignores empty lines
                        .distinct()               // Removes duplicates!
                        .forEach(deck::push);
                Collections.shuffle(deck);
            } else {
                deck.push("NEBULA");
                deck.push("CHRONOS");
                deck.push("ALGORITHM");
            }
        } catch (Exception e) {
            System.err.println("Error loading words: " + e.getMessage());
        }
    }

    public String drawWord() {
        if (deck.isEmpty()) return "GAME OVER";
        return deck.pop();
    }

    public void recordWin(boolean isTeamA, String word) {
        if (word == null || word.equals("GAME OVER")) return;

        if (isTeamA) {
            teamAWords.add(word);
        } else {
            teamBWords.add(word);
        }
    }

    public int getScore(boolean isTeamA) {
        return isTeamA ? teamAWords.size() : teamBWords.size();
    }

    public List<String> getWords(boolean isTeamA) {
        return isTeamA ? teamAWords : teamBWords;
    }

    public String getScoreString() {
        return "Team A: " + getScore(true) + " | Team B: " + getScore(false);
    }

    public String rollDice() {
        int roll = (int) (Math.random() * 6) + 1;
        if (roll == 6) {
            return "⚠️ ROLL 6: " + getPowerUp();
        }
        return "STANDARD ROUND (Roll: " + roll + ")";
    }

    private String getPowerUp() {
        int r = (int) (Math.random() * 6) + 1;
        return switch (r) {
            case 1 -> "WHITEBOARD CHALLENGE";
            case 2 -> "SIMULTANEOUS CHARADES";
            case 3 -> "LOW BANDWIDTH (1 Syllable)";
            case 4 -> "DATA CORRUPTION";
            case 5 -> "HIGH TRAFFIC";
            case 6 -> "LOOKAHEAD";
            default -> "POWER UP";
        };
    }

    public void resetGame() {
        deck.clear();
        teamAWords.clear();
        teamBWords.clear();
        loadWords();
    }
}