package com.Backend;

import java.math.BigDecimal;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


public class HistoryService {
    private static final String DB_NAME = "CurrencyConverter";
    private static final String COLLECTION_NAME = "History";
    
    public static void saveConversion(String from, String to, BigDecimal amount, BigDecimal result) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase database = mongoClient.getDatabase(DB_NAME);
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
            
            Document doc = new Document()
                .append("from", from)
                .append("to", to)
                .append("amount", amount.toString())
                .append("result", result.toString())
                .append("timestamp", new java.util.Date());
                
            collection.insertOne(doc);
        } catch (Exception e) {
            System.err.println("Error saving to MongoDB: " + e.getMessage());
        }
    }
}