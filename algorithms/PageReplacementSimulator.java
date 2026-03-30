// PageReplacementSimulator.java
package algorithms;

import java.io.*;
import java.util.*;

public class PageReplacementSimulator {
    private List<Algorithm> algorithms;
    private Random random;

    public PageReplacementSimulator() {
        algorithms = new ArrayList<>();
        algorithms.add(new FIFOAlgorithm());
        algorithms.add(new LRUAlgorithm());
        algorithms.add(new OptimalAlgorithm());
        algorithms.add(new ClockAlgorithm());
        algorithms.add(new SecondChanceAlgorithm());
        algorithms.add(new EnhancedSecondChanceAlgorithm());
        algorithms.add(new LFUAlgorithm());
        algorithms.add(new MFUAlgorithm());

        random = new Random();
    }

    // Random generation method
    public SimulationInput generateRandomInput() {
        int length = random.nextInt(31) + 10; // 10-40
        int frameSize = random.nextInt(8) + 3; // 3-10
        int[] referenceString = new int[length];

        for (int i = 0; i < length; i++) {
            referenceString[i] = random.nextInt(21); // 0-20
        }

        return new SimulationInput(referenceString, frameSize);
    }

    // User-defined input validation
    public boolean validateInput(int[] referenceString, int frameSize) {
        if (referenceString.length < 10 || referenceString.length > 40) {
            return false;
        }
        if (frameSize < 3 || frameSize > 10) {
            return false;
        }
        for (int page : referenceString) {
            if (page < 0 || page > 20) {
                return false;
            }
        }
        return true;
    }

    // Read from file
    public SimulationInput readFromFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int[] referenceString = null;
            int frameSize = 0;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Reference String:")) {
                    String[] parts = line.split(":");
                    String[] numbers = parts[1].trim().split("\\s+");
                    referenceString = new int[numbers.length];
                    for (int i = 0; i < numbers.length; i++) {
                        referenceString[i] = Integer.parseInt(numbers[i]);
                    }
                } else if (line.startsWith("Frame Size:")) {
                    String[] parts = line.split(":");
                    frameSize = Integer.parseInt(parts[1].trim());
                }
            }

            return new SimulationInput(referenceString, frameSize);
        }
    }

    // Run all algorithms
    public Map<String, SimulationResult> runAllAlgorithms(int[] referenceString, int frameSize) {
        Map<String, SimulationResult> results = new LinkedHashMap<>();

        for (Algorithm algorithm : algorithms) {
            SimulationResult result = algorithm.simulate(referenceString, frameSize);
            results.put(result.getAlgorithmName(), result);
        }

        return results;
    }

    // Run single algorithm
    public SimulationResult runAlgorithm(String algorithmName, int[] referenceString, int frameSize) {
        for (Algorithm algorithm : algorithms) {
            if (algorithm.getClass().getSimpleName().replace("Algorithm", "").equals(algorithmName)) {
                return algorithm.simulate(referenceString, frameSize);
            }
        }
        return null;
    }

    // Save results to file
    public void saveResultsToFile(Map<String, SimulationResult> results, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Page Replacement Algorithms Simulation Results");
            writer.println("==============================================");
            writer.println();

            for (Map.Entry<String, SimulationResult> entry : results.entrySet()) {
                SimulationResult result = entry.getValue();
                writer.println("Algorithm: " + result.getAlgorithmName());
                writer.println("Page Faults: " + result.getPageFaults());
                writer.println("Page Hits: " + result.getPageHits());
                writer.printf("Hit Ratio: %.2f%%\n", result.getHitRatio());
                writer.printf("Fault Ratio: %.2f%%\n", result.getFaultRatio());
                writer.println("----------------------------------------------");
                writer.println();
            }
        }
    }

    // Inner class for simulation input
    public static class SimulationInput {
        private int[] referenceString;
        private int frameSize;

        public SimulationInput(int[] referenceString, int frameSize) {
            this.referenceString = referenceString;
            this.frameSize = frameSize;
        }

        public int[] getReferenceString() {
            return referenceString;
        }

        public int getFrameSize() {
            return frameSize;
        }
    }
}