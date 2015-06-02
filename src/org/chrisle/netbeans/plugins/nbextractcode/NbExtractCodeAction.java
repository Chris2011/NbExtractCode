package org.chrisle.netbeans.plugins.nbextractcode;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;
import org.openide.filesystems.FileUtil;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.spi.editor.AbstractEditorAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

@ActionID(
        category = "Edit",
        id = "org.chrisle.netbeans.plugins.nbextractcode.NbExtractCodeAction"
)
@ActionRegistration(
        displayName = "Extract code to new file"
)
@ActionReferences({
    @ActionReference(
            path = "Editors/Popup",
            position = 1295,
            name = "#CTL_NbExtractCodeAction"
    )
})
//@EditorActionRegistrations({
//    @EditorActionRegistration(
//            name = "Extract code to new file",
//            menuPath = "GoTo",
//            menuPosition = 900,
//            menuText = "Test",
//            popupText = "Tester"
//    )
//})

@Messages("CTL_NbExtractCodeAction=Extract code to new file")
public final class NbExtractCodeAction extends AbstractEditorAction {

    private static String _selectedText;

    @Override
    protected void actionPerformed(ActionEvent ae, JTextComponent target) {
        resetCaretMagicPosition(target);

        BaseDocument doc = Utilities.getDocument(target);
        _selectedText = target.getSelectedText();
        
        TopComponent activeTC = TopComponent.getRegistry().getActivated();
        DataObject dataLookup = activeTC.getLookup().lookup(DataObject.class);
        String filePath = FileUtil.toFile(dataLookup.getPrimaryFile()).getPath();
        
        JDialog tt = new JDialog();
        JLabel ll = new JLabel();
        ll.setText(filePath);
        tt.setSize(100, 100);
        tt.add(ll);
        tt.setVisible(true);

        if (!_selectedText.isEmpty()) {
            // TODO: Call NewFileWizard to create a new file and add the selected text to the new file.
            try {
                //fallback to create arbitrary file in current folder
                String fileName = JOptionPane.showInputDialog("New file (" + filePath + "):", "NewFile." + FileUtil.getExtension(FileUtil.toFile(dataLookup.getPrimaryFile()).getName()));
                if (null != fileName) {
                    final File file = new File(filePath + fileName);

                    if (file.exists()) {
                        JOptionPane.showMessageDialog(null, String.format("Cannot create a file from clipboard content.\nFile %s already exists.", file.getAbsolutePath()), "Paste to new file", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    FileWriter fileWriter = new FileWriter(file);

                    fileWriter.write(_selectedText);
                    fileWriter.close();

                    FileObject fileObj = FileUtil.createData(file);
//                    writeToFile(file, clipboardContent);
                    //open newly created file in editor
                    DataObject.find(fileObj).getLookup().lookup(OpenCookie.class).open();
                }
            } catch (HeadlessException | IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static String getSelectedText() {
        return _selectedText;
    }
}
