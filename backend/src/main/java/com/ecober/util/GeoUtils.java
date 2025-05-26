package com.ecober.util;

import com.ecober.adapter.Dto.DistanceDurationDTO;

import java.util.*;

public class GeoUtils {

    public static class Edge {
        public final int to;
        public final double weight;

        public Edge(int to, double weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    public static class Node implements Comparable<Node> {
        public final int id;
        public final double distance;

        public Node(int id, double distance) {
            this.id = id;
            this.distance = distance;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.distance, other.distance);
        }
    }

    /**
     * @param graph A map of node IDs to a list of edges
     * @param start Starting node ID
     * @return Map of node IDs to their shortest distance from the start
     */
    public static Map<Integer, Double> dijkstra(Map<Integer, List<Edge>> graph, int start) {
        Map<Integer, Double> distances = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>();

        for (int node : graph.keySet()) {
            distances.put(node, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);
        pq.add(new Node(start, 0.0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            double currDist = current.distance;

            for (Edge edge : graph.get(current.id)) {
                double newDist = currDist + edge.weight;
                if (newDist < distances.get(edge.to)) {
                    distances.put(edge.to, newDist);
                    pq.add(new Node(edge.to, newDist));
                }
            }
        }

        return distances;
    }

    
    public static double calculateEmissions(double distanceKm,String vehicleType)
    {
        double factor=switch(vehicleType.toUpperCase())
        {
            case "EV" ->0.05;
            case "BIKE" ->0.08;
            case "SUV" ->0.25;
            case "SEDAN" ->0.21;
            default ->0.21;
        };
        return distanceKm*factor;
    }

    public static DistanceDurationDTO haversinDistanceandDuration(double lat1, double lon1, double lat2, double lon2) {
    final int EARTH_RADIUS = 6371;
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);

    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
             + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
             * Math.sin(dLon / 2) * Math.sin(dLon / 2);

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double distance = Math.round(EARTH_RADIUS * c * 100.0) / 100.0;

    double avgSpeedKmph = 25.0;
    double timeInHours = distance / avgSpeedKmph;
    long timeInMins = Math.round(timeInHours * 60);
    int timeInSecs = (int) (timeInHours * 3600);

    return DistanceDurationDTO.builder()
        .distanceKm(distance)
        .durationInMins(timeInMins)
        .durationInTrafficSecs(timeInSecs)
        .build();
}
    
}
