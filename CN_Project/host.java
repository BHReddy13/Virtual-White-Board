import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

class host extends JFrame {
	private JComboBox cb;
	private JButton clear, send;
	private JPanel pan1, pan2, pan3, pan4;
	private JTextArea ta1;
	private JTextField tf1;
	private JLabel lab1;
	private JScrollPane sp1;
	private int x, y, port;
	private String color;
	private ArrayList<InetSocketAddress> guestAddresses;
	static int count = 0;

	host() {
		super("host");
		pan1 = new JPanel();
		pan1.setLayout(null);
		pan1.setBounds(0, 0, 1100, 40);
		pan1.setBackground(Color.ORANGE);
		pan2 = new JPanel();
		pan2.setLayout(null);
		pan2.setBounds(0, 40, 1100, 2000);
		pan2.setBackground(Color.BLACK);
// from here
		pan3 = new JPanel();
		pan3.setLayout(null);
		pan3.setBounds(1100, 0, 2000, 640);
		pan3.setBackground(Color.GREEN);
		pan4 = new JPanel();
		pan4.setLayout(null);
		pan4.setBackground(Color.ORANGE);
		pan4.setBounds(1100, 640, 1500, 800);
		tf1 = new JTextField("   ");
		send = new JButton("SEND");

		pan4.add(tf1);
		pan4.add(send);

		tf1.setBounds(10, 10, 360, 40);
		send.setBounds(100, 60, 200, 40);
		send.addActionListener(new action_event_listener());

		lab1 = new JLabel("   Messages from the Clients   ");
		ta1 = new JTextArea(400, 600);
		sp1 = new JScrollPane(ta1);

		pan3.add(lab1);
		pan3.add(sp1);
		pan3.add(ta1);

		lab1.setBounds(0, 0, 900, 40);
		ta1.setBounds(0, 40, 400, 600);
//to here
		cb = new JComboBox();
		cb.addItem("black");
		cb.addItem("red");
		cb.addItem("blue");
		cb.addItem("green");
		cb.addItem("eraser");
		cb.setEditable(false);
		cb.setBounds(50, 0, 70, 40);
		cb.addActionListener(new action_event_listener());
		clear = new JButton("clear");
		clear.setBounds(200, 0, 70, 40);
		clear.addActionListener(new action_event_listener());
		mouse_event_listener mouse_event = new mouse_event_listener();
		pan2.addMouseMotionListener(mouse_event);
		pan2.addMouseListener(mouse_event);
		pan1.add(cb);
		pan1.add(clear);
		setLayout(null);
		setResizable(false);
		setSize(1500, 1500);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		add(pan1);
		add(pan2);
		add(pan3);
		add(pan4);
		window_event_listener window_event_listener = new window_event_listener();
		addWindowListener(window_event_listener);
		color = "black";
		guestAddresses = new ArrayList<InetSocketAddress>(10);
		ServerThread server = new ServerThread();
		server.start();
		GuestKillerThread gkt = new GuestKillerThread();
		gkt.start();
	}

	class ServerThread extends Thread {
		public void run() {
			ServerSocket ss = null;
			while (ss == null) {
				try {
					ss = new ServerSocket(9789);
				} catch (IOException e) {
					System.out.println(e);
				}
			}
			Socket s = null;

			while (true) {
				try {
					System.out.println("listening for requests");
					s = ss.accept();
					guestAddresses.add((InetSocketAddress) s.getRemoteSocketAddress());
					System.out.println(guestAddresses.get(guestAddresses.size() - 1).getAddress());
				} catch (Exception e) {
					System.out.println(e);
				} finally {
					try {
						s.close();
					} catch (Exception e) {
						System.out.println(e);
					}
				}
			}
		}
	}

	class GuestKillerThread extends Thread {
		public void run() {
			while (true) {
				ServerSocket ss = null;
				Socket s = null;
				try {
					ss = new ServerSocket(10000);
					s = ss.accept();
					InetSocketAddress target = (InetSocketAddress) s.getRemoteSocketAddress();
					InetSocketAddress real_target = new InetSocketAddress(target.getAddress(), target.getPort() - 1);
					System.out.println(guestAddresses.remove(real_target));
					System.out.println("one guest exited");
				} catch (Exception e) {
					System.out.println(e);

				} finally {
					try {
						s.close();
						ss.close();
					} catch (Exception e) {
						System.out.println(e);
					}
				}
			}
		}
	}

	class ActionThread extends Thread {
		public void run() {
			Iterator<InetSocketAddress> it = guestAddresses.iterator();
			InetSocketAddress temp = null;
			setBackground(Color.WHITE);
			repaint();
			while (it.hasNext()) {
				temp = it.next();
				Socket s = null;
				DataOutputStream dout = null;
				try {
					s = new Socket(temp.getAddress(), temp.getPort());
					dout = new DataOutputStream(s.getOutputStream());
					dout.writeUTF("clear");
				} catch (Exception e) {
					System.out.println(e);
				} finally {
					try {

						s.close();
						dout.close();
					} catch (Exception e) {
						System.out.println(e);
					}
				}
			}
		}
	}

	class DrawingThread extends Thread {
		private int x1, y1, x2, y2;
		private Graphics gr;

		DrawingThread(Graphics g, int a, int b, int c, int d) {
			x1 = a;
			y1 = b;
			x2 = c;
			y2 = d;
			gr = g;
		}

		public void run() {
			if (color == "eraser") {
				gr.fillRect(x2 - 50, y2 - 50, 100, 100);
				Iterator<InetSocketAddress> it = guestAddresses.iterator();
				InetSocketAddress temp;
				while (it.hasNext()) {
					temp = it.next();
					Socket s = null;
					DataOutputStream dout = null;
					try {
						s = new Socket(temp.getAddress(), temp.getPort());
						dout = new DataOutputStream(s.getOutputStream());
						String encoded = x2 + "," + y2 + ":" + x1 + "," + y1 + ":" + color;
						dout.writeUTF(encoded);
						dout.flush();
					}

					catch (Exception e) {
						System.out.println(e);
					} finally {
						try {
							dout.close();
							s.close();
						} catch (Exception e) {
							System.out.println(e);
						}
					}
				}
			} else {
				gr.drawLine(x2, y2, x1, y1);
				x = x2;
				y = y2;
				Iterator<InetSocketAddress> it = guestAddresses.iterator();
				InetSocketAddress temp;
				while (it.hasNext()) {
					temp = it.next();
					Socket s = null;
					DataOutputStream dout = null;
					try {
						s = new Socket(temp.getAddress(), temp.getPort());
						dout = new DataOutputStream(s.getOutputStream());
						String encoded = x2 + "," + y2 + ":" + x1 + "," + y1 + ":" + color;
						dout.writeUTF(encoded);
						dout.flush();
					} catch (Exception e) {
						System.out.println(e);
					} finally {
						try {
							dout.close();
							s.close();
						} catch (Exception e) {
							System.out.println(e);
						}
					}
				}
			}
		}
	}

	class mouse_event_listener extends MouseAdapter {
		public void mousePressed(MouseEvent me) {
			x = me.getX();
			y = me.getY();
		}

		public void mouseDragged(MouseEvent me) {
			Graphics gr = ((JPanel) me.getSource()).getGraphics();
			int t1 = x;
			int u1 = y;
			switch (color) {
			case "black":
				gr.setColor(Color.BLACK);
				break;
			case "blue":
				gr.setColor(Color.BLUE);
				break;
			case "green":
				gr.setColor(Color.GREEN);
				break;
			case "red":
				gr.setColor(Color.RED);
				break;
			case "eraser":
				gr.setColor(Color.WHITE);
				break;
			}

			DrawingThread dthread = new DrawingThread(gr, t1, u1, me.getX(), me.getY());
			dthread.start();
		}
	}

	class action_event_listener implements ActionListener {
		synchronized public void actionPerformed(ActionEvent ae) {
			Socket s = null;
			DataOutputStream dout = null;
			/*if(ae.getActionCommand().equals("SEND")) {
				Iterator<InetSocketAddress> it = guestAddresses.iterator();
				InetSocketAddress tem;
				//String sd = tf1.getText();
				while (it.hasNext()) {
					tem = it.next();
					//Socket s = null;
					//DataOutputStream dout = null;
					try {
						s = new Socket(tem.getAddress(), tem.getPort());
						dout = new DataOutputStream(s.getOutputStream());
						String encoded = tf1.getText();
						ta1.append("HOST :"+encoded+"\n");
						dout.writeUTF(encoded);
						dout.flush();
					}

					catch (Exception e) {
						System.out.println(e);
					} finally {
						try {
							dout.close();
							s.close();
						} catch (Exception e) {
							System.out.println(e);
						}
					}
			}*/
			if (ae.getSource().hashCode() == send.hashCode()) {
				String st = tf1.getText();
				ta1.append("HOST :"+st+"\n");
				Iterator<InetSocketAddress> it = guestAddresses.iterator();
				InetSocketAddress tem;
				while (it.hasNext()) {
					tem = it.next();
					try {
						s = new Socket(tem.getAddress(), tem.getPort());
						dout = new DataOutputStream(s.getOutputStream());
						ta1.append("HOST :"+st+"\n");
						
						dout.writeUTF(st);
						dout.flush();
					}
					catch(Exception e) {
						
					}
				}
			}
			if (ae.getSource().hashCode() == cb.hashCode()) {
				JComboBox temp = (JComboBox) ae.getSource();
				color = (String) temp.getSelectedItem();
			} else {
				new ActionThread().start();
			}
		}
	}

	class window_event_listener extends WindowAdapter {
		public void windowIconified(WindowEvent w) {
			System.out.println("window has been minimized");
			Socket s = null;
			DataOutputStream dout = null;
			Iterator<InetSocketAddress> it = guestAddresses.iterator();
			InetSocketAddress temp = null;
			while (it.hasNext()) {
				temp = it.next();
				try {
					s = new Socket(temp.getAddress(), temp.getPort());
					dout = new DataOutputStream(s.getOutputStream());
					dout.writeUTF("clear");
					dout.flush();
				} catch (Exception e) {
					System.out.println(e);
				} finally {
					try {
						s.close();
						dout.close();
					} catch (Exception e) {
						System.out.println(e);
					}
				}
			}
		}

		public void windowClosing(WindowEvent w) {
			Socket s = null;
			DataOutputStream dout = null;
			Iterator<InetSocketAddress> it = guestAddresses.iterator();
			InetSocketAddress temp;
			while (it.hasNext()) {
				temp = it.next();
				try {
					s = new Socket(temp.getAddress(), temp.getPort());
					dout = new DataOutputStream(s.getOutputStream());
					dout.writeUTF("abort");
					dout.flush();
				} catch (Exception e) {
					System.out.println(e);
				} finally {
					try {
						s.close();
						dout.close();
					} catch (Exception e) {
						System.out.println(e);
					}
				}
			}
			guestAddresses.clear();
		}
	}

	  static public void main(String ar[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new host();
			}
		});
	}
}