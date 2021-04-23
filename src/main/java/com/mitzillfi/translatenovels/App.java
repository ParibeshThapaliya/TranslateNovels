package com.mitzillfi.translatenovels;

import static java.lang.Thread.sleep;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import net.miginfocom.swing.MigLayout;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;

/**
 * Hello world!
 *
 */
public class App extends JFrame {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected App() {
        setTitle("Translate Novels");
        setResizable(false);
        Screen screen = new Screen();
        add(screen);
        screen.init();
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new App();
    }

    private class Screen extends JPanel {
        private static final long serialVersionUID = 1L;
        public static final int WIDTH = 375;
        public static final int HEIGHT = 400;
        private transient MigLayout layout;
        private JLabel directory, website, startChapter, endchapter, bookName, language, coverImage;
        private static final String WRAP = "wrap 20";
        private JTextField inputPath, inputWebURL, inputChapterStart, inputChapterEnd, inputBookName, inputCoverImage;
        private JComboBox<String> selectLanguage;
        private String[] languageOptions;

        private JButton pasteURL, openDir, selectImage, startTranslate;

        protected Screen() {
            super();
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setFocusable(true);
            layout = new MigLayout("insets 10% 10% 20% 10%");
            setLayout(layout);
            requestFocus();

        }

        protected void init() {
            languageOptions = new String[] { "Chinese", "Japanese" };
            directory = new JLabel(" Directory");
            website = new JLabel("Website");
            startChapter = new JLabel("start");
            endchapter = new JLabel("       end           ");
            bookName = new JLabel("Book Name");
            language = new JLabel("language");
            coverImage = new JLabel("Cover Image");

            inputPath = new JTextField(System.getProperty("user.home") + "\\Desktop\\", 20);
            inputWebURL = new JTextField("https://ncode.syosetu.com/n1745ct/2/", 20);
            inputChapterStart = new JTextField("1", 15);
            inputChapterEnd = new JTextField("3", 15);
            inputBookName = new JTextField("", 20);
            inputCoverImage = new JTextField("", 20);

            selectLanguage = new JComboBox<>(languageOptions);
            openDir = new JButton("...");
            selectImage = new JButton("...");
            pasteURL = new JButton("\uD83D\uDCCB");
            startTranslate = new JButton("START");

            openDir.setMaximumSize(new Dimension(48, 17));
            selectImage.setMaximumSize(new Dimension(48, 17));
            openDir.setFocusPainted(false);
            selectImage.setFocusPainted(false);
            pasteURL.setMaximumSize(new Dimension(48, 17));
            inputWebURL.setSelectedTextColor(new Color(0, 0, 0, 200));
            inputWebURL.selectAll();
            openDir.setSize(20, 15);
            selectImage.setSize(20, 15);
            add(directory);
            add(inputPath);
            add(openDir, WRAP);
            add(coverImage);
            add(inputCoverImage);
            add(selectImage, WRAP);
            add(website);
            add(inputWebURL);
            add(pasteURL, WRAP);
            add(startChapter);
            add(inputChapterStart, "split");
            add(endchapter);
            add(inputChapterEnd, WRAP);
            add(bookName);
            add(inputBookName, WRAP);
            add(language);
            add(selectLanguage, WRAP);
            add(startTranslate, "cell 1 7 ");
            openDir.addActionListener(e -> inputPath.setText(selectFile(inputPath.getText(), true)));
            selectImage.addActionListener(e -> {
                try {
                    inputCoverImage.setText(
                            Paths.get(selectFile(inputCoverImage.getText(), false)).toUri().toURL().toString());
                } catch (MalformedURLException e2) {

                    e2.printStackTrace();
                }
            });

            pasteURL.addActionListener(e -> {
                try {
                    inputWebURL.setText((String) Toolkit.getDefaultToolkit().getSystemClipboard()
                            .getData(java.awt.datatransfer.DataFlavor.stringFlavor));
                } catch (HeadlessException | UnsupportedFlavorException | IOException e1) {
                    e1.printStackTrace();
                }
            });
            startTranslate.addActionListener(e -> {
                System.out.println(selectLanguage.getSelectedItem().toString());
                new GenerateChapters(inputPath.getText(), inputWebURL.getText(),
                        0 + Integer.parseInt(inputChapterStart.getText()),
                        (inputChapterEnd.getText().matches("\\b\\d+\\b"))
                                ? 0 + Integer.parseInt(inputChapterEnd.getText())
                                : 0,
                        inputBookName.getText(), inputCoverImage.getText(), selectLanguage.getSelectedItem().toString())
                                .init();
            });

        }

        private String selectFile(String s, boolean directoriesOnly) {
            JFileChooser chooser = new JFileChooser();
            if (directoriesOnly) {
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setDialogTitle("select Folder");
            } else {
                String[] suffices = ImageIO.getReaderFileSuffixes();
                for (int i = 0; i < suffices.length; i++) {
                    FileFilter filter = new FileNameExtensionFilter(suffices[i] + " files", suffices[i]);
                    chooser.addChoosableFileFilter(filter);
                }
                chooser.setDialogTitle("select Image");
            }

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                return chooser.getSelectedFile().getPath() + "\\";

            } else {
                return s;
            }

        }

    }

}

class GenerateChapters {
    private String filePath, websiteURL, bookName, bookCoverImageFilePath, language;
    private int startChapter, endChapter;
    private String cn = "Chinese";

    public GenerateChapters(String filePath, String websiteURL, int startChapter, int endChapter, String bookName,
            String bookCoverImageFilePath, String language) {
        this.bookName = bookName;
        this.filePath = filePath;
        this.websiteURL = websiteURL;
        this.startChapter = startChapter;
        this.endChapter = endChapter;
        this.bookCoverImageFilePath = bookCoverImageFilePath;
        this.language = language;
    }

    public void init() {
        Extract extract = new Extract(websiteURL, (startChapter < endChapter) ? (endChapter - startChapter) + 1 : 1);
        extract.init();
        Translate translate = new Translate(extract.getRawChapters());
        translate.translateRawChapters();

    }

    protected class Extract {
        private String websiteUrl;
        private String[] rawChapters;
        private String[] rawChapterLinks;
        private String[] rawChapterName;
        private String rawChapterSource;

        public Extract(String websiteURL, int howManyChapters) {
            this.websiteUrl = websiteURL;
            rawChapters = new String[howManyChapters];
            rawChapterLinks = new String[howManyChapters + 1];
            rawChapterName = new String[howManyChapters];
        }

        public void init() {
            if (language.equals(cn)) {
                rawChapterSource = "https://www.biqubao.com";
            } else {
                rawChapterSource = "https://ncode.syosetu.com";
            }
            findRawChapterLinks();
            findRawChaptherName();
            rawChapterTexts();
        }

        private void findRawChapterLinks() {

            Document d;
            rawChapterLinks[0] = websiteUrl;
            try {

                for (int i = 1; i < rawChapterLinks.length; i++) {
                    d = Jsoup.connect(rawChapterLinks[i - 1]).get();
                    Element link;
                    if (language.equals(cn)) {
                        link = d.select(
                                "#wrapper > div.content_read > div > div.bookname > div.bottem1 > a:nth-child(3)")
                                .first();
                        rawChapterLinks[i] = rawChapterSource + link.attr("href");

                    } else {
                        Elements s = d.getElementsByClass("novel_bn");
                        for (int j = s.size() - 1; j > 0; j--) {
                            if (s.first().child(j).text().contains("次へ")) {
                                link = s.first().child(j);
                                rawChapterLinks[i] = rawChapterSource + link.attr("href");
                                break;
                            }
                        }

                    }

                }
                if (!rawChapterLinks[rawChapterLinks.length - 1].contains("html")) {
                    rawChapterLinks[rawChapterLinks.length - 1] = rawChapterLinks[rawChapterLinks.length - 2];
                }

            } catch (

            IOException e) {

                e.printStackTrace();
            }

        }

        private void rawChapterTexts() {

            for (int i = 0; i < rawChapters.length; i++) {

                Document d;
                try {
                    d = Jsoup.connect(rawChapterLinks[i]).get();
                    d.outputSettings(new Document.OutputSettings().prettyPrint(false));
                    Element e;
                    if (language.equals(cn)) {
                        e = d.select("#content").first();
                    } else {
                        e = d.getElementById("novel_honbun");
                    }

                    e.select("br").append("\\n");
                    e.select("p").prepend("\\n\\n");
                    rawChapters[i] = "\t\t\t\t\t\t\t" + rawChapterName[i] + " \n\n"
                            + e.text().replaceAll("\\\\n", "\n");

                } catch (IOException e1) {

                    e1.printStackTrace();
                }

            }
        }

        public void findRawChaptherName() {
            Document d;

            try {

                for (int i = 0; i < rawChapterName.length; i++) {
                    d = Jsoup.connect(rawChapterLinks[i]).get();
                    String name;
                    if (language.equals(cn)) {
                        name = d.select("#wrapper > div.content_read > div > div.bookname > h1").text();
                    } else {
                        name = d.getElementsByClass("novel_subtitle").text();
                    }
                    rawChapterName[i] = name;
                }

            } catch (

            IOException e) {

                e.printStackTrace();
            }
        }

        public String[] getRawChapters() {
            return rawChapters;
        }

        public String[] getRawChapterName() {
            return rawChapterName;
        }

        public String getRawChapterFinalLink() {
            return rawChapterLinks[rawChapterLinks.length - 1];

        }
    }

    protected class Translate {
        /*
         * options.addArguments("user-data-dir=C:\\Users\\Parib\\AppData\\Local\\Google\
         * \Chrome\\User Data\\"); options.addAr guments("profile-directory=Profile 5");
         */
        private String val = "value";
        private int currentChapter;
        private String[] rawChapters;
        private WebDriver driver;
        private ChromeOptions options = new ChromeOptions();
        private String[] translatedChapters;
        private String[] translatedTitles;
        private String langTag;

        public Translate(String[] rawChapters) {
            this.currentChapter = startChapter;
            this.rawChapters = rawChapters;

            translatedChapters = new String[rawChapters.length];
            translatedTitles = new String[rawChapters.length];
            if (language.equals(cn)) {
                langTag = "zh/en/";
            } else {
                langTag = "ja/en/";
            }
            System.setProperty("webdriver.chrome.driver", "c:\\chromedriver.exe");

            options.addArguments("headless");

            driver = new ChromeDriver(options);
        }

        public void translateRawChapters() {

            for (int i = 0; i < rawChapters.length; i++) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
                driver.get("https://www.deepl.com/en/translator#" + langTag
                        + URLEncoder.encode(rawChapters[i], StandardCharsets.UTF_8).replace("+", "%20"));
                String out = "#dl_translator > div.lmt__text > div.lmt__sides_container >div.lmt__side_container.lmt__side_container--target >div.lmt__textarea_container > div.lmt__inner_textarea_container > textarea";

                String text = output(driver, out, 2500) + "\n";
                System.out.println(currentChapter + " translated");
                translatedChapters[i] = (text);
                translatedTitles[i] = bookName + "-Chapter-" + String.format("%03d", currentChapter);
                currentChapter++;
                if (i % 15 == 0 && i != 0) {
                    driver = new ChromeDriver(options);
                }
            }
            createHTML();
            createEBook();
        }

        private String output(WebDriver d, String path, int i) {
            WebElement translatedElement = driver.findElement(By.cssSelector(path));
            waitForTextToAppear(d, translatedElement, i);
            return translatedElement.getAttribute(val);
        }

        private void waitForTextToAppear(WebDriver newDriver, WebElement element, int x) {

            WebDriverWait wait = new WebDriverWait(newDriver, 60);
            wait.until(new ExpectedCondition<Boolean>() {
                boolean f = false;

                public Boolean apply(WebDriver d) {
                    if (element.getAttribute(val).length() != 0) {
                        if (!element.getAttribute(val).contains("[...]")) {
                            System.out.println("case Matched");
                            f = true;
                        } else {
                            System.out.println("invalid char");
                            f = false;
                        }
                    } else
                        System.out.println("length 0");
                    try {
                        sleep(x);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                    return f;
                }
            });
        }

        private void createHTML() {
            for (int i = 0; i < rawChapters.length; i++) {
                try (Scanner sc = new Scanner(translatedChapters[i])) {
                    // String html =
                    // Files.readString(Paths.get(App.class.getClassLoader().getResource("base.html").toURI()));
                    String html = IOUtils.toString(App.class.getClassLoader().getResourceAsStream("base.html"),
                            StandardCharsets.UTF_8);

                    String ss = "<h2 class=\"t\">" + sc.nextLine() + "</h2>";
                    StringBuilder bld = new StringBuilder();
                    while (sc.hasNext()) {
                        bld.append("<p class=\"pg\">" + sc.nextLine() + "</p>");
                    }
                    ss += bld.toString();
                    ss = ss.replaceAll("<p class=\\\"pg\\\">\\s*<\\/p>", "");
                    html = html.replaceAll("</title>", translatedTitles[i] + "</title>").replaceAll("</body>",
                            ss + "</body>");
                    translatedChapters[i] = html;
                    System.out.println(translatedTitles[i] + " created SucessFully");

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

        }

        private void createEBook() {
            String regex = "[\\/:\"*?<>|]";
            System.out.println("GOT HERE ");
            try {
                System.out.println("got in ");
                Book book = new Book();
                book.getMetadata().addTitle(bookName + "Chapter " + startChapter + " - " + endChapter);
                book.getMetadata().addAuthor(new Author("jo", "mama"));
                book.getResources()
                        .add(new Resource(
                                IOUtils.toByteArray(App.class.getClassLoader().getResourceAsStream("page_styles.css")),
                                "page_styles.css"));

                book.getResources()
                        .add(new Resource(
                                IOUtils.toByteArray(App.class.getClassLoader().getResourceAsStream("stylesheet.css")),
                                "stylesheet.css"));
                if (bookCoverImageFilePath.length() >= 2) {
                    book.setCoverImage(new Resource(new URL(bookCoverImageFilePath).openStream(), "cover.jpg"));
                }

                for (int i = 0; i < rawChapters.length; i++) {
                    book.addSection("Chapter " + (startChapter + i),
                            new Resource(
                                    new ByteArrayInputStream(translatedChapters[i].getBytes(StandardCharsets.UTF_8)),
                                    translatedTitles[i].replaceAll("\\s", "_").replaceAll(regex, "") + ".html"));
                }
                EpubWriter epubWriter = new EpubWriter();
                String bkName = (startChapter >= endChapter) ? startChapter + "" : startChapter + "-" + endChapter;
                epubWriter.write(book, new FileOutputStream(filePath + bookName + " Chapter " + bkName + ".epub"));
            } catch (

            Exception e) {
                e.printStackTrace();
            }

        }

        public int getCurrentChapter() {
            return currentChapter;
        }

    }
}