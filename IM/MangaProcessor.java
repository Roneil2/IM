package Test3;
import java.io.*;
import java.util.*;
import java.util.stream.*;

public class MangaProcessor {

    static class Manga {
        String title;
        String datePublished;
        String chapters;
        String author;

        Manga(String title, String datePublished, String chapters, String author) {
            this.title = title;
            this.datePublished = datePublished;
            this.chapters = chapters;
            this.author = author;
        }

        @Override
        public String toString() {
            return String.format("Title: %s, Date Published: %s, Chapters: %s, Author: %s",
                    title, datePublished, chapters, author);
        }
    }

    public static void main(String[] args) {
        String fileName = "Test3/manga - Sheet1.csv"; // Replace with your dataset path
        List<Manga> mangaList = loadDataset(fileName);

        if (mangaList.isEmpty()) {
            System.out.println("No manga records loaded.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n=== Manga Processor Menu ===");
            System.out.println("1. Sort Alphabetically");
            System.out.println("2. Sort by Chapters (Ascending)");
            System.out.println("3. Sort by Chapters (Descending)");
            System.out.println("4. Filter by Chapters Greater Than a Value");
            System.out.println("5. Search by Title");
            System.out.println("6. Aggregate: Total Chapters");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    sortAlphabetically(mangaList);
                    break;
                case 2:
                    sortByChaptersAscending(mangaList);
                    break;
                case 3:
                    sortByChaptersDescending(mangaList);
                    break;
                case 4:
                    filterByChapters(scanner, mangaList);
                    break;
                case 5:
                    searchByTitle(scanner, mangaList);
                    break;
                case 6:
                    aggregateTotalChapters(mangaList);
                    break;
                case 0:
                    System.out.println("Exiting... Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);

        scanner.close();
    }

    private static List<Manga> loadDataset(String fileName) {
        List<Manga> mangaList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String title = parts[0].trim();
                    String datePublished = parts[1].trim();
                    String chapters = parts[2].trim();
                    String author = parts[3].trim();
                    mangaList.add(new Manga(title, datePublished, chapters, author));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading dataset: " + e.getMessage());
        }
        return mangaList;
    }

    private static void sortAlphabetically(List<Manga> mangaList) {
        System.out.println("\nSorted Alphabetically:");
        mangaList.stream()
                .sorted(Comparator.comparing(m -> m.title))
                .forEach(System.out::println);
    }

    private static void sortByChaptersAscending(List<Manga> mangaList) {
        System.out.println("\nSorted by Chapters (Ascending):");
        mangaList.stream()
                .sorted(Comparator.comparingInt(m -> parseChapters(m.chapters)))
                .forEach(System.out::println);
    }

    private static void sortByChaptersDescending(List<Manga> mangaList) {
        System.out.println("\nSorted by Chapters (Descending):");
        mangaList.stream()
                .sorted((m1, m2) -> Integer.compare(parseChapters(m2.chapters), parseChapters(m1.chapters)))
                .forEach(System.out::println);
    }

    private static void filterByChapters(Scanner scanner, List<Manga> mangaList) {
        System.out.print("Enter the minimum number of chapters: ");
        int minChapters = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.println("\nFiltered Manga (Chapters > " + minChapters + "):");
        mangaList.stream()
                .filter(m -> parseChapters(m.chapters) > minChapters)
                .forEach(System.out::println);
    }

    private static void searchByTitle(Scanner scanner, List<Manga> mangaList) {
        System.out.print("Enter the title to search: ");
        String searchTitle = scanner.nextLine().toLowerCase();

        System.out.println("\nSearch Results for '" + searchTitle + "':");
        mangaList.stream()
                .filter(m -> m.title.toLowerCase().contains(searchTitle))
                .forEach(System.out::println);
    }

    private static void aggregateTotalChapters(List<Manga> mangaList) {
        int totalChapters = mangaList.stream()
                .mapToInt(m -> parseChapters(m.chapters))
                .sum();
        System.out.println("\nTotal Chapters in Dataset: " + totalChapters);
    }

    private static int parseChapters(String chapters) {
        try {
            if (chapters.contains("+")) {
                return Integer.parseInt(chapters.replace("+", "").trim());
            }
            return Integer.parseInt(chapters.trim());
        } catch (NumberFormatException e) {
            return 0; // Default to 0 if parsing fails
        }
    }
}
