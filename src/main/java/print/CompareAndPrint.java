package print;

import com.inet.config.ConfigurationManager;
import com.inet.pdfc.PDFComparer;
import com.inet.pdfc.error.PdfcException;
import com.inet.pdfc.presenter.DifferencesPrintPresenter;
import util.SampleUtil;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.*;
import java.io.File;

/**
 * Created by richardr on 03.06.2016.
 */
public class CompareAndPrint {

    public static void main( String[] args ) {
        File[] files = getFileOfArguments( args );

        //Used the current i-net PDFC configuration. If no configuration has been previously set then the default configuration will be used.

        //set up Printer service
        PrintService printService = PrintServiceLookup
                        .lookupDefaultPrintService(); //use the default printservice, for testing purpose it makes sense to use a virtual printer!
        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
        //select paper A4
        attributes.add( MediaSizeName.ISO_A4 );
        //select orientation landscape
        attributes.add( OrientationRequested.PORTRAIT );

        PDFComparer pdfComparer = new PDFComparer().addPresenter( new DifferencesPrintPresenter( printService, attributes ) );
        try {
            pdfComparer.compare( files[1], files[0] );
            SampleUtil.showPresenterError( pdfComparer );
        } catch( PdfcException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Get 2 Files back, that was checked
     *
     * @param args the arguments
     * @return 2 Files
     */
    public static File[] getFileOfArguments( final String[] args ) {
        ConfigurationManager.getInstance().setCurrent( ConfigurationManager.getInstance().get( 1, "Default" ) );
        if( args == null || args.length != 2 ) {
            throw new IllegalArgumentException( "Usage: CompareTwoFilesAndPrint <PDF-File1> <PDF-File2>" );
        }
        return new File[] { SampleUtil.checkAndGetFile( args[0] ), SampleUtil.checkAndGetFile( args[1] ) };
    }
}