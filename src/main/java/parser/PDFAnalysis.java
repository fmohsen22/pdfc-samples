package parser;

import com.inet.pdfc.PDFC;
import com.inet.pdfc.PDFComparer;
import com.inet.pdfc.model.Document;
import com.inet.pdfc.model.DrawableElement;
import com.inet.pdfc.model.Page;
import com.inet.pdfc.results.ResultModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * A sample to show the internal PDF data structure
 * Expected 2 arguments, the path of the PDF files
 */
public class PDFAnalysis {

    /**
     * Start the sample, to show the internal PDF data structure
     *
     * @param args Expected 2 arguments, the path of the PDF files
     */
    public static void main( String[] args ) {
        try {
            PDFC.requestAndSetTrialLicenseIfRequired();
        } catch( IOException e ) {
            e.printStackTrace();
        }

        File[] files = getFileOfArguments( args );
        PDFComparer pdfComparer = new PDFComparer();
        ResultModel compare = pdfComparer.compare( files[0], files[1] );

        try {
            Document document = compare.getComparisonParameters().getFirstFile().getContent();
            System.out.println( "number of pages = " + document.getNumPages() );

            for( int i = 0; i < document.getNumPages(); ++i ) {
                System.out.println( "\npage number = " + i );
                Page page = document.getPage( i );

                List<DrawableElement> list = page.getElementList().getList();
                for( DrawableElement drawableElement : list ) {
                    System.out.println( "Type = " + drawableElement.getType() + "\t\t" + drawableElement );
                }

            }
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Get 2 files that are to be checked for comparisons
     *
     * @param args the arguments
     * @return 2 files to compare
     */
    public static File[] getFileOfArguments( final String[] args ) {
        if( args == null || args.length != 2 ) {
            throw new IllegalArgumentException( "Usage: CompareTwoFilesAndPrint <PDF-File1> <PDF-File2>" );
        }
        return new File[] { checkAndGetFile( args[0] ), checkAndGetFile( args[1] ) };
    }

    /**
     * Returns a File object based on a string path
     * The file must not be null, must exist and must not be a directory
     *
     * @param file path to the file
     * @return The File object
     */
    public static File checkAndGetFile( final String file ) {
        if( file == null ) {
            throw new IllegalArgumentException( "The parameter is empty.\n parameter = " + file );
        }
        final File fileObject = new File( file );

        if( !fileObject.exists() ) {
            throw new IllegalArgumentException( "The file didn't exist.\n parameter = " + file );
        }
        if( fileObject.isDirectory() ) {
            throw new IllegalArgumentException( "The file is a folder and not a PDF file.\n parameter = " + file );
        }

        return fileObject;
    }
}
