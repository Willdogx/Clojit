import javax.swing.JFrame;
import javax.swing.JList;

class Test
{
    public static void main(String[] args)
    {
        Object[] listContent = new Object[] {
            "'Foo'",
            "'Bar'",
            "'Foobar'"
        };

        JList jlist = new JList<>(listContent);

        JFrame frame = new JFrame();
        frame.getContentPane().add(jlist);
        
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame.pack();
        frame.setVisible(true);
    }
}