/*
* @Author: Krishna
* @Date:   2016-05-26 01:53:12
* @Last Modified by:   Krishna
* @Last Modified time: 2016-05-26 14:33:00
*/
import java.util.*;
import java.io.*;
public class VigenereCracker {
	private String cipher;
    private static double[] frequencies = { 8.167, 1.492, 2.782, 4.253, 12.702, 2.228,
            2.015, 6.094, 6.966, 0.153, 0.772, 4.025, 2.406, 6.749, 7.507,
            1.929, 0.095, 5.987, 6.327, 9.056, 2.758, 0.978, 2.360, 0.150,
            1.974, 0.074 };
    public VigenereCracker(String cipher) {
    	this.cipher = cipher;
    }
    public String decrypt(String key) {
        if(key == null) {
            key = crackKey();
            System.out.println("Guessed Key by Frequency Analysis: " + key);
        }
        StringBuilder message = new StringBuilder();
        for(int i = 0; i < this.cipher.length(); i++) {
            int k = key.charAt(i % key.length());
            int c = this.cipher.charAt(i);
            int m = c - k;
            if(m < 0) {
                m += 26;
            }
            m = 65 + (m % 26);
            message.append((char)m);
        }
        return message.toString();
    }
    public String crackKey() {
        ArrayList<String> caesars = generateCaesarCiphers();
        StringBuilder guessedKey = new StringBuilder();
        for(int i = 0; i < caesars.size(); i++) {
            String current = caesars.get(i);
            char best = 'A';
            double bestScore = Double.MAX_VALUE;
            for(int j = 0; j < 26; j++) {
                String shifted = shift(current, j);
                double score = calcScore(shifted);
                if(score < bestScore) {
                    bestScore = score;
                    best = (char)(65 + j);
                }
            }
            guessedKey.append(best);
        }
        return guessedKey.toString();
    }
    public String shift(String input, int shift) {
        StringBuilder op = new StringBuilder();
        for(int i = 0;  i < input.length(); i++) {
            int ascii = input.charAt(i) - 65 - shift;
            if(ascii < 0) {
                ascii += 26;
            }
            ascii = 65 + (ascii % 26);
            op.append((char)ascii);
        }
        return op.toString();
    }
    private double calcScore(String cipher) {
        int[] vals = new int[26];
        double score = 0.0;
        for(int i = 0; i < cipher.length(); i++) {
            vals[cipher.charAt(i) - 'A']++;
        }
        for(int i = 0; i < 26; i++) {
            double gFrequency = vals[i] * 100.0 / cipher.length();
            double eFrequency = frequencies[i];
            score += Math.pow(gFrequency - eFrequency, 2);
        }
        return score;
    }
    private ArrayList<String> generateCaesarCiphers() {
        int keyLength = guessKeyLength();
        ArrayList<String> caesars = new ArrayList<>(keyLength);
        String bigCipher = this.cipher;
        for(int i = 0; i < keyLength; i++) {
            StringBuilder sb = new StringBuilder();
            for(int j = i; j < bigCipher.length(); j += keyLength) {
                sb.append(bigCipher.charAt(j));
            }
            caesars.add(sb.toString());
        }
        return caesars;
    }
    private int guessKeyLength() {
    	ArrayList<Integer> distances = getFrequentDistances(3);
    	HashMap<Integer, Integer> factors = new HashMap<>();
    	for(int i = 0; i < distances.size(); i++) {
    		int x = distances.get(i);
    		for(int j = 4; j <= Math.sqrt(x); j++) {
    			if( x % j == 0 ) {
    				if(factors.get(j) == null) {
    					factors.put(j, 1);
    				} else {
    					factors.put(j, factors.get(j) + 1);
    				}
                    int other = x / j;
                    if(other != j) {
                        if(factors.get(other) == null) {
                            factors.put(other, 1);
                        } else {
                            factors.put(other, factors.get(other) + 1);
                        }
                    }
                    
    			}
    		}
    	}
        Map<Integer, Integer> sorted = MapUtil.sortByValue(factors);
        int bestKey = -1;
        for(Map.Entry<Integer, Integer> entry : sorted.entrySet()) {
            bestKey = entry.getKey();
            System.out.println("Most probable key length: " + entry.getKey());
            break;
        }
    	return bestKey;
    }
    private ArrayList<Integer> getFrequentDistances(int wordLength) {
    	ArrayList<Integer> vals = new ArrayList<Integer>();
    	HashMap<String, Pair> distances = new HashMap<>();
    	for(int i = 0; i < this.cipher.length() - wordLength; i++) {
    		String cur = this.cipher.substring(i, i + wordLength + 1);
    		if(distances.get(cur) == null) {
    			distances.put(cur, new Pair(i, false));
    		} else {
    			distances.put(cur, new Pair(i - distances.get(cur).getFirst(), true));
    		}
    	}
    	for(Map.Entry<String, Pair> entry : distances.entrySet()) {
    		if(entry.getValue().getSecond()) {
    			vals.add(entry.getValue().getFirst());
    		}
    	}
    	return vals;
    }
    public static void main(String[] args) {
        String inFile = "";
        String outFile = "";
        String key = "";
        try {
            if(args[0].equals("--inFile")) {
                inFile = args[1];
            } else if(args[0].equals("--outFile")) {
                outFile = args[1];
            } else {
                throw new IllegalArgumentException();
            }
            if(args[2].equals("--outFile")) {
                outFile = args[3];
            } else if(args[2].equals("--inFile")) {
                inFile = args[3];
            } else {
                throw new IllegalArgumentException();
            }
            BufferedReader br = new BufferedReader(new FileReader(new File(inFile)));
            String cipher = br.readLine();
            String message = new VigenereCracker(cipher).decrypt(null);
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(outFile)));
            dos.writeBytes(message);
            dos.flush();
            System.out.println("Output saved to " + outFile);
        } catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Usage:> java VigenereCracker --inFile [input_file] --outFile [output_file]");
        } catch(IOException io) {
            System.err.println("File Not Found");
        }
        
        
    }
}