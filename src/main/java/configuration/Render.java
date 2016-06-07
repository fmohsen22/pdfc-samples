package configuration;

import com.inet.pdfc.PDFC;
import com.inet.pdfc.PDFComparer;
import com.inet.pdfc.generator.model.DiffGroup;
import com.inet.pdfc.generator.model.Modification;
import com.inet.pdfc.model.PagedElement;
import com.inet.pdfc.results.ResultModel;
import com.inet.pdfc.results.ResultPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * A sample to show the render function with a simplify marker function.
 *
 * Expected 2 arguments, the path of the pdf files
 */
public class Render {

    private JFrame frame;

    public static void main( String[] args ) {
        try {
            PDFC.requestAndSetTrialLicenseIfRequired();
        } catch( IOException e ) {
            e.printStackTrace();
        }

        File[] files = getFileOfArguments( args );
        new Render( files ).show();
    }

    public Render(final File[] files){
        PDFComparer pdfComparer = new PDFComparer();
        ResultModel compare = pdfComparer.compare( files[0], files[1] );
        ResultPage page = compare.getPage( 0, true );

        frame = new JFrame(  );
        frame.setTitle("PDF Difference");
        frame.setSize(page.getWidth(), page.getHeight());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            frame.add( new PDFViewer( compare ) );
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public void show(){
        frame.setVisible( true );
    }

    /**
     * To show one PDF file.
     *
     * Every click go to the next page,
     * if no next page exist it is start at page 1
     *
     * Every changes is with a blue transparent color marked
     * over the line.
     */
    public class PDFViewer extends JComponent{

        private int currentPageIndex = 0;
        private ResultModel compare;
        private final int maxPageNumber;

        public PDFViewer(final ResultModel compare) throws IOException {
            this.compare = compare;
            maxPageNumber = compare.getComparisonParameters().getFirstFile().getContent().getNumPages();
            addMouseListener( new MouseListener() {
                /**
                 * For every click, it switch to the next page,
                 * if no next page exist it start at the start page
                 * @param e
                 */
                @Override
                public void mouseClicked( MouseEvent e ) {
                    ++currentPageIndex;
                    if(maxPageNumber <= currentPageIndex){
                        currentPageIndex = 0;
                    }
                    repaint();
                }

                @Override
                public void mousePressed( MouseEvent e ) {}

                @Override
                public void mouseReleased( MouseEvent e ) {}

                @Override
                public void mouseEntered( MouseEvent e ) {}

                @Override
                public void mouseExited( MouseEvent e ) {}
            } );
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2d = (Graphics2D) g;

            ResultPage page = compare.getPage( currentPageIndex, true );
            //draw the pdf file, alternative: page.renderPage(1.0, g2d  );
            BufferedImage pageImage = page.getPageImage( 1.0 );
            g2d.drawImage( pageImage, 0,0, null );


            //for highlight the differences lines
            g2d.setColor(new Color( 0,40,255,40  ) ); //transparent blue
            List<DiffGroup> differences = compare.getDifferences( false );
            for( DiffGroup difference : differences ) {
                if(hasChangesForThisPage(difference.getModifications())) {
                    Rectangle bounds = difference.getBounds( true );
                    g2d.fillRect( bounds.x, bounds.y - currentPageIndex*page.getHeight(), page.getWidth(), bounds.height );
                }
            }
        }

        /**
         * Check the modification for the relevant for the current page.
         *
         * @param modifications a list with modification
         * @return true the modification is for the current page relevant, false if it no modification for the current page
         */
        private boolean hasChangesForThisPage(final List<Modification> modifications){
            if(modifications == null) {
                return false;
            }

            for( Modification modification : modifications ) {
                List<PagedElement> affectedElements = modification.getAffectedElements( true );
                for( PagedElement affectedElement : affectedElements ) {
                    int pageIndex = affectedElement.getPageIndex();
                    if(pageIndex == currentPageIndex ) {
                        return true;
                    }
                }
                affectedElements = modification.getAffectedElements( false );
                for( PagedElement affectedElement : affectedElements ) {
                    int pageIndex = affectedElement.getPageIndex();
                    if(pageIndex == currentPageIndex ) {
                        return true;
                    }
                }
            }
            return false;
        }
    }



    /**
     * Get 2 Files back, that was checked
     *
     * @param args the arguments
     * @return 2 Files
     */
    public static File[] getFileOfArguments(final String[] args){
        if (args == null || args.length != 2) {
            throw new IllegalArgumentException( "Usage: CompareTwoFilesAndPrint <PDF-File1> <PDF-File2>" );
        }
        return new File[]{ checkAndGetFile( args[0] ), checkAndGetFile( args[1] )};
    }

    /**
     * For get a File-Object out a String-Path
     *
     * Check for null, exists and directory
     *
     * @param file Path for the File
     * @return The Fileobject
     */
    public static File checkAndGetFile( final String file){
        if(file == null){
            throw new IllegalArgumentException( "The parameter is empty.\n parameter = " + file );
        }
        final File fileObject = new File( file );

        if( ! fileObject.exists() ){
            throw new IllegalArgumentException( "The file didn't exist.\n parameter = " + file );
        }
        if( fileObject.isDirectory()){
            throw new IllegalArgumentException( "The file is a folder and not a pdf file.\n parameter = " + file );
        }

        return  fileObject;
    }


}
