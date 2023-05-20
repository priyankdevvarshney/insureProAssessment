package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.*;


public class Main {
    public static void main(String[] args) {
        // Data structure to store the sales data
        //  u have to add the filepath to read that file from your local
        // the filepath before running this code
        List<Sale> salesData = readSalesDataFromFile("/Users/priyank/Desktop/data.txt");

        // Generate the reports
        // for each part of question method is created  and on the console output will print accordingly
        generateTotalSalesReport(salesData);
        System.out.println();
        generateMonthlySalesReport(salesData);
        System.out.println();
        generatePopularItemReport(salesData);
        System.out.println();
        generateRevenueReport(salesData);
        System.out.println();
        generatePopularItemOrderStats(salesData);
        System.out.println();

    }



    private static List<Sale> readSalesDataFromFile(String filePath) {
        List<Sale> salesData = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true; // Skip the header line

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] fields = line.split(",");
                String date = fields[0];
                String sku = fields[1];
                double unitPrice = Double.parseDouble(fields[2]);
                int quantity = Integer.parseInt(fields[3]);
                double totalPrice = Double.parseDouble(fields[4]);

                Sale sale = new Sale(date, sku, unitPrice, quantity, totalPrice);
                salesData.add(sale);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return salesData;
    }


    // Report: Total sales of the store
    private static void generateTotalSalesReport(List<Sale> salesData) {
        double totalSales = 0;
        for (Sale sale : salesData) {
            totalSales += sale.getTotalPrice();
        }
        System.out.println("Total sales of the store: $" + totalSales);
    }

    // Report: Month-wise sales totals
    private static void generateMonthlySalesReport(List<Sale> salesData) {
        Map<String, Double> monthlySalesMap = new HashMap<>();

        for (Sale sale : salesData) {
            String month = sale.getDate().substring(0, 7);
            double salesTotal = monthlySalesMap.getOrDefault(month, 0.0);
            salesTotal += sale.getTotalPrice();
            monthlySalesMap.put(month, salesTotal);
        }

        System.out.println("Month-wise sales totals:");
        for (Map.Entry<String, Double> entry : monthlySalesMap.entrySet()) {
            String month = entry.getKey();
            double salesTotal = entry.getValue();
            System.out.println(month + ": $" + salesTotal);
        }
    }

    // Report: Most popular item (most quantity sold) in each month
    private static void generatePopularItemReport(List<Sale> salesData) {
        Map<String, Map<String, Integer>> popularItemMap = new HashMap<>();

        for (Sale sale : salesData) {
            String month = sale.getDate().substring(0, 7);
            Map<String, Integer> monthItemsMap = popularItemMap.getOrDefault(month, new HashMap<>());
            int itemQuantity = monthItemsMap.getOrDefault(sale.getSku(), 0);
            itemQuantity += sale.getQuantity();
            monthItemsMap.put(sale.getSku(), itemQuantity);
            popularItemMap.put(month, monthItemsMap);
        }

        System.out.println("Most popular item (most quantity sold) in each month:");
        for (Map.Entry<String, Map<String, Integer>> entry : popularItemMap.entrySet()) {
            String month = entry.getKey();
            Map<String, Integer> monthItemsMap = entry.getValue();
            int maxQuantity = 0;
            String popularItem = "";

            for (Map.Entry<String, Integer> itemEntry : monthItemsMap.entrySet()) {
                String sku = itemEntry.getKey();
                int quantity = itemEntry.getValue();

                if (quantity > maxQuantity) {
                    maxQuantity = quantity;
                    popularItem = sku;
                }
            }

            System.out.println(month + ": " + popularItem + " (Quantity: " + maxQuantity + ")");
        }
    }

    // Report: Items generating most revenue in each month
    private static void generateRevenueReport(List<Sale> salesData) {
        Map<String, Map<String, Double>> revenueMap = new HashMap<>();

        for (Sale sale : salesData) {
            String month = sale.getDate().substring(0, 7);
            Map<String, Double> monthRevenueMap = revenueMap.getOrDefault(month, new HashMap<>());
            double itemRevenue = monthRevenueMap.getOrDefault(sale.getSku(), 0.0);
            itemRevenue += sale.getTotalPrice();
            monthRevenueMap.put(sale.getSku(), itemRevenue);
            revenueMap.put(month, monthRevenueMap);
        }

        System.out.println("Items generating most revenue in each month:");
        for (Map.Entry<String, Map<String, Double>> entry : revenueMap.entrySet()) {
            String month = entry.getKey();
            Map<String, Double> monthRevenueMap = entry.getValue();
            double maxRevenue = 0;
            String revenueItem = "";

            for (Map.Entry<String, Double> itemEntry : monthRevenueMap.entrySet()) {
                String sku = itemEntry.getKey();
                double revenue = itemEntry.getValue();

                if (revenue > maxRevenue) {
                    maxRevenue = revenue;
                    revenueItem = sku;
                }
            }

            System.out.println(month + ": " + revenueItem + " (Revenue: $" + maxRevenue + ")");
        }
    }

    // Report: Min, max, and average number of orders for the most popular item each month
    private static void generatePopularItemOrderStats(List<Sale> salesData) {
        Map<String, Map<String, List<Integer>>> popularItemOrdersMap = new HashMap<>();

        for (Sale sale : salesData) {
            String month = sale.getDate().substring(0, 7);
            Map<String, List<Integer>> monthOrdersMap = popularItemOrdersMap.getOrDefault(month, new HashMap<>());
            List<Integer> itemOrders = monthOrdersMap.getOrDefault(sale.getSku(), new ArrayList<>());
            itemOrders.add(sale.getQuantity());
            monthOrdersMap.put(sale.getSku(), itemOrders);
            popularItemOrdersMap.put(month, monthOrdersMap);
        }

        System.out.println("Min, max, and average number of orders for the most popular item each month:");
        for (Map.Entry<String, Map<String, List<Integer>>> entry : popularItemOrdersMap.entrySet()) {
            String month = entry.getKey();
            Map<String, List<Integer>> monthOrdersMap = entry.getValue();
            int maxOrders = 0;
            String popularItem = "";

            for (Map.Entry<String, List<Integer>> itemEntry : monthOrdersMap.entrySet()) {
                String sku = itemEntry.getKey();
                List<Integer> orders = itemEntry.getValue();
                int totalOrders = orders.stream().mapToInt(Integer::intValue).sum();

                if (totalOrders > maxOrders) {
                    maxOrders = totalOrders;
                    popularItem = sku;
                }
            }

            List<Integer> itemOrders = monthOrdersMap.get(popularItem);
            int minOrders = Collections.min(itemOrders);
            int maxOrdersForMonth = Collections.max(itemOrders);
            double averageOrders = itemOrders.stream().mapToInt(Integer::intValue).average().orElse(0);

            System.out.println(month + ": " + popularItem);
            System.out.println("   Min Orders: " + minOrders);
            System.out.println("   Max Orders: " + maxOrdersForMonth);
            System.out.println("   Average Orders: " + averageOrders);
        }
    }

    // Sale class to represent each sale entry
    private static class Sale {
        private String date;
        private String sku;
        private double unitPrice;
        private int quantity;
        private double totalPrice;

        public Sale(String date, String sku, double unitPrice, int quantity, double totalPrice) {
            this.date = date;
            this.sku = sku;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
            this.totalPrice = totalPrice;
        }

        public String getDate() {
            return date;
        }

        public String getSku() {
            return sku;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getTotalPrice() {
            return totalPrice;
        }
    }
}
