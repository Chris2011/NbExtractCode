package org.chrisle.netbeans.plugins.nbextractcode;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorActionNames;
import org.netbeans.api.editor.EditorActionRegistration;
import org.netbeans.api.editor.EditorActionRegistrations;
import org.openide.filesystems.FileUtil;
import org.netbeans.spi.editor.AbstractEditorAction;
import org.openide.awt.ActionID;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.windows.TopComponent;

@ActionID(
    category = "Edit",
    id = "org.chrisle.netbeans.plugins.nbextractcode.NbExtractCodeAction"
)

@EditorActionRegistration(
    popupText = "Extract code to new file",
    popupPosition = 1295,
    name = "Extract code to new file"
)
public final class NbExtractCodeAction extends AbstractEditorAction {
    private static String _selectedText;

    @Override
    protected void actionPerformed(ActionEvent ae, JTextComponent target) {
        resetCaretMagicPosition(target);

        _selectedText = target.getSelectedText();

        if (_selectedText != null && !_selectedText.isEmpty()) {
            TopComponent activeTC = TopComponent.getRegistry().getActivated();
            DataObject dataLookup = activeTC.getLookup().lookup(DataObject.class);
            String filePath = FileUtil.toFile(dataLookup.getPrimaryFile()).getParent() + "\\";
            
            try {
                //fallback to create arbitrary file in current folder
                JFileChooser test = new JFileChooser(filePath);
                String fileName = "NewFile." + FileUtil.getExtension(FileUtil.toFile(dataLookup.getPrimaryFile()).getName());

                test.showSaveDialog(null);

                if (null != fileName) {
                    final File file = new File(filePath + fileName);

                    if (file.exists()) {
                        JOptionPane.showMessageDialog(null, String.format("Cannot create File %s already exists.", file.getAbsolutePath()), "Paste to new file", JOptionPane.WARNING_MESSAGE);
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

//    public static String getSelectedText() {
//        return _selectedText;
//    }
}