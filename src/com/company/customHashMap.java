package com.company;

public class customHashMap<K, V> {
    private int capacity = 16;
    private Entry<K, V>[] table;
    public customHashMap<String, Double> tf_idf;

    public customHashMap(){
        table = new Entry[capacity];
    }

    public customHashMap(int capacity){
        this.capacity = capacity;
        table = new Entry[capacity];
    }

    public void put(K key, V value){
        int index = index(key);
        Entry newEntry = new Entry(key, value, null);
        if(table[index] == null){
            table[index] = newEntry;
        }else {
            Entry<K, V>previousPair = null;
            Entry<K, V>currentPair = table[index];
            while (currentPair != null){
                if(currentPair.getKey().equals(key)){
                    currentPair.setValue(value);
                    break;
                }
                previousPair = currentPair;
                currentPair = currentPair.getNext();
            }
            if(previousPair != null){
                previousPair.setNext(newEntry);
            }
        }
    }

    public V get(K key){
        V value = null;
        int index = index(key);
        Entry<K, V>entry = table[index];
        while(entry != null){
            if(entry.getKey().equals(key)){
                value = entry.getValue();
                break;
            }
            entry = entry.getNext();
        }
        return value;
    }

    public String toString(){
        String List = "";
        for(Entry<K, V>currentpair : table){
            if(currentpair != null){
                List = List + currentpair.getKey() + "-> " + currentpair.getValue()+"\n";
            }
        }
        return List;
    }
    public Boolean contains(K key){
        for(Entry<K, V>currentPair : table){
            if(currentPair != null && currentPair.getKey().equals(key)){
                return true;
            }
        }
        return false;
    }

    public int getLength(){
        int count = 0;
        for(Entry<K, V>currentpair: table){
            if(currentpair != null){
                count += 1;
            }
        }
        return count;
    }

    public Entry<K, V>[] getTable(){
        return this.table;
    }

    private int index(K key){
        if (key == null){
            return 0;
        }
        return Math.abs(key.hashCode() % capacity);
    }
}
