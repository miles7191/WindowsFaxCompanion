/*
 * Copyright (C) 2021 Matthew Rosato
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.t07m.wfc;

import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;

public abstract class Util {

	public static boolean TiffToPDF(String tiffPath, String pdfPath) {
		try{
			//Read the Tiff File
			RandomAccessFileOrArray tiffFile = new RandomAccessFileOrArray(tiffPath);
			//Find number of images in Tiff file
			int numberOfPages = TiffImage.getNumberOfPages(tiffFile);
			Document pdfDocument = new Document();
			PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfPath));
			pdfWriter.setStrictImageSequence(true);
			pdfDocument.open();
			//Run a for loop to extract images from Tiff file
			//into a Image object and add to PDF recursively
			for(int i = 1; i <= numberOfPages; i++){
				Image tempImage = TiffImage.getTiffImage(tiffFile, i);
				Rectangle pageSize = new Rectangle(tempImage.getWidth(), tempImage.getHeight());
				pdfDocument.setMargins(0, 0, 0, 0);
				pdfDocument.setPageSize(pageSize);
				pdfDocument.newPage();
				pdfDocument.add(tempImage);
			}
			pdfDocument.close();
			return true;
		}
		catch (Exception i1){
			i1.printStackTrace();
		}   
		return false;
	}
	
}
