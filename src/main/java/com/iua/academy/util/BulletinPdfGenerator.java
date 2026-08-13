package com.iua.academy.util;

import com.iua.academy.model.Etudiant;
import com.iua.academy.model.NoteDetaillee;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Genere le bulletin de notes d'un etudiant au format PDF,
 * a partir de ses notes detaillees et des parametres de l'etablissement.
 */
public class BulletinPdfGenerator {

    private static final Color COULEUR_NAVY = new Color(15, 42, 77);
    private static final Color COULEUR_SKY = new Color(46, 159, 224);
    private static final Color COULEUR_GRIS_CLAIR = new Color(245, 248, 252);

    private static final Font POLICE_TITRE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, COULEUR_NAVY);
    private static final Font POLICE_SOUS_TITRE = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.DARK_GRAY);
    private static final Font POLICE_LABEL = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COULEUR_NAVY);
    private static final Font POLICE_VALEUR = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
    private static final Font POLICE_ENTETE_TABLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
    private static final Font POLICE_CELLULE = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
    private static final Font POLICE_MOYENNE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, COULEUR_NAVY);

    /**
     * Genere le PDF du bulletin et l'ecrit dans le fichier de destination.
     */
    public void genererBulletin(File destination,
                                 Etudiant etudiant,
                                 List<NoteDetaillee> notes,
                                 Double moyenneGenerale,
                                 String nomEtablissement,
                                 String anneeScolaire,
                                 double seuilValidation) throws IOException {

        Document document = new Document(PageSize.A4, 40, 40, 30, 40);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(destination));
            document.open();

            ajouterEnTete(document, nomEtablissement, anneeScolaire);
            ajouterInfosEtudiant(document, etudiant);
            ajouterTableauNotes(document, notes);
            ajouterSyntheseFinale(document, moyenneGenerale, seuilValidation);
            ajouterPiedDePage(document);

        } catch (DocumentException e) {
            throw new IOException("Erreur lors de la generation du PDF", e);
        } finally {
            document.close();
        }
    }

    private void ajouterEnTete(Document document, String nomEtablissement, String anneeScolaire) throws DocumentException {
        Paragraph etablissement = new Paragraph(nomEtablissement, POLICE_TITRE);
        etablissement.setAlignment(Element.ALIGN_CENTER);
        document.add(etablissement);

        Paragraph titre = new Paragraph("BULLETIN DE NOTES", POLICE_SOUS_TITRE);
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingAfter(2);
        document.add(titre);

        Paragraph annee = new Paragraph("Annee scolaire " + anneeScolaire, POLICE_SOUS_TITRE);
        annee.setAlignment(Element.ALIGN_CENTER);
        annee.setSpacingAfter(20);
        document.add(annee);
    }

    private void ajouterInfosEtudiant(Document document, Etudiant etudiant) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20);

        ajouterLigneInfo(table, "Etudiant", etudiant.getNomComplet());
        ajouterLigneInfo(table, "Matricule", etudiant.getMatricule());
        ajouterLigneInfo(table, "Classe", etudiant.getClasse());
        if (etudiant.getEmail() != null && !etudiant.getEmail().isBlank()) {
            ajouterLigneInfo(table, "Email", etudiant.getEmail());
        }

        document.add(table);
    }

    private void ajouterLigneInfo(PdfPTable table, String label, String valeur) {
        PdfPCell celluleLabel = new PdfPCell(new Phrase(label, POLICE_LABEL));
        celluleLabel.setBorder(0);
        celluleLabel.setPaddingBottom(4);
        table.addCell(celluleLabel);

        PdfPCell celluleValeur = new PdfPCell(new Phrase(valeur, POLICE_VALEUR));
        celluleValeur.setBorder(0);
        celluleValeur.setPaddingBottom(4);
        table.addCell(celluleValeur);
    }

    private void ajouterTableauNotes(Document document, List<NoteDetaillee> notes) throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2.5f, 1f, 1.5f, 1.5f, 1.5f});
        table.setSpacingAfter(20);

        ajouterEnteteColonne(table, "Matiere");
        ajouterEnteteColonne(table, "Coef.");
        ajouterEnteteColonne(table, "Note /20");
        ajouterEnteteColonne(table, "Type");
        ajouterEnteteColonne(table, "Date");

        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        boolean ligneClaire = true;
        for (NoteDetaillee note : notes) {
            Color fond = ligneClaire ? Color.WHITE : COULEUR_GRIS_CLAIR;

            ajouterCellule(table, note.getMatiereNom(), fond, Element.ALIGN_LEFT);
            ajouterCellule(table, String.format("%.1f", note.getCoefficient()), fond, Element.ALIGN_CENTER);
            ajouterCellule(table, String.format("%.2f", note.getValeur()), fond, Element.ALIGN_CENTER);
            ajouterCellule(table, note.getTypeEvaluation(), fond, Element.ALIGN_CENTER);
            ajouterCellule(table, note.getDateEvaluation().format(formatDate), fond, Element.ALIGN_CENTER);

            ligneClaire = !ligneClaire;
        }

        if (notes.isEmpty()) {
            PdfPCell celluleVide = new PdfPCell(new Phrase("Aucune note enregistree", POLICE_CELLULE));
            celluleVide.setColspan(5);
            celluleVide.setPadding(10);
            celluleVide.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(celluleVide);
        }

        document.add(table);
    }

    private void ajouterEnteteColonne(PdfPTable table, String texte) {
        PdfPCell cellule = new PdfPCell(new Phrase(texte, POLICE_ENTETE_TABLE));
        cellule.setBackgroundColor(COULEUR_NAVY);
        cellule.setPadding(6);
        cellule.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cellule);
    }

    private void ajouterCellule(PdfPTable table, String texte, Color fond, int alignement) {
        PdfPCell cellule = new PdfPCell(new Phrase(texte, POLICE_CELLULE));
        cellule.setBackgroundColor(fond);
        cellule.setPadding(6);
        cellule.setHorizontalAlignment(alignement);
        table.addCell(cellule);
    }

    private void ajouterSyntheseFinale(Document document, Double moyenneGenerale, double seuilValidation) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20);

        String texteMoyenne = moyenneGenerale == null
            ? "Moyenne generale : -"
            : String.format("Moyenne generale : %.2f / 20", moyenneGenerale);

        PdfPCell cellule = new PdfPCell(new Phrase(texteMoyenne, POLICE_MOYENNE));
        cellule.setBackgroundColor(COULEUR_GRIS_CLAIR);
        cellule.setPadding(12);
        cellule.setBorderColor(COULEUR_SKY);
        cellule.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cellule);

        document.add(table);

        if (moyenneGenerale != null) {
            String mention = determinerMention(moyenneGenerale);
            Color couleurMention = moyenneGenerale >= seuilValidation
                ? new Color(30, 132, 73)
                : new Color(214, 69, 69);
            Font policeMention = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, couleurMention);

            Paragraph mentionParagraphe = new Paragraph("Mention : " + mention, policeMention);
            mentionParagraphe.setAlignment(Element.ALIGN_CENTER);
            mentionParagraphe.setSpacingAfter(20);
            document.add(mentionParagraphe);
        }
    }

    private String determinerMention(double moyenne) {
        if (moyenne < 10) return "Insuffisant";
        if (moyenne < 12) return "Passable";
        if (moyenne < 14) return "Assez bien";
        if (moyenne < 16) return "Bien";
        return "Excellent";
    }

    private void ajouterPiedDePage(Document document) throws DocumentException {
        Font policeFooter = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, Color.GRAY);
        String dateEdition = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        Paragraph footer = new Paragraph("Document genere automatiquement le " + dateEdition + " par Academy", policeFooter);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }
}