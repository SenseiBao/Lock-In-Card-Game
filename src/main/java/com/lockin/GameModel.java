package com.lockin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class GameModel {
    private Stack<String> deck = new Stack<>();
    private int teamAScore = 0;
    private int teamBScore = 0;

    public GameModel() {
        loadWords();
    }

    private void loadWords() {
        try {
            var stream = getClass().getResourceAsStream("/words.txt");
            if (stream != null) {
                new BufferedReader(new InputStreamReader(stream)).lines().forEach(deck::push);
                Collections.shuffle(deck);
            } else {
                deck.push("NEBULA"); // Fallback
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

    public void addScore(boolean isTeamA) {
        if (isTeamA) teamAScore++;
        else teamBScore++;
    }

    public String getScoreString() {
        return "Team A: " + teamAScore + " | Team B: " + teamBScore;
    }

    public String rollDice() {
        int roll = (int) (Math.random() * 6) + 1;
        if (roll == 6) {
            return "⚠️ ROLL 6: " + getPowerUp();
        }
        return "STANDARD ROUND (Roll: " + roll + ")";
    }

    public void resetGame() {
        deck.clear();
        teamAScore = 0;
        teamBScore = 0;
        loadWords(); // Re-reads the file or fallback
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
}