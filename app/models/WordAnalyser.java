package models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

public class WordAnalyser {
    private static final WordAnalyser _instance = new WordAnalyser();
    private final HashMap<String, Double> _dict;

    /**
     * Changed few parts but mainly is from the git repository
     * it reads the SentiWordNet_3.0.0.txt file
     * then it makes a HashMap out of it and stores the word and its score
     *
     * @author SentiWordNet by aesuli
     */

    private WordAnalyser() {
        _dict = new HashMap<>();
        HashMap<String, Vector<Double>> _temp = new HashMap<>();

        BufferedReader csv;
        try {
            csv = new BufferedReader(new FileReader("public/resources/SentiWordNet_3.0.0.txt"));

            String line = csv.readLine();
            while (line != null) {
                String[] data = line.split("\t");
                if (data.length > 1) {
                    if (!data[2].equals("PosScore") && !data[2].isEmpty()) {
                        Double score = Double.parseDouble(data[2]) - Double.parseDouble(data[3]);
                        String[] words = data[4].split(" ");
                        for (String w : words) {
                            String[] w_n = w.split("#");
                            w_n[0] += "#" + data[0];
                            int index = Integer.parseInt(w_n[1]) - 1;
                            if (_temp.containsKey(w_n[0])) {
                                Vector<Double> v = _temp.get(w_n[0]);
                                if (index > v.size()) {
                                    for (int i = v.size(); i < index; i++) {
                                        v.add(0.0);
                                    }
                                }
                                v.add(index, score);
                                _temp.put(w_n[0], v);
                            } else {
                                Vector<Double> v = new Vector<>();
                                for (int i = 0; i < index; i++) {
                                    v.add(0.0);
                                }
                                v.add(index, score);
                                _temp.put(w_n[0], v);
                            }
                        }
                    }
                }
                line = csv.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (String word : _temp.keySet()) {
            Vector<Double> v = _temp.get(word);
            double score = 0.0;
            double sum = 0.0;
            for (int i = 0; i < v.size(); i++) {
                score += (1D / (double) (i + 1)) * v.get(i);
            }
            for (int i = 1; i <= v.size(); i++) {
                sum += 1D / (double) i;
            }
            score /= sum;
            _dict.put(word, score);
        }
    }

    public static WordAnalyser getInstance() {
        return _instance;
    }

    /**
     * uses the word that is passed to it and return the total score of that word
     *
     * @param word gets a single word to find its score
     * @return the number from the file in Double
     * @author Jananee Aruboribaran
     */
    public double get(String word) {

        double total = 0.0;

        if (_dict.get(word + "#n") != null) {
            total += _dict.get(word + "#n");
        }
        if (_dict.get(word + "#a") != null) {
            total += _dict.get(word + "#a");
        }
        if (_dict.get(word + "#v") != null) {
            total += _dict.get(word + "#v");
        }

        return total;
    }
}
