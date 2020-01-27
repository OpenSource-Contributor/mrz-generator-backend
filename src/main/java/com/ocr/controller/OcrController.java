package com.ocr.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.ocr.model.Passport;
import com.ocr.model.Request;
import org.springframework.web.bind.annotation.*;

import com.ocr.model.OcrModel;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import sun.misc.BASE64Decoder;

@RestController
@CrossOrigin(origins = {"*"})
public class OcrController
{

	@PostMapping("/api/ocr")
	public Passport DoOCR( @RequestBody Request requestBody ) throws IOException
	{
		String destinationLanguage = requestBody.getLanguage();
		String imageBase64 = requestBody.getImage();
		BufferedImage bufferedImage = decodeToImage(imageBase64);

		OcrModel request = new OcrModel();
		request.setDestinationLanguage( destinationLanguage );

		ITesseract instance = new Tesseract();

		try
		{

			BufferedImage in = ImageIO.read( convert( bufferedImage ) );

			BufferedImage newImage = new BufferedImage( in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB );

			Graphics2D g = newImage.createGraphics();
			g.drawImage( in, 0, 0, null );
			g.dispose();
//
			instance.setLanguage( request.getDestinationLanguage() );
			instance.setDatapath( "D:\\Research\\Java\\springboot-tesseract-ocr\\tessdata" );

			String result = instance.doOCR( newImage );
			Passport passport = this.getPassportDetails( result );

			if (passport != null) {
				return passport;
			} else {
				return null;
			}

		}
		catch ( TesseractException e )
		{
			System.err.println( e.getMessage() );
			return null;
		}

	}

//	public static File convert( MultipartFile file ) throws IOException
//	{
//		File convFile = new File( file.getOriginalFilename() );
//		convFile.createNewFile();
//		FileOutputStream fos = new FileOutputStream( convFile );
//		fos.write( file.getBytes() );
//		fos.close();
//		return convFile;
//	}
	public static File convert( BufferedImage file ) throws IOException
	{
		File convFile = new File( "1.jpeg");
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream( convFile );
		ImageIO.write( file, "jpeg", fos );
		fos.close();
		return convFile;
	}

	public Passport getPassportDetails( String ocr )
	{
		Passport passport = new Passport();
		String newOcr = "";
		for(int i=0; i< ocr.length(); i++) {
			if (ocr.charAt( i ) != ' ') {
				newOcr += ocr.charAt( i );
			}
		}
		String[] lines = newOcr.split( "\n" );
		String line1 = "";
		String line2 = "";
		if (lines.length > 1) {
			line1 = lines[0];
			line2 = lines[1];
		}
		passport.setCountry( line1.substring( 2, 5 ) );
		String givenName = "";
		String lastName = "";
		int j = 0;
		for (j=5; j < line1.length(); j++) {
			if (line1.charAt( j ) == '<') {
				break;
			} else {
				lastName += line1.charAt( j );
			}
		}
		String givenNameNotStrip = line1.substring( j, line1.length() );
		for (int k = 0; k< givenNameNotStrip.length(); k++) {
			if (givenNameNotStrip.charAt( k ) != '<') {
				givenName += givenNameNotStrip.charAt( k );
			} else {
				givenName += " ";
			}
		}
		passport.setGivenName( givenName.trim() );
		passport.setSurname( lastName.trim() );
		String passportNo = line2.substring( 0, 9 );
		String newPassportNo = "";
		for (int m=0 ; m< passportNo.length() ; m++) {
			if (passportNo.charAt( m ) != '<') {
				newPassportNo += passportNo.charAt( m );
			}
		}
		passport.setPassportNo( newPassportNo );
		passport.setNationality( line2.substring( 10, 13 ) );
		try {
			int year = Integer.parseInt( "19" + line2.substring( 13, 15 ));
			int month = Integer.parseInt( line2.substring( 15, 17 ) );
			int day = Integer.parseInt( line2.substring( 17, 19 ));
			passport.setDob( LocalDate.of(year, month, day) );
		} catch ( Exception e ) {
			Logger.getLogger( "DOB Generate Failed" );
			passport.setDob( null );
		}
		if (line2.charAt( 20 ) == 'F') {
			passport.setGender( "FEMALE" );
		} else {
			passport.setGender( "MALE" );
		}

		try {
			int yearExpiry = Integer.parseInt( "20" + line2.substring( 21, 23 ));
			int monthExpiry = Integer.parseInt( line2.substring( 23, 25 ) );
			int dayExpiry = Integer.parseInt( line2.substring( 25, 27 ));
			passport.setExpiryDate(LocalDate.of( yearExpiry, monthExpiry, dayExpiry ));
		} catch ( Exception e ) {
			Logger.getLogger( "Expiry Date Generate Failed" );
			passport.setExpiryDate( null );
		}
		return passport;
	}

	public BufferedImage decodeToImage(String imageString) {

		BufferedImage image = null;
		byte[] imageByte;
		try {
			BASE64Decoder decoder = new BASE64Decoder();
			imageByte = decoder.decodeBuffer(imageString);
			ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
			image = ImageIO.read(bis);
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}
}
