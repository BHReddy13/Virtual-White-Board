import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
class SouFrame implements ActionListener
 {
 static JButton jb1;
 static JButton jb2;
 static JLabel jl1,jl2;
 static JTextField jtf;
 static JTextArea jtf1;
 static JScrollPane jsp;
 static JFrame jf;
 String dest_addr=" ";
 String Msg=" ";
 String conc=" ";
 String fin=" ";
 int st=0;
 int end=48;
 int len1=0;
 Socket soc;
 int split=0;
 InetAddress in1=InetAddress.getLocalHost();

 SouFrame() throws IOException
{
CreateFrame();
}
public void CreateFrame()
{
jf=new JFrame("Source");
Container cp=jf.getContentPane();
jl1=new JLabel("Destination Machine Name :");
jtf=new JTextField();
cp.setBackground(Color.pink);
jl2=new JLabel("Type the Message :");
jtf1=new JTextArea(10,10);
jb1=new JButton("SEND");
jb2=new JButton("CLEAR");
jsp=new JScrollPane(jtf1);
cp.setLayout(null);
cp.add(jl1);
cp.add(jtf);
cp.add(jl2);
cp.add(jsp);
cp.add(jb1);
jl1.setForeground(Color.blue);
jl2.setForeground(Color.blue);
jb1.setBorder(BorderFactory.createEtchedBorder(Color.yellow,Color.red));
jtf.setBorder(BorderFactory.createEtchedBorder(Color.black,Color.black));
jsp.setBorder(BorderFactory.createEtchedBorder(Color.black,Color.black));
jb1.addActionListener(this);
cp.add(jb2);
jb2.setBorder(BorderFactory.createEtchedBorder(Color.yellow,Color.red));
jb2.addActionListener(this);
jl1.setBounds(5,50,195,25);
jtf.setBounds(165,50,200,25);
jl2.setBounds(5,100,195,25);
jsp.setBounds(165,100,200,200);
jb1.setBounds(150,325,75,25);
jb2.setBounds(250,325,75,25);
jf.setVisible(true);
jf.setBounds(100,100,400,400);
jf.validate();
jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
}
public void actionPerformed(ActionEvent e)
{
if(e.getSource()==jb1)
 {
try
{

SendPacket();
 }
 catch(IOException e1)
 {
JOptionPane.showMessageDialog((Component) 
null,"InRouter Machine is Not Ready To Data Transfer","Click 
OK",JOptionPane.ERROR_MESSAGE);
}
}
else
 {
jtf.setText("");
jtf1.setText("");
 }
 }
public void SendPacket() throws IOException
{
try
{
 dest_addr=jtf.getText();
 Msg=jtf1.getText();
 if(((dest_addr.trim()).length())>0)
 {
 if(((Msg.trim()).length())>0)
 {
 
System.out.println("**********************"+jtf.getText()+"****************");

 soc=new Socket("192.168.0.6",7788);
 OutputStream out = soc.getOutputStream();
 st=0;
 end=48;
 conc=dest_addr+"`"+in1.getHostName()+"`";
 byte buffer[]=Msg.getBytes();
 int len=buffer.length;
 len1=len;
 if(len<=48)
 {
 fin=conc+Msg+"\n"+"null";
 byte buffer1[]=fin.getBytes();
 out.write(buffer1);
 }
 else
 {
fin=conc+Msg.substring(st,end)+"\n";
 byte buffer2[]=fin.getBytes();
 out.write(buffer2);
 Thread.sleep(1000);
 while(len1>48)
 {
 len1-=48;
 if(len1<=48)
 {
 

fin=conc+Msg.substring(end,len)+"\n"+"null";
 byte buffer3[]=fin.getBytes();
 out.write(buffer3);
 Thread.sleep(1000);
 }
else
 {
 split=end+48;
 fin=conc+Msg.substring(end,split)+"\n";
 end=split;
 byte buffer5[]=fin.getBytes();
 out.write(buffer5);
 Thread.sleep(1000);
 }
 }
 }
}
else
{
JOptionPane.showMessageDialog((Component) 
null,"There Is No Message To Send","Click OK",JOptionPane.INFORMATION_MESSAGE);
}
}
else
{
JOptionPane.showMessageDialog((Component) 
null,"Destination Machine Name is Missing","Click 
OK",JOptionPane.INFORMATION_MESSAGE);

}
 }
catch(UnknownHostException e)
{
JOptionPane.showMessageDialog((Component) null,"Check the 
InRouter Machine Name","Click OK",JOptionPane.INFORMATION_MESSAGE);
}
 catch(InterruptedException e1)
 {
}
}
}
class CFRSource
 {
 public static void main(String args[])throws IOException
 {
 SouFrame sf=new SouFrame();
 }
 }
InRouter Module
import javax.swing.*;
import java.net.*;
import java.io.*;

import java.awt.*;
import java.util.Vector;
import javax.swing.UIManager;
class In_Frame //implements Runnable
{
JFrame Ing_fra;
Container cp1;
int tot;
static JTextArea Ing_data=new JTextArea();
JTextField InTxt;
static JTextField OutTxt=new JTextField();
JLabel InPac,OutPac;
String instring="";
static int length=0;
static int length1=0;
static String s="";
static String s1="";
static int I=0;
String fin="";
static String dest="";
static String sou="";
boolean sta=true;
static int inp=0,outp=0;
JScrollPane jsp;
BufferedReader in1;
OutputStream out;

int readcnt=0;
static String text;
String egg="";
static String text1;
static Vector msg=new Vector();
static Vector len=new Vector();
static Vector des=new Vector();
static Vector sour=new Vector();
char chstr[]=new char[512];
long l;
Send sen=new Send(this);
String inf="com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
In_Frame()
{
}
public void dis_ing_fra()
{
Ing_fra=new JFrame("In Router");
cp1=Ing_fra.getContentPane();
cp1.setLayout(null);
InPac=new JLabel("INCOMING PACKETS :");
OutPac=new JLabel("OUTGOING PACKETS :");
InTxt=new JTextField();
InTxt.setText("0");
OutTxt.setText("0");

InPac.setForeground(Color.blue);
OutPac.setForeground(Color.blue);
Ing_data.setEditable(false);
InTxt.setEditable(false);
OutTxt.setEditable(false);
Ing_data.setBorder(BorderFactory.createEtchedBorder(Color.black,Color.black));
InTxt.setBorder(BorderFactory.createEtchedBorder(Color.black,Color.black));
OutTxt.setBorder(BorderFactory.createEtchedBorder(Color.black,Color.black));
jsp=new JScrollPane(Ing_data);
cp1.add(jsp);
cp1.add(InPac);
cp1.add(InTxt);
cp1.add(OutPac);
cp1.add(OutTxt);
jsp.setBounds(5,5,470,400);
InPac.setBounds(5,425,130,25);
InTxt.setBounds(125,425,75,25);
OutPac.setBounds(250,425,150,25);
OutTxt.setBounds(375,425,75,25);
Ing_fra.setBounds(5,5,500,500);
Ing_fra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
try
{
UIManager.setLookAndFeel(inf);
SwingUtilities.updateComponentTreeUI(Ing_fra);

}
catch(Exception e)
{
e.printStackTrace();
}
Ing_fra.setVisible(true);
}
public void dis_ing_data()
{
try
{
ServerSocket ss=new ServerSocket(7788);
while(true)
{
System.out.println("waiting");
Socket soc=ss.accept();
System.out.println("Connected");
sta=true;
while(sta)
{
in1= new BufferedReader(new 
InputStreamReader(soc.getInputStream()));
out=soc.getOutputStream();
display();
}
}

}
catch(IOException e)
{
e.printStackTrace();
}
}
public void add()
{
text+="Source :"+sou+"\nMessage :"+s.trim()+"\nDestination 
:"+dest+"\n";
 Ing_data.setText(text);
 }
 public void incoming(int in)
 {
InTxt.setText(in+"");
}
public void outgoing(int out)
{
OutTxt.setText(out+"");
}
public void display()
{
try
{

while(true)
{
readcnt=in1.read(chstr);
if(readcnt <=0)
 {
 continue;
}
 else
 {
 break;
}
}
instring =new String(chstr, 0, readcnt);
 msg.add(instring);
 I++;
if(!instring.endsWith("null"))
{
length=instring.length();
 length1=0;
 len.add(length+"");
 for(int l=0;l<length;l++)
 {
 if((instring.charAt(l))=='`')
 {
 dest=instring.substring(0,l);
 des.add(dest);

 s1=instring.substring(l+1,length);
 l=length+1;
 length1=s1.length();
 }
 }
 for(int l=0;l<length1;l++)
 {
if((s1.charAt(l))=='`')
 {
 sou=s1.substring(0,l);
 sour.add(sou);
 s=s1.substring(l+1,length1);
 l=length1+1;
 }
}
add();
inp++;
incoming(inp);
 }
 else
 {
sta=false;
length=instring.length()-4;
length1=0;
len.add(length+"");
for(int l=0;l<length;l++)
{

if((instring.charAt(l))=='`')
 {
dest=instring.substring(0,l);
des.add(dest);
s1=instring.substring(l+1,length);
l=length+1;
length1=s1.length();
 }
 }
 for(int l=0;l<length1;l++)
{
if((s1.charAt(l))=='`')
{
sou=s1.substring(0,l);
sour.add(sou);
 s=s1.substring(l+1,length1);
 l=length1+1;
}
}
add();
inp++;
incoming(inp);
}
}
catch(IOException e)
{

e.printStackTrace();
}
}
}
class A extends Thread
{
In_Frame i;
A(In_Frame obj1)
{
i=obj1;
java.util.Timer t=new java.util.Timer();
if((i.msg.size())>0)
{
t.schedule(i.sen,10000);
}
else
{
t.schedule(i.sen,10000,30000);
}
try
{
Thread.sleep(1000);
}
catch(InterruptedException e)
{
e.printStackTrace();

}
}
}
class B extends Thread
{
In_Frame i1;
B(In_Frame obj1)
{
i1=obj1;
try
{
Leaky l=new Leaky(i1);
java.util.Timer t1=new java.util.Timer();
t1.schedule(l,10000,1000);
}
catch(Exception e){}
}
}
class InRouter
{
public static void main(String args[])
{
try
{
In_Frame obj=new In_Frame();

obj.dis_ing_fra();
//Fin f=new Fin();
A a=new A(obj);
Back b=new Back(obj);
RateControl rc=new RateControl(obj);
obj.dis_ing_data();
}
catch(Exception e){}
}
}

