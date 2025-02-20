package com.sena.lcdsena.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class emailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public String enviarCorreoBienvenida(String destinatario, String nombre_usuario) {
        try {
            String asunto = "¡Bienvenid@  " + nombre_usuario + "  a TuTurismo Neiva!";
            String cuerpo = ""
                    + "<body style=\"font-family: Arial, sans-serif; \">\r\n"
                    + "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/boxicons/2.1.0/dist/boxicons.js\"></script>"
                    + "<div class=\"container\" style=\"width: 70%; background-color: #f4f4f4; margin: 0% 10% 0% 10%; \">\r\n"
                    + "<div class=\"header\" style=\"background-color: #333; padding: 5px; text-align: center;\">\r\n"
                    + " <img src=\"https://i.postimg.cc/vTxcSyPm/logo.png\" alt=\"TuTurismoNeiva Logo\" style=\"max-width: 100px; margin-right: 90%;\">\r\n"
                    + "</div>\r\n"
                    + "<div class=\"content\" style=\" text-align: center; color: #333;\">\r\n"
                    + "<h1 style=\"font-size: 24px; font-weight: bold; margin-bottom: 20px;\">¡Hola " + nombre_usuario
                    + "!</h1>\r\n"
                    + "<p style=\"font-size: 16px; margin-bottom: 20px;\"> Nos complace darte la bienvenida a TuTurismoNeiva, la plataforma ideal para descubrir los encantos de la ciudad de Neiva.En TuTurismoNeiva encontrarás todo lo que necesitas para conocer la cultura Neivana, desde información sobre lugares de interés hasta su ubicación. ¡Estamos seguros de que TuTurismoNeiva te ayudará a vivir una experiencia inolvidable en la ciudad! ¡No dudes en contactarnos si tienes alguna pregunta! Gracias por unirte a nosotros. No olvides seguirnos en nuestras redes sociales.</p>\r\n"
                    + "<br>TuTurismo Neiva\r\n"
                    + "</div>\r\n"
                    + "<div class=\"footer\" style=\"background-color: #333; color: #fff; padding: 10px; text-align: center; margin-top: 20px;\">\r\n"
                    + "<a href=\"#\" style=\"color: #fff; text-decoration: none; margin: 0 10px;\">Términos y condiciones</a> | <a href=\"#\" style=\"color: #fff; text-decoration: none; margin: 0 10px;\">Política de privacidad</a>\r\n"
                    + "<div>\r\n"
                    + "<box-icon name='phone' type='solid' color='#ffffff'></box-icon>\r\n"
                    + "<box-icon name='gmail' type='logo' color='#f7f5f5'></box-icon>\r\n"
                    + "<box-icon name='instagram-alt' type='logo' color='#ffffff'></box-icon>\r\n"
                    + "<box-icon name='tiktok' type='logo' color='#f9f7f7'></box-icon>\r\n"
                    + "</div>\r\n"
                    + "</div>\r\n"
                    + "</div>\r\n"
                    + " <script src=\"https://unpkg.com/boxicons@2.1.4/dist/boxicons.js\"></script>\r\n"
                    + "    <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js\"\r\n"
                    + "        integrity=\"sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz\"\r\n"
                    + "        crossorigin=\"anonymous\"></script>\r\n"
                    + "\r\n"
                    + "</body>";

            var retorno = enviarCorreo(destinatario, asunto, cuerpo);
            if (retorno) {
                return "se envió correctamente";
            } else {
                return "No se pudo envíar";
            }

        } catch (Exception e) {
            // TODO: handle exception
            return "Error al envíar " + e.getMessage();
        }
    }

    public boolean enviarCorreo(String destinatario, String asunto, String cuerpo) throws MessagingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(cuerpo, true);

            javaMailSender.send(message);
            return true;
        } catch (Exception e) {

            return false;
        }

    }
}
