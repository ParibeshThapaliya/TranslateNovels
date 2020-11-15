package com.mitzillfi.translatenovels;

import java.io.FileOutputStream;
import java.net.URL;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;

public class AppTest {
        public static void main(String[] args) {
                try {
                        // Create new Book
                        Book book = new Book();

                        // Set the title
                        book.getMetadata().addTitle("ED");

                        // Add an Author
                        book.getMetadata().addAuthor(new Author("jo", "mama"));

                        // Set cover image
                        book.setCoverImage(new Resource(
                                        new URL("https://cdn.novelupdates.com/images/2016/09/liqiye.jpg").openStream(),
                                        "cover.jpg"));

                        // Add Chapter 1
                        book.addSection("chapter 1",
                                        new Resource(AppTest.class.getClassLoader().getResourceAsStream("\\ed1.html"),
                                                        "chapter1.html"));

                        // Add css file
                        book.getResources().add(new Resource(
                                        AppTest.class.getClassLoader().getResourceAsStream("\\page_styles.css"),
                                        "page_styles.css"));
                        book.getResources().add(new Resource(
                                        AppTest.class.getClassLoader().getResourceAsStream("\\stylesheet.css"),
                                        "stylesheet.css"));

                        // Add Chapter 2
                        book.addSection("Chapter 2",
                                        new Resource(AppTest.class.getClassLoader().getResourceAsStream("\\ed1.html"),
                                                        "chapter2.html"));

                        // Create EpubWriter
                        EpubWriter epubWriter = new EpubWriter();

                        // Write the Book as Epub
                        epubWriter.write(book, new FileOutputStream("edp449.epub"));
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        // private void writeToFile(String content, String name) {

        // String regex = "[\\/:\"*?<>|]+";
        // File file;
        // try (Scanner sc = new Scanner(content)) {

        // file = new File(filePath + name.replaceAll(regex, "") + ".html");
        // if (file.isFile()) {
        // System.out.println("file already exists ");
        // } else {

        // try (BufferedWriter out = new BufferedWriter(new
        // FileWriter(file.getAbsoluteFile()))) {
        // String html = Files.readString(Paths.get(
        // App.class.getClassLoader().getResource("base.html").toURI()));

        // String ss = "<h2 class=\"t\">" + sc.nextLine() + "</h2>";
        // StringBuilder bld = new StringBuilder();
        // while (sc.hasNext()) {
        // bld.append("<p class=\"pg\">" + sc.nextLine() + "</p>");
        // }
        // ss += bld.toString();
        // ss = ss.replaceAll("<p class=\\\"pg\\\">\\s*<\\/p>", "");
        // html = html.replaceAll("</title>", name + "</title>").replaceAll("</body>",
        // ss + "</body>");
        // out.write(html);

        // }
        // System.out.println(name + " created SucessFully");
        // }

        // } catch (Exception e) {
        // e.printStackTrace();

        // }
        // }

        // }
}