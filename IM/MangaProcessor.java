
import java.io.*;
import java.util.*;


public class MangaProcessor {

    static class Manga {
        String title;
        String latestChapter;
        String dateReleased;
        String genre;
        String status;
        double rating;

        Manga(String title, String latestChapter, String dateReleased, String genre, String status, double rating) {
            this.title = title;
            this.latestChapter = latestChapter;
            this.dateReleased = dateReleased;
            this.genre = genre;
            this.status = status;
            this.rating = rating;
        }
    }

    public static void main(String[] args) {
        String fileName = "Test3/MANGA-Data-Set-Group-5.csv"; 
        List<Manga> mangaList = loadDataset(fileName);
        if (mangaList.isEmpty()) {
            System.out.println("No manga records loaded.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n=== Main Menu ===");
            System.out.println();
            System.out.println();
            System.out.println("CHOOSE A NUMBER");
            System.out.println("1. Show all Manga Titles");
            System.out.println("2. Top 10 Highest Rated Manga");
            System.out.println("3. Longest Chapter");
            System.out.println("4. Genres");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine(); // Consume newline
            switch (choice) {
                case 1:
                    showMangaTitles(mangaList, sc);
                    break;
                case 2:
                    topRatedManga(mangaList, sc);
                    break;
                case 3:
                    longestChapter(mangaList, sc);
                    break;
                case 4:
                    genres(mangaList, sc);
                    break;
                case 5:
                    System.out.println("Exiting... Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 5);

        sc.close();
    }
    private static List<Manga> loadDataset(String fileName) {
        List<Manga> mangaList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1) continue;
                String[] parts = line.split(",(?=([^\"']*\"[^\"']*\")*[^\"']*$)", -1);

                if (parts.length >= 7) { // Ensure the line has at least 7 elements
                    try {
                        String title = parts[0].trim().replaceAll("\"", "");
                        String latestChapter = parts[1].trim().replaceAll("\"", "");
                        String dateReleased = parts[2].trim().replaceAll("\"", "");
                        String genre = parts[5].trim().replaceAll("\"", "");
                        String status = parts[6].trim().replaceAll("\"", "");
                        double rating = parts.length > 7 ? parseRating(parts[7].trim().replaceAll("\"", "")) : 0.0; // Handle missing rating
                        mangaList.add(new Manga(title, latestChapter, dateReleased, genre, status, rating));
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid entry on line " + lineNumber + ": " + Arrays.toString(parts));
                    }
                } else {
                    System.err.println("Skipping incomplete entry on line " + lineNumber + ": " + Arrays.toString(parts));
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + fileName);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return mangaList;
    }
    private static double parseRating(String ratingStr) {
        try {
            return Double.parseDouble(ratingStr);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static void showMangaTitles(List<Manga> mangaList, Scanner sc) {
        int choice;
        do {
            System.out.println("\n--- Show All Manga Titles ---");
            System.out.println("1. Sort Titles");
            System.out.println("2. Search for a Title");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter your choice: ");
            choice = getValidatedChoice(sc);
            switch (choice) {
                case 1:
                    sortByTitle(mangaList);
                    break;
                case 2:
                    searchByTitle(sc, mangaList);
                    break;
                case 3:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 3);
    }

    // Top 10 Highest Rated Manga Menu
    private static void topRatedManga(List<Manga> mangaList, Scanner sc) {
        int choice;
        List<Manga> topRatedManga = mangaList.stream()
                .sorted((m1, m2) -> Double.compare(m2.rating, m1.rating))
                .limit(10)
                .toList();
        do {
            System.out.println("\n--- Top 10 Highest Rated Manga ---");
            System.out.println("1. Show Titles and Ratings");
            System.out.println("2. Sort Titles (Alphabetical, Ascending, Descending)");
            System.out.println("3. Search for a Title");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");
            choice = getValidatedChoice(sc);
            switch (choice) {
                case 1:
                    displayTable(topRatedManga);
                    break;
                case 2:
                    sortByTitle(topRatedManga);
                    break;
                case 3:
                    searchByTitle(sc, topRatedManga);
                    break;
                case 4:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 4);
    }

    // Longest Chapter Menu
    private static void longestChapter(List<Manga> mangaList, Scanner sc) {
        int choice;
        // Convert the sorted result into a mutable list
        List<Manga> sortedByChapter = new ArrayList<>(
                mangaList.stream().sorted((m1, m2) -> Integer.compare(parseChapters(m2.latestChapter), parseChapters(m1.latestChapter)))
                        .toList()
        );

        do {
            System.out.println("\n--- Longest Chapter ---");
            System.out.println("1. Show Longest Chapters");
            System.out.println("2. Sort Titles (Alphabetical, Ascending, Descending)");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter your choice: ");
            choice = getValidatedChoice(sc);

            switch (choice) {
                case 1:
                    displayTable(sortedByChapter);
                    break;
                case 2:
                    sortByTitle(sortedByChapter); // This method is now fixed to work with mutable lists
                    break;
                case 3:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 3);
    }

    private static int parseChapters(String latestChapter) {
        // Example implementation: Convert chapter to an integer
        try {
            String chapter = "";
            return Integer.parseInt(chapter);
        } catch (NumberFormatException e) {
            return 0; // Default to 0 if parsing fails
        }
    }


    // Genres Menu
    private static void genres(List<Manga> mangaList, Scanner sc) {
        int choice;
        do {
            System.out.println("\n--- Genres Menu ---");
            System.out.println("1. Show All Genres");
            System.out.println("2. Search for a Genre");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter your choice: ");
            choice = getValidatedChoice(sc);

            switch (choice) {
                case 1:
                    showAllGenres(mangaList);
                    break;
                case 2:
                    filterByGenre(sc, mangaList);
                    break;
                case 3:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 3);
    }

    private static void showAllGenres(List<Manga> mangaList) {

        Set<String> genresSet = new HashSet<>();


        for (Manga manga : mangaList) {
            String[] genres = manga.genre.split(",");
            for (String genre : genres) {
                genresSet.add(genre.trim());
            }
        }


        List<String> sortedGenres = new ArrayList<>(genresSet);
        Collections.sort(sortedGenres);

        // Display yung genre
        System.out.println("\n--- All Genres ---");
        int count = 1;
        for (String genre : sortedGenres) {
            System.out.printf("%d. %s%n", count++, genre);
        }
    }


    private static int getValidatedChoice(Scanner sc) {
        while (!sc.hasNextInt()) {
            System.out.print("Invalid input. Enter a number: ");
            sc.next();
        }
        return sc.nextInt();
    }


    private static void displayTable(List<Manga> mangaList) {
        String[] header = {"Title", "Latest Chapter", "Date Released", "Genre", "Status", "Rating"};
        int[] columnWidths = calculateColumnWidths(header, mangaList);

        printHeader(header, columnWidths);
        for (Manga manga : mangaList) {
            String[] row = {
                    manga.title,
                    manga.latestChapter,
                    manga.dateReleased,
                    manga.genre,
                    manga.status,
                    String.format("%.2f", manga.rating)
            };
            printRow(row, columnWidths);
        }
    }

    private static int[] calculateColumnWidths(String[] header, List<Manga> mangaList) {
        int[] widths = new int[header.length];
        for (int i = 0; i < header.length; i++) {
            widths[i] = header[i].length();
        }
        for (Manga manga : mangaList) {
            widths[0] = Math.max(widths[0], manga.title.length());
            widths[1] = Math.max(widths[1], manga.latestChapter.length());
            widths[2] = Math.max(widths[2], manga.dateReleased.length());
            widths[3] = Math.max(widths[3], manga.genre.length());
            widths[4] = Math.max(widths[4], manga.status.length());
            widths[5] = Math.max(widths[5], String.format("%.2f", manga.rating).length());
        }
        for (int i = 0; i < widths.length; i++) {
            widths[i] += 2;
        }
        return widths;
    }

    private static void printHeader(String[] headerRow, int[] columnWidths) {
        printSeparator(columnWidths);
        for (int i = 0; i < headerRow.length; i++) {
            System.out.printf("| %" + (-columnWidths[i]) + "s ", headerRow[i]);
        }
        System.out.println("|");
        printSeparator(columnWidths);
    }

    private static void printRow(String[] row, int[] columnWidths) {
        for (int i = 0; i < row.length; i++) {
            System.out.printf("| %" + (-columnWidths[i]) + "s ", row[i]);
        }
        System.out.println("|");
    }

    private static void printSeparator(int[] columnWidths) {
        for (int width : columnWidths) {
            System.out.print("+" + "-".repeat(width));
        }
        System.out.println("+");
    }

    private static void sortByTitle(List<Manga> mangaList) {
        if (mangaList.isEmpty()) {
            System.out.println("No manga titles available to sort.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- Sort Titles ---");
        System.out.println("1. Ascending");
        System.out.println("2. Descending");
        System.out.print("Enter your choice: ");
        int choice = getValidatedChoice(sc);

        // Create a mutable copy of the list
        List<Manga> mutableList = new ArrayList<>(mangaList);

        if (choice == 1) {
            mutableList.sort(Comparator.comparing(m -> m.title.toLowerCase()));
            System.out.println("\nTitles sorted in ascending order:");
        } else if (choice == 2) {
            mutableList.sort((m1, m2) -> m2.title.toLowerCase().compareTo(m1.title.toLowerCase()));
            System.out.println("\nTitles sorted in descending order:");
        } else {
            System.out.println("Invalid choice. Returning to the previous menu.");
            return;
        }

        // Display the sorted list
        displayTable(mutableList);
    }

    private static void filterByGenre(Scanner scanner, List<Manga> mangaList) {
        System.out.print("Enter the genre to filter by: ");
        String genre = scanner.nextLine().toLowerCase();
        List<Manga> filteredList = new ArrayList<>();
        for (Manga manga : mangaList) {
            if (manga.genre.toLowerCase().contains(genre)) {
                filteredList.add(manga);
            }
        }
        displayTable(filteredList);
    }

    private static void searchByTitle(Scanner scanner, List<Manga> mangaList) {
        System.out.print("Enter the title to search: ");
        String searchTitle = scanner.nextLine().toLowerCase();
        List<Manga> filteredList = new ArrayList<>();
        for (Manga manga : mangaList) {
            if (manga.title.toLowerCase().contains(searchTitle)) {
                filteredList.add(manga);
            }
        }
        displayTable(filteredList);
    }
}

/*
private static void filterByStatus(Scanner scanner, List<Manga> mangaList) {
        System.out.print("Enter the status to filter by (Ongoing/Completed): ");
        String status = scanner.nextLine().toLowerCase();
        List<Manga> filteredList = new ArrayList<>();
        for (Manga manga : mangaList) {
            if (manga.status.toLowerCase().contains(status)) {
                filteredList.add(manga);
            }
        }
        displayTable(filteredList);
    }
 */
/*
   private static void sortByRatingDescending(List<Manga> mangaList) {
        mangaList.sort((m1, m2) -> Double.compare(m2.rating, m1.rating));
        displayTable(mangaList);
    }
 */
