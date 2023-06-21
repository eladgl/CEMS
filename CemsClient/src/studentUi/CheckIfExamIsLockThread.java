package studentUi;

import Client.CEMSClientUI;
import entities.ConductExamIdWithScreenTypeObject;
import entities.ExamType;
import javafx.application.Platform;
import utilities.CONSTANTS;
import utilities.Message;

/**
 * This class is used inside the Manual Exam and the Computerized Exam Controllers.
 * This thread checks every 15 seconds whether the exam has been locked by the lecturer or not.
 * It sends a message to the server to check for the exam lock status and handles the alert if the exam is locked.
 * @author razi
 *
 */
public class CheckIfExamIsLockThread extends Thread {
	private int conduct_exam_id;
	private ExamType exam_type;
	public volatile boolean running = true; //all threads see the most up-to-date value.
	/**
	 * Constructs a new CheckIfExamIsLockThread with the specified conduct exam ID and exam type.
	 * 
	 * @param conduct_exam_id the ID of the conducted exam
	 * @param exam_type the type of the exam (Manual or Computerized)
	 */
	public CheckIfExamIsLockThread(int conduct_exam_id, ExamType exam_type) {
		this.conduct_exam_id = conduct_exam_id;
		this.exam_type = exam_type;
	}
	/**
	 * Starts the thread's execution. This method checks the exam lock status every 15 seconds
	 * by sending a message to the server. If the exam is locked, it triggers an alert in the
	 * corresponding controller.
	 * <p>
	 * This method overrides the {@code run()} method in the {@code Thread} class.
	 * </p>
	 * It also referes each type of flag and checks if it was changed. The flag that
	 * it checks is changed in the file CEMCClient.java - in this case: case CONSTANTS.responseForCheckForExamLock
	 */
	@Override
	public void run() {
		try {
			Thread.sleep(5000); // Wait for 5 seconds before starting to check for time change
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		while(running) {
			ConductExamIdWithScreenTypeObject data = new ConductExamIdWithScreenTypeObject(conduct_exam_id, exam_type);
			try {
				CEMSClientUI.chat.accept(new Message(CONSTANTS.CheckForExamLock, data));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			if(! running)
				break;

			try {
				Thread.sleep(15000); // Wait for 15 seconds
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if(ManualTestExam.trigger_exam_is_locked_alert) {
			if(this.exam_type == ExamType.MANUAL) {
				ManualTestExam.trigger_exam_is_locked_alert = false;
				ManualTestExam manual = new ManualTestExam();
				Platform.runLater(() -> {
					manual.createAlertForExamIsLocked();
					
				});
			}else {
				
			}
		}
		if(ComputerizedTestScreenController.trigger_exam_is_locked_alert) {
			if(this.exam_type == ExamType.COMPUTERIZED) {
				ComputerizedTestScreenController.trigger_exam_is_locked_alert = false;
				ComputerizedTestScreenController computerized = ComputerizedTestScreenController.getInstance();
				Platform.runLater(() -> {
					computerized.createAlertForExamIsLocked();
					
				});
			}else {
				// computerzied Action
			}
		}
		
	}
	/**
	 * Stops the execution of the thread.
	 */
	public void stopRunning() {
		running = false;
	}
}