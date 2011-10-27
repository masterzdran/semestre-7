import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public final class ConfinementDemoForm extends JFrame {
	
	private final JLabel message = new JLabel();
	private final JButton doIt = new JButton("Do it!");
	
	public ConfinementDemoForm(){
		setLayout();
		setBehaviour();
	}
	
	public void setLayout(){
		this.add(message, BorderLayout.CENTER);
		JPanel panel = new JPanel();
		panel.add(doIt);
		this.add(panel, BorderLayout.SOUTH);
		setSize(500,250);
	}
	
	public void setBehaviour(){
		doIt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				doIt.setEnabled(false);
				message.setText("Doing it...");
				//new SwingWorker<String, Object>() {
				new SimpleBackgroundworker() {
					public Object doInBackground(){
						try {
							Thread.sleep(10000);
						} catch(InterruptedException ie){
							
						}
						return "Done!";
					}

					
					public void done(){
						Object result;
						try {
							result = get();
						} catch(Exception e){
						}
						message.setText("Done!");
						doIt.setEnabled(true);
					}
					
				}.execute();
				
				
/*				new Thread(new Runnable() {
					public void run() {
						// take your time
						try {
							Thread.sleep(10000);
						} catch(InterruptedException ie){
							
						}
						EventQueue.invokeLater(new Runnable() {
							public void run() {
								message.setText("Done!");
								doIt.setEnabled(true);								
							}
						});
					
					}
				}).start();
*/
			}
		});
	}


	public static void main(String[] args) {

		JFrame frame = new ConfinementDemoForm();
		frame.setVisible(true);
		System.out.println("Main ending...");

	}

}
