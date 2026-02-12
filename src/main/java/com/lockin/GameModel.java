package com.lockin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameModel {
    private List<String> deck = new ArrayList<>();
    private int teamAScore = 0;
    private int teamBScore = 0;

    public GameModel() {
        loadWords();
    }

    private void loadWords() {
        try {
            var stream = getClass().getResourceAsStream("/words.txt");
            if (stream != null) {
                new BufferedReader(new InputStreamReader(stream)).lines().forEach(deck::add);
                Collections.shuffle(deck);
            } else {
                deck.add("NO WORDS FOUND");
            }
        } catch (Exception e) {
            deck.add("ERROR LOADING");
        }
    }

    public String drawWord() {
        if (deck.isEmpty()) return "GAME OVER";
        return deck.remove(0);
    }

    public void addScore(boolean isTeamA) {
        if (isTeamA) teamAScore++;
        else teamBScore++;
    }

    public String getScoreString() {
        return "Team A: " + teamAScore + "  |  Team B: " + teamBScore;
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
            default -> "ERROR";
        };
    }
}