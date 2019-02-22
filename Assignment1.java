import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

class Assignment1 {
    public static void main(String[] args) {
        // part2a h(k) = k mod m
        System.out.println("----- part2a -----\n");
        part2(800000, 1);
        part2(1000000, 1);
        part2(2000000, 1);
        part2(3000000, 1);

        // part2b h(k) = f(k, m) = ⌊m(kA mod 1)⌋
        System.out.println("----- part2b -----\n");
        part2(800000, 2);
        part2(1000000, 2);
        part2(2000000, 2);
        part2(3000000, 2);

        // part3a linear probing
        System.out.println("----- part3a linear probing -----\n");
        part3(500000, 1);
        part3(800000, 1);
        part3(1000000, 1);
        part3(1048575, 1);

        // part3b quadratic probing
        System.out.println("----- part3b quadratic probing -----\n");
        part3(500000, 2);
        part3(800000, 2);
        part3(1000000, 2);
        part3(1048575, 2);

        // part3c double hashing
        System.out.println("----- part3c double hashing -----\n");
        part3(500000, 3);
        part3(800000, 3);
        part3(1000000, 3);
        part3(1048575, 3);
    }

    // LCG pseudo random nubmer generator
    public static long pseudoRandom(long seed) {
        long a = 16807;
        long M = 2147483647;
        long xn = seed;
        long xn1 = (a * xn) % M;
        return xn1;
    }

    public static double calculateMean(int[] myArray) {
        double sum = 0.0;
        for (int i : myArray) {
            sum += i;
        }
        double mean = sum / myArray.length;
        return mean;
    }

    public static double calculateStandardDeviation(int[] myArray) {
        double mean = calculateMean(myArray);
        double sum = 0;
        for (int i : myArray) {
            double j = i - mean;
            double k = j * j;
            sum += k;
        }
        double deviation = Math.sqrt(sum / (myArray.length - 1));
        return deviation;
    }

    // hash function h(k, m)
    public static long h(long k, int m) {
        return k % m;
    }

    // hash function f(k, m)
    public static long f(long k, int m) {
        double A = (Math.sqrt(5) - 1) / 2;
        return (long) Math.floor(m * ((k * A) % 1));
    }

    // part2 calculations
    public static void part2(int n, int exp) {
        int m = 1000003;
        long seed = 98760053;
        int[] hashTable = new int[m];
        Arrays.fill(hashTable, 0);

        for (int i = 0; i < n; i++) {
            long keyValue = pseudoRandom(seed);
            int bucket = 0;
            switch (exp) {
                case 1:
                    // for part2a use h(k) = k mod m
                    bucket = (int) h(keyValue, m);
                    break;
                case 2:
                    // for part2b use h(k) = f(k, m) = ⌊m(kA mod 1)⌋
                    bucket = (int) f(keyValue, m);
                    break;
            }
            hashTable[bucket] += 1; // insert the hashed value into the hash table array;
            seed = keyValue; // set new seed for the random number generator;
        }

        /*
        map every element in the hash table array into a treemap to
        count the number of buckets and bucket sizes.
        */
        TreeMap<Integer, Integer> buckets = new TreeMap<>();
        for (int i : hashTable) {
            if (buckets.containsKey(i)) {
                int currentBucketCount = buckets.get(i);
                buckets.put(i, currentBucketCount + 1);
            } else {
                buckets.put(i, 1);
            }
        }

        System.out.println("Total # of bucket: " + m);
        System.out.println("# of elements inserted: " + n);
        System.out.println("\nDistribution of bucket sizes:");

        /*
        print each bucket and it's size, which is already sorted by the treemap.
        */
        for (Map.Entry entry : buckets.entrySet()) {
            double b = (double) (int) entry.getValue() / m;
            System.out.println(entry.getKey() + ", " + entry.getValue() + ", " + b);
        }

        double sd = calculateStandardDeviation(hashTable);
        double lf = (double) n / m;
        System.out.println("\nLoad factor: " + lf);
        System.out.println("Standard deviation: " + sd + "\n");
    }

    // linear probing sequence
    public static int linearProbe(int[] ht, long k, int n, int m) {
        int probes = 0;
        long hash1 = f(k, m);
        for (int i = 0; i < n; i++) {
            int probePosition = (int) h(hash1 + i, m);
            if (ht[probePosition] == 0) {
                ht[probePosition] += 1; // if position is empty, insert key value
                probes += i + 1; // count how many probes performed for this insertion of key value
                break;
            } else {
                continue; // try the next position
            }
        }
        return probes;
    }

    // quadratic probe sequence
    public static int quadraticProbe(int[] ht, long k, int n, int m) {
        int probes = 0;
        long hash1 = f(k, m);
        for (int i = 0; i < n; i++) {
            long offset = ((long) i*(i+1))/2;
            int probePosition = (int) h((hash1 + offset), m);
            if (ht[probePosition] == 0) {
                ht[probePosition] += 1; // if position is empty, insert key value
                probes += i + 1; // count how many probes performed for this insertion of key value
                break;
            } else {
                continue; // try the next position
            }
        }
        return probes;
    }

    // double hashing probe sequence
    public static int doubleHashingProbe(int[] ht, long k, int n, int m) {
        int probes = 0;
        long h1 = f(k, m);
        long testHash = h(k, m);
        long h2 = (testHash % 2 != 0) ? testHash : ++testHash;
        for (int i = 0; i < n; i++) {
            int probePosition = (int) h((h1 + i * h2), m);
            if (ht[probePosition] == 0) {
                ht[probePosition] += 1; // if position is empty, insert key value
                probes += i + 1; // count how many probes performed for this insertion of key value
                break;
            } else {
                continue; // try the next position
            }
        }
        return probes;
    }

    // part3 calculations
    public static void part3(int n, int exp) {
        int m = 1048576;
        long seed = 98760053;
        int[] hashTable = new int[m];
        Arrays.fill(hashTable, 0);

        int totalProbes = 0;
        for (int element = 0; element < n; element++) {
            long keyValue = pseudoRandom(seed);
            switch (exp) {
            case 1:
                // for part3a use linear probing sequence
                totalProbes += linearProbe(hashTable, keyValue, n, m);
                break;
            case 2:
                // for part3b use quadratic probing sequence
                totalProbes += quadraticProbe(hashTable, keyValue, n, m);
                break;
            case 3:
                // for part3c use double hashing sequence
                totalProbes += doubleHashingProbe(hashTable, keyValue, n, m);
                break;
            }
            seed = keyValue; // set new seed for the random number generator;
        }

        System.out.println("Array size: " + m);
        System.out.println("# of elements inserted: " + n);
        double lf = (double) n / m;
        System.out.println("Load factor: " + lf);
        double avgProbe = (double) totalProbes / n;
        System.out.println("Average # of probes: " + avgProbe);

        TreeMap<Integer, Integer> clusters = new TreeMap<>();
        TreeMap<Integer, Integer> emptyClusters = new TreeMap<>();
        int totalClusters = 0;
        int sumOfClusterSizes = 0;
        int totalEmptyClusters = 0;
        int sumOfEmptyClusterSizes = 0;

        /*
        find clusters and empty clusters in the hash table array
        by parsing the array linearly, if a cluster if found,
        put it in clusters treemap, if an empty cluster if found,
        put it in the empty cluster treemap, until the end of the
        array is parsed.

        each time a cluster or empty cluster is found, it's "removed"
        from the array by making a new array starting from the index
        after the last value of the cluster to the end of the array.
        */
        int positionMarker = 0;
        while (hashTable.length != 1) {
            if (hashTable[0] != 0) { // when the first value is non zero, it is a cluster
                // find the next zero index, indicating the end of a cluster
                for (int i = 1; i < hashTable.length; i++) {
                    if (hashTable[i] == 0) {
                        positionMarker = i; // mark the first index of zero value
                        // count clusters with a treemap
                        // key = i = length of cluster
                        // value = # of clusters of length i
                        if (clusters.containsKey(i)) {
                            int c = (int) clusters.get(i);
                            clusters.put(i, c + 1);
                        } else {
                            clusters.put(i, 1);
                        }
                        totalClusters++;
                        break;
                    }
                }
            } else { // when the first value is zero, it is an empty cluster
                // find the next non zero index, indicating the end of an empty cluster
                for (int i = 1; i < hashTable.length; i++) {
                    if (hashTable[i] != 0) {
                        positionMarker = i; // mark the first index of non zero value
                        // count empty clusters with a treemap
                        // key = i = length of empty cluster
                        // value = # of empty clusters of length i
                        if (emptyClusters.containsKey(i)) {
                            int c = (int) emptyClusters.get(i);
                            emptyClusters.put(i, c + 1);
                        } else {
                            emptyClusters.put(i, 1);
                        }
                        totalEmptyClusters++;
                        break;
                    }
                }
            }
            // shorten the array from the left by the length of the found cluster/empty cluster
            hashTable = Arrays.copyOfRange(hashTable, positionMarker, hashTable.length);
        }

        // print out the distribution data for clusters
        System.out.println("\nDistribution of clusters:");
        for (Map.Entry entry : clusters.entrySet()) {
            int key = (int) entry.getKey();
            int val = (int) entry.getValue();
            sumOfClusterSizes += key * val;
            double x = (double) val / totalClusters;
            System.out.println(key + ", " + val + ", " + x);
        }
        System.out.println("\nTotal # of clusters: " + totalClusters);
        double avgSize = (double) sumOfClusterSizes / totalClusters;
        System.out.println("Average cluster size: " + avgSize);

        // print out the distribution data for empty clusters
        System.out.println("\nDistribution of empty clusters:");
        for (Map.Entry entry : emptyClusters.entrySet()) {
            int key = (int) entry.getKey();
            int val = (int) entry.getValue();
            sumOfEmptyClusterSizes += key * val;
            double x = (double) val / totalEmptyClusters;
            System.out.println(key + ", " + val + ", " + x);
        }
        System.out.println("\nTotal # of empty clusters: " + totalEmptyClusters);
        double avgEmptySize = (double) sumOfEmptyClusterSizes / totalEmptyClusters;
        System.out.println("Average empty cluster size: " + avgEmptySize + "\n");
    }
}