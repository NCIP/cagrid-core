/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package gov.nih.nci.cagrid.introduce.portal.common;

import java.io.File;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.cagrid.grape.ApplicationComponent;


public class LogViewer extends ApplicationComponent {

    private LogPanel logPanel = null;


    /**
     * This method initializes
     */
    public LogViewer() {
        super();
        initialize();
        this.addInternalFrameListener(new InternalFrameAdapter() {

            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                LogViewer.this.logPanel.cancel();
            }

        });
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setContentPane(getLogPanel());

    }


    /**
     * This method initializes logPanel
     * 
     * @return javax.swing.JPanel
     */
    private LogPanel getLogPanel() {
        if (this.logPanel == null) {
            this.logPanel = new LogPanel("log" + File.separator + "introduce.log");
        }
        return this.logPanel;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
