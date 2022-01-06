package edu.uwm.cs351;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The driver for Lexicon.
 */
public class Driver {
	
	/** The main method.
	 * @param args the arguments */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: Main <lexicon file>");
			System.exit(1);
		}
		new Driver(args[0]).run();
	}

	private Lexicon lexicon;
	private String filename;
	private Scanner in;
	
	/** Instantiates a new driver, loading lexicon from file.
	 * @param name the name of the file */
	public Driver(String name) {
		lexicon = new Lexicon();
		filename = name;
		in = new Scanner(System.in);
	}
	
	/** Runs the driver. */
	public void run() {
		readLexicon();
		help();
		for (;;) {
			System.out.print("> ");
			String line = in.nextLine();
			if (line.equals("list")) list();
			else if (line.startsWith("find")) find(line);
			else if (line.startsWith("add")) add(line);
			else if (line.equals("save")) writeLexicon();
			else if (line.equals("help")) help();
			else if (line.equals("quit")) break;
			else error(line);
		}
	}

	private void help() {
		System.out.println("Lexicon System.  Words: " + lexicon.size());
		System.out.println("Commands: ");
		System.out.println("  list - print out entire lexicon");
		System.out.println("  find <prefix> - show all words that begin with given prefix");
		System.out.println("  add <word> - add the given word to the lexicon");
		System.out.println("  save - save the lexicon in the file it was read from.");
		System.out.println("  help - get this message");
		System.out.println("  quit - quite the program without saving");
	}
	
	private void error(String line) {System.out.println("Error: cannot understand command, type 'help' for help.");}

	private void list() {
		lexicon.consumeAll(string -> System.out.println(string));
	}
	
	private void find(String line) {
		String[] tokens = line.split(" ");
		if (tokens.length == 2)
			lexicon.consumeAllWithPrefix(string -> System.out.println(string), tokens[1]);
		else System.out.println("Prefix must be one word, type 'help' for help.");
	}
	
	private void add(String line) {
		String[] tokens = line.split(" ");
		if (tokens.length == 2) {
			String word = tokens[1];
			System.out.println(lexicon.add(word)
					? word+" added."
					: word+" already exists in lexicon.");
		}
		else System.out.println("Invalid command, type 'help' for help.");
	}
	
	private void readLexicon() {
		List<String> words = new ArrayList<String>();
		try (Scanner s = new Scanner(new File(filename))){
			while (s.hasNextLine())
					words.add(s.nextLine());
		} catch (FileNotFoundException e) {
			System.out.println("Could not open " + filename + " for reading.  Skipping.");
		}
		lexicon.addAll(words.toArray(new String[words.size()]), 0, words.size());
	}
	
	private void writeLexicon() {
		String[] words = lexicon.toArray(null);
		try {
			Writer w = new FileWriter(filename);
			for (String l : words) {
				w.write(l.toString());
				w.write('\n');
			}
			w.close();
			System.out.println("Lexicon successfully written");
		} catch (IOException e) {
			System.out.println("Problem: file may be partly written: " + e);
		}
	}
}
