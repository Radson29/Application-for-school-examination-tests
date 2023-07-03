package pl.edu.ur.pdflib;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import pl.edu.ur.pdflib.dto.GroupedPeopleDTO;
import pl.edu.ur.pdflib.dto.PersonDTO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class QuizPDFLibrary {
    public static byte[] GeneratePDFForStudents(List<GroupedPeopleDTO> groupedPeopleDTOList) throws DocumentException, IOException {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, outputStream);
        document.open();

       BaseFont baseBold = BaseFont.createFont("/FiraCode-Bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
       Font fontBold = new Font(baseBold, 16);

       BaseFont baseRegular = BaseFont.createFont("/FiraCode-Light.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
       Font fontRegular = new Font(baseRegular, 12);

        for (GroupedPeopleDTO group : groupedPeopleDTOList) {
            Paragraph heading = new Paragraph(group.getGroupName(), fontBold);
            heading.setSpacingAfter(10);
            document.add(heading);
            if (!group.getPeople().isEmpty()) {
                for (PersonDTO person : group.getPeople()) {
                    Paragraph p = new Paragraph("\t - Imię i nazwisko: " + person.getFirstName() + " " + person.getLastName() + " - Login: " + person.getUsername(), fontRegular);
                    p.setSpacingAfter(5);
                    document.add(p);
                }
            }
            document.add(new Paragraph());
        }
        document.close();

        return outputStream.toByteArray();
    }
}
