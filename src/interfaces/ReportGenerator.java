package interfaces;
import model.Booking;
public interface ReportGenerator {

   
    boolean generateReport(String reportType, Object data);


    boolean logBooking(Booking booking);

  
}
