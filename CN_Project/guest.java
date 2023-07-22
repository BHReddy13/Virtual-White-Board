import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class guest extends JFrame {
	private int serverPort;
	private String ip_address;
	private boolean stop_signal;
//
	private JPanel pan2, pan4;
	private JLabel l1;
	private JTextArea ta1;
	private JScrollPane sp1;
	private JTextField tf1;
	private JButton send;

//
	guest() throws Exception {
		super("guest");
		stop_signal = false;
		JPanel pan = new JPanel();
		pan.setBounds(0, 0, 2000, 2000);

		pan.setBackground(Color.BLACK);
//
		pan.setLayout(null);
		pan2 = new JPanel();
		pan2.setLayout(null);
		pan2.setBounds(1100, 0, 1500, 1500);
		pan2.setBackground(Color.GREEN);
		pan.add(pan2);
		l1 = new JLabel("   CHAT BOX   ");
		l1.setBounds(10, 10, 700, 40);
		pan2.add(l1);
		pan4 = new JPanel();
		pan4.setLayout(null);
		pan4.setBackground(Color.ORANGE);
		pan4.setBounds(0, 640, 1500, 800);
		pan2.add(pan4);
		ta1 = new JTextArea(400, 600);
		sp1 = new JScrollPane(ta1);
		ta1.setBounds(0, 40, 400, 600);
		pan2.add(ta1);

		tf1 = new JTextField("   enter the message  ");
		send = new JButton("SEND");
		tf1.setBounds(10, 10, 360, 40);
		send.setBounds(100, 60, 200, 40);
		pan4.add(tf1);
		pan4.add(send);
//
		setSize(1500, 1500);
		setVisible(true);
		setLayout(null);
		setBackground(Color.WHITE);
		setContentPane(pan);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		addWindowListener(new window_event_listener());
		RegisterThread reg_thread = new RegisterThread();
		reg_thread.start();
		reg_thread.join();
		DrawingThread drawer = new DrawingThread();
		drawer.start();
	}

	class RegisterThread extends Thread {
		public void run() {
			boolean registered = false;
			boolean connected = false;
			Socket initiate = null;
			System.out.print("enter the ip address of the host:");
			ip_address = new java.util.Scanner(System.in).next();
			System.out.print("enter the port through which the registration has to be done :");
			serverPort = new java.util.Scanner(System.in).nextInt();
			while (registered == false) {
				try {
					initiate = new Socket();
					initiate.bind(new InetSocketAddress(serverPort));
					while (connected == false) {
						try {
							System.out.println(initiate.isClosed() + "1");
							initiate.connect(new InetSocketAddress(InetAddress.getByName(ip_address), 9789));
							System.out.println(initiate.isConnected() + "2");
							connected = true;
						} catch (Exception e) {
							System.out.println(e);
						}
					}
					registered = true;
					System.out.println("registered succesfully");
				} catch (BindException e) {
					System.out.print("error:");
					System.out.println(e);
					System.out.print("enter another port number :");
					serverPort = new java.util.Scanner(System.in).nextInt();
				} catch (ConnectException e) {
					System.out.println(e);
				} catch (Exception e) {
					System.out.println(e);
				} finally {
					try {
						initiate.close();
					} catch (Exception f) {
						System.out.println(f);
					}
				}
			}
		}
	}

	class DrawingThread extends Thread {
		private JLabel lable;

		DrawingThread() {
			stop_signal = false;
		}

		public void run() {
			while (true && stop_signal != true) {
				ServerSocket ss = null;
				Socket s = null;
				DataInputStream din = null;
				try {
					ss = new ServerSocket(serverPort);
					s = ss.accept();
					din = new DataInputStream(s.getInputStream());
					String encoded = (String) din.readUTF();
					System.out.println(encoded);
					if (encoded.equals("clear")) {
						getContentPane().repaint();
					} else if (encoded.equals("abort")) {
						System.out.println("meet ended");
						stop_signal = true;
					}else if(encoded.charAt(0)=='H'){
						ta1.append(encoded+"\n");
						System.out.println(encoded);
					}
					else {
						int index1 = encoded.indexOf(":");
						String first_point = encoded.substring(0, encoded.indexOf(":"));
						String second_point = encoded.substring(index1 + 1, encoded.indexOf(":", index1 + 1));
						int index2 = encoded.indexOf(":", index1 + 1);
						String color = encoded.substring(index2 + 1, encoded.length());
						int x1 = Integer.parseInt(first_point.substring(0, first_point.indexOf(",")));
						int y1 = Integer
								.parseInt(first_point.substring(first_point.indexOf(",") + 1, first_point.length()));
						int x2 = Integer.parseInt(second_point.substring(0, second_point.indexOf(",")));
						int y2 = Integer
								.parseInt(second_point.substring(second_point.indexOf(",") + 1, second_point.length()));
						y1 += 70;
						y2 += 70;
						Graphics gr = getContentPane().getGraphics();
						switch (color) {
						case "eraser":
							gr.setColor(Color.WHITE);
							break;
						case "red":
							gr.setColor(Color.RED);
							break;
						case "green":
							gr.setColor(Color.GREEN);
							break;
						case "blue":
							gr.setColor(Color.BLUE);
							break;
						case "black":
							gr.setColor(Color.BLACK);
							break;
						}
						if (color.equals("eraser")) {
							gr.fillRect(x1 - 50, y1 - 50, 100, 100);
						} else {
							gr.drawLine(x1, y1, x2, y2);
						}
					}
				} catch (Exception e) {
					System.out.println(e);
				} finally {
					try {
						s.close();
						ss.close();
						din.close();
					} catch (Exception e) {
						System.out.println(e);
					}
				}
			}
			if (stop_signal == true) {
				lable = new JLabel("host has ended the meeting");
				lable.setBounds(500, 50, 2000, 30);
				lable.setFont(new Font("Serif", Font.PLAIN, 30));
				getContentPane().repaint();
				getContentPane().add(lable);
				System.out.println("host has ended the meeting");
			}
		}
	}

	class window_event_listener extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			Socket s = null;
			if (stop_signal != true) {
				try {
					s = new Socket();
//we assign for every session the user initites two sequential ports will be reserved
					s.bind(new InetSocketAddress(serverPort + 1));
					s.connect(new InetSocketAddress(ip_address, 10000));
				} catch (Exception e) {
					System.out.println(e);
				} finally {
					try {
						s.close();
					} catch (Exception e) {
						System.out.println(e);
					}
				}
			} else if (stop_signal == true) {
			}
		}
	}

	static public void main(String ar[]) throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new guest();
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		});
	}
}