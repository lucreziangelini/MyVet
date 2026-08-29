package it.ispwproject.myvet.service;

/**
 * Servizio per l'invio di notifiche email tramite SendGrid.
 *
 * Gestisce la comunicazione con il servizio email esterno,
 * mantenendo separata la logica applicativa dall'invio delle email.
 *
 * In futuro può essere esteso per supportare altri canali,
 * come SMS e notifiche push.
 */

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import it.ispwproject.myvet.bean.BookingResponseBean;
import it.ispwproject.myvet.bean.ActivityBean;
import it.ispwproject.myvet.exception.NotificationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class NotificationService {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input =
                     NotificationService.class
                             .getClassLoader()
                             .getResourceAsStream("db.properties")) {

            if (input == null) {
                throw new ExceptionInInitializerError(
                        "Impossibile trovare db.properties"
                );
            }

            PROPERTIES.load(input);

        } catch (IOException e) {
            throw new ExceptionInInitializerError(
                    "Impossibile caricare db.properties"
            );
        }
    }

    private static final String API_KEY =
            PROPERTIES.getProperty("SENDGRID_API_KEY");

    private static final String FROM_EMAIL =
            PROPERTIES.getProperty("SENDGRID_FROM_EMAIL");

    private static final String TEMPLATE_CONFIRMATION_OWNER =
            PROPERTIES.getProperty(
                    "SENDGRID_TEMPLATE_CONFIRMATION_OWNER"
            );

    private static final String TEMPLATE_CANCELLATION_OWNER =
            PROPERTIES.getProperty(
                    "SENDGRID_TEMPLATE_CANCELLATION_OWNER"
            );

    private static final String TEMPLATE_CONFIRMATION_VETERINARIAN =
            PROPERTIES.getProperty(
                    "SENDGRID_TEMPLATE_CONFIRMATION_VETERINARIAN"
            );

    private static final String TEMPLATE_CANCELLATION_VETERINARIAN =
            PROPERTIES.getProperty(
                    "SENDGRID_TEMPLATE_CANCELLATION_VETERINARIAN"
            );

    private static final String TEMPLATE_NEW_CARE_ACTIVITY =
            PROPERTIES.getProperty(
                    "SENDGRID_TEMPLATE_NEW_CARE_ACTIVITY"
            );

    private static final String KEY_OWNER_NAME = "ownerName";
    private static final String KEY_VETERINARIAN_NAME = "veterinarianName";
    private static final String KEY_PET_NAME = "petName";
    private static final String KEY_CLINIC_ADDRESS = "clinicAddress";
    private static final String KEY_DATE = "date";
    private static final String KEY_START_TIME = "startTime";
    private static final String KEY_END_TIME = "endTime";
    private static final String KEY_ACTIVITY_TITLE = "activityTitle";
    private static final String KEY_ACTIVITY_DESCRIPTION =
            "activityDescription";

    private NotificationService() {
        // Prevents instantiation
    }

    public static void sendBookingConfirmationToOwner(
            String toEmail,
            BookingResponseBean booking) throws NotificationException {

        Personalization personalization =
                buildBookingPersonalization(toEmail, booking);

        sendTemplateEmail(
                TEMPLATE_CONFIRMATION_OWNER,
                personalization
        );
    }

    public static void sendBookingConfirmationToVeterinarian(
            String toEmail,
            BookingResponseBean booking) throws NotificationException {

        Personalization personalization =
                buildBookingPersonalization(toEmail, booking);

        sendTemplateEmail(
                TEMPLATE_CONFIRMATION_VETERINARIAN,
                personalization
        );
    }

    public static void sendBookingCancellationToOwner(
            String toEmail,
            BookingResponseBean booking) throws NotificationException {

        Personalization personalization =
                buildBookingPersonalization(toEmail, booking);

        sendTemplateEmail(
                TEMPLATE_CANCELLATION_OWNER,
                personalization
        );
    }

    public static void sendBookingCancellationToVeterinarian(
            String toEmail,
            BookingResponseBean booking) throws NotificationException {

        Personalization personalization =
                buildBookingPersonalization(toEmail, booking);

        sendTemplateEmail(
                TEMPLATE_CANCELLATION_VETERINARIAN,
                personalization
        );
    }

    public static void sendNewCareActivity(
            String toEmail,
            ActivityBean activity) throws NotificationException {

        if (activity == null) {
            throw new NotificationException(
                    "L'attività di cura non può essere nulla"
            );
        }

        Personalization personalization = new Personalization();
        personalization.addTo(new Email(toEmail));

        personalization.addDynamicTemplateData(
                KEY_PET_NAME,
                activity.getPet().getName()
        );

        personalization.addDynamicTemplateData(
                KEY_VETERINARIAN_NAME,
                activity.getVeterinarian().getFullName()
        );

        personalization.addDynamicTemplateData(
                KEY_ACTIVITY_TITLE,
                activity.getTitle()
        );

        personalization.addDynamicTemplateData(
                KEY_ACTIVITY_DESCRIPTION,
                activity.getDescription()
        );

        sendTemplateEmail(
                TEMPLATE_NEW_CARE_ACTIVITY,
                personalization
        );
    }

    private static Personalization buildBookingPersonalization(
            String toEmail,
            BookingResponseBean booking) throws NotificationException {

        if (booking == null) {
            throw new NotificationException(
                    "La prenotazione non può essere nulla"
            );
        }

        Personalization personalization = new Personalization();
        personalization.addTo(new Email(toEmail));

        personalization.addDynamicTemplateData(
                KEY_OWNER_NAME,
                booking.getPetOwner().getFullName()
        );

        personalization.addDynamicTemplateData(
                KEY_VETERINARIAN_NAME,
                booking.getVeterinarian().getFullName()
        );

        personalization.addDynamicTemplateData(
                KEY_PET_NAME,
                booking.getPet().getName()
        );

        personalization.addDynamicTemplateData(
                KEY_CLINIC_ADDRESS,
                booking.getVeterinarian().getClinicAddress()
        );

        personalization.addDynamicTemplateData(
                KEY_DATE,
                booking.getTimeSlot().getDate().toString()
        );

        personalization.addDynamicTemplateData(
                KEY_START_TIME,
                booking.getTimeSlot().getStartTime().toString()
        );

        personalization.addDynamicTemplateData(
                KEY_END_TIME,
                booking.getTimeSlot().getEndTime().toString()
        );

        return personalization;
    }

    private static void sendTemplateEmail(
            String templateId,
            Personalization personalization)
            throws NotificationException {

        if (API_KEY == null || API_KEY.isBlank()) {
            throw new NotificationException(
                    "Chiave API SendGrid non configurata"
            );
        }

        if (FROM_EMAIL == null || FROM_EMAIL.isBlank()) {
            throw new NotificationException(
                    "Email mittente SendGrid non configurata"
            );
        }

        if (templateId == null || templateId.isBlank()) {
            throw new NotificationException(
                    "Template SendGrid non configurato"
            );
        }

        Mail mail = new Mail();
        mail.setFrom(new Email(FROM_EMAIL, "MyVet"));
        mail.setTemplateId(templateId);
        mail.addPersonalization(personalization);

        SendGrid sendGrid = new SendGrid(API_KEY);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            if (response.getStatusCode() >= 400) {
                throw new NotificationException(
                        "Errore durante l'invio dell'email (status "
                                + response.getStatusCode()
                                + "): "
                                + response.getBody()
                );
            }

        } catch (IOException e) {
            throw new NotificationException(
                    "Errore durante l'invio dell'email: "
                            + e.getMessage(),
                    e
            );
        }
    }
}
