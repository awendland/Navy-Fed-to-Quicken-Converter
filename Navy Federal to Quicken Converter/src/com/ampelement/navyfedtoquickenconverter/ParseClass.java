package com.ampelement.navyfedtoquickenconverter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;

public class ParseClass {

	String purchaseTransString;
	String paymentTransString;
	final String FID = "11281";
	final String INTU_BID = FID;
	String userID;
	String acctID;
	String newBalance;
	String serverDate;

	// final List<Integer> listLengthList = new ArrayList<Integer>();

	Date firstDate = null;
	Date lastDate = null;
	SimpleDateFormat inputDateFormat = new SimpleDateFormat("MM/dd/yy");
	SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat fitidOutputDF = new SimpleDateFormat("yyyy-MM-dd");

	Comparator<List<String>> comparator = new Comparator<List<String>>() {
		public int compare(List<String> o1, List<String> o2) {
			// Simple string comparison here, add more sophisticated logic
			// if needed.
			int o1Int = Integer.parseInt(o1.get(1));
			int o2Int = Integer.parseInt(o2.get(1));
			if (o1Int > o2Int) {
				return 1;
			} else if (o1Int < o2Int) {
				return -1;
			} else {
				return 0;
			}
		}
	};

	public static void main(String[] args) {
		new ParseClass();
	}

	ParseClass() {
		userID = JOptionPane.showInputDialog("Access Number");
		acctID = JOptionPane.showInputDialog("Credit Card Number");
		newBalance = JOptionPane.showInputDialog("Balance At End of Statement (numerical value)");

		transInputs();
	}
	
	public void transInputs() {
		final JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		final JTextArea textInput = new JTextArea("", 50, 70);
		JScrollPane scrollPane = new JScrollPane(textInput);
		panel.add(scrollPane);
		JButton button = new JButton("Enter");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setPaymentTransString(textInput.getText());

				frame.dispose();

				final JFrame frame1 = new JFrame();
				JPanel panel1 = new JPanel();
				final JTextArea textInput1 = new JTextArea("", 50, 70);
				JScrollPane scrollPane1 = new JScrollPane(textInput1);
				panel1.add(scrollPane1);
				JButton button1 = new JButton("Enter");
				button1.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						setPurchaseTransString(textInput1.getText());

						createQFX();

						frame1.dispose();
					}
				});
				panel1.add(button1);
				frame1.add(panel1);
				frame1.setTitle("Purchase Transaction");
				frame1.setSize(800, 950);
				frame1.show();

			}
		});
		panel.add(button);
		frame.add(panel);
		frame.setTitle("Payment Transaction");
		frame.setSize(800, 950);
		frame.show();
	}

	void setPurchaseTransString(String string) {
		purchaseTransString = string;
	}

	void setPaymentTransString(String string) {
		paymentTransString = string;
	}

	private String createIndent(int numberOfIndents) {
		final String FOUR_SPACE = "    ";
		StringBuilder indentBuilder = new StringBuilder();
		int i = 1;
		while (i <= numberOfIndents) {
			indentBuilder.append(FOUR_SPACE);
			i++;
		}
		return indentBuilder.toString();
	}

	private void createQFX() {

		List<List<String>> paymentList = new ArrayList<List<String>>();
		List<List<String>> purchaseList = new ArrayList<List<String>>();

		purchaseList = parseTransInputs(purchaseTransString, false);
		paymentList = parseTransInputs(paymentTransString, true);

		/*ListIterator<Integer> lengthIterator = listLengthList.listIterator();
		StringBuilder displayString = new StringBuilder();
		while (lengthIterator.hasNext()) {
			if (lengthIterator.hasPrevious()) {
				displayString.append(",");
			}
			displayString.append(lengthIterator.next());
		}
		System.out.println(displayString.toString());*/

		serverDate = outputDateFormat.format(lastDate) + "183705.485[-5:EST]";

		final StringBuilder xmlDisplayStringBuilder = new StringBuilder();
		xmlDisplayStringBuilder.append("OFXHEADER:100");
		xmlDisplayStringBuilder.append("\n" + "DATA:OFXSGML");
		xmlDisplayStringBuilder.append("\n" + "VERSION:102");
		xmlDisplayStringBuilder.append("\n" + "SECURITY:NONE");
		xmlDisplayStringBuilder.append("\n" + "ENCODING:USASCII");
		xmlDisplayStringBuilder.append("\n" + "CHARSET:1252");
		xmlDisplayStringBuilder.append("\n" + "COMPRESSION:NONE");
		xmlDisplayStringBuilder.append("\n" + "OLDFILEUID:NONE");
		xmlDisplayStringBuilder.append("\n" + "NEWFILEUID:NONE");
		xmlDisplayStringBuilder.append("\n" + "\n" + "<OFX>");
		xmlDisplayStringBuilder.append("\n" + createIndent(1) + "<SIGNONMSGSRSV1>");
		xmlDisplayStringBuilder.append("\n" + createIndent(2) + "<SONRS>");
		xmlDisplayStringBuilder.append("\n" + createIndent(3) + "<STATUS>");
		xmlDisplayStringBuilder.append("\n" + createIndent(4) + "<CODE>0");
		xmlDisplayStringBuilder.append("\n" + createIndent(4) + "<SEVERITY>INFO");
		xmlDisplayStringBuilder.append("\n" + createIndent(3) + "</STATUS>");
		xmlDisplayStringBuilder.append("\n" + createIndent(3) + "<DTSERVER>" + serverDate);
		xmlDisplayStringBuilder.append("\n" + createIndent(3) + "<LANGUAGE>ENG");
		xmlDisplayStringBuilder.append("\n" + createIndent(3) + "<FI>");
		xmlDisplayStringBuilder.append("\n" + createIndent(4) + "<ORG>Navy Federal Credit Union");
		xmlDisplayStringBuilder.append("\n" + createIndent(4) + "<FID>" + FID);
		xmlDisplayStringBuilder.append("\n" + createIndent(3) + "</FI>");
		xmlDisplayStringBuilder.append("\n" + createIndent(3) + "<INTU.BID>" + INTU_BID);
		xmlDisplayStringBuilder.append("\n" + createIndent(3) + "<INTU.USERID>" + userID);
		xmlDisplayStringBuilder.append("\n" + createIndent(2) + "</SONRS>");
		xmlDisplayStringBuilder.append("\n" + createIndent(1) + "</SIGNONMSGSRSV1>");
		xmlDisplayStringBuilder.append("\n" + createIndent(1) + "<CREDITCARDMSGSRSV1>");
		xmlDisplayStringBuilder.append("\n" + createIndent(2) + "<CCSTMTTRNRS>");
		xmlDisplayStringBuilder.append("\n" + createIndent(3) + "<TRNUID>0");
		xmlDisplayStringBuilder.append("\n" + createIndent(3) + "<STATUS>");
		xmlDisplayStringBuilder.append("\n" + createIndent(4) + "<CODE>0");
		xmlDisplayStringBuilder.append("\n" + createIndent(4) + "<SEVERITY>INFO");
		xmlDisplayStringBuilder.append("\n" + createIndent(3) + "</STATUS>");
		xmlDisplayStringBuilder.append("\n" + createIndent(3) + "<CCSTMTRS>");
		xmlDisplayStringBuilder.append("\n" + createIndent(4) + "<CURDEF>USD");
		xmlDisplayStringBuilder.append("\n" + createIndent(4) + "<CCACCTFROM>");
		xmlDisplayStringBuilder.append("\n" + createIndent(5) + "<ACCTID>4060412018010237");
		xmlDisplayStringBuilder.append("\n" + createIndent(4) + "</CCACCTFROM>");
		xmlDisplayStringBuilder.append("\n" + createIndent(4) + "<BANKTRANLIST>");
		xmlDisplayStringBuilder.append("\n" + createIndent(5) + "<DTSTART>").append(outputDateFormat.format(firstDate));
		xmlDisplayStringBuilder.append("\n" + createIndent(5) + "<DTEND>").append(outputDateFormat.format(lastDate));

		List<List<String>> transList = new ArrayList<List<String>>();
		transList.addAll(purchaseList);
		transList.addAll(paymentList);
		Collections.sort(transList, comparator);

		for (List<String> transaction : transList) {
			xmlDisplayStringBuilder.append("\n" + createIndent(6) + "<STMTTRN>");
			xmlDisplayStringBuilder.append("\n" + createIndent(7) + "<TRNTYPE>" + transaction.get(0));
			xmlDisplayStringBuilder.append("\n" + createIndent(7) + "<DTPOSTED>" + transaction.get(1));
			xmlDisplayStringBuilder.append("\n" + createIndent(7) + "<TRNAMT>" + transaction.get(2));
			xmlDisplayStringBuilder.append("\n" + createIndent(7) + "<FITID>" + transaction.get(3));
			xmlDisplayStringBuilder.append("\n" + createIndent(7) + "<NAME>" + transaction.get(4));
			xmlDisplayStringBuilder.append("\n" + createIndent(6) + "</STMTTRN>");
		}

		/*for (List<String> transaction : paymentList) {
			xmlDisplayStringBuilder.append("\n" + createIndent(6) + "<STMTTRN>");
			xmlDisplayStringBuilder.append("\n" + createIndent(7) + "<TRNTYPE>" + transaction.get(0));
			xmlDisplayStringBuilder.append("\n" + createIndent(7) + "<DTPOSTED>" + transaction.get(1));
			xmlDisplayStringBuilder.append("\n" + createIndent(7) + "<TRNAMT>" + transaction.get(2));
			xmlDisplayStringBuilder.append("\n" + createIndent(7) + "<FITID>" + transaction.get(3));
			xmlDisplayStringBuilder.append("\n" + createIndent(7) + "<NAME>" + transaction.get(4));
			xmlDisplayStringBuilder.append("\n" + createIndent(6) + "</STMTTRN>");
		}
		for (List<String> transaction : purchaseList) {
			xmlDisplayStringBuilder.append("\n" + createIndent(6) + "<STMTTRN>");
			xmlDisplayStringBuilder.append("\n" + createIndent(7) + "<TRNTYPE>" + transaction.get(0));
			xmlDisplayStringBuilder.append("\n" + createIndent(7) + "<DTPOSTED>" + transaction.get(1));
			xmlDisplayStringBuilder.append("\n" + createIndent(7) + "<TRNAMT>" + transaction.get(2));
			xmlDisplayStringBuilder.append("\n" + createIndent(7) + "<FITID>" + transaction.get(3));
			xmlDisplayStringBuilder.append("\n" + createIndent(7) + "<NAME>" + transaction.get(4));
			xmlDisplayStringBuilder.append("\n" + createIndent(6) + "</STMTTRN>");
		}*/

		xmlDisplayStringBuilder.append("\n" + createIndent(4) + "</BANKTRANLIST>");
		xmlDisplayStringBuilder.append("\n" + createIndent(4) + "<LEDGERBAL>");
		xmlDisplayStringBuilder.append("\n" + createIndent(5) + "<BALAMT>").append(newBalance);
		xmlDisplayStringBuilder.append("\n" + createIndent(5) + "<DTASOF>" + serverDate);
		xmlDisplayStringBuilder.append("\n" + createIndent(4) + "</LEDGERBAL>");
		xmlDisplayStringBuilder.append("\n" + createIndent(3) + "</CCSTMTRS>");
		xmlDisplayStringBuilder.append("\n" + createIndent(2) + "</CCSTMTTRNRS>");
		xmlDisplayStringBuilder.append("\n" + createIndent(1) + "</CREDITCARDMSGSRSV1>");
		xmlDisplayStringBuilder.append("\n" + "</OFX>");

		final JFrame frame = new JFrame();
		// Set the default close operation so the window won't close
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// Add window listener
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent arg0) {
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				// Display confirm dialog
				int confirmed = JOptionPane.showConfirmDialog(frame, "Are you sure you want to quit?", "Confirm Quit", JOptionPane.YES_NO_OPTION);

				// Close if user confirmed
				if (confirmed == JOptionPane.YES_OPTION) {
					// Close frame
					frame.dispose();
				}
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
			}

			@Override
			public void windowActivated(WindowEvent arg0) {
			}
		});
		JPanel panel = new JPanel();
		final JTextArea textInput = new JTextArea(xmlDisplayStringBuilder.toString(), 50, 90);
		JScrollPane scrollPane = new JScrollPane(textInput);
		panel.add(scrollPane);
		JButton buttonSave = new JButton("Save File");
		buttonSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new QFXFileFilter());
				int returnVal = fc.showSaveDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					String savePath = fc.getSelectedFile().getAbsolutePath();
					if (!savePath.endsWith(".qfx")) {
						savePath = savePath + ".qfx";
					}
					File file = new File(savePath);
					FileWriter fw = null;
					try {
						fw = new FileWriter(file);
						fw.write(xmlDisplayStringBuilder.toString());
					} catch (IOException e1) {
					} finally {
						if (fw != null) {
							try {
								fw.close();
							} catch (IOException e1) {
							}
						}
					}

				} else {
				}
			}
		});
		panel.add(buttonSave);
		JButton buttonRestart = new JButton("Next Statement");
		buttonRestart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
				transInputs();
			}
		});
		panel.add(buttonRestart);
		frame.add(panel);
		frame.setTitle("Quicken Output");
		frame.setSize(1100, 950);
		frame.show();
	}

	public class QFXFileFilter extends FileFilter {
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return false;
			}
			return f.getName().endsWith(".qfx");
		}

		public String getDescription() {
			return "Quicken files (*.qfx)";
		}
	}

	private List<List<String>> parseTransInputs(String transInputString, boolean isPayment) {
		final List<List<String>> transList = new ArrayList<List<String>>();
		String[] splitString = transInputString.split("\n");
		for (String rowOfInput : splitString) {
			if (rowOfInput.trim().matches(" ") || rowOfInput.trim().matches("")) {

			} else {
				boolean isItem = true;
				List<String> xmlItem = new ArrayList<String>();
				String[] transaction = rowOfInput.split("   ");
				List<String> transactionList = new ArrayList<String>();
				for (String item : transaction) {
					transactionList.add(item);
				}
				ListIterator<String> transactionIterator = transactionList.listIterator();
				while (transactionIterator.hasNext()) {
					String itemTrimmed = transactionIterator.next().trim();
					if (itemTrimmed.matches("") || itemTrimmed.matches(" ")) {
						transactionIterator.remove();
					} else {
						transactionIterator.set(itemTrimmed);
					}
				}

				if (transactionList.size() < 4) {
				} else {

					if (transactionList.size() > 6) {
						List<String> transactionListOriginal = new ArrayList<String>();
						for (String item : transactionList) {
							transactionListOriginal.add(item);
						}
						String newMiddle = "";
						String end = transactionListOriginal.get(transactionListOriginal.size() - 1);
						transactionList.remove(transactionList.size() - 1);
						transactionListOriginal.remove(transactionListOriginal.size() - 1);
						int size = transactionListOriginal.size();
						for (int i = 0; i < size; i++) {
							if (i > 2) {
								newMiddle = newMiddle + " " + transactionListOriginal.get(i);
								if (transactionList.size() > 3) {
									transactionList.remove(3);
								}
							}
						}
						transactionList.add(newMiddle);
						transactionList.add(end);
					}

					/*if (transactionList.size() == 7) {
						String one = transactionList.get(4);
						String two = transactionList.get(5);
						String price = transactionList.get(6);
						transactionList.remove(6);
						transactionList.set(4, one + " " + two);
						transactionList.set(5, price);
					}*/

					if (transactionList.size() == 5) {
						transactionList.add(4, "");
					}

					if (isPayment) {
						xmlItem.add("CREDIT");
					} else {
						xmlItem.add("DEBIT");
					}
					String dateString = "";
					String fitidDateString = "";
					try {
						Date inputDate = inputDateFormat.parse(transactionList.get(1));
						dateString = outputDateFormat.format(inputDate);
						fitidDateString = fitidOutputDF.format(inputDate);

						if (lastDate == null) {
							lastDate = inputDate;
						}
						if (firstDate == null) {
							firstDate = inputDate;
						}
						if (inputDate.after(lastDate)) {
							lastDate = inputDate;
						}
						if (inputDate.before(firstDate)) {
							firstDate = inputDate;
						}
					} catch (ParseException e1) {
						isItem = false;
					}
					xmlItem.add(dateString);

					String moneyString = "";
					try {
						String moneyInput = transactionList.get(5);
						moneyInput = moneyInput.replace("$", "").replace(",", "");
						if (moneyInput.endsWith("-")) {
							moneyInput = moneyInput.substring(0, moneyInput.length() - 1);
						}
						double moneyDouble = Double.parseDouble(moneyInput);
						/*double moneyDoubleFinal = 0.0;
						if (!isPayment) {
							moneyDoubleFinal = moneyDouble * -1.0;
						} else {
							moneyDoubleFinal = moneyDouble;
						}*/
						NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
						moneyString = currencyFormatter.format(moneyDouble).replace("(", "").replace(")", "").replace("$", "").replace(",", "");
						if (!isPayment) {
							moneyString = "-" + moneyString;
						}
					} catch (Exception e54) {

					}
					xmlItem.add(moneyString);

					xmlItem.add(transactionList.get(2).replace(" ", "") + transactionList.get(3) + transactionList.get(4) + fitidDateString);
					xmlItem.add(transactionList.get(3));

					transactionIterator = transactionList.listIterator();
					StringBuilder displayString = new StringBuilder();
					while (transactionIterator.hasNext()) {
						if (transactionIterator.hasPrevious()) {
							displayString.append("   |   ");
						}
						displayString.append(transactionIterator.next());
					}
					if (isItem) {
						// listLengthList.add(transactionList.size());
						// System.out.println(displayString.toString());
						transList.add(xmlItem);
					}
				}
			}
		}
		return transList;
	}
}
