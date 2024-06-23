import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }


        JFileChooser fc = new JFileChooser(System.getenv("APPDATA") + "/SpaceEngineers/Saves");
        FileNameExtensionFilter sbcFileFilter = new FileNameExtensionFilter("SBC Files", "sbc");
        boolean goodFile = false;

        JOptionPane.showMessageDialog(null, "Select a Sandbox_config.sbc to extract the mod list from.", "Notification", JOptionPane.INFORMATION_MESSAGE);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(sbcFileFilter);

        do {
            int option = fc.showOpenDialog(null);

            if (option == JFileChooser.APPROVE_OPTION) {
                File sandboxFile = fc.getSelectedFile();

                //Error codes
                if (!sandboxFile.exists()) {
                    JOptionPane.showMessageDialog(null, "File does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (!FilenameUtils.getExtension(sandboxFile.getName()).equals("sbc")) {
                    JOptionPane.showMessageDialog(null, "Incorrect file type selected.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (!sandboxFile.getName().equals("Sandbox_config.sbc")) {
                    JOptionPane.showMessageDialog(null, "You need to select a Sandbox_config.sbc file.", "Error", JOptionPane.ERROR_MESSAGE);
                } else
                    goodFile = true;
            } else
                return;
        } while (!goodFile);
        saveFile(fc);
    }

    private static void saveFile(JFileChooser fc) throws IOException {
        FileNameExtensionFilter textFileFilter = new FileNameExtensionFilter("Text Files (.txt, .doc)", "txt", "doc");
        ModList modList = new ModList(fc.getSelectedFile());
        int result = -1;

        do {
            JOptionPane.showMessageDialog(null, "Select a location to save your mod list", "Save file", JOptionPane.INFORMATION_MESSAGE);
            fc = new JFileChooser(System.getProperty("user.home") + "/Desktop");
            fc.setFileFilter(textFileFilter);
            fc.addChoosableFileFilter(fc.getAcceptAllFileFilter());
            int option = fc.showSaveDialog(null);

            if (option == JFileChooser.APPROVE_OPTION) {
                try {
                    result = writeModList(fc, modList.getModListUrls());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }while(result == 1); //This is equal to selecting "No" in the overwrite dialog.

        if(result == 2) { //This is equal to selecting "Cancel" in the overwrite dialog.
            JOptionPane.showMessageDialog(null, "Mod list extraction cancelled. Your files have not been modified.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
        }
        else if (result == JFileChooser.APPROVE_OPTION)
            JOptionPane.showMessageDialog(null, "Mod list saved!", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    private static int writeModList(JFileChooser fc, String completedSandboxFile) throws IOException {
        String savePath;

        if(!fc.getSelectedFile().toPath().toString().contains(".")) {
            savePath = fc.getSelectedFile().toPath() + ".txt";
        }
        else
            savePath = fc.getSelectedFile().toPath().toString();


        File file = new File(savePath);

        if (!file.exists()) {
            file.createNewFile();
        }
        else {
            int option = JOptionPane.showOptionDialog(null, "File already exists! Overwrite?", "File exists", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, null, "No");
            if(option != JFileChooser.APPROVE_OPTION) {
                return option;
            }
        }

        FileWriter fw = new FileWriter(file);

        BufferedWriter output = new BufferedWriter(fw);

        output.write(completedSandboxFile);

        output.flush();
        output.close();
        return 0;
    }
}