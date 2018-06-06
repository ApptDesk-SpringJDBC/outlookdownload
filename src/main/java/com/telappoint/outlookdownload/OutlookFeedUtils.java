package com.telappoint.outlookdownload;

import java.util.GregorianCalendar;
import java.util.List;
import org.apache.log4j.Logger;
import com.moyosoft.connector.com.ComponentObjectModelException;
import com.moyosoft.connector.exception.LibraryNotFoundException;
import com.moyosoft.connector.ms.outlook.Outlook;
import com.moyosoft.connector.ms.outlook.appointment.BusyStatus;
import com.moyosoft.connector.ms.outlook.appointment.OutlookAppointment;
import com.moyosoft.connector.ms.outlook.folder.FolderType;
import com.moyosoft.connector.ms.outlook.folder.OutlookFolder;
import com.moyosoft.connector.ms.outlook.item.ItemsIterator;
import com.moyosoft.connector.ms.outlook.item.OutlookItem;
import com.moyosoft.connector.ms.outlook.meeting.MeetingStatus;
import com.telappoint.outlookdownload.constants.Constants;
import com.telappoint.outlookdownload.model.AppointmentData;

public class OutlookFeedUtils implements Constants {
	private static Logger logger = Logger.getLogger(OutlookFeedUtils.class);

	public void saveDataToOutLook(List<AppointmentData> outlookApptList) {
		Outlook outlook = null;
		try {
			outlook = new Outlook();
			StringBuilder body = null;
			for (AppointmentData apptData : outlookApptList) {
				try {
					if (apptData.getApptType() == LOOKUP_APPT_TYPE_MAKE_APPT) {
						OutlookFolder calendar = outlook.getDefaultFolder(FolderType.CALENDAR);
						OutlookAppointment appointment = new OutlookAppointment(calendar);
						appointment.setSubject("Meeting with " + apptData.getFirstName() + " " + apptData.getLastName());
						GregorianCalendar apptDateTime = Utils.formatSqlStringToGC(apptData.getDateTime());
						appointment.setStart(apptDateTime.getTime());
						appointment.setDuration(apptData.getDuration());

						body = new StringBuilder();
						body.append("Degree Program :" + apptData.getLocationName() + "\r\n");
						body.append("StudentID :" + apptData.getAccountNumber() + "\r\n");
						body.append("Student Name :" + apptData.getFirstName() + " " + apptData.getLastName() + "\r\n");
						body.append("Phone :" + Utils.formatPhoneNumber(apptData.getHomePhone()) + "\r\n");
						body.append("Email :" + apptData.getEmail() + "\r\n");
						body.append("Student Notes :" + "\r\n");
						body.append(apptData.getComments());
						
						appointment.setBody(body.toString());
						appointment.setLocation(apptData.getServiceName());
						appointment.setBusyStatus(BusyStatus.BUSY);
						appointment.setReminderMinutesBeforeStart(15);
						appointment.setReminderPlaySound(true);
						appointment.setMeetingStatus(MeetingStatus.MEETING);
						appointment.save();
					} else if (apptData.getApptType() == LOOKUP_APPT_TYPE_CANCEL_APPT) {
						OutlookFolder folder = outlook.getDefaultFolder(FolderType.CALENDAR);
						GregorianCalendar apptDateTime = Utils.formatSqlStringToGC(apptData.getDateTime());
						String dateStr = Utils.formatGCDateToMMDDYYYY_HHMMAMPM(apptDateTime);
						String filter = "[Start] = '" + dateStr + "' ";
						ItemsIterator iterator = folder.getItems().findItems(filter, true);

						while (iterator.hasNext()) {
							OutlookItem item = iterator.nextItem();
							if (item != null && item.getType().isAppointment()) {
								OutlookAppointment appointment = (OutlookAppointment) item;
								if (appointment.getSubject().contains(apptData.getFirstName()) && appointment.getSubject().contains(apptData.getLastName())
										&& appointment.getBody().contains(apptData.getAccountNumber())) {
									logger.info("Deleting Appointment for " + " Meeting with " + apptData.getFirstName() + " " + apptData.getLastName());
									logger.info("Subject: " + appointment.getSubject());
									logger.info("Location: " + appointment.getLocation());
									logger.info("Start: " + appointment.getStart());
									logger.info("End: " + appointment.getEnd());
									appointment.delete();
								}
							}
						}
					}
				} catch (Exception e) {
					apptData.setOutlookSyncStatus(false);
					logger.error("Error while loading the appt data: " + apptData.toString());
					logger.error("Error while appt loading into outlook. " + e, e);
				}
			}
		} catch (ComponentObjectModelException ex) {
			logger.error("Error: " + ex.toString(), ex);
		} catch (LibraryNotFoundException le) {
			logger.error("Error: " + le.toString(), le);
		} finally {
			if (!outlookApptList.isEmpty()) {
				outlook.dispose();
			}
		}
	}
}