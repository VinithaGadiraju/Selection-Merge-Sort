// Vinitha Gadiraju
//CIS 212 
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Phone entry class uses accessor functions and setter functions to get the name 
// and phone number
class PhoneEntry {
	private String m_name;
	private String m_phoneNum;
	
	public PhoneEntry (String name, String phoneNum) {
		m_name = name;
		m_phoneNum = phoneNum;
	}
	public PhoneEntry (PhoneEntry pe) {
		m_name = pe.getName();
		m_phoneNum = pe.getNum();
	}
	public String getName() {
		return m_name;
	}
	public String getNum() {
		return m_phoneNum;
	}
	public void setPhone(PhoneEntry setP) {
		m_name = setP.getName();
		m_phoneNum = setP.getNum();
	}
	
}

//Event Handler
class ButtonHandler implements ActionListener {
	private static final boolean isUIthreadedS = true;
	private static final boolean isUIthreadedM = true;
	private ArrayList<PhoneEntry> m_phonebook;
	private JButton m_s_btn;
	private JButton m_m_btn;
	private JLabel m_s_label;
	private JLabel m_m_label;
	
	public ButtonHandler(ArrayList<PhoneEntry> phonebook, JButton s_btn, JButton m_btn, 
		JLabel s_label, JLabel m_label) {
		m_phonebook = phonebook;
		m_s_btn = s_btn;
		m_m_btn = m_btn;
		m_s_label = s_label;
		m_m_label = m_label;	
	}
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource()==m_s_btn) {
			// thread for extra credit for selection sort
			if (isUIthreadedS) {
				ThreadedSelectionSort t_sort = new ThreadedSelectionSort(m_phonebook, m_s_label);
				Thread thread = new Thread(t_sort);
				thread.start();
			}
			else {
				long start_time = System.nanoTime();
				ArrayList<PhoneEntry> sorted_list = Main.selectionSort(m_phonebook);
				if (Main.isSorted(sorted_list) == true) {
					long end_time = System.nanoTime();
					long elapsed_time = (end_time - start_time)/1000000;
					m_s_label.setText("Elapsed time: " + elapsed_time + " milliseconds");
				}
				else {
					m_s_label.setText("Error, Sort failed.");
				}
			}
		}
		if (ae.getSource()==m_m_btn) {
			// thread for extra credit for merge sort 
			if (isUIthreadedM) {
				ThreadedMergeSort t_sort = new ThreadedMergeSort(m_phonebook, m_m_label);
				Thread thread = new Thread(t_sort);
				thread.start();
			}
			else {
				long start_time = System.nanoTime();
				ArrayList<PhoneEntry> sorted_list = Main.mergeSort(m_phonebook);
				if (Main.isSorted(sorted_list) == true) {
					long end_time = System.nanoTime();
					long elapsed_time = (end_time - start_time)/1000000;
					m_m_label.setText("Elapsed time: " + elapsed_time + " milliseconds");
				}
				else {
					m_m_label.setText("Error, Sort failed.");
				}
			}	
		}
	}
}

// thread for selection sort 
class ThreadedSelectionSort implements Runnable {
	private ArrayList<PhoneEntry> m_phonebook;
	private JLabel m_slabel;
	
	public ThreadedSelectionSort (ArrayList<PhoneEntry> pe, JLabel sl) {
		m_phonebook = pe;
		m_slabel = sl;
	}
	
	public void run() {
		long start_time = System.nanoTime();
		m_slabel.setText("Wait...");
		ArrayList<PhoneEntry> sorted_list = Main.selectionSort(m_phonebook);
		if (Main.isSorted(sorted_list) == true) {
			long end_time = System.nanoTime();
			long elapsed_time = (end_time - start_time)/1000000;
			m_slabel.setText("Elapsed time: " + elapsed_time + " milliseconds");
		}
		else {
			m_slabel.setText("Error, Sort failed.");
		}
		return;
	}
}

// thread for merge sort 
class ThreadedMergeSort implements Runnable {
	private ArrayList<PhoneEntry> m_phonebook;
	private JLabel m_mlabel;
	
	public ThreadedMergeSort(ArrayList<PhoneEntry> pe, JLabel ml) {
		m_phonebook = pe;
		m_mlabel = ml;
	}
	
	public void run() {
		long start_time = System.nanoTime();
		ArrayList<PhoneEntry> sorted_list = Main.mergeSort(m_phonebook);
		if (Main.isSorted(sorted_list) == true) {
			long end_time = System.nanoTime();
			long elapsed_time = (end_time - start_time)/1000000;
			m_mlabel.setText("Elapsed time: " + elapsed_time + " milliseconds");
		}
		else {
			m_mlabel.setText("Error, Sort failed.");
		}
		return;
	}
}

public class Main {
	public static void main(String[] args) throws Exception {
		// creating phone book 
		ArrayList<PhoneEntry> cs_phonebook = createPhoneBook("http://www.cs.uoregon.edu/Classes/16S/cis212/assignments/phonebook.txt");
		
		// creating graphic frame 
		JFrame app = new JFrame("Assignment 5");
		JPanel top_panel = new JPanel();
		JButton s_btn = new JButton("Selection Sort");
		JLabel s_label = new JLabel("...");
		BoxLayout sl = new BoxLayout(top_panel, BoxLayout.X_AXIS);
		top_panel.setLayout(sl);
		top_panel.add(s_btn); top_panel.add(s_label);
		app.add(top_panel, BorderLayout.NORTH);
		
		JPanel bottom_panel = new JPanel();
		JButton m_btn = new JButton("Merge Sort");
		JLabel m_label = new JLabel("...");
		BoxLayout ml = new BoxLayout(bottom_panel, BoxLayout.X_AXIS);
		bottom_panel.setLayout(ml);
		bottom_panel.add(m_btn); bottom_panel.add(m_label);
		app.add(bottom_panel, BorderLayout.SOUTH);
		
		//implementing event handler for each button 
		ButtonHandler b_handler = new ButtonHandler(cs_phonebook, s_btn, m_btn, s_label, m_label);
		s_btn.addActionListener(b_handler);
		m_btn.addActionListener(b_handler);
				
		app.setSize(400, 100);
		app.setVisible(true);
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
	}
	// based on http://stackoverflow.com/questions/1485708/how-do-i-do-a-http-get-in-java
	// this method creates the phone book from string/url
	public static ArrayList<PhoneEntry> createPhoneBook(String urlToRead) throws Exception {
		
		ArrayList<PhoneEntry> phoneEntries = new ArrayList<PhoneEntry>();
		URL url = new URL(urlToRead);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			String [] line_array = line.split(" ",2);
			phoneEntries.add(new PhoneEntry(line_array[1], line_array[0]));	
		}
		rd.close();
		return phoneEntries;
	}
	// this method creates the phone book from a text file 
	public static ArrayList<PhoneEntry> createPhoneBookfromFile(String filename) throws Exception {
		ArrayList<PhoneEntry> phoneBook = new ArrayList<PhoneEntry>();
		InputStream filestream = new FileInputStream(filename);
		InputStreamReader isr = new InputStreamReader(filestream, Charset.forName("UTF-8"));
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
			String [] line_array = line.split(" ",2);
			phoneBook.add(new PhoneEntry(line_array[1], line_array[0]));	
		}
		br.close();
		return phoneBook;
	}
	
	// printing phone book for debugging purposes
	public static void printPhoneBook(ArrayList<PhoneEntry> pb, String desc) {
		System.out.println(desc);
		System.out.println("The size of the array is " + pb.size());
		if (isSorted(pb)) {
			System.out.println("This array is sorted");
		}
		else {
			System.out.println("This array is unsorted");
		}
		for (PhoneEntry p : pb) {
			System.out.println(p.getName());
		}
	}
	
	// method implements selection sort 
	public static ArrayList<PhoneEntry> selectionSort(ArrayList<PhoneEntry> in_array) {
		ArrayList<PhoneEntry> out_array = new ArrayList<PhoneEntry>();
		for (PhoneEntry entry : in_array) {
			out_array.add(new PhoneEntry(entry));
		}
		
		int out_size = out_array.size();
		for (int i=0; i < out_size-1; i++) {
			int min_index = i;
			PhoneEntry min_value = out_array.get(i);
			for (int j=i; j < out_size; j++) {
				if (out_array.get(j).getName().compareTo(min_value.getName()) < 0) {
					min_index = j;
					min_value = out_array.get(j);
				}
			}
			if (i != min_index) {
				PhoneEntry temp = new PhoneEntry(out_array.get(i));
				out_array.get(i).setPhone(out_array.get(min_index));
				out_array.get(min_index).setPhone(temp);
			}
		}
		return out_array;
	}
	
	// method implements merge sort 
	public static ArrayList<PhoneEntry> mergeSort(ArrayList<PhoneEntry> in_array) {
		int in_size = in_array.size();
		// recursion set up ending case here 
		if (in_size==1) {
			return in_array;
		}
		// cut in_array into 2 arrays
		int mid_point = in_size/2;
		ArrayList<PhoneEntry> first_array = new ArrayList<PhoneEntry>();
		ArrayList<PhoneEntry> second_array = new ArrayList<PhoneEntry>();
		
		for (int i = 0; i < mid_point; i++) {
			first_array.add(new PhoneEntry(in_array.get(i)));
		}
		for (int i = mid_point; i < in_size; i++) {
			second_array.add(new PhoneEntry(in_array.get(i)));
		}
		
		// sort the cut arrays 
		first_array = mergeSort(first_array);
		second_array = mergeSort(second_array);
		
		//combine 2 arrays into a new array
		ArrayList<PhoneEntry> out_array = new ArrayList<PhoneEntry>();
		int size1 = first_array.size();
		int size2 = second_array.size();
		int i = 0; int j = 0;
		while (i != size1 || j != size2) {
			//the following 2 if statements are to address remaining end elements of cut arrays
			// when one of them is completed 
			if (i==size1) {
				out_array.add(second_array.get(j));
				j++;
				continue;
			}
			if (j==size2) {
				out_array.add(first_array.get(i));
				i++;
				continue;
			}
			// here we compare the individual elements of first and second cut arrays to merge into a bigger array 
			if (first_array.get(i).getName().compareTo(second_array.get(j).getName()) < 0) {
				out_array.add(first_array.get(i));
				i++;
			}
			else {
				out_array.add(second_array.get(j));
				j++;
			}
		}
		return out_array;
	}
	
	// method checks if array list is properly sorted 
	public static boolean isSorted(ArrayList<PhoneEntry> pe) {
		int array_size = pe.size();
		if (array_size==0 || array_size == 1) {
			return true;
		}
		for (int i = 1; i < array_size; i++){
			if (pe.get(i-1).getName().compareTo(pe.get(i).getName()) <= 0) {
				continue;
			}
			else {
				return false;
			}
		}
		return true;
 	}
}
