package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;

public class Main{
    public static <T> Set<T> ArrayToSet(T[] array){
        Set<T> set = new HashSet<>();
        Collections.addAll(set, array);

        return set;
    }
    public static double magnitudes(Vector<Double>a1){
        double magnitude = 0;
        for(double x: a1){
            magnitude = magnitude + x*x;
        }
        return Math.sqrt(magnitude);
    }

    public static double dotProduct(Vector<Double>a1, Vector<Double>a2, int initial){
        double scalar;
        if(initial == a1.size() -1 ){
            scalar = (a1.get(initial) * a2.get(initial));
        }else {
            scalar = (a1.get(initial) * a2.get(initial)) + (dotProduct(a1, a2, initial + 1));

        }
        return scalar;
    }
    public static double CosineSimilarities(customHashMap<String, Double> testFile, customHashMap<String, Double> userInput){
        Vector<Double> testF = new Vector<>(testFile.getLength() + userInput.getLength());
        Vector<Double> userF = new Vector<>(testFile.getLength() + userInput.getLength());
        double divider;
        for(Entry<String, Double> epoch : testFile.getTable()){
            if(epoch != null){
                testF.addElement(epoch.getValue());
                if(userInput.contains(epoch.getKey())){
                    userF.addElement(userInput.get(epoch.getKey()));
                }else{
                    userF.addElement(0.0);
                }
            }
        }

        for(Entry<String, Double> epoch : userInput.getTable()){
            if(epoch != null){
                if(!testFile.contains(epoch.getKey())){
                    testF.addElement(0.0);
                }
                if(!testFile.contains(epoch.getKey()))
                    userF.addElement(epoch.getValue());
            }
        }
        System.out.println(testF);
        System.out.println(userF);
        divider = dotProduct(testF, userF, 0);


        return divider/(magnitudes(testF) * magnitudes(userF));

    }
    public static customHashMap<String, Integer> getTermFrequency(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Element elem = doc.body();
        String CorpusBody = elem.text();
        String[] wordList = CorpusBody.split("[\s.{3},:!?]");
        customHashMap<String, Integer> countIndex = new customHashMap<>(wordList.length);
        for(String word : wordList){
            if(!countIndex.contains(word.toLowerCase())){
                countIndex.put(word.toLowerCase(), 1);
            }else{
                countIndex.put(word.toLowerCase(), countIndex.get(word.toLowerCase())+1);
            }
        }
        return countIndex;
    }

    public static customHashMap<String, Integer> Df(String[] urls) throws IOException{
        customHashMap<String, Integer> countIndex = new customHashMap<>(20000);
        for(String url : urls){
            Document doc = Jsoup.connect(url).get();
            Element elem = doc.body();
            String corpusBody = elem.text().toLowerCase();
            String[] wordList = corpusBody.split("[\s.{3},:!?]");
            Set<String> CheckedList = ArrayToSet(wordList);
            System.out.println(CheckedList);
            for(String word : CheckedList){
                if(!countIndex.contains(word.toLowerCase())){
                    countIndex.put(word.toLowerCase(), 1);
                }else {
                    countIndex.put(word.toLowerCase(), countIndex.get(word.toLowerCase())+1);
                }
            }
        }

        return countIndex;

    }

    public static customHashMap<String, Double> tf_idfs(customHashMap<String, Integer> tf, customHashMap<String, Integer> df, int x, int length){
        double N = x;
        customHashMap<String, Double> FinalValue = new customHashMap<>(length);
        //tf is the hashmap for term frequency, and df is the dictionary.
        String[] wordList = tf.toString().split("\n");
        for(String word :wordList){
            String[] sub = word.split("->");
            //Scroll through the key words of tf, then find it in df to get the value
            if(sub[0] != null && df.get(sub[0]) != null && tf.get(sub[0]) != null){
                //calculate the InverseFrequency
                double F = N/df.get(sub[0]);
                double IF = Math.log10(F);
                double tf_idf = IF * tf.get(sub[0]);
                FinalValue.put(sub[0], tf_idf);
            }
        }
        tf.tf_idf = FinalValue;
        return FinalValue;
    }
    public static void main(String[] args) throws IOException {

        //Initial variables declarations
        int width = 700;
        int height = 500;
        final String[] webList = {null};
        final String[] UserInput = new String[1];

        //Instances Creations
        JFrame f=new JFrame();
         JTextArea editor = new JTextArea(5, 40);
         JTextArea userInput = new JTextArea(5, 40);
        JButton submit = new JButton("Submit");


        //Function calling
        editor.setBounds(50, height/2 - (400/2) , 600, 200);
        userInput.setBounds(50, height/2 + 50, 600, 30);
        submit.setBounds(width/2 - 50, height/2 + 120, 100, 30);

        //Adding each component to JFrame
        f.add(editor);
        f.add(userInput);
        f.add(submit);
        JLabel results = new JLabel();
        results.setBounds(200, 200, 40, 40);
        f.add(results);
        //Window setting
        f.getContentPane().setBackground(Color.GRAY);
        f.setSize(width,height);//400 width and 500 height
        f.setLayout(null);//using no layout managers
        f.setVisible(true);//making the frame visible

        while(webList[0] == null || webList[0].length() == 0 && UserInput[0] == null || UserInput[0].length() == 0) {
            submit.addActionListener(e -> {
                webList[0] = editor.getText() +"\n"+ userInput.getText();
                UserInput[0] = userInput.getText();
            });
        }
        String[] inputs = webList[0].split("\n");
        System.out.println(Arrays.toString(inputs));

        ArrayList<customHashMap<String, Integer>> TermFrequency = new ArrayList<>();
        customHashMap<String, Double>[] finalVectors = new customHashMap[inputs.length];
        for (String x : inputs) {
            TermFrequency.add(getTermFrequency(x));
        }

        TermFrequency.add(getTermFrequency(UserInput[0]));
        customHashMap<String, Integer> TermDocs = Df(inputs);



        for(int x = 0; x < inputs.length; x++){
            finalVectors[x] = tf_idfs(TermFrequency.get(x), TermDocs, inputs.length, TermFrequency.get(x).getLength());
        }

        double[] result = new double[finalVectors.length -1];

        for(int x = 0; x < finalVectors.length-1; x++){
            result[x] = CosineSimilarities(finalVectors[x], finalVectors[finalVectors.length - 1]);
        }

        double max = 0;
        String finals;
        int index = 0;

        for(int x =0; x < result.length; x++){
            if(result[x]>max){
                max = result[x];
                index = x;
            }
        }

        System.out.println(max);finals = "the link in row "+ index +  " is the most similar";


        results.setText(finals);
        System.out.println(finals);

        JFrame small = new JFrame("result");
        JLabel F = new JLabel(finals);
        F.setBounds(width/4-40, height/6, 300, 100);
        small.add(F);
        small.getContentPane().setBackground(Color.GRAY);
        small.setSize(width-200,height-200);//400 width and 500 height
        small.setLayout(null);//using no layout managers
        small.setVisible(true);//making the frame visible





    }

    }


