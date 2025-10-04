package com.shivam.cloudlet_api.utilities;

public class EmailTemplateUtil {
    public static String buildResetPasswordEmail(String userName, String resetLink) {
        String year = String.valueOf(java.time.Year.now().getValue());

        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8" />
                  <title>Password Reset</title>
                </head>
                <body style="font-family: Arial, sans-serif; background-color: #f8f9fa; padding: 20px;">
                  <table role="presentation" width="100%%" cellspacing="0" cellpadding="0"
                         style="max-width: 600px; margin: auto; background: #ffffff; border-radius: 8px;
                                box-shadow: 0 2px 6px rgba(0,0,0,0.1);">
                    <tr>
                      <td style="padding: 20px; text-align: center; background-color: #2563eb;
                                 color: #ffffff; border-radius: 8px 8px 0 0;">
                        <h2>Password Reset Request</h2>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding: 20px; color: #333333;">
                        <p>Hello <strong>%s</strong>,</p>
                        <p>We received a request to reset your password. Please click the button below to set a new password:</p>
                        <p style="text-align: center; margin: 30px 0;">
                          <a href="%s" style="background-color: #2563eb; color: #ffffff; padding: 12px 24px;
                                             text-decoration: none; border-radius: 6px; font-weight: bold;">
                            Reset Password
                          </a>
                        </p>
                        <p>This link will expire in <strong>15 minutes</strong>.
                           If you did not request a password reset, you can safely ignore this email.</p>
                        <p style="margin-top: 30px;">Thank you,<br/>The Store API Team</p>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding: 15px; text-align: center; font-size: 12px; color: #888888;
                                 background-color: #f1f1f1; border-radius: 0 0 8px 8px;">
                        &copy; %s Store API. All rights reserved.
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """
                .formatted(userName, resetLink, year);
    }
}
