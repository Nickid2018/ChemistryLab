package com.github.nickid2018.chemistrylab.crashreport;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ErrorSwingWindow extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5801308345027349622L;

	private boolean detailed = false;

	/**
	 * Create the dialog.
	 */
	public ErrorSwingWindow(Throwable error, String crashfile) {
		getContentPane().setBackground(UIManager.getColor("Button.background"));
		setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 12));
		setTitle("Error");
		setResizable(false);
		setModal(true);
		setBounds(100, 100, 600, 135);
		getContentPane().setLayout(null);

		JLabel errorlabel = new JLabel("An error has occured.");
		errorlabel.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 12));
		errorlabel.setHorizontalAlignment(SwingConstants.LEFT);
		errorlabel.setBounds(10, 10, 374, 34);
		errorlabel.setIcon(new ImageIcon(ErrorSwingWindow.class.getResource("/assets/textures/gui/swing_error.png")));
		getContentPane().add(errorlabel);

		JTextField crashreportlabel = new JTextField("A crash report has been saved in " + crashfile);
		crashreportlabel.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 12));
		crashreportlabel.setEditable(false);
		crashreportlabel.setBorder(new EmptyBorder(0, 0, 0, 0));
		crashreportlabel.setBounds(48, 45, 536, 15);
		getContentPane().add(crashreportlabel);

		JTextArea crash = new JTextArea(ErrorUtils.asStack(error));
		crash.setEditable(false);
		crash.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 12));

		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(crash);
		scroll.setBounds(48, 70, 536, 445);

		JButton shutdownbutton = new JButton("Shutdown Program");
		shutdownbutton.addActionListener(e -> System.exit(-1));
		shutdownbutton.setMnemonic('S');
		shutdownbutton.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 12));
		shutdownbutton.setBounds(48, 70, 145, 23);
		getContentPane().add(shutdownbutton);

		JButton detailbutton = new JButton("Details>>");
		detailbutton.addActionListener(e -> {
			detailed = !detailed;
			detailbutton.setText(detailed ? "<<Simple" : "Details>>");
			setSize(600, detailed ? 600 : 135);
			shutdownbutton.setBounds(48, detailed ? 535 : 70, 145, 23);
			detailbutton.setBounds(407, detailed ? 535 : 70, 145, 23);
			if (detailed)
				getContentPane().add(scroll);
			else
				getContentPane().remove(scroll);
		});
		detailbutton.setMnemonic('D');
		detailbutton.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 12));
		detailbutton.setBounds(407, 70, 145, 23);
		getContentPane().add(detailbutton);
	}
}
